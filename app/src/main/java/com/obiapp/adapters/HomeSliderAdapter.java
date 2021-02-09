package com.obiapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import com.obiapp.R;
import com.obiapp.databinding.SliderRowBinding;
import com.obiapp.models.SliderModel;
import com.obiapp.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeSliderAdapter extends PagerAdapter {
    private List<SliderModel.Data> list ;
    private Context context;
    private LayoutInflater inflater;

    public HomeSliderAdapter(List<SliderModel.Data> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        SliderRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.slider_row,container,false);
        Picasso.get().load(Uri.parse(Tags.IMAGE_URL+list.get(position).getImage())).into(binding.image);
        container.addView(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
