package com.sregg.android.tv.spotifyPlayer.views;

import android.content.Context;
import android.support.v17.leanback.widget.ImageCardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.squareup.otto.Subscribe;
import com.sregg.android.tv.spotifyPlayer.BusProvider;
import com.sregg.android.tv.spotifyPlayer.R;
import com.sregg.android.tv.spotifyPlayer.events.AbsPlayingEvent;
import com.sregg.android.tv.spotifyPlayer.events.OnPause;
import com.sregg.android.tv.spotifyPlayer.events.OnPlay;
import com.sregg.android.tv.spotifyPlayer.events.OnTrackChanged;
import com.sregg.android.tv.spotifyPlayer.events.PlayingState;
import com.sregg.android.tv.spotifyPlayer.utils.Utils;

import kaaes.spotify.webapi.android.models.TrackSimple;

/**
 * Created by simonreggiani on 15-02-04.
 */
public class SpotifyCardView extends FrameLayout {
    private ImageCardView mImageCardView;
    private View mBadgeView;
    private NowPlayingIndicatorView mNowPlayingView;
    private String mUri;
    private Object mItem;
    private Integer mSelectedInfoAreaBackgroundColor;
    private int mSelectedInfoAreaBacgroundDefaultColor;
    private int mInfoAreaBacgroundDefaultColor;

    public SpotifyCardView(Context context) {
        super(context);
        init();
    }

    public SpotifyCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpotifyCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SpotifyCardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.card_view, this);

        mImageCardView = (ImageCardView) findViewById(R.id.image_card_view);
        mBadgeView = findViewById(R.id.card_badge);
        mNowPlayingView = (NowPlayingIndicatorView) findViewById(R.id.card_now_playing);

        mSelectedInfoAreaBacgroundDefaultColor = getResources().getColor(R.color.card_info_area_selected_default);
        mInfoAreaBacgroundDefaultColor = getResources().getColor(R.color.card_info_area_default);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        BusProvider.unregister(this);
        super.onDetachedFromWindow();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onTrackChanged(OnTrackChanged onTrackChanged) {
        initNowPlaying(isSelf(onTrackChanged));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onPlay(OnPlay onPlay) {
        if (isSelf(onPlay)) {
            mNowPlayingView.startAnimation();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onPause(OnPause onPause) {
        if (isSelf(onPause)) {
            mNowPlayingView.stopAnimations();
        }
    }

    private boolean isSelf(AbsPlayingEvent playingEvent) {
        PlayingState playingState = playingEvent.getPlayingState();
        return mItem instanceof TrackSimple ? playingState.isCurrentTrack(mUri) : playingState.isCurrentObject(mUri);
    }

    public void initNowPlaying(boolean isSelf) {
        if (isSelf) {
            mBadgeView.setVisibility(VISIBLE);
            mNowPlayingView.setVisibility(VISIBLE);
            mNowPlayingView.startAnimation();
        } else {
            mBadgeView.setVisibility(GONE);
            mNowPlayingView.setVisibility(GONE);
            mNowPlayingView.stopAnimations();
        }
    }

    public int getImageSize() {
        return getResources().getDimensionPixelSize(R.dimen.card_image_view_size);
    }

    public ImageCardView getImageCardView() {
        return mImageCardView;
    }

    public View getBadgeView() {
        return mBadgeView;
    }

    public NowPlayingIndicatorView getNowPlayingView() {
        return mNowPlayingView;
    }


    public void setItem(Object item) {
        mItem = item;
        mUri = Utils.getUriFromSpotiyObject(item);
    }

    public void setSelectedInfoAreaBackgroundColor(Integer selectedInfoAreaBackgroundColor) {
        mSelectedInfoAreaBackgroundColor = selectedInfoAreaBackgroundColor;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        // change info area bg color based on palette color from image
        Integer color;
        if (selected) {
            color = mSelectedInfoAreaBackgroundColor;
            if (color == null) {
                color = mSelectedInfoAreaBacgroundDefaultColor;
            }
        } else {
            color = mInfoAreaBacgroundDefaultColor;
        }

        getImageCardView().setInfoAreaBackgroundColor(color);
    }
}
