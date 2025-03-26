import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "1.9.21"
    alias(libs.plugins.kobweb.application)
    alias(libs.plugins.kobwebx.markdown)
}

group = "org.example.app"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            description.set("Powered by Kobweb")
        }
    }
}

kotlin {
    // This example is frontend only. However, for a fullstack app, you can uncomment the includeServer parameter
    // and the `jvmMain` source set below.
    configAsKobwebApplication("app" /*, includeServer = true*/)

    sourceSets {
//        commonMain.dependencies {
//          // Add shared dependencies between JS and JVM here if building a fullstack app
//        }

        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
            implementation("com.juul.kable:kable-core:0.36.1")
            implementation("com.juul.kable:kable-exceptions:0.34.0")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")// ✅ Latest serialization library
            implementation("com.benasher44:uuid:0.8.4") 
            // This default template uses built-in SVG icons, but what's available is limited.
            // Uncomment the following if you want access to a large set of font-awesome icons:
            // implementation(libs.silk.icons.fa)
            implementation(libs.kobwebx.markdown)
            
            // https://mvnrepository.com/artifact/org.jetbrains.compose.runtime/runtime
            runtimeOnly("org.jetbrains.compose.runtime:runtime:1.7.3")
            // https://mvnrepository.com/artifact/org.jetbrains.compose.web/web-core
            implementation("org.jetbrains.compose.web:web-core:1.7.3")

            
                // 🔹 Add React wrappers for frontend UI
            implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.405")
            implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.405")
        }

        // Uncomment the following if you pass `includeServer = true` into the `configAsKobwebApplication` call.
//        jvmMain.dependencies {
//            compileOnly(libs.kobweb.api) // Provided by Kobweb backend at runtime
//        }
    }
}
