/*
 * Copyright 2017 Herman Cheung
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

import hkhc.electricspock.ElectricSpecification
import hkhc.electricspock.runner.ElectricSuite
import hkhc.electricspock.runner.InnerSpecRunner
import org.junit.runner.RunWith
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title;
/**
 * Created by herman on 15/1/2017.
 */

@Title("Sample Suite")
class SampleSuite extends ElectricSuite {

    @Title("Hello Spec")
    @Narrative("This is a long nattative")
    static class HelloSpec extends ElectricSpecification {

        def "Hello test"() {
            given:
                def a=10
            when:
                a++
            then:
                a==11
        }
    }

    @Title("World Spec")
    static class WorldSpec extends Specification {

        def "World test"() {
            given:
                def a=10
            when:
                a++
            then:
                a==11
        }
    }

}