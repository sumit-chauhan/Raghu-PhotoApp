package com.marvsystems.fotosoftapp.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.marvsystems.fotosoftapp.BuildConfig
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*


object AppUtil {


    fun getSimpleDateFormat(dateTimeFormat: DateTimeFormat): SimpleDateFormat {
        return SimpleDateFormat(dateTimeFormat.toString(), Locale.getDefault())
    }


    fun getCurrentDateTime(dateTimeFormat: DateTimeFormat): String {
        val date = Date()
        Log.e("Date", getSimpleDateFormat(dateTimeFormat).format(date))
        return getSimpleDateFormat(dateTimeFormat).format(date)
    }

    fun getUId(): Int {
        val r = Random(System.currentTimeMillis())
        return (1 + r.nextInt(2)) * 10000 + r.nextInt(10000)
    }

    /**
     * Method to check is Date/Time set to Automatic
     */
    fun isTimeAutomatic(context: Context): Boolean {
        return Settings.Global.getInt(context.contentResolver, Settings.Global.AUTO_TIME, 0) == 1
    }

    /**
     * Method to show alert dialog
     */
    fun alertDialog(compatActivity: Activity, title: String, message: String) {
        val alertBuilder = AlertDialog.Builder(compatActivity)
        alertBuilder.setTitle(title)
        alertBuilder.setMessage(message)
        alertBuilder.setPositiveButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        alertBuilder.create().show()
    }

    fun convertBase64ToBitmap(b64: String): Bitmap? {
        val imageAsBytes: ByteArray = Base64.decode(b64.toByteArray(), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
    }

    private fun getExtension(filePath: String): String? {
        val strLength = filePath.lastIndexOf(".")
        return if (strLength > 0) filePath.substring(strLength + 1)
            .toLowerCase(Locale.getDefault()) else null
    }

    fun getUUIDFileName(filePath: String): String {
        val extension = getExtension(filePath)
        return UUID.randomUUID().toString() + "." + extension
    }

    fun getIPAddress(useIPv4: Boolean): String? {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr: String = addr.hostAddress
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(
                                    0,
                                    delim
                                ).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (ignored: Exception) {
        } // for now eat exceptions
        return ""
    }

    public fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }


    fun nthLastIndexOf(nth: Int, ch: String?, string: String): Int {
        var nth = nth
        return if (nth <= 0) string.length else nthLastIndexOf(
            --nth, ch, string.substring(
                0, string.lastIndexOf(
                    ch!!
                )
            )
        )
    }

    fun getOSName(): String {
        val fields = Build.VERSION_CODES::class.java.fields
        var codeName = "UNKNOWN"
        fields.filter { it.getInt(Build.VERSION_CODES::class) == Build.VERSION.SDK_INT }
            .forEach { codeName = it.name }
        return codeName
    }

    fun getOSVersion(): String {
        return Build.VERSION.RELEASE
    }

    fun getAppVersion(): String {
        return BuildConfig.VERSION_NAME
    }

    fun is64Bit(): Boolean {
        return Build.SUPPORTED_64_BIT_ABIS != null && Build.SUPPORTED_64_BIT_ABIS.isNotEmpty()
    }
}

