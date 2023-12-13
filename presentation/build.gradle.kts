plugins {
    `java-library`
}

version = rootProject.version
group = rootProject.group

val jacksonVersion: String by project

dependencies {
    api(project(":core"))

    implementation("org.javatuples", "javatuples", "1.2")
    implementation("com.fasterxml.jackson.core", "jackson-core", jacksonVersion)
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", jacksonVersion)
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-xml", jacksonVersion)
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", jacksonVersion)

    testImplementation("junit", "junit", "4.12")
    testImplementation("pl.pragmatists", "JUnitParams", "1.1.1")
}
