enableFeaturePreview("GRADLE_METADATA")

pluginManagement {
   repositories {
      mavenCentral()
      gradlePluginPortal()
      maven("https://dl.bintray.com/kotlin/kotlin-eap")
      jcenter()
   }
}

include("tabby-core")

include("tabby-jackson")