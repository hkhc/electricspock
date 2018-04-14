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

package hkhc.electricspock

import org.robolectric.annotation.Config;
/**
 * Created by herman on 29/9/2017.
 */
@Config(manifest=Config.NONE)
class RobolectricVersionCheckerSpec extends ElectricSpecification {

    def "Unknown Robolectric version"() {
        given:
        def checker = new RobolectricVersionChecker("non-existence-file.properties")
        expect:
        checker.currentRobolectricVersion == "Unknown"
    }

    def "detect Robolectric version"() {
        given:
        def checker = new RobolectricVersionChecker("test-robolectric.properties")
        expect:
        checker.currentRobolectricVersion == "test-version"
    }

    def "accept version name and various kind of prefixes"() {

        given:
        def checker = new RobolectricVersionChecker("test-robolectric.properties")

        expect:
        !checker.isVersion(null, "3.3")
        !checker.isVersion("", "3.3")
        checker.isVersion("3.3", "3.3")
        checker.isVersion("3.3.1", "3.3")
        checker.isVersion("3.3.1.1", "3.3")
        checker.isVersion("3.3-1", "3.3")
        checker.isVersion("3.3-rc1", "3.3")
        checker.isVersion("3.3-hello.this.is.a.test", "3.3")
        !checker.isVersion("3.2", "3.3")
        !checker.isVersion("3.5.1", "3.3")

    }

    def "check currently accept version 3.3 and 3.4"() {

        given:
            def checker = new RobolectricVersionChecker("test-robolectric.properties")
            checker.acceptedVersions = ["3.3", "3.4"] as String[]
        expect:
            !checker.isVersion("3.2", checker.acceptedVersions)
            checker.isVersion("3.3", checker.acceptedVersions)
            checker.isVersion("3.3-rc1", checker.acceptedVersions)
            checker.isVersion("3.3.1", checker.acceptedVersions)
            checker.isVersion("3.4.2", checker.acceptedVersions)
            !checker.isVersion("3.5", checker.acceptedVersions)

    }

    def "check error message"() {
        given:
            def checker = new RobolectricVersionChecker("test-robolectric.properties")
        when:
            checker.checkRobolectricVersion(ver)
        then:
            def ex = thrown(RuntimeException)
            ex.message == "This version of ElectricSpock supports Robolectric 3.3.x to 3.8.x only. Version ${ver} is detected." as String

        where:
            ver || _
            "3.1" || _
            "9.9" || _
    }

}