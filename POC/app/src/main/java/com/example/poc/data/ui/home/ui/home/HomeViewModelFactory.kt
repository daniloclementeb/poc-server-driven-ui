package com.example.poc.data.ui.home.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.poc.data.HomeDataSource
import com.example.poc.data.HomeRepository
import com.example.poc.data.LoginDataSource
import com.example.poc.data.LoginRepository
import com.example.poc.data.ui.home.HomeActivity

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