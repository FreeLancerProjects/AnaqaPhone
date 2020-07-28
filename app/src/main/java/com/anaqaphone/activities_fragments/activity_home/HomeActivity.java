package com.anaqaphone.activities_fragments.activity_home;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_home.fragments.Fragment_Cart;
import com.anaqaphone.activities_fragments.activity_home.fragments.Fragment_Department;
import com.anaqaphone.activities_fragments.activity_home.fragments.Fragment_Main;
import com.anaqaphone.activities_fragments.activity_home.fragments.Fragment_More;
import com.anaqaphone.activities_fragments.activity_home.fragments.Fragment_Offer;
import com.anaqaphone.activities_fragments.activity_login.LoginActivity;
import com.anaqaphone.activities_fragments.activity_notification.NotificationActivity;
import com.anaqaphone.activities_fragments.activity_search.SearchActivity;
import com.anaqaphone.databinding.ActivityHomeBinding;
import com.anaqaphone.language.Language;
import com.anaqaphone.models.NotFireModel;
import com.anaqaphone.models.NotificationCount;
import com.anaqaphone.models.UserModel;
import com.anaqaphone.preferences.Preferences;
import com.anaqaphone.remote.Api;
import com.anaqaphone.share.Common;
import com.anaqaphone.singleton.CartSingleton;
import com.anaqaphone.tags.Tags;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.google.firebase.iid.FirebaseInstanceId;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private Preferences preferences;
    private FragmentManager fragmentManager;
    private Fragment_Main fragment_main;
    private Fragment_Cart fragment_cart;
    private Fragment_Offer fragment_offer;
    private Fragment_More fragment_more;
    private Fragment_Department fragment_department;
    private UserModel userModel;
    private String lang;
    private String token;
    private CartSingleton singleton;
    private int back = 0;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initView();


    }

    private void initView() {
        fragmentManager = getSupportFragmentManager();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        setUpBottomNavigation();


        binding.flSearch.setOnClickListener(view -> {

            Intent intent = new Intent(this, SearchActivity.class);
            startActivityForResult(intent, 100);
            // displayFragmentSearch();

        });


        binding.flNotification.setOnClickListener(view -> {


            if (userModel != null) {
                readNotificationCount();
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);

            } else {
                Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));
            }

        });


        if (userModel != null) {
            EventBus.getDefault().register(this);
            getNotificationCount();
            updateTokenFireBase();

        }


    }


    private void setUpBottomNavigation() {

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getString(R.string.home), R.drawable.ic_home);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getString(R.string.department), R.drawable.ic_list);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getString(R.string.offers), R.drawable.ic_tag);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getString(R.string.cart), R.drawable.ic_cart);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem(getString(R.string.more), R.drawable.ic_more);

        binding.ahBottomNav.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        binding.ahBottomNav.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.second));
        binding.ahBottomNav.setTitleTextSizeInSp(13, 13);
        binding.ahBottomNav.setForceTint(true);
        binding.ahBottomNav.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimary));
        binding.ahBottomNav.setInactiveColor(ContextCompat.getColor(this, R.color.white));

        binding.ahBottomNav.addItem(item1);
        binding.ahBottomNav.addItem(item2);
        binding.ahBottomNav.addItem(item3);
        binding.ahBottomNav.addItem(item4);
        binding.ahBottomNav.addItem(item5);


        binding.ahBottomNav.setOnTabSelectedListener((position, wasSelected) -> {
            switch (position) {
                case 0:
                    displayFragmentMain();
                    break;
                case 1:

                    displayFragmentDepartment();


                    break;
                case 2:

                    displayFragmentOffer();


                    break;
                case 3:
                    displayFragmentCart();
                    break;
                case 4:
                    displayFragmentMore();
                    break;
            }
            return false;
        });

        updateBottomNavigationPosition(0);
        displayFragmentMain();

    }

    public void updateBottomNavigationPosition(int pos) {

        binding.ahBottomNav.setCurrentItem(pos, false);

    }


    private void getNotificationCount() {
        Api.getService(Tags.base_url)
                .getUnreadNotificationCount(userModel.getUser().getToken())
                .enqueue(new Callback<NotificationCount>() {
                    @Override
                    public void onResponse(Call<NotificationCount> call, Response<NotificationCount> response) {
                        if (response.isSuccessful()) {
                            binding.setNotCount(response.body().getCount());
                        } else {
                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationCount> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private void readNotificationCount() {
        binding.setNotCount(0);

        /*Api.getService(Tags.base_url)
                .readNotification(userModel.getUser().getToken())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            binding.setNotCount(0);
                        } else {
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });*/
    }


    public void displayFragmentMain() {
        try {
            if (fragment_main == null) {
                fragment_main = Fragment_Main.newInstance();
            }


            if (fragment_cart != null && fragment_cart.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_cart).commit();
            }
            if (fragment_department != null && fragment_department.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_department).commit();
            }
            if (fragment_offer != null && fragment_offer.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_offer).commit();
            }

            if (fragment_more != null && fragment_more.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_more).commit();
            }
            if (fragment_main.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_main).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_main, "fragment_main").addToBackStack("fragment_main").commit();

            }
            binding.setTitle(getString(R.string.home));
            updateBottomNavigationPosition(0);
        } catch (Exception e) {
        }

    }

    public void displayFragmentCart() {
        try {
            if (fragment_cart == null) {
                fragment_cart = Fragment_Cart.newInstance();
            } else {
                fragment_cart.updateUI();
            }

            if (fragment_more != null && fragment_more.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_more).commit();
            }
            if (fragment_department != null && fragment_department.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_department).commit();
            }
            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }

            if (fragment_offer != null && fragment_offer.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_offer).commit();
            }
            if (fragment_cart.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_cart).commit();
            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_cart, "fragment_cart").addToBackStack("fragment_cart").commit();

            }
            binding.setTitle(getString(R.string.cart));
            updateBottomNavigationPosition(3);
        } catch (Exception e) {
        }

    }

    public void displayFragmentOffer() {

        try {
            if (fragment_offer == null) {
                fragment_offer = Fragment_Offer.newInstance();
            }


            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }
            if (fragment_department != null && fragment_department.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_department).commit();
            }

            if (fragment_more != null && fragment_more.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_more).commit();
            }

            if (fragment_cart != null && fragment_cart.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_cart).commit();
            }

            if (fragment_offer.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_offer).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_offer, "fragment_offer").addToBackStack("fragment_offer").commit();

            }
            binding.setTitle(getString(R.string.offers));
            updateBottomNavigationPosition(2);
        } catch (Exception e) {
        }
    }


    public void displayFragmentMore() {

        try {
            if (fragment_more == null) {
                fragment_more = Fragment_More.newInstance();
            }


            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }
            if (fragment_department != null && fragment_department.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_department).commit();
            }

            if (fragment_offer != null && fragment_offer.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_offer).commit();
            }

            if (fragment_cart != null && fragment_cart.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_cart).commit();
            }

            if (fragment_more.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_more).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_more, "fragment_more").addToBackStack("fragment_more").commit();

            }
            binding.setTitle(getString(R.string.more));
            updateBottomNavigationPosition(4);
        } catch (Exception e) {
        }
    }


    public void displayFragmentDepartment() {

        try {
            if (fragment_department == null) {
                fragment_department = Fragment_Department.newInstance();
            }


            if (fragment_main != null && fragment_main.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_main).commit();
            }
            if (fragment_more != null && fragment_more.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_more).commit();
            }

            if (fragment_offer != null && fragment_offer.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_offer).commit();
            }

            if (fragment_cart != null && fragment_cart.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_cart).commit();
            }

            if (fragment_department.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_department).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_department, "fragment_department").addToBackStack("fragment_department").commit();

            }
            binding.setTitle(getString(R.string.department));
            updateBottomNavigationPosition(1);
        } catch (Exception e) {
        }
    }


    private void updateTokenFireBase() {

        FirebaseInstanceId.getInstance()
                .getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                token = task.getResult().getToken();

                try {
                    Log.e("llll", userModel.getUser().getToken());
                    Api.getService(Tags.base_url)
                            .updatePhoneToken(userModel.getUser().getToken(), token, "android")
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Log.e("token", "updated successfully");
                                    } else {
                                        try {

                                            Log.e("errorToken", response.code() + "_" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    try {

                                        if (t.getMessage() != null) {
                                            Log.e("errorToken2", t.getMessage());
                                            if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                                Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    } catch (Exception e) {
                                    }
                                }
                            });
                } catch (Exception e) {


                }

            }
        });
    }


    public void logout() {
        if (userModel != null) {


            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.show();


            FirebaseInstanceId.getInstance()
                    .getInstanceId().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    token = task.getResult().getToken();

                    Api.getService(Tags.base_url)
                            .logout(userModel.getUser().getToken(), token, "android")
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    dialog.dismiss();
                                    if (response.isSuccessful()) {
                                        Log.e("dd", "ddd");
                                        preferences.clear(HomeActivity.this);
                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        if (manager != null) {
                                            manager.cancel(Tags.not_tag, Tags.not_id);
                                        }
                                        navigateToSignInActivity();


                                    } else {
                                        dialog.dismiss();
                                        try {
                                            Log.e("error", response.code() + "__" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        if (response.code() == 500) {
                                            Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    try {
                                        dialog.dismiss();
                                        if (t.getMessage() != null) {
                                            Log.e("error", t.getMessage() + "__");

                                            if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                                Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e("Error", e.getMessage() + "__");
                                    }
                                }
                            });

                }
            });


        } else {
            navigateToSignInActivity();
        }

    }


    public void refreshActivity(String lang) {
        Paper.book().write("lang", lang);
        Language.setNewLocale(this, lang);
        new Handler()
                .postDelayed(() -> {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }, 1050);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenToNotifications(NotFireModel notFireModel) {
        if (userModel != null) {
            getNotificationCount();

        }
    }

    @Override
    public void onBackPressed() {

        if (fragment_main != null && fragment_main.isAdded() && fragment_main.isVisible()) {
            if(userModel!=null){
            if (singleton.getItemCartModelList() != null && singleton.getItemCartModelList().size() > 0) {
                if (back == 0) {
                    back = 1;
                    String sound_Path = "android.resource://" + getPackageName() + "/" + R.raw.not;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        String CHANNEL_ID = "my_channel_02";
                        CharSequence CHANNEL_NAME = "my_channel_name";
                        int IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

                        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
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
                        builder.setSmallIcon(R.drawable.logo);


                        builder.setContentTitle(getResources().getString(R.string.cart));


                        builder.setContentText(getResources().getString(R.string.cart_not_empty));


                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_only);
                        builder.setLargeIcon(bitmap);
                        manager.createNotificationChannel(channel);
                        manager.notify(new Random().nextInt(200), builder.build());
                    } else {

                        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

                        builder.setSound(Uri.parse(sound_Path), AudioManager.STREAM_NOTIFICATION);
                        builder.setSmallIcon(R.drawable.logo);

                        builder.setContentTitle(getResources().getString(R.string.cart));


                        builder.setContentText(getResources().getString(R.string.cart_not_empty));


                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_only);
                        builder.setLargeIcon(bitmap);
                        manager.notify(new Random().nextInt(200), builder.build());

                    }
                } else {
                   // back=1;
                    CreatecloseDialog();

                }
            }
           else {
                finish();
            }}
            else {
                navigateToSignInActivity();
            }
        } else {
            displayFragmentMain();
        }
    }

    private void CreatecloseDialog() {

        final androidx.appcompat.app.AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_delete, null);
        TextView tvDelete = view.findViewById(R.id.tvDelete);
        TextView tvCancel = view.findViewById(R.id.tvCancel);
        TextView tvtitle = view.findViewById(R.id.tvtitle);
        TextView tvcart = view.findViewById(R.id.tvcart);

        tvDelete.setText(getResources().getString(R.string.back));
        tvcart.setText(getResources().getString(R.string.cart));
        tvtitle.setText(getResources().getString(R.string.if_you));
        tvCancel.setOnClickListener(v -> {
            back = 0;
            dialog.dismiss();

        });

        tvDelete.setOnClickListener(v -> {
            try {
                dialog.dismiss();
                finish();
            } catch (Exception e) {
            }

            dialog.dismiss();
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setView(view);
        dialog.show();

    }

    private void navigateToSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void updateCartCount(int count) {


        if (count == 0) {
            AHNotification notification = new AHNotification.Builder()
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
                    .setTextColor(ContextCompat.getColor(this, R.color.white))
                    .setText(null)
                    .build();
            binding.ahBottomNav.setNotification(notification, 3);

        } else {
            AHNotification notification = new AHNotification.Builder()
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
                    .setTextColor(ContextCompat.getColor(this, R.color.white))
                    .setText(String.valueOf(count))
                    .build();
            binding.ahBottomNav.setNotification(notification, 3);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        singleton = CartSingleton.newInstance();
        if (singleton.getItemCartModelList() != null) {
            updateCartCount(singleton.getItemCount());
        }

    }
}
