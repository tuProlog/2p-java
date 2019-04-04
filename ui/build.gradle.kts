plugins {
    `java-library`
    application
}

version = rootProject.version
group = rootProject.group

dependencies {
    api(project(":core"))

    testImplementation("junit", "junit", "4.12")
    testImplementation("org.concordion", "concordion", "2.+")
    testImplementation("org.concordion", "concordion-embed-extension", "1.2.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}