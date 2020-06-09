package com.anaqaphone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.anaqaphone.R;
import com.anaqaphone.databinding.BankRowBinding;
import com.anaqaphone.models.BankDataModel;

import java.util.List;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.MyHolder> {

    private List<BankDataModel.BankModel> bankDataModelList;
    private Context context;

    public BankAdapter(List<BankDataModel.BankModel> bankDataModelList, Context context) {
        this.bankDataModelList = bankDataModelList;
        this.context = context;


    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BankRowBinding bankRowBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.bank_row,parent,false);
        return new MyHolder(bankRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {

        BankDataModel.BankModel bankModel = bankDataModelList.get(position);
        holder.bankRowBinding.setBankModel(bankModel);

    }

    @Override
    public int getItemCount() {
        return bankDataModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private BankRowBinding bankRowBinding;

        public MyHolder(BankRowBinding bankRowBinding) {
            super(bankRowBinding.getRoot());
            this.bankRowBinding = bankRowBinding;



        }


    }
}
