plugins {
    `java-library`
}

version = rootProject.version
group = rootProject.group

dependencies {
    api(project(":core"))
    api(files(*listOf("jade", "jadeSecurity").map { "lib/$it.jar" }.toTypedArray()))

    implementation("com.google.code.gson", "gson", "2.8.5")

    testImplementation("junit", "junit", "4.12")
    testImplementation("org.concordion", "concordion", "2.+")
    testImplementation("org.concordion", "concordion-embed-extension", "1.2.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

task<JavaExec>("ide") {
    group = "run"
    dependsOn("classes")

    main = "alice.tuprologx.ide.GUILauncher"
    sourceSets {
        main {
            classpath = runtimeClasspath
        }
    }
}

task<JavaExec>("repl") {
    group = "run"
    dependsOn("classes")

    main = "alice.tuprologx.ide.CUIConsole"
    sourceSets {
        main {
            classpath = runtimeClasspath
        }
    }

    standardInput = System.`in`
    standardOutput = System.out
}