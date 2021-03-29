package com.ourorobos.firebasephoneauthentication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ourorobos.firebasephoneauthentication.R;
import com.ourorobos.firebasephoneauthentication.model.user.Users;
import com.ourorobos.firebasephoneauthentication.view.chats.ChatsActivity;
import com.ourorobos.firebasephoneauthentication.view.contact.ContactsActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>{
    private List<Users> list;
    private Context context;

    public ContactsAdapter(List<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_contact_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ContactsAdapter.ViewHolder holder, int position) {

        Users user = list.get(position);

        holder.contactusernametv.setText(user.getUserName());
        holder.contactuserbiotv.setText(user.getBio());

        Glide.with(context).load(user.getImageProfile()).into(holder.contactimageprofile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userID",user.getUserID())
                        .putExtra("userName",user.getUserName())
                        .putExtra("userProfile",user.getImageProfile())
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircularImageView contactimageprofile;
        private TextView contactuserbiotv,contactusernametv;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            contactimageprofile = itemView.findViewById(R.id.contactprofileimage);
            contactuserbiotv = itemView.findViewById(R.id.contactuserbiotv);
            contactusernametv = itemView.findViewById(R.id.contactusernametv);


        }
    }
}
