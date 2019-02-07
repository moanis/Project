package com.example.project.uInterface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.project.R;
import com.example.project.UserClient;
import com.example.project.adapters.ChatMessageRecyclerAdapter;
import com.example.project.models.ChatMessage;
import com.example.project.models.Chatroom;
import com.example.project.models.ChatUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChatroomActivity extends AppCompatActivity implements
        View.OnClickListener
{
    private static final String TAG = "ChatroomActivity";

    private ListenerRegistration _chatMessageEventListener, _userListEventListener;
    private RecyclerView _chatMessageRecyclerView;
    private ChatMessageRecyclerAdapter _chatMessageRecyclerAdapter;
    private FirebaseFirestore dBase;
    private ArrayList<ChatMessage> _messages = new ArrayList<>();
    private Set<String> _messageIds = new HashSet<>();
    private ArrayList<ChatUser> _userList = new ArrayList<>();

    private Chatroom _chatroom;
    private EditText _message;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        _message = findViewById(R.id.input_message);
        _chatMessageRecyclerView = findViewById(R.id.chatmessage_recycler_view);

        findViewById(R.id.checkmark).setOnClickListener(this);

        dBase = FirebaseFirestore.getInstance();

        getIncomingIntent();
        initChatroomRecyclerView();
        getChatroomUsers();
    }

    private void getChatMessages(){

        CollectionReference messagesRef = dBase
                .collection(getString(R.string.collection_chatrooms))
                .document(_chatroom.getChatroom_id())
                .collection(getString(R.string.collection_chat_messages));

        _chatMessageEventListener = messagesRef
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "Error message - onEvent: Listen failed.", e);
                    return;
                }

                if(queryDocumentSnapshots != null){
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        ChatMessage message = doc.toObject(ChatMessage.class);
                        if(!_messageIds.contains(message.getMessage_id())){
                            _messageIds.add(message.getMessage_id());
                            _messages.add(message);
                            _chatMessageRecyclerView.smoothScrollToPosition(_messages.size() - 1);
                        }

                    }
                    _chatMessageRecyclerAdapter.notifyDataSetChanged();

                }
            }
        });
    }

    private void getChatroomUsers(){

        CollectionReference usersRef = dBase
                .collection(getString(R.string.collection_chatrooms))
                .document(_chatroom.getChatroom_id())
                .collection(getString(R.string.collection_chatroom_user_list));

        _userListEventListener = usersRef
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Error message - onEvent: Listen failed.", e);
                            return;
                        }

                        if(queryDocumentSnapshots != null){

                            // Clearing the list and adding all the users again
                            _userList.clear();
                            _userList = new ArrayList<>();

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                ChatUser user = doc.toObject(ChatUser.class);
                                _userList.add(user);
                            }

                            Log.d(TAG, "onEvent: user list size: " + _userList.size());
                        }
                    }
                });
    }

    private void initChatroomRecyclerView(){

        _chatMessageRecyclerAdapter = new ChatMessageRecyclerAdapter
                (_messages, new ArrayList<ChatUser>(), this);

        _chatMessageRecyclerView.setAdapter(_chatMessageRecyclerAdapter);
        _chatMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        _chatMessageRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    _chatMessageRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(_messages.size() > 0){
                                _chatMessageRecyclerView.smoothScrollToPosition(
                                        _chatMessageRecyclerView.getAdapter().getItemCount() - 1);
                            }

                        }
                    }, 100);
                }
            }
        });

    }


    private void insertNewMessage(){
        String message = _message.getText().toString();

        if(!message.equals("")){
            message = message.replaceAll(System.getProperty("line.separator"), "");

            DocumentReference newMessageDoc = dBase
                    .collection(getString(R.string.collection_chatrooms))
                    .document(_chatroom.getChatroom_id())
                    .collection(getString(R.string.collection_chat_messages))
                    .document();

            ChatMessage newChatMessage = new ChatMessage();
            newChatMessage.setMessage(message);
            newChatMessage.setMessage_id(newMessageDoc.getId());

            ChatUser user = ((UserClient)(getApplicationContext())).getUser();
            Log.d(TAG, "insertNewMessage: retrieved user client: " + user.toString());
            newChatMessage.setUser(user);

            newMessageDoc.set(newChatMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        clearMessage();
                    }else{
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, "Error handling new message", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void clearMessage(){
        _message.setText("");
    }

    private void inflateUserListFragment(){
        hideSoftKeyboard();

        UserListFragment fragment = UserListFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getString(R.string.intent_user_list), _userList);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        transaction.replace(R.id.user_list_container, fragment, getString(R.string.fragment_user_list));
        transaction.addToBackStack(getString(R.string.fragment_user_list));
        transaction.commit();
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    private void getIncomingIntent(){
        if(getIntent().hasExtra(getString(R.string.intent_chatroom))){
            _chatroom = getIntent().getParcelableExtra(getString(R.string.intent_chatroom));
            setChatroomName();
            joinChatroom();
        }
    }

    private void leaveChatroom(){

        DocumentReference joinChatroomRef = dBase
                .collection(getString(R.string.collection_chatrooms))
                .document(_chatroom.getChatroom_id())
                .collection(getString(R.string.collection_chatroom_user_list))
                .document(FirebaseAuth.getInstance().getUid());

        joinChatroomRef.delete();
    }

    private void joinChatroom(){

        DocumentReference joinChatroomRef = dBase
                .collection(getString(R.string.collection_chatrooms))
                .document(_chatroom.getChatroom_id())
                .collection(getString(R.string.collection_chatroom_user_list))
                .document(FirebaseAuth.getInstance().getUid());

        ChatUser user = ((UserClient)(getApplicationContext())).getUser();

        Log.d(TAG, "joinChatroom: user: " + user.getEmail());
        joinChatroomRef.set(user);
    }

    private void setChatroomName(){
        getSupportActionBar().setTitle(_chatroom.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChatMessages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(_chatMessageEventListener != null){
            _chatMessageEventListener.remove();
        }
        if(_userListEventListener != null){
            _userListEventListener.remove();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatroom_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:{
                UserListFragment fragment =
                        (UserListFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.fragment_user_list));
                if(fragment != null){
                    if(fragment.isVisible()){
                        getSupportFragmentManager().popBackStack();
                        return true;
                    }
                }
                finish();
                return true;
            }
            case R.id.action_chatroom_user_list:{
                inflateUserListFragment();
                return true;
            }
            case R.id.action_chatroom_leave:{
                leaveChatroom();
                return true;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkmark:{
                insertNewMessage();
            }
        }
    }

}
