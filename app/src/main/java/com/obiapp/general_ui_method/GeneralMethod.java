package com.obiapp.general_ui_method;

import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.obiapp.R;
import com.obiapp.share.Time_Ago;
import com.obiapp.tags.Tags;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class GeneralMethod {

    @BindingAdapter("error")
    public static void errorValidation(View view, String error) {
        if (view instanceof EditText) {
            EditText ed = (EditText) view;
            ed.setError(error);
        } else if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setError(error);


        }
    }








    @BindingAdapter("image")
    public static void image(View view, String endPoint) {
        if (view instanceof CircleImageView) {
            CircleImageView imageView = (CircleImageView) view;
            if (endPoint != null) {

                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).into(imageView);
            }
        } else if (view instanceof RoundedImageView) {
            RoundedImageView imageView = (RoundedImageView) view;

            if (endPoint != null) {

                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).fit().into(imageView);
            }
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;

            if (endPoint != null) {

                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).fit().into(imageView);
            }
        }

    }

    @BindingAdapter("user_image")
    public static void user_image(View view, String endPoint) {
        if (view instanceof CircleImageView) {
            CircleImageView imageView = (CircleImageView) view;

            if (endPoint != null) {
                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).placeholder(R.drawable.ic_avatar).into(imageView);
            }
        } else if (view instanceof RoundedImageView) {
            RoundedImageView imageView = (RoundedImageView) view;
            if (endPoint != null) {
                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).fit().placeholder(R.drawable.ic_avatar).into(imageView);
            }
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            if (endPoint != null) {
                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).placeholder(R.drawable.ic_avatar).fit().into(imageView);
            }
        }

    }

    @BindingAdapter("date")
    public static void date(TextView view, String date) {
        if (date!=null&&!date.isEmpty()){
           String[] dates = date.split("T");
           view.setText(dates[0]);
        }

    }

    @BindingAdapter("date2")
    public static void date2(TextView view, long date2) {
        String d = Time_Ago.getTimeAgo(date2*1000,view.getContext());
        view.setText(d);
    }


}










