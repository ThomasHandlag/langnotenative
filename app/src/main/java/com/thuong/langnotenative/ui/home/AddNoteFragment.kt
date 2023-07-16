package com.thuong.langnotenative.ui.home

import android.os.Bundle
import android.text.Html
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.thuong.langnotenative.R
import com.thuong.langnotenative.databinding.FragmentAddNoteBinding
import com.thuong.langnotenative.db.AppDB
import com.thuong.langnotenative.db.Note
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddNoteFragment : Fragment() {
    private lateinit var binding: FragmentAddNoteBinding
    private lateinit var viewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNoteBinding.inflate(
            inflater,
            container,
            false
        )
        viewModel = ViewModelProvider(
            requireActivity(), HomeViewModelFactory(
                AppDB.getDatabase(
                    requireNotNull(this.activity).application
                ).appDao
            )
        )[HomeViewModel::class.java]
        //safe arguments
        val args: AddNoteFragmentArgs by navArgs()
        when (args.action) {
            "ADD" -> {
                with(binding) {
                    addBtn.setOnClickListener {
                        val subj = subjEditText.text
                        val cont = contEditText.text
                        viewModel.viewModelScope.launch {
                            viewModel.addNoteItem(
                                Note(
                                    name = Html.toHtml(subj, Html.FROM_HTML_MODE_COMPACT),
                                    cont = Html.toHtml(cont, Html.FROM_HTML_MODE_COMPACT),
                                    uid = generateUID(),
                                    date = dateString()
                                )
                            )
                        }
                        viewModel.initNoteItems()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                }
            }

            "EDIT" -> {
                with(binding) {
                    args.note?.let {
                        val name = Html.fromHtml(it.name, Html.FROM_HTML_MODE_COMPACT)
                        val cont = Html.fromHtml(it.cont, Html.FROM_HTML_MODE_COMPACT)
                        subjEditText.setText(name)
                        contEditText.setText(cont)
                        addBtn.setOnClickListener { _ ->
                            viewModel.updateNoteItem(
                                Note(
                                    uid = it.uid,
                                    name = Html.toHtml(
                                        subjEditText.text,
                                        Html.FROM_HTML_MODE_COMPACT
                                    ),
                                    cont = Html.toHtml(
                                        contEditText.text,
                                        Html.FROM_HTML_MODE_COMPACT
                                    )
                                )
                            )
                            viewModel.initNoteItems()
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                    addBtn.text = requireContext().getString(R.string.save_lb)
                }
            }
        }
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
                Toast.makeText(requireContext(), R.string.unavailable_function, Toast.LENGTH_SHORT).show()
            }
            adjustColorBtn.setOnClickListener {
                colorizeText()
            }
            centeredBtn.setOnClickListener {
                if (viewModel.selectedViewId.value == binding.subjEditText.id.toString()) {
                        subjEditText.gravity = Gravity.CENTER_HORIZONTAL
                } else if (viewModel.selectedViewId.value == binding.contEditText.id.toString()) {
                    contEditText.gravity = Gravity.CENTER_HORIZONTAL

                }
            }
            leftBtn.setOnClickListener {
                if (viewModel.selectedViewId.value == binding.subjEditText.id.toString()) {
                    subjEditText.gravity = Gravity.START
                } else if (viewModel.selectedViewId.value == binding.contEditText.id.toString()) {
                    contEditText.gravity = Gravity.START

                }
            }
            rightBtn.setOnClickListener {
                if (viewModel.selectedViewId.value == binding.subjEditText.id.toString()) {
                    subjEditText.gravity = Gravity.END
                } else if (viewModel.selectedViewId.value == binding.contEditText.id.toString()) {
                    contEditText.gravity = Gravity.END
                }
            }
            subjEditText.setOnFocusChangeListener { it, _ ->
                viewModel.selectedViewId.value = it.id.toString()
            }
            contEditText.setOnFocusChangeListener { it, _ ->
                viewModel.selectedViewId.value = it.id.toString()
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
        if (viewModel.selectedViewId.value == binding.subjEditText.id.toString()) {
            val startInd = binding.subjEditText.selectionStart
            val endInd = binding.subjEditText.selectionEnd
            if (startInd == endInd) {
                selectedText = binding.contEditText.text?.substring(0).toString()
                val text = Html.fromHtml("<b>$selectedText</b>", Html.FROM_HTML_MODE_COMPACT)
                binding.contEditText.setText(text)
            } else {
                selectedText = binding.contEditText.text?.substring(startInd, endInd).toString()
                val text = Html.fromHtml("<b>$selectedText</b>", Html.FROM_HTML_MODE_COMPACT)
                binding.contEditText.text?.replace(startInd, endInd, text)
            }
        } else if (viewModel.selectedViewId.value == binding.contEditText.id.toString()) {
            val startInd = binding.contEditText.selectionStart
            val endInd = binding.contEditText.selectionEnd
            if (startInd == endInd) {
                selectedText = binding.contEditText.text?.substring(0).toString()
                val text = Html.fromHtml("<b>$selectedText</b>", Html.FROM_HTML_MODE_COMPACT)
                binding.contEditText.setText(text)
            } else {
                selectedText = binding.contEditText.text?.substring(startInd, endInd).toString()
                val text = Html.fromHtml("<b>$selectedText</n>", Html.FROM_HTML_MODE_COMPACT)
                binding.contEditText.text?.replace(startInd, endInd, text)
            }
        }
    }
    private fun underlineText() {
        val selectedText: String
        if (viewModel.selectedViewId.value == binding.subjEditText.id.toString()) {
            val startInd = binding.subjEditText.selectionStart
            val endInd = binding.subjEditText.selectionEnd
            if (startInd == endInd) {
                selectedText = binding.contEditText.text?.substring(0).toString()
                val text = Html.fromHtml("<u>$selectedText</u>", Html.FROM_HTML_MODE_COMPACT)
                binding.contEditText.setText(text)
            } else {
                selectedText = binding.contEditText.text?.substring(startInd, endInd).toString()
                val text = Html.fromHtml("<u>$selectedText</u>", Html.FROM_HTML_MODE_COMPACT)
                binding.contEditText.text?.replace(startInd, endInd, text)
            }
        } else if (viewModel.selectedViewId.value == binding.contEditText.id.toString()) {
            val startInd = binding.contEditText.selectionStart
            val endInd = binding.contEditText.selectionEnd
            if (startInd == endInd) {
                selectedText = binding.contEditText.text?.substring(0).toString()
                val text = Html.fromHtml("<u>$selectedText</u>", Html.FROM_HTML_MODE_COMPACT)
                binding.contEditText.setText(text)
            } else {
                selectedText = binding.contEditText.text?.substring(startInd, endInd).toString()
                val text = Html.fromHtml("<u>$selectedText</u>", Html.FROM_HTML_MODE_COMPACT)
                binding.contEditText.text?.replace(startInd, endInd, text)
            }
        }
    }
    private fun italicText() {
        val selectedText: String
        if (viewModel.selectedViewId.value == binding.subjEditText.id.toString()) {
            val startInd = binding.subjEditText.selectionStart
            val endInd = binding.subjEditText.selectionEnd
            if (startInd == endInd) {
                selectedText = binding.subjEditText.text?.substring(0).toString()
                val text = Html.fromHtml("<i>$selectedText</i>", Html.FROM_HTML_MODE_COMPACT)
                binding.subjEditText.setText(text)
            } else {
                selectedText = binding.subjEditText.text?.substring(startInd, endInd).toString()
                val text = Html.fromHtml("<i>$selectedText</i>", Html.FROM_HTML_MODE_COMPACT)
                binding.subjEditText.text?.replace(startInd, endInd, text)
            }
        } else if (viewModel.selectedViewId.value == binding.contEditText.id.toString()) {
            val startInd = binding.contEditText.selectionStart
            val endInd = binding.contEditText.selectionEnd
            if (startInd == endInd) {
                selectedText = binding.contEditText.text?.substring(0).toString()
                val text = Html.fromHtml("<i>$selectedText</i>", Html.FROM_HTML_MODE_COMPACT)
                binding.contEditText.setText(text)
            } else {
                selectedText = binding.contEditText.text?.substring(startInd, endInd).toString()
                val text = Html.fromHtml("<i>$selectedText</i>", Html.FROM_HTML_MODE_COMPACT)
                binding.contEditText.text?.replace(startInd, endInd, text)
            }
        }
    }

    private fun colorizeText() {
        val selectedText: String
        if (viewModel.selectedViewId.value == binding.subjEditText.id.toString()) {
            val startInd = binding.subjEditText.selectionStart
            val endInd = binding.subjEditText.selectionEnd
            if (startInd == endInd) {
                selectedText = binding.subjEditText.text?.substring(0).toString()
                val text = Html.fromHtml(
                    "<font color='#ff0011'>$selectedText</font>",
                    Html.FROM_HTML_MODE_COMPACT
                )
                binding.subjEditText.setText(text)
            } else {
                selectedText = binding.subjEditText.text?.substring(startInd, endInd).toString()
                val text = Html.fromHtml(
                    "<font color='#ff0011'>$selectedText</font>",
                    Html.FROM_HTML_MODE_COMPACT
                )
                binding.subjEditText.text?.replace(startInd, endInd, text)
            }
        } else if (viewModel.selectedViewId.value == binding.contEditText.id.toString()) {
            val startInd = binding.contEditText.selectionStart
            val endInd = binding.contEditText.selectionEnd
            if (startInd == endInd) {
                selectedText = binding.contEditText.text?.substring(0).toString()
                val text = Html.fromHtml(
                    "<font color='#ff0011'>$selectedText</font>",
                    Html.FROM_HTML_MODE_COMPACT
                )
                binding.contEditText.setText(text)
            } else {
                selectedText = binding.contEditText.text?.substring(startInd, endInd).toString()
                val text = Html.fromHtml(
                    "<font color='#ff0011'>$selectedText</font>",
                    Html.FROM_HTML_MODE_COMPACT
                )
                binding.contEditText.text?.replace(startInd, endInd, text)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.selectedViewId.value = ""
    }
}