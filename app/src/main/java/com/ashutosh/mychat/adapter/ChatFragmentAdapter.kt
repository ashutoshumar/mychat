package com.ashutosh.mychat.adapter

import activity.MessageActivity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.ashutosh.mychat.ModelClasses.Users
import com.ashutosh.mychat.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatFragmentAdapter(val context: Context, val users:List<Users>, val isChatCheck:Boolean):
    RecyclerView.Adapter<ChatFragmentAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.user_chat,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mUsers=users[position]
        holder.txtChatUserName.text=mUsers!!.getUsername()
        Picasso.get().load(mUsers.getProfile()).placeholder(R.drawable.profile).into(holder.imgChatProfileImage)
        holder.itemView.setOnClickListener {
            val options=arrayOf<CharSequence>(
                "send message",
                "visit profile"
            )
            val builder: AlertDialog.Builder= AlertDialog.Builder(context)
            builder.setTitle("what do you want?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position==0){
                    val intent= Intent(context, MessageActivity::class.java)
                    intent.putExtra("visit_id",mUsers.getUid())
                    //intent.putExtra("sender_pic",mUsers.getProfile())
                    context.startActivity(intent)
                }
                if (position==1){
                    Toast.makeText(context,"VISIT After SOMEDAY", Toast.LENGTH_LONG).show()
                }
            })
            builder.show()
        }
    }
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        var txtChatUserName: TextView =view.findViewById(R.id.txtChatUserName)
        var imgChatProfileImage: CircleImageView =view.findViewById(R.id.imgChatProfileImage)
        var txtChatLastSeen: TextView =view.findViewById(R.id.txtChatLastSeen)
        var imgChatOnlineStatus: CircleImageView =view.findViewById(R.id.imgChatOnlineStatus)
        var imgChatOfflineStatus: CircleImageView =view.findViewById(R.id.imgChatOfflineStatus)
        var chatLinearLayout: LinearLayout =view.findViewById(R.id.chatLinearLayout)
    }
}