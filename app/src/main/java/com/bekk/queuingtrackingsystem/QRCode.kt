package com.bekk.queuingtrackingsystem

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_generate.*
import kotlinx.android.synthetic.main.dialog_qr_code.*

class QRCode (val context : Activity) {

    private lateinit var loadingDialog : Dialog
    private lateinit var bitmap : Bitmap

    fun generateImage(ivQrCode : ImageView, tvLabel : TextView, tvCode : TextView, code : String) {
        val writer = MultiFormatWriter()

        try {
            // generate qr code
            val matrix = writer.encode(code, BarcodeFormat.QR_CODE, 350, 350)
            val encoder = BarcodeEncoder()
            bitmap = encoder.createBitmap(matrix)
            ivQrCode.setImageBitmap(bitmap)

            // show code
            tvLabel.visibility = View.VISIBLE
            tvCode.text = code


        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    fun generateImage(ivQrCode : ImageView, code : String) {
        val writer = MultiFormatWriter()

        try {
            // generate qr code
            val matrix = writer.encode(code, BarcodeFormat.QR_CODE, 350, 350)
            val encoder = BarcodeEncoder()
            bitmap = encoder.createBitmap(matrix)
            ivQrCode.setImageBitmap(bitmap)

        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    fun show(code : String){
        loadingDialog = Dialog(context, R.style.Theme_Dialog)
        loadingDialog.setCancelable(true)
        loadingDialog.setContentView(R.layout.dialog_qr_code)

        val ivQrCodeDialog = loadingDialog.findViewById<ImageView>(R.id.ivQrCodeDialog)
        val tvCodeDialog = loadingDialog.findViewById<TextView>(R.id.tvCodeDialog)

        generateImage(ivQrCodeDialog, code)
        ivQrCodeDialog.setImageBitmap(bitmap)
        tvCodeDialog.text = code

        loadingDialog.show()
    }

}