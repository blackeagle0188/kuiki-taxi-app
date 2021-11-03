package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class DriverChatActivity extends AppCompatActivity {

    ChatView chatView;
    FirebaseDatabase database;
    DatabaseReference myMsgRef;
    DatabaseReference oppMsgRef;
    TextView opposite_name;
    JsonObject opposite;
    String opposite_type;
    String user_id;
    String agency_id;
    ChildEventListener _childListener;
    private OkHttpClient httpClient = new OkHttpClient();
    String type = "yes";
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_chat);
        Intent intent = getIntent();
        type = intent.getStringExtra("trip");

        chatView = findViewById(R.id.chat_view);
        user_id = Common.getInstance().getUser_uid();
        agency_id = Common.getInstance().getDriver_uid();
        btnBack = (Button) findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oppMsgRef.removeEventListener(_childListener);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        database = FirebaseDatabase.getInstance();
        myMsgRef = database.getReference("chat/user/" + user_id + "/" + agency_id);
        oppMsgRef = database.getReference("chat/agency/"+ agency_id + "/" + user_id);
        _childListener= oppMsgRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage value = snapshot.getValue(ChatMessage.class);
                database.getReference("chat/agency/"+ agency_id + "/" + user_id + "/" + snapshot.getKey()  + "/status").setValue("yes");
                if(value!=null)
                    chatView.addMessage(value);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getReady();
    }
    public void getReady(){
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                oppMsgRef.push().setValue(chatMessage);
                chatMessage.setType(ChatMessage.Type.RECEIVED);
                myMsgRef.push().setValue(chatMessage);
                chatView.getInputEditText().setText("");
                if(type.equals("yes")) {
                    sendNotification(getResources().getString(R.string.string_new_message_trip));
                } else {
                    sendNotification(getResources().getString(R.string.string_new_message_delivery));
                }

                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        oppMsgRef.removeEventListener(_childListener);
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void sendNotification(String content){
        Map<String,Object> payMap = new HashMap<>();
        Map<String,Object> itemMap = new HashMap<>();
        String token = Common.getInstance().getUserPhone_token();
        payMap.put("to",Common.getInstance().getUserPhone_token());
        itemMap.put("body",content );
        itemMap.put("Title","KUIKI");
        payMap.put("notification",itemMap);
        String json = new Gson().toJson(payMap);
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("Authorization", "key=AAAAu_pgIBk:APA91bFZRT5Yw52C6TTffiY6yoWUTtuQyfFZiqfK5lj_TfK1DTGmOvGRc_PWGW1DQ-vh-pZsw_NFk8m7hsBBm7jUjET3phUnXzSIIXeHiLaRmUv0gLusC2o0WzMyN4lkRnQg_7eZ9ZMk")
                .addHeader("Content-Type", "application/json")
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("error:", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Log.d("response:", response.toString());
            }
        });
    }
}