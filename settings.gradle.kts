pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/public") {
            content { includeGroupByRegex("com\\.chaquo.*") }
        }
        maven("https://maven.aliyun.com/repository/gradle-plugin") {
            content { includeGroupByRegex("org\\.jetbrains.*"); includeGroupByRegex("com\\.android.*"); includeGroupByRegex("com\\.chaquo.*") }
        }
        google {
            content { includeGroupByRegex("com\\.android.*"); includeGroupByRegex("com\\.google.*"); includeGroupByRegex("androidx.*") }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://maven.aliyun.com/repository/public") {
            content { includeGroupByRegex("com\\.chaquo.*") }
        }
        maven("https://maven.aliyun.com/repository/google") {
            content { includeGroupByRegex("com\\.android.*"); includeGroupByRegex("com\\.google.*"); includeGroupByRegex("androidx.*") }
        }
        maven("https://maven.aliyun.com/repository/public") {
            content { includeGroupByRegex("androidx.*"); includeGroupByRegex("org\\.jetbrains.*"); includeGroupByRegex("org\\.kotlinx.*") }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "PyNeon"
include(":app")
