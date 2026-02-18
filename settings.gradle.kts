pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
    }

    // חשוב: למחוק את כל הבלוק הזה:
    // versionCatalogs { create("libs") { from(...) } }
    // Gradle טוען אוטומטית את gradle/libs.versions.toml
}

rootProject.name = "SpinWheelWidget"
include(":app")
include(":spinwheel")