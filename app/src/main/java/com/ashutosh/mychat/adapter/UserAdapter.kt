package com.ashutosh.mychat.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import activity.MessageActivity
import com.ashutosh.mychat.ModelClasses.Chat
import com.ashutosh.mychat.ModelClasses.Users
import com.ashutosh.mychat.R
import activity.VisitUserProfilrActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(val context: Context,val users:List<Users>,val isChatCheck:Boolean):RecyclerView.Adapter<UserAdapter.ViewHolder>() {
      var lastMsg:String=""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.user_search_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val mUsers=users[position]
        holder.txtSearchUserName.text=mUsers!!.getUsername()
        Picasso.get().load(mUsers.getProfile()).placeholder(R.drawable.profile).into(holder.imgSearchProfileImage)

        if (isChatCheck){
            retriveLastMessage(mUsers.getUid(),holder.txtSearchLastSeen)
        }else{
            holder.txtSearchLastSeen.visibility=View.GONE
        }
        if (isChatCheck){
            if (mUsers.getStatus()=="online"){
                holder.imgSearchOnlineStatus.visibility=View.VISIBLE
                holder.imgSearchOfflineStatus.visibility=View.GONE
            }else{
                holder.imgSearchOnlineStatus.visibility=View.GONE
                holder.imgSearchOfflineStatus.visibility=View.VISIBLE
            }
        }else{
            holder.imgSearchOnlineStatus.visibility=View.GONE
            holder.imgSearchOfflineStatus.visibility=View.GONE
        }
        holder.itemView.setOnClickListener {
            val options=arrayOf<CharSequence>(
                "send message",
                "visit profile"
            )
            val builder:AlertDialog.Builder=AlertDialog.Builder(context)
            builder.setTitle("what do you want?")
            builder.setItems(options,DialogInterface.OnClickListener { dialog, position ->
                if (position==0){
                    val intent= Intent(context, MessageActivity::class.java)
                    intent.putExtra("visit_id",mUsers.getUid())
                    //intent.putExtra("sender_pic",mUsers.getProfile())
                    context.startActivity(intent)
                }
                if (position==1){
                    val intent= Intent(context, VisitUserProfilrActivity::class.java)
                    intent.putExtra("visit_id",mUsers.getUid())
                    //intent.putExtra("sender_pic",mUsers.getProfile())
                    context.startActivity(intent)
                }
            })
            builder.show()
        }
    }



    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var txtSearchUserName:TextView=view.findViewById(R.id.txtSearchUserName)
        var imgSearchProfileImage:CircleImageView=view.findViewById(R.id.imgSearchProfileImage)
        var txtSearchLastSeen:TextView=view.findViewById(R.id.txtSearchLastSeen)
        var imgSearchOnlineStatus:CircleImageView=view.findViewById(R.id.imgSearchOnlineStatus)
        var imgSearchOfflineStatus:CircleImageView=view.findViewById(R.id.imgSearchOfflineStatus)
        var SearchLinearLayout:LinearLayout=view.findViewById(R.id.SearchLinearLayout)
    }
    private fun retriveLastMessage(chatUserId: String?, txtSearchLastSeen: TextView) {
       lastMsg="defaultMsg"
        val firebaseUser=FirebaseAuth.getInstance().currentUser
        val refrence=FirebaseDatabase.getInstance().reference.child("Chats")
        refrence.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                for (dataSnapshot in p0.children){
                    val chat: Chat?=dataSnapshot.getValue(Chat::class.java)
                    if (firebaseUser!=null && chat !=null){
                        if (chat.getReceiver()==firebaseUser!!.uid && chat.getSender()==chatUserId || chat.getReceiver()==chatUserId&&chat.getSender()==firebaseUser!!.uid){
                           lastMsg=chat.getMessage()!!
                        }
                    }
                }
                when(lastMsg){
                    "defaultMsg"-> txtSearchLastSeen.text="no message"
                    "sent you an image"->txtSearchLastSeen.text="image sent."
                    else->txtSearchLastSeen.text=lastMsg
                }
                lastMsg="defaultMsg"
            }

        })
    }
}