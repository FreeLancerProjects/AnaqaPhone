package com.anaqaphone.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.anaqaphone.R;
import com.anaqaphone.activities_fragments.activity_notification.NotificationActivity;
import com.anaqaphone.databinding.ClientNotificationRowBinding;
import com.anaqaphone.databinding.LoadmoreRowBinding;
import com.anaqaphone.models.NotificationDataModel;


import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int DATA_ROW =1;
    private final int LOAD_ROW =2;

    private List<NotificationDataModel.NotificationModel> list;
    private Context context;
    private LayoutInflater inflater;
    private NotificationActivity activity;

    public NotificationAdapter(List<NotificationDataModel.NotificationModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (NotificationActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == DATA_ROW){
            ClientNotificationRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.client_notification_row, parent, false);
            return new MyHolder(binding);
        }else {
            LoadmoreRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.loadmore_row, parent, false);
            return new LoadMoreHolder(binding);
        }




    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder){
            MyHolder myHolder = (MyHolder) holder;

            myHolder.binding.setModel(list.get(position));

            myHolder.binding.imageDelete.setOnClickListener(v -> {
                NotificationDataModel.NotificationModel notificationModel = list.get(myHolder.getAdapterPosition());
                activity.setItemData(notificationModel,myHolder.getAdapterPosition());
            });



        }else {
            LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
            loadMoreHolder.binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            loadMoreHolder.binding.progBar.setIndeterminate(true);
        }





    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public ClientNotificationRowBinding binding;

        public MyHolder(@NonNull ClientNotificationRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;



        }
    }

    public class LoadMoreHolder extends RecyclerView.ViewHolder {
        public LoadmoreRowBinding binding;

        public LoadMoreHolder(@NonNull LoadmoreRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;



        }
    }


    @Override
    public int getItemViewType(int position) {
        NotificationDataModel.NotificationModel model = list.get(position);
        if (model ==null){
            return LOAD_ROW;
        }else {
            return DATA_ROW;
        }
    }
}