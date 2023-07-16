package com.thuong.langnotenative.ui.dashboard

import android.os.Bundle
import android.text.Html
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.thuong.langnotenative.R
import com.thuong.langnotenative.databinding.FragmentAddWordBinding
import com.thuong.langnotenative.db.AppDB
import com.thuong.langnotenative.db.Word
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddWordFragment : Fragment() {

    private lateinit var viewModel: WordViewModel
    private lateinit var binding: FragmentAddWordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddWordBinding.inflate(inflater, container, false)
        val args: AddWordFragmentArgs by navArgs()
        viewModel = ViewModelProvider(
            requireActivity(), WordViewModelFactory(
                AppDB.getDatabase(
                    requireNotNull(this.activity).application
                ).appDao
            )
        )[WordViewModel::class.java]
        val listType = listOf(
            "Idiom",
            "Verb",
            "Noun",
            "Adjective",
            "Adverb"
        )
        with(binding) {
            boldBtn.setOnClickListener {
                boldSelected()
            }
            italicBtn.setOnClickListener {
                italicText()
            }
            underlineBtn.setOnClickListener {
                underlineText()
            }
            addImgBtn.setOnClickListener {
                Toast.makeText(requireContext(), R.string.unavailable_function, Toast.LENGTH_SHORT)
                    .show()
            }
            adjustColorBtn.setOnClickListener {
                colorizeText()
            }
            centeredBtn.setOnClickListener {
                if (viewModel.selectedViewId.value == binding.noteEdittext.id.toString()) {
                    noteEdittext.gravity = Gravity.CENTER_HORIZONTAL
                } else if (viewModel.selectedViewId.value == binding.noteEdittext.id.toString()) {
                    noteEdittext.gravity = Gravity.CENTER_HORIZONTAL

                }
            }
            leftBtn.setOnClickListener {
                if (viewModel.selectedViewId.value == binding.noteEdittext.id.toString()) {
                    noteEdittext.gravity = Gravity.START
                } else if (viewModel.selectedViewId.value == binding.noteEdittext.id.toString()) {
                    noteEdittext.gravity = Gravity.START

                }
            }
            rightBtn.setOnClickListener {
                if (viewModel.selectedViewId.value == binding.noteEdittext.id.toString()) {
                    noteEdittext.gravity = Gravity.END
                } else if (viewModel.selectedViewId.value == binding.noteEdittext.id.toString()) {
                    noteEdittext.gravity = Gravity.END
                }
            }
            noteEdittext.setOnFocusChangeListener { it, _ ->
                viewModel.selectedViewId.value = it.id.toString()
            }
            val adapter = ArrayAdapter(requireContext(), R.layout.type_litem, listType)
            typeEdit.setAdapter(adapter)
            typeEdit.setOnItemClickListener { _, _, ind, _ ->
                viewModel.typeSelected.value = ind
            }
        }
        when (args.type) {
            "ADD" -> {
                with(binding) {
                    addBtn.text = requireContext().getString(R.string.add)

                    addBtn.setOnClickListener {
                        val word = wordEdit.text
                        val trans = transEdit.text
                        val type = viewModel.typeSelected.value!!
                        val note = noteEdittext.text
                        viewModel.viewModelScope.launch {
                            viewModel.addWordItem(
                                Word(
                                    word = Html.toHtml(word, Html.FROM_HTML_MODE_COMPACT),
                                    mean = Html.toHtml(trans, Html.FROM_HTML_MODE_COMPACT),
                                    uid = generateUID(),
                                    type = type,
                                    eg = Html.toHtml(note, Html.FROM_HTML_MODE_COMPACT),
                                    date = dateString()
                                )
                            )
                        }
                        viewModel.initWordItems()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }

            "EDIT" -> {
                with(binding) {
                    args.word?.let {
                        val name = Html.fromHtml(it.word, Html.FROM_HTML_MODE_COMPACT)
                        val cont = Html.fromHtml(it.mean, Html.FROM_HTML_MODE_COMPACT)
                        wordEdit.setText(name)
                        transEdit.setText(cont)
                        typeEdit.setText(requireContext().getString(R.string.str, listType[it.type]), false)
                        viewModel.typeSelected.value = it.type
                        addBtn.setOnClickListener { _ ->
                            viewModel.updateWordItem(
                                Word(
                                    uid = it.uid,
                                    word = wordEdit.text.toString(),
                                    mean = transEdit.text.toString(),
                                    date = it.date,
                                    eg = Html.toHtml(
                                        noteEdittext.text,
                                        Html.FROM_HTML_MODE_COMPACT
                                    ),
                                    type = viewModel.typeSelected.value!!
                                )
                            )
                            viewModel.initWordItems()
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                    addBtn.text = requireContext().getString(R.string.save_lb)
                }
            }
        }
        return binding.root
    }

    private fun generateUID(): String {
        var id = ""
        val str = "qwertyuiopasdfghjklzxcvbnm1234567890"
        val len = str.length - 1
        for (i in 0..10) {
            id += str[(0..len).random()]
        }
        return id
    }

    private fun dateString(): String {
        return SimpleDateFormat(
            "MM/dd/yyyy HH:mm a",
            Locale.getDefault()
        ).format(Calendar.getInstance().time)
    }

    private fun boldSelected() {
        val selectedText: String
        if (viewModel.selectedViewId.value == binding.noteEdittext.id.toString()) {
            val startInd = binding.noteEdittext.selectionStart
            val endInd = binding.noteEdittext.selectionEnd
            if (startInd == endInd) {
                selectedText = binding.noteEdittext.text?.substring(0).toString()
                val text = Html.fromHtml("<b>$selectedText</b>", Html.FROM_HTML_MODE_COMPACT)
                binding.noteEdittext.setText(text)
            } else {
                selectedText = binding.noteEdittext.text?.substring(startInd, endInd).toString()
                val text = Html.fromHtml("<b>$selectedText</b>", Html.FROM_HTML_MODE_COMPACT)
                binding.noteEdittext.text?.replace(startInd, endInd, text)
            }
        }
    }

    private fun underlineText() {
        val selectedText: String
        if (viewModel.selectedViewId.value == binding.noteEdittext.id.toString()) {
            val startInd = binding.noteEdittext.selectionStart
            val endInd = binding.noteEdittext.selectionEnd
            if (startInd == endInd) {
                selectedText = binding.noteEdittext.text?.substring(0).toString()
                val text = Html.fromHtml("<u>$selectedText</u>", Html.FROM_HTML_MODE_COMPACT)
                binding.noteEdittext.setText(text)
            } else {
                selectedText = binding.noteEdittext.text?.substring(startInd, endInd).toString()
                val text = Html.fromHtml("<u>$selectedText</u>", Html.FROM_HTML_MODE_COMPACT)
                binding.noteEdittext.text?.replace(startInd, endInd, text)
            }
        }
    }

    private fun italicText() {
        val selectedText: String
        if (viewModel.selectedViewId.value == binding.noteEdittext.id.toString()) {
            val startInd = binding.noteEdittext.selectionStart
            val endInd = binding.noteEdittext.selectionEnd
            if (startInd == endInd) {
                selectedText = binding.noteEdittext.text?.substring(0).toString()
                val text = Html.fromHtml("<i>$selectedText</i>", Html.FROM_HTML_MODE_COMPACT)
                binding.noteEdittext.setText(text)
            } else {
                selectedText = binding.noteEdittext.text?.substring(startInd, endInd).toString()
                val text = Html.fromHtml("<i>$selectedText</i>", Html.FROM_HTML_MODE_COMPACT)
                binding.noteEdittext.text?.replace(startInd, endInd, text)
            }
        }
    }

    private fun colorizeText() {
        val selectedText: String
        if (viewModel.selectedViewId.value == binding.noteEdittext.id.toString()) {
            val startInd = binding.noteEdittext.selectionStart
            val endInd = binding.noteEdittext.selectionEnd
            if (startInd == endInd) {
                selectedText = binding.noteEdittext.text?.substring(0).toString()
                val text = Html.fromHtml(
                    "<font color='#ff0011'>$selectedText</font>",
                    Html.FROM_HTML_MODE_COMPACT
                )
                binding.noteEdittext.setText(text)
            } else {
                selectedText = binding.noteEdittext.text?.substring(startInd, endInd).toString()
                val text = Html.fromHtml(
                    "<font color='#ff0011'>$selectedText</font>",
                    Html.FROM_HTML_MODE_COMPACT
                )
                binding.noteEdittext.text?.replace(startInd, endInd, text)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.selectedViewId.value = ""
    }
}