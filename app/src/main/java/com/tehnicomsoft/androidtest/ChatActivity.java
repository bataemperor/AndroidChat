package com.tehnicomsoft.androidtest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.tehnicomsoft.androidtest.model.Message;
import com.tehnicomsoft.androidtest.utility.Utility;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    RecyclerView recyclerView;
    public DatabaseReference databaseReference;
    Button btnSend, btnImg;
    EditText etText;
    FirebaseAuth firebaseAuth;
    FirebaseRecyclerAdapter<Message, MessageHolder> firebaseRecyclerAdapter;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        etText = (EditText) findViewById(R.id.etText);
        btnSend = (Button) findViewById(R.id.btnSend);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnImg = (Button) findViewById(R.id.btnImg);
        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Message, MessageHolder>(
                        Message.class, R.layout.layout_message_item_friend,
                        MessageHolder.class, databaseReference.child("chat").child("messages")) {
                    @Override
                    protected void populateViewHolder(MessageHolder viewHolder, final Message model, int position) {
                        if (getItemViewType(position) == 2) {
                            Picasso.with(ChatActivity.this).load(model.getImageUri())
                                    .into(viewHolder.ivPhoto);
                            viewHolder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getImageUri()));
                                    startActivity(intent);
                                }
                            });
                        } else {
                            viewHolder.tv.setText(model.getMessage());
                            if (position != 0 && getItem(position - 1).getName().equalsIgnoreCase(getItem(position).getName())) {
                                viewHolder.iv.setVisibility(View.INVISIBLE);
                            } else {
                                viewHolder.iv.setVisibility(View.VISIBLE);
                            }
                        }

                        viewHolder.tvTime.setText(getTime(position));

                    }

                    @Override
                    public int getItemViewType(int position) {
                        if (getItem(position).getImageUri() != null) return 2;
                        if (getItem(position).getName().equalsIgnoreCase(firebaseAuth.getCurrentUser().getEmail())) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }

                    private String getTime(int position) {
                        Date date = new Date(getItem(position).getCreated());

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);

                        Calendar calendarYesterday = Calendar.getInstance();
                        calendarYesterday.add(Calendar.DAY_OF_YEAR, -1);

                        StringBuilder stringBuilder = new StringBuilder();
                        if (calendarYesterday.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && calendarYesterday.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
                            stringBuilder.append("Yesterday at ");
                        }
                        stringBuilder.append(sdf.format(date));

                        return stringBuilder.toString();
                    }

                    LayoutInflater inflater = LayoutInflater.from(ChatActivity.this);

                    @Override
                    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        switch (viewType) {
                            case 0:
                                return new MessageHolder(
                                        inflater.inflate
                                                (R.layout.layout_message_item_user, parent, false));
                            case 1:
                                return new MessageHolder
                                        (inflater.inflate
                                                (R.layout.layout_message_item_friend, parent, false));
                            case 2:
                                return new MessageHolder(
                                        inflater.inflate(R.layout.layout_image, parent, false));

                            default:
                                return null;
                        }
                    }
                };
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message(firebaseAuth.getCurrentUser().getEmail(), etText.getText().toString(), new Date().getTime(), null);
                databaseReference.child("chat").child("messages").push().setValue(message);
                sendNotificationToUser("chat", etText.getText().toString());
                etText.setText("");
            }
        });
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (firebaseRecyclerAdapter.getItemCount() != 1)
                    recyclerView.smoothScrollToPosition(firebaseRecyclerAdapter.getItemCount() - 1);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);

            }
        });
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    public void sendNotificationToUser(String user, final String message) {

//        Firebase ref = new Firebase(FIREBASE_URL);
//        final Firebase notifications = ref.child("notificationRequests");
//
        Map notification = new HashMap<>();
        notification.put("username", user);
        notification.put("message", message);
//
        databaseReference.child("notificationRequests").push().setValue(notification);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
//            try {
            Uri uri = data.getData();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://androidtest-290ed.appspot.com");
            StorageReference mountainsRef = storageRef.child(System.currentTimeMillis() + ".jpg");

//                InputStream iStream = getContentResolver().openInputStream(uri);
//                byte[] inputData = getBytes(iStream);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = Utility.getBitmap(uri.toString(), ChatActivity.this);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] dataA = baos.toByteArray();


            UploadTask uploadTask = mountainsRef.putBytes(dataA);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if (downloadUrl != null) {
                        Message message = new Message(firebaseAuth.getCurrentUser().getEmail(), null, new Date().getTime(), downloadUrl.toString());
                        databaseReference.child("chat").child("messages").push().setValue(message);
                        sendNotificationToUser("chat", etText.getText().toString());
                    }
                }
            });


//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        ImageView iv, ivPhoto;
        TextView tv, tvTime;

        MessageHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.ivImage);
            ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
            tv = (TextView) itemView.findViewById(R.id.tvMessage);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
        }
    }

    public static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

}
