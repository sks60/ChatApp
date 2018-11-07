package com.example.sandysaju.chatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sandysaju.chatapp.Data.MessageViewContent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessagesActivity extends AppCompatActivity implements MessagesFragment.OnListFragmentInteractionListener {

    private DatabaseReference mDatabase;
    public MessagesFragment messagesFragment;
    public Toast liveToasting;
    SharedPreferences sharedPreferences;
    ConnectivityManager conMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        liveToasting = Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG);
        setTitle(getIntent().getStringExtra(getString(R.string.senderDN)));
        conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);



        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mDatabase = FirebaseDatabase.getInstance().getReference();


        if(savedInstanceState == null) { //to fix the duplicate view
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            messagesFragment = MessagesFragment.newInstance(1);
            fragmentTransaction.add(R.id.fragmentContainer, messagesFragment);
            fragmentTransaction.commit();
        }

        mDatabase.child(getString(R.string.chatRooms)).child(getIntent().getStringExtra(getString(R.string.chatRoomKey))).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MessageViewContent.clearItems();
                for(DataSnapshot message: dataSnapshot.getChildren()){
                    MessageViewContent.addItem(new MessageViewContent.Message(message.child(getString(R.string.senderID)).getValue().toString(),
                            message.child(getString(R.string.textMessage)).getValue().toString(),
                            message.child(getString(R.string.senderDN)).getValue().toString(),
                            Long.parseLong(message.child(getString(R.string.time)).getValue().toString())));
                }
                messagesFragment.adapter.notifyDataSetChanged();
                messagesFragment.recyclerView.scrollToPosition(MessageViewContent.ITEMS.size()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ImageButton sendMessage = findViewById(R.id.sendMessage);
        final EditText message = findViewById(R.id.message);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = message.getText().toString().trim();
                if(text.length() > 0){
                    message.setText("");
                    DatabaseFunctions.sendMessage(getIntent().getStringExtra(getString(R.string.chatRoomKey)),text, mDatabase);
                }

            }
        });

        message.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    //if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    String text = message.getText().toString().trim();
                    if(text.length() > 0){
                        message.setText("");
                        DatabaseFunctions.sendMessage(getIntent().getStringExtra(getString(R.string.chatRoomKey)),text, mDatabase);
                        return true;
                    }else{
                        return false;
                    }
                }
                return false;
            }
        });

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (sharedPreferences.getBoolean("liveMessage_switch", true)){
                    if (charSequence.length() > 0) {
                        DatabaseFunctions.updateLiveMessage(getIntent().getStringExtra(getString(R.string.chatRoomKey)), charSequence.toString(), mDatabase, getApplicationContext());
                    }else{
                        DatabaseFunctions.clearLiveMessage(getIntent().getStringExtra(getString(R.string.chatRoomKey)), mDatabase);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDatabase.child(getString(R.string.live)).child(getIntent().getStringExtra(getString(R.string.chatRoomKey))).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Log.d("", "onDataChange: "+dataSnapshot);
                if(conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED) {

                    if (dataSnapshot.getValue() != null) {
                        String name = dataSnapshot.child(getString(R.string.senderDN)).getValue().toString();
                        String liveMessage = dataSnapshot.child(getString(R.string.textMessage)).getValue().toString();
                        String senderID = dataSnapshot.child(getString(R.string.senderID)).getValue().toString();
                        liveToasting.setText(name+": "+liveMessage);
                        if (!senderID.equals(DatabaseFunctions.getCurrentUserEmailID())) {
                            liveToasting.show();
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Log.d("", "onCreate: " + getIntent().getStringExtra("senderID"));

    }

    @Override
    public void onListFragmentInteraction(MessageViewContent.Message item) {

    }
}
