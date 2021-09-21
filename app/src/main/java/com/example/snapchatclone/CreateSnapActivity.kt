package com.example.snapchatclone

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.jar.Manifest
import com.google.android.gms.tasks.OnSuccessListener

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class CreateSnapActivity : AppCompatActivity() {

    var snapImageView : ImageView? = null
    var messageEditText: EditText? = null
    var chooseButton: Button? = null
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser

    fun getphoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    public fun chooseImage (view:View){

        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
           getphoto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //works when we select a photo from the gallery
        //uri is object to store the location of media
        Log.i("Intent works", "Successful")
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                val selectedimage = data.data //uri is object to store the location of media
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedimage)
                snapImageView?.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getphoto()
            }
        }
    }


    public fun next (view:View){

        val sdf = SimpleDateFormat("dd-MM-yyyy_HH:mm:SS", Locale.getDefault())
        val currentDateandTime: String = sdf.format(Date())

        var Imagename = currentUser?.email+"_"+currentDateandTime+".jpeg"
        var userEmail = "*"+currentUser?.email+"*"

        // Get the data from an ImageView as bytes
        snapImageView?.isDrawingCacheEnabled = true
        snapImageView?.buildDrawingCache()
        val bitmap = (snapImageView?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask =FirebaseStorage.getInstance().reference.child(userEmail).child("Snaps").child(Imagename).putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this,"There is a error in uploading th image",Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            val result = taskSnapshot.metadata!!.reference!!
                .downloadUrl
            result.addOnSuccessListener { uri -> val photoStringLink = uri.toString()
                Log.i("url",photoStringLink)

                var intent = Intent(this,ChooseUserActivity::class.java)
                intent.putExtra("ImageName",Imagename)
                intent.putExtra("ImageURL",photoStringLink)
                intent.putExtra("Message",messageEditText?.text.toString())
                startActivity(intent)
            }
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        snapImageView = findViewById(R.id.snapImageView)
        messageEditText = findViewById(R.id.messageEditText)
        chooseButton = findViewById(R.id.chooseButton)

        /*********
         in the video he uses "UUID.randomUUID.toString"
            to generate random alpha numeric word
         ********/

    }

    override fun onBackPressed() {
        super.onBackPressed()
        //this function is called when back button is pressed!!!
    }
}