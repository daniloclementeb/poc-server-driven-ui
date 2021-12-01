package com.danilo.poc.data.ui.home

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.danilo.poc.R
import com.danilo.poc.data.ui.home.ui.dashboard.DashboardFragment
import com.danilo.poc.data.ui.home.ui.home.HomeActivityViewModel
import com.danilo.poc.data.ui.home.ui.home.HomeFragment
import com.danilo.poc.data.ui.home.ui.home.HomeViewModel
import com.danilo.poc.data.ui.home.ui.home.HomeViewModelFactory
import com.danilo.poc.ui.login.LoggedInUserView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var homeActivityViewModel: HomeActivityViewModel
    private lateinit var username: Map<String, String>
    private lateinit var dados: Map<String, Object>
    private lateinit var homeFragment: HomeFragment
    private lateinit var dashboardFragment: DashboardFragment
    val fm: FragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        homeActivityViewModel = ViewModelProvider(this, HomeViewModelFactory())
            .get(HomeActivityViewModel::class.java)

        //pega dados que vieram de parametro do cliente
        var login = /*"123"*/intent.getSerializableExtra("login") as LoggedInUserView
        var city = /*"123"*/intent.getSerializableExtra("city") as String
        var customer = HashMap<String, String>()
        customer.put("token", login.token)
        customer.put("city", city)
        username = customer as Map<String, String>

        //inicializa observer
        launch(Dispatchers.Unconfined) {
            homeActivityViewModel.subscribeTopic(customer, applicationContext)
        }
        homeActivityViewModel.topic.observeForever({
            System.out.println("ObserverForever tabbar")
            if (it.isSuccess) {
                dados = it.getOrNull()!!
                if (dados.get("bar") != null) {
                    //trata tabbar
                    if((dados.get("bar") as Map<String, Object>)?.get("updated") != null)
                        when ((dados.get("bar") as Map<String, Object>)?.get("updated") as String) {
                            "true" -> {
                                initTabbarUI(dados.get("bar") as Map<String, Object>)
                                loadItemMenu(menuItemId(dados.get("bar") as Map<String, Object>))
                                (dados.get("bar") as HashMap<String, Object>).remove("updated")
                            }
                            else -> {
                                //do nothing
                            }
                        }
                }
            }
        })
    }

    private fun loadItemMenu(menuItemId: Int) {
        var bar = dados.get("bar") as Map<String, Object>
        val list = bar.get("list") as ArrayList<Map<String, Object>>
        list.forEach {
            (it as HashMap<String, Object>).remove("checked")
        }
        when (list[menuItemId].get("page-style") as String) {
            "menu" -> {
                val field = list[menuItemId].get("data-origin") as String?
                (list[menuItemId] as HashMap<String, Object>).put("checked", true as Object)
                val service = dados.get(field) as Map<String, Object>?
                homeActivityViewModel.topic.observeForever({
                    System.out.println("ObserverForever dashboard")
                    if (it.isSuccess) {
                        var dados = it.getOrNull()!!
                        if (dados.get(field) != null && (dados.get(field) as Map<String, Object>).get("updated")?.equals("true") == true) {
                            System.out.println("Atualizaaee")
                            dashboardFragment = DashboardFragment(service, username.get("city") as String, username.get("token") as String, field!!)
                            //fm.beginTransaction().apply {replace(R.id.nav_host_fragment, dashboardFragment).commit()}
                            (dados.get(field) as HashMap<String, Object>).remove("updated")
                        }
                    }
                })
                dashboardFragment = DashboardFragment(service, username.get("city") as String, username.get("token") as String, field!!)
                fm.beginTransaction().apply {replace(R.id.nav_host_fragment, dashboardFragment).commit()}
            }
            "timeline" -> {
                val field = list[menuItemId].get("data-origin") as String?
                (list[menuItemId] as HashMap<String, Object>).put("checked", true as Object)
                homeFragment = HomeFragment(this, field)
                fm.beginTransaction().apply{replace(R.id.nav_host_fragment, homeFragment).commit()}
            }
            else ->  System.out.println("XABUGO")
        }
    }

    private fun menuItemId(map: Map<String, Object>): Int {
        val list = map.get("list") as ArrayList<Map<String, Object>>
        var indice = 0
        list.forEachIndexed { index, it ->
            var map: HashMap<String, String> = it as HashMap<String, String>
            when (map.get("checked") != null && map.get("checked") as Boolean) {
                true -> indice = index
            }
        }
        return indice
    }

    private fun initTabbarUI(bar: Map<String, Object>) {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        when (bar.get("clear")?.equals("true")) {
            true -> {
                navView.menu.clear()
                (bar as HashMap<String, Object>).remove("clear")
            }
        }
        val list = bar.get("list") as ArrayList<Map<String, Object>>
        this.run {
            list.forEachIndexed { index, it ->
                var map: HashMap<String, String> = it as HashMap<String, String>
                if (map.get("updated") != null)
                    when (map.get("updated") as String) {
                        "true" -> {
                            addMenuItem(navView.menu, map, index)
                            map.remove("updated")
                        }
                    }
            }
        }
        navView.setOnNavigationItemSelectedListener{
            loadItemMenu(it.itemId)
            true
        }
        this.invalidateOptionsMenu()
    }
    private fun addMenuItem(menu: Menu, map: java.util.HashMap<String, String>, index: Int) {
        var menuItem = menu.add(Menu.NONE, index, index, map.get("title").toString())
        if (map.get("type").toString().equals("fixed_icon")) {
            menuItem.setIcon(this.applicationContext.resources.getIdentifier(map.get("icon")
                    .toString(), "drawable", this.applicationContext.packageName))
        } else {
            //tratar icone dinamico
            Picasso.get().load(map.get("icon"))
                .into(object: com.squareup.picasso.Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        println("icon loaded $bitmap")
                        menuItem.icon = BitmapDrawable(resources, bitmap)
                    }
                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        println("Loading failed... ${e?.message}")
                    }
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        println("Loading your icon...")
                     }
                })
        }
        map.remove("updated")
    }
}


