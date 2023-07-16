package com.thuong.langnotenative.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.thuong.langnotenative.R
import com.thuong.langnotenative.databinding.FragmentDashboardBinding
import com.thuong.langnotenative.db.AppDB
import com.thuong.langnotenative.db.Word

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WordAdapter
    private lateinit var viewModel: WordViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(
            requireActivity(),
            WordViewModelFactory(
                AppDB.getDatabase(requireContext()).appDao
            )
        )[WordViewModel::class.java]
        val itemAction = object : ItemWordAction {
            override fun setSelected(str: Any) {
                viewModel.checkItem(str)
            }

            override fun openEditor(word: Word) {
                findNavController().navigate(DashboardFragmentDirections.editOrAdd(word, "EDIT"))
            }

            override fun startSelected() {
                viewModel.setupWordList()
                viewModel.showAllCheckbox()
            }

            override fun setUnselected(str: Any) {
                viewModel.unCheckItem(str)
            }

        }
        adapter = WordAdapter(requireContext(), itemAction)
        viewModel.wordItems.observe(requireActivity()) {
            adapter.submitList(it)
        }

        with(binding) {
            viewModel.selectedListWords.observe(viewLifecycleOwner) {
                if (it != null) delBtn.text =
                    requireActivity().getString(R.string.delete_item, it)
                else delBtn.text = requireActivity().getString(R.string.delete_item, 0)
            }
            wordView.adapter = adapter
            floatBtn.setOnClickListener {
                findNavController().navigate(DashboardFragmentDirections.editOrAdd(null, "ADD"))
            }
            viewModel.supportBarOn.observe(viewLifecycleOwner) {
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
            val listId = mutableListOf<Long>()
            for (i in 0..adapter.itemCount) {
                listId.add(adapter.getItemId(i))
            }
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.checkAllWord()
                }
                else viewModel.unCheckAllWord()
            }
            cancelBtn.setOnClickListener {
                viewModel.unCheckAllWord()
                checkbox.isChecked = false
                viewModel.hideAllCheckbox()
            }
            wordView.setOnScrollChangeListener { view, _, nsy, _, osy ->
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
                viewModel.deleteSelectedWordItem()
                viewModel.initWordItems()
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface ItemWordAction {
    fun setSelected(str: Any)

    fun openEditor(word: Word)

    fun startSelected()

    fun setUnselected(str: Any)

}