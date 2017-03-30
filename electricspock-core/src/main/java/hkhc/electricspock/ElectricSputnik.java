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

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.SandboxFactory;
import org.robolectric.internal.SdkConfig;
import org.robolectric.internal.SdkEnvironment;
import org.robolectric.internal.bytecode.SandboxConfig;
import org.robolectric.internal.dependency.DependencyResolver;
import org.robolectric.manifest.AndroidManifest;
import org.spockframework.runtime.Sputnik;
import org.spockframework.runtime.model.SpecInfo;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import hkhc.electricspock.internal.AndroidManifestFactory;
import hkhc.electricspock.internal.ConfigFactory;
import hkhc.electricspock.internal.ElectricSpockInterceptor;
import spock.lang.Title;

import static java.util.Arrays.asList;

/**
 * Created by herman on 27/12/2016.
 * Test Runner
 */

public class ElectricSputnik extends RobolectricTestRunner {

    private Object sputnik;

    static {
        new SecureRandom(); // this starts up the Poller SunPKCS11-Darwin thread early, outside of any Robolectric classloader
    }

    public static class AAA {
        @Test
        public void testOne() {

        }
    }

    public ElectricSputnik(Class<?> testClass)  throws InitializationError {

        super(AAA.class);

        checkRobolectricVersion();

        Config config = (new ConfigFactory()).getConfig(testClass);

        AndroidManifestFactory androidManifestFactory = new AndroidManifestFactory();
        AndroidManifest appManifest = androidManifestFactory.getAppManifest(config);

//        DependencyResolverFactory dependencyResolverFactory = new DependencyResolverFactory();

        List<FrameworkMethod> childs = getChildren();

        FrameworkMethod placeholder = childs.get(0);

            SdkEnvironment sdkEnvironment = getSandbox(testClass, config, placeholder);
        configureShadows(placeholder, sdkEnvironment);
        Class bootstrappedTestClass = sdkEnvironment.bootstrappedClass(testClass);

        // Since we have bootstrappedClass we may properly initialize

        try {

            this.sputnik = sdkEnvironment
                    .bootstrappedClass(Sputnik.class)
                    .getConstructor(Class.class)
                    .newInstance(bootstrappedTestClass);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // let's manually add our initializers

        for(Method method : sputnik.getClass().getDeclaredMethods()) {
            if(method.getName().equals("getSpec")) {
                method.setAccessible(true);
                try {
                    Object spec = method.invoke(sputnik);

                    // ElectricSpockInterceptor self-register on construction, no need to keep a ref here

                    sdkEnvironment
                            .bootstrappedClass(ElectricSpockInterceptor.class)
                            .getConstructor(
                                    sdkEnvironment.bootstrappedClass(SpecInfo.class),
                                    SdkEnvironment.class,
                                    Config.class,
                                    AndroidManifest.class,
                                    DependencyResolver.class
                            )
                            .newInstance(spec, sdkEnvironment, config, appManifest, getJarResolver());

                } catch (IllegalAccessException | InstantiationException |
                        NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    @NotNull
    protected SdkEnvironment getSandbox(Class<?> testClass, Config config, FrameworkMethod method) {

//        DependencyResolverFactory dependencyResolverFactory = new DependencyResolverFactory();
        AndroidManifestFactory androidManifestFactory = new AndroidManifestFactory();
        AndroidManifest appManifest = androidManifestFactory.getAppManifest(config);
        SdkConfig sdkConfig = new SdkConfig(androidManifestFactory.pickSdkVersion(config, appManifest));

        return SandboxFactory.INSTANCE.getSdkEnvironment(
                createClassLoaderConfig(method), getJarResolver(), sdkConfig);

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
        if (!(ver.equals("3.3") ||
                ver.indexOf("3.3.")==0 ||
                ver.indexOf("3.3-")==0))
            throw new RuntimeException("This version of ElectricSpock supports Robolectric 3.3 only");
    }

    @NotNull
    protected Class<?>[] getExtraShadows(FrameworkMethod method) {
        List<Class<?>> shadowClasses = new ArrayList<>();
        addShadows(shadowClasses, getTestClass().getAnnotation(SandboxConfig.class));
        addShadows(shadowClasses, method.getAnnotation(SandboxConfig.class));
        return shadowClasses.toArray(new Class[shadowClasses.size()]);
    }

    private void addShadows(List<Class<?>> shadowClasses, SandboxConfig annotation) {
        if (annotation != null) {
            shadowClasses.addAll(asList(annotation.shadows()));
        }
    }

    public Description getDescription() {

        Description originalDesc = ((Runner) sputnik).getDescription();

        Class<?> testClass = originalDesc.getTestClass();
        String title = null;
       Annotation[] annotations = null;
        if (testClass!=null) {
            annotations = testClass.getAnnotations();
            for (Annotation a : annotations) {
                if (a instanceof Title) {
                    title = ((Title) a).value();
                    break;
                }
            }
        }


        Description overridedDesc = Description.createSuiteDescription(
                title==null ? testClass.getName() : title
        );
        for(Description d : originalDesc.getChildren()) {
            overridedDesc.addChild(d);
        }
        return overridedDesc;

    }

    public void run(RunNotifier notifier) {
        ((Runner) sputnik).run(notifier);
    }

    public void filter(Filter filter) throws NoTestsRemainException {
        ((Filterable) sputnik).filter(filter);
    }

    public void sort(Sorter sorter) {
        ((Sortable) sputnik).sort(sorter);
    }

}
