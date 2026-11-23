package com.aicleanerstorage.spaceclean.base.navigation

/**
 * Main 页签请求在 `savedStateHandle` 中的编解码。
 *
 * 写入端只写 [String]；读取端同时兼容历史版本写入的 [MainTab] 枚举，
 * 避免升级后恢复旧状态时出现类型转换崩溃。
 */
internal object MainTabRequest {
    /**
     * 编码为可跨进程保存的字符串。
     *
     * @param tab 目标页签。
     * @return 页签名，作为 `savedStateHandle` 的值。
     */
    fun encode(tab: MainTab): String = tab.name

    /**
     * 解码页签；无法识别时返回 null，由调用方保留当前页签。
     *
     * @param value `savedStateHandle` 中读到的原始值，可能是字符串、旧枚举或空。
     * @return 识别出的页签；非法值返回 null。
     */
    fun decode(value: Any?): MainTab? =
        when (value) {
            is MainTab -> value
            is String -> MainTab.entries.firstOrNull { it.name == value }
            else -> null
        }
}
