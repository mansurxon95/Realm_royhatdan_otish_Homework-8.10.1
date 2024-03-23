// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("io.realm.kotlin") version "1.14.0" apply false
}

buildscript {
    dependencies {
        //noinspection UseTomlInstead
        classpath("io.realm.kotlin:gradle-plugin:<1.12.0>-SNAPSHOT")
    }
}


