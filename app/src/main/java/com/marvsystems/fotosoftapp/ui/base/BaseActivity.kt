package com.marvsystems.fotosoftapp.ui.base

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.data.database.AppDatabase
import com.marvsystems.fotosoftapp.utils.AppSharedPreferences
import com.marvsystems.fotosoftapp.utils.Constants.DB_NAME


/**
 * @author Anshul Gour on 19,June,2019
Description : -
 */
open class BaseActivity : AppCompatActivity() {

    private var dialog: Dialog? = null
    var appSharedPreferences: AppSharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialog = Dialog(this)
        dialog?.setContentView(R.layout.layout_progress)
        dialog?.setCanceledOnTouchOutside(false)
        appSharedPreferences = AppSharedPreferences.getInstance(this)

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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

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
}