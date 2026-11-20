package com.aicleanerstorage.spaceclean.app.view.junkclean.ui.part

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.junkclean.interaction.SelectionSummary
import com.aicleanerstorage.spaceclean.base.designsystem.component.buttons.CleanXGradientBottomActionBar

@Composable
internal fun JunkRemoveBar(
    selectedSummary: SelectionSummary,
    onClick: () -> Unit,
) {
    val onlyZeroByteSelection =
        selectedSummary.checkedCount == 0 && selectedSummary.checkedEmptyCategoryCount > 0
    val enabled = selectedSummary.checkedCount > 0
    val buttonText =
        when {
            selectedSummary.checkedCount > 0 ->
                stringResource(R.string.remove_size, compactSizeLabel(selectedSummary.checkedSize))
            onlyZeroByteSelection -> stringResource(R.string.result_zero_byte_selection_button)
            else -> stringResource(R.string.result_select_items_button)
        }

    CleanXGradientBottomActionBar(
        text = buttonText,
        enabled = enabled,
        onClick = onClick,
    )
}

private fun compactSizeLabel(size: Long): String {
    val mainSize = displayMainSize(size)
    val unit = displayUnit(size)
    return "$mainSize$unit"
}
