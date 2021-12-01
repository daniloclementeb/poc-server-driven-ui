package com.danilo.poc.ui.login

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.danilo.poc.BuildConfig
import com.danilo.poc.R
import com.danilo.poc.data.ui.home.HomeActivity
import com.danilo.poc.data.ui.home.ui.ads.BannerViewModel
import com.danilo.poc.data.ui.home.HomeV1Activity
import com.danilo.poc.data.ui.home.ui.ads.BannerViewModelFactory
import com.danilo.poc.data.ui.home.ui.home.HomeActivityViewModel
import com.danilo.poc.data.ui.home.ui.home.HomeViewModelFactory
import com.google.android.gms.ads.*
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*


class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var bannerViewModel: BannerViewModel

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bannerViewModel =
            ViewModelProvider(this, BannerViewModelFactory(context = this)).get(BannerViewModel::class.java)

        var adContainerView = findViewById<AdView>(R.id.adviewLogin)
        var adView = AdView(this)

        adContainerView.addView(adView)
        bannerViewModel.loadBanner(adView, "ca-app-pub-9333521400694042/6831621481")

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        threatLocationPermission()

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(this.applicationContext, loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(this.applicationContext, loginResult.success)
                var fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        val geocoder = Geocoder(
                            this, Locale
                                .getDefault()
                        )
                        var addresses = geocoder.getFromLocation(location?.latitude ?:-23.533773, location?.longitude ?:-46.625290, 1)
                        var city = addresses.get(0).adminArea
                        if (BuildConfig.DEBUG) {
                            val home = Intent(this, HomeActivity::class.java).apply {
                                putExtra(
                                    "login",
                                    loginResult.success
                                ).putExtra("city", city)
                            }
                            startActivity(home)
                        } else {
                            val home = Intent(this, HomeV1Activity::class.java).apply {
                                putExtra(
                                    "login",
                                    loginResult.success
                                ).putExtra("city", city)
                            }
                            startActivity(home)
                        }
                    }

            }
            //setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
//            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        launch(Dispatchers.Main) {
                            loginViewModel.login(
                                username = username.text.toString(),
                                password = password.text.toString()
                            )
                        }
                    }
                }
                false
            }

            login.setOnClickListener {
                launch(Dispatchers.Main) {
                    loading.visibility = View.VISIBLE

                    loginViewModel.login(username.text.toString(), password.text.toString())
                }

            }
        }
    }

    private fun threatLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) ===
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                    finish()
                }
                return
            }
        }
    }



    private fun updateUiWithUser(context: Context, model: LoggedInUserView) {
        val welcome = R.string.welcome
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            context,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(context: Context, @StringRes errorString: Int) {
        Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}