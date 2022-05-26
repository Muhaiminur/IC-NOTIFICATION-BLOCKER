package com.itclanbd.icnotificationblocker;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.itclanbd.icnotificationblocker.MODEL.BlockList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import com.facebook.ads.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class On_Off_Page extends Fragment {
    View view;
    Button on, off;
    View customView;
    Switch all_block;
    private RecyclerView recyclerView;
    private App_List_Adapter mAdapter;
    LayoutInflater inflater1;
    TextView check_page;


    //Add work
    private final String TAG = On_Off_Page.class.getSimpleName();
    private NativeAd nativeAd;

    private LinearLayout nativeAdContainer;
    private LinearLayout adView;

    public On_Off_Page() {
        // Required empty public constructor
    }


    //for database
    private Realm realm;
    BlockList blockList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_on__off__page, container, false);
        try {
            /*RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();*/
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("notification.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);
            on = view.findViewById(R.id.on);
            off = view.findViewById(R.id.off);
            check_page = view.findViewById(R.id.check_tutorial);


            on.setTag("STOP");
            blockList = realm.where(BlockList.class).equalTo("package_name", "BLOCK_ALL").findFirst();
            if (blockList != null) {
                if (blockList.getStatus().equals("no") || blockList.getStatus().equals("yes")) {
                    off.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    off.setBackgroundResource(R.drawable.left_off);
                    on.setTextColor(getResources().getColor(R.color.white));
                    on.setBackgroundResource(R.drawable.right_on);
                    //on.setTag("RUNNING");
                }
            }
            on.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (on.getTag().equals("RUNNING")) {

                    } else {
                        off.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        off.setBackgroundResource(R.drawable.left_off);
                        on.setTextColor(getResources().getColor(R.color.white));
                        on.setBackgroundResource(R.drawable.right_on);
                        //on.setTag("RUNNING");

                        inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        customView = inflater1.inflate(R.layout.activity_main, null);
                        recyclerView = customView.findViewById(R.id.app_list_recycler);
                        recyclerView.setHasFixedSize(true);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        mAdapter = new App_List_Adapter(getActivity(), new ApkInfoExtractor(getActivity()).GetAllInstalledApkInfo());
                        recyclerView.setAdapter(mAdapter);
                        recyclerView.setFocusable(false);

                        show("Select", "", "", "on");
                        all_block = customView.findViewById(R.id.block_all_switch);
                    }
                }
            });

            off.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        off.setTextColor(getResources().getColor(R.color.white));
                        off.setBackgroundResource(R.drawable.left_on);
                        on.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        on.setBackgroundResource(R.drawable.right_off);
                        on.setTag("STOP");
                        Snackbar.make(view, "Ending Notification Block", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        blockList = realm.where(BlockList.class).equalTo("package_name", "BLOCK_ALL").findFirst();
                        realm.beginTransaction();
                        if (blockList == null) {
                            BlockList blockList_new = realm.createObject(BlockList.class);
                            blockList_new.setPackage_name("BLOCK_ALL");
                            blockList_new.setStatus("not_at_all");
                        } else {
                            blockList.setStatus("not_at_all");
                        }
                        realm.commitTransaction();
                    } catch (Exception e) {
                        Log.d("Error Line Number", Log.getStackTraceString(e));
                    }
                }
            });

            loadNativeAd();

            //che

            //register work
            String myString = "If App is not working, Click here.";
            int i1 = myString.indexOf("C");
            int i2 = myString.lastIndexOf(".");
            check_page.setMovementMethod(LinkMovementMethod.getInstance());
            check_page.setText(myString, TextView.BufferType.SPANNABLE);
            Spannable mySpannable = (Spannable) check_page.getText();
            ClickableSpan myClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    startActivity(new Intent(getContext(), Tutorial_Page.class));
                }
            };
            mySpannable.setSpan(myClickableSpan, i1, i2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
            if (realm != null) {
                realm.close();
            }
        }
        return view;
    }

    public void show(String title, String msg, String button, String con) {
        try {
            switch (con) {
                case "on":
                    MaterialStyledDialog.Builder dialog = new MaterialStyledDialog.Builder(getActivity());
                    dialog.setStyle(Style.HEADER_WITH_ICON)
                            //.setTitle(title)
                            //.setDescription("A loooooooooong looooooooooong really loooooooooong content. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam pulvinar sem nibh, et efficitur massa mattis eget. Phasellus condimentum ligula.")
                            .setCustomView(customView)
                            .setIcon(R.drawable.ntification_icon)
                            .setHeaderColor(R.color.colorPrimaryDark)
                            .withDarkerOverlay(true)
                            .withDialogAnimation(true)
                            .setCancelable(false)
                            .setScrollable(true)
                            .setPositiveText(R.string.button_on)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    blockList = realm.where(BlockList.class).equalTo("package_name", "BLOCK_ALL").findFirst();
                                    realm.beginTransaction();
                                    if (all_block.isChecked()) {
                                        Snackbar.make(view, "ALL Notification Blocked", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                        if (blockList == null) {
                                            BlockList blockList_new = realm.createObject(BlockList.class);
                                            blockList_new.setPackage_name("BLOCK_ALL");
                                            blockList_new.setStatus("yes");
                                        } else {
                                            blockList.setStatus("yes");
                                        }
                                    } else {
                                        if (blockList == null) {
                                            BlockList blockList_new = realm.createObject(BlockList.class);
                                            blockList_new.setPackage_name("BLOCK_ALL");
                                            blockList_new.setStatus("no");
                                        } else {
                                            blockList.setStatus("no");
                                        }
                                    }
                                    realm.commitTransaction();
                                    NotificationManager nManager = ((NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE));
                                    nManager.cancelAll();
                                }
                            })
                            .setNegativeText("Dismiss")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Log.d("MaterialStyledDialogs", "Do something!");
                                }
                            })
                            .show();
                default:

            }
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
            if (realm != null) {
                realm.close();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }


    private void loadNativeAd() {
        try {
            // Instantiate a NativeAd object.
            // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
            // now, while you are testing and replace it later when you have signed up.
            // While you are using this temporary code you will only get test ads and if you release
            // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
            //nativeAd = new NativeAd(getContext(), "234111967234493_234112227234467");
            nativeAd = new NativeAd(getContext(), "VID_HD_16_9_15S_LINK#234111967234493_234112227234467");
            /*nativeAd.setAdListener(new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {

                }

                @Override
                public void onError(Ad ad, AdError adError) {

                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Race condition, load() called again before last ad was displayed
                    if (nativeAd == null || nativeAd != ad) {
                        return;
                    }
                    // Inflate Native Ad into Container
                    inflateAd(nativeAd);
                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            });
            // Request an ad
            nativeAd.loadAd();*/
        } catch (Exception e) {
            Log.d("Error", "facebook Add");
        }
    }

   /* private void inflateAd(NativeAd nativeAd) {

        try {
            nativeAd.unregisterView();

            // Add the Ad view into the ad container.
            nativeAdContainer = view.findViewById(R.id.native_ad_container);
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
            adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdContainer, false);
            nativeAdContainer.addView(adView);

            // Add the AdChoices icon
            LinearLayout adChoicesContainer = view.findViewById(R.id.ad_choices_container);
            AdChoicesView adChoicesView = new AdChoicesView(getActivity(), nativeAd, true);
            adChoicesContainer.addView(adChoicesView, 0);

            // Create native UI using the ad metadata.
            AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
            TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
            MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
            TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
            TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
            TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
            Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

            // Set the Text.
            nativeAdTitle.setText(nativeAd.getAdvertiserName());
            nativeAdBody.setText(nativeAd.getAdBodyText());
            nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
            nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
            nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
            sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

            // Create a list of clickable views
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(nativeAdTitle);
            clickableViews.add(nativeAdCallToAction);

            // Register the Title and CTA button to listen for clicks.
            nativeAd.registerViewForInteraction(
                    adView,
                    nativeAdMedia,
                    nativeAdIcon,
                    clickableViews);
        } catch (Exception e) {
            Log.d("Error", "facebook Add");
        }
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
