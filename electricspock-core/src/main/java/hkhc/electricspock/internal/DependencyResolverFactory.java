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

import org.robolectric.internal.dependency.CachedDependencyResolver;
import org.robolectric.internal.dependency.DependencyResolver;
import org.robolectric.internal.dependency.LocalDependencyResolver;
import org.robolectric.internal.dependency.MavenDependencyResolver;
import org.robolectric.internal.dependency.PropertiesDependencyResolver;
import org.robolectric.res.Fs;
import org.robolectric.res.FsFile;
import org.robolectric.util.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by herman on 28/12/2016.
 */

public class DependencyResolverFactory {

    private static DependencyResolver dependencyResolver;

    public DependencyResolver getJarResolver() {
        if (dependencyResolver == null) {
            if (Boolean.getBoolean("robolectric.offline")) {
                String dependencyDir = System.getProperty("robolectric.dependency.dir", ".");
                dependencyResolver = new LocalDependencyResolver(new File(dependencyDir));
            } else {
                File cacheDir = new File(new File(System.getProperty("java.io.tmpdir")), "robolectric");

                if (cacheDir.exists() || cacheDir.mkdir()) {
                    Logger.info("Dependency cache location: %s", cacheDir.getAbsolutePath());
                    dependencyResolver = new CachedDependencyResolver(new MavenDependencyResolver(), cacheDir, 60 * 60 * 24 * 1000);
                } else {
                    dependencyResolver = new MavenDependencyResolver();
                }
            }

            URL buildPathPropertiesUrl = getClass().getClassLoader().getResource("robolectric-deps.properties");
            if (buildPathPropertiesUrl != null) {
                Logger.info("Using Robolectric classes from %s", buildPathPropertiesUrl.getPath());

                FsFile propertiesFile = Fs.fileFromPath(buildPathPropertiesUrl.getFile());
                try {
                    dependencyResolver = new PropertiesDependencyResolver(propertiesFile, dependencyResolver);
                } catch (IOException e) {
                    throw new RuntimeException("couldn't read " + buildPathPropertiesUrl, e);
                }
            }
        }

        return dependencyResolver;
    }



}
