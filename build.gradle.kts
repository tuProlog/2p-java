plugins {
    id("maven-publish")
    signing
    java
}

version = "4.0.3"
group = "it.unibo.alice.tuprolog"

repositories {
    mavenCentral()
}

fun capitalize(s: String): String {
    return s[0].toUpperCase() + s.substring(1)
}

val signTask: DefaultTask = task<DefaultTask>("sign")

subprojects {

    apply(plugin = "signing")
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    repositories {
        mavenCentral()
    }

    group = rootProject.group
    version = rootProject.version

    val jarTask = tasks["jar"] as Jar

    jarTask.archiveBaseName.set("${rootProject.name}-${this@subprojects.name}")

    task<Jar>("sourcesJar") {
        from(sourceSets["main"].allSource)
        group = "documentation"

        destinationDirectory.set(jarTask.destinationDirectory.get())
        archiveBaseName.set(jarTask.archiveBaseName.get())
        archiveVersion.set(this@subprojects.version.toString())
        archiveClassifier.set("sources")
    }

    task<Jar>("javadocJar") {
        dependsOn("javadoc")
        from(tasks["javadoc"])
        group = "documentation"

        destinationDirectory.set(jarTask.destinationDirectory.get())
        archiveBaseName.set(jarTask.archiveBaseName.get())
        archiveVersion.set(this@subprojects.version.toString())
        archiveClassifier.set("javadoc")
    }

    publishing {
        publications {

            repositories {

                val mavenRepoUrl = if (version.toString().contains("SNAPSHOT")) {
                    "https://oss.sonatype.org/content/repositories/snapshots/"
                } else {
                    "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
                }

                maven(mavenRepoUrl) {
                    credentials {
                        // env ORG_GRADLE_PROJECT_ossrhUsername
                        val ossrhUsername: String? by project
                        // env ORG_GRADLE_PROJECT_ossrhPassword
                        val ossrhPassword: String? by project

                        username = ossrhUsername
                        //rootProject.property("ossrhUsername").toString()
                        password = ossrhPassword
                        // rootProject.property("ossrhPassword").toString()
                    }
                }
            }

            create<MavenPublication>(this@subprojects.name) {
                groupId = rootProject.group.toString()
                artifactId = "${rootProject.name}-${this@subprojects.name}"
                version = rootProject.version.toString()

                val javaComponent = this@subprojects.components.find { it.name == "java" }
                        ?: throw IllegalStateException("Cannot find Java project component.")

                from(javaComponent)
                artifact(tasks["javadocJar"])
                artifact(tasks["sourcesJar"])

                pom {
                    name.set("tuProlog ${capitalize(this@subprojects.name)}")
                    description.set("${capitalize(this@subprojects.name)} module for tuProlog")
                    url.set("http://tuprolog.unibo.it")

                    packaging = "jar"

                    licenses {
                        license {
                            name.set("GNU LGPL 2.1")
                            url.set("http://www.gnu.org/licenses/lgpl-2.1.html")
                        }
                    }

                    developers {
                        developer {
                            email.set("giovanni.ciatto@unibo.it")
                            url.set("https://about.me/gciatto")
                            organization.set("Alma Mater Studiorum-Università di Bologna")
                            organizationUrl.set("http://apice.unibo.it/xwiki/bin/view/Main/WebHome?language=en")
                        }
                        developer {
                            email.set("roberta.calegari@unibo.it")
                            url.set("http://apice.unibo.it/xwiki/bin/view/RobertaCalegari/WebHome")
                            organization.set("Alma Mater Studiorum-Università di Bologna")
                            organizationUrl.set("http://apice.unibo.it/xwiki/bin/view/Main/WebHome?language=en")
                        }
                    }

                    scm {
                        connection.set("scm:git:git:///gitlab.com/pika-lab/tuprolog/2p.git")
                        developerConnection.set("scm:git:ssh://gitlab.com/pika-lab/tuprolog/2p.git")
                        url.set("https://gitlab.com/pika-lab/tuprolog/2p")
                    }
                }
            }
        }
    }

    signing {
        // env ORG_GRADLE_PROJECT_signingKey
        val signingKey: String? by project
        // env ORG_GRADLE_PROJECT_signingPassword
        val signingPassword: String? by project

        useInMemoryPgpKeys(signingKey, signingPassword)

        // This generates a task for each publication, named `sign<CapitalisedPubName>Publication`
        sign(publishing.publications)
    }

    signTask.dependsOn(tasks["sign${capitalize(this@subprojects.name)}Publication"])

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}
