package io.github.supermonster003.autojs6.plugin.audioplayer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.supermonster003.autojs6.plugin.audioplayer.databinding.ItemPlaybackQueueBinding

internal data class PlaybackQueueRow(
    val mediaId: String,
    val index: Int,
    val title: String,
    val current: Boolean,
)

internal class PlaybackQueueAdapter(
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
            binding.currentIndicator.isVisible = row.current
            binding.trackTitle.text = row.title
            binding.trackPosition.text = binding.root.context.getString(
                R.string.queue_item_position,
                row.index + 1,
            )
            binding.root.isActivated = row.current
            binding.root.setOnClickListener {
                bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let(onSelect)
            }
            binding.removeButton.contentDescription = binding.root.context.getString(
                R.string.action_remove_from_queue_named,
                row.title,
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
}
