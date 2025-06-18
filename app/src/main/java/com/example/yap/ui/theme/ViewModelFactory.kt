package com.example.yap.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T
    {
        if(modelClass.isAssignableFrom(CardViewModel::class.java))
        {
            @Suppress("Unchecked_Cast")
            return CardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}