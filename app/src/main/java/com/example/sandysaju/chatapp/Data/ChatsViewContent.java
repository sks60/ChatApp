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
public class ChatsViewContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Conversation> ITEMS = new ArrayList<Conversation>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Conversation> ITEM_MAP = new HashMap<String, Conversation>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        //for (int i = 1; i <= COUNT; i++) {
          //  addItem(createDummyItem(i));
        //}
    }

    public static void addItem(Conversation item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void clearItems(){
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    private static Conversation createDummyItem(int position) {
        return new Conversation(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of textMessage.
     */
    public static class Conversation {
        public final String id;
        public final String senderID;
        public final String senderDisplayName;

        public Conversation(String id, String senderID, String senderDisplayName) {
            this.id = id;
            this.senderID = senderID;
            this.senderDisplayName = senderDisplayName;
        }

        @Override
        public String toString() {
            return id;
        }

        public Map<String, String> toMap(){
            HashMap<String, String> result = new HashMap<>();
            result.put("senderID", senderID);
            result.put("senderDisplayName", senderDisplayName);

            return result;
        }
    }
}
