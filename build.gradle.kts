// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    kotlin("jvm") version "2.0.0" // update version
    kotlin("plugin.serialization") version "2.0.0" // update version
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id ("com.google.devtools.ksp") version "2.0.0-1.0.22" apply false
}