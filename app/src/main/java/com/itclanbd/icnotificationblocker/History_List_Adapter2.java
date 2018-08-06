package com.itclanbd.icnotificationblocker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.itclanbd.icnotificationblocker.MODEL.Notification_History;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class History_List_Adapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int RECIPE = 0;
    private static final int NATIVE_AD = 1;

    // A menu item view type.
    private static final int MENU_ITEM_VIEW_TYPE = 0;

    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;

    // An Activity's Context.
    private final Context mContext;
    List<Object> stringList;
    //List<String> final_app_list=new ArrayList<String>();
    RealmResults<Notification_History> notification_histories;
    Context context1;
    private Realm realm;
    public History_List_Adapter2(Context context, List<Object> list) {
        this.mContext = context;
        stringList=list;
        context1 = context;
        try {
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("notification.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
            /*notification_histories=realm.where(Notification_History.class).findAll().sort("notification_count", Sort.DESCENDING);
            for (Notification_History n:notification_histories){
                final_app_list.add(n.getApkname());
            }
            for (Object s:list){
                if(!final_app_list.contains(s)){
                    final_app_list.add((String) s);
                }
            }*/
        }catch (Exception e){
            Log.d("Error Line Number",Log.getStackTraceString(e));
            if (realm!=null){
                realm.close();
            }
        }finally {

        }
    }


    public class History_List_Adapter3 extends RecyclerView.ViewHolder {
        public TextView history_name,history_count;
        public ImageView history_app_icon;
        public History_List_Adapter3(View view) {
            super(view);
            history_name = view.findViewById(R.id.history_name);
            history_count=view.findViewById(R.id.history_number);
            history_app_icon = view.findViewById(R.id.history_app_icon);
        }
    }
    @Override
    public int getItemCount() {
        return stringList.size();
    }
    @Override
    public int getItemViewType(int position) {

        /*Object recyclerViewItem = final_app_list.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE;
        }
        return MENU_ITEM_VIEW_TYPE;*/
        /*Object recyclerViewItem = stringList.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE;
        }
        return MENU_ITEM_VIEW_TYPE;*/
        Object item = stringList.get(position);
        if (item instanceof String) {
            return RECIPE;
        } else if (item instanceof Ad) {
            return NATIVE_AD;
        } else {
            return -1;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                View unifiedNativeLayoutView = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.ad_unified,
                        viewGroup, false);
                return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            case MENU_ITEM_VIEW_TYPE:
                // Fall through.
            default:
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.history_list, viewGroup, false);
                return new History_List_Adapter3(itemView);
                //return new MenuItemViewHolder(menuItemLayoutView);
        }*/

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == RECIPE) {
            View itemView = inflater.inflate(R.layout.history_list, parent, false);
            return new History_List_Adapter3(itemView);
        } else if (viewType == NATIVE_AD) {
            View nativeAdItem = inflater.inflate(R.layout.item_native_ad, parent, false);
            return new NativeAdViewHolder(nativeAdItem);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        /*int viewType = getItemViewType(position);
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                *//*Object b=final_app_list.get(position);
                UnifiedNativeAd nativeAd = (UnifiedNativeAd)b;
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
                break;*//*
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) stringList.get(position);
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
                break;
            case MENU_ITEM_VIEW_TYPE:
                // fall through
            default:
                History_List_Adapter3 menuItemHolder = (History_List_Adapter3) holder;
                ApkInfoExtractor apkInfoExtractor = new ApkInfoExtractor(context1);


                final String ApplicationPackageName = (String) stringList.get(position);
                //final String ApplicationPackageName = (String) final_app_list.get(position);
                final String ApplicationLabelName = apkInfoExtractor.GetAppName(ApplicationPackageName);
                Drawable drawable = apkInfoExtractor.getAppIconByPackageName(ApplicationPackageName);

                menuItemHolder.history_name.setText(ApplicationLabelName);

                //viewHolder.textView_App_Package_Name.setText(ApplicationPackageName);

                Notification_History history=realm.where(Notification_History.class).equalTo("apkname",ApplicationPackageName).findFirst();
                if (history!=null){
                    menuItemHolder.history_count.setText(history.getNotification_count());
                }else {
                    menuItemHolder.history_count.setText("0");
                }


                menuItemHolder.history_app_icon.setImageDrawable(drawable);
        }*/
        int itemType = getItemViewType(position);

        if (itemType == RECIPE) {
            History_List_Adapter3 menuItemHolder = (History_List_Adapter3) holder;
            ApkInfoExtractor apkInfoExtractor = new ApkInfoExtractor(context1);


            final String ApplicationPackageName = (String) stringList.get(position);
            //final String ApplicationPackageName = (String) final_app_list.get(position);
            final String ApplicationLabelName = apkInfoExtractor.GetAppName(ApplicationPackageName);
            Drawable drawable = apkInfoExtractor.getAppIconByPackageName(ApplicationPackageName);

            menuItemHolder.history_name.setText(ApplicationLabelName);

            //viewHolder.textView_App_Package_Name.setText(ApplicationPackageName);

            Notification_History history=realm.where(Notification_History.class).equalTo("apkname",ApplicationPackageName).findFirst();
            if (history!=null){
                menuItemHolder.history_count.setText(history.getNotification_count());
            }else {
                menuItemHolder.history_count.setText("0");
            }


            menuItemHolder.history_app_icon.setImageDrawable(drawable);
        } else if (itemType == NATIVE_AD) {
            NativeAdViewHolder nativeAdViewHolder = (NativeAdViewHolder) holder;
            NativeAd nativeAd = (NativeAd) stringList.get(position);

            AdIconView adIconView = nativeAdViewHolder.adIconView;
            TextView tvAdTitle = nativeAdViewHolder.tvAdTitle;
            TextView tvAdBody = nativeAdViewHolder.tvAdBody;
            Button btnCTA = nativeAdViewHolder.btnCTA;
            LinearLayout adChoicesContainer = nativeAdViewHolder.adChoicesContainer;
            MediaView mediaView = nativeAdViewHolder.mediaView;
            TextView sponsorLabel = nativeAdViewHolder.sponsorLabel;

            tvAdTitle.setText(nativeAd.getAdvertiserName());
            tvAdBody.setText(nativeAd.getAdBodyText());
            btnCTA.setText(nativeAd.getAdCallToAction());
            sponsorLabel.setText(nativeAd.getSponsoredTranslation());

            AdChoicesView adChoicesView = new AdChoicesView(context1, nativeAd, true);
            adChoicesContainer.addView(adChoicesView);

            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(btnCTA);
            clickableViews.add(mediaView);
            nativeAd.registerViewForInteraction(nativeAdViewHolder.container, mediaView, adIconView, clickableViews);
        }
    }
    /*private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }*/

    private static class NativeAdViewHolder extends RecyclerView.ViewHolder {
        AdIconView adIconView;
        TextView tvAdTitle;
        TextView tvAdBody;
        Button btnCTA;
        View container;
        TextView sponsorLabel;
        LinearLayout adChoicesContainer;
        MediaView mediaView;

        NativeAdViewHolder(View itemView) {
            super(itemView);
            this.container = itemView;
            adIconView = (AdIconView) itemView.findViewById(R.id.adIconView);
            tvAdTitle = (TextView) itemView.findViewById(R.id.tvAdTitle);
            tvAdBody = (TextView) itemView.findViewById(R.id.tvAdBody);
            btnCTA = (Button) itemView.findViewById(R.id.btnCTA);
            adChoicesContainer = (LinearLayout) itemView.findViewById(R.id.adChoicesContainer);
            mediaView = (MediaView) itemView.findViewById(R.id.mediaView);
            sponsorLabel = (TextView) itemView.findViewById(R.id.sponsored_label);
        }
    }
}
