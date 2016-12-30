# ElectricSpock

The Android test framework [Robolectric](https://github.com/robolectric/robolectric) is designed with JUnit in mind and it does not work well with Spock framework. Fortunately there is a project [RoboSpock](https://github.com/robospock/RoboSpock) is designed for that. It configure Robolectric properly under the framework of Spock framework. However it based on some internal source code of Robolectric and it has no official support to Robolectric 3.1 yet. So I decide to make my own.

It is heavily based on RoboSpock project. It borrow a lot of code from there, and make some tweak of my own.

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
