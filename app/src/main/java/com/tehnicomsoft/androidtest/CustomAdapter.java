package com.tehnicomsoft.androidtest;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.tehnicomsoft.androidtest.model.Message;

/**
 * Created by aleksandar on 12.10.16..
 */

public class CustomAdapter extends FirebaseRecyclerAdapter<Message,ChatActivity.MessageHolder> {
    public CustomAdapter(Class<Message> modelClass, int modelLayout, Class<ChatActivity.MessageHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    public CustomAdapter(Class<Message> modelClass, int modelLayout, Class<ChatActivity.MessageHolder> viewHolderClass, DatabaseReference ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(ChatActivity.MessageHolder viewHolder, Message model, int position) {

    }
}
