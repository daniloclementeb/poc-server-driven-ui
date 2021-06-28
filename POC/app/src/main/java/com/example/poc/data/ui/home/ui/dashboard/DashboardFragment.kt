package com.example.poc.data.ui.home.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.poc.R
import com.example.poc.data.model.MenuRecyclerAdapter

class DashboardFragment(private var field: Map<String, Object>?) : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var adapter: RecyclerView.Adapter<MenuRecyclerAdapter.ViewHolder>? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView: TextView = root.findViewById(R.id.textView)
        if (field != null) {
            val title = field!!.get("title") as String
            if (title != null)
                textView.text = title
        }
        /*dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        return root
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            if (field != null)
                adapter = MenuRecyclerAdapter(field!!.get("list") as ArrayList<Map<String, Object>>)
        }
    }
}