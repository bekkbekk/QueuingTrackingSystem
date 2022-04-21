package com.bekk.queuingtrackingsystem

import android.app.Activity
import android.app.Dialog
import kotlinx.android.synthetic.main.dialog_loading_screen.*
import kotlinx.android.synthetic.main.dialog_loading_screen_circle.*

class LoadingDialogHorizontal (val context : Activity) {

    private lateinit var loadingDialogBox : Dialog

    fun start(){
        loadingDialogBox = Dialog(context, R.style.Theme_Dialog)
        loadingDialogBox.setCancelable(false)
        loadingDialogBox.setContentView(R.layout.dialog_loading_screen)
        loadingDialogBox.show()
    }

    fun start(message : String){
        loadingDialogBox = Dialog(context, R.style.Theme_Dialog)
        loadingDialogBox.setCancelable(false)
        loadingDialogBox.setContentView(R.layout.dialog_loading_screen)
        loadingDialogBox.textView.text = message
        loadingDialogBox.show()
    }

    fun stop(){
        loadingDialogBox.dismiss()
    }

}