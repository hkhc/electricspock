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
import org.robolectric.annotation.Config;
import org.robolectric.internal.SdkConfig;
import org.robolectric.internal.SdkEnvironment;
import org.robolectric.internal.bytecode.ClassHandler;
import org.robolectric.internal.bytecode.InvokeDynamic;
import org.robolectric.internal.bytecode.RobolectricInternals;
import org.robolectric.internal.bytecode.ShadowInvalidator;
import org.robolectric.internal.bytecode.ShadowMap;
import org.robolectric.internal.bytecode.ShadowWrangler;
import org.robolectric.util.ReflectionHelpers;

import java.util.Set;

/**
 * Created by herman on 28/12/2016.
 * Migrate shadows configuration logic from RobolectricTestRunner
 */

public class ShadowMaker {

    public void configureShadows(SdkEnvironment sdkEnvironment, Config config) {
        ShadowMap shadowMap = createShadowMap();

        if (config != null) {
            Class<?>[] shadows = config.shadows();
            if (shadows.length > 0) {
                shadowMap = shadowMap.newBuilder().addShadowClasses(shadows).build();
            }
        }

        if (InvokeDynamic.ENABLED) {
            sdkEnvironment.replaceShadowMap(shadowMap);
        }

        ClassHandler classHandler = createClassHandler(shadowMap, sdkEnvironment.getSdkConfig());
        injectEnvironment(sdkEnvironment.getRobolectricClassLoader(), classHandler, sdkEnvironment.getShadowInvalidator());
    }

    private ShadowMap createShadowMap() {
        return ShadowMap.EMPTY;
    }

    /**
     * Create a {@link ClassHandler} appropriate for the given arguments.
     *
     * Robolectric may chose to cache the returned instance, keyed by <tt>shadowMap</tt> and <tt>sdkConfig</tt>.
     *
     * Custom TestRunner subclasses may wish to override this method to provide alternate configuration.
     *
     * @param shadowMap the {@link ShadowMap} in effect for this test
     * @param sdkConfig the {@link SdkConfig} in effect for this test
     * @return an appropriate {@link ClassHandler}. This implementation returns a {@link ShadowWrangler}.
     * @since robolectric-2.3
     */
    @NotNull
    protected ClassHandler createClassHandler(ShadowMap shadowMap, SdkConfig sdkConfig) {
        return new ShadowWrangler(shadowMap, sdkConfig.getApiLevel(), null);
    }


    private void injectEnvironment(ClassLoader robolectricClassLoader,
                                         ClassHandler classHandler, ShadowInvalidator invalidator) {
        String className = RobolectricInternals.class.getName();
        Class<?> robolectricInternalsClass = ReflectionHelpers.loadClass(robolectricClassLoader, className);
        ReflectionHelpers.setStaticField(robolectricInternalsClass, "classHandler", classHandler);
        ReflectionHelpers.setStaticField(robolectricInternalsClass, "shadowInvalidator", invalidator);
    }


}
