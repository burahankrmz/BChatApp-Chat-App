package com.ourorobos.firebasephoneauthentication.menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ourorobos.firebasephoneauthentication.R;
import com.ourorobos.firebasephoneauthentication.adapter.CallListAdapter;
import com.ourorobos.firebasephoneauthentication.adapter.ChatListAdapter;
import com.ourorobos.firebasephoneauthentication.model.CallList;

import java.util.ArrayList;
import java.util.List;

public class CallsFragment extends Fragment {

    public CallsFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calls,container,false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<CallList> list = new ArrayList<>();
        //recyclerView.setAdapter(new CallListAdapter(list,getContext()));
        return view;
    }
}