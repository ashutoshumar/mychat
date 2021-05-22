package activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ashutosh.mychat.ModelClasses.Users
import com.ashutosh.mychat.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_visit_user_profilr.*

class VisitUserProfilrActivity : AppCompatActivity() {
    private var userVisitId:String?=""
     var user:Users?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_user_profilr)
        userVisitId=intent.getStringExtra("visit_id")
        val ref=FirebaseDatabase.getInstance().reference.child("users").child(userVisitId!!)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
               if (p0.exists()){
                    user=p0.getValue(Users::class.java)
                   txtVisitProfileUserName.text=user!!.getUsername()
                   Picasso.get().load(user!!.getCover()).into(imgVisitProfileCoverPic)
                   Picasso.get().load(user!!.getProfile()).into(imgVisitProfileProfilePic)

               }

            }

        })
        imgProfileFacebook.setOnClickListener {
            val uri= Uri.parse(user!!.getFacebook())
            val intent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }
        imgProfileInstagram.setOnClickListener {
            val uri= Uri.parse(user!!.getInstagram())
            val intent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }
        imgProfileGoogle.setOnClickListener {
            val uri= Uri.parse(user!!.getWebsite())
            val intent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }
        btnProfileSAendMessage.setOnClickListener {
            val intent=Intent(this@VisitUserProfilrActivity,MessageActivity::class.java)
            intent.putExtra("visit_id",user!!.getUid())
            startActivity(intent)
        }


    }
}
