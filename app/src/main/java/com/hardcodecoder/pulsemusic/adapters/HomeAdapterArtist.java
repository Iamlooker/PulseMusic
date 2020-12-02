package com.hardcodecoder.pulsemusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.interfaces.SimpleTransitionClickListener;
import com.hardcodecoder.pulsemusic.model.TopArtistModel;
import com.hardcodecoder.pulsemusic.themes.TintHelper;
import com.hardcodecoder.pulsemusic.utils.ImageUtil;

import java.util.List;
import java.util.Locale;

public class HomeAdapterArtist extends RecyclerView.Adapter<HomeAdapterArtist.AdapterSVH> {

    private final List<TopArtistModel> mList;
    private final LayoutInflater mInflater;
    private final SimpleTransitionClickListener mListener;

    public HomeAdapterArtist(List<TopArtistModel> list, LayoutInflater inflater, SimpleTransitionClickListener listener) {
        this.mList = list;
        this.mInflater = inflater;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AdapterSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterSVH(mInflater.inflate(R.layout.rv_home_item_artist, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSVH holder, int position) {
        holder.updateData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        if (null != mList)
            return mList.size();
        return 0;
    }

    static class AdapterSVH extends RecyclerView.ViewHolder {

        private final ImageView artistArt;
        private final MaterialTextView artistName;
        private final MaterialTextView trackCount;

        AdapterSVH(@NonNull View itemView, SimpleTransitionClickListener listener) {
            super(itemView);
            itemView.setBackground(ImageUtil.getHighlightedItemBackground(itemView.getContext()));
            artistArt = itemView.findViewById(R.id.home_rv_artist_list_item_art);
            TintHelper.setAccentTintTo(artistArt);
            artistName = itemView.findViewById(R.id.home_rv_list_item_title);
            trackCount = itemView.findViewById(R.id.home_rv_list_item_text);
            itemView.setOnClickListener(v -> listener.onItemClick(artistArt, getAdapterPosition()));
        }

        void updateData(TopArtistModel artistModel) {
            artistArt.setTransitionName("shared_transition_artist_iv_" + getAdapterPosition());
            artistName.setText(artistModel.getArtistName());
            trackCount.setText(String.format(Locale.ENGLISH, "%s %d", itemView.getResources().getString(R.string.tracks_plays), artistModel.getNumOfPlays()));
        }
    }
}