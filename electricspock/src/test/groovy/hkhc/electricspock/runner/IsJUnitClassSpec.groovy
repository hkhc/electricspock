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

import hkhc.electricspock.runner.testdata.Dummy
import hkhc.electricspock.runner.testdata.DummyWithAnno
import hkhc.electricspock.runner.testdata.ExtendingWithRunWith
import hkhc.electricspock.runner.testdata.WithRunWith
import hkhc.electricspock.runner.testdata.WithTestMethod
import spock.lang.Specification
import spock.lang.Title


/**
 * Created by herman on 16/1/2017.
 */
@Title("check if a class is JUnit test case class")
class IsJUnitClassSpec extends Specification {

    def "Plain class is not JUnit class"() {
        expect:
        SpecUtils.isDirectJUnitClass(Dummy)==false
    }

    def "Class with @RunWith is JUnit class"() {
        expect:
        SpecUtils.isDirectJUnitClass(WithRunWith)
    }

    def "Class with @Test method is JUnit class"() {
        expect:
        SpecUtils.isDirectJUnitClass(WithTestMethod)
    }

    def "Class with arbitrary annotation is not JUnit class"() {
        expect:
        SpecUtils.isDirectJUnitClass(DummyWithAnno)==false
    }

    def "Plain class is not JUnit class based on ancestors"() {
        expect:
        SpecUtils.isJUnitClass(Dummy)==false
    }

    def "Class with @RunWith is JUnit class based on ancestors"() {
        expect:
        SpecUtils.isJUnitClass(ExtendingWithRunWith)
    }


}