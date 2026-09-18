package ru.rusholiday.pro;
import android.app.*; import android.content.*; import android.os.*;
public class HolidayReceiver extends BroadcastReceiver{
 public void onReceive(Context c,Intent i){
  String title=i.getStringExtra("title"); if(title==null) title="Праздник России";
  NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
  if(Build.VERSION.SDK_INT>=26) nm.createNotificationChannel(new NotificationChannel("holidays","Праздники",NotificationManager.IMPORTANCE_DEFAULT));
  Notification n=new Notification.Builder(c,"holidays").setSmallIcon(android.R.drawable.ic_dialog_info)
    .setContentTitle("Завтра праздник").setContentText(title).setAutoCancel(true).build();
  nm.notify((int)System.currentTimeMillis(),n);
 }
}
