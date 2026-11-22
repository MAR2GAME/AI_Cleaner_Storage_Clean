package com.aicleanerstorage.spaceclean.app.view.common.files.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.currentStateAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.aicleanerstorage.spaceclean.app.view.common.files.interaction.FileManagerEvent
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.effect.FileManagerFlowEffects
import com.aicleanerstorage.spaceclean.base.mvi.MviViewModel
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.navigation.AppNavigator
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionRequestEffect
import com.aicleanerstorage.spaceclean.base.permission.PermissionResult

/**
 * 鏂囦欢绠＄悊娴佺▼鐨勬潈闄愮姸鎬佸涓汇€? *
 * 鐘舵€佹寕鍦ㄦ祦绋嬫牴 entry 鐨?ViewModel 涓婏紝鍒楄〃椤典笌璇︽儏椤靛叡鐢ㄥ悓涓€涓疄渚嬶紱
 * [onCleared] 鏃舵爣璁扮寮€锛岄伩鍏嶈繑鍥炶繃绋嬩腑鐨勮繜鍒颁簨浠跺啀娆″鑸€? *
 * @param requiredPermissions 鏈祦绋嬮渶瑕佺殑鏉冮檺闆嗗悎銆? * @param initialGranted 鍒涘缓鏃舵牴鎹郴缁熺姸鎬佺畻鍑虹殑鍒濆鍊笺€? */
internal class FileManagerOwnerState(
    requiredPermissions: Set<AppPermission>,
    initialGranted: Boolean,
) : ViewModel() {
    val permissionState = FileManagerPermissionState(requiredPermissions, initialGranted)

    override fun onCleared() {
        permissionState.markLeaving()
        super.onCleared()
    }
}

/**
 * 鍙栧緱锛堟垨鍒涘缓锛夋枃浠剁鐞嗘祦绋嬬殑鏉冮檺鐘舵€併€? *
 * @param ownerEntry 娴佺▼鏍?destination 鐨?back stack entry銆? * @param requiredPermissions 鏈祦绋嬮渶瑕佺殑鏉冮檺闆嗗悎銆? * @return 鎸傚湪 [ownerEntry] 涓娿€佸垪琛ㄩ〉涓庤鎯呴〉鍏变韩鐨勬潈闄愮姸鎬併€? */
@Composable
internal fun rememberFileManagerOwnerState(
    ownerEntry: NavBackStackEntry,
    requiredPermissions: Set<AppPermission>,
): FileManagerPermissionState {
    val context = LocalContext.current
    val granted = requiredPermissions.all { it.isGranted(context) }
    val factory =
        remember(granted, requiredPermissions) {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    FileManagerOwnerState(requiredPermissions, granted) as T
            }
        }
    val holder: FileManagerOwnerState = viewModel(viewModelStoreOwner = ownerEntry, factory = factory)
    return holder.permissionState
}

/**
 * 鏂囦欢绠＄悊娴佺▼瀹夸富锛氬垪琛ㄩ〉涓庤鎯呴〉鍚勮嚜鎸傝浇涓€浠斤紝浣嗗彧鏈夊浜?RESUMED 鐨勫綋鍓嶉〉浼氭敹闆? * [MviViewModel.event]锛屽洜姝ゆ祦绋嬬骇 effect 鍦ㄤ换鎰忔椂鍒诲彧鏈変竴涓秷璐硅€呫€? *
 * 杩涘叆椤甸潰鍜屼粠绯荤粺璁剧疆杩斿洖鏃堕兘浼氶噸鏂版煡璇?[requiredPermissions]锛涙潈闄愰綈澶囧悗
 * [FileManagerFlowEffects] 鎵嶄細瑙﹀彂棣栨鍔犺浇锛岄伩鍏嶁€滃厛鎵弿銆佸悗鎶ユ潈闄愬け璐モ€濄€? *
 * @param rootEntry 娴佺▼鏍?destination 鐨?entry锛屾潈闄愮姸鎬佹寕鍦ㄥ畠涓婇潰銆? * @param currentEntry 褰撳墠椤甸潰鐨?entry锛岀敤浜庡垽鏂槸鍚?RESUMED銆? * @param navigator 搴旂敤瀵艰埅鍣紝鐢?effect 鎵ц璺宠浆銆? * @param viewModel 鏈祦绋嬪叡浜殑 MVI ViewModel銆? * @param error 浠庨〉闈㈢姸鎬佷腑鍙栭敊璇枃妗堬紱涓?null 鏃朵笉鎻愮ず銆? * @param requiredPermissions 鏈祦绋嬮渶瑕佺殑鏉冮檺闆嗗悎锛岄粯璁ゅ彧瑕佹眰瀛樺偍鏉冮檺銆? * @param content 椤甸潰鍐呭锛涘弬鏁版槸鍏变韩鐨勬潈闄愮姸鎬併€? */
@Composable
internal fun <S : Any, A : Any> FileManagerFlowHost(
    rootEntry: NavBackStackEntry,
    currentEntry: NavBackStackEntry,
    navigator: AppNavigator,
    viewModel: MviViewModel<S, A, FileManagerEvent>,
    initialLoad: A,
    dismissError: A,
    error: (S) -> UiText?,
    requiredPermissions: Set<AppPermission> = setOf(AppPermission.StorageFiles),
    requestPermissions: Boolean = true,
    content: @Composable (FileManagerPermissionState) -> Unit,
) {
    val context = LocalContext.current
    val permissionState = rememberFileManagerOwnerState(rootEntry, requiredPermissions)
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleState by currentEntry.lifecycle.currentStateAsState()
    var pendingPermission by remember(requestPermissions, requiredPermissions) {
        mutableStateOf(
            if (requestPermissions) {
                requiredPermissions.firstOrNull { !it.isGranted(context) }
            } else {
                null
            },
        )
    }

    PermissionRequestEffect(pendingPermission) { result ->
        pendingPermission = null
        permissionState.refresh(context)
        when (result) {
            PermissionResult.Granted ->
                pendingPermission = requiredPermissions.firstOrNull { !it.isGranted(context) }
            PermissionResult.Denied,
            PermissionResult.PermanentlyDenied,
            PermissionResult.RequiresSettings,
            -> navigator.back()
        }
    }

    DisposableEffect(lifecycleOwner, permissionState) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    permissionState.refresh(context)
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (lifecycleState.isAtLeast(Lifecycle.State.RESUMED)) {
        FileManagerFlowEffects(
            FileManagerFlowParams(
                effects = viewModel.event,
                navigator = navigator,
                permissionState = permissionState,
                error = error(uiState),
                onClearError = { viewModel.dispatch(dismissError) },
                onStartIfNeeded = { viewModel.dispatch(initialLoad) },
            ),
        )
    }
    content(permissionState)
}


