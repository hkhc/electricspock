package hkhc.electricspock.sample

import android.content.Context
import hkhc.electricspock.ElectricSpecification
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.manifest.AndroidManifest

/**
 * Created by hermanc on 2/8/2017.
 */
@Config(manifest="src/main/AndroidManifest.xml")
class ResourceWithManifestSpec extends ElectricSpecification {

    def "Test cases shall be able to access Android resources with AndroidManifest.xml"() {
        given:
            Context context = RuntimeEnvironment.application;
        when: "Access resource"
            String appName = context.getResources().getString(R.string.app_name);
        then:
            appName == "ElectricSpock"
    }

}