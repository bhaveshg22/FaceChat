package com.example.facechat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.view.StandaloneActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.facechat.Classes.Status;
import com.example.facechat.Classes.User_status;
import com.example.facechat.MainActivity;
import com.example.facechat.R;
import com.example.facechat.databinding.ItemStatusBinding;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TopStatusAdapter extends RecyclerView.Adapter<TopStatusAdapter.TopStatusHolder> {

    Context context;
    ArrayList<User_status>statuses;

    public TopStatusAdapter(Context context,ArrayList<User_status> statusArrayList)
    {
        this.context=context;
        this.statuses=statusArrayList;
    }
    @NonNull
    @Override
    public TopStatusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.item_status,parent,false);
        return new TopStatusHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusHolder holder, int position) {

        User_status user_status=statuses.get(position);
        Status laststatus=user_status.getStatuses().get(user_status.getStatuses().size()-1);
        Glide.with(context).load(laststatus.getImage_url()).into(holder.binding.circleImage);

        holder.binding.circularStatusView.setPortionsCount(user_status.getStatuses().size());


        holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(Status status:user_status.getStatuses())
                {
                    myStories.add(new MyStory(status.getImage_url()));
                }
                new StoryView.Builder(((MainActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(user_status.getName()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(user_status.getProfile_image()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return statuses.size();
    }

    public class TopStatusHolder extends RecyclerView.ViewHolder {

        ItemStatusBinding binding;
        public TopStatusHolder(@NonNull View itemView) {
            super(itemView);
            binding=ItemStatusBinding.bind(itemView);

        }
    }
}
