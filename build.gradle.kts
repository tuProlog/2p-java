plugins {
    id("maven-publish")
    signing
}

version = "4.0.0"
group = "it.unibo.alice.tuprolog"

repositories {
    jcenter()
    mavenCentral()
}

fun capitalize(s: String): String {
    return s[0].toUpperCase() + s.substring(1)
}

subprojects {

    apply(plugin = "signing")
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    repositories {
        jcenter()
        mavenCentral()
    }

    task<Jar>("javadocJar") {
        from(tasks["javadoc"])
        group = "documentation"
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
                        username = System.getenv("OSSRH-USERNAME")
                        //rootProject.property("ossrhUsername").toString()
                        password = System.getenv("OSSRH-PASSWORD")
                        // rootProject.property("ossrhPassword").toString()
                    }
                }
            }

            create<MavenPublication>(this@subprojects.name) {
                groupId = rootProject.group.toString()
                artifactId = "${rootProject.name}-${this@subprojects.name}"
                version = rootProject.version.toString()

                artifact(tasks["jar"])
//                artifact(tasks["javadocJar"])

                println("$groupId:$artifactId:$version")

                pom {
                    name.set("tuProlog ${capitalize(this@subprojects.name)}")
                    description.set("${capitalize(this@subprojects.name)} module for tuProlog")
                    url.set("http://tuprolog.unibo.it")

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
        val signingKey: String? = System.getenv("2P-TEAM-KEY-ASCII")
        val signingPassword: String? = System.getenv("2P-TEAM-KEY-PASSPHRASE")
        useInMemoryPgpKeys(signingKey, signingPassword)

        sign(publishing.publications)
    }
}

//publishing {
//    val pubs = publications.withType<MavenPublication>().map{ "sign${capitalize(it.name)}Publication" }
//
//    task<Sign>("signAllPublications") {
//        dependsOn(*pubs.toTypedArray())
//    }
//}
