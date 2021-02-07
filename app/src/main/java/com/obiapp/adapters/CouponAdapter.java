package com.obiapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.obiapp.R;
import com.obiapp.activities_fragments.activity_my_coupon.MyCouponsActivity;
import com.obiapp.databinding.CouponRowBinding;
import com.obiapp.databinding.OfferRowBinding;
import com.obiapp.models.CouponModel;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CouponModel> list;
    private Context context;
    private LayoutInflater inflater;
    private MyCouponsActivity activity;

    public CouponAdapter(List<CouponModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (MyCouponsActivity) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CouponRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.coupon_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));
            myHolder.binding.imageCopy.setOnClickListener(view -> {
                String couponCode = list.get(myHolder.getAdapterPosition()).getCoupon_code();
                activity.copy(couponCode);
            });

            myHolder.itemView.setOnClickListener(view -> {
                CouponModel couponModel= list.get(myHolder.getAdapterPosition());
                activity.setItemData(couponModel);
            });

            myHolder.binding.imageDelete.setOnClickListener(view -> {
                CouponModel couponModel= list.get(myHolder.getAdapterPosition());
                activity.deleteCoupon(couponModel.getId(),myHolder.getAdapterPosition());

            });



        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public CouponRowBinding binding;

        public MyHolder(@NonNull CouponRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
