package com.obiapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.obiapp.R;
import com.obiapp.models.ProductImageModel;
import com.obiapp.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapter extends PagerAdapter {
    private List<ProductImageModel> list;
    private Context context;
    private LayoutInflater inflater;
    private SimpleExoPlayer player;
    private int currentWindow = 0;
    private long currentPosition = 0;
    private boolean playWhenReady = false;
    private PlayerView playerView;
    private ProgressBar progressBar;


    public SliderAdapter(List<ProductImageModel> list, Context context) {
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
        View view = null;
        ProductImageModel model = list.get(position);

        if (model.getType().equals("video")){
            view = inflater.inflate(R.layout.slider_video,container,false);
            playerView = view.findViewById(R.id.player);
            progressBar = view.findViewById(R.id.progBarBuffering);
            Uri uri = Uri.parse(Tags.IMAGE_URL+model.getName());
            initPlayer(playerView,progressBar,uri);

        }else {
            view = inflater.inflate(R.layout.slider_image,container,false);
            ImageView imageView = view.findViewById(R.id.image);
            Picasso.get().load(Uri.parse(Tags.IMAGE_URL+model.getName())).into(imageView);

            Log.e("image",Tags.IMAGE_URL+model.getName());
        }

        container.addView(view);



        return view;
    }




    private void initPlayer(PlayerView playerView, ProgressBar progBarBuffering, Uri uri)
    {
        if (player == null) {
            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            trackSelector.setParameters(trackSelector.buildUponParameters().setMaxVideoSizeSd());
            player = ExoPlayerFactory.newSimpleInstance(context);
            playerView.setPlayer(player);
            MediaSource mediaSource = buildMediaSource(uri);

            player.seekTo(currentWindow, currentPosition);
            player.setPlayWhenReady(playWhenReady);
            player.prepare(mediaSource);
        } else {
            currentWindow = 0;
            currentPosition = 0;
            MediaSource mediaSource = buildMediaSource(uri);

            player.seekTo(currentWindow, currentPosition);
            player.setPlayWhenReady(playWhenReady);
            player.prepare(mediaSource);
        }

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady) {
                    if (playbackState == Player.STATE_BUFFERING) {
                        progBarBuffering.setVisibility(View.VISIBLE);
                    }else if (playbackState==Player.STATE_READY){
                        progBarBuffering.setVisibility(View.GONE);

                    }else if (playbackState == Player.STATE_ENDED) {
                        progBarBuffering.setVisibility(View.GONE);
                        currentWindow = 0;
                        currentPosition = 0;
                    } else if (playbackState == Player.TIMELINE_CHANGE_REASON_RESET){
                       progBarBuffering.setVisibility(View.VISIBLE);

                    }else {
                        progBarBuffering.setVisibility(View.GONE);

                    }
                }
            }


        });


    }
    private MediaSource buildMediaSource(Uri uri)
    {
        String userAgent = "exoPlayer_elebel";


        if (uri.getLastPathSegment().contains("mp3") || uri.getLastPathSegment().contains("mp4")) {
            return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);
        } else if (uri.getLastPathSegment().contains("m3u8")) {

            return new HlsMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);
        } else {

            DefaultDashChunkSource.Factory factory = new DefaultDashChunkSource.Factory(new DefaultHttpDataSourceFactory(userAgent));

            return new DashMediaSource.Factory(factory, new DefaultDataSourceFactory(context, userAgent)).createMediaSource(uri);
        }

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ProductImageModel model = list.get(position);
        if (model.getType().equals("video")){
            progressBar.setVisibility(View.GONE);
            releasePlayer();
        }
        container.removeView((View) object);
    }

    public void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            currentWindow = player.getCurrentWindowIndex();
            currentPosition = player.getCurrentPosition();
            player.stop();

        }
    }
}
