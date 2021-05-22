package activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.ashutosh.mychat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    lateinit var etNameSignUp: EditText
    lateinit var etEmailSignUp: EditText
    lateinit var etPasswordSignUp: EditText
    lateinit var etConfirmPasswordSignUp: EditText
    lateinit var btnSignUp: Button
    lateinit var txtLoginSignUp: TextView
    private  lateinit var auth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        auth= FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        etNameSignUp=findViewById(R.id.etNameSignUp)
        etEmailSignUp=findViewById(R.id.etEmailSignUp)
        etPasswordSignUp=findViewById(R.id.etPasswordSignUp)
        etConfirmPasswordSignUp=findViewById(R.id.etConfirmPasswordSignUp)
        btnSignUp=findViewById(R.id.btnSignUp)
        txtLoginSignUp=findViewById(R.id.txtLoginSignUp)

        btnSignUp.setOnClickListener {
            val email=etEmailSignUp.text.toString()
            val name=etNameSignUp.text.toString()
            val password=etPasswordSignUp.text.toString()
            val confirmPassword=etConfirmPasswordSignUp.text.toString()

            if(!email.isEmpty()&&!name.isEmpty()&&!password.isEmpty()&&!confirmPassword.isEmpty()){
                if(confirmPassword==password){
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this){
                            task->
                        if (task.isSuccessful){
                            var firebaseUserID=auth.currentUser!!.uid
                            refUsers= FirebaseDatabase.getInstance().reference.child("users").child(firebaseUserID)
                            val userHashMap=HashMap<String,Any>()
                            userHashMap["uid"]=firebaseUserID
                            userHashMap["username"]=name
                            userHashMap["profile"]="https://firebasestorage.googleapis.com/v0/b/my-chat-10888.appspot.com/o/profile.png?alt=media&token=2bb89bb9-f138-4ada-86ce-af2093759ccd"
                            userHashMap["cover"]="https://firebasestorage.googleapis.com/v0/b/my-chat-10888.appspot.com/o/cover.jpg?alt=media&token=e2f13acd-a663-4df7-946d-e628e3dc4b43"
                            userHashMap["status"]="offline"
                            userHashMap["search"]=name.toLowerCase()
                            userHashMap["facebook"]="https://m.facebook.com"
                            userHashMap["instagram"]="https://m.instagram.com"
                            userHashMap["website"]="https://m.google.com"
                            refUsers.updateChildren(userHashMap).addOnCompleteListener(this){
                                    task ->
                                if (task.isSuccessful){
                                    startActivity(Intent(this@SignUpActivity,Main2Activity::class.java))
                                    finish()

                                }else{
                                    Toast.makeText(this@SignUpActivity," Storage Error", Toast.LENGTH_SHORT).show()
                                }
                            }
                          //  Toast.makeText(this@SignUpActivity,"Error", Toast.LENGTH_SHORT).show()

                        }
                    }

                }else{
                    Toast.makeText(this@SignUpActivity,"ConfirmPassword And Password Do Not Match",
                        Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this@SignUpActivity,"Please Fill The Credentials", Toast.LENGTH_SHORT).show()
            }

        }


        txtLoginSignUp.setOnClickListener {
            startActivity(Intent(this@SignUpActivity,LoginActivity::class.java))
            finish()
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    fun updateUI(currentUser: FirebaseUser?){
        if(currentUser!=null){
            startActivity(Intent(this@SignUpActivity,Main2Activity::class.java))
            Toast.makeText(this@SignUpActivity,"Logged In", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(this@SignUpActivity," Not Logged In", Toast.LENGTH_SHORT).show()
        }
    }

}

