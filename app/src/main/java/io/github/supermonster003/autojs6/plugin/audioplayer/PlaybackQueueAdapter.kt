package io.github.supermonster003.autojs6.plugin.audioplayer

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.supermonster003.autojs6.plugin.audioplayer.databinding.ItemPlaybackQueueBinding
import io.github.supermonster003.autojs6.plugin.audioplayer.theme.AudioThemePalette
import io.github.supermonster003.autojs6.plugin.audioplayer.theme.AudioThemePaletteGenerator

internal data class PlaybackQueueRow(
    val mediaId: String,
    val index: Int,
    val title: String,
    val current: Boolean,
)

internal class PlaybackQueueAdapter(
    private val palette: AudioThemePalette,
    private val onSelect: (Int) -> Unit,
    private val onRemove: (Int) -> Unit,
) : ListAdapter<PlaybackQueueRow, PlaybackQueueAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemPlaybackQueueBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemPlaybackQueueBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(row: PlaybackQueueRow) {
            val rowBackground = if (row.current) {
                palette.primaryContainer
            } else {
                palette.surfaceContainerLow
            }
            val rowForeground = if (row.current) {
                palette.onPrimaryContainer
            } else {
                palette.onSurface
            }
            binding.root.background = RippleDrawable(
                ColorStateList.valueOf(
                    AudioThemePaletteGenerator.withAlpha(palette.primary, RIPPLE_ALPHA),
                ),
                rowBackground.toDrawable(),
                null,
            )
            binding.currentIndicator.isVisible = row.current
            ImageViewCompat.setImageTintList(
                binding.currentIndicator,
                ColorStateList.valueOf(rowForeground),
            )
            binding.trackTitle.text = row.title
            binding.trackTitle.setTextColor(rowForeground)
            binding.trackPosition.text = binding.root.context.getString(
                R.string.queue_item_position,
                row.index + 1,
            )
            binding.trackPosition.setTextColor(
                if (row.current) {
                    AudioThemePaletteGenerator.withAlpha(rowForeground, SECONDARY_TEXT_ALPHA)
                } else {
                    palette.onSurfaceVariant
                },
            )
            binding.root.isActivated = row.current
            binding.root.setOnClickListener {
                bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let(onSelect)
            }
            binding.removeButton.contentDescription = binding.root.context.getString(
                R.string.action_remove_from_queue_named,
                row.title,
            )
            ImageViewCompat.setImageTintList(
                binding.removeButton,
                ColorStateList.valueOf(rowForeground),
            )
            binding.removeButton.setOnClickListener {
                bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let(onRemove)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<PlaybackQueueRow>() {
        override fun areItemsTheSame(oldItem: PlaybackQueueRow, newItem: PlaybackQueueRow): Boolean =
            oldItem.mediaId == newItem.mediaId

        override fun areContentsTheSame(oldItem: PlaybackQueueRow, newItem: PlaybackQueueRow): Boolean =
            oldItem == newItem
    }

    private companion object {
        const val RIPPLE_ALPHA = 0x24
        const val SECONDARY_TEXT_ALPHA = 0xCC
    }
}
