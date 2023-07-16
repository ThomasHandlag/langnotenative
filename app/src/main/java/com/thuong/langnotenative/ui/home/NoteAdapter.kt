package com.thuong.langnotenative.ui.home

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.thuong.langnotenative.R
import com.thuong.langnotenative.databinding.NoteItemBinding
import com.thuong.langnotenative.db.Note

val diffUtil = object : DiffUtil.ItemCallback<NoteItem>() {
    override fun areItemsTheSame(o: NoteItem, n: NoteItem): Boolean {
        return o == n
    }

    override fun areContentsTheSame(o: NoteItem, n: NoteItem): Boolean {
        return o == n
    }
}

class NoteAdapter(private val context: Context, private val noteAction: ItemAction) :
    ListAdapter<NoteItem, NoteAdapter.NoteViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnLongClickListener {
            noteAction.startSelected()
            true
        }
        holder.itemView.setOnClickListener {
            noteAction.openEditor(getItem(position).note)
        }
        if (getItem(position).isShowBox) {
            holder.showCheckbox()
        } else holder.hideCheckbox()
    }


    inner class NoteViewHolder(private val binding: NoteItemBinding) : ViewHolder(binding.root) {
        fun bind(note: NoteItem) {
            with(binding) {
                checkBox.isChecked = note.isSelected
                if (note.isShowBox) checkBox.visibility = View.VISIBLE
                else checkBox.visibility = View.GONE
                name.text = context.getString(
                    R.string.str,
                    Html.fromHtml(note.note.name, Html.FROM_HTML_MODE_COMPACT)
                )
                preCont.text =
                    context.getString(
                        R.string.str,
                        Html.fromHtml(note.note.cont, Html.FROM_HTML_MODE_COMPACT)
                    )
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) noteAction.setSelected(note)
                    else noteAction.setUnselected(note)
                }
            }
        }

        fun showCheckbox() {
            binding.checkBox.visibility = View.VISIBLE
        }

        fun hideCheckbox() {
            binding.checkBox.visibility = View.INVISIBLE
        }
    }
}
data class NoteItem(val note: Note, var isSelected: Boolean = false, var isShowBox: Boolean = false)
