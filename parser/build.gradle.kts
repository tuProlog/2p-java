plugins {
    `java-library`
    antlr
}

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr", "antlr4", "4.7.2")

    api("org.antlr", "antlr4-runtime", "4.7.2")
    
    testImplementation("pl.pragmatists:JUnitParams:1.1.1")
}

configurations {
    compile {
        setExtendsFrom(emptyList())
    }
}

tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments = arguments + listOf("-visitor", "-long-messages")
    outputDirectory = File("${project.buildDir}/generated-src/antlr/main/alice/tuprolog/parser")
}

