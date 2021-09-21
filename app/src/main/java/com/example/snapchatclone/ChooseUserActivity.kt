package com.example.snapchatclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChooseUserActivity : AppCompatActivity() {

    var userlistview:ListView? = null
    var emails:ArrayList<String> = ArrayList()
    var keyEmails:ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        userlistview = findViewById(R.id.userListView)
        var adapter:ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)
        userlistview?.adapter= adapter

      /*  FirebaseDatabase.getInstance().getReference("Users").addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }) */

        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                emails.clear()
                keyEmails.clear()
                var children = snapshot.children
                children.forEach {
                    keyEmails.add(it.key.toString())
                    emails.add(it.child("Email").value.toString())
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        userlistview?.onItemClickListener= AdapterView.OnItemClickListener { adapterView, view, i, l ->

            var snapmap : Map<String, String?> = mapOf("From" to FirebaseAuth.getInstance().currentUser?.email!!,"ImageName" to intent.getStringExtra("ImageName"),"ImageURL" to intent.getStringExtra("ImageURL"),"Message" to intent.getStringExtra("Message"))
            FirebaseDatabase.getInstance().getReference("Users").child(keyEmails?.get(i)).child("Snaps").push().setValue(snapmap)

            var intent = Intent(this,SnapsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}