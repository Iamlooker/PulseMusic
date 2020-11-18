package com.hardcodecoder.pulsemusic.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.fragments.settings.base.SettingsBaseFragment;
import com.hardcodecoder.pulsemusic.themes.TintHelper;


public class SettingsDonationFragment extends SettingsBaseFragment {

    public static final String TAG = SettingsDonationFragment.class.getSimpleName();

    public static SettingsDonationFragment getInstance() {
        return new SettingsDonationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_donation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton coffeeBtn = view.findViewById(R.id.bmc_btn);
        MaterialButton payPalBtn = view.findViewById(R.id.pp_btn);

        TintHelper.setAccentTintToMaterialButton(coffeeBtn, false);
        TintHelper.setAccentTintToMaterialButton(payPalBtn, false);

        coffeeBtn.setOnClickListener(v -> openLink("https://www.buymeacoffee.com/HardcodeCoder"));
        payPalBtn.setOnClickListener(v -> openLink("https://paypal.me/HardcodeCoder"));
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getToolbarTitleForFragment() {
        return R.string.donations;
    }

    private void openLink(String link) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        startActivity(i);
    }
}