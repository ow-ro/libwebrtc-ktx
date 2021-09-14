import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URI

plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.dokka")
    id("maven-publish")
}

object Maven {
    const val groupId = "io.github.ow-ro.libwebrtc"
    const val artifactId = "libwebrtc-ktx"
    const val name = "libwebrtc-ktx"
    const val desc = "Libwebrtc Kotlin Extensions"
    const val version = "1.1.2"
    const val siteUrl = "https://github.com/ow-ro/libwebrtc-ktx"
    const val issueTrackerUrl = "https://github.com/ow-ro/libwebrtc-ktx/issues"
    const val gitUrl = "https://github.com/ow-ro/libwebrtc-ktx.git"
    const val githubRepo = "ow-ro/libwebrtc-ktx"
    const val licenseName = "The Apache Software License, Version 2.0"
    const val licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0.txt"
    const val licenseDist = "repo"
}

group = Maven.groupId
version = Maven.version

android {
    buildToolsVersion = "31.0.0"
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-proguard-rules.pro")
    }

    lint {
        textReport = true
        textOutput("stdout")
    }

    libraryVariants.all {
        generateBuildConfigProvider?.configure {
            enabled = false
        }
    }

    buildTypes {
        getByName("debug") {
            isJniDebuggable = true
        }
        getByName("release") {
            isJniDebuggable = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-module-name", "libwebrtc-ktx")
            jvmTarget = "1.8"
            apiVersion = "1.5"
            languageVersion = "1.5"
        }
    }
}

dependencies {
    api(kotlin("stdlib"))
    compileOnly("com.github.ow-ro:libwebrtc-bin:_")
}

val sourcesJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    archiveClassifier.set("sources")
    from(sourceSets.create("main").allSource)
}

val customDokkaTask by tasks.creating(DokkaTask::class) {
    dokkaSourceSets.getByName("main") {
        noAndroidSdkLink.set(false)
    }
    dependencies {
        plugins("org.jetbrains.dokka:javadoc-plugin:_")
    }
    inputs.dir("src/main/java")
    outputDirectory.set(buildDir.resolve("javadoc"))
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn(customDokkaTask)
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles JavaDoc JAR"
    archiveClassifier.set("javadoc")
    from(customDokkaTask.outputDirectory)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])
                groupId = Maven.groupId
                artifactId = Maven.artifactId
                version = Maven.version

                println("""
                    |Creating maven publication
                    |    Group: $groupId
                    |    Artifact: $artifactId
                    |    Version: $version
                """.trimMargin())

                artifact(sourcesJar)
                artifact(javadocJar)
            }
        }
    }
}

    tasks {
        withType<Test> {
            useJUnitPlatform()
            testLogging {
                showStandardStreams = true
                events("passed", "skipped", "failed")
            }
        }
    }
}
