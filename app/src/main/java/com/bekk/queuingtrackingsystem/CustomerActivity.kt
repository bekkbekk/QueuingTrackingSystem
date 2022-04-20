package com.bekk.queuingtrackingsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html

class CustomerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        val code = intent.getStringExtra("code")
        supportActionBar?.title = Html.fromHtml("<font color=\"0xffffff\">$code</font>")

    }
}