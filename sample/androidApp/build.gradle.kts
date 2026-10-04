plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
}

android {
  namespace = "io.github.imanih20.datetimewheelpicker.androidapp"
  compileSdk = 37

  defaultConfig {
    applicationId = "dev.darkokoa.datetimewheelpicker.androidApp"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0.0"
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}

dependencies {
  implementation(projects.sample.composeApp)
  implementation(libs.androidx.activityCompose)
  implementation(libs.compose.ui)
}
