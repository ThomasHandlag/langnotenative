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
    private val _notes = MutableLiveData<List<Note>>(listOf())
    val notes: LiveData<List<Note>>
        get() = _notes

    val _selectedViewId = MutableLiveData("")

    private val _selectedListNotes = MutableLiveData<List<String>>().apply {
        this.value = listOf()
    }
    val selectedListNotes: LiveData<List<String>>
        get() = _selectedListNotes

    private val _supportBarOn = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val supportBarOn: LiveData<Boolean>
        get() = _supportBarOn

    init {
        getAllNoteItems()
    }

    fun unselectedNote(str: String) {
        _selectedListNotes.value?.minus(str)
    }

    fun setupNoteList() {
        _selectedListNotes.value = listOf("")
        _supportBarOn.value = true
    }

    fun setSelected(str: String) {
        _selectedListNotes.value?.plus(str)
    }

    fun getAllNoteItems() {
        viewModelScope.launch {
            _notes.value = getNotes()
        }
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

    fun deleteNoteItem(note: Note) {
        viewModelScope.launch {
            deleteNote(note)
        }
    }

    fun deleteNoteItemById(str: String) {
        viewModelScope.launch {
            deleteNoteById(str)
        }
    }

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

