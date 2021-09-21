package com.example.snapchatclone

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class ViewSnapActivity : AppCompatActivity() {

    var message: TextView? = null
    var snapImage: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)

        message= findViewById(R.id.messagetextView)
        snapImage = findViewById(R.id.SnapPhoto)

        message?.text= intent.getStringExtra("Message")

        val task = download()
        val image: Bitmap
        try {
            image = task.execute(intent.getStringExtra("ImageURL")).get()!!
            snapImage?.setImageBitmap(image)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class download : AsyncTask<String?, Void?, Bitmap?>() {
        protected override fun doInBackground(vararg p0: String?): Bitmap? {
            return try {
                val url = URL(p0[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val `in` = connection.inputStream
                return BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("There is some error", e.toString())
                return null
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var snapkey = intent.getStringExtra("SnapKey")
        var from = intent.getStringExtra("From")
        var imagename = intent.getStringExtra("ImageName")
        if (snapkey != null) {
            //to delete snap info from firebase realtime database
            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("Snaps").child(snapkey).removeValue()
       }
        if (imagename != null) {
            //to delete image.jpeg from firebase storage
            FirebaseStorage.getInstance().getReference("*"+from+"*").child("Snaps").child(imagename).delete()
        }
        finish()
    }
}