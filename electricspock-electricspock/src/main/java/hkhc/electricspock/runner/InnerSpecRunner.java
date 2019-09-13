/*
 * Copyright 2017 Herman Cheung
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

package hkhc.electricspock.runner;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.lang.annotation.Annotation;
import java.util.List;

import spock.lang.Title;

import static hkhc.electricspock.runner.SpecUtils.getSpecClasses;

/**
 * Created by herman on 15/1/2017.
 */

public class InnerSpecRunner extends Suite {

    public InnerSpecRunner(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        super(builder, klass, getSpecClasses(klass));
    }

    // Not expected to call
    public InnerSpecRunner(RunnerBuilder builder, Class<?>[] classes) throws InitializationError {
        super(builder, classes);
    }

    // Not expected to call
    public InnerSpecRunner(Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
        super(klass, suiteClasses);
    }

    // Not expected to call
    public InnerSpecRunner(RunnerBuilder builder, Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
        super(builder, klass, suiteClasses);
    }

    // Not expected to call
    public InnerSpecRunner(Class<?> klass, List<Runner> runners) throws InitializationError {
        super(klass, runners);
    }

    @Override
    public Description getDescription() {
        Description d = super.getDescription();
        dumpDescription(d);
        return d;
    }

    private void dumpDescription(Description d) {
        dumpDescription(0, d);
    }

    private void dumpDescription(int level, Description d) {
        for (Description c : d.getChildren()) {
            dumpDescription(level + 1, c);
        }
    }

    @Override
    protected Description describeChild(Runner child) {

        Description d = super.describeChild(child);
        Class<?> testClass = d.getTestClass();
        String title = null;
        Annotation[] annotations = null;
        if (testClass != null) {
            annotations = testClass.getAnnotations();
            for (Annotation a : annotations) {
                if (a instanceof Title) {
                    title = ((Title) a).value() + " (" + testClass.getName() + ")";
                    break;
                }
            }
        }
        if (title != null) {
            Description newD = Description.createSuiteDescription(title, annotations);
            for (Description childD : d.getChildren()) {
                newD.addChild(Description.createTestDescription(newD.getTestClass(), childD.getDisplayName()));
            }
            dumpDescription(newD);
            return newD;
        } else {
            System.out.println("title is null");
            return d;
        }
    }
}
