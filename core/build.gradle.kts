plugins {
    java
}

version = rootProject.version
group = rootProject.group

dependencies {
    implementation(project(":parser"))
    implementation("com.google.code.gson", "gson", "2.8.5")
    implementation("org.apache.commons", "commons-lang3", "3.8.1")
    implementation("mx4j", "mx4j-tools", "3.0.1")

    testImplementation("junit", "junit", "4.12")
    testImplementation("pl.pragmatists", "JUnitParams", "1.1.1")
    testImplementation("org.concordion", "concordion", "2.+")
    testImplementation("org.concordion", "concordion-embed-extension", "1.2.0")
}

tasks.getByName<Test>("test") {
    // display test output on console
    testLogging.showStandardStreams = true
    // write output to build/reports/spec
    systemProperties["concordion.output.dir"] = "${reporting.baseDir}/spec"
    // force tests to run even if code hasn't changed
    outputs.upToDateWhen { false }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}