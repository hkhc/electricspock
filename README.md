# ElectricSpock

[![Release](https://jitpack.io/v/hkhc/electricspock.svg)](https://jitpack.io/#hkhc/electricspock)
[![Travis CI Build Status](https://travis-ci.org/hkhc/electricspock.svg?branch=master)](https://travis-ci.org/hkhc/electricspock)

## What's new

<<<<<<< HEAD
The latest version is 0.7.1. Past history of the library is [over there](history.md).

Version 0.7.1 works with Robolectric 3.6.1. (It does not work with Robolectric 3.6)

_IMPORTANT_: Starting from version 0.7, the library will no longer expose the dependent library implicitly.
=======
The latest version is 0.8. Past history of the library is [over there](history.md).

Version 0.8 tested with Robolectric 3.7 and Android Gradle Plugin 3.0.1

_IMPORTANT_: Starting from ElectricSpock 0.7, the library will no longer expose the dependent library implicitly.
>>>>>>> - Update for Robolectric 3.7
This means you have to add dependencies of Robolectric, Spock Framework and Groovy explicitly.
This reduce the chances of version conflict in future. See [Installation](#installation-gradle) for details.

For those who stick to Robolectric 3.2, please use version 0.4.1.
For those who stick to Robolectric 3.1, please use version 0.1.

## About

The Android test framework [Robolectric](https://github.com/robolectric/robolectric) is designed with JUnit in mind and it does not work well with Spock framework. Fortunately there is a project [RoboSpock](https://github.com/robospock/RoboSpock) is designed for that. It configure Robolectric properly under the framework of Spock framework. However it based on some internal source code of Robolectric and it has no official support to Robolectric 3.1 yet. There is an [issue](https://github.com/robospock/RoboSpock/issues/59) for that, but it has been quite some time. So I decided to make my own.

It is heavily based on RoboSpock project. It borrow a lot of code from there, and make some tweak of my own. This project is never possible without the excellent foundation.

<<<<<<< HEAD
Current version (0.7.1) of the library is tested with Robolectric 3.6.1.
=======
Current version (0.8) of the library is tested with Robolectric 3.7.
>>>>>>> - Update for Robolectric 3.7

# Installation (Gradle)

The archive of this project is deployed with [jitpack](https://jitpack.io). Add the following to your gradle build script.

Add it in your root build.gradle at the end of repositories:

```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the dependencies

```groovy
	// AGP 3.0
	dependencies {
		testImplementation 'com.github.hkhc:electricspock:0.8'
		testImplementation 'org.robolectric:robolectric:3.7.1'
		testImplementation 'org.robolectric:shadows-support-v4:3.4-rc2'
		testImplementation 'org.codehaus.groovy:groovy-all:2.4.12'
		testImplementation 'org.spockframework:spock-core:1.1-groovy-2.4'
	}
```
```groovy
	// pre-AGP 3.0
	dependencies {
		testCompile 'com.github.hkhc:electricspock:0.8'
		testCompile 'org.robolectric:robolectric:3.7.1'
		testCompile 'org.robolectric:shadows-support-v4:3.4-rc2'
		testCompile 'org.codehaus.groovy:groovy-all:2.4.12'
		testCompile 'org.spockframework:spock-core:1.1-groovy-2.4'
	}
```

Then we may just write Spock specification with `ElectricSpecification`
class and Robolectric's `@Config` annotation

```groovy

@Config(constants=BuildConfig)
class MySpec extends ElectricSpecification {

    def "Robolectric is enabled"() {

        when: "invoking call to Android API"
            android.util.Log.d("TAG", "Hello world")

        then: "there should not be any error"
            notThrown Exception

    }

}

```

# ElectricSuite

The class `ElectricSuite` is a helper class to help organize test methods of Specification into group.
It is essentially a test class with `Suite` runner. However we don't need to use `@SuiteClasses` annotation
to specify test classes. Instead the class scan all static inner class as test classes.

For example

```groovy

class MySpec extends ElectricSuite {

    static class MyInnerSpec1 extends ElectricSpecification {
        [....]
    }

    static class MyInnerSpec2 extends ElectricSpecification [
        [....]
    }

}

```

Please note that there are a few limitations:

* We cannot have any test method in the `ElectricSuite` class. All test methods shall be in the inner test classes
of `ElectricSuite`.

* (ElecrtricSpock 0.5 fixed this) ~~When using with [Spock-reporting-plugin](https://github.com/renatoathaydes/spock-reports), all inner classes
of the same `ElectricSuite` class shall have the same base class. i.e. Either all of them extend from `ElectricSpecification`
or `Specification`. Mix of different base classes will cause exception in the reporting plugin.
It will be something like `OverlappingFileLockException`.~~
