plugins {
    `java-library`
    antlr
}

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.7.2")
//    api("org.javatuples:javatuples:1.2")
//    api("org.apache.commons:commons-collections4:4.3")
//    implementation("org.apache.commons:commons-lang3:3.8.1")
    testImplementation("pl.pragmatists:JUnitParams:1.1.1")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments = arguments + listOf("-visitor", "-long-messages")
    outputDirectory = File("${project.buildDir}/generated-src/antlr/main/alice/tuprolog/parser")
}

