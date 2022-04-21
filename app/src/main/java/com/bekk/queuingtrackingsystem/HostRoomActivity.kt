package com.bekk.queuingtrackingsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_host_room.*

class HostRoomActivity : AppCompatActivity() {

    private val TAG = "HostRoomActivity"

    lateinit var customerList: ArrayList<String>

    private lateinit var fDbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private lateinit var email: String
    private lateinit var code: String
    private lateinit var qrCode: QRCode
    private lateinit var loadingDialogCircle: LoadingDialogCircle
    private lateinit var loadingDialogHorizontal: LoadingDialogHorizontal
    private lateinit var uidDialog: UIDDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_room)

        supportActionBar?.title = Html.fromHtml("<font color=\"0xffffff\">" + "e-Line" + "</font>")
        supportActionBar?.hide()

        // initialization
        fDbRef = FirebaseDatabase.getInstance().reference
        fAuth = FirebaseAuth.getInstance()
        qrCode = QRCode(this)
        loadingDialogCircle = LoadingDialogCircle(this)
        loadingDialogHorizontal = LoadingDialogHorizontal(this)
        customerList = ArrayList()
        uidDialog = UIDDialog(this)

        loadingDialogHorizontal.start()


        code = intent.getStringExtra("code").toString()
        Log.d(TAG, code)

        email = fAuth.currentUser?.email.toString()

        getCustomerListFromDatabase()

        // customerList = mutableListOf(412, 346, 57, 5, 457, 865, 867)

//        updateList()
//        setUpRecyclerView()

        // ------------------------------ onClickListeners ------------------------------------

        btnNext.setOnClickListener {
            try {
                updateList()
                setUpRecyclerView()
            } catch (e: IndexOutOfBoundsException) {
                Toast.makeText(this, "No more next in line", Toast.LENGTH_SHORT).show()
            }
        }

        btnQRCodeOpener.setOnClickListener {

            qrCode.show(code)

        }

        btnLeave.setOnClickListener {
            // confirm dialog muna dapat

            confirmLeaveDialogBox()


        }

        btnBackHome.setOnClickListener {
            val i = Intent(this, HomeActivity::class.java)
            finish()
            startActivity(i)
        }

        cardView.setOnClickListener {
            uidDialog.show(tvNumberTurn.text.toString())
        }

        // --------------------------end of onClickListeners ---------------------------

    }

    private fun getCustomerListFromDatabase() {
        fDbRef.child("active_rooms").child(code).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                customerList.clear()
                for (postSnapshot in snapshot.children){
                    customerList.add(postSnapshot.value as String)
                }
                setUpRecyclerView()
                loadingDialogHorizontal.stop()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun confirmLeaveDialogBox() {
        MaterialAlertDialogBuilder(this)
            .setTitle("End")
            .setMessage("Are you sure you want to leave and end this room?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, which ->

                deleteRecord()

            }
            .setNegativeButton("No") { dialog, which ->

            }
            .show()
    }

    private fun deleteRecord() {
        loadingDialogCircle.start()

        fDbRef.child("generated_codes").child(fAuth.currentUser!!.uid).removeValue()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    fDbRef.child("active_hosts").child(fAuth.currentUser!!.uid).removeValue()
                        .addOnSuccessListener {

                            fDbRef.child("active_codes").child(fAuth.currentUser!!.uid).removeValue()
                                .addOnSuccessListener {

                                    val i = Intent(this, HomeActivity::class.java)
                                    loadingDialogCircle.stop()
                                    finish()
                                    startActivity(i)

                                }

                        }.addOnFailureListener {
                            loadingDialogCircle.stop()
                            Toast.makeText(this, "Error.", Toast.LENGTH_SHORT).show()
                        }

                } else {
                    loadingDialogCircle.stop()
                    Toast.makeText(this, "Error.", Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun updateList() {
        tvNumberTurn.text = customerList[0].toString()
        customerList.removeAt(0)
    }

    private fun setUpRecyclerView() {
        val adapter = CustomerAdapter(customerList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}