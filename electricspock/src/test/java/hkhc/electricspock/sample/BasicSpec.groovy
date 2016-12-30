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

import hkhc.electricspock.ElectricSpecification
import org.robolectric.annotation.Config
import spock.lang.Narrative
import spock.lang.Title

/**
 * Created by herman on 28/12/2016.
 */
@Config(manifest=Config.NONE)
@Narrative("""
This spec assert that the enhanced specification should work just like the original specification.
""")
class BasicSpec extends ElectricSpecification {

    def "It should work just like ordinary Specification"() {

        given:
            def a=10
        when:
            a=a*2
        then:
            a==21

    }

}