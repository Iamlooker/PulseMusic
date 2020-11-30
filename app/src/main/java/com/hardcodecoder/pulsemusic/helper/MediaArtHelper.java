package com.hardcodecoder.pulsemusic.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;

import com.hardcodecoder.pulsemusic.MediaArtCache;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.themes.ThemeColors;
import com.hardcodecoder.pulsemusic.utils.ImageUtil;

public class MediaArtHelper {

    private static TypedArray mMediaArtColors;

    public static Drawable getDefaultAlbumArt(Context context, long albumId) {
        // -1 represents the primary color tinted album art
        // So, we need not convert it into positive int
        if (albumId != -1) albumId = Math.abs(albumId);

        if (null != MediaArtCache.getMediaArtDrawable(albumId))
            return MediaArtCache.getMediaArtDrawable(albumId);
        Drawable drawable = ImageUtil.generateTintedDefaultAlbumArt(context, getTintColor(context, albumId));
        MediaArtCache.addDrawableIfNotPresent(drawable, albumId);
        return drawable;
    }

    public static Bitmap getDefaultAlbumArtBitmap(Context context, long albumId) {
        // -1 represents the primary color tinted album art
        // So, we need not convert it into positive int
        if (albumId != -1) albumId = Math.abs(albumId);

        Drawable drawable = ImageUtil.generateTintedDefaultAlbumArt(context, getTintColor(context, albumId));
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @ColorInt
    private static int getTintColor(Context context, long albumId) {
        if (albumId == -1) return ThemeColors.getCurrentColorPrimary();
        if (null == mMediaArtColors)
            mMediaArtColors = context.getResources().obtainTypedArray(R.array.album_art_colors);
        return mMediaArtColors.getColor((int) albumId % mMediaArtColors.length(), 0xFF0000);
    }
}