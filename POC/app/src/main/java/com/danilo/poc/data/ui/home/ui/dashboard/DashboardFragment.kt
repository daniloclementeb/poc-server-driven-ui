package com.danilo.poc.data.ui.home.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danilo.poc.R
import com.danilo.poc.data.model.MenuRecyclerAdapter


class DashboardFragment(private var field: Map<String, Object>?, var city:String, var topic:String) : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

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
            if (field != null){
                var list = ArrayList<Map<String, Object>>()
                if (field?.get("title") != null) {
                    var title = HashMap<String, Object>()
                    title.put("title", field?.get("title") as Object)
                    title.put("type", "title" as Object)
                    title.put("order", "-1000" as Object)
                    //title.put("image", null as Object)
                    list.add(title)
                } /*else if (field?.get("weather") != null) {
                    //só posso ter 1 weather e é logo abaixo do titulo
                    var weather = field?.get("weather") as HashMap<String, Object>
                    list.add(weather)
                }*/
                list.addAll(field!!.get("list") as ArrayList<Map<String, Object>>)
                list.sortBy {
                    ((it.get("order") as Object?).toString().toIntOrNull() ?: 0)
                }
                adapter = MenuRecyclerAdapter(list, context, city, topic)
            }
        }
    }
}




