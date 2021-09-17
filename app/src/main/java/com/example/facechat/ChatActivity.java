package com.example.facechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.facechat.Adapter.MessageAdapter;
import com.example.facechat.Classes.Message;
import com.example.facechat.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    FirebaseDatabase database;
    private String senderRoom,receiverRoom;
    private MessageAdapter messageAdapter;

    ProgressDialog dialog;
    FirebaseStorage storage;
    private ArrayList<Message>messages;
    String sender_uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        messages=new ArrayList<>();
        messageAdapter=new MessageAdapter(this,messages,senderRoom,receiverRoom);

        setSupportActionBar(binding.toolbar);
        dialog=new  ProgressDialog(this);
        dialog.setMessage("Uploading Image...");
        dialog.setCancelable(false);

        //binding the adapter to the recyclerview
        binding.chatView.setAdapter(messageAdapter);

        //getting the intent from the main activity
        Intent intent=getIntent();
        String name=intent.getStringExtra("name");
        String receiver_uid=intent.getStringExtra("uni_id");
        String image_url=intent.getStringExtra("image");

        database.getReference().child("presence").child(receiver_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String online_Status=snapshot.getValue(String.class);
                    if(online_Status.equals("Online"))
                    {
                        binding.status.setText("Online");
                        binding.status.setVisibility(View.VISIBLE);
                    }
                    else if(online_Status.equals("typing...."))
                    {
                        binding.status.setText(online_Status);
                        binding.status.setVisibility(View.VISIBLE);
                    }
                    else
                        binding.status.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.name.setText(name);
        Glide.with(ChatActivity.this).load(image_url).placeholder(R.drawable.avatar).into(binding.profile);
        sender_uid= FirebaseAuth.getInstance().getUid();


        binding.imageView2.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      finish();

                                                  }
                                              }
        );
        senderRoom=sender_uid+receiver_uid;
        receiverRoom=receiver_uid+sender_uid;

        database.getReference().child("chats").child(senderRoom).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())

                {
                    Message message=dataSnapshot.getValue(Message.class);
                    message.setMessage_id(dataSnapshot.getKey());
                    messages.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Handler handler=new Handler();
        //adding a text change listner to the edittext box
        binding.messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                database.getReference().child("presence").child(sender_uid).setValue("typing....");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userstoppedtyping,1000);
            }
            Runnable userstoppedtyping=new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(sender_uid).setValue("Online");
                }
            };
        });


        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageTxt=binding.messageBox.getText().toString();
                if(messageTxt.equals(""))
                {
                    Toast.makeText(ChatActivity.this, "Enter something to send", Toast.LENGTH_SHORT).show();
                    return;
                }

                Date date=new Date();
                Message message=new Message(messageTxt,sender_uid,date.getTime());

                binding.messageBox.setText("");
                String randomKey=database.getReference().push().getKey();

                HashMap<String,Object> lastMssgObj =new HashMap<>();
                lastMssgObj.put("lastMssg",message.getMessage());
                lastMssgObj.put("lastTime",date.getTime());

                //updating the last message send to the chat
                database.getReference().child("chats").child(senderRoom).updateChildren(lastMssgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMssgObj);


                database.getReference().child("chats").child(senderRoom).child("messages").child(randomKey).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        database.getReference().child("chats").child(receiverRoom).child("messages").child(randomKey).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });

                    }
                });

            }
        });

        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent();
                intent1.setAction(Intent.ACTION_GET_CONTENT);
                intent1.setType("image/*");
                startActivityForResult(intent1,54);

            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setTitle(name);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if(requestCode==54)
        {
            if(data!=null)
            {
                if(data.getData()!=null)
                {
                    Uri selectedImage=data.getData();
                    Calendar calendar=Calendar.getInstance();
                    StorageReference reference=storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
                    dialog.show();
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                       String filePath=uri.toString();
                                        String messageTxt=binding.messageBox.getText().toString();
                                        Date date=new Date();
                                        Message message=new Message(messageTxt,sender_uid,date.getTime());
                                        message.setMessage("photo");
                                        message.setImage_url(filePath);
                                        binding.messageBox.setText("");
                                        String randomKey=database.getReference().push().getKey();

                                        HashMap<String,Object> lastMssgObj =new HashMap<>();
                                        lastMssgObj.put("lastMssg",message.getMessage());
                                        lastMssgObj.put("lastTime",date.getTime());

                                        //updating the last message send to the chat
                                        database.getReference().child("chats").child(senderRoom).updateChildren(lastMssgObj);
                                        database.getReference().child("chats").child(receiverRoom).updateChildren(lastMssgObj);


                                        database.getReference().child("chats").child(senderRoom).child("messages").child(randomKey).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                database.getReference().child("chats").child(receiverRoom).child("messages").child(randomKey).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                });

                                            }
                                        });
//                                        Toast.makeText(ChatActivity.this, filePath, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }

                                });
                            }
                        }
                    });
                }
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        String curr_id=FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(curr_id).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String curr_id=FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(curr_id).setValue("Offline");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}