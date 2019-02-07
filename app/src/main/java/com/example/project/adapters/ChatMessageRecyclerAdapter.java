package com.example.project.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.project.R;
import com.example.project.models.ChatMessage;
import com.example.project.models.ChatUser;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChatMessageRecyclerAdapter extends RecyclerView.Adapter<ChatMessageRecyclerAdapter.ViewHolder>{

    private ArrayList<ChatMessage> _messages;
    private ArrayList<ChatUser> _users;
    private Context _context;



    public ChatMessageRecyclerAdapter(ArrayList<ChatMessage> messages,
                                      ArrayList<ChatUser> users,
                                      Context context) {
        this._messages = messages;
        this._users = users;
        this._context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_chat_message_list_item, parent, false);

        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        if(FirebaseAuth.getInstance().getUid().equals(_messages.get(position).getUser().getUser_id())){
            holder.username.setTextColor(ContextCompat.getColor(_context, R.color.green1));
        }
        else{
            holder.username.setTextColor(ContextCompat.getColor(_context, R.color.blue2));
        }

        holder.username.setText(_messages.get(position).getUser().getUsername());
        holder.message.setText(_messages.get(position).getMessage());
    }



    @Override
    public int getItemCount() {

        return _messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView message, username;

        public ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message_message);
            username = itemView.findViewById(R.id.chat_message_username);
        }
    }


}
















