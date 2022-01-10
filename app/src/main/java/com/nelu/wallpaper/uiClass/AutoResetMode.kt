package com.nelu.wallpaper.uiClass

@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
/*@IntDef(
    AutoResetMode.NEVER,
    AutoResetMode.UNDER,
    AutoResetMode.OVER,
    AutoResetMode.ALWAYS
)*/
annotation class AutoResetMode {

    object Parser {
        @AutoResetMode
        fun fromInt(value: Int): Int {
            return when (value) {
                OVER -> OVER
                ALWAYS -> ALWAYS
                NEVER -> NEVER
                else -> UNDER
            }
        }
    }

    companion object {
        var UNDER = 0
        var OVER = 1
        var ALWAYS = 2
        var NEVER = 3
    }
}