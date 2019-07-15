plugins {
    java
}

dependencies {
    implementation(project(":parser"))
    
    implementation("org.apache.commons", "commons-text", "1.7")
    implementation("com.codepoetics", "protonpack", "1.13")

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