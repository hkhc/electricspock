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

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.internal.dependency.DependencyResolver;

/**
 * Created by herman on 28/12/2016.
 * Borrow code from RobolectricTrestRunner
 */

public class DependencyResolverFactory {

    private static DependencyResolver dependencyResolver;

    private class BorrowRobolectricTestRunner extends RobolectricTestRunner {
        public BorrowRobolectricTestRunner() throws InitializationError {
            super(null);
        }
        protected DependencyResolver getJarResolver() {
            return super.getJarResolver();
        }
    }

    private BorrowRobolectricTestRunner borrowRobolectricTestRunner = null;

    public DependencyResolverFactory() {
        try {
            borrowRobolectricTestRunner = new BorrowRobolectricTestRunner();
        }
        catch (InitializationError e) {
            // do nothing
        }
    }

    public DependencyResolver getJarResolver() {
        if (dependencyResolver==null) {
            dependencyResolver = borrowRobolectricTestRunner.getJarResolver();
        }
        return dependencyResolver;
    }



}
