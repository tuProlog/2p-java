plugins {
    `java-library`
    antlr
}

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr", "antlr4", "4.13.1")

    api("org.antlr", "antlr4-runtime", "4.13.1")
    
    testImplementation("pl.pragmatists:JUnitParams:1.1.1")
}

tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments = arguments + listOf("-visitor", "-long-messages")
    val buildDir = project.layout.buildDirectory.asFile.getOrElse(project.buildFile.parentFile)
    outputDirectory = File("$buildDir/generated-src/antlr/main/alice/tuprolog/parser")
}

tasks.classes {
    dependsOn(tasks.generateGrammarSource)
}
