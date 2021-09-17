package com.example.facechat.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.facechat.Classes.Message;
import com.example.facechat.R;
import com.example.facechat.databinding.ItemReceivedBinding;
import com.example.facechat.databinding.ItemSendBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message> messages;

    final int Sent_item=1;
    final int Received_item=2;

    String Sender_room,Receiver_room;
    public MessageAdapter(Context context, ArrayList<Message> messages,String sender_room,String receiver_room)
    {
      this.context=context;
      this.messages=messages;
      this.Sender_room=sender_room;
      this.Receiver_room=receiver_room;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       if(viewType==1)
       {
          View view= LayoutInflater.from(context).inflate(R.layout.item_send,parent,false);
          return new SentViewHolder(view);
       }
       else
       {
           View view= LayoutInflater.from(context).inflate(R.layout.item_received,parent,false);
           return new ReceiveViewHolder(view);
       }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message=messages.get(position);

        if(holder.getClass()== SentViewHolder.class)
        {
            SentViewHolder viewHolder=(SentViewHolder)holder;

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

        ItemSendBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= ItemSendBinding.bind(itemView);
        }
    }

    public class ReceiveViewHolder extends RecyclerView.ViewHolder {
        ItemReceivedBinding binding;
        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding= ItemReceivedBinding.bind(itemView);
        }
    }
}
