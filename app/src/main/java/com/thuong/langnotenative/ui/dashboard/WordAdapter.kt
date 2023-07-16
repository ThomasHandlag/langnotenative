package com.thuong.langnotenative.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thuong.langnotenative.databinding.WordItemBinding
import com.thuong.langnotenative.db.Word

val wordDiffUtil = object : DiffUtil.ItemCallback<Word>() {
    override fun areItemsTheSame(o: Word, n: Word): Boolean {
        return o == n
    }

    override fun areContentsTheSame(o: Word, n: Word): Boolean {
        return o == n
    }

}
class WordAdapter(): ListAdapter<Word, WordAdapter.WordViewHolder>(wordDiffUtil) {
        inner class WordViewHolder(private val binding: WordItemBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(word: Word) {
                with(binding) {

                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
            return WordViewHolder(WordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
}