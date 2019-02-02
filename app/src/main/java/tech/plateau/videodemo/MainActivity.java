package tech.plateau.videodemo;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnPlayListener {

    private RecyclerView rv;
    private VideoAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private SimpleExoPlayer mSimpleExoPlayer; // only one ExoPlayer

    public static boolean isAutoPlay = true; // Auto play means it will auto play when you scroll.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv);

        initPlayer();
        initRV();
        initData();
        initListeners();
    }

    private void initPlayer() {
        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);
    }

    private void initRV() {
        mAdapter = new VideoAdapter(this);
        mAdapter.setOnPlayListener(this);
        rv.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);
    }

    private void initData() {
        ArrayList<VideoInfo> list = new ArrayList<>();
        // TODO: Insert your data here. FirstFrame can be null
        mAdapter.setData(list);
    }

    private float minPercent = 0.6f; // the minimum percent of visible area so that video can be played
    private int lastVideoHolderPos = -1; // the last video holder position which can play

    private void initListeners() {
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstPos = mLayoutManager.findFirstVisibleItemPosition();
                int lastPos = mLayoutManager.findLastVisibleItemPosition();

                if (lastVideoHolderPos != -1) {
                    VideoHolder lastVideoHolder = getVideoHolderByPos(lastVideoHolderPos);
                    if ((firstPos > lastVideoHolderPos || lastVideoHolderPos > lastPos)
                            || ((lastVideoHolder != null) && getVisiblePercentOfView(lastVideoHolder.pv) < minPercent)) {
                        if (lastVideoHolder != null) {
                            // once the visible percent is below minPercent, stop playback
                            lastVideoHolder.stop(mSimpleExoPlayer);
                            lastVideoHolderPos = -1;
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int firstPos = mLayoutManager.findFirstVisibleItemPosition();
                int lastPos = mLayoutManager.findLastVisibleItemPosition();

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    for (int i = firstPos; i <= lastPos; i++) {
                        VideoHolder videoHolder = getVideoHolderByPos(i);
                        if (videoHolder != null) {
                            float percent = getVisiblePercentOfView(videoHolder.pv);
                            if (percent >= minPercent && lastVideoHolderPos == -1) {
                                // after IDLE, if there's no video is playing,
                                // find the first playerView that is visible
                                videoHolder.play(mSimpleExoPlayer);
                                lastVideoHolderPos = i;
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    private VideoHolder getVideoHolderByPos(int pos) {
        RecyclerView.ViewHolder vh = rv.findViewHolderForAdapterPosition(pos);
        if (vh instanceof VideoHolder) {
            return (VideoHolder) vh;
        }
        return null;
    }

    /**
     * Get the percent of visible area of a view.
     * @param view any view
     * @return
     */
    public float getVisiblePercentOfView(View view) {
        Rect drawRect = new Rect();
        view.getDrawingRect(drawRect);
        long drawArea = drawRect.width() * drawRect.height();

        Rect realRect = new Rect();
        boolean visible = view.getGlobalVisibleRect(realRect);
        long realArea = realRect.width() * realRect.height();

        if (visible) {
            return realArea * 1.0f / drawArea;
        } else {
            return 0f;
        }
    }

    /**
     * this method can be triggered if you clicked a play btn.
     */
    @Override
    public void onPlay(int adapterPosition) {
        VideoHolder videoHolder = getVideoHolderByPos(adapterPosition);
        if (videoHolder != null) {
            if (getVisiblePercentOfView(videoHolder.pv) >= minPercent) {
                // stop the others
                if (lastVideoHolderPos != -1) {
                    VideoHolder lastVideoHolder = getVideoHolderByPos(lastVideoHolderPos);
                    if (lastVideoHolder != null) {
                        lastVideoHolder.stop(mSimpleExoPlayer);
                        lastVideoHolderPos = -1;
                    }
                }

                videoHolder.play(mSimpleExoPlayer);
                lastVideoHolderPos = adapterPosition;
            }
        }
    }
}
