package com.example.facechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.facechat.Adapter.TopStatusAdapter;
import com.example.facechat.Adapter.UsersAdapter;
import com.example.facechat.Classes.Status;
import com.example.facechat.Classes.User;
import com.example.facechat.Classes.User_status;
import com.example.facechat.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase database;
    public ArrayList<User> users;
    public UsersAdapter usersAdapter;
    public TopStatusAdapter statusAdapter;
    ArrayList<User_status>user_statuses;
    ProgressDialog dialog;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance();
        users=new ArrayList<>();
        user_statuses=new ArrayList<>();

        dialog=new ProgressDialog(this);
        dialog.setMessage("uploading image");
        dialog.setCancelable(false);

        usersAdapter=new UsersAdapter(this,users);
        statusAdapter=new TopStatusAdapter(this,user_statuses);

        //getting the current user for the name ,image and other details;
        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user =snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    user_statuses.clear();
                    for(DataSnapshot storysnapshot:snapshot.getChildren())
                    {
                        User_status status=new User_status();
                        status.setName(storysnapshot.child("name").getValue(String.class));
                        status.setProfile_image(storysnapshot.child("profileImage").getValue(String.class));
                        status.setLastupdate(storysnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status> statuses = new ArrayList<>();

                        for(DataSnapshot statusSnapshot : storysnapshot.child("statuses").getChildren()) {
                            Status sampleStatus = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }

                        status.setStatuses(statuses);
                         user_statuses.add(status);
                    }
                    binding.statusList.hideShimmerAdapter();
                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.recylerView.setAdapter(usersAdapter);
        binding.recylerView.showShimmerAdapter();
        binding.statusList.setAdapter(statusAdapter);
        binding.statusList.showShimmerAdapter();

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    User user1=snapshot1.getValue(User.class);
                    if(!user1.getUni_id().equals(FirebaseAuth.getInstance().getUid()))
                    users.add(user1);
                }
                binding.recylerView.hideShimmerAdapter();
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.bottomNavigationView2.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.status:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 75);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null)
        {
            if(data.getData()!=null)
            {
                dialog.show();
                FirebaseStorage storage=FirebaseStorage.getInstance();
                Date date=new Date();
                StorageReference reference=storage.getReference().child("status").child(date.getTime()+"");
                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    User_status user_status=new User_status();
                                    user_status.setName(user.getName());
                                    user_status.setProfile_image(user.getProfile_picture());
                                    user_status.setLastupdate(date.getTime());

                                    HashMap<String ,Object>hashMap=new HashMap<>();
                                    hashMap.put("name",user_status.getName());
                                    hashMap.put("profileImage",user_status.getProfile_image());
                                    hashMap.put("lastUpdated",user_status.getLastupdate());

                                    Status status=new Status(uri.toString(),user_status.getLastupdate());

                                    database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid()).updateChildren(hashMap);
                                    database.getReference().child("stories").child(FirebaseAuth.getInstance().getUid()).child("statuses").push().setValue(status);
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                });
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.search:
                Toast.makeText(this, "Searched is clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "Settings is being clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.invite:
                Toast.makeText(this, "Invite is being clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.groups:
                startActivity(new Intent(MainActivity.this,Groupchat.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_resource,menu);
        return super.onCreateOptionsMenu(menu);
    }
}