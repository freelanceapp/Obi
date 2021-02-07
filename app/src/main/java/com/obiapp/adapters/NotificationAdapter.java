package com.obiapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.obiapp.R;
import com.obiapp.activities_fragments.activity_notification.NotificationActivity;
import com.obiapp.databinding.NotificationRowBinding;
import com.obiapp.models.NotificationModel;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<NotificationModel> list;
    private Context context;
    private LayoutInflater inflater;
    private NotificationActivity activity;


    public NotificationAdapter(List<NotificationModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (NotificationActivity) context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        NotificationRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.notification_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder){
            MyHolder myHolder = (MyHolder) holder;
            NotificationModel model = list.get(position);
            myHolder.binding.setModel(model);


            myHolder.binding.tvDelete.setOnClickListener(view -> {
                NotificationModel model2 = list.get(myHolder.getAdapterPosition());
                activity.setItemData(model2,myHolder.getAdapterPosition());

            });

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        private NotificationRowBinding binding;

        public MyHolder(NotificationRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;


        }

    }



}
