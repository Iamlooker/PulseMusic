package com.hardcodecoder.pulsemusic.helper;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;

import com.hardcodecoder.pulsemusic.loaders.LoaderCache;
import com.hardcodecoder.pulsemusic.model.MusicModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataModelHelper {

    public static List<MusicModel> getModelsObjectFromTitlesList(List<String> titles) {
        List<MusicModel> modelList = new ArrayList<>();
        Map<String, MusicModel> modelMap = LoaderCache.getModelMap();
        for (String name : titles) {
            if (null != modelMap.get(name))
                modelList.add(modelMap.get(name));
        }
        return modelList;
    }

    public static List<String> getTitlesListFromModelsObject(List<MusicModel> modelList) {
        List<String> titlesList = new ArrayList<>();
        for (MusicModel musicModel : modelList) {
            titlesList.add(musicModel.getTrackName());
        }
        return titlesList;
    }

    public static MusicModel buildMusicModelFrom(Context context, Intent data) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, data.getData());
        try {
            String path = data.getDataString();
            if (path != null) {
                String name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                int duration = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                mmr.release();
                return new MusicModel(-1, name, path, album, artist, null, 0, duration);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
