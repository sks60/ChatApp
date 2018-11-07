package com.example.sandysaju.chatapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.sandysaju.chatapp.Data.ChatsViewContent;
import com.example.sandysaju.chatapp.Data.MessageViewContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class DatabaseFunctions {

    public static void addUser(String emailID, String displayName, DatabaseReference mDatabase){

        mDatabase.child("Users/"+ formatEmailID(emailID)).setValue(displayName);
    }

    public static String formatEmailID(String email){
        return email.replace(".", "%");
    }

    public static String getCurrentUserDisplayName(){
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    public static String getCurrentUserEmailID(){
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    static void pushConversation(String emailID, String key,
                                 DatabaseReference mDatabase,
                                 ChatsViewContent.Conversation conversation_A,
                                    ChatsViewContent.Conversation conversation_B){

        mDatabase.child("Conversations").child(formatEmailID(getCurrentUserEmailID())).child(key).push().setValue(conversation_A.toMap());
        mDatabase.child("Conversations").child(formatEmailID(emailID)).child(key).push().setValue(conversation_B.toMap());
        mDatabase.child("ChatRooms").child(key).setValue(0);
    }

    static long getSystemTime(){
        return System.currentTimeMillis()/1000L;
    }

    static String fomatedTime(long time){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM hh:mm");
        Date newDate = new Date(time*1000L);
        Log.d("", "fomatedTime: "+ time);
        Calendar c = Calendar.getInstance();
        format.setTimeZone(c.getTimeZone());

        return format.format(newDate);
    }

    static void sendMessage(String chatRoomKey, String textMessage, DatabaseReference mDatabase){
        MessageViewContent.Message newMessage = new MessageViewContent.Message(getCurrentUserEmailID(),
                textMessage,getCurrentUserDisplayName(),
                getSystemTime());

        mDatabase.child("ChatRooms").child(chatRoomKey).push().setValue(newMessage.toMap());
    }

    static void updateLiveMessage(String chatRoomKey, String text, DatabaseReference mDatabase,Context c){
        ConnectivityManager conMgr = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {
            if(conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED){
                // notify user you are online
                HashMap<String, String> result = new HashMap<>();
                result.put("senderDisplayName", getCurrentUserDisplayName());
                result.put("senderID", getCurrentUserEmailID());
                result.put("textMessage", text);
                mDatabase.child("Live").child(chatRoomKey).setValue(result);
            }

        }

    }

    static void clearLiveMessage(String chatRoomKey, DatabaseReference mDatabase){
        mDatabase.child("Live").child(chatRoomKey).removeValue();
    }

}
