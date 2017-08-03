package hkhc.electricspock.sample

import android.content.Context
import android.content.res.Resources
import hkhc.electricspock.ElectricSpecification
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Created by hermanc on 2/8/2017.
 */
@Config(manifest="src/main/non-exist-AndroidManifest.xml")
class ResourceWithInvalidManifestSpec extends ElectricSpecification {

    def "Test cases shall fail when access Android resources with non exist AndroidManifest.xml"() {
        given:
            Context context = RuntimeEnvironment.application;
        when: "Access resource"
            String appName = context.getResources().getString(R.string.app_name);
        then: "we get exception"
            thrown Resources.NotFoundException

    }

}