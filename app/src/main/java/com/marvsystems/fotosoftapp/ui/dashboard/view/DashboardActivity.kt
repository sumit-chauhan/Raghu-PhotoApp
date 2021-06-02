package com.marvsystems.fotosoftapp.ui.dashboard.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.data.api.ApiHelper
import com.marvsystems.fotosoftapp.data.api.ApiServiceImpl
import com.marvsystems.fotosoftapp.data.database.Lab
import com.marvsystems.fotosoftapp.data.model.LoginModel
import com.marvsystems.fotosoftapp.ui.base.BaseActivity
import com.marvsystems.fotosoftapp.ui.base.ViewModelFactory
import com.marvsystems.fotosoftapp.ui.dashboard.viewmodel.DashboardViewModel
import com.marvsystems.fotosoftapp.ui.main.view.MainActivity
import kotlinx.android.synthetic.main.nav_header_main.view.*


class DashboardActivity : BaseActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var navController: NavController;
    private var data: LoginModel? = null
    private var lab: Lab? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        initImage(toolbar.network_type)
        data = intent.getSerializableExtra("DATA") as LoginModel?
        lab = intent.getSerializableExtra("LAB") as Lab?

        setupViewModel()
        dashboardViewModel.user = data
        dashboardViewModel.lab = lab

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.dashboard_nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_profile, R.id.nav_my_orders, R.id.nav_about_us
            ), drawerLayout
        )

        val logoutItem = navView.menu.findItem(R.id.nav_logout)
        logoutItem.setOnMenuItemClickListener {
            drawerLayout.close()
            logoutDialog()
        }

        val header = navView.getHeaderView(0)
        header.tv_username.text = data?.username


        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.dashboard_nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    public fun logoutDialog(): Boolean {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Logout?")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, p1 ->
                dialog?.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .setNegativeButton(
                "No"
            ) { dialog, p1 -> dialog?.dismiss() }.create()
        alertDialog.show()
        return true
    }

    private fun setupViewModel() {
        dashboardViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()), getDatabase())
        ).get(DashboardViewModel::class.java)
    }

/*    private override fun getDatabase(): AppDatabase {
        return Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, DB_NAME
        ).build()
    }*/

    override fun onBackPressed() {
        if (getCurrentVisibleFragment() != null) {
            if (dashboardViewModel.recentOrder != null) {
                val alertDialog = AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit? In-progress upload will be stopped.")
                    .setPositiveButton("Yes") { dialog, p1 ->
                        dialog?.dismiss()
                        startActivity(Intent(this, MainActivity::class.java))
                        finishAffinity()
                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog, p1 -> dialog?.dismiss() }.create()
                alertDialog.show()
            } else {
                val alertDialog = AlertDialog.Builder(this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes") { dialog, p1 ->
                        dialog?.dismiss()
                        startActivity(Intent(this, MainActivity::class.java))
                        finishAffinity()
                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog, p1 -> dialog?.dismiss() }.create()
                alertDialog.show()
            }


//            val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
//            alertBuilder.setTitle("Exit?")
//            alertBuilder.setMessage("Are you sure want to exit, In Progress upload will cancel?")
//            alertBuilder.setPositiveButton("Yes") { dialog, which ->
//                dialog.dismiss()
//                dashboardViewModel.cancelRequest()
//                super.onBackPressed()
//
//            }
//            alertBuilder.setNegativeButton("No") { dialog, which ->
//                dialog.dismiss()
//
//            }
//            alertBuilder.create().show()
        } else {
            navController.navigate(R.id.nav_home);

//            navController.currentDestination;
        }
    }

    private fun getCurrentVisibleFragment(): HomeFragment? {
        val navHostFragment = supportFragmentManager.primaryNavigationFragment as NavHostFragment?
        val fragmentManager: FragmentManager = navHostFragment!!.childFragmentManager
        val homeFragment: Fragment? = fragmentManager.getPrimaryNavigationFragment()
        return if (homeFragment is HomeFragment) {
            homeFragment
        } else null
    }

    /*  override fun onResume() {
          CommonFunctions().updateNetworkImage(
              this, network_type
          );
          super.onResume()
      }*/
}