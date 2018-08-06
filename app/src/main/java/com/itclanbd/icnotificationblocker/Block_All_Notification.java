package com.itclanbd.icnotificationblocker;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Process;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.itclanbd.icnotificationblocker.MODEL.BlockList;
import com.itclanbd.icnotificationblocker.MODEL.Notification_History;

import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class Block_All_Notification extends NotificationListenerService {

    private static final String TAG = "NotifiCollectorMonitor";
    Realm realm;
    BlockList blockList;

    public Block_All_Notification() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        Log.d("Package Name",sbn.getPackageName());
        try {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("notification.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
            blockList=realm.where(BlockList.class).equalTo("package_name", "BLOCK_ALL").findFirst();
            Log.d("Check",blockList.getStatus());
            if (blockList!=null){
                if (blockList.getStatus().equals("not_at_all")){

                }else if (blockList.getStatus().equals("no")){
                    blockList=realm.where(BlockList.class).equalTo("package_name", sbn.getPackageName()).findFirst();
                    if (blockList!=null && blockList.getStatus().equals("okok")){
                        Log.d("Check1","no cancel");
                    }else {
                        Log.d("Check2","cancel particular");
                        cancelAllNotifications();
                    }
                }else if (blockList.getStatus().equals("yes")){
                    cancelAllNotifications();
                }else {

                }
            }
        }catch (Exception e){
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }finally {
            if (realm!=null){
                realm.close();
            }
        }
        //cancelAllNotifications();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        Log.d("Notification Removed",sbn.getPackageName());
        try {
            Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("notification.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
            Notification_History notification_history=realm.where(Notification_History.class).equalTo("apkname", sbn.getPackageName()).findFirst();
            realm.beginTransaction();
            if (notification_history!=null){
                notification_history.setNotification_count((Integer.parseInt(notification_history.getNotification_count())+1)+"");
            }else if (notification_history==null){
                Notification_History history=realm.createObject(Notification_History.class);
                history.setNotification_count("1");
                history.setApkname(sbn.getPackageName());
            }
            realm.commitTransaction();
        }catch (Exception e){
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }finally {
            if (realm!=null){
                realm.close();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            ensureCollectorRunning();
            if (isnotificationserviceenable(getApplicationContext())){
                toggleNotificationListenerService();
            }
            /*Realm.init(getApplicationContext());
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("notification.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);*/
        }catch (Exception e){
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            ensureCollectorRunning();
            if (isnotificationserviceenable(getApplicationContext())){
                toggleNotificationListenerService();
            }
        }catch (Exception e){
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
        return START_STICKY;

    }






    //check for service is runnng
    private void ensureCollectorRunning() {
        try{
            ComponentName collectorComponent = new ComponentName(this, /*NotificationListenerService Inheritance*/ Block_All_Notification.class);
            Log.v(TAG, "ensureCollectorRunning collectorComponent: " + collectorComponent);
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            boolean collectorRunning = false;
            List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
            if (runningServices == null ) {
                Log.w(TAG, "ensureCollectorRunning() runningServices is NULL");
                return;
            }
            for (ActivityManager.RunningServiceInfo service : runningServices) {
                if (service.service.equals(collectorComponent)) {
                    Log.w(TAG, "ensureCollectorRunning service - pid: " + service.pid + ", currentPID: " + Process.myPid() + ", clientPackage: " + service.clientPackage + ", clientCount: " + service.clientCount
                            + ", clientLabel: " + ((service.clientLabel == 0) ? "0" : "(" + getResources().getString(service.clientLabel) + ")"));
                    if (service.pid == Process.myPid() /*&& service.clientCount > 0 && !TextUtils.isEmpty(service.clientPackage)*/) {
                        collectorRunning = true;
                    }
                }
            }
            if (collectorRunning) {
                Log.d(TAG, "ensureCollectorRunning: collector is running");
                return;
            }
            Log.d(TAG, "ensureCollectorRunning: collector not running, reviving...");
            toggleNotificationListenerService();
        }catch (Exception e){
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
    }

    private void toggleNotificationListenerService() {
        Log.d(TAG, "toggleNotificationListenerService() called");
        try {
            ComponentName thisComponent = new ComponentName(this, /*getClass()*/ Block_All_Notification.class);
            PackageManager pm = getPackageManager();
            pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            pm.setComponentEnabledSetting(thisComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }catch (Exception e){
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }

    }
    private static boolean isnotificationserviceenable(Context context){
        try{
            Set<String> packaageNames= NotificationManagerCompat.getEnabledListenerPackages(context);
            if (packaageNames.contains(context.getPackageName())){
                return true;
            }
        }catch (Exception e){
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
        return false;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if (realm!=null){
            realm.close();
        }*/
    }

}
