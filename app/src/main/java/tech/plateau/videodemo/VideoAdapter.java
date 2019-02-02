package tech.plateau.videodemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoHolder> {

    private Context mContext;
    private List<VideoInfo> videoInfos;
    private DataSource.Factory dataSourceFactory;
    private ExtractorMediaSource.Factory mediaSourceFactory;

    public VideoAdapter(Context context) {
        mContext = context;
        // Produces DataSource instances through which media data is loaded.
        dataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, "VideoDemo"));
        // This is the MediaSource representing the media to be played.
        mediaSourceFactory = new ExtractorMediaSource.Factory(dataSourceFactory);
    }

    public void setData(List<VideoInfo> videoInfos) {
        this.videoInfos = videoInfos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VideoHolder(viewGroup, mOnPlayListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoHolder viewHolder, int i) {
        viewHolder.bind(videoInfos.get(i), mediaSourceFactory);
    }

    @Override
    public int getItemCount() {
        if (videoInfos == null) {
            return 0;
        }
        return videoInfos.size();
    }

    private OnPlayListener mOnPlayListener;

    public void setOnPlayListener(OnPlayListener onPlayListener) {
        mOnPlayListener = onPlayListener;
    }
}
