package com.marvsystems.fotosoftapp.ui.base

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.data.database.AppDatabase
import com.marvsystems.fotosoftapp.utils.AppSharedPreferences
import com.marvsystems.fotosoftapp.utils.ConnectivityReceiver
import com.marvsystems.fotosoftapp.utils.Constants.DB_NAME
import kotlinx.android.synthetic.main.app_bar.view.*


/**
 * @author Anshul Gour on 19,June,2019
Description : -
 */
open class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    private val REQUEST_CODE_ASK_PERMISSIONS = 1002
    private var dialog: Dialog? = null
    public var appSharedPreferences: AppSharedPreferences? = null
    private lateinit var network_type: ImageView;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialog = Dialog(this)
        dialog?.setContentView(R.layout.layout_progress)
        dialog?.setCanceledOnTouchOutside(false)
        appSharedPreferences = AppSharedPreferences.getInstance(this)


        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    public fun initImage(imageView: ImageView) {
        network_type = imageView;
    }

    /**
     * Method to show progress dialog
     */
    protected fun showProgress() {
        dialog?.show()
    }

    /**
     * Method to dismiss progress dialog
     */
    protected fun dismissProgress() {
        dialog?.dismiss()
    }

    /**
     * Method to show Toast
     * @param message Message to display on toast
     */
    protected fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Method to show Back arrow on toolbar
     *
     */
    protected fun showBackOnToolbar() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    /**
     * Method to set Linear layout as LayoutManager on recyclerview
     * @param recyclerView RecyclerView
     */
    protected fun setLinearLayoutManager(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }


    /**
     * Method to set Grid layout as LayoutManager on recyclerview
     * @param recyclerView RecyclerView
     * @param count No of items in GridView
     */
    protected fun setGridLayoutManager(recyclerView: RecyclerView, count: Int) {
        recyclerView.layoutManager = GridLayoutManager(this, count)
    }

    /**
     * Method to start Activity
     * @param context Context of Component
     * @param cls Destination Class
     */
    protected fun startActivity(context: Context, cls: Class<*>) {
        startActivity(Intent(context, cls))
    }

    /**
     * Method to handle back button
     */

    /**
     * Method to clear errors on text Change
     * @param textInputEditText
     * @param textInputLayout
     */
    fun clearError(textInputLayout: TextInputLayout, textInputEditText: TextInputEditText) {
        textInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayout.error = null
            }

        })
    }

    /**
     * Method to check Internet connection
     */
    fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val n = cm.activeNetwork
        if (n != null) {
            val nc = cm.getNetworkCapabilities(n)
            return nc?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)!! || nc.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            )
        }
        return false
    }


    fun String.decode(): String {
        return Base64.decode(this, Base64.DEFAULT).toString(charset("UTF-8"))
    }

    fun String.encode(): String {
        return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.DEFAULT)
    }

    protected fun getDatabase(): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, DB_NAME
        ).build()
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this

        handlingNetwork()
    }

    fun handlingNetwork() {
        try {
            val toolbar: Toolbar = findViewById(R.id.toolbar)
            initImage(toolbar.network_type)
            if (isOnline(this))
                setNetworkMode();
            else {
                network_type.setImageResource(R.drawable.network_wifi_off);
                network_type.setColorFilter(
                    ContextCompat.getColor(this, R.color.orange_red),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            }
        } catch (exception: Exception) {
            exception.printStackTrace();
        }
    }

    fun setNetworkMode() {
        try {
            if (network_type != null) {
                var mode = appSharedPreferences?.getString("ConnectionMode");
                if (mode == "Cellular") {
                    appSharedPreferences?.setString("ConnectionType", getNetworkClass(this));
                    updateNetworkImage(network_type)
                } else {
                    network_type.setImageResource(R.drawable.network_wifi_on);
                    network_type.setColorFilter(
                        ContextCompat.getColor(this, R.color.green),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    );
                }
            }

        } catch (exception: Exception) {
            exception.printStackTrace();
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        //            Uri personPhoto = acct.getPhotoUrl();
        if (isConnected) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val res = checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                if (res != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_PHONE_STATE),
                        REQUEST_CODE_ASK_PERMISSIONS
                    )
                } else {
                    handlingNetwork()
//                    appSharedPreferences?.setString("ConnectionType", getNetworkClass(this));
                }
            }
//            updateNetworkImage(appSharedPreferences?.getString("ConnectionType"), nwImg);
        } else {
            if (network_type != null) {
                network_type.setImageResource(R.drawable.network_wifi_off);
                network_type.setColorFilter(
                    ContextCompat.getColor(this, R.color.orange_red),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            }
        }
    }


    open fun getNetworkClass(context: Context): String? {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) else {
            val mTelephonyManager =
                context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            return when (mTelephonyManager.networkType) {
                TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> "2G"
                TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"
                TelephonyManager.NETWORK_TYPE_LTE -> "4G"
                else -> "Unknown"
            }
        }
        return "Normal";
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_PHONE_STATE
                        ) ===
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        appSharedPreferences?.setString("ConnectionType", getNetworkClass(this));
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    appSharedPreferences?.setString("ConnectionMode", "Cellular");
//                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    appSharedPreferences?.setString("ConnectionMode", "Wifi");
//                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    appSharedPreferences?.setString("ConnectionMode", "Ethernet");
//                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun updateNetworkImage(image: ImageView) {
        var type = appSharedPreferences?.getString("ConnectionType");
        if (image != null && type != null) {
            if (type == "2G") {
                image.setImageResource(R.drawable.network_2g);
                image.setColorFilter(
                    ContextCompat.getColor(this, R.color.yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            } else if (type == "3G") {
                image.setImageResource(R.drawable.network_3g);
                image.setColorFilter(
                    ContextCompat.getColor(this, R.color.green),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            } else if (type == "4G") {
                image.setImageResource(R.drawable.network_4g);
                image.setColorFilter(
                    ContextCompat.getColor(this, R.color.green),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            } else if (type == "LTE") {
                image.setImageResource(R.drawable.network_lte);
                image.setColorFilter(
                    ContextCompat.getColor(this, R.color.green_dark),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            } else { // TODO check
                image.setImageResource(R.drawable.network_h);
                image.setColorFilter(
                    ContextCompat.getColor(this, R.color.gray),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
            }
        }
    }
}