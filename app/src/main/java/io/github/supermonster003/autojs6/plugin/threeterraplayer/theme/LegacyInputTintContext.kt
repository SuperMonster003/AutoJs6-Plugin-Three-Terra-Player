package io.github.supermonster003.autojs6.plugin.threeterraplayer.theme

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.widget.TextView

/**
 * Local API 24-28 cursor/handle tinting using public resource dispatch only.
 * Editor loads these drawables lazily through TextView.context.getDrawable(); no private fields,
 * framework resource names or global theme/resource mutations are used.
 */
@Suppress("DEPRECATION") // Resources constructor/legacy overloads remain public on these APIs.
internal class LegacyInputTintContext(base: Context, color: Int) : ContextWrapper(base) {
    private val tintedResources = InputResources(base.resources, color)

    override fun getResources(): Resources = tintedResources

    /** Call after Material has wrapped the input's context, before its first draw. */
    fun register(input: TextView) {
        val ids = mutableSetOf<Int>()
        // AppCompat is the real default used by TextInputEditText. Include the platform default
        // for platform widgets/OEM styles, and resolve against the final Material context.
        for (style in intArrayOf(androidx.appcompat.R.attr.editTextStyle, android.R.attr.editTextStyle)) {
            val values = input.context.obtainStyledAttributes(null, DRAWABLE_ATTRIBUTES, style, 0)
            try {
                for (index in DRAWABLE_ATTRIBUTES.indices) {
                    values.getResourceId(index, 0).takeIf { it != 0 }?.let(ids::add)
                }
            } finally {
                values.recycle()
            }
        }
        tintedResources.drawableIds = ids
    }

    private class InputResources(private val source: Resources, private val color: Int) :
        Resources(source.assets, source.displayMetrics, source.configuration) {
        var drawableIds: Set<Int> = emptySet()

        private fun tint(id: Int, drawable: Drawable): Drawable =
            if (id in drawableIds) drawable.mutate().apply { setTint(color) } else drawable

        override fun getDrawable(id: Int): Drawable = tint(id, source.getDrawable(id))
        override fun getDrawable(id: Int, theme: Theme?): Drawable = tint(id, source.getDrawable(id, theme))
        override fun getDrawableForDensity(id: Int, density: Int): Drawable? =
            source.getDrawableForDensity(id, density)?.let { tint(id, it) }
        override fun getDrawableForDensity(id: Int, density: Int, theme: Theme?): Drawable? =
            source.getDrawableForDensity(id, density, theme)?.let { tint(id, it) }
    }

    private companion object {
        val DRAWABLE_ATTRIBUTES = intArrayOf(
            android.R.attr.textCursorDrawable,
            android.R.attr.textSelectHandle,
            android.R.attr.textSelectHandleLeft,
            android.R.attr.textSelectHandleRight,
        )
    }
}
