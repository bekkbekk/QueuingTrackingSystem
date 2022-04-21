package com.bekk.queuingtrackingsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CustomerActivity : AppCompatActivity() {

    private lateinit var fDbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private var code: String? = null
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        // initialization
        fDbRef = FirebaseDatabase.getInstance().reference
        fAuth = FirebaseAuth.getInstance()
        email = fAuth.currentUser?.email.toString()

        code = intent.getStringExtra("code")
        supportActionBar?.title = Html.fromHtml("<font color=\"0xffffff\">$code</font>")

        addUserToCustomerDatabase()

    }

    private fun addUserToCustomerDatabase() {
        fDbRef.child("active_rooms").child(code!!).child(System.currentTimeMillis().toString())
            .setValue(fAuth.currentUser?.uid)
    }

}