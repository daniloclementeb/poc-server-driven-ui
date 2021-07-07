package com.danilo.poc.data.ui.home.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.danilo.poc.data.HomeDataSource
import com.danilo.poc.data.HomeRepository


/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class HomeViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeActivityViewModel::class.java)) {
            return HomeActivityViewModel(
                    homeRepository = HomeRepository(
                            dataSource = HomeDataSource()
                    )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}