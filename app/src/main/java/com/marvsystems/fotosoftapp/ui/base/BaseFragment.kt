package com.marvsystems.fotosoftapp.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.marvsystems.fotosoftapp.R

open class BaseFragment : Fragment() {

    private var dialog: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = Dialog(requireActivity())
        dialog?.setContentView(R.layout.layout_progress)
        dialog?.setCanceledOnTouchOutside(false)
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
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }
}