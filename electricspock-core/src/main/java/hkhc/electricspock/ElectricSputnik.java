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

package hkhc.electricspock;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.robolectric.internal.SdkEnvironment;
import org.spockframework.runtime.Sputnik;
import org.spockframework.runtime.model.SpecInfo;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.Properties;

import hkhc.electricspock.internal.ContainedRobolectricTestRunner;
import hkhc.electricspock.internal.ElectricSpockInterceptor;
import spock.lang.Title;

/**
 * Created by herman on 27/12/2016.
 * Test Runner
 */

public class ElectricSputnik extends Runner implements Filterable, Sortable {

    private SdkEnvironment sdkEnvironment;

    /* it is used to setup Robolectric infrastructure, and not used to run actual test cases */
    private ContainedRobolectricTestRunner containedRunner;

    /* the real test runner to run test classes. It is enclosed by ElectricSputnik so that it is
    run within Robolectric interception
     */
    private Runner sputnik;

    static {
        new SecureRandom(); // this starts up the Poller SunPKCS11-Darwin thread early, outside of any Robolectric classloader
    }

    public ElectricSputnik(Class<?> testClass)  throws InitializationError {

        /* The project is so sensitive to the version of Robolectric, that we strictly check
        its version before proceed
         */
        checkRobolectricVersion();

        containedRunner = new ContainedRobolectricTestRunner();
        sdkEnvironment = containedRunner.getContainedSdkEnvironment();

        // Since we have bootstrappedClass we may properly initialize
        sputnik = createSputnik(testClass);

        registerSpec();

    }

    private Runner createSputnik(Class<?> testClass) {

        Class bootstrappedTestClass = sdkEnvironment.bootstrappedClass(testClass);

        try {

            return (Runner)sdkEnvironment
                    .bootstrappedClass(Sputnik.class)
                    .getConstructor(Class.class)
                    .newInstance(bootstrappedTestClass);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void registerSpec() {

        Constructor interceptorConstructor = getInterceptorConstructor();

        for(Method method : sputnik.getClass().getDeclaredMethods()) {
            Object spec = getSpec(method);
            if (spec!=null) {
                try {
                    // ElectricSpockInterceptor self-register on construction, no need to keep a ref here
                    interceptorConstructor.newInstance(spec, containedRunner);
                }
                catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    private Object getSpec(Method method) {

        if (method.getName().equals("getSpec")) {
            method.setAccessible(true);
            try {
                return method.invoke(sputnik);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            return null;
        }
    }

    private Constructor getInterceptorConstructor() {

        try {
            return sdkEnvironment
                    .bootstrappedClass(ElectricSpockInterceptor.class)
                    .getConstructor(
                            sdkEnvironment.bootstrappedClass(SpecInfo.class),
                            ContainedRobolectricTestRunner.class
                    );
        }
        catch (NoSuchMethodException e) {
            // it should not happen in production code as the class
            // ElectricSpockInterceptor is known
            throw new RuntimeException(e);
        }

    }

    private String getCurrentRobolectricVersion() {

        try {
            Properties prop = new Properties();
            prop.load(getClass().getClassLoader().getResourceAsStream("robolectric-version.properties"));
            return prop.getProperty("robolectric.version");
        }
        catch (IOException e) {
            return "Unknown";
        }


    }

    private void checkRobolectricVersion() {

        String ver = getCurrentRobolectricVersion();
        if (!(ver.equals("3.4") ||
                ver.indexOf("3.4.")==0 ||
                ver.indexOf("3.4-")==0))
            throw new RuntimeException("This version of ElectricSpock supports Robolectric 3.3 only");
    }

    public Description getDescription() {

        Description originalDesc = sputnik.getDescription();

        Class<?> testClass = originalDesc.getTestClass();
        if (testClass==null) throw new RuntimeException("Unexpected null testClass");

        String title = null;
        Annotation[] annotations = testClass.getAnnotations();
        for (Annotation a : annotations) {
            if (a instanceof Title) {
                title = ((Title) a).value();
                break;
            }
        }

        Description overridedDesc = Description.createSuiteDescription(
                title==null ? testClass.getName() : title
        );
        for(Description d: originalDesc.getChildren()) {
            overridedDesc.addChild(d);
        }

        return overridedDesc;

    }

    public void run(RunNotifier notifier) {
        sputnik.run(notifier);
    }

    public void filter(Filter filter) throws NoTestsRemainException {
        ((Filterable) sputnik).filter(filter);
    }

    public void sort(Sorter sorter) {
        ((Sortable) sputnik).sort(sorter);
    }

}
