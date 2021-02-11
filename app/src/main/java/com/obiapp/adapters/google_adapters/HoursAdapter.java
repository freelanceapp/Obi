package com.obiapp.adapters.google_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.obiapp.R;
import com.obiapp.databinding.WorkHourRowBinding;
import com.obiapp.models.google_models.HourModel;

import java.util.List;

public class HoursAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HourModel> list;
    private Context context;
    private LayoutInflater inflater;

    public HoursAdapter(List<HourModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        WorkHourRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.work_hour_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public WorkHourRowBinding binding;

        public MyHolder(@NonNull WorkHourRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
