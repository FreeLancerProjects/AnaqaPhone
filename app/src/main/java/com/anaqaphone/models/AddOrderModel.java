package com.anaqaphone.models;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.databinding.library.baseAdapters.BR;

import com.anaqaphone.R;

import java.io.Serializable;

public class AddOrderModel extends BaseObservable implements Serializable {

    private String address;
    private double lat;
    private double lng;
    private long date;
    private long time;
    private String payment_type;
    public ObservableField<String> error_address = new ObservableField<>();



    public AddOrderModel() {
        setAddress("");
        setLat(0.0);
        setLat(0.0);
        setDate(0);
        setTime(0);

        setPayment_type("");
    }

    public boolean isStep1Valid(Context context)
    {
        if (!address.trim().isEmpty())
        {
            error_address.set(null);
            return true;
        }else {
            error_address.set(context.getString(R.string.field_required));
            return false;
        }
    }

    public boolean isStep2Valid(Context context)
    {
        if (date!=0&&time!=0)
        {
            return true;
        }else {

            if (date==0)
            {
                Toast.makeText(context, R.string.ch_date, Toast.LENGTH_SHORT).show();
            }

            if (time==0)
            {
                Toast.makeText(context, R.string.ch_time, Toast.LENGTH_SHORT).show();

            }
            return false;
        }
    }

    public boolean isStep3Valid(Context context)
    {
        if (!payment_type.isEmpty())
        {
            return true;
        }else {
            Toast.makeText(context, R.string.ch_payment_type, Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }




    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }
}
