package com.bekk.queuingtrackingsystem

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    val TAG = "HomeActivity"

    private lateinit var fDbRef: DatabaseReference
    private lateinit var fAuth : FirebaseAuth
    private lateinit var toCustomerActivity: Intent
    private lateinit var codeLists : ArrayList<Host>
    private lateinit var email : String
    private lateinit var loadingDialog : LoadingDialog
    private var activeRoom = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        this.supportActionBar?.hide()

        // initialization
        fDbRef = FirebaseDatabase.getInstance().reference
        fAuth = FirebaseAuth.getInstance()
        toCustomerActivity = Intent(this, CustomerActivity::class.java)
        codeLists = ArrayList()
        email = fAuth.currentUser?.email.toString()
        loadingDialog = LoadingDialog(this)

        loadingDialog.start()

        readGeneratedCodeFromFirebase()

        // check if the user created a room
        checkIfActive()


        btnCreate.setOnClickListener {
            if (activeRoom){
                val i = Intent(this, HostRoomActivity::class.java)
                // send code
                val code = getCodeFromLists()
                i.putExtra("code", code)
                startActivity(i)
            } else {
                val i = Intent(this, GenerateActivity::class.java)
                startActivity(i)
            }
        }

        btnJoin.setOnClickListener {

            showEnterCodeDialogBox()

        }

    }

    private fun getCodeFromLists(): String? {

        for (codes in codeLists){
            if (codes.user == email){
                return codes.code
            }
        }
        return ""

    }

    private fun checkIfActive() {
        fDbRef.child("active_hosts").child(fAuth.currentUser!!.uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children){
                    activeRoom = postSnapshot.getValue(String::class.java) != null
                }
                loadingDialog.stop()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun readGeneratedCodeFromFirebase() {
        fDbRef.child("generated_codes").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children){
                    val userAndCode = postSnapshot.getValue(Host::class.java)
                    codeLists.add(userAndCode!!)
                }
                for (x in codeLists){
                    Log.d(TAG, x.code + x.user)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun showEnterCodeDialogBox() {

        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(true)
        updateDialog.setContentView(R.layout.dialog_enter_code)

        val etEnterCode : EditText = updateDialog.findViewById(R.id.etEnterCode)
        val btnEnterRoom : Button = updateDialog.findViewById(R.id.btnEnterRoom)
        val btnScanCode : Button = updateDialog.findViewById(R.id.btnScanCode)

        btnEnterRoom.setOnClickListener {

            val code = etEnterCode.text.toString().trim()

            if (code.isEmpty()){
                etEnterCode.error = "Empty"
                etEnterCode.requestFocus()
                return@setOnClickListener
            }

            checkCodeToEnter(code)


        }

        btnScanCode.setOnClickListener {

            // scan qr code then returns string na masstore sa variable

            checkCodeToEnter("CODE_VARIABLE")

        }








        updateDialog.show()

    }

    private fun checkCodeToEnter(code : String) {

        val codeStrings : ArrayList<String> = ArrayList()

        if (codeStrings.contains(code)){
            toCustomerActivity.putExtra("code", code)
            startActivity(toCustomerActivity)
        } else {
            Toast.makeText(this, "No such room exists", Toast.LENGTH_SHORT).show()
        }
    }
}