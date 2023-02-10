package com.rchyn.unscramble.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rchyn.unscramble.data.INCREASE_SCORE
import com.rchyn.unscramble.data.MAX_OF_WORDS
import com.rchyn.unscramble.data.allWordsList

class GameViewModel : ViewModel() {
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    private val _currentScrambleWord = MutableLiveData<String>()
    val currentScrambleWord: LiveData<String>
        get() = _currentScrambleWord

    private val wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    init {
        getNextWord()
    }

    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord
            .toCharArray()
        tempWord.shuffle()

        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }

        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambleWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordsList.add(currentWord)
        }
    }

    fun isUserWordCorrect(word: String): Boolean {
        if (word.equals(currentWord, false)) {
            increaseScore()
            return true
        }
        return false
    }

    private fun increaseScore() {
        _score.value = (_score.value)?.plus(INCREASE_SCORE)
    }

    fun nextWord(): Boolean {
        return if ((_currentWordCount.value ?: 0) < MAX_OF_WORDS) {
            getNextWord()
            true
        } else {
            false
        }
    }

    fun reinitializeUnscramble() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }
}