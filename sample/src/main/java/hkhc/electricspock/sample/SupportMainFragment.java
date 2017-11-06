/*
 * Copyright 2017 Herman Cheung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package hkhc.electricspock.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * Created by herman on 8/10/2017.
 */

public class SupportMainFragment extends Fragment {

    public TextView helloWorldText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup)inflater.inflate(R.layout.fragment_main, null);
        helloWorldText = (TextView)vg.findViewById(R.id.text);

        // Create a banner ad. The ad size and ad unit ID must be set before calling loadAd.
        AdView mAdView = new AdView(getActivity());
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId("myAdUnitId");

        // Create an ad request.
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        // Optionally populate the ad request builder.
        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

        // Add the AdView to the view hierarchy.
        vg.addView(mAdView);

        // Start loading the ad.
        // **** it will freeze Robolectric test
        // **** in practise we shall decouple the loadAd method from
        // the class under test
//        mAdView.loadAd(adRequestBuilder.build());

        return vg;

    }

}
