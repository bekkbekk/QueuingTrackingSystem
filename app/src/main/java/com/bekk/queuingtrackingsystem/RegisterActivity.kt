package com.bekk.queuingtrackingsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var fAuth : FirebaseAuth
    private lateinit var fDbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // initialization
        fAuth = FirebaseAuth.getInstance()
        fDbRef = FirebaseDatabase.getInstance().reference

        btnRegister.setOnClickListener {

            val email = etEmailAdd.text.toString().trim()
            val password = etPasswordPhone.text.toString().trim()

            signUp(email, password)



        }

        if (fAuth.currentUser != null){

            val i = Intent(this, HomeActivity::class.java)
            finish()
            startActivity(i)

        }

    }

    private fun signUp(email: String, password: String) {

        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->

            if (task.isSuccessful){
                addUserToDatabase(email, password, fAuth.currentUser?.uid)
                val i = Intent(this, HomeActivity::class.java)
                startActivity(i)
            } else {
                Toast.makeText(this, "Error. ${task.exception!!.message}", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun addUserToDatabase(email: String, password: String, uid : String?) {

        fDbRef.child("users").child(fAuth.currentUser!!.uid).push().setValue(User(email, password, uid))

    }
}