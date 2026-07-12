pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        google()
        mavenCentral()
    }
}

rootProject.name = "oldpods"

include(":app")
include(":core:model")
include(":core:network")
include(":core:repository")
include(":core:common")
include(":feature:home")
include(":feature:dashboard")
include(":feature:detail")
include(":feature:search")
include(":feature:download")
include(":feature:login")
include(":feature:forum")
include(":feature:notifications")
include(":feature:ai")
include(":feature:update")
