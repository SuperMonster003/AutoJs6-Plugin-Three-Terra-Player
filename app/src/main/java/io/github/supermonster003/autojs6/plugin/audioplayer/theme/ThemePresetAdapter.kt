package io.github.supermonster003.autojs6.plugin.audioplayer.theme

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.github.supermonster003.autojs6.plugin.audioplayer.R
import io.github.supermonster003.autojs6.plugin.audioplayer.databinding.ItemThemePresetBinding

internal class ThemePresetAdapter(
    private val names: List<String>,
    private val palette: AudioThemePalette,
    private val selectedKey: String?,
    private val onSelected: (ThemePreset) -> Unit,
) : RecyclerView.Adapter<ThemePresetAdapter.ViewHolder>() {

    init {
        require(names.size == ThemePresetCatalog.colors.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemThemePresetBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    )

    override fun getItemCount(): Int = ThemePresetCatalog.colors.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ThemePresetCatalog.colors[position], names[position])
    }

    inner class ViewHolder(
        private val binding: ItemThemePresetBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(preset: ThemePreset, name: String) {
            val selected = preset.key == selectedKey
            binding.themePresetSwatch.setCardBackgroundColor(preset.color)
            binding.themePresetSwatch.strokeColor = if (selected) palette.primary else palette.outline
            binding.themePresetSwatch.strokeWidth = dp(if (selected) 3 else 1)
            binding.themePresetCheck.isVisible = selected
            binding.themePresetCheck.imageTintList = ColorStateList.valueOf(
                AudioThemePaletteGenerator.bestMonochromeForeground(preset.color),
            )
            binding.themePresetName.text = name
            binding.themePresetName.setTextColor(palette.onSurface)
            binding.root.contentDescription = binding.root.context.getString(
                if (selected) {
                    R.string.theme_preset_selected_accessibility
                } else {
                    R.string.theme_preset_accessibility
                },
                name,
                AudioThemePaletteGenerator.colorHex(preset.color),
            )
            binding.root.setOnClickListener { onSelected(preset) }
        }

        private fun dp(value: Int): Int =
            (value * binding.root.resources.displayMetrics.density).toInt()
    }
}
