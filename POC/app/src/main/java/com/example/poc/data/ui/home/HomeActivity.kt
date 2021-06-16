package com.example.poc.data.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.poc.R
import com.example.poc.data.ui.home.ui.dashboard.DashboardFragment
import com.example.poc.data.ui.home.ui.home.HomeActivityViewModel
import com.example.poc.data.ui.home.ui.home.HomeFragment
import com.example.poc.data.ui.home.ui.home.HomeViewModel
import com.example.poc.data.ui.home.ui.home.HomeViewModelFactory
import com.example.poc.ui.login.LoggedInUserView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class HomeActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var homeActivityViewModel: HomeActivityViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var username: String
    private lateinit var map: Map<String, Object>
    private lateinit var homeFragment: HomeFragment
    private lateinit var dashboardFragment: DashboardFragment
    var menuId: MenuItem? = null

    val fm: FragmentManager = supportFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        var login = /*"123"*/intent.getSerializableExtra("login") as LoggedInUserView

        homeActivityViewModel = ViewModelProvider(this, HomeViewModelFactory())
            .get(HomeActivityViewModel::class.java)

        launch(Dispatchers.Unconfined) {
            homeActivityViewModel.subscribeHomeTopic(login.token, applicationContext)
        }
        homeActivityViewModel.navResult.observe(this@HomeActivity, Observer {
            if (it.isSuccess) {
                var mapa = it.getOrNull()
                if (mapa != null) {
                    map = mapa
                }
                if (!mapa.isNullOrEmpty()) {
                    System.out.println(mapa.get("message"))
                    //trata tabbar
                    if (mapa.get("bar") != null) {
                        var tabbar = mapa.get("bar") as Map<String, Object>
                        initTabbarUI(tabbar)
                    }
                    //trata cards
                }
            }
        })

    }

     private fun initTabbarUI(bar: Map<String, Object>) {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        var menu = navView.menu
        if (bar.get("clear")?.equals("true") == true) {
            menu.clear()
            menuId = null
        }
        val list = bar.get("list") as ArrayList<Map<String, Object>>
        this.run { list.forEachIndexed { index, it ->
            System.out.println(index)
            if (it != null) {
                var map: Map<String, String> = it as Map<String, String>
                var menuItem = menu.add(Menu.NONE, index, index, map.get("title").toString())
                if (map.get("type").toString().equals("fixed_icon")) {
                    if (menuItem != null)
                        menuItem.setIcon(this.applicationContext.resources.getIdentifier(map.get("icon").toString(), "drawable", this.applicationContext.packageName))
                }

                if (map.get("checked") != null && map.get("checked") as Boolean) {
                    if (menuId == null) {
                        menuId = menuItem
                    }
                }

            }
        }}.also{
            if (menuId != null) {
                navView.selectedItemId = menuId!!.itemId as Int
                loadItemMenu(menuId!!)
            }
        }

         navView.setOnNavigationItemSelectedListener{
             loadItemMenu(it)
             true
         }
         this.invalidateOptionsMenu()
    }

    private fun loadItemMenu(it: MenuItem) {
        menuId = it
        if (map.get("bar") != null) {
            var tabbar = map.get("bar") as Map<String, Object>
            val list = tabbar.get("list") as ArrayList<Map<String, Object>>
            when (list[it.itemId].get("page-style") as String) {
                "menu" -> {
                    val field = list[it.itemId].get("data-origin") as String?
                    dashboardFragment = DashboardFragment(field)
                    fm.beginTransaction().apply {replace(R.id.nav_host_fragment, dashboardFragment).commit()}
                }
                "timeline" -> {
                    val field = list[it.itemId].get("data-origin") as String?
                    homeFragment = HomeFragment(this, field)
                    fm.beginTransaction().apply{replace(R.id.nav_host_fragment, homeFragment).commit()}
                }
                else ->  System.out.println("XABUGO")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            homeActivityViewModel.disconnect(username, applicationContext)
        } catch (e: Exception) {

        }
    }

}

