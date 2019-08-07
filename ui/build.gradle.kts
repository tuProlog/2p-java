plugins {
    `java-library`
}

version = rootProject.version
group = rootProject.group

dependencies {
    api(project(":core"))
    api("com.fifesoft", "rsyntaxtextarea", "3.0.3")
    api("com.fifesoft", "autocomplete", "3.0.0")

    testImplementation("junit", "junit", "4.12")
    testImplementation("org.concordion", "concordion", "2.+")
    testImplementation("org.concordion", "concordion-embed-extension", "1.2.0")
}

val ideMainClass = "alice.tuprologx.ide.GUILauncher"
val replMainClass = "alice.tuprologx.ide.CUIConsole"

task<JavaExec>("ide") {
    group = "run"
    dependsOn("classes")

    main = ideMainClass
    sourceSets {
        main {
            classpath = runtimeClasspath
        }
    }
}

task<JavaExec>("repl") {
    group = "run"
    dependsOn("classes")

    main = replMainClass
    sourceSets {
        main {
            classpath = runtimeClasspath
        }
    }

    standardInput = System.`in`
    standardOutput = System.out
}

val jarTask = tasks["jar"] as Jar

task<Jar>("runnableJar") {
    manifest {
        attributes["Main-Class"] = ideMainClass
    }

    group = "jar"
    dependsOn(configurations.runtimeClasspath)

    from(sourceSets.main.get().output)
    from(
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    )

    destinationDirectory.set(jarTask.destinationDirectory.get())
    archiveBaseName.set(rootProject.name)
    archiveVersion.set(project.version.toString())
}