package com.sregg.android.tv.spotifyPlayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.util.Log;

import com.sregg.android.tv.spotifyPlayer.R;
import com.sregg.android.tv.spotifyPlayer.SpotifyTvApplication;
import com.sregg.android.tv.spotifyPlayer.activities.AlbumActivity;
import com.sregg.android.tv.spotifyPlayer.activities.ArtistsAlbumsActivity;
import com.sregg.android.tv.spotifyPlayer.presenters.AlbumDetailsPresenter;
import com.sregg.android.tv.spotifyPlayer.presenters.AlbumTrackRowPresenter;
import com.sregg.android.tv.spotifyPlayer.utils.Utils;

import java.util.List;

import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.TrackSimple;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AlbumDetailsFragment extends TracksDetailsFragment {

    private static final String TAG = AlbumDetailsFragment.class.getSimpleName();

    private static final long ACTION_PLAY_ALBUM = 1;
    private static final long ACTION_VIEW_ARTIST = 2;

    private String mAlbumId;
    private Album mAlbum;
    private List<String> mAlbumTrackUris;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();

        mAlbumId = intent.getStringExtra(AlbumActivity.ARG_ALBUM_ID);

        setupFragment();
        loadAlbum();
    }

    @Override
    protected Presenter getDetailsPresenter() {
        return new AlbumDetailsPresenter();
    }

    @Override
    protected Presenter getTrackRowPresenter(OnItemViewClickedListener onTrackRowItemClicked) {
        return new AlbumTrackRowPresenter(onTrackRowItemClicked);
    }

    private void setupFragment() {
        setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if (action.getId() == ACTION_PLAY_ALBUM) {
                    SpotifyTvApplication.getInstance().getSpotifyPlayerController().play(mAlbum.uri, mAlbumTrackUris, getTracks());
                } else if (action.getId() == ACTION_VIEW_ARTIST) {
                    ArtistSimple artist = null;

                    if (mAlbum.artists.size() > 0) {
                        artist = mAlbum.artists.get(0);
                    }

                    if (artist != null) {
                        ArtistsAlbumsActivity.launch(getActivity(), artist.id, artist.name);
                    }
                }
            }
        });
    }

    @Override
    protected List<TrackSimple> getTracks() {
        return mAlbum.tracks.items;
    }

    @Override
    protected List<String> getTrackUris() {
        return mAlbumTrackUris;
    }

    @Override
    protected String getObjectUri() {
        return mAlbum.uri;
    }

    private void loadAlbum() {
        // load artist from API to get their image
        SpotifyTvApplication.getInstance().getSpotifyService().getAlbum(mAlbumId, new Callback<Album>() {
            @Override
            public void success(final Album album, Response response) {
                mAlbum = album;
                mAlbumTrackUris = Utils.getTrackUrisFromTrackPager(mAlbum.tracks);
                setupDetails(album);
                setupTracksRows(album.tracks.items);

                if (album.images.size() > 0) {
                    String imageUrl = album.images.get(0).url;
                    loadDetailsRowImage(imageUrl);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void setupDetails(Album album) {
        DetailsOverviewRow detailsRow = new DetailsOverviewRow(album);

        detailsRow.addAction(new Action(
                ACTION_PLAY_ALBUM,
                getResources().getString(R.string.lb_playback_controls_play),
                null,
                getActivity().getResources().getDrawable(R.drawable.lb_ic_play)
        ));
        detailsRow.addAction(new Action(
                ACTION_VIEW_ARTIST, getResources().getString(R.string.go_to_artist),
                null
        ));

        setDetailsRow(detailsRow);
    }
}
