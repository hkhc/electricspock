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
            ShadowMap oldShadowMap = sdkEnvironment.replaceShadowMap(shadowMap);
            Set<String> invalidatedClasses = shadowMap.getInvalidatedClasses(oldShadowMap);
            sdkEnvironment.getShadowInvalidator().invalidateClasses(invalidatedClasses);
        }

        ClassHandler classHandler = getClassHandler(sdkEnvironment, shadowMap);
        injectEnvironment(sdkEnvironment.getRobolectricClassLoader(), classHandler, sdkEnvironment.getShadowInvalidator());
    }

    private ShadowMap createShadowMap() {
        return ShadowMap.EMPTY;
    }

    private ClassHandler getClassHandler(SdkEnvironment sdkEnvironment, ShadowMap shadowMap) {
        ClassHandler classHandler;
        synchronized (sdkEnvironment) {
            classHandler = sdkEnvironment.classHandlersByShadowMap.get(shadowMap);
            if (classHandler == null) {
                classHandler = new ShadowWrangler(shadowMap);
            }
        }
        return classHandler;
    }

    private void injectEnvironment(ClassLoader robolectricClassLoader,
                                         ClassHandler classHandler, ShadowInvalidator invalidator) {
        String className = RobolectricInternals.class.getName();
        Class<?> robolectricInternalsClass = ReflectionHelpers.loadClass(robolectricClassLoader, className);
        ReflectionHelpers.setStaticField(robolectricInternalsClass, "classHandler", classHandler);
        ReflectionHelpers.setStaticField(robolectricInternalsClass, "shadowInvalidator", invalidator);
    }


}
