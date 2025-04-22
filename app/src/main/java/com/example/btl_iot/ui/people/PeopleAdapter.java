package com.example.btl_iot.ui.people;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_iot.R;
import com.example.btl_iot.data.model.User;
import com.example.btl_iot.util.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PersonViewHolder> {
    private static final String TAG = "PeopleAdapter";
    private final PersonClickListener clickListener;
    private List<User> userList = new ArrayList<>();

    public interface PersonClickListener {
        void onPersonClick(User user);
    }

    public PeopleAdapter(PersonClickListener clickListener) {
        this.clickListener = clickListener;
        Log.d(TAG, "PeopleAdapter created");
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_person, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        User user = userList.get(position);
        Log.d(TAG, "onBindViewHolder position: " + position + ", user: " + user.getName());
        holder.bind(user, clickListener);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void submitList(List<User> list) {
        Log.d(TAG, "submitList called, size: " + (list != null ? list.size() : 0));
        if (list != null) {
            this.userList = new ArrayList<>(list);
            notifyDataSetChanged();
        }
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder {
        private final ImageView avatarImageView;
        private final TextView nameTextView;
        private final TextView ageGenderTextView;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.img_person_avatar);
            nameTextView = itemView.findViewById(R.id.txt_person_name);
            ageGenderTextView = itemView.findViewById(R.id.txt_person_details);
        }

        public void bind(User user, PersonClickListener listener) {
            nameTextView.setText(user.getName());
            
            // Format age and gender info
            StringBuilder details = new StringBuilder();
            details.append(user.getAge()).append(" years");
            
            if (user.getGender() != null && !user.getGender().isEmpty()) {
                details.append(", ").append(user.getGender());
            }
            
            ageGenderTextView.setText(details.toString());
            
            String imagePath = user.getImageUrl();
            Context context = itemView.getContext();
            
            if (imagePath != null && !imagePath.isEmpty()) {
                // Load image with Glide for better performance and caching
                Glide.with(context)
                        .load(new File(imagePath))
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_camera)
                        .centerCrop()
                        .into(avatarImageView);
            } else {
                // Default image
                avatarImageView.setImageResource(android.R.drawable.ic_menu_camera);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPersonClick(user);
                }
            });
        }
    }
} 