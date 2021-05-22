package activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.ashutosh.mychat.ModelClasses.Chat
import com.ashutosh.mychat.ModelClasses.Users
import com.ashutosh.mychat.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import fragment.ChatFragment
import fragment.SearchFragment
import fragment.SettingFragment

class Main2Activity : AppCompatActivity() {
    lateinit var coordinatoLayout: CoordinatorLayout
    lateinit var mainToolbar: Toolbar
    lateinit var imgMainProfileImage: ImageView
    lateinit var txtMainUserName: TextView
    lateinit var mainFrame: FrameLayout
    lateinit var navigationView: BottomNavigationView

    var refUsers:DatabaseReference?=null
    var firebaseUser:FirebaseUser?=null
    var previousMenuItem:MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        coordinatoLayout=findViewById(R.id.coordinatoLayout)
        mainToolbar=findViewById(R.id.mainToolbar)
        imgMainProfileImage=findViewById(R.id.imgMainProfileImage)
        txtMainUserName=findViewById(R.id.txtMainUserName)
        mainFrame=findViewById(R.id.mainFrame)
        navigationView=findViewById(R.id.navigationView)

        openChat()
        mainToolBar()

        firebaseUser=FirebaseAuth.getInstance().currentUser
        refUsers=FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
    //for counting num of unread msg
        val ref=FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {


            }

            override fun onDataChange(p0: DataSnapshot) {
                var countUnseenMsg=0

                for (dataSnapshot in p0.children){
                    val chat=dataSnapshot.getValue(Chat::class.java)
                    if(chat!!.getReceiver().equals(firebaseUser!!.uid)&& !chat!!.isIsSeen()){
                        countUnseenMsg=countUnseenMsg+1
                    }
                }
            }

        })



        navigationView.setOnNavigationItemSelectedListener {
                if(previousMenuItem!=null){
          previousMenuItem?.isChecked=false
      }
            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it



            when(it.itemId){
                R.id.menuChat->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFrame,ChatFragment())
                        .commit()

                }
                R.id.menuSearch->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFrame, SearchFragment())
                        .commit()


                }
                R.id.menuSetting->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.mainFrame, SettingFragment())
                        .commit()

                }
            }
            return@setOnNavigationItemSelectedListener true
        }
        //display username and profile picture
        refUsers!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
              if(p0.exists()){
                  val user:Users?=p0.getValue(Users::class.java)
                  txtMainUserName.text=user!!.getUsername()
                  Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(imgMainProfileImage)
              }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }
    fun openChat(){
        val fragment=ChatFragment()
        val transection=supportFragmentManager.beginTransaction()
        transection.replace(R.id.mainFrame,fragment)
        transection.commit()

          }


    override fun onBackPressed() {

        val frag=supportFragmentManager.findFragmentById(R.id.mainFrame)
        when(frag){
            !is ChatFragment ->{
                openChat()
                navigationView.setSelectedItemId(R.id.menuChat)
            }
            else-> super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_homepage,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_signout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@Main2Activity, LoginActivity::class.java))
                finish()
                return true
            }
        }
        return false
    }
    fun mainToolBar(){
        setSupportActionBar(mainToolbar)
        supportActionBar?.title="  "
    }
    private fun updateStatus(status:String){
        val ref=FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        val hashmap=HashMap<String,Any>()
        hashmap["status"]=status
        ref!!.updateChildren(hashmap)
    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")
    }

    override fun onPause() {
        super.onPause()
        updateStatus("offline")
    }

}
