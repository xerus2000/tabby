object Libs {

   const val kotlinVersion = "1.4.21"
   const val dokkaVersion = "0.10.1"

   object Kotest {
      private const val version = "4.3.2"
      const val assertions = "io.kotest:kotest-assertions-core-jvm:$version"
      const val junit5 = "io.kotest:kotest-runner-junit5-jvm:$version"
   }

   object Jackson {
      private const val version = "2.12.0"
      const val core = "com.fasterxml.jackson.core:jackson-core:$version"
      const val databind = "com.fasterxml.jackson.core:jackson-databind:$version"
      const val kotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:$version"
   }

   object Coroutines {
      private const val version = "1.4.1"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
      const val coreJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$version"
      const val coreJvm = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$version"
   }
}
