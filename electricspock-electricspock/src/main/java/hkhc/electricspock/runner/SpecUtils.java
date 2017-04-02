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

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by herman on 16/1/2017.
 */

class SpecUtils {

    /**
     * Find all inner Specification class in a class
     * @param kClass outer class
     * @return array of inner Specification classes
     */
    static Class<?>[] getSpecClasses(Class<?> kClass) {

        Class<?>[] declaredClasses = kClass.getDeclaredClasses();

        Class<?>[] filteredClasses = new Class<?>[declaredClasses.length+1];
        int count = 0;

        for(Class<?> cls : declaredClasses) {
            if (isJUnitClass(cls)) {
                filteredClasses[count++] = cls;
            }
        }

        Class<?>[] resultClasses = new Class<?>[count];
        System.arraycopy(filteredClasses,0, resultClasses, 0, count);

        return resultClasses;
    }

    static boolean isJUnitClass(Class<?> cls) {
        if (isDirectJUnitClass(cls)) {
            return true;
        }
        Class<?> superClass = cls.getSuperclass();
        if (superClass!=null) {
            return isJUnitClass(superClass);
        }
        else {
            return false;
        }
    }

    static boolean isDirectJUnitClass(Class<?> cls) {

        Annotation[] annotations = cls.getAnnotations();
        for(Annotation a : annotations) {
            if (a instanceof RunWith) {
                return true;
            }
        }
        Method[] methods = cls.getMethods();
        for(Method m : methods) {
            Annotation[] methodAnnos = m.getAnnotations();
            for(Annotation a : methodAnnos) {
                if (a instanceof Test) {
                    return true;
                }
            }
        }
        return false;

    }

    // TODO move it out of this class
    static boolean isExtendedFrom(Class<?> cls, Class<?> targetBaseClass) {

        if (cls==targetBaseClass) {
            return true;
        }
        else {
            Class<?> superClass = cls.getSuperclass();
            if (superClass==null) {
                return false;
            }
            else {
                return isExtendedFrom(superClass, targetBaseClass);
            }
        }

    }

}
