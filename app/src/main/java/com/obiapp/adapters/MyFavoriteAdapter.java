package com.obiapp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.obiapp.R;
import com.obiapp.activities_fragments.activity_my_favorite.MyFavoriteActivity;
import com.obiapp.databinding.ProductRow2Binding;
import com.obiapp.models.ProductModel;

import java.util.List;

public class MyFavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ProductModel> list;
    private Context context;
    private LayoutInflater inflater;
    private MyFavoriteActivity activity;

    public MyFavoriteAdapter(List<ProductModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (MyFavoriteActivity) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductRow2Binding binding = DataBindingUtil.inflate(inflater, R.layout.product_row2, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));
            myHolder.binding.tvOldPrice.setPaintFlags(myHolder.binding.tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            myHolder.itemView.setOnClickListener(view -> {
                MyFavoriteActivity myFavoriteActivity = (MyFavoriteActivity) activity;
                myFavoriteActivity.setProductItemData(list.get(myHolder.getAdapterPosition()),myHolder.getAdapterPosition());

            });

            myHolder.binding.imageDelete.setOnClickListener(view -> {
                MyFavoriteActivity myFavoriteActivity = (MyFavoriteActivity) activity;
                myFavoriteActivity.disLike(list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition());
            });


        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public ProductRow2Binding binding;

        public MyHolder(@NonNull ProductRow2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
