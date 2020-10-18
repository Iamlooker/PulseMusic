package com.hardcodecoder.pulsemusic.providers;

import android.os.Environment;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.hardcodecoder.pulsemusic.TaskRunner;
import com.hardcodecoder.pulsemusic.TaskRunner.Callback;
import com.hardcodecoder.pulsemusic.storage.StorageUtil;

import java.io.File;
import java.util.List;

public class IgnoreListProvider {

    private final Handler mHandler;
    private final String mIgnoredFilePath;

    public IgnoreListProvider(String baseFilesDir, Handler handler) {
        mHandler = handler;
        mIgnoredFilePath = baseFilesDir + File.separator + "ignoredList.txt";
        createFileIfNotExists();
    }

    public void addToIgnoreList(@NonNull String folderToAdd) {
        TaskRunner.executeAsync(() -> {
            if (createFileIfNotExists()) {
                File ignoredListFile = new File(mIgnoredFilePath);
                StorageUtil.writeStringToFile(ignoredListFile, folderToAdd + System.lineSeparator(), true);
            }
        });
    }

    public void getIgnoredList(@NonNull Callback<List<String>> callback) {
        TaskRunner.executeAsync(() -> {
            List<String> ignoredList = StorageUtil.readLinesFromFile(new File(mIgnoredFilePath));
            mHandler.post(() -> callback.onComplete(ignoredList));
        });
    }

    public List<String> getIgnoredList() {
        return StorageUtil.readLinesFromFile(new File(mIgnoredFilePath));
    }

    public void removeFromIgnoreList(@NonNull String folderToRemove) {
        TaskRunner.executeAsync(() -> {
            File file = new File(mIgnoredFilePath);
            if (file.exists()) {
                getIgnoredList(result -> {
                    if (null == result) return;
                    StringBuilder stringBuilder = new StringBuilder();
                    String remove = folderToRemove.trim();
                    for (String folder : result) {
                        if (remove.equals(folder)) continue;
                        stringBuilder.append(folder).append(System.lineSeparator());
                    }
                    StorageUtil.writeStringToFile(file, stringBuilder.toString(), false);
                });
            }
        });
    }

    private synchronized boolean createFileIfNotExists() {
        File ignoredListFile = new File(mIgnoredFilePath);

        if (ignoredListFile.exists()) return true;

        if (StorageUtil.createFile(ignoredListFile)) {
            // Default ignored folders
            String folders =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS) +
                            System.lineSeparator() +
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES) +
                            System.lineSeparator() +
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS) +
                            System.lineSeparator();
            StorageUtil.writeStringToFile(ignoredListFile, folders, false);
            return true;
        }
        return false;
    }
}