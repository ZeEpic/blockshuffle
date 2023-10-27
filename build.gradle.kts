import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.8.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

val projectName = "BlockShuffle"
group = "me.zeepic"
val ver = "1.0.0"
version = ver
val mcVersion = "1.20"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("io.papermc.paper:paper-api:$mcVersion-R0.1-SNAPSHOT")
    implementation("com.konghq:unirest-java:3.11.09")
    implementation(kotlin("reflect"))
    implementation("org.reflections:reflections:0.10.2")
}

// For convenience
println(project.gradle.gradleUserHomeDir)
val folder = file("C:\\Users\\isaol\\Desktop\\Minecraft\\Paper 1.19.4 Server\\plugins") // file(project.gradle.gradleUserHomeDir.path.dropLast(7) +  "\\Desktop\\Minecraft\\Paper 1.19.4 Server\\plugins")


tasks.withType<ShadowJar> {
    archiveFileName.set("$projectName.jar")
    destinationDirectory.set(folder)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

bukkit {
    main = "$group.blockshuffle.$projectName"
    name = projectName
    description = "Block Shuffle minigame."
    version = ver
    authors = listOf("ZeEpic")
    apiVersion = mcVersion.substring(0, 4)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
