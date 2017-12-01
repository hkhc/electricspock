package hkhc.electricspock.sample

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.util.Log
import hkhc.electricspock.ElectricSpecification
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
import org.robolectric.util.FragmentTestUtil

/**
 * Created by hermanc on 10/4/2017.
 *
 * About manifest location
 * With Gradle build system, Robolectric looks for AndroidManifest.xml in the following order.
 * - Java resource folder
 * - build/intermediates/manifests/[full or fast-start]/[build-type]
 * So it is a common mistake to specify the location of AndroidManifest.xml according to source
 * code folder organization (e.g. src/main/AndroidManifest.xml) The specified AndroidManifest.xml
 * location affect Robolectric to look for merged resources as well. So if some resource is not
 * found in test, it is probably due to incorrect setting of AndroidManifest.xml location.
 * That said, the Android Gradle plugin merge the AndroidManifest.xml and put the result under
 * the above mentioned intermediates directory. So the src/main/AndroidManifest.xml affect the test
 * result.
 *
 */

@Config(manifest="AndroidManifest.xml", sdk=24)
class HelloWorldFragmentSpec extends ElectricSpecification {

//    def "This is a test for Activity"() {
//        given: "Given the activity"
//        def mainActivity = Robolectric.buildActivity(MainActivity2).create().get()
//
//        when: "Accessing the textview member bariable"
//        Log.d("TAG", "Hello")
//        def text = mainActivity.helloTextView.text
//
//        then: "It actually point to the view in display"
//        text == "Hello World!"
//
//    }

    def "This is a test for Fragment with buildFragment"() {
        given: "Given the fragment"
        def fragment = Robolectric.buildFragment(MainFragment).create().start().resume().get()

        when: "Accessing the textview member variable"
        Log.d("TAG", "Hello")
        def text = fragment.helloWorldText.text

        then: "It actually point to the view in display"
        text == "Hello World!"

    }

    def "This is a test for Fragment with FragmentTestUtil"() {
        given: "Given the fragment"
        def fragment = new MainFragment()
        FragmentTestUtil.startVisibleFragment(fragment)

        when: "Accessing the textview member variable"
        Log.d("TAG", "Hello")
        def text = fragment.helloWorldText.text

        then: "It actually point to the view in display"
        text == "Hello World!"

    }

    def "This is a test for Fragment with custom activity"() {
        given: "Given the fragment"
        def fragment = new MainFragment()
        startFragment(fragment)

        when: "Accessing the textview member variable"
        Log.d("TAG", "Hello")
        def text = fragment.helloWorldText.text

        then: "It actually point to the view in display"
        text == "Hello World!"

    }

    def "Test fragment with SupportFragmentTestUtil"() {

        given: "Given the fragment"
        def fragment = new SupportMainFragment()
        SupportFragmentTestUtil.startVisibleFragment(fragment)

        when: "Accessing the textview member variable"
        def text = fragment.helloWorldText.text

        then: "It actually point to the view in display"
        text == "Hello World!"

    }

    public static void startFragment(Fragment fragment )
    {
        Activity activity = Robolectric.buildActivity( MainActivity2.class )
                .create()
                .start()
                .resume()
                .get();

        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.activity_main2, fragment);
        fragmentTransaction.commit();
    }

}