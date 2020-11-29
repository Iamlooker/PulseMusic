package com.hardcodecoder.pulsemusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.interfaces.ItemGestureCallback;
import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperAdapter;
import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperViewHolder;
import com.hardcodecoder.pulsemusic.interfaces.PlaylistCardListener;
import com.hardcodecoder.pulsemusic.utils.ImageUtil;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.CardsSVH>
        implements ItemTouchHelperAdapter {

    private final List<String> mPlaylistNames;
    private final PlaylistCardListener mListener;
    private final LayoutInflater mInflater;
    private final ItemGestureCallback<String> mCallback;

    public PlaylistAdapter(
            @NonNull List<String> playlistNames,
            @NonNull LayoutInflater inflater,
            @NonNull PlaylistCardListener listener,
            @Nullable ItemGestureCallback<String> callback) {
        mPlaylistNames = playlistNames;
        mInflater = inflater;
        mListener = listener;
        mCallback = callback;
    }

    public void addPlaylist(String playlistName) {
        mPlaylistNames.add(1, playlistName);
        notifyItemInserted(1);
    }

    public void removePlaylist(int position) {
        mPlaylistNames.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        if (null != mCallback)
            mCallback.onItemDismissed(mPlaylistNames.get(position), position);
    }

    @NonNull
    @Override
    public CardsSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardsSVH(mInflater.inflate(R.layout.rv_playlist_card_item, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsSVH holder, int position) {
        holder.updateView(mPlaylistNames.get(position));
    }

    @Override
    public int getItemCount() {
        return mPlaylistNames.size();
    }

    static class CardsSVH extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        private final TextView title;

        CardsSVH(@NonNull View itemView, PlaylistCardListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.playlist_title);

            itemView.findViewById(R.id.edit_btn).setOnClickListener(v -> listener.onItemEdit(getAdapterPosition()));

            itemView.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));
        }

        void updateView(String s) {
            title.setText(s);
            if (getAdapterPosition() == 0)
                itemView.findViewById(R.id.edit_btn).setVisibility(View.GONE);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackground(ImageUtil.getAccentTintedSelectedItemBackground(itemView.getContext()));
        }

        @Override
        public void onItemClear() {
            itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.overlay_color_bck_drawable));
        }
    }
}