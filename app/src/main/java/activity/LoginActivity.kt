package activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.ashutosh.mychat.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    lateinit var etEmailLogin: EditText
    lateinit var etPasswordLogin: EditText
    lateinit var btnLogIn: Button
    lateinit var txtForgetPasswordLogin: TextView
    lateinit var txtSignUPLogin: TextView
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        mAuth= FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        etEmailLogin = findViewById(R.id.etEmailLogin)
        etPasswordLogin = findViewById(R.id.etPasswordLogin)
        btnLogIn = findViewById(R.id.btnLogIn)
        txtForgetPasswordLogin = findViewById(R.id.txtForgetPasswordLogin)
        txtSignUPLogin = findViewById(R.id.txtSignUPLogin)

        btnLogIn.setOnClickListener(View.OnClickListener { View -> logIn()

        })

        txtForgetPasswordLogin.setOnClickListener {
            startActivity(Intent(this@LoginActivity,ForgetPasswordActivity::class.java))
        }

        txtSignUPLogin.setOnClickListener {
            startActivity(Intent(this@LoginActivity,SignUpActivity::class.java))
        }

    }
    private fun logIn(){
        val email=etEmailLogin.text.toString()
        val password=etPasswordLogin.text.toString()

        if(!email.isEmpty() && !password.isEmpty()){
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this@LoginActivity,
                OnCompleteListener { task ->
                    if (task.isSuccessful){
                        startActivity(Intent(this@LoginActivity,Main2Activity::class.java))
                        Toast.makeText(this@LoginActivity,"Logged In", Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        Toast.makeText(this@LoginActivity,"Invalid Credentials", Toast.LENGTH_SHORT).show()
                    }
                })
        }else{
            Toast.makeText(this@LoginActivity,"Please Fill The Credentials", Toast.LENGTH_SHORT).show()
        }
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }
    fun updateUI(currentUser: FirebaseUser?){
        if(currentUser!=null){
            startActivity(Intent(this@LoginActivity,Main2Activity::class.java))
            Toast.makeText(this@LoginActivity,"Logged In", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(this@LoginActivity," Not Logged In", Toast.LENGTH_SHORT).show()
        }
    }


}

