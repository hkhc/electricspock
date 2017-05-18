package hkhc.electricspock.sample

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.util.Log;
import hkhc.electricspock.ElectricSpecification
import org.robolectric.Robolectric
import org.robolectric.util.FragmentTestUtil

/**
 * Created by hermanc on 10/4/2017.
 */
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

        when: "Accessing the textview member bariable"
        Log.d("TAG", "Hello")
        def text = fragment.helloWorldText.text

        then: "It actually point to the view in display"
        text == "Hello World!"

    }

    def "This is a test for Fragment with FragmentTestUtil"() {
        given: "Given the fragment"
        def fragment = new MainFragment()
        FragmentTestUtil.startVisibleFragment(fragment)

        when: "Accessing the textview member bariable"
        Log.d("TAG", "Hello")
        def text = fragment.helloWorldText.text

        then: "It actually point to the view in display"
        text == "Hello World!"

    }

    def "This is a test for Fragment with custom activity"() {
        given: "Given the fragment"
        def fragment = new MainFragment()
        startFragment(fragment)

        when: "Accessing the textview member bariable"
        Log.d("TAG", "Hello")
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