package com.elitedom.app.ui.messaging;

import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elitedom.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FeedMessaging extends AppCompatActivity {

    String dorm, uid, author;
    private ArrayList<Message> messageArrayList;
    private MessageAdapter mAdapter;
    private FirebaseFirestore mDatabase;
    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_messaging);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Objects.requireNonNull(getSupportActionBar()).hide();

        RelativeLayout relativeLayout = findViewById(R.id.user_feed_container);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        RecyclerView mRecyclerView = findViewById(R.id.messageList);
        mRecyclerView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        mRecyclerView.setClipToOutline(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        uid = getIntent().getStringExtra("uid");
        dorm = getIntent().getStringExtra("dorm");
        message = findViewById(R.id.edittext_chatbox);
        mDatabase = FirebaseFirestore.getInstance();
        messageArrayList = new ArrayList<>();
        mAdapter = new MessageAdapter(this, messageArrayList);
        mRecyclerView.setAdapter(mAdapter);
        initializeData();
    }

    private void initializeData() {
        messageArrayList.clear();
        mDatabase.collection("dorms").document(dorm).collection("posts").document(uid).collection("chats")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                if (document.get("sender") != null && Objects.requireNonNull(document.get("sender")).toString().length() > 0)
                                    messageArrayList.add(new Message((String) document.get("message"), (String) document.get("timestamp"), (String) document.get("sender"), Uri.parse((String) document.get("image"))));
                                else
                                    messageArrayList.add(new Message((String) document.get("message"), (String) document.get("timestamp")));
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
        mDatabase.collection("dorms").document(dorm).collection("posts").document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) author = (String) Objects.requireNonNull(task.getResult()).get("author");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    public void sendMessage(View view) {
        if (message.getText().toString().length() > 0) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Map<String, Object> messageBlock = new HashMap<>();
            messageBlock.put("message", message.getText().toString());
            messageBlock.put("timestamp", getDate(timestamp.toString()));
            if (!author.equals(auth.getUid())) {
                messageBlock.put("sender", getProfileSender(auth.getUid()));
                messageBlock.put("image", getProfileImage(auth.getUid()));
            }
            mDatabase.collection("dorms").document(dorm).collection("posts").document(uid).collection("chats").document(String.valueOf(timestamp.getTime()))
                    .set(messageBlock);
        }
    }

    private String getProfileSender(String uid) {
        final String[] sender = {""};
        mDatabase.collection("users").document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            sender[0] = document.get("firstName") + " " + document.get("lastName");
                        }
                    }
                });
        return sender[0];
    }

    private String getProfileImage(String uid) {
        final String[] sender = {""};
        mDatabase.collection("users").document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            assert document != null;
                            sender[0] = (String) document.get("image");
                        }
                    }
                });
        return sender[0];
    }

    private String getDate(String time) {
        StringBuilder res = new StringBuilder();
        for (char ch : time.toCharArray()) {
            if (ch == ' ' || res.length() > 0) res.append(ch);
            if (res.length() > 5) break;
        }
        return res.toString();
    }
}
