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

import org.spockframework.runtime.extension.AbstractMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;
import org.spockframework.runtime.model.SpecInfo;

import hkhc.electricspock.ElectricSputnik;

/**
 * Created by herman on 27/12/2016.
 */

public class ElectricSpockInterceptor extends AbstractMethodInterceptor {

    private ContainedRobolectricTestRunner containedTestRunner;

    public ElectricSpockInterceptor(SpecInfo spec,
                                    ContainedRobolectricTestRunner containedRobolectricTestRunner) {
        this.containedTestRunner = containedRobolectricTestRunner;

        spec.addInterceptor(this);
    }



    /**
        Migrate from RobolectricTestRunner.methodBlock
        Replace the classloader by Robolectric's when executing a specification. Restore it when
        execution finished.
        @param invocation The method invocation to be intercept
     */
    @Override
    public void interceptSpecExecution(IMethodInvocation invocation) throws Throwable {

        Thread.currentThread().setContextClassLoader(
                containedTestRunner.getContainedSdkEnvironment().getRobolectricClassLoader());

        try {
            containedTestRunner.containedBeforeTest();
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }

        // todo: this try/finally probably isn't right -- should mimic RunAfters? [xw]
        try {
            invocation.proceed();
        } finally {
            try {
                containedTestRunner.containedAfterTest();
            } finally {
                Thread.currentThread().setContextClassLoader(ElectricSputnik.class.getClassLoader());
            }
        }

    }

}
