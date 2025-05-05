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
    // Evita repositorios definidos a nivel de proyecto en cada módulo
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Repositorios donde buscar las librerías de Android y CameraX
        google()
        mavenCentral()
    }
}

// Nombre del proyecto raíz
rootProject.name = "Examen2ndoparcial"
include(":app")
