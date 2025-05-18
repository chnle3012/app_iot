package com.example.btl_iot.ui.people;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
        void onDeleteClick(Person person);
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
        private final TextView idTextView;
        private final TextView detailsTextView;
        private final ImageButton optionsButton;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.img_person_avatar);
            nameTextView = itemView.findViewById(R.id.txt_person_name);
            idTextView = itemView.findViewById(R.id.txt_person_id);
            detailsTextView = itemView.findViewById(R.id.txt_person_details);
            optionsButton = itemView.findViewById(R.id.btn_options);
        }

        public void bind(Person person, PersonClickListener listener) {
            // Set name with proper handling of null/empty values
            String name = person.getName();
            nameTextView.setText(name != null && !name.isEmpty() ? name : "Không có tên");
            
            // Set ID information separately
            String idValue = person.getIdentificationId();
            if (idValue != null && !idValue.isEmpty()) {
                idTextView.setText("ID: " + idValue);
                idTextView.setVisibility(View.VISIBLE);
            } else {
                idTextView.setVisibility(View.GONE);
            }

            // Create the details text with gender and birthday
            StringBuilder details = new StringBuilder();
            if (person.getGender() != null && !person.getGender().isEmpty()) {
                details.append(person.getGender());
            }
            
            if (person.getBirthday() != null && !person.getBirthday().isEmpty()) {
                if (details.length() > 0) details.append(", ");
                details.append(person.getBirthday());
            }
            
            if (details.length() > 0) {
                detailsTextView.setText(details.toString());
                detailsTextView.setVisibility(View.VISIBLE);
            } else {
                detailsTextView.setVisibility(View.GONE);
            }

            // Load avatar image with proper error handling
            String imagePath = person.getFaceImagePath();
            Context context = itemView.getContext();
            if (imagePath != null && !imagePath.isEmpty()) {
                Glide.with(context)
                        .load(imagePath)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_camera)
                        .into(avatarImageView);
            } else {
                Glide.with(context)
                        .load(android.R.drawable.ic_menu_camera)
                        .apply(RequestOptions.circleCropTransform())
                        .into(avatarImageView);
            }

            // Set click listener for the entire item
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPersonClick(person);
                }
            });
            
            // Set click listener for the options button
            optionsButton.setOnClickListener(v -> {
                if (listener != null) {
                    // Show a popup menu with options
                    PopupMenu popup = new PopupMenu(context, v);
                    popup.inflate(R.menu.person_options_menu);
                    popup.setOnMenuItemClickListener(item -> {
                        int itemId = item.getItemId();
                        if (itemId == R.id.action_edit_person) {
                            listener.onPersonClick(person);
                            return true;
                        } else if (itemId == R.id.action_delete_person) {
                            // Call the delete method in the listener
                            listener.onDeleteClick(person);
                            return true;
                        }
                        return false;
                    });
                    popup.show();
                }
            });
        }
    }
} 