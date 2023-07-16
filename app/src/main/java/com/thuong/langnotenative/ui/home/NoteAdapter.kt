package com.thuong.langnotenative.ui.home

import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.thuong.langnotenative.R
import com.thuong.langnotenative.databinding.NoteItemBinding
import com.thuong.langnotenative.db.Note

val diffUtil = object : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(o: Note, n: Note): Boolean {
        return o == n
    }

    override fun areContentsTheSame(o: Note, n: Note): Boolean {
        return o == n
    }

}

class NoteAdapter(private val context: Context, private val noteAction: NoteItemAction) :
    ListAdapter<Note, NoteAdapter.NoteViewHolder>(diffUtil) {
    lateinit var adapterHelper: AdapterHelper

    inner class NoteViewHolder(private val binding: NoteItemBinding) : ViewHolder(binding.root) {
        fun bind(note: Note) {
            with(binding) {
                name.text = context.getString(
                    R.string.str,
                    Html.fromHtml(note.name, Html.FROM_HTML_MODE_COMPACT)
                )
                preCont.text =
                    context.getString(
                        R.string.str,
                        Html.fromHtml(note.cont, Html.FROM_HTML_MODE_COMPACT)
                    )
                if (noteAction.isShowCheckBox()) {
                    checkBox.visibility = View.VISIBLE
                }
                checkBox.setOnClickListener {
                    if (checkBox.isChecked) noteAction.setSelected(note.uid)
                    else noteAction.setUnselected(note.uid)
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
        adapterHelper = object : AdapterHelper {
            override fun hideCheckBox() {
                holder.hideCheckbox()
            }

            override fun checkAll() {

            }
        }
        holder.bind(getItem(position))
        holder.itemView.setOnLongClickListener {
            noteAction.startSelected()
            holder.showCheckbox()
            true
        }
        holder.itemView.setOnClickListener {
            noteAction.openEditor(getItem(position))
        }
    }
}

interface AdapterHelper {
    fun hideCheckBox()
    fun checkAll()
}