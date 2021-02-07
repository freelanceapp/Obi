package com.obiapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.obiapp.R;
import com.obiapp.activities_fragments.activity_home.HomeActivity;
import com.obiapp.databinding.ExpandDepartmentRowBinding;
import com.obiapp.databinding.OfferRowBinding;
import com.obiapp.models.DepartmentModel;

import java.util.List;

import io.paperdb.Paper;

public class ExpandDepartmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DepartmentModel> list;
    private Context context;
    private LayoutInflater inflater;
    private HomeActivity activity;
    private String lang;

    public ExpandDepartmentAdapter(List<DepartmentModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (HomeActivity) context;
        Paper.init(context);
        lang = Paper.book().read("lang","ar");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ExpandDepartmentRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.expand_department_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            DepartmentModel departmentModel = list.get(position);
            myHolder.binding.setModel(departmentModel);
            myHolder.binding.setLang(lang);
            myHolder.binding.recView.setLayoutManager(new LinearLayoutManager(context));
            ExpandSubDepartmentAdapter adapter = new ExpandSubDepartmentAdapter(departmentModel.getSub_categories(),context,position);
            myHolder.binding.recView.setAdapter(adapter);
            myHolder.itemView.setOnClickListener(view -> {
                if (myHolder.binding.expandLayout.isExpanded()){
                    myHolder.binding.expandLayout.collapse(true);
                }else {
                    myHolder.binding.expandLayout.expand(true);

                }
            });



        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public ExpandDepartmentRowBinding binding;

        public MyHolder(@NonNull ExpandDepartmentRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
