package hkhc.electricspock.sample

import android.content.Context
import hkhc.electricspock.ElectricSpecification
import org.robolectric.RuntimeEnvironment
import spock.lang.Ignore

/**
 * Created by hermanc on 2/8/2017.
 */
//@Config(manifest="non-exist-AndroidManifest.xml")
@Ignore('''Since Robolectric 3.5, non-exist manifest location will cause IllegalArgumentException, 
                the test no longer valid''')
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