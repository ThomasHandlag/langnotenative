package com.thuong.langnotenative.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.checkbox.MaterialCheckBox
import com.thuong.langnotenative.R
import com.thuong.langnotenative.databinding.FragmentHomeBinding
import com.thuong.langnotenative.db.AppDB
import com.thuong.langnotenative.db.Note

class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val database = AppDB.getDatabase(this.requireContext())
        viewModel = ViewModelProvider(
            requireActivity(),
            HomeViewModelFactory(database.appDao)
        )[HomeViewModel::class.java]

        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val noteAction = object : NoteItemAction {
            override fun setSelected(str: String) {
                viewModel.setSelected(str)
            }

            override fun openEditor(note: Note) {
                navController.navigate(HomeFragmentDirections.editOrAdd("EDIT", note))
            }

            override fun startSelected() {
                viewModel.setupNoteList()
            }

            override fun setUnselected(str: String) {
                viewModel.unselectedNote(str)
            }

            override fun isShowCheckBox(): Boolean {
                return viewModel.supportBarOn.value!!
            }

        }
        val adapter = NoteAdapter(requireActivity().application, noteAction)
        viewModel.notes.observe(requireActivity()) {
            adapter.submitList(it)
        }
        viewModel.supportBarOn.observe(this.viewLifecycleOwner) {
            if (it) {
                binding.delBtn.visibility = View.VISIBLE
                binding.cancelBtn.visibility = View.VISIBLE
                binding.checkbox.visibility = View.VISIBLE
            } else {
                binding.delBtn.visibility = View.GONE
                binding.cancelBtn.visibility = View.GONE
                binding.checkbox.visibility = View.GONE
            }
        }
        viewModel.selectedListNotes.observe(this.viewLifecycleOwner) {
            binding.delBtn.text = requireActivity().getString(R.string.delete_item, it.size)
        }
        binding.noteView.adapter = adapter

        with(binding) {
            floatBtn.setOnClickListener {
                navController.navigate(HomeFragmentDirections.editOrAdd("ADD", null))
            }
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {

                }
            }
            cancelBtn.setOnClickListener {
                adapter.adapterHelper.hideCheckBox()
            }
            noteView.setOnScrollChangeListener { view, _, nsy, _, osy ->
                val y = nsy - osy
                if (y > 0) {
                    //scrolling view
                    floatBtn.animate().alpha(0.0f).translationX(28f).duration = 500
                } else if(view.scrollY<=20) floatBtn.animate().alpha(1.0f).translationX(-20f).duration = 500

            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun deleteSelectedNote(): Boolean {
        viewModel.selectedListNotes.value?.forEach {
            viewModel.deleteNoteItemById(it)
        }
        return true
    }
}

interface NoteItemAction {
    fun setSelected(str: String)
    fun openEditor(note: Note)
    fun startSelected()
    fun setUnselected(str: String)
    fun isShowCheckBox(): Boolean
}