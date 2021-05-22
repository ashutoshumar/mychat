package fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ashutosh.mychat.ModelClasses.Users
import com.ashutosh.mychat.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_setting.*

/**
 * A simple [Fragment] subclass.
 */
class SettingFragment : Fragment() {
    var usersReference:DatabaseReference?=null
    var firebaseUser:FirebaseUser?=null
    private val requestCODE=438
    private var imageUrl:Uri?=null
    private var storageRef:StorageReference?=null
    private var coverChecker:String=" "
    lateinit var imgSettingCoverPic:ImageView
    lateinit var imgSettingProfilePic:CircleImageView
    lateinit var txtSettingUserName:TextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_setting, container, false)

        imgSettingCoverPic=view.findViewById(R.id.imgSettingCoverPic)
        imgSettingProfilePic=view.findViewById(R.id.imgSettingProfilePic)
        txtSettingUserName=view.findViewById(R.id.txtSettingUserName)

        firebaseUser=FirebaseAuth.getInstance().currentUser
        usersReference=FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        storageRef=FirebaseStorage.getInstance().reference.child("user images")

        usersReference!!.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
               if (p0.exists()){
                   val user:Users?=p0.getValue(Users::class.java)
                  if(context!=null){

                      txtSettingUserName.text=user!!.getUsername()
                      Picasso.get().load(user.getProfile()).into(imgSettingProfilePic)
                      Picasso.get().load(user.getCover()).into(imgSettingCoverPic)

                  }
               }
            }

        })
        imgSettingProfilePic.setOnClickListener {
            pickImage()
        }
        imgSettingCoverPic.setOnClickListener {
            coverChecker="cover"
            pickImage()
        }



        return view
    }



    private fun pickImage() {
        val intent=Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,requestCODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==requestCODE&&resultCode==Activity.RESULT_OK&&data!!.data!=null){
          imageUrl=data.data
            Toast.makeText(context,"uploading,,,,,",Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar=ProgressDialog(context)
        progressBar.setMessage("image is uploading,please wait.....")
        progressBar.show()
        if(imageUrl!=null){
            val fileRef=storageRef!!.child(System.currentTimeMillis().toString()+".jpg")

            var uploadTask:StorageTask<*>
            uploadTask=fileRef.putFile(imageUrl!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>> {
                task ->
                if (!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }

                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val downloadUrl=task.result
                    val url=downloadUrl.toString()

                    if(coverChecker=="cover"){
                      val mapCoverImage=HashMap<String,Any>()
                        mapCoverImage["cover"]=url
                        usersReference!!.updateChildren(mapCoverImage)
                        coverChecker=" "
                    }else{
                        val mapProfileImage=HashMap<String,Any>()
                        mapProfileImage["profile"]=url
                        usersReference!!.updateChildren(mapProfileImage)
                        coverChecker=" "
                    }
                    progressBar.dismiss()
                }
            }
        }
    }

}
