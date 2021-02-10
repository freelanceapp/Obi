package com.obiapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.obiapp.R;
import com.obiapp.activities_fragments.activity_home.HomeActivity;
import com.obiapp.activities_fragments.activity_home.fragments.Fragment_Home;
import com.obiapp.databinding.CatogryRowBinding;
import com.obiapp.models.SingleCategoryModel;
import com.obiapp.tags.Tags;

import java.util.List;

import io.paperdb.Paper;

public class Category_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SingleCategoryModel> singleCategoryModelList;
    private Context context;
    private LayoutInflater inflater;
    private String lang;
    private Fragment fragment;
    private HomeActivity homeactivity;

    public Category_Adapter(List<SingleCategoryModel> singleCategoryModelList, Context context, Fragment fragment) {
        this.singleCategoryModelList = singleCategoryModelList;
        this.context = context;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
        lang = Paper.book().read("lang", java.util.Locale.getDefault().getLanguage());
        this.fragment = fragment;
    }

    @androidx.annotation.NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {


        CatogryRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.catogry_row, parent, false);
        return new EventHolder(binding);


    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull RecyclerView.ViewHolder holder, int position) {

        EventHolder eventHolder = (EventHolder) holder;
        eventHolder.binding.setCategorymodel(singleCategoryModelList.get(position));
        Log.e("dldkldk", Tags.IMAGE_URL+singleCategoryModelList.get(position).getImage());
        eventHolder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (fragment instanceof Fragment_Home) {
                    Fragment_Home fragment_home = (Fragment_Home) fragment;
             //       fragment_home.showdata(singleCategoryModelList.get(eventHolder.getLayoutPosition()));
                }
            }
        });
/*
if(i==position){
    if(i!=0) {
        if (((EventHolder) holder).binding.expandLayout.isExpanded()) {
            ((EventHolder) holder).binding.tvTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ((EventHolder) holder).binding.recView.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
            ((EventHolder) holder).binding.expandLayout.collapse(true);
            ((EventHolder) holder).binding.expandLayout.setVisibility(View.GONE);



        }
        else {

          //  ((EventHolder) holder).binding.tvTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ((EventHolder) holder).binding.recView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            ((EventHolder) holder).binding.expandLayout.setVisibility(View.VISIBLE);

           ((EventHolder) holder).binding.expandLayout.expand(true);
        }
    }
    else {
        eventHolder.binding.tvTitle.setBackground(activity.getResources().getDrawable(R.drawable.linear_bg_green));

        ((EventHolder) holder).binding.tvTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ((EventHolder) holder).binding.recView.setLayoutParams(new FrameLayout.LayoutParams(0, 0));

    }
}
if(i!=position) {
    eventHolder.binding.tvTitle.setBackground(activity.getResources().getDrawable(R.drawable.linear_bg_white));
    ((EventHolder) holder).binding.tvTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

    ((EventHolder) holder).binding.recView.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
    ((EventHolder) holder).binding.expandLayout.collapse(true);


}*/

    }

    @Override
    public int getItemCount() {
        return singleCategoryModelList.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        public CatogryRowBinding binding;

        public EventHolder(@androidx.annotation.NonNull CatogryRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
