package com.obiapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.obiapp.R;
import com.obiapp.databinding.SpinnerRowBinding;
import com.obiapp.models.DepartmentModel;

import java.util.List;

public class SpinnerDepartmentAdapter extends BaseAdapter {
    private List<DepartmentModel> list;
    private Context context;
    private LayoutInflater inflater;

    public SpinnerDepartmentAdapter(List<DepartmentModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") SpinnerRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.spinner_row,parent,false);
        String title = list.get(position).getTitle();
        binding.setTitle(title);
        return binding.getRoot();
    }
}
