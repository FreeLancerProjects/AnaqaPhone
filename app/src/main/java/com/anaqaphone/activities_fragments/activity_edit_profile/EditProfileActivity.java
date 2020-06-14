package com.anaqaphone.activities_fragments.activity_edit_profile;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.anaqaphone.R;
import com.anaqaphone.databinding.ActivityEditProfileBinding;
import com.anaqaphone.interfaces.Listeners;
import com.anaqaphone.language.Language;
import com.anaqaphone.models.EditProfileModel;
import com.anaqaphone.models.UserModel;
import com.anaqaphone.preferences.Preferences;
import com.anaqaphone.remote.Api;
import com.anaqaphone.share.Common;
import com.anaqaphone.tags.Tags;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity implements Listeners.BackListener , Listeners.UpdateProfileListener {
    private ActivityEditProfileBinding binding;
    private String lang;
    private EditProfileModel editProfileModel;
    private Preferences preferences;
    private UserModel userModel;
    private Uri uri = null;
    private final String camera_perm = Manifest.permission.CAMERA;
    private final String write_perm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final int camera_req = 1;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        initView();

    }


    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setBackListener(this);
        editProfileModel = new EditProfileModel();
        binding.setUpdateListener(this);
        binding.setEditModel(editProfileModel);
        updateUI();

        binding.flImage.setOnClickListener(view -> checkCameraPermission());


    }


    private void updateUI()
    {
        binding.edtName.setText(userModel.getUser().getName());
        binding.edtEmail.setText(userModel.getUser().getEmail());

        editProfileModel.setName(userModel.getUser().getName());
        editProfileModel.setEmail(userModel.getUser().getEmail());


        if (userModel.getUser().getLogo()!=null&&!userModel.getUser().getLogo().isEmpty()&&!userModel.getUser().getLogo().equals("0"))
        {
            binding.iconUpload.setVisibility(View.GONE);
            Picasso.get().load(Uri.parse(Tags.IMAGE_URL+userModel.getUser().getLogo())).fit().into(binding.image);

        }


    }





    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, camera_perm) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, write_perm) == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{camera_perm, write_perm}, camera_req);
        }
    }

    private void selectImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, camera_req);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == camera_req && grantResults.length > 0) {
            boolean isGranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    isGranted = true;
                } else {
                    isGranted = false;
                }
            }
            if (isGranted) {
                selectImage();

            }
            else{
                Toast.makeText(this, "access images denied", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == camera_req && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            binding.iconUpload.setVisibility(View.GONE);
            binding.image.setImageBitmap(bitmap);
            uri = getUriFromBitmap(bitmap);
            binding.image.setImageBitmap(bitmap);

        }
    }



    private Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "", ""));
    }



    @Override
    public void updateProfile() {

        if (editProfileModel.isDataValid(this))
        {
            if(uri!=null)
            {
                updateWithImage();
            }else
            {
                updateWithoutImage();
            }
        }
    }


    private void updateWithoutImage()
    {

        final ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        RequestBody id_part = Common.getRequestBodyText(String.valueOf(userModel.getUser().getId()));
        RequestBody name_part = Common.getRequestBodyText(editProfileModel.getName());
        RequestBody email_part = Common.getRequestBodyText(editProfileModel.getEmail());


        try {
            Api.getService(Tags.base_url)
                    .editClientProfileWithoutImage(userModel.getUser().getToken(),name_part,email_part)
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                preferences.create_update_userdata(EditProfileActivity.this,response.body());
                                Intent intent = getIntent();
                                if (intent!=null)
                                {
                                    setResult(Activity.RESULT_OK,intent);
                                }
                                finish();

                            } else {

                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (response.code() == 500) {
                                    Toast.makeText(EditProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(EditProfileActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(EditProfileActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(EditProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
            dialog.dismiss();

        }
    }
    private void updateWithImage()
    {

        final ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        RequestBody id_part = Common.getRequestBodyText(String.valueOf(userModel.getUser().getId()));
        RequestBody name_part = Common.getRequestBodyText(editProfileModel.getName());
        RequestBody email_part = Common.getRequestBodyText(editProfileModel.getEmail());
        MultipartBody.Part image_part = Common.getMultiPart(this,uri,"logo");

        try {
            Api.getService(Tags.base_url)
                    .editClientProfileWithImage(userModel.getUser().getToken(),name_part,email_part,image_part)
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                preferences.create_update_userdata(EditProfileActivity.this,response.body());
                                Intent intent = getIntent();
                                if (intent!=null)
                                {
                                    setResult(Activity.RESULT_OK,intent);
                                }
                                finish();

                            } else {

                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (response.code() == 500) {
                                    Toast.makeText(EditProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(EditProfileActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(EditProfileActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(EditProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
            dialog.dismiss();

        }

    }


    @Override
    public void back() {
        finish();
    }
}
