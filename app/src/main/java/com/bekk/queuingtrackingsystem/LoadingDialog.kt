package com.bekk.queuingtrackingsystem

import android.app.Activity
import android.app.Dialog
import kotlinx.android.synthetic.main.dialog_loading_screen.*

class LoadingDialog (val context : Activity) {

    private lateinit var loadingDialogBox : Dialog

    fun start(){
        loadingDialogBox = Dialog(context, R.style.Theme_Dialog)
        loadingDialogBox.setCancelable(false)
        loadingDialogBox.setContentView(R.layout.dialog_loading_screen)
        loadingDialogBox.show()
    }

    fun stop(){
        loadingDialogBox.dismiss()
    }

}