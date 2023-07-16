package com.thuong.langnotenative.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.thuong.langnotenative.db.AppDao
import com.thuong.langnotenative.db.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val db: AppDao) : ViewModel() {
    private val _noteItems = MutableLiveData<List<NoteItem>>()
    val noteItems: LiveData<List<NoteItem>> get() = _noteItems

    val selectedViewId = MutableLiveData("")

    val selectedListNotes = MutableLiveData<Int>().apply {
        this.value = 0
    }

    private val _supportBarOn = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val supportBarOn: LiveData<Boolean>
        get() = _supportBarOn

    init {
        initNoteItems()
    }

    private fun countSelectedNoteItem() {
        var count = 0
        _noteItems.value?.forEach {
            if (it.isSelected) count++
        }
        selectedListNotes.value = count
    }

    fun  unCheckItem(item: Any) {
        if (item is NoteItem) {
            _noteItems.value?.forEach {
                if (item.note.uid == it.note.uid) {
                    it.isSelected = false
                }
            }
        }
        countSelectedNoteItem()
    }

    fun checkAllNote() {
        _noteItems.value?.forEach {
            it.isSelected = true
        }
        countSelectedNoteItem()
    }

    fun unCheckAllNote() {
        _noteItems.value?.forEach {
            it.isSelected = false
        }
        countSelectedNoteItem()
    }

    fun hideAllCheckbox() {
        _noteItems.value?.forEach {
            it.isShowBox = false
        }
        _supportBarOn.value = false
    }

    fun showAllCheckbox() {
        _noteItems.value?.forEach {
            it.isShowBox = true
        }
        _supportBarOn.value = true
    }

    fun initNoteItems() {
        val listNoteItems = mutableListOf<NoteItem>()
        viewModelScope.launch {
            getNotes().forEach {
                listNoteItems.add(NoteItem(it))
            }
            _noteItems.value = listNoteItems
        }
    }

    fun setupNoteList() {
        _supportBarOn.value = true
    }

    fun checkItem(str: Any) {
        if (str is NoteItem) {
            _noteItems.value?.forEach {
                if (str.note.uid == it.note.uid) {
                    it.isSelected = true
                }
            }
        }
        countSelectedNoteItem()
    }


    fun addNoteItem(note: Note) {
        viewModelScope.launch {
            addNote(note)
        }
    }

    fun updateNoteItem(note: Note) {
        viewModelScope.launch {
            updateNote(note)
        }
    }

    fun deleteSelectedNoteItem() {
        _noteItems.value?.forEach {
            if (it.isSelected) deleteNoteItem(it.note)
        }
    }

    private fun deleteNoteItem(note: Note) {
        viewModelScope.launch {
            deleteNote(note)
        }
        countSelectedNoteItem()
    }

//    fun deleteNoteItemById(str: String) {
//        viewModelScope.launch {
//            deleteNoteById(str)
//        }
//    }

    private suspend fun deleteNoteById(str: String) = withContext(Dispatchers.IO) {
        db.deleteNoteById(str)
    }

    private suspend fun updateNote(note: Note) = withContext(Dispatchers.IO) {
        db.updateNote(note)
    }

    private suspend fun getNotes(): List<Note> = withContext(Dispatchers.IO) {
        db.getAllNote()
    }

    private suspend fun addNote(note: Note) = withContext(Dispatchers.IO) {
        db.insertNote(note)
    }

    private suspend fun deleteNote(note: Note) = withContext(Dispatchers.IO) {
        db.deleteNote(note)
    }
}

class HomeViewModelFactory(
    private val dataSource: AppDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

