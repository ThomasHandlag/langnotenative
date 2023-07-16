package com.thuong.langnotenative.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.thuong.langnotenative.R
import com.thuong.langnotenative.ui.home.HomeViewModel

class AddWordFragment : Fragment() {

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this.requireActivity())[HomeViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_word, container, false)
    }

    companion object {

    }
}