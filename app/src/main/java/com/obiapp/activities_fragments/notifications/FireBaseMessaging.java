package com.obiapp.activities_fragments.notifications;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.obiapp.R;
import com.obiapp.activities_fragments.activity_chat.ChatActivity;
import com.obiapp.activities_fragments.activity_chat_admin.ChatAdminActivity;
import com.obiapp.activities_fragments.activity_home.HomeActivity;
import com.obiapp.models.AdminMessageModel;
import com.obiapp.models.AdminRoomModel;
import com.obiapp.models.ChatUserModel;
import com.obiapp.models.MessageModel;
import com.obiapp.models.NotFireModel;
import com.obiapp.models.UserModel;
import com.obiapp.preferences.Preferences;
import com.obiapp.tags.Tags;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

public class FireBaseMessaging extends FirebaseMessagingService {

    private Preferences preferences = Preferences.getInstance();
    private Map<String, String> map;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        map = remoteMessage.getData();

        for (String key : map.keySet()) {
            Log.e("Key=", key + "_value=" + map.get(key));
        }

        if (getSession().equals(Tags.session_login)) {
            String to_user_id = String.valueOf(Integer.parseInt(map.get("to_user_id")));
            String my_id = String.valueOf(getUserData().getData().getId());
            String notification_type = map.get("notification_type");
            String from_user_id = map.get("from_user_id");

            if (notification_type.equals("message_send")) {
                ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                String current_class = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
                if (current_class.equals("com.obiapp.activities_fragments.activity_chat.ChatActivity")) {
                    if (to_user_id.equals(my_id)) {

                        String id = String.valueOf(map.get("id"));
                        String room_id = map.get("chat_room_id");
                        String type = String.valueOf(map.get("message_kind"));
                        String message = String.valueOf(map.get("message"));
                        String date = String.valueOf(map.get("date"));
                        String file = "";
                        if (type.equals("file")) {
                            file = map.get("file_link");
                        }

                        String fromUser = map.get("from_user");
                        String toUser = map.get("to_user");

                        UserModel.Data fromUserModel = new Gson().fromJson(fromUser, UserModel.Data.class);
                        UserModel.Data toUserModel = new Gson().fromJson(toUser, UserModel.Data.class);
                        MessageModel.RoomModel roomModel = new MessageModel.RoomModel();


                        MessageModel messageModel = new MessageModel(Integer.parseInt(id), Integer.parseInt(room_id), Integer.parseInt(from_user_id), Integer.parseInt(to_user_id), type, message, file, Long.parseLong(date), roomModel, fromUserModel, toUserModel);
                        EventBus.getDefault().post(messageModel);

                    } else {
                        manageNotification(map);

                    }
                } else {

                    manageNotification(map);
                }


            } else if (notification_type.equals("admin_message_send")) {
                ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                String current_class = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
                if (current_class.equals("com.obiapp.activities_fragments.activity_chat_admin.ChatAdminActivity")) {
                    if (to_user_id.equals(my_id)) {

                        String id = String.valueOf(map.get("id"));
                        String room_id = map.get("chat_room_id");
                        String type = String.valueOf(map.get("message_kind"));
                        String message = String.valueOf(map.get("message"));
                        String date = String.valueOf(map.get("date"));
                        String file = "";
                        if (type.equals("file")) {
                            file = map.get("file_link");
                        }


                        AdminRoomModel adminRoomModel = new AdminRoomModel();
                        AdminMessageModel messageModel = new AdminMessageModel(Integer.parseInt(id), Integer.parseInt(room_id), Integer.parseInt(from_user_id), Integer.parseInt(to_user_id), "", type, message, file, Long.parseLong(date), notification_type, adminRoomModel);
                        EventBus.getDefault().post(messageModel);

                    } else {
                        manageNotification(map);

                    }
                } else {

                    manageNotification(map);
                }


            } else {
                manageNotification(map);

            }
        }
    }

    private void manageNotification(Map<String, String> map) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNewNotificationVersion(map);
        } else {
            createOldNotificationVersion(map);

        }

    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);


    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createNewNotificationVersion(Map<String, String> map) {
        String notification_type = map.get("notification_type");
        String type = String.valueOf(map.get("message_kind"));

        String sound_Path = "";
        if (sound_Path.isEmpty()) {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            sound_Path = uri.toString();
        }

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        String CHANNEL_ID = "my_channel_02";
        CharSequence CHANNEL_NAME = "my_channel_name";
        int IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

        final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE);

        channel.setShowBadge(true);
        channel.setSound(Uri.parse(sound_Path), new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build()
        );
        builder.setChannelId(CHANNEL_ID);
        builder.setSound(Uri.parse(sound_Path), AudioManager.STREAM_NOTIFICATION);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);


        if (notification_type.equals("message_send") || notification_type.equals("admin_message_send")) {

            String title = "";
            String body = "";
            Intent intent;
            if (notification_type.equals("admin_message_send")) {
                title = getString(R.string.admin);
                intent = new Intent(this, ChatAdminActivity.class);
                ChatUserModel chatUserModel = new ChatUserModel();
                chatUserModel.setRoom_id(Integer.parseInt(map.get("chat_room_id")));
                intent.putExtra("data", chatUserModel);

            } else {
                title = map.get("fromUserName");
                intent = new Intent(this, ChatActivity.class);
                String chat_user_id = "";
                String chat_user_name = "";
                String chat_user_image = "";

                String to_user_id = String.valueOf(Integer.parseInt(map.get("to_user_id")));
                String my_id = String.valueOf(getUserData().getData().getId());
                String from_user_id = map.get("from_user_id");
                String fromUser = map.get("from_user");
                String toUser = map.get("to_user");
                String room_id = map.get("chat_room_id");

                UserModel.Data fromUserModel = new Gson().fromJson(fromUser, UserModel.Data.class);
                UserModel.Data toUserModel = new Gson().fromJson(toUser, UserModel.Data.class);
                if (to_user_id.equals(my_id)) {
                    chat_user_id = from_user_id;
                    chat_user_name = fromUserModel.getName();
                    chat_user_image = fromUserModel.getLogo();

                } else {
                    chat_user_id = to_user_id;
                    chat_user_name = toUserModel.getName();
                    chat_user_image = toUserModel.getLogo();
                }


                ChatUserModel chatUserModel = new ChatUserModel(Integer.parseInt(chat_user_id), chat_user_name, chat_user_image, Integer.parseInt(room_id));

                intent.putExtra("data", chatUserModel);

            }
            builder.setContentTitle(title);

            if (type.equals("text")) {
                body = map.get("message");
            } else {
                body = getString(R.string.image_uploded);
            }

            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
            taskStackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_avatar);
            builder.setLargeIcon(bitmap);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {

                manager.createNotificationChannel(channel);
                manager.notify(Tags.not_tag, Tags.not_id, builder.build());
                EventBus.getDefault().post(new NotFireModel(true));

            }

        } else {

            String title = map.get("tile");
            String body = map.get("body");

            builder.setContentTitle(title);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));

            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
            taskStackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
            builder.setLargeIcon(bitmap);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {

                manager.createNotificationChannel(channel);
                manager.notify(Tags.not_tag, Tags.not_id, builder.build());
                EventBus.getDefault().post(new NotFireModel(true));


            }


        }


    }

    private void createOldNotificationVersion(Map<String, String> map) {

        String notification_type = map.get("notification_type");
        String type = String.valueOf(map.get("message_kind"));

        String sound_Path = "";
        if (sound_Path.isEmpty()) {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            sound_Path = uri.toString();
        }

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSound(Uri.parse(sound_Path), AudioManager.STREAM_NOTIFICATION);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);


        if (notification_type.equals("message_send") || notification_type.equals("admin_message_send")) {

            String title = "";
            String body = "";
            Intent intent;
            if (notification_type.equals("admin_message_send")) {
                title = getString(R.string.admin);
                intent = new Intent(this, ChatAdminActivity.class);
                ChatUserModel chatUserModel = new ChatUserModel();
                chatUserModel.setRoom_id(Integer.parseInt(map.get("chat_room_id")));
                intent.putExtra("data", chatUserModel);

            } else {
                title = map.get("fromUserName");
                intent = new Intent(this, ChatActivity.class);
                String chat_user_id = "";
                String chat_user_name = "";
                String chat_user_image = "";

                String to_user_id = String.valueOf(Integer.parseInt(map.get("to_user_id")));
                String my_id = String.valueOf(getUserData().getData().getId());
                String from_user_id = map.get("from_user_id");
                String fromUser = map.get("from_user");
                String toUser = map.get("to_user");
                String room_id = map.get("chat_room_id");

                UserModel.Data fromUserModel = new Gson().fromJson(fromUser, UserModel.Data.class);
                UserModel.Data toUserModel = new Gson().fromJson(toUser, UserModel.Data.class);
                if (to_user_id.equals(my_id)) {
                    chat_user_id = from_user_id;
                    chat_user_name = fromUserModel.getName();
                    chat_user_image = fromUserModel.getLogo();

                } else {
                    chat_user_id = to_user_id;
                    chat_user_name = toUserModel.getName();
                    chat_user_image = toUserModel.getLogo();
                }


                ChatUserModel chatUserModel = new ChatUserModel(Integer.parseInt(chat_user_id), chat_user_name, chat_user_image, Integer.parseInt(room_id));

                intent.putExtra("data", chatUserModel);

            }
            builder.setContentTitle(title);

            if (type.equals("text")) {
                body = map.get("message");
            } else {
                body = getString(R.string.image_uploded);
            }


            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));


            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
            taskStackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_avatar);
            builder.setLargeIcon(bitmap);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {

                manager.notify(Tags.not_tag, Tags.not_id, builder.build());

            }

        } else {

            String title = map.get("tile");
            String body = map.get("body");

            builder.setContentTitle(title);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));

            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
            taskStackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
            builder.setLargeIcon(bitmap);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {

                manager.notify(Tags.not_tag, Tags.not_id, builder.build());


            }


        }


    }


    private UserModel getUserData() {
        return preferences.getUserData(this);
    }

    private String getSession() {
        return preferences.getSession(this);
    }

    private String getRoom() {
        return preferences.getRoomId(this);
    }


}
