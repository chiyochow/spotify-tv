package com.sregg.android.tv.spotify.presenters;

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.TrackSimple;


public class NowPlayingDetailsPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        TrackSimple track = (TrackSimple) item;

        if (track != null) {
            viewHolder.getTitle().setText(track.name);

            if (track.artists != null && track.artists.size() > 0) {
                viewHolder.getSubtitle().setText(track.artists.get(0).name);
            }
        }
    }
}
