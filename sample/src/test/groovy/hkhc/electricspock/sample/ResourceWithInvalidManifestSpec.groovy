package hkhc.electricspock.sample

import android.content.Context
import hkhc.electricspock.ElectricSpecification
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Created by hermanc on 2/8/2017.
 */
@Config(manifest="non-exist-AndroidManifest.xml")
class ResourceWithInvalidManifestSpec extends ElectricSpecification {

    def "Test cases shall fall back to default Android resources with non exist AndroidManifest.xml"() {
        given:
            Context context = RuntimeEnvironment.application
            println "Expect to see warning that \"No manifest file found\" above."
        when: "Access resource"
            String appName = context.getResources().getString(R.string.app_name)
        then: "we get fall back processing of resources"
            appName == "ElectricSpock"

    }

}