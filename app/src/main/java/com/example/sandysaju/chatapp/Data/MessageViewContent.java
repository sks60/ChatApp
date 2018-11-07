package com.example.sandysaju.chatapp.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample textMessage for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MessageViewContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Message> ITEMS = new ArrayList<Message>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Message> ITEM_MAP = new HashMap<String, Message>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
    }

    public static void addItem(Message item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.senderID, item);
    }

    public static void clearItems(){
        ITEMS.clear();
        ITEM_MAP.clear();
    }


    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of textMessage.
     */
    public static class Message {
        public final String senderID;
        public final String senderDisplayName;
        public final String textMessage;
        public final long time;

        public Message(String senderID, String textMessage, String senderDisplayName, long time) {
            this.senderID = senderID;
            this.textMessage = textMessage;
            this.senderDisplayName = senderDisplayName;
            this.time = time;
        }

        @Override
        public String toString() {
            return textMessage;
        }

        public Map<String, String> toMap(){
            HashMap<String, String> result = new HashMap<>();
            result.put("senderID", senderID);
            result.put("textMessage", textMessage);
            result.put("senderDisplayName" , senderDisplayName);
            result.put("time", time+"");

            return result;
        }
    }
}
