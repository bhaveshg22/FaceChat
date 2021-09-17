package com.example.facechat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.facechat.ChatActivity;
import com.example.facechat.Classes.User;
import com.example.facechat.R;
import com.example.facechat.databinding.RowConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersHolder> {


    Context context;
    ArrayList<User> users;
    public UsersAdapter(Context context, ArrayList<User> users)
    {
        this.context=context;
        this.users=users;
    }
    @NonNull
    @Override
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_conversation,parent,false);

        return new UsersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersHolder holder, int position) {

        User user=users.get(position);
        String sender_id= FirebaseAuth.getInstance().getUid();

        String Sender_Room=sender_id+user.getUni_id();

        FirebaseDatabase.getInstance().getReference().child("chats").child(Sender_Room).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String lastmssg=snapshot.child("lastMssg").getValue(String.class);
                    long lasttime=snapshot.child("lastTime").getValue(Long.class);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    holder.binding.msgTime.setText(dateFormat.format(new Date(lasttime)));
                    holder.binding.lastMsg.setText(lastmssg);
                }
                else
                {
                    holder.binding.lastMsg.setText("Tap to chat");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.binding.username.setText(user.getName());
        Glide.with(context).load(user.getProfile_picture()).placeholder(R.drawable.avatar).into(holder.binding.profile);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("name",user.getName());
                intent.putExtra("uni_id",user.getUni_id());
                intent.putExtra("image",user.getProfile_picture());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersHolder extends RecyclerView.ViewHolder{

        RowConversationBinding binding;
        public UsersHolder(@NonNull View itemView) {
            super(itemView);
            binding=RowConversationBinding.bind(itemView);
        }
    }
}
