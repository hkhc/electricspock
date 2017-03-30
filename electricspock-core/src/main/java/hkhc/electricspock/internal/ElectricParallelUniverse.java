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

/**
 * Created by herman on 30/12/2016.
 * Hook
 */

import android.app.Application;
import android.app.LoadedApk;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Looper;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.ShadowsAdapter;
import org.robolectric.TestLifecycle;
import org.robolectric.android.fakes.RoboInstrumentation;
import org.robolectric.annotation.Config;
import org.robolectric.internal.ParallelUniverseInterface;
import org.robolectric.internal.SdkConfig;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.manifest.RoboNotFoundException;
import org.robolectric.res.Qualifiers;
import org.robolectric.res.ResName;
import org.robolectric.res.ResourceTable;
import org.robolectric.res.builder.DefaultPackageManager;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.Scheduler;

import java.lang.reflect.Method;
import java.security.Security;

class ElectricParallelUniverse implements ParallelUniverseInterface {
    private final ShadowsAdapter shadowsAdapter = Robolectric.getShadowsAdapter();

    private boolean loggingInitialized = false;
    private SdkConfig sdkConfig;

    @Override
    public void resetStaticState(Config config) {
        RuntimeEnvironment.setMainThread(Thread.currentThread());
        Robolectric.reset();

        if (!loggingInitialized) {
            shadowsAdapter.setupLogging();
            loggingInitialized = true;
        }
    }

    @Override
    public void setUpApplicationState(Method method, TestLifecycle testLifecycle, AndroidManifest appManifest,
                                      Config config, ResourceTable compileTimeResourceTable,
                                      ResourceTable appResourceTable,
                                      ResourceTable systemResourceTable) {
        ReflectionHelpers.setStaticField(RuntimeEnvironment.class, "apiLevel", sdkConfig.getApiLevel());

        RuntimeEnvironment.application = null;
        RuntimeEnvironment.setMasterScheduler(new Scheduler());
        RuntimeEnvironment.setMainThread(Thread.currentThread());

        DefaultPackageManager packageManager = new DefaultPackageManager();
        RuntimeEnvironment.setRobolectricPackageManager(packageManager);

        RuntimeEnvironment.setCompileTimeResourceTable(compileTimeResourceTable);
        RuntimeEnvironment.setAppResourceTable(appResourceTable);
        RuntimeEnvironment.setSystemResourceTable(systemResourceTable);

        initializeAppManifest(appManifest, appResourceTable, packageManager);
        packageManager.setDependencies(appManifest, appResourceTable);

        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.insertProviderAt(new BouncyCastleProvider(), 1);
        }

        String qualifiers = Qualifiers.addPlatformVersion(config.qualifiers(), sdkConfig.getApiLevel());
        qualifiers = Qualifiers.addSmallestScreenWidth(qualifiers, 320);
        qualifiers = Qualifiers.addScreenWidth(qualifiers, 320);
        Resources systemResources = Resources.getSystem();
        Configuration configuration = systemResources.getConfiguration();
        configuration.smallestScreenWidthDp = Qualifiers.getSmallestScreenWidth(qualifiers);
        configuration.screenWidthDp = Qualifiers.getScreenWidth(qualifiers);
        systemResources.updateConfiguration(configuration, systemResources.getDisplayMetrics());
        RuntimeEnvironment.setQualifiers(qualifiers);

        Class<?> contextImplClass = ReflectionHelpers.loadClass(getClass().getClassLoader(), shadowsAdapter.getShadowContextImplClassName());

        Class<?> activityThreadClass = ReflectionHelpers.loadClass(getClass().getClassLoader(), shadowsAdapter.getShadowActivityThreadClassName());
        // Looper needs to be prepared before the activity thread is created
        if (Looper.myLooper() == null) {
            Looper.prepareMainLooper();
        }
        ShadowLooper.getShadowMainLooper().resetScheduler();
        Object activityThread = ReflectionHelpers.newInstance(activityThreadClass);
        RuntimeEnvironment.setActivityThread(activityThread);

        ReflectionHelpers.setField(activityThread, "mInstrumentation", new RoboInstrumentation());
        ReflectionHelpers.setField(activityThread, "mCompatConfiguration", configuration);

        Context systemContextImpl = ReflectionHelpers.callStaticMethod(contextImplClass, "createSystemContext", ReflectionHelpers.ClassParameter.from(activityThreadClass, activityThread));

        final Application application = (Application) testLifecycle.createApplication(method, appManifest, config);
        RuntimeEnvironment.application = application;

        if (application != null) {
            shadowsAdapter.bind(application, appManifest);

            ApplicationInfo applicationInfo;
            try {
                applicationInfo = packageManager.getApplicationInfo(appManifest.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }

            Class<?> compatibilityInfoClass = ReflectionHelpers.loadClass(getClass().getClassLoader(), "android.content.res.CompatibilityInfo");

            LoadedApk loadedApk = ReflectionHelpers.callInstanceMethod(activityThread, "getPackageInfo",
                    ReflectionHelpers.ClassParameter.from(ApplicationInfo.class, applicationInfo),
                    ReflectionHelpers.ClassParameter.from(compatibilityInfoClass, null),
                    ReflectionHelpers.ClassParameter.from(int.class, Context.CONTEXT_INCLUDE_CODE));

            try {
                Context contextImpl = systemContextImpl.createPackageContext(applicationInfo.packageName, Context.CONTEXT_INCLUDE_CODE);
                ReflectionHelpers.setField(activityThreadClass, activityThread, "mInitialApplication", application);
                org.robolectric.android.ApplicationTestUtil.attach(application, contextImpl);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }

            Resources appResources = application.getResources();
            ReflectionHelpers.setField(loadedApk, "mResources", appResources);
            ReflectionHelpers.setField(loadedApk, "mApplication", application);

            appResources.updateConfiguration(configuration, appResources.getDisplayMetrics());

            application.onCreate();
        }
    }

    private void initializeAppManifest(AndroidManifest appManifest, ResourceTable appResourceTable, DefaultPackageManager packageManager) {
        try {
            appManifest.initMetaData(appResourceTable);
        } catch (RoboNotFoundException e) {
//            throw new Resources.NotFoundException(e.getMessage(), e);
        }

        int labelRes = 0;
        if (appManifest.getLabelRef() != null) {
            String fullyQualifiedName = ResName.qualifyResName(appManifest.getLabelRef(), appManifest.getPackageName());
            Integer id = fullyQualifiedName == null ? null : appResourceTable.getResourceId(new ResName(fullyQualifiedName));
            labelRes = id != null ? id : 0;
        }
        packageManager.addManifest(appManifest, labelRes);
    }

    @Override
    public Thread getMainThread() {
        return RuntimeEnvironment.getMainThread();
    }

    @Override
    public void setMainThread(Thread newMainThread) {
        RuntimeEnvironment.setMainThread(newMainThread);
    }

    @Override
    public void tearDownApplication() {
        if (RuntimeEnvironment.application != null) {
            RuntimeEnvironment.application.onTerminate();
        }
    }

    @Override
    public Object getCurrentApplication() {
        return RuntimeEnvironment.application;
    }

    @Override
    public void setSdkConfig(SdkConfig sdkConfig) {
        this.sdkConfig = sdkConfig;
        ReflectionHelpers.setStaticField(RuntimeEnvironment.class, "apiLevel", sdkConfig.getApiLevel());
    }
}
