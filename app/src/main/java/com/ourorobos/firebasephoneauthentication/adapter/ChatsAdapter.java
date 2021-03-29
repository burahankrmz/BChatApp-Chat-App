package com.ourorobos.firebasephoneauthentication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ourorobos.firebasephoneauthentication.R;
import com.ourorobos.firebasephoneauthentication.model.Chatlist;
import com.ourorobos.firebasephoneauthentication.model.chat.Chats;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    private List<Chats> list;
    private Context context;
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RİGHT = 1;
    private FirebaseUser firebaseUser;
    private TextView chatsuserstatus;

    public ChatsAdapter(List<Chats> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textMessage;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            textMessage =itemView.findViewById(R.id.chattv);
        }
        void bind (Chats chats) {
            textMessage.setText(chats.getTextMessage());
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(list.get(position).getSender().equals(firebaseUser.getUid())) {
            return  MSG_TYPE_RİGHT;
        }else {
            return  MSG_TYPE_LEFT;
        }
     }
}
