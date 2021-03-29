package com.ourorobos.firebasephoneauthentication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ourorobos.firebasephoneauthentication.R;
import com.ourorobos.firebasephoneauthentication.model.CallList;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.Holder> {
    private final List<CallList> list;
    private final Context context;

    public CallListAdapter(List<CallList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public CallListAdapter.Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_calls_list,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CallListAdapter.Holder holder, int position) {

        CallList callList = list.get(position);

        holder.callsusername.setText(callList.getUserName());
        holder.whencallstextview.setText(callList.getDate());
        if(callList.getCallType().equals("going")) {
            if(callList.getCallcontacted().equals("missed")) {
                holder.incomgoingview.setImageDrawable(context.getDrawable(R.drawable.goingcalls));
                holder.incomgoingview.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_red_dark));
            } else
            {
                holder.incomgoingview.setImageDrawable(context.getDrawable(R.drawable.goingcalls));
                holder.incomgoingview.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_dark));
            }
        }
        else {
            if(callList.getCallcontacted().equals("missed")) {
                holder.incomgoingview.setImageDrawable(context.getDrawable(R.drawable.incomecalls));
                holder.incomgoingview.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_red_dark));
            } else
            {
                holder.incomgoingview.setImageDrawable(context.getDrawable(R.drawable.incomecalls));
                holder.incomgoingview.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_dark));
            }

        }

        Glide.with(context).load(callList.getUrlProfile()).into(holder.callsprofileicon);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private ImageView calltypeview,incomgoingview;
        private CircularImageView callsprofileicon;
        private TextView whencallstextview,callsusername;

        public Holder(@NonNull @NotNull View itemView) {
            super(itemView);

            calltypeview = itemView.findViewById(R.id.calltypeview);
            incomgoingview = itemView.findViewById(R.id.incomgoingview);
            callsprofileicon = itemView.findViewById(R.id.callsprofileicon);
            whencallstextview = itemView.findViewById(R.id.whencallstextview);
            callsusername = itemView.findViewById(R.id.callsusername);
        }
    }
}
