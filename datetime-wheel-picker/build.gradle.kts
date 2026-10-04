//import com.google.devtools.ksp.gradle.KspTaskMetadata
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.multiplatform)
  alias(libs.plugins.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.android.kotlin.multiplatform.library)
  alias(libs.plugins.maven.publish)
  alias(libs.plugins.ksp)
}

// The Gradle project identity below is only the IDE/`:dependencies` label — the
// published Maven coordinates come from the mavenPublishing `pom {}` DSL plus the
// ORG_GRADLE_PROJECT_* properties the release workflow passes in, so the artifact
// ships under io.github.imanih20 and never under dev.darkokoa.
group = "io.github.imanih20"
version = "1.5.0"

kotlin {
  applyDefaultHierarchyTemplate()

  android {
    namespace = "dev.darkokoa.datetimewheelpicker"
    compileSdk = 37
    minSdk = 21

    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
    }

    withHostTest {}
  }

  jvm {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
    }
  }

  js {
    browser()
    binaries.executable()
  }

  @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
  wasmJs {
    browser()
    binaries.executable()
  }

  listOf(
    iosArm64(),
    iosSimulatorArm64()
  ).forEach {
    it.binaries.framework {
      baseName = "ComposeApp"
      isStatic = true
    }
  }

  sourceSets {
    all {
      languageSettings {
        optIn("kotlin.time.ExperimentalTime")
      }
    }
    commonMain.dependencies {
      implementation(libs.compose.runtime)
      implementation(libs.compose.foundation)
      implementation(libs.compose.ui)
      implementation(libs.compose.material3)
      implementation(libs.kotlinx.datetime)

      implementation(libs.lyricist)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))
    }

    androidMain.dependencies {
      implementation(libs.persianDate)
    }

    jvmMain.dependencies {
    }

    jvmTest.dependencies {
      implementation(libs.compose.ui.test)
      implementation(compose.desktop.currentOs)
    }

    jsMain.dependencies {
    }

    wasmJsMain.dependencies {
    }

    iosMain.dependencies {
    }

  }
}

dependencies {
  add("kspCommonMainMetadata", libs.lyricist.processor)
}

//kotlin.sourceSets.commonMain {
//  tasks.withType<KspTaskMetadata> { kotlin.srcDir(destinationDirectory) }
//}

// Add task dependencies after plugin configuration is complete
afterEvaluate {
  // Make all compilation tasks depend on KSP
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
      dependsOn("kspCommonMainKotlinMetadata")
    }
  }

  // Make all tasks depend on KSP if their names contain specific patterns
  tasks.configureEach {
    if (name.contains("SourcesJar") || name == "sourcesJar") {
      dependsOn("kspCommonMainKotlinMetadata")
      logger.info("Added KSP dependency to task: $name")
    }
  }
}

kotlin.sourceSets.commonMain {
  kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}

ksp {
  arg("lyricist.internalVisibility", "true")
  arg("lyricist.packageName", "dev.darkokoa.datetimewheelpicker")
}

mavenPublishing {
  publishToMavenCentral(automaticRelease = true)
  signAllPublications()

  // POM metadata is declared here and nowhere else. The Vanniktech plugin also
  // reads POM_* properties from gradle.properties, and those lists are APPENDED
  // to the ones set below rather than replacing them — having both produced a POM
  // with two developer and two license entries. Keep this block authoritative.
  pom {
    name.set("Datetime Wheel Picker")
    description.set("A datetime wheel picker for Compose Multiplatform.")
    inceptionYear.set("2026")
    url.set("https://github.com/imanih20/compose-datetime-wheel-picker")

    licenses {
      license {
        name.set("The Apache License, Version 2.0")
        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
        distribution.set("repo")
      }
    }
    developers {
      developer {
        id.set("imanih20")
        name.set("mohyeddin")
        url.set("https://github.com/imanih20")
      }
    }
    scm {
      url.set("https://github.com/imanih20/compose-datetime-wheel-picker")
      connection.set("scm:git:git://github.com/imanih20/compose-datetime-wheel-picker.git")
      developerConnection.set("scm:git:ssh://git@github.com:imanih20/compose-datetime-wheel-picker.git")
    }
  }
}

// https://youtrack.jetbrains.com/issue/CMP-4906
tasks.withType<org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest> {
  enabled = false
}

tasks.withType<Test> {
  failOnNoDiscoveredTests.set(false)
}
