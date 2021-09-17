package com.example.facechat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.facechat.Classes.Message;
import com.example.facechat.Classes.User;
import com.example.facechat.R;
import com.example.facechat.databinding.GroupReceivedBinding;
import com.example.facechat.databinding.GroupSendBinding;
import com.example.facechat.databinding.ItemReceivedBinding;
import com.example.facechat.databinding.ItemSendBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;

public class GroupMessageAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messages;

    final int Sent_item=1;
    final int Received_item=2;

    public GroupMessageAdapter(Context context, ArrayList<Message> messages)
    {
      this.context=context;
      this.messages=messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       if(viewType==1)
       {
          View view= LayoutInflater.from(context).inflate(R.layout.group_send,parent,false);
          return new SentViewHolder(view);
       }
       else
       {
           View view= LayoutInflater.from(context).inflate(R.layout.group_received,parent,false);
           return new ReceiveViewHolder(view);
       }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message=messages.get(position);

        if(holder.getClass()== SentViewHolder.class)
        {
            SentViewHolder viewHolder=(SentViewHolder)holder;

            FirebaseDatabase.getInstance()
                    .getReference().child("users")
                    .child(message.getSender_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                User user = snapshot.getValue(User.class);
                                viewHolder.binding.name.setVisibility(View.GONE);
//                                if(user.getUni_id()!=FirebaseAuth.getInstance().getUid())
//                                viewHolder.binding.name.setText("@" + user.getName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            if(message.getMessage().equals("photo"))
            {
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context).load(message.getImage_url()).placeholder(R.drawable.placeholder).into(viewHolder.binding.image);
            }
            viewHolder.binding.message.setText(message.getMessage());
        }
        else
        {
           ReceiveViewHolder viewHolder=(ReceiveViewHolder)holder;

            FirebaseDatabase.getInstance()
                    .getReference().child("users")
                    .child(message.getSender_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                User user = snapshot.getValue(User.class);
                                viewHolder.binding.name.setText("@" + user.getName());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            if(message.getMessage().equals("photo"))
            {
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context).load(message.getImage_url()).placeholder(R.drawable.placeholder).into(viewHolder.binding.image);

            }
            viewHolder.binding.message.setText(message.getMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(FirebaseAuth.getInstance().getUid().equals(messages.get(position).getSender_id()))
            return Sent_item;
        else
            return Received_item;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {

        GroupSendBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= GroupSendBinding.bind(itemView);
        }
    }

    public class ReceiveViewHolder extends RecyclerView.ViewHolder {
        GroupReceivedBinding binding;
        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= GroupReceivedBinding.bind(itemView);
        }
    }
}
