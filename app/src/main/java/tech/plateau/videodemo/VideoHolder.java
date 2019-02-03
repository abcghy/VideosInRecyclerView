package tech.plateau.videodemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.File;

public class VideoHolder extends RecyclerView.ViewHolder {

    public PlayerView pv;
    private ImageView iv;
    private ProgressBar pb;
    private ImageView ivPlayBtn;
    private Context mContext;
    private VideoInfo mVideoInfo;
    private AdsMediaSource.MediaSourceFactory mMediaSourceFactory;

    private OnPlayListener mOnPlayListener;

    public VideoHolder(@NonNull ViewGroup parent, OnPlayListener onPlayListener, AdsMediaSource.MediaSourceFactory mediaSourceFactory) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false));
        mContext = itemView.getContext();

        mOnPlayListener = onPlayListener;
        mMediaSourceFactory = mediaSourceFactory;

        pv = itemView.findViewById(R.id.pv);
        iv = itemView.findViewById(R.id.iv);
        pb = itemView.findViewById(R.id.pb);
        pb.setVisibility(View.GONE);
        ivPlayBtn = itemView.findViewById(R.id.ivPlayBtn);

        pv.setUseController(false);

        ivPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPlayListener != null) {
                    mOnPlayListener.onPlay(getAdapterPosition());
                }
            }
        });
    }

    public void bind(VideoInfo videoInfo, ExtractorMediaSource.Factory mediaSourceFactory) {
        if (videoInfo.getWidth() == 0 || videoInfo.getHeight() == 0) {
            return;
        }

        mVideoInfo = videoInfo;

        // PlayerView's bounds.
        float widthHeightRatio = videoInfo.getWidth() * 1.0f / videoInfo.getHeight();
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams lp = pv.getLayoutParams();
        lp.width = width;
        lp.height = (int) (width / widthHeightRatio);
        pv.setLayoutParams(lp);

        // the source for ExoPlayer
//        videoSource = mediaSourceFactory.createMediaSource(Uri.parse(videoInfo.getUrl()));

        // PlaceHolder's bounds.
        ViewGroup.LayoutParams ivLp = iv.getLayoutParams();
        ivLp.width = width;
        ivLp.height = (int) (width / widthHeightRatio);
        iv.setLayoutParams(ivLp);

        // a default background placeholder color, so it won't be black(TextureView default color)
        // We can put it in VideoInfo later.
        iv.setBackgroundColor(Color.parseColor("#EFEFEF"));
        Glide.with(mContext.getApplicationContext())
                .load(videoInfo.getFirstFrame())
                .into(iv);
    }

    @SuppressLint("CheckResult")
    public void play(final ExoPlayer exoPlayer, boolean isAutoPlayByClick) {
        if (mVideoInfo == null || mVideoInfo.getUrl() == null) {
            return;
        }
        pv.setPlayer(exoPlayer);
        exoPlayer.setPlayWhenReady(MainActivity.isAutoPlay || isAutoPlayByClick);

        RequestOptions requestOptions = new RequestOptions()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.DATA);

        // Use Glide to cache video. It's convenient. If you have some particular needs. Switch it.
        Glide.with(itemView)
                .load(Uri.parse(mVideoInfo.getUrl()))
                .apply(requestOptions)
                .downloadOnly(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        Log.d("test", "file: " + resource);
                        MediaSource mediaSource = mMediaSourceFactory.createMediaSource(Uri.fromFile(resource));
                        exoPlayer.prepare(mediaSource);
                    }
                });

        exoPlayer.addListener(mEventListener);

        ivPlayBtn.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
    }

    private Player.EventListener mEventListener = new Player.EventListener() {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_READY) {
                iv.setVisibility(View.GONE);
                pb.setVisibility(View.GONE);
            }

            if (playbackState == Player.STATE_ENDED) {
                iv.setVisibility(View.VISIBLE);
                ivPlayBtn.setVisibility(View.VISIBLE);
            }
        }
    };

    public void stop(ExoPlayer exoPlayer, boolean isStop) {
        exoPlayer.removeListener(mEventListener);
        if (isStop) {
            exoPlayer.stop();
        }
        exoPlayer.setPlayWhenReady(false);
        iv.setVisibility(View.VISIBLE);
        ivPlayBtn.setVisibility(View.VISIBLE);
        pv.setPlayer(null);
    }
}
