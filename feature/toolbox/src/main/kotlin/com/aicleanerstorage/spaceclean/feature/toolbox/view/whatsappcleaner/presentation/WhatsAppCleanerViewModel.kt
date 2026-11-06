package com.aicleanerstorage.spaceclean.feature.toolbox.view.whatsappcleaner.presentation

import com.aicleanerstorage.spaceclean.core.presentation.MviViewModel
import androidx.lifecycle.viewModelScope
import com.aicleanerstorage.spaceclean.core.ui.R
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureOperationEvent
import com.aicleanerstorage.spaceclean.core.contracts.feature.OperationAction
import com.aicleanerstorage.spaceclean.core.contracts.feature.FeatureKey
import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileItem
import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileType
import com.aicleanerstorage.spaceclean.core.contracts.files.FileRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WhatsAppCleanerViewModel(
    private val message: (Int) -> String,
    private val repository: FileRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MviViewModel<WhatsAppCleanerUiState, WhatsAppCleanerIntent, WhatsAppCleanerEffect>(WhatsAppCleanerUiState()) {
    private val stateUpdateLock = Any()
    override fun onIntent(intent: WhatsAppCleanerIntent) {
        when (intent) {
            is WhatsAppCleanerIntent.Navigate -> emitEffect(WhatsAppCleanerEffect.Navigate(intent.destination))
            WhatsAppCleanerIntent.StartScanIfNeeded -> startScanIfNeeded()
            WhatsAppCleanerIntent.CleanSelectedFiles -> cleanSelectedFiles()
            else -> {
        if (intent is WhatsAppCleanerStateChanged) {
            updateState { WhatsAppCleanerReducer.reduce(this, intent) }
        } else {
            handleIntent(intent)
        }
    
            }
        }
    }

    private fun publishState(value: WhatsAppCleanerUiState) {
        synchronized(stateUpdateLock) { dispatch(WhatsAppCleanerStateChanged(value)) }
    }

    private fun changeState(transform: (WhatsAppCleanerUiState) -> WhatsAppCleanerUiState) {
        synchronized(stateUpdateLock) { dispatch(WhatsAppCleanerStateChanged(transform(currentState))) }
    }

    
    private var hasStarted = false
    private var scanJob: Job? = null
    private var cleanJob: Job? = null


    private fun handleIntent(action: WhatsAppCleanerIntent) {
        when (action) {
            WhatsAppCleanerIntent.Back -> { cancelActiveOperation(); emitEffect(WhatsAppCleanerEffect.Exit(false)) }
            WhatsAppCleanerIntent.ExitAfterComplete -> emitEffect(WhatsAppCleanerEffect.Exit(true))
            WhatsAppCleanerIntent.StartScan -> startScanIfNeeded()
            WhatsAppCleanerIntent.CleanSelected -> cleanSelectedFiles()
            WhatsAppCleanerIntent.Retry -> retry()
            WhatsAppCleanerIntent.CancelActiveOperation -> cancelActiveOperation()
            WhatsAppCleanerIntent.CancelCleaningAndReturnToResult -> cancelCleaningAndReturnToResult()
            WhatsAppCleanerIntent.ClearResult -> clearResult()
            WhatsAppCleanerIntent.ShowResult -> showResult()
            is WhatsAppCleanerIntent.ToggleGroup -> toggleGroup(action.group)
            is WhatsAppCleanerIntent.ToggleCategory -> toggleCategory(action.group, action.category)
            is WhatsAppCleanerIntent.ToggleExpanded -> toggleExpanded(action.group)
            is WhatsAppCleanerStateChanged -> Unit
            else -> Unit
        }
    }

    private fun startScanIfNeeded() {
        if (hasStarted) return
        hasStarted = true
        scanJob?.cancel()
        publishState(WhatsAppCleanerUiState(phase = WhatsAppCleanerPhase.Scanning))
        trackOperationEvent(FeatureOperationEvent.ScanStarted(FeatureKey.WHATSAPP_CLEANER))
        scanJob =
            viewModelScope.launch(ioDispatcher) {
                try {
                    val files = repository.loadWhatsAppFiles()
                    delay(SCAN_DELAY_MILLIS)
                    val nextState = buildScanResultState(files)
                    publishState(nextState)
                    trackOperationEvent(
                        FeatureOperationEvent.ScanFinished(
                            FeatureKey.WHATSAPP_CLEANER,
                            nextState.scannedBytes > 0L,
                        ),
                    )
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    changeState {
                        it.copy(
                            phase = WhatsAppCleanerPhase.Error,
                            errorMessage = error.message ?: appString(R.string.whatsapp_clean_unavailable),
                        )
                    }
                }
            }
    }

    private fun toggleGroup(group: WhatsAppCleanerGroup) {
        changeState { state ->
            if (state.phase != WhatsAppCleanerPhase.ScanResult) return@changeState state
            val targetGroup = state.groups.firstOrNull { it.group == group } ?: return@changeState state
            val shouldSelect = !targetGroup.selected
            state.withGroups(
                state.groups.map { groupItem ->
                    if (groupItem.group != group) {
                        groupItem
                    } else {
                        groupItem.copy(
                            children = groupItem.children.map { child ->
                                if (child.hasFiles) child.copy(selected = shouldSelect) else child
                            },
                        )
                    }
                },
            )
        }
    }

    private fun toggleCategory(group: WhatsAppCleanerGroup, category: WhatsAppCleanerCategory) {
        changeState { state ->
            if (state.phase != WhatsAppCleanerPhase.ScanResult) return@changeState state
            state.withGroups(
                state.groups.map { groupItem ->
                    if (groupItem.group != group) {
                        groupItem
                    } else {
                        groupItem.copy(
                            children = groupItem.children.map { child ->
                                if (child.category == category && child.hasFiles) {
                                    child.copy(selected = !child.selected)
                                } else {
                                    child
                                }
                            },
                        )
                    }
                },
            )
        }
    }

    private fun toggleExpanded(group: WhatsAppCleanerGroup) {
        changeState { state ->
            if (state.phase != WhatsAppCleanerPhase.ScanResult) return@changeState state
            state.copy(
                groups = state.groups.map { groupItem ->
                    if (groupItem.group == group) {
                        groupItem.copy(expanded = !groupItem.expanded)
                    } else {
                        groupItem
                    }
                },
            )
        }
    }

    private fun cleanSelectedFiles() {
        val state = currentState
        if (state.phase != WhatsAppCleanerPhase.ScanResult || state.selectedBytes <= 0L) return

        val selectedFiles =
            state.groups
                .flatMap { it.children }
                .filter { it.selected }
                .flatMap { it.files }
                .distinctBy { it.path ?: it.uri.toString() }
        val expectedBytes = selectedFiles.sumOf { it.sizeBytes }
        val cleaningStartedAt = System.currentTimeMillis()

        trackOperationEvent(FeatureOperationEvent.OperationStarted(FeatureKey.WHATSAPP_CLEANER, OperationAction.CLEAN))
        changeState {
            it.copy(
                phase = WhatsAppCleanerPhase.Cleaning,
                selectedBytes = expectedBytes,
                selectedCount = selectedFiles.size,
                errorMessage = null,
            )
        }
        cleanJob?.cancel()
        cleanJob =
            viewModelScope.launch(ioDispatcher) {
                try {
                    val result = repository.deleteFiles(selectedFiles)
                    if (result.deletedCount <= 0) error(appString(R.string.whatsapp_clean_unavailable))
                    delayRemainingCleaningAnimation(cleaningStartedAt)
                    publishState(WhatsAppCleanerUiState(
                            phase = WhatsAppCleanerPhase.CompleteAnimation,
                            deletedBytes = result.freedBytes,
                            deletedCount = result.deletedCount,
                        ))
                    delay(COMPLETE_ANIMATION_MILLIS)
                    trackOperationEvent(
                        FeatureOperationEvent.OperationFinished(
                            FeatureKey.WHATSAPP_CLEANER,
                            OperationAction.CLEAN,
                            success = true,
                        ),
                    )
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    trackOperationEvent(
                        FeatureOperationEvent.OperationFinished(
                            FeatureKey.WHATSAPP_CLEANER,
                            OperationAction.CLEAN,
                            success = false,
                        ),
                    )
                    changeState {
                        it.copy(
                            phase = WhatsAppCleanerPhase.Error,
                            errorMessage = error.message ?: appString(R.string.whatsapp_clean_unavailable),
                        )
                    }
                }
            }
    }

    private suspend fun delayRemainingCleaningAnimation(startedAtMillis: Long) {
        val remainingMillis = CLEANING_ANIMATION_MIN_MILLIS - (System.currentTimeMillis() - startedAtMillis)
        if (remainingMillis > 0L) delay(remainingMillis)
    }

    private fun retry() {
        cancelActiveOperation()
        hasStarted = false
        startScanIfNeeded()
    }

    private fun cancelActiveOperation() {
        scanJob?.cancel()
        scanJob = null
        cleanJob?.cancel()
        cleanJob = null
        hasStarted = false
    }

    private fun clearResult() {
        cancelActiveOperation()
        publishState(WhatsAppCleanerUiState())
    }

    private fun showResult() {
        changeState { current ->
            if (current.phase == WhatsAppCleanerPhase.CompleteAnimation) {
                current.copy(phase = WhatsAppCleanerPhase.Result)
            } else {
                current
            }
        }
    }

    private fun cancelCleaningAndReturnToResult() {
        cleanJob?.cancel()
        cleanJob = null
        changeState { current ->
            if (current.phase == WhatsAppCleanerPhase.Cleaning || current.phase == WhatsAppCleanerPhase.CompleteAnimation) {
                current.copy(phase = WhatsAppCleanerPhase.ScanResult)
            } else {
                current
            }
        }
    }

    override fun onCleared() {
        scanJob?.cancel()
        cleanJob?.cancel()
        super.onCleared()
    }

    private fun buildScanResultState(files: List<ManagedFileItem>): WhatsAppCleanerUiState {
        val cleanableFiles = files.filter { it.sizeBytes > 0L }
        val groups =
            WhatsAppCleanerGroup.values().map { group ->
                val groupFiles = cleanableFiles.filter { item -> item.toCleanerGroup() == group }
                WhatsAppCleanerGroupItem(
                    group = group,
                    children =
                        WhatsAppCleanerCategory.values().map { category ->
                            val categoryFiles = groupFiles.filter { item -> item.toCleanerCategory() == category }
                            WhatsAppCleanerSubItem(
                                group = group,
                                category = category,
                                files = categoryFiles,
                                selected = categoryFiles.isNotEmpty() && category.shouldSelectByDefault(group),
                            )
                        },
                )
            }
        return WhatsAppCleanerUiState(
            phase = WhatsAppCleanerPhase.ScanResult,
            groups = groups,
            scannedBytes = groups.sumOf { it.totalBytes },
            selectedBytes = groups.sumOf { it.selectedBytes },
            selectedCount = groups.flatMap { it.children }.filter { it.selected }.sumOf { it.files.size },
        )
    }

    private fun trackOperationEvent(event: FeatureOperationEvent) {
        emitEffect(WhatsAppCleanerEffect.Operation(event))
    }

    private fun WhatsAppCleanerUiState.withGroups(newGroups: List<WhatsAppCleanerGroupItem>): WhatsAppCleanerUiState =
        copy(
            groups = newGroups,
            selectedBytes = newGroups.sumOf { it.selectedBytes },
            selectedCount = newGroups.flatMap { it.children }.filter { it.selected }.sumOf { it.files.size },
        )

    private fun ManagedFileItem.toCleanerGroup(): WhatsAppCleanerGroup {
        val source = listOfNotNull(path, bucketName, name).joinToString("/").lowercase()
        return if (
            "cache" in source ||
            "tmp" in source ||
            "temp" in source ||
            ".trash" in source ||
            ".statuses" in source ||
            "thumb" in source
        ) {
            WhatsAppCleanerGroup.Cache
        } else {
            WhatsAppCleanerGroup.File
        }
    }

    private fun ManagedFileItem.toCleanerCategory(): WhatsAppCleanerCategory {
        val source = listOfNotNull(path, bucketName, name).joinToString("/").lowercase()
        val extension = name.substringAfterLast('.', missingDelimiterValue = "").lowercase()
        return when {
            "database" in source || extension in DATABASE_EXTENSIONS -> WhatsAppCleanerCategory.Databases
            "document" in source || (type == ManagedFileType.Document && extension in DOCUMENT_EXTENSIONS) -> WhatsAppCleanerCategory.Documents
            type == ManagedFileType.Image -> WhatsAppCleanerCategory.Images
            type == ManagedFileType.Video -> WhatsAppCleanerCategory.Videos
            type == ManagedFileType.Audio -> WhatsAppCleanerCategory.Audios
            else -> WhatsAppCleanerCategory.Other
        }
    }

    private fun WhatsAppCleanerCategory.shouldSelectByDefault(group: WhatsAppCleanerGroup): Boolean =
        group == WhatsAppCleanerGroup.Cache || this == WhatsAppCleanerCategory.Other

    private companion object {
        private const val SCAN_DELAY_MILLIS = 650L
        private const val CLEANING_ANIMATION_MIN_MILLIS = 1600L
        private const val COMPLETE_ANIMATION_MILLIS = 800L
        val DATABASE_EXTENSIONS = setOf("db", "crypt", "crypt5", "crypt7", "crypt8", "crypt12", "crypt14", "crypt15")
        val DOCUMENT_EXTENSIONS = setOf(
            "pdf",
            "doc",
            "docx",
            "xls",
            "xlsx",
            "ppt",
            "pptx",
            "txt",
            "csv",
            "rtf",
            "zip",
            "rar",
            "7z",
            "apk",
            "json",
            "xml",
        )
    }

    private fun appString(resId: Int): String = message(resId)
}

