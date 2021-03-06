package de.avalax.fitbuddy.port.adapter.service.ad_mob;

import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import de.avalax.fitbuddy.application.ad_mod.AdMobProvider;
import de.avalax.fitbuddy.application.billing.BillingProvider;

public class GmsAdMobProvider implements AdMobProvider {
    private static final String APP_ID = "ca-app-pub-3067141613739864~9851773284";
    private static final String[] TEST_DEVICES = {
            AdRequest.DEVICE_ID_EMULATOR,
            "8F6B70E5DC92FE9E826BAA77A492D912",
            "84AA29C1F31F798EAC70198A31E9E7A4"
    };

    private BillingProvider billingProvider;

    public GmsAdMobProvider(BillingProvider billingProvider) {
        this.billingProvider = billingProvider;
    }

    public void initAdView(View view) {
        if (billingProvider.isPaid()) {
            view.setVisibility(View.GONE);
        } else {
            MobileAds.initialize(view.getContext(), APP_ID);
            AdRequest.Builder adRequest = new AdRequest.Builder();
            for (String testDevice : TEST_DEVICES) {
                adRequest.addTestDevice(testDevice);
            }
            ((AdView) view).loadAd(adRequest.build());
        }
    }
}
