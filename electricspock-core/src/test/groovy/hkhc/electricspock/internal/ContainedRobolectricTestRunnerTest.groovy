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

package hkhc.electricspock.internal

import hkhc.electricspock.ElectricSpecification
import hkhc.electricspock.sample.BasicSpec
import hkhc.electricspock.sample.ConfigAnnotatedSpec1
import hkhc.electricspock.sample.ConfigAnnotatedSpec2
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runners.model.FrameworkMethod
import org.robolectric.annotation.Config
import org.robolectric.internal.SdkEnvironment
import org.robolectric.internal.bytecode.InstrumentationConfiguration

import java.lang.reflect.Method

import static org.assertj.core.api.Assertions.assertThat

/**
 * Created by herman on 9/10/2017.
 */
class ContainedRobolectricTestRunnerTest {

    ContainedRobolectricTestRunner runner = null;

    @Before
    void setUp() {
        // given
        runner = new ContainedRobolectricTestRunner(BasicSpec)
    }

    @Test
    void "getPlaceHolderMethod shall return non-null object"() throws Exception {

        // when
        FrameworkMethod method = runner.getPlaceHolderMethod()

        // then
        assertThat method isNotNull()
        assertThat method.method.name isEqualTo "testPlaceholder"

    }

    @Test
    void "getContainedSdkEnvironment shall return non-null object"() throws Exception {

        // when
        SdkEnvironment sdkEnv = runner.getContainedSdkEnvironment()

        // then
        assertThat sdkEnv isNotNull()

    }

    @Test
    void "SdkEnvironment.getBootstrap shall return a class object with different class loader"() {

        // given
        SdkEnvironment sdkEnv = runner.getContainedSdkEnvironment()

        // when
        Class c = sdkEnv.bootstrappedClass(ElectricSpecification)

        // then
        assertThat c.getName() isEqualTo ElectricSpecification.getName()
        assertThat c.getClassLoader() isNotEqualTo ElectricSpecification.getClassLoader()


    }

    @Test
    void "it shall avoid to be part of instrumentation"() {

        // when
        InstrumentationConfiguration config = runner.createClassLoaderConfig(runner.placeHolderMethod)

        // then
        assertThat config.shouldAcquire(ContainedRobolectricTestRunner.name) isFalse()

    }

    @Test
    void "it shall recognize @Config annotation at class"() {

        Config config = null

        // given
        runner = new ContainedRobolectricTestRunner(ConfigAnnotatedSpec1)
        config = runner.getConfig(ConfigAnnotatedSpec1.getMethod("placeholder"))

        // then
        assertThat config.manifest isEqualTo Config.NONE
        assertThat config.packageName isEqualTo ""


        // given
        runner = new ContainedRobolectricTestRunner(ConfigAnnotatedSpec2)
        config = runner.getConfig(ConfigAnnotatedSpec2.getMethod("placeholder"))

        // then
        assertThat config.manifest isEqualTo Config.DEFAULT_MANIFEST_NAME
        assertThat config.packageName isEqualTo "hkhc.testpackage"

    }

    @Test
    @Ignore
    void "it shall recognize @Config annotation at method"() {
        fail("to be implemented")
    }

    @Test
    void "the bootstrap method is the sandboxed method of the placeholder test class"() {

        Method method = runner.getBootstrapedMethod()
        assertThat method.name isEqualTo "testPlaceholder"
        assertThat method.class.classLoader isNotEqualTo ContainedRobolectricTestRunner.PlaceholderTest.classLoader

    }

}