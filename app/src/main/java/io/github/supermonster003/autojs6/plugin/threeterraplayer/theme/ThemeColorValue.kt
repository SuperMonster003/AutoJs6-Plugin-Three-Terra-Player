package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

/** Pure input/value policy. No preferences or process theme are changed by a draft. */
internal object ThemeColorValue {
    val presets = listOf(0xffffdead.toInt(), 0xfff44336.toInt(), 0xffe91e63.toInt(),
        0xff9c27b0.toInt(), 0xff673ab7.toInt(), 0xff3f51b5.toInt(), 0xff2196f3.toInt(),
        0xff03a9f4.toInt(), 0xff00bcd4.toInt(), 0xff009688.toInt(), 0xff4caf50.toInt(),
        0xff8bc34a.toInt(), 0xffff9800.toInt(), 0xffff5722.toInt(), 0xff795548.toInt(), 0xff607d8b.toInt())
    private val hex = Regex("#?([0-9a-fA-F]{6})")
    private val rgb = Regex("rgb\\(\\s*(\\d{1,3})\\s*,\\s*(\\d{1,3})\\s*,\\s*(\\d{1,3})\\s*\\)", RegexOption.IGNORE_CASE)

    fun parse(input: String): Int? {
        val text = input.trim()
        hex.matchEntire(text)?.let { return it.groupValues[1].toInt(16) or -0x1000000 }
        val channels = rgb.matchEntire(text)?.groupValues?.drop(1)?.map(String::toInt) ?: return null
        if (channels.any { it !in 0..255 }) return null
        return -0x1000000 or (channels[0] shl 16) or (channels[1] shl 8) or channels[2]
    }

    fun hex(color: Int): String = String.format(java.util.Locale.ROOT, "#%06X", color and 0xffffff)
    fun rgb(color: Int): String = "rgb(${color ushr 16 and 255}, ${color ushr 8 and 255}, ${color and 255})"

    fun onColor(color: Int): Int {
        fun linear(value: Int): Double = (value / 255.0).let { if (it <= 0.04045) it / 12.92 else Math.pow((it + 0.055) / 1.055, 2.4) }
        val l = 0.2126 * linear(color ushr 16 and 255) + 0.7152 * linear(color ushr 8 and 255) + 0.0722 * linear(color and 255)
        return if ((l + 0.05) / 0.05 >= 1.05 / (l + 0.05)) 0xff000000.toInt() else 0xffffffff.toInt()
    }
}
