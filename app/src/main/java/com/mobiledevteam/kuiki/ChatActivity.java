package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class ChatActivity extends AppCompatActivity {
    ChatView chatView;
    FirebaseDatabase database;
    DatabaseReference myMsgRef;
    DatabaseReference oppMsgRef;
    String uid;
    String user_id;
    String agency_id;
    ChildEventListener _childListener;

    private Button btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatView = findViewById(R.id.chat_view);

        user_id = Common.getInstance().getUser_uid();
        agency_id = Common.getInstance().getDriver_uid();

        btn_back = (Button) findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myMsgRef.removeEventListener(_childListener);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        database = FirebaseDatabase.getInstance();
        myMsgRef = database.getReference("chat/user/" + user_id + "/" + agency_id);
        oppMsgRef = database.getReference("chat/agency/"+ agency_id + "/" + user_id);
        _childListener = myMsgRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage value = dataSnapshot.getValue(ChatMessage.class);
                database.getReference("chat/user/" + user_id + "/" + agency_id + "/" + dataSnapshot.getKey() + "/status").setValue("yes");
                if(value!=null)
                    chatView.addMessage(value);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        getReady();
    }
    public void getReady(){
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                myMsgRef.push().setValue(chatMessage);
                chatMessage.setType(ChatMessage.Type.RECEIVED);
                oppMsgRef.push().setValue(chatMessage);
                chatView.getInputEditText().setText("");
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        myMsgRef.removeEventListener(_childListener);
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}