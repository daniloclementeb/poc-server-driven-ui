package com.example.poc.data.ui.home.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.poc.R
import com.example.poc.data.ui.home.HomeActivity

class HomeFragment(private var activity:HomeActivity, private var field: String?) : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var pubsub: HomeActivityViewModel

    private lateinit var linearLayoutManager: LinearLayoutManager


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home, container, false)


        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pubsub = ViewModelProvider(this, HomeViewModelFactory())
                .get(HomeActivityViewModel::class.java)

        pubsub.hash.observe(viewLifecycleOwner) {
            it.onSuccess {
                if (it.get(field) != null) {
                    Toast.makeText(context, "Chegou alteracao!", Toast.LENGTH_SHORT)
                }
            }
        }
    }
}