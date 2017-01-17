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
import org.robolectric.internal.GradleManifestFactory;
import org.robolectric.internal.ManifestFactory;
import org.robolectric.internal.ManifestIdentifier;
import org.robolectric.internal.MavenManifestFactory;
import org.robolectric.manifest.AndroidManifest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by herman on 28/12/2016.
 */
public class AndroidManifestFactory {

    private static final Map<ManifestIdentifier, AndroidManifest> appManifestsCache = new HashMap<>();

    /*
            Create AndroidManifest object based on Robolectric config,
            and cached it globally
         */
    public AndroidManifest getAppManifest(Config config) {
        ManifestFactory manifestFactory = getManifestFactory(config);
        ManifestIdentifier identifier = manifestFactory.identify(config);

        synchronized (appManifestsCache) {
            AndroidManifest appManifest;
            appManifest = appManifestsCache.get(identifier);
            if (appManifest == null) {
                appManifest = manifestFactory.create(identifier);
                appManifestsCache.put(identifier, appManifest);
            }

            return appManifest;
        }
    }

    public int pickSdkVersion(Config config, AndroidManifest manifest) {
        if (config != null && config.sdk().length > 1) {
            throw new IllegalArgumentException("RobolectricTestRunner does not support multiple values for @Config.sdk");
        } else if (config != null && config.sdk().length == 1) {
            return config.sdk()[0];
        } else {
            return manifest.getTargetSdkVersion();
        }
    }

    /**
     * Detects what build system is in use and returns the appropriate ManifestFactory implementation.
     * @param config Specification of the SDK version, manifest file, package name, etc.
     */
    private ManifestFactory getManifestFactory(Config config) {
        if (config.constants() != null && config.constants() != Void.class) {
            return new GradleManifestFactory();
        } else {
            return new MavenManifestFactory();
        }
    }



}
