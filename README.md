# ElectricSpock

The Android test framework [Robolectric](https://github.com/robolectric/robolectric) is designed with JUnit in mind and it does not work well with Spock framework. Fortunately there is a project [RoboSpock](https://github.com/robospock/RoboSpock) is designed for that. It configure Robolectric properly under the framework of Spock framework. However it based on some internal source code of Robolectric and it has no official support to Robolectric 3.1 yet. There is an [issue](https://github.com/robospock/RoboSpock/issues/59) for that, but it has been quite some time. So I decided to make my own.

It is heavily based on RoboSpock project. It borrow a lot of code from there, and make some tweak of my own. This project is never possible without the excellent foundation.

Current version of the library is tested with Robolectric 3.1.4. 

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

Add the dependency

```groovy
	dependencies {
		compile 'com.github.hkhc:electricspock:0.1'
	}
```

Spock, Robolectric and Groovy are dependencies of ElectricSpock, so it should work without adding these dependencies to build.gradle, but you may override them with the version you prefer.

Then we may just write Spock specification with `ElectricSpecification` class and Robolectric's `@Config` annotation

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