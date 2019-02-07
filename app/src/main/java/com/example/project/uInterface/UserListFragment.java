package com.example.project.uInterface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.project.R;
import com.example.project.adapters.UserRecyclerAdapter;
import com.example.project.models.ChatUser;

import java.util.ArrayList;

public class UserListFragment extends Fragment {

    private static final String TAG = "UserListFragment";

    //widgets
    private RecyclerView _userListRecyclerView;


    //vars
    private ArrayList<ChatUser> _userList = new ArrayList<>();
    private UserRecyclerAdapter _userRecyclerAdapter;


    public static UserListFragment newInstance(){
        return new UserListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            _userList = getArguments().getParcelableArrayList(getString(R.string.intent_user_list));
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_user_list, container, false);
        _userListRecyclerView = view.findViewById(R.id.user_list_recycler_view);

        initUserListRecyclerView();
        return view;
    }


    private void initUserListRecyclerView(){
        _userRecyclerAdapter = new UserRecyclerAdapter(_userList);
        _userListRecyclerView.setAdapter(_userRecyclerAdapter);
        _userListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}



















