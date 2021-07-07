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
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.danilo.poc.R
import com.danilo.poc.data.ui.home.HomeActivity
import com.google.android.gms.ads.*
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*


class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var loginViewModel: LoginViewModel

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*AppLovinSdk.getInstance( this ).setMediationProvider( "max" )
        AppLovinSdk.getInstance( this ).initializeSdk({ configuration: AppLovinSdkConfiguration ->
            // AppLovin SDK is initialized, start loading ads
        })*/


        MobileAds.initialize(
            this
        ) { }

        var adContainerView = findViewById<AdView>(R.id.adviewLogin)
        var adView = AdView(this)

        adContainerView.addView(adView)
        loadBanner(adView)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)
        //username.setText("32488498866")
        //password.setText("Teste13579!")
        //login.isEnabled = true

        //request permission
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
                        val home = Intent(this, HomeActivity::class.java).apply {
                            putExtra(
                                "login",
                                loginResult.success
                            ).putExtra("city", city)
                        }
                        startActivity(home)
                    }

            }
            //setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
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

    @SuppressLint("ResourceAsColor")
    private fun loadBanner(adView: AdView) {
        /*var adView = MaxAdView("d4e19143602867b0", this)
        adView!!.setListener(this)

        // Stretch to the width of the screen for banners to be fully functional
        val width = resources.getDimensionPixelSize(R.dimen.banner_width)

        // Banner height on phones and tablets is 50 and 90, respectively
        val heightPx = resources.getDimensionPixelSize(R.dimen.banner_height)

        adView!!.layoutParams = FrameLayout.LayoutParams(width, heightPx)

        // Set background or background color for banners to be fully functional
        adView!!.setBackgroundColor(R.color.material_on_background_emphasis_high_type)

        val rootView = findViewById<ViewGroup>(android.R.id.content)
        rootView.addView(adView)

        // Load the ad
        adView!!.loadAd()*/

        val adSize = AdSize(adView.width, adView.height)

        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-9333521400694042/6831621481"
        /*MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("47A8C4EFF0CD078CECA5EB60B4E963AB"))
                .build()
        )*/

        val adRequest: AdRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.adListener = (object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                // Gets the domain from which the error came.
                val errorDomain = error.domain
                // Gets the error code. See
                // https://developers.google.com/android/reference/com/google/android/gms/ads/AdRequest#constant-summary
                // for a list of possible codes.
                val errorCode = error.code
                // Gets an error message.
                // For example "Account not approved yet". See
                // https://support.google.com/admob/answer/9905175 for explanations of
                // common errors.
                val errorMessage = error.message
                // Gets additional response information about the request. See
                // https://developers.google.com/admob/android/response-info for more
                // information.
                val responseInfo = error.responseInfo
                // Gets the cause of the error, if available.
                val cause = error.cause
                // All of this information is available via the error's toString() method.
                Log.d("Ads", error.toString())
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        })
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