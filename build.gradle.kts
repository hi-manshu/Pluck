// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath(Deps.Gradle.androidGradlePlugin)
        classpath(Deps.Gradle.kotlinGradlePlugin)
        classpath(Deps.Gradle.ktlintGradlePlugin)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
subprojects {
    apply(plugin = Plugins.ktlint)
}

