 package fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ashutosh.mychat.ModelClasses.Users
import com.ashutosh.mychat.R
import com.ashutosh.mychat.adapter.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {
    private var userAdapter:UserAdapter?=null
    private var mUsers:List<Users>?=null
    lateinit var etSearchUsers:EditText
    lateinit var searchRecyclerView:RecyclerView
    lateinit var searchProgressLayout:RelativeLayout
    lateinit var searchProgressBar:ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_search, container, false)
        etSearchUsers=view.findViewById(R.id.etSearchUsers)
        searchProgressLayout=view.findViewById(R.id.searchProgressLayout)
        searchProgressBar=view.findViewById(R.id.searchProgressBar)
        searchProgressLayout.visibility=View.VISIBLE
        searchProgressBar.visibility=View.VISIBLE
        searchRecyclerView=view.findViewById(R.id.searchRecyclerView)
        searchRecyclerView!!.setHasFixedSize(true)
        searchRecyclerView!!.layoutManager=LinearLayoutManager(context)

        mUsers=ArrayList()
        retrieveAllUsers()

        etSearchUsers.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
               searchForUsers(cs.toString().toLowerCase())
            }

        })

        return view
    }
    fun retrieveAllUsers(){
        var firebaseUserId=FirebaseAuth.getInstance().currentUser!!.uid
        var refUsers=FirebaseDatabase.getInstance().reference.child("users")
        refUsers!!.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
               if(etSearchUsers!!.text.toString()==""){
                   for (snapshot in p0.children){
                       val user:Users?=snapshot.getValue(Users::class.java)
                       if(!(user!!.getUid()).equals(firebaseUserId)){
                           searchProgressLayout.visibility=View.GONE
                           (mUsers as ArrayList<Users>).add(user)
                       }
                   }
                   if (context!=null){
                   userAdapter= UserAdapter(context!!,(mUsers as ArrayList<Users>),false)
                   searchRecyclerView!!.adapter=userAdapter}
               }
               }

        })
    }
    private fun searchForUsers(str:String){
        var firebaseUserId=FirebaseAuth.getInstance().currentUser!!.uid
        var querryUsers=FirebaseDatabase.getInstance().reference.child("users").orderByChild("search").startAt(str).endAt(str+"\ufaff")
        querryUsers.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for(snapshot in p0.children){
                    val user:Users?=snapshot.getValue(Users::class.java)
                    if(!(user!!.getUid()).equals(firebaseUserId)){
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                if (context!=null){
                userAdapter= UserAdapter(context!!,mUsers!!,false)
                searchRecyclerView!!.adapter=userAdapter}
            }

        })
    }

}
