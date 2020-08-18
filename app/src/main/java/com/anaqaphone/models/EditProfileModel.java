package com.anaqaphone.models;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.anaqaphone.R;

public class EditProfileModel extends BaseObservable {

    private String image_url;
    private String name;
    private String email;

    public ObservableField<String> error_name = new ObservableField<>();
    public ObservableField<String> error_email = new ObservableField<>();


    public boolean isDataValid(Context context) {

        Log.e("name", name);
        Log.e("email", email);


        if (!TextUtils.isEmpty(name) &&
                !TextUtils.isEmpty(email) &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
        ) {


            error_name.set(null);
            error_email.set(null);

            return true;
        } else {


            if (name.isEmpty()) {
                error_name.set(context.getString(R.string.field_required));
            } else {
                error_name.set(null);
            }

            if (email.isEmpty()) {
                error_email.set(context.getString(R.string.field_required));

            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                error_email.set(context.getString(R.string.inv_email));
            } else {
                error_email.set(null);
            }


            return false;
        }
    }

    public EditProfileModel() {

        this.name = "";
        notifyPropertyChanged(BR.name);
        this.email = "";
        notifyPropertyChanged(BR.email);
        this.image_url = "";


    }


    @Bindable
    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);

    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);

    }


}
