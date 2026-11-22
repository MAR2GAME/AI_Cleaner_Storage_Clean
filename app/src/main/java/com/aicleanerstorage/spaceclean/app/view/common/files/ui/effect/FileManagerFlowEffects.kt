package com.aicleanerstorage.spaceclean.app.view.common.files.ui.effect

import androidx.compose.runtime.Composable
import com.aicleanerstorage.spaceclean.app.view.common.files.ui.FileManagerFlowParams

/**
 * 鏂囦欢绠＄悊娴佺▼鐨勫壇浣滅敤闆嗗悎锛氬鑸€侀敊璇彁绀恒€佹潈闄愰綈澶囧悗鐨勯娆″姞杞姐€?
 *
 * @param effects ViewModel 涓嬪彂鐨勪簨浠舵祦銆?
 * @param navigator 搴旂敤瀵艰埅鍣ㄣ€?
 * @param permissionState 娴佺▼鏉冮檺鐘舵€侊紱棣栨鍔犺浇浠?[FileManagerPermissionState.granted] 涓哄墠鎻愩€?
 * @param error 褰撳墠閿欒鏂囨锛涗负 null 鏃朵笉鎻愮ず銆?
 * @param onClearError 閿欒鎻愮ず娑堣垂鍚庣殑鍥炶皟銆?
 * @param onStartIfNeeded 鏉冮檺榻愬鍚庤Е鍙戦娆″姞杞界殑鍥炶皟銆?
 */
@Composable
fun FileManagerFlowEffects(params: FileManagerFlowParams) {
    FileManagerOperationEventsEffect(params.effects, params.permissionState, params.navigator)
    FileManagerErrorToastEffect(params.error, params.onClearError)
    FileManagerStartEffect(params.permissionState, params.onStartIfNeeded)
}


