package com.thuong.langnotenative.ui.dashboard

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.thuong.langnotenative.R
import com.thuong.langnotenative.databinding.WordItemBinding

val wordDiffUtil = object : DiffUtil.ItemCallback<WordItem>() {
    override fun areItemsTheSame(o: WordItem, n: WordItem): Boolean {
        return o == n
    }

    override fun areContentsTheSame(o: WordItem, n: WordItem): Boolean {
        return o == n
    }

}

class WordAdapter(private val context: Context, private val action: ItemWordAction) :
    ListAdapter<WordItem, WordAdapter.WordViewHolder>(wordDiffUtil) {
    init {
        setHasStableIds(true)
    }
    inner class WordViewHolder(private val binding: WordItemBinding) :
        ViewHolder(binding.root) {
        fun bind(word: WordItem) {
            with(binding) {
                name.text = context.getString(
                    R.string.str,
                    Html.fromHtml(word.word.word, Html.FROM_HTML_MODE_COMPACT)
                )
                preCont.text = context.getString(
                    R.string.str,
                    Html.fromHtml(word.word.mean, Html.FROM_HTML_MODE_COMPACT)
                )
                checkbox.isSelected = word.isSelected
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) action.setSelected(word)
                    else action.setUnselected(word.word)
                }
            }
        }

        fun showCheckbox() {
            binding.checkbox.visibility = View.VISIBLE
        }

        fun hideCheckbox() {
            binding.checkbox.visibility = View.GONE
        }
        fun isChecked(bool: Boolean) {
            binding.checkbox.isChecked = bool
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        return WordViewHolder(
            WordItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = getItem(position)
        holder.bind(word)
        if (word.isShowBox) {
            holder.showCheckbox()
        } else holder.hideCheckbox()
        holder.itemView.setOnClickListener {
            action.openEditor(word.word)
        }
        holder.itemView.setOnLongClickListener {
            action.startSelected()
            true
        }
        if (word.isSelected)
        {
            holder.isChecked(true)
        }
        else holder.isChecked(false)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int = currentList.size
}