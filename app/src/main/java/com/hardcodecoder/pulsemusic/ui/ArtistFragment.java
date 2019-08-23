package com.hardcodecoder.pulsemusic.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.audiofx.AudioEffect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.DetailsActivity;
import com.hardcodecoder.pulsemusic.activities.MainActivity;
import com.hardcodecoder.pulsemusic.activities.SearchActivity;
import com.hardcodecoder.pulsemusic.activities.SettingsActivity;
import com.hardcodecoder.pulsemusic.adapters.ArtistAdapter;
import com.hardcodecoder.pulsemusic.interfaces.ArtistDataFetchCompletionCallback;
import com.hardcodecoder.pulsemusic.interfaces.TransitionClickListener;
import com.hardcodecoder.pulsemusic.model.ArtistModel;
import com.hardcodecoder.pulsemusic.utils.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class ArtistFragment extends Fragment implements TransitionClickListener {

    private List<ArtistModel> mList;
    private ArtistAdapter adapter;
    private int spanCount;
    private int currentConfig = Configuration.ORIENTATION_PORTRAIT;
    private GridLayoutManager layoutManager;

    public ArtistFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_artist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toolbar t = view.findViewById(R.id.toolbar);
        t.setTitle(R.string.artist);

        if (getActivity() != null)
            ((MainActivity) getActivity()).setSupportActionBar(t);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            spanCount = UserInfo.getLandscapeGridSpanCount(getContext());
        else
            spanCount = UserInfo.getPortraitGridSpanCount(getContext());
        setRv(view);
    }

    private void setRv(View view) {
        Handler h = new Handler();
        if (null != getActivity())
            new ArtistFetcher(getActivity().getContentResolver(), data -> {
                if (null != data && data.size() > 0) {
                    mList = data;
                    h.post(() -> {
                        RecyclerView rv = view.findViewById(R.id.rv_artist_fragment);
                        layoutManager = new GridLayoutManager(rv.getContext(), spanCount);
                        rv.setLayoutManager(layoutManager);
                        rv.setHasFixedSize(true);
                        rv.setItemAnimator(new DefaultItemAnimator());
                        adapter = new ArtistAdapter(mList, getLayoutInflater(), this);
                        rv.setAdapter(adapter);
                    });
                }
            }).execute();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (currentConfig == Configuration.ORIENTATION_PORTRAIT) {
            if (spanCount != UserInfo.getPortraitGridSpanCount(getContext()))
                layoutManager.setSpanCount(UserInfo.getPortraitGridSpanCount(getContext()));
        } else if (currentConfig == Configuration.ORIENTATION_LANDSCAPE) {
            if (spanCount != UserInfo.getLandscapeGridSpanCount(getContext()))
                layoutManager.setSpanCount(UserInfo.getLandscapeGridSpanCount(getContext()));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_album_artist_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_search:
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;

            case R.id.menu_action_equalizer:
                final Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                if (null != getContext()) {
                    if ((intent.resolveActivity(getContext().getPackageManager()) != null))
                        startActivityForResult(intent, 599);
                    else
                        Toast.makeText(getContext(), getString(R.string.equalizer_error), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.menu_action_setting:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            currentConfig = Configuration.ORIENTATION_LANDSCAPE;
            spanCount = UserInfo.getLandscapeGridSpanCount(getContext());
            layoutManager.setSpanCount(spanCount);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            currentConfig = Configuration.ORIENTATION_PORTRAIT;
            spanCount = UserInfo.getPortraitGridSpanCount(getContext());
            layoutManager.setSpanCount(spanCount);
        }
    }

    @Override
    public void onItemClick(View v, int pos) {
        Intent i = new Intent(getContext(), DetailsActivity.class);
        i.putExtra(DetailsActivity.KEY_ART_URL, String.valueOf(R.drawable.album_art_error));
        i.putExtra(DetailsActivity.KEY_TITLE, mList.get(pos).getArtistName());
        i.putExtra(DetailsActivity.KEY_ITEM_CATEGORY, DetailsActivity.CATEGORY_ARTIST);
        startActivity(i);
    }

    static class ArtistFetcher extends AsyncTask<Void, Void, List<ArtistModel>> {

        private ArtistDataFetchCompletionCallback mCallback;
        private List<ArtistModel> data = new ArrayList<>();
        private ContentResolver mContentResolver;

        ArtistFetcher(ContentResolver mContentResolver, ArtistDataFetchCompletionCallback mCallback) {
            this.mCallback = mCallback;
            this.mContentResolver = mContentResolver;
        }

        @Override
        protected List<ArtistModel> doInBackground(Void... voids) {
            String[] col = {MediaStore.Audio.Albums._ID,
                    MediaStore.Audio.Albums._ID,
                    MediaStore.Audio.Artists.ARTIST,
                    MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
                    MediaStore.Audio.Artists.NUMBER_OF_TRACKS};

            final Cursor cursor = mContentResolver.query(
                    MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                    col,
                    null,
                    null,
                    MediaStore.Audio.Artists.ARTIST + " ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Artists._ID));

                    String artist = cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST));

                    int num_albumns = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS));

                    int num_tracks = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));

                    data.add(new ArtistModel(id, artist, num_albumns, num_tracks));
                } while (cursor.moveToNext());
                cursor.close();
            }
            return data;
        }

        @Override
        protected void onPostExecute(List<ArtistModel> albumModels) {
            mCallback.onTaskComplete(albumModels);
        }

    }
}
