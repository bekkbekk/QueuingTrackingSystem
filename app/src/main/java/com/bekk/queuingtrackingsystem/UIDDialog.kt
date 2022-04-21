package com.bekk.queuingtrackingsystem

import android.app.Dialog
import android.content.Context
import kotlinx.android.synthetic.main.dialog_customer_uid.*

class UIDDialog (val context: Context) {

    fun show(uid : String){
        val loadingDialogBox = Dialog(context, R.style.Theme_Dialog)
        loadingDialogBox.setCancelable(true)
        loadingDialogBox.setContentView(R.layout.dialog_customer_uid)

        loadingDialogBox.tvUIDDialog.text = uid

        loadingDialogBox.show()
    }

}