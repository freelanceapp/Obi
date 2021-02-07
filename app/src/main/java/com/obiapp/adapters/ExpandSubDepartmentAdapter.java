package com.obiapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.obiapp.R;
import com.obiapp.activities_fragments.activity_home.HomeActivity;
import com.obiapp.databinding.ExpandSubDepartmentRowBinding;
import com.obiapp.databinding.OfferRowBinding;
import com.obiapp.models.DepartmentModel;

import java.util.List;

public class ExpandSubDepartmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DepartmentModel.SubCategory> list;
    private Context context;
    private LayoutInflater inflater;
    private HomeActivity activity;
    private int parent_pos;

    public ExpandSubDepartmentAdapter(List<DepartmentModel.SubCategory> list, Context context,int parent_pos) {
        this.list = list;
        this.context = context;
        this.parent_pos = parent_pos;
        inflater = LayoutInflater.from(context);
        activity = (HomeActivity) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ExpandSubDepartmentRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.expand_sub_department_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));
            myHolder.itemView.setOnClickListener(view -> {
                DepartmentModel.SubCategory subCategory  = list.get(myHolder.getAdapterPosition());
                activity.setItemSubDepartmentData(subCategory,parent_pos,myHolder.getAdapterPosition());
            });

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public ExpandSubDepartmentRowBinding binding;

        public MyHolder(@NonNull ExpandSubDepartmentRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
