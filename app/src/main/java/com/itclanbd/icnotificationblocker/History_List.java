package com.itclanbd.icnotificationblocker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.itclanbd.icnotificationblocker.MODEL.Notification_History;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class History_List extends Fragment {

    View view;
    private RecyclerView recyclerView;
    //private History_List_Adapter mAdapter;
    private History_List_Adapter2 mAdapter;



    private AdView mAdView;

    // The number of native ads to load.
    public static final int NUMBER_OF_ADS = 5;

    // The AdLoader used to load ads.
    private AdLoader adLoader;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    List<Object> mRecyclerViewItems = new ArrayList<>();

    List<String> stringList=new ArrayList<String>();
    List<String> final_app_list=new ArrayList<String>();
    RealmResults<Notification_History> notification_histories;
    private Realm realm;

    public History_List() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //mRecyclerViewItems.addAll(new ApkInfoExtractor(getActivity().getApplication()).GetAllInstalledApkInfo());
            stringList.addAll(new ApkInfoExtractor(getActivity().getApplication()).GetAllInstalledApkInfo());
            try {
                RealmConfiguration config = new RealmConfiguration.Builder()
                        .name("notification.realm")
                        .schemaVersion(1)
                        .deleteRealmIfMigrationNeeded()
                        .build();
                realm = Realm.getInstance(config);
                notification_histories=realm.where(Notification_History.class).findAll().sort("notification_count", Sort.DESCENDING);
                for (Notification_History n:notification_histories){
                    final_app_list.add(n.getApkname());
                }
                for (Object s:stringList){
                    if(!final_app_list.contains(s)){
                        final_app_list.add((String) s);
                    }
                }
            }catch (Exception e){
                Log.d("Error Line Number",Log.getStackTraceString(e));
                if (realm!=null){
                    realm.close();
                }
            }finally {

            }
        }catch (Exception e){

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_history__list, container, false);
        try {
            mRecyclerViewItems.addAll(final_app_list);
            MobileAds.initialize(getActivity(), getString(R.string.admob_app_id));
            recyclerView =view.findViewById(R.id.history_recycler);
            recyclerView.setFocusable(false);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            //mAdapter= new History_List_Adapter(getActivity(), new ApkInfoExtractor(getActivity()).GetAllInstalledApkInfo());
            //recyclerView.setAdapter(mAdapter);

            mAdapter= new History_List_Adapter2(getActivity().getApplication(),mRecyclerViewItems);
            recyclerView.setAdapter(mAdapter);
            //loadNativeAds();

            mAdView = view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


            NativeAd nativeAd = new NativeAd(this.getContext(), "234111967234493_234112227234467");
            nativeAd.setAdListener(new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {

                }

                @Override
                public void onError(Ad ad, AdError adError) {

                }

                @Override
                public void onAdLoaded(Ad ad) {
                    //int c=4;
                    mRecyclerViewItems.add(4, ad);
                    Log.d("Admob","Not working 3");
                    if (mRecyclerViewItems.size()>0){
                        Log.d("Admob",mRecyclerViewItems.size()+"size");
                        //mRecyclerViewItems.add(8, ad);
                        for (int c=3;c<mRecyclerViewItems.size();c=c+10){
                            Log.d("Admob","Not working");
                            mRecyclerViewItems.add(c, ad);
                        }

                    }

                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            });

            nativeAd.loadAd();

        }catch (Exception e){
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }

        return view;
    }


    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    /*private void loadNativeAds() {

        //AdLoader.Builder builder = new AdLoader.Builder(getContext(), "ca-app-pub-3940256099942544/2247696110");
        AdLoader.Builder builder = new AdLoader.Builder(getContext(), getResources().getString(R.string.admob_app_history_recycler_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        mNativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                        }
                    }
                }).build();

        // Load the Native ads.
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }
    private void insertAdsInMenuItems() {
        if (mNativeAds.size() <= 0) {
            return;
        }

        int offset = (mRecyclerViewItems.size() / mNativeAds.size()) + 1;
        int index = 3;
        for (UnifiedNativeAd ad : mNativeAds) {
            mRecyclerViewItems.add(index, ad);
            index = index + offset;
        }
        mAdapter.notifyDataSetChanged();
    }*/
}
