package hkhc.electricspock.sample;

import android.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * Created by hermanc on 10/4/2017.
 */

public class MainFragment extends Fragment {

    public TextView helloWorldText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup vg = (ViewGroup)inflater.inflate(R.layout.fragment_main, null);
        helloWorldText = vg.findViewById(R.id.text);

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
