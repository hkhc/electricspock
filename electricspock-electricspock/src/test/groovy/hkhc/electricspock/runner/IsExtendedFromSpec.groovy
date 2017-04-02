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

package hkhc.electricspock.runner

import hkhc.electricspock.runner.testdata.LivingThing
import hkhc.electricspock.runner.testdata.Plant
import hkhc.electricspock.runner.testdata.Rose
import spock.lang.Specification

/**
 * Created by herman on 15/1/2017.
 */
class IsExtendedFromSpec extends Specification {

    def "a class is extended from its super class"() {
        expect:
        SpecUtils.isExtendedFrom(Rose, Plant)
    }

    def "a class is extended from its ancestor class"() {
        expect: "a class is extended from its ancestor class"
        SpecUtils.isExtendedFrom(Rose, LivingThing)
    }

    def "a class is not extended from another class"() {

        expect: "a class is not extended from another class"
        false == SpecUtils.isExtendedFrom(Rose, String)
    }

}