package com.example.snapchatclone

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import android.graphics.drawable.ColorDrawable




class SnapsActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser
    var emails: ArrayList<String> = ArrayList()
    var snapListView: ListView? = null
    var snapshotlist: ArrayList<DataSnapshot> = ArrayList()


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val mf = menuInflater
        mf.inflate(R.menu.snaps_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item?.itemId==R.id.snap){
            //new snap is to created
                Log.i("new Snap","Success")
            val intent = Intent(this,CreateSnapActivity::class.java)
            startActivity(intent)
        }else if(item?.itemId==R.id.logout){
            //log out
                Log.i("Logged Out","Success")
            Firebase.auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)

        val colorDrawable = ColorDrawable(Color.parseColor("#FFF4E458"))
        getSupportActionBar()?.setBackgroundDrawable(colorDrawable)
        getSupportActionBar()?.setTitle(currentUser?.email+"' Snapschat!")
        snapListView = findViewById(R.id.snapListView)
        var adapter: ArrayAdapter<String> = ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)
        snapListView?.adapter=adapter

        FirebaseDatabase.getInstance().getReference("Users").child(mAuth.uid.toString()).child("Snaps").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var children = snapshot.children
                snapshotlist.clear()
                emails.clear()
                children.forEach {
                    emails.add(it.child("From").value.toString())
                    snapshotlist.add(it)
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        snapListView?.onItemClickListener= AdapterView.OnItemClickListener { adapterView, view, i, l ->
            var Imageurl =snapshotlist?.get(i).child("ImageURL").value.toString()
            var message =snapshotlist?.get(i).child("Message").value.toString()
            var snapkey = snapshotlist?.get(i).key
            var from = snapshotlist?.get(i).child("From").value.toString()
            var imagename = snapshotlist?.get(i).child("ImageName").value.toString()
            var intent = Intent(this,ViewSnapActivity::class.java)
            intent.putExtra("ImageURL",Imageurl)
            intent.putExtra("Message",message)
            intent.putExtra("SnapKey",snapkey)
            intent.putExtra("From",from)
            intent.putExtra("ImageName",imagename)
            startActivity(intent)
        }

    }
}