package hkhc.electricspock.sample

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import hkhc.electricspock.ElectricSpecification
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Created by hermanc on 2/8/2017.
 */
// Alternate AndroidManifest.xml shall be placed under resource directory
@Config(manifest="AndroidManifest-alt.xml")

class ResourceWithAltManifestSpec extends ElectricSpecification {

    def "Test cases shall be able to access Android resources with non-standard AndroidManifest.xml"() {
        given:
            Context context = RuntimeEnvironment.application;
        when: "Access resource"
            String appName = context.getResources().getString(R.string.app_name);
        then:
            appName == "ElectricSpock"
    }

    def "Test cases shall be able to access meta data with non-standard AndroidManifest.xml"() {
        given:
            String packageName = RuntimeEnvironment.application.packageName
            PackageManager packageManager = RuntimeEnvironment.application.packageManager

        when:
            ApplicationInfo ai = packageManager
                    .getApplicationInfo(packageName, PackageManager.GET_META_DATA)

        then:
            ai!=null

        when:
            Bundle bundle = ai.metaData
            int myApiKey = bundle.getInt("com.google.android.gms.version")
            int version = RuntimeEnvironment.application.getResources().getInteger(R.integer.google_play_services_version);


        then:
            version==11020000

    }

    // Assume that google play service library is present
    def "Test cases shall be able to access library resources with non-standard AndroidManifest.xml"() {
        given:
        Context context = RuntimeEnvironment.application;
        when: "Library resource"
        int googlePlayVersion = context.getResources().getInteger(R.integer.google_play_services_version);
        then:
        googlePlayVersion == 11020000
    }

}