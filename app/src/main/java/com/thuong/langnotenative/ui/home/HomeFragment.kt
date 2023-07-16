package com.thuong.langnotenative.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.thuong.langnotenative.R
import com.thuong.langnotenative.databinding.FragmentHomeBinding
import com.thuong.langnotenative.db.AppDB
import com.thuong.langnotenative.db.Note

class HomeFragment() : Fragment() {
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
        val noteAction = object : ItemAction {
            override fun setSelected(str: Any) {
                viewModel.checkItem(str)
            }

            override fun openEditor(note: Note) {
                navController.navigate(HomeFragmentDirections.editOrAdd("EDIT", note))
            }

            override fun startSelected() {
                viewModel.setupNoteList()
                viewModel.showAllCheckbox()
            }

            override fun setUnselected(str: Any) {
                viewModel.unCheckItem(str)
            }
        }
        val adapter = NoteAdapter(requireActivity().application, noteAction)

        viewModel.noteItems.observe(requireActivity()) {
            adapter.submitList(it)
        }
        viewModel.supportBarOn.observe(this.viewLifecycleOwner) {
            with(binding) {
                if (it) {
                    delBtn.visibility = View.VISIBLE
                    cancelBtn.visibility = View.VISIBLE
                    checkbox.visibility = View.VISIBLE
                    delBtn.animate().translationX(40f).alpha(1f).duration = 100
                    cancelBtn.animate().translationX(40f).alpha(1f).duration = 100
                    checkbox.animate().translationX(40f).alpha(1f).duration = 100

                } else {
                    delBtn.animate().translationX(-40f).alpha(0f).withEndAction {
                        delBtn.visibility = View.GONE
                        cancelBtn.visibility = View.GONE
                        checkbox.visibility = View.GONE
                    }.duration = 100
                    cancelBtn.animate().translationX(-40f).alpha(0f).duration = 100
                    checkbox.animate().translationX(-40f).alpha(0f).duration = 100

                }
            }
        }
        viewModel.selectedListNotes.observe(this.viewLifecycleOwner) {
            if (it != null) binding.delBtn.text =
                requireActivity().getString(R.string.delete_item, it)
            else binding.delBtn.text = requireActivity().getString(R.string.delete_item, 0)
        }
        binding.noteView.adapter = adapter

        with(binding) {
            floatBtn.setOnClickListener {
                navController.navigate(HomeFragmentDirections.editOrAdd("ADD", null))
            }
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked)
                    viewModel.checkAllNote()
                else viewModel.unCheckAllNote()
            }
            cancelBtn.setOnClickListener {
                viewModel.unCheckAllNote()
                checkbox.isChecked = false
                viewModel.hideAllCheckbox()
            }
            noteView.setOnScrollChangeListener { view, _, nsy, _, osy ->
                val y = nsy - osy
                if (y > 0) {
                    //scrolling view
                    floatBtn.animate().alpha(0.0f).translationX(28f)
                        .withEndAction { floatBtn.visibility = View.GONE }.duration = 200

                } else if (view.scrollY <= 20) {
                    floatBtn.animate().alpha(1.0f)
                        .translationX(-10f).duration = 200
                    floatBtn.visibility = View.VISIBLE
                }
            }
            delBtn.setOnClickListener {
                viewModel.deleteSelectedNoteItem()
                viewModel.initNoteItems()
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private fun deleteSelectedNote(): Boolean {
//
//        return true
//    }
}


interface ItemAction {
    fun setSelected(str: Any)
    fun openEditor(note: Note)
    fun startSelected()
    fun setUnselected(str: Any)
}