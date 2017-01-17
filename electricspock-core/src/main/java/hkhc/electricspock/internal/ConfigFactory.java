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

import org.jetbrains.annotations.NotNull;
import org.robolectric.ConfigMerger;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by herman on 28/12/2016.
 * Migrate the config creation code from RobolectricTestRunner
 */

public class ConfigFactory {

    private static final String CONFIG_PROPERTIES = "robolectric.properties";
    private final ConfigMerger configMerger;

    public ConfigFactory() {

        this.configMerger = createConfigMerger();

    }

    public Config getConfig(Class testClass) {

        // A hack to create an arbitrary method object for ConfigMerger.
        // With Spock we cannot have per-method robolectric config,
        // and ConfigMerge expect a method object to check Config annotation. However, it does
        // not do a null check. Just we pass something (which has no @Config) to claim it down.
        Method method = null;
        try {
            method = Object.class.getMethod("wait");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }

        return configMerger.getConfig(testClass, method, buildGlobalConfig());

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
        return Config.Builder.defaults().build();
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

    /**
     * Create a {@link ConfigMerger} for calculating the {@link Config} tests.
     *
     * Custom TestRunner subclasses may wish to override this method to provide alternate configuration.
     *
     * @return an {@link ConfigMerger}.
     * @since robolectric-3.2
     */
    @NotNull
    private ConfigMerger createConfigMerger() {
        return new ConfigMerger();
    }

}
