package com.thuong.langnotenative.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.thuong.langnotenative.db.AppDao
import com.thuong.langnotenative.db.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordViewModel(private val db: AppDao): ViewModel() {
    private val _wordItems = MutableLiveData<List<WordItem>>()
    val wordItems: LiveData<List<WordItem>> get() = _wordItems

    val selectedViewId = MutableLiveData("")

    val selectedListWords = MutableLiveData<Int>().apply {
        this.value = 0
    }

    val typeSelected = MutableLiveData<Int>().apply {
        this.value = 0
    }

    private val _supportBarOn = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    val supportBarOn: LiveData<Boolean>
        get() = _supportBarOn

    init {
        initWordItems()
    }

    private fun countSelectedWordItem() {
        var count = 0
        _wordItems.value?.forEach {
            if (it.isSelected) count++
        }
        selectedListWords.value = count
    }

    fun unCheckItem(item: Any) {
        if (item is WordItem) {
            _wordItems.value?.forEach {
                if (item.word.uid == it.word.uid) {
                    it.isSelected = false
                }
            }
        }
        countSelectedWordItem()
    }

    fun checkAllWord() {
        _wordItems.value?.forEach {
            it.isSelected = true
        }
        countSelectedWordItem()
    }

    fun unCheckAllWord() {
        _wordItems.value?.forEach {
            it.isSelected = false
        }
        countSelectedWordItem()
    }

    fun hideAllCheckbox() {
        _wordItems.value?.forEach {
            it.isShowBox = false
        }
        _supportBarOn.value = false
    }

    fun showAllCheckbox() {
        _wordItems.value?.forEach {
            it.isShowBox = true
        }
        _supportBarOn.value = true
    }

    fun initWordItems() {
        val listWordItems = mutableListOf<WordItem>()
        viewModelScope.launch {
            getWords().forEach {
                listWordItems.add(WordItem(it))
            }
            _wordItems.value = listWordItems
        }
    }

    fun setupWordList() {
        _supportBarOn.value = true
    }

    fun checkItem(str: Any) {
        if (str is WordItem) {
            _wordItems.value?.forEach {
                if (str.word.uid == it.word.uid) {
                    it.isSelected = true
                }
            }
        }
        countSelectedWordItem()
    }


    fun addWordItem(word: Word) {
        viewModelScope.launch {
            addWord(word)
        }
    }

    fun updateWordItem(word: Word) {
        viewModelScope.launch {
            updateWord(word)
        }
    }

    fun deleteSelectedWordItem() {
        _wordItems.value?.forEach {
            if (it.isSelected) deleteWordItem(it.word)
        }
    }

    private fun deleteWordItem(word: Word) {
        viewModelScope.launch {
            deleteWord(word)
        }
        countSelectedWordItem()
    }

//    fun deleteWordItemById(str: String) {
//        viewModelScope.launch {
//            deleteWordById(str)
//        }
//    }

//    private suspend fun deleteWordById(str: String) = withContext(Dispatchers.IO) {
//        db.deleteWordById(str)
//    }

    private suspend fun updateWord(word: Word) = withContext(Dispatchers.IO) {
        db.updateWord(word)
    }

    private suspend fun getWords(): List<Word> = withContext(Dispatchers.IO) {
        db.getAllWord()
    }

    private suspend fun addWord(word: Word) = withContext(Dispatchers.IO) {
        db.insertWord(word)
    }

    private suspend fun deleteWord(word: Word) = withContext(Dispatchers.IO) {
        db.deleteWord(word)
    }
}

class WordViewModelFactory(
    private val dataSource: AppDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
            return WordViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
data class WordItem(val word: Word, var isSelected: Boolean = false, var isShowBox: Boolean = false)
