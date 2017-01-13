/*
 * Copyright 2016 Herman Cheung
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

package hkhc.electricspock.sample

import android.util.Log
import hkhc.electricspock.ElectricSpecification
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Title
import spock.lang.Unroll

/**
 * Created by herman on 27/12/2016.
 */

@Config(constants = BuildConfig)
@Title("This is a testing spec")
@Narrative("""
A for apple
B for boy
C for cat
""")
class HelloWorldSpec extends ElectricSpecification {

    def "This is a test for Log"() {
        given: "This is a given block"
        def mainActivity = Robolectric.buildActivity(MainActivity).create().get()

        when: "This is a when block"
        Log.d("TAG", "Hello")
        def text = mainActivity.helloTextView.text

        then: "This is a then block"
        text == "Hello World!"

    }

    def "This is a test for Log 2"() {
        given:
        def mainActivity = Robolectric.buildActivity(MainActivity).create().get()

        when:
        Log.d("TAG", "Hello")
        def text = mainActivity.helloTextView.text

        then:
        text == "Hello World!"

    }

    @Unroll
    @Ignore("Just to illustrate a failed test")
    @Issue("http://www.google.com")
    def "Test multiplication of #a"() {

        when:
            def result = a*10

        then:
            result == ((a==3) ? a*11 : a*10)

        where:
            a << [1,2,3,4,5]

    }

}