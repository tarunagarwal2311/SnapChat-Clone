package com.example.snapchatclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    var email: AutoCompleteTextView? = null
    var password: EditText? = null
    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser

    public fun goButton (view: View){
        Log.i("Button pressed!","Successful")

        mAuth.signInWithEmailAndPassword(email?.text.toString(), password?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // log in success, update UI with the logged-in user's information
                    login()
                } else {
                    // If sign in fails, display a message to the user.
                    //create a new user
                    mAuth.createUserWithEmailAndPassword(email?.text.toString(), password?.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                //new user created! and add to database
                                /*********
                                 *  Invalid Firebase Database path: xyz@gmail.com. Firebase Database paths must not contain '.', '#', '$', '[', or ']'
                                 *********/

                                task.result?.user?.let {
                                    FirebaseDatabase.getInstance().getReference("Users").child(
                                        it.uid).child("Email").setValue(email?.text.toString())
                                }
                                login()
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(this, "Authentication failed.",Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)

        if(currentUser != null){
            login()
            //user is already loged in
            //move to next activity
        }
    }

    fun login(){
        val intent = Intent (this,SnapsActivity::class.java)
        startActivity(intent)
        finish()
    }
}