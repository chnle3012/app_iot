package com.example.btl_iot.ui.people;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btl_iot.R;
import com.example.btl_iot.data.model.Person;

import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PersonViewHolder> {
    private static final String TAG = "PeopleAdapter";
    private final PersonClickListener clickListener;
    private List<Person> personList = new ArrayList<>();

    public interface PersonClickListener {
        void onPersonClick(Person person);
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
        Person person = personList.get(position);
        Log.d(TAG, "onBindViewHolder position: " + position + ", person: " + person.getName());
        holder.bind(person, clickListener);
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public void submitList(List<Person> list) {
        Log.d(TAG, "submitList called, size: " + (list != null ? list.size() : 0));
        if (list != null) {
            this.personList = new ArrayList<>(list);
            notifyDataSetChanged();
        }
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder {
        private final ImageView avatarImageView;
        private final TextView nameTextView;
        private final TextView detailsTextView;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.img_person_avatar);
            nameTextView = itemView.findViewById(R.id.txt_person_name);
            detailsTextView = itemView.findViewById(R.id.txt_person_details);
        }

        public void bind(Person person, PersonClickListener listener) {
            nameTextView.setText(person.getName());

            String details = "";
            if (person.getIdentificationId() != null) {
                details += person.getIdentificationId();
            }
            if (person.getGender() != null) {
                if (!details.isEmpty()) details += ", ";
                details += person.getGender();
            }
            if (person.getBirthday() != null) {
                if (!details.isEmpty()) details += ", ";
                details += person.getBirthday();
            }
            detailsTextView.setText(details);

            String imagePath = person.getFaceImagePath();
            Context context  = itemView.getContext();
            if (imagePath != null && !imagePath.isEmpty()) {
                Glide.with(context)
                        .load(imagePath)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_camera)
                        .centerCrop()
                        .into(avatarImageView);
            } else {
                avatarImageView.setImageResource(android.R.drawable.ic_menu_camera);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPersonClick(person);
                }
            });
        }
    }
} 