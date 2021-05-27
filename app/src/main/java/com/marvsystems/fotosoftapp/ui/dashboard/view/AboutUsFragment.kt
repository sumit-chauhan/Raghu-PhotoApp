package com.marvsystems.fotosoftapp.ui.dashboard.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.marvsystems.fotosoftapp.R
import com.marvsystems.fotosoftapp.ui.dashboard.viewmodel.DashboardViewModel
import kotlinx.android.synthetic.main.fragment_about_us.*


class AboutUsFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(requireActivity()).get(DashboardViewModel::class.java)
        return inflater.inflate(R.layout.fragment_about_us, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        web_view.loadUrl("https://marvsystems.com/about.html")
        web_view.settings.javaScriptEnabled = true
        web_view.webViewClient = WebViewClient()
    }

}