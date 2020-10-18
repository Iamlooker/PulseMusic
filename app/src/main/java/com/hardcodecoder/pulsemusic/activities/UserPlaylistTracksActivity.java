package com.hardcodecoder.pulsemusic.activities;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.base.AdvancePlaylist;
import com.hardcodecoder.pulsemusic.model.MusicModel;
import com.hardcodecoder.pulsemusic.providers.ProviderManager;

import java.util.ArrayList;

public class UserPlaylistTracksActivity extends AdvancePlaylist {

    public static final String KEY_TITLE = "playlist name";
    private String playListTitle = "";
    private boolean isPlaylistModified = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null)
            playListTitle = getIntent().getExtras().getString(KEY_TITLE);

        setUpToolbar(playListTitle);

        if (playListTitle != null)
            ProviderManager.getPlaylistProvider().getTrackForPlaylist(playListTitle, tracks -> setUpData(tracks, 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_advance_playllist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_action_clear_duplicates) {
            ProviderManager.getPlaylistProvider().deleteAllDuplicatesInPlaylist(playListTitle, mPlaylistTracks, result ->
                    mAdapter.updatePlaylist(result));
            return true;
        }
        return false;
    }

    @Override
    protected SpannableStringBuilder getEmptyListStyledText() {
        String text = getString(R.string.no_playlist_tracks_found);
        int len = text.length();
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
        stringBuilder.setSpan(new AbsoluteSizeSpan(getResources().getDimensionPixelSize(R.dimen.spannable_text_absolute_size_span)),
                len - 1,
                len,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return stringBuilder;
    }

    @Override
    protected void onReceiveData(ArrayList<MusicModel> receivedData) {
        ProviderManager.getPlaylistProvider().addTracksToPlaylist(receivedData, playListTitle, true);
        if (null == mAdapter) setUpData(receivedData, 0);
        else mAdapter.addItems(receivedData);
    }

    @Override
    public void onItemDismissed(int position) {
        Snackbar sb = Snackbar.make(findViewById(R.id.playlist_data_root_view), R.string.item_removed, Snackbar.LENGTH_SHORT);
        sb.setAction(getString(R.string.snack_bar_action_undo), v -> mAdapter.restoreItem());
        isPlaylistModified = true;
        sb.show();
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        isPlaylistModified = true;
    }

    @Override
    protected void onDestroy() {
        if (isPlaylistModified)
            ProviderManager.getPlaylistProvider().updatePlaylistTracks(playListTitle, mPlaylistTracks);
        super.onDestroy();
    }
}