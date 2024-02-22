package com.saveurlife.goodnews.ble;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.saveurlife.goodnews.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BleNotification {
    private static BleNotification instance;
    private Context applicationContext;
    private int alter = 1;


    public static BleNotification getInstance(Context applicationContext){
        if(instance==null){
            instance=new BleNotification(applicationContext);
        }
        return instance;
    }


    private BleNotification(Context context){
        this.applicationContext =context;
    }

    //구조요청, 채팅(포그라운드)
    public void foresendNotification(String[] parts) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = LayoutInflater.from(applicationContext);
                View layout = inflater.inflate(R.layout.custom_toast, null);
                View chatLayout = inflater.inflate(R.layout.custom_toast_chat, null);

                // 커스텀 레이아웃의 파라미터 설정
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layout.setLayoutParams(layoutParams);

                // 커스텀 레이아웃의 뷰에 접근하여 설정
                TextView senderName = layout.findViewById(R.id.toast_name);
                TextView time = layout.findViewById(R.id.toast_time);

                TextView nameChat = chatLayout.findViewById(R.id.toast_chat_name);
                TextView context = chatLayout.findViewById(R.id.toast_chat_text);
                TextView timeChat = chatLayout.findViewById(R.id.toast_chat_time);

                String name = parts.length > 2 ? parts[2] : "이름 없음";
                if (parts[0].equals("help")) {
                    String content = name + "님께서 구조를 요청했습니다.";
                    senderName.setText(content);

                } else if (parts[0].equals("chat")) {
                    nameChat.setText(name);
                    String content = parts.length > 9 ? parts[9] : "내용 없음";
                    context.setText(content);
                }


                String currentTime = new SimpleDateFormat("a hh:mm", Locale.KOREA).format(Calendar.getInstance().getTime());
                time.setText(currentTime);
                timeChat.setText(currentTime);

                // 시스템 알림 사운드 재생
                try {
//                                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                                    r.play();
                    MediaPlayer mediaPlayer = MediaPlayer.create(applicationContext, R.raw.toast_alarm);
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast toast = new Toast(applicationContext);
                toast.setDuration(Toast.LENGTH_SHORT);
                if (parts[0].equals("help")) {
                    toast.setView(layout);
                } else if (parts[0].equals("chat")) {
                    toast.setView(chatLayout);
                }
                toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
                toast.show();

//                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //가족 알림(포그라운드)
    public void foresendFamilyNotification(String name, boolean isConnect) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = LayoutInflater.from(applicationContext);
                View layout = inflater.inflate(R.layout.custom_toast_family, null);

                // 커스텀 레이아웃의 파라미터 설정
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layout.setLayoutParams(layoutParams);

                // 커스텀 레이아웃의 뷰에 접근하여 설정
                TextView senderName = layout.findViewById(R.id.toast_family);
                if(isConnect){
                    String content = "가족 " + name + "님이 연결되었습니다.";
                    senderName.setText(content);
                }else{
                    String content = "가족 " + name + "님과 연결이 끊겼습니다.";
                    senderName.setText(content);
                }



                // 시스템 알림 사운드 재생
                try {
                    MediaPlayer mediaPlayer = MediaPlayer.create(applicationContext, R.raw.toast_alarm);
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast toast = new Toast(applicationContext);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
                toast.show();
            }
        });
    }





    //구조요청 알림
    public void sendNotification(String messageContent) {
        // Notification Channel 생성 (Android O 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Notification Channel";
            String description = "Channel for My App";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("MY_CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // 채널을 시스템에 등록
            NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(applicationContext, "MY_CHANNEL_ID")
                .setSmallIcon(R.drawable.good_news_logo) // 알림 아이콘 설정
                .setContentTitle("구조 요청") // 알림 제목
                .setContentText(messageContent + "님이 구조를 요청했습니다.") // 'message'는 받은 메시지의 내용
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // 알림 표시
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(applicationContext);
        notificationManager.notify(alter++, builder.build()); // 'notificationId'는 각 알림을 구별하는 고유 ID

    }


    //채팅 알림(백그라운드)
    public void sendChatting(String messageContent, String contentBack) {
        // Notification Channel 생성 (Android O 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Notification Channel";
            String description = "Channel for My App";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("MY_CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // 채널을 시스템에 등록
            NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(applicationContext, "MY_CHANNEL_ID")
                .setSmallIcon(R.drawable.good_news_logo) // 알림 아이콘 설정
                .setContentTitle(messageContent) // 알림 제목
                .setContentText(contentBack) // 'message'는 받은 메시지의 내용
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // 알림 표시
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(applicationContext);
        notificationManager.notify(alter++, builder.build()); // 'notificationId'는 각 알림을 구별하는 고유 ID

    }

    //채팅 알림(백그라운드)
//    private void sendChatting(String messageContent, String contentBack) {
//        // Notification Channel 생성 (Android O 이상)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "My Notification Channel";
//            String description = "Channel for My App";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("MY_CHANNEL_ID", name, importance);
//            channel.setDescription(description);
//            // 채널을 시스템에 등록
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "MY_CHANNEL_ID")
//                .setSmallIcon(R.drawable.good_news_logo) // 알림 아이콘 설정
//                .setContentTitle(messageContent) // 알림 제목
//                .setContentText(contentBack) // 'message'는 받은 메시지의 내용
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        // 알림 표시
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.notify(alter++, builder.build()); // 'notificationId'는 각 알림을 구별하는 고유 ID
//
//    }
}
