package com.example.sandysaju.chatapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sandysaju.chatapp.Data.ChatsViewContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;

public class MainActivity extends AppCompatActivity implements ChatFragment.OnListFragmentInteractionListener {

    private DatabaseReference mDatabase;
    public ChatFragment chatFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(savedInstanceState == null) { //to fix the duplicate view
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            chatFragment = ChatFragment.newInstance(1);
            fragmentTransaction.add(R.id.fragmentContainer, chatFragment);
            fragmentTransaction.commit();
        }

        mDatabase.child(getString(R.string.conversations)).child(DatabaseFunctions.formatEmailID(DatabaseFunctions.getCurrentUserEmailID())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatsViewContent.clearItems();
                if(dataSnapshot.getValue() != null){
                    for(DataSnapshot chatRoom: dataSnapshot.getChildren()){
                        String chatName = "";
                        String senderID = "";
                        String chatRoomKey = chatRoom.getKey();
                        for(DataSnapshot users: chatRoom.getChildren()){
                            chatName += users.child(getString(R.string.senderDN)).getValue();
                            senderID += users.child(getString(R.string.senderID)).getValue();
                        }
                        Log.d("", "onDataChange: " + chatName + chatRoom + chatRoomKey);
                        ChatsViewContent.addItem(new ChatsViewContent.Conversation(chatRoomKey, senderID, chatName));
                        chatFragment.adapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final Context context = this;

        FloatingActionButton newChat = findViewById(R.id.newChat);
        newChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getString(R.string.enterEmail));
                final EditText input = new EditText(getApplicationContext());
                input.setInputType(TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                builder.setView(input);
                builder.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final String user_B = input.getText().toString();
                        final String chatRoomKey = mDatabase.child(getString(R.string.chatRooms)).push().getKey();
                            mDatabase.child("Users/" + DatabaseFunctions.formatEmailID(user_B)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String user_A = DatabaseFunctions.getCurrentUserEmailID();
                                    //String user_B = input.getText().toString();
                                    if(dataSnapshot.getValue() != null && !user_B.isEmpty()) {
                                        ChatsViewContent.Conversation conversation_A = new ChatsViewContent.Conversation(chatRoomKey, user_B, dataSnapshot.getValue().toString());
                                        ChatsViewContent.Conversation conversation_B = new ChatsViewContent.Conversation(chatRoomKey, user_A, DatabaseFunctions.getCurrentUserDisplayName());

                                        DatabaseFunctions.pushConversation(user_B, chatRoomKey, mDatabase, conversation_A, conversation_B);
                                    }else{
                                        //builder.setMessage("Sorry. This email address is not register to this app.");
                                        Toast.makeText(context, "Sorry. This email address is not registered to this app", Toast.LENGTH_LONG).show();

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });
    }


    @Override
    public void onListFragmentInteraction(ChatsViewContent.Conversation item) {
        Intent openChatMessage = new Intent(getApplicationContext(), MessagesActivity.class);
        openChatMessage.putExtra(getString(R.string.chatRoomKey), item.id);
        openChatMessage.putExtra(getString(R.string.senderDN), item.senderDisplayName);
        openChatMessage.putExtra(getString(R.string.senderID), item.senderID);
        startActivity(openChatMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent openLoginActivity = new Intent(getApplicationContext(), LogIn.class);
                startActivity(openLoginActivity);
                finish();
                return true;
            case R.id.settings:
                Intent settingsPage = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsPage);

        }

        return true;
    }

}
