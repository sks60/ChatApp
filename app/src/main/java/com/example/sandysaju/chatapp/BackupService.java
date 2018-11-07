package com.example.sandysaju.chatapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class BackupService extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int days = Integer.parseInt(preferences.getString("sync_frequency", "1"));
        boolean enable = preferences.getBoolean("enableBackup", true);
        //Log.d("222222", "onReceive: qwerty");
//        Toast.makeText(context, "BAC"+intent.getAction(), Toast.LENGTH_LONG).show();

        final DatabaseReference mDb = FirebaseDatabase.getInstance().getReference();
        mDb.child(context.getString(R.string.conversations))
                .child(DatabaseFunctions.formatEmailID(DatabaseFunctions.getCurrentUserEmailID()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot chats: dataSnapshot.getChildren()){
                    final String chatRoomKey = chats.getKey();
//                    Toast.makeText(context, "KEY"+chatRoomKey, Toast.LENGTH_LONG).show();

                    mDb.child(context.getString(R.string.chatRooms)).child(chatRoomKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Gson g = new Gson();
                            String s = g.toJson(dataSnapshot.getValue());
                            try {
                                String folder_main = "ChatAppStuff";

                                File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                                if (!f.exists()) {
                                    f.mkdirs();
                                }

                                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(f.getAbsolutePath()+"/"+chatRoomKey+".txt"));
                                Log.d("SERVICE", "onDataChange: " + s);
                                outputStreamWriter.write(s);
                                outputStreamWriter.close();
//                                Toast.makeText(context, f.getAbsolutePath()+"/"+chatRoomKey+".txt", Toast.LENGTH_LONG).show();

                            }
                            catch (IOException e) {
                                Log.e("Exception", "File write failed: " + e.toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (enable) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, BackupService.class);
            i.setAction("backup.intent");
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

            if (Build.VERSION.SDK_INT >= 23) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 60 * (1440*days)), pi);
            } else if (Build.VERSION.SDK_INT >= 19) {
                am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 60 * (1440*days)), pi);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (1000 * 60 * (1440*days)), pi);
            }
        }


    }
}
