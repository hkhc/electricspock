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

package hkhc.electricspock.internal;

import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by herman on 28/12/2016.
 */

public class ConfigFactory {

    private static final String CONFIG_PROPERTIES = "robolectric.properties";

    public Config getConfig(Class testClass, Method method) {
        Config config = new Config.Builder().build();

        Config globalConfig = buildGlobalConfig();
        if (globalConfig != null) {
            config = new Config.Implementation(config, globalConfig);
        }

        if (method!=null) {
            Config methodClassConfig = method.getDeclaringClass().getAnnotation(Config.class);
            if (methodClassConfig != null) {
                config = new Config.Implementation(config, methodClassConfig);
            }
        }

        ArrayList<Class> testClassHierarchy = new ArrayList<>();
//        Class testClass = getTestClass().getJavaClass();

        while (testClass != null) {
            testClassHierarchy.add(0, testClass);
            testClass = testClass.getSuperclass();
        }

        for (Class clazz : testClassHierarchy) {
            Config classConfig = (Config) clazz.getAnnotation(Config.class);
            if (classConfig != null) {
                config = new Config.Implementation(config, classConfig);
            }
        }

        if (method!=null) {
            Config methodConfig = method.getAnnotation(Config.class);
            if (methodConfig != null) {
                config = new Config.Implementation(config, methodConfig);
            }
        }

        return config;
    }

    /**
     * Generate the global {@link Config}. More specific test class and test method configurations
     * will override values provided here.
     *
     *
     * The returned object is likely to be reused for many tests.
     *
     * @return global {@link Config} object
     */
    private Config buildGlobalConfig() {
        return Config.Implementation.fromProperties(getConfigProperties());
    }

    private Properties getConfigProperties() {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream resourceAsStream = classLoader.getResourceAsStream(CONFIG_PROPERTIES)) {
            if (resourceAsStream == null) return null;
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
