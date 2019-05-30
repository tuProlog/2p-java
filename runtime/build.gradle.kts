plugins {
    `java-library`
}

version = rootProject.version
group = rootProject.group

dependencies {
    api(project(":core"))
//    api(files(*listOf("jade", "jadeSecurity").map { "lib/$it.jar" }.toTypedArray()))
//
//    implementation("com.google.code.gson", "gson", "2.8.5")
    implementation("org.glassfish.corba", "glassfish-corba", "4.2.1")

    testImplementation("junit", "junit", "4.12")
    testImplementation("org.concordion", "concordion", "2.+")
    testImplementation("org.concordion", "concordion-embed-extension", "1.2.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}