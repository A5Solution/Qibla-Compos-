pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven(url = "https://jitpack.io") // Corrected syntax
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                // Do not change the username below.
                // This should always be `mapbox` (not your username).
                authentication.create<BasicAuthentication>("basic")
                username = "mapbox"
                // Use the secret token you stored in gradle.properties as the password
                password = "sk.eyJ1Ijoic2hhaHphaWItbWFsaWsiLCJhIjoiY2xzd3hrdGJpMWc5ejJwcGdyZXljMml1diJ9.VZr1mUXTkta9rG-M0r8_Bg"
            }
        }

    }


}

rootProject.name = "Kibla Direction"
include(":app")
