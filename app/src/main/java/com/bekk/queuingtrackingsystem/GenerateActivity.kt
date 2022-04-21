package com.bekk.queuingtrackingsystem

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_generate.*
import java.lang.Math.abs

class GenerateActivity : AppCompatActivity(), GestureDetector.OnGestureListener {

    // NOTE: merong nilagay na dependecy sa gradle module para sa qr code

    val TAG = "GenerateActivity"
    private lateinit var fDbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth
    private lateinit var email: String
    private lateinit var loadingDialogCircle: LoadingDialogCircle
    private lateinit var loadingDialogHorizontalHorizontal: LoadingDialogHorizontal
    private lateinit var takenCodeLists: ArrayList<String?>

    private lateinit var qrCode: QRCode

    var code: String? = null
    private lateinit var gestureDetector: GestureDetector
    var x1 = 0.0f
    var x2 = 0.0f
    var y1 = 0.0f
    var y2 = 0.0f

    companion object {
        const val MIN_DISTANCE = 150
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate)

        supportActionBar?.hide()

        // initialization
        fDbRef = FirebaseDatabase.getInstance().reference
        fAuth = FirebaseAuth.getInstance()
        email = fAuth.currentUser?.email.toString()
        qrCode = QRCode(this)
        loadingDialogCircle = LoadingDialogCircle(this)
        loadingDialogHorizontalHorizontal = LoadingDialogHorizontal(this)

        takenCodeLists = ArrayList()
        loadingDialogHorizontalHorizontal.start("Updating data...")
        getActiveCodesFromDatabase()


        gestureDetector = GestureDetector(this, this)



        btnGenerate.setOnClickListener {
            if (etTextCode.text.trim().isEmpty()) {
                etTextCode.error = "Empty"
                etTextCode.requestFocus()
                return@setOnClickListener
            }

            code = etTextCode.text.toString().trim()

            qrCode.generateImage(ivQrCode, tvLabel, tvCode, code!!)
            hideKeyboard()


        }

    }

    private fun getActiveCodesFromDatabase() {
        fDbRef.child("active_codes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                takenCodeLists.clear()
                for (postSnapshot in snapshot.children) {
                    takenCodeLists.add(postSnapshot.getValue(String::class.java))
                }
                loadingDialogHorizontalHorizontal.stop()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun hideKeyboard() {
        // hide keyboard
        val manager: InputMethodManager = getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager

        manager.hideSoftInputFromWindow(etTextCode.applicationWindowToken, 0)
    }

    // hindi na gagamitin kasi gumawa na ako gn class for qr code
    private fun generateQRCode() {
        val writer = MultiFormatWriter()

        try {
            // generate qr code
            val matrix = writer.encode(code, BarcodeFormat.QR_CODE, 350, 350)
            val encoder = BarcodeEncoder()
            val bitmap = encoder.createBitmap(matrix)
            ivQrCode.setImageBitmap(bitmap)

            // show code
            tvLabel.visibility = View.VISIBLE
            tvCode.text = code


        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    // gesture listener
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        gestureDetector.onTouchEvent(event)

        when (event?.action) {

            // start of swiper
            0 -> {
                x1 = event.x
                y1 = event.y
            }

            // end of swipe
            1 -> {
                x2 = event.x
                y2 = event.y

                val valueX = x2 - x1
                val valueY = y2 - y1

                // kung mas malaki yung swipe distance kesa sa given na minimum distance
                if (abs(valueX) > MIN_DISTANCE) { // left or right

                    // swipe right
                    if (x2 > x1) {

                        Log.d(TAG, "Swiped right")

                    } else { // swipe left

                        Log.d(TAG, "Swiped left")

                    }

                } else if (abs(valueY) > MIN_DISTANCE) { // top or bottom

                    // swipe down
                    if (y2 > y1) {

                        Log.d(TAG, "Swiped bottom")

                    } else { // swipe up

                        // no input code
                        if (code == null) {
                            Toast.makeText(this, "Enter code first", Toast.LENGTH_SHORT).show()
                        } else {

                            if (takenCodeLists.contains(code)) {
                                etTextCode.error = "A room is active with this code."
                                etTextCode.requestFocus()
                                return false
                            } else {
                                // posibleng pagkapindot ng una ay meron tas biglang delete room
                                // so pagka pindot ulit ay pede na mag generate so alsin na si eror
                                etTextCode.error = null
                            }

                            loadingDialogCircle.start()
                            btnGenerate.isEnabled = false
                            val i = Intent(this, HostRoomActivity::class.java)
                            i.putExtra("code", code)
                            startActivity(i)

                            // upload generated code to database
                            fDbRef.child("generated_codes").child(fAuth.currentUser!!.uid).push()
                                .setValue(Host(email, code!!))
                            fDbRef.child("active_codes").child(fAuth.currentUser!!.uid)
                                .setValue(code!!)
                            fDbRef.child("active_hosts").child(fAuth.currentUser!!.uid)
                                .push().setValue(email).addOnCompleteListener {
                                    loadingDialogCircle.stop()
                                }

                        }


                        Log.d(TAG, "Swiped top")

                    }

                }

            }

        }

        return false
    }

    // ------------------------ not needed ------------------------

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }
}