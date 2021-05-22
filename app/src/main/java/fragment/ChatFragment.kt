package fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ashutosh.mychat.ModelClasses.ChatList
import com.ashutosh.mychat.ModelClasses.Users
import com.ashutosh.mychat.Notifications.Token
import com.ashutosh.mychat.R
import com.ashutosh.mychat.adapter.ChatFragmentAdapter
import com.ashutosh.mychat.adapter.UserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {
    lateinit var userAdapter:UserAdapter
    private var mUsers:List<Users>?=null
    private var usersChatList:List<ChatList>?=null
    lateinit var chatRecyclerView:RecyclerView
    private var firebaseUser:FirebaseUser?=null
    lateinit var layoutManager:RecyclerView.LayoutManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_chat, container, false)

        chatRecyclerView=view.findViewById(R.id.chatFragmentRecyclerView)
        chatRecyclerView.setHasFixedSize(true)
        layoutManager=LinearLayoutManager(activity)


        firebaseUser=FirebaseAuth.getInstance().currentUser

        usersChatList=ArrayList()
        val ref=FirebaseDatabase.getInstance().reference.child("newChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                (usersChatList as ArrayList).clear()
                for (dataSnapshot in p0.children){
                    val chatList=dataSnapshot.getValue(ChatList::class.java)
                   if(!(chatList!!.getId()).equals(firebaseUser!!.uid)){
                    (usersChatList as ArrayList).add(chatList!!)}

                }
                retrieveChatList()
            }

        })
        updateToken(FirebaseInstanceId.getInstance().token)
        return view
    }

    private fun updateToken(token: String?) {
       val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1=Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun retrieveChatList(){
        mUsers=ArrayList()
        val ref=FirebaseDatabase.getInstance().reference.child("users")
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList).clear()
                for (dataSnapshot in p0.children){
                    val user=dataSnapshot.getValue(Users::class.java)
                    for (eachChatList in usersChatList!!){
                        if (user!!.getUid().equals(eachChatList.getId())&& !(user!!.getUid()).equals(firebaseUser!!.uid))
                        {
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
                if(context!=null){
                    userAdapter= UserAdapter(context!!,(mUsers as ArrayList<Users>),true)
                    chatRecyclerView!!.adapter=userAdapter
                chatRecyclerView!!.layoutManager=layoutManager}
            }

        })
    }

}
