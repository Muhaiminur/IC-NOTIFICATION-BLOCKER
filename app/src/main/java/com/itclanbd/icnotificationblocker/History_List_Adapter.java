package com.itclanbd.icnotificationblocker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.itclanbd.icnotificationblocker.MODEL.BlockList;
import com.itclanbd.icnotificationblocker.MODEL.Notification_History;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class History_List_Adapter extends RecyclerView.Adapter<History_List_Adapter.MyViewHolder> {
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView history_name, history_count;
        public ImageView history_app_icon;

        public MyViewHolder(View view) {
            super(view);
            history_name = view.findViewById(R.id.history_name);
            history_count = view.findViewById(R.id.history_number);
            history_app_icon = view.findViewById(R.id.history_app_icon);
        }
    }

    List<String> stringList;
    List<String> final_app_list = new ArrayList<String>();
    RealmResults<Notification_History> notification_histories;
    Context context1;
    private Realm realm;
    BlockList blockList;

    History_List_Adapter(Context context, List<String> list) {
        stringList = list;
        context1 = context;
        try {
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("notification.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
            notification_histories = realm.where(Notification_History.class).findAll().sort("notification_count", Sort.DESCENDING);
            for (Notification_History n : notification_histories) {
                final_app_list.add(n.getApkname());
            }
            for (String s : list) {
                if (!final_app_list.contains(s)) {
                    final_app_list.add(s);
                }
            }
            /*if (!list.containsAll(final_app_list)){
                final_app_list.addAll(list);
            }*/
            /*for (String s:final_app_list){
                Log.d("Notification Count",s);
            }*/
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
            if (realm != null) {
                realm.close();
            }
        } finally {

        }
    }

    @NonNull
    @Override
    public History_List_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final History_List_Adapter.MyViewHolder holder, int position) {
        ApkInfoExtractor apkInfoExtractor = new ApkInfoExtractor(context1);


        final String ApplicationPackageName = final_app_list.get(position);
        final String ApplicationLabelName = apkInfoExtractor.GetAppName(ApplicationPackageName);
        Drawable drawable = apkInfoExtractor.getAppIconByPackageName(ApplicationPackageName);

        holder.history_name.setText(ApplicationLabelName);

        //viewHolder.textView_App_Package_Name.setText(ApplicationPackageName);

        Notification_History history = realm.where(Notification_History.class).equalTo("apkname", ApplicationPackageName).findFirst();
        if (history != null) {
            holder.history_count.setText(history.getNotification_count());
        } else {
            holder.history_count.setText("0");
        }


        holder.history_app_icon.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
