@file:Suppress("UnstableApiUsage")

import dev.kikugie.loomx.LoomCompatDependencyExtension
import dev.kikugie.loomx.LoomCompatProjectExtension
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.ornithemc.ploceus.api.PloceusGradleExtensionApi
import org.gradle.kotlin.dsl.getByName
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension

plugins {
    java
    kotlin("jvm")
    kotlin("plugin.compose")
    id("dev.kikugie.loom-back-compat")
    id("net.fabricmc.fabric-loom-remap") version "1.17-SNAPSHOT" apply false
    id("ploceus") version "1.17.4" apply false
    id("me.modmuss50.mod-publish-plugin")
}

val stonecutter = extensions.getByName("stonecutter") as StonecutterBuildExtension
val loomx = extensions.getByType<LoomCompatProjectExtension>()
val mcVersion = stonecutter.current.version
val isOrnithe = mcVersion == "1.8.9"
val loader = if (isOrnithe) "ornithe" else "fabric"
val ploceus = if (isOrnithe) {
    pluginManager.apply("net.fabricmc.fabric-loom-remap")
    pluginManager.apply("ploceus")

    configurations.configureEach {
        exclude(group = "org.lwjgl.lwjgl")
    }

    extensions.getByType<PloceusGradleExtensionApi>().apply {
        setIntermediaryGeneration(2)
    }
} else {
    null
}

run {
    val (version, loader) = stonecutter.current.project.split("-", limit = 2)
    stonecutter.properties.tags(version, loader)
}

fun optionalProperty(vararg names: String): String? =
    names.firstNotNullOfOrNull { name -> findProperty(name)?.toString()?.takeIf { it.isNotBlank() } }

val minecraftPredicate = property("mod.mc_compat") as String

val fabricLoaderVersion = property("deps.fabric_loader") as String
val fabricLanguageKotlinVersion = property("deps.fabric_language_kotlin") as String
val kotlinVersion = property("deps.kotlin") as String
val mixinExtrasVersion = property("deps.mixin_extras") as String
val devauthVersion = property("deps.devauth") as String

val flkProvidedModules = setOf(
    "org.jetbrains.kotlinx:kotlinx-serialization-core-jvm",
    "org.jetbrains.kotlinx:kotlinx-serialization-json-jvm",
    "org.jetbrains.kotlinx:kotlinx-serialization-cbor-jvm",
    "org.jetbrains.kotlinx:kotlinx-io-core-jvm",
    "org.jetbrains.kotlinx:kotlinx-io-bytestring-jvm",
)

group = property("mod.group") as String
version = "${property("mod.version")}+${stonecutter.current.version}"
base.archivesName = property("mod.id") as String
val oneconfigVersion = property("deps.oneconfig") as String

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }

    if (isOrnithe) {
        exclusiveContent {
            forRepository { mavenCentral() }
            filter { includeGroup("org.lwjgl") }
        }
    }
    mavenLocal()
    mavenCentral()
    google()
    maven("https://repo.polyfrost.org/releases")
    maven("https://repo.polyfrost.org/snapshots")
    maven("https://redirector.kotlinlang.org/maven/compose-dev") {
        content { includeGroupAndSubgroups("org.jetbrains") }
    }
    maven("https://maven.cloverclient.com/releases")
    maven("https://jitpack.io") {
        content { includeGroupAndSubgroups("com.github") }
    }
    maven("https://maven.fabricmc.net/") {
        content { includeGroupAndSubgroups("net.fabricmc") }
    }
    maven("https://central.sonatype.com/repository/maven-snapshots") {
        content { includeGroup("net.kyori") }
    }
    strictMaven("https://maven.terraformersmc.com/releases/", "TerraformersMC", "com.terraformersmc")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

val flkProvidedVersions: Map<String, String> = run {
    configurations.detachedConfiguration(
        dependencies.create("net.fabricmc:fabric-language-kotlin:$fabricLanguageKotlinVersion"),
    ).resolvedConfiguration.resolvedArtifacts
        .map { it.moduleVersion.id }
        .filter { "${it.group}:${it.name}" in flkProvidedModules }
        .associate { "${it.group}:${it.name}" to it.version }
}

val flkCoroutinesVersion: String = configurations.detachedConfiguration(
    dependencies.create("net.fabricmc:fabric-language-kotlin:$fabricLanguageKotlinVersion"),
).resolvedConfiguration.resolvedArtifacts
    .map { it.moduleVersion.id }
    .single { it.group == "org.jetbrains.kotlinx" && it.name == "kotlinx-coroutines-core-jvm" }
    .version

configurations.all {
    resolutionStrategy.eachDependency {
        flkProvidedVersions["${requested.group}:${requested.name}"]?.let { useVersion(it) }
        if (requested.group == "net.kyori" && requested.name == "adventure-platform-fabric" && requested.version == "7.0.0-SNAPSHOT") {
            useVersion("7.0.0")
        }
    }
}

val javaVersion = when {
    isOrnithe || stonecutter.current.parsed >= "26.1" -> 25
    else -> 21
}

configure<KotlinJvmExtension> {
    jvmToolchain(javaVersion)
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)

    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

val loomExt = extensions.getByName<LoomGradleExtensionAPI>("loom")

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")

    if (isOrnithe) {
        mappings(ploceus!!.layeredMappings {
            mappings("net.ornithemc:feather-gen2:$mcVersion+build.${property("feather_build")}:v2") {
                containsUnpick()
            }
            mappings(rootProject.file("mappings/feather-overrides.tiny"))
        })
        modImplementation("net.ornithemc.osl-gen2:core:${property("deps.osl_core")}")
        modImplementation("net.ornithemc.osl-gen2:keybinds:${property("deps.osl_keybinds")}")
    } else {
        dependencies.extensions.getByType<LoomCompatDependencyExtension>().applyMojangMappings()
    }

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    annotationProcessor("io.github.llamalad7:mixinextras-common:$mixinExtrasVersion")

    if (!isOrnithe) {
        modLocalRuntime("me.djtheredstoner:DevAuth-fabric:$devauthVersion")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    }
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    runtimeOnly("net.fabricmc:fabric-language-kotlin:$fabricLanguageKotlinVersion")

    modImplementation("org.polyfrost.oneconfig:$mcVersion-$loader:$oneconfigVersion") {
        exclude(group = "net.fabricmc", module = "fabric-language-kotlin")
    }
    for (module in listOf(
        "commands",
        "config",
        "config-impl",
        "hud",
        "notifications",
        "poly-compose",
        "utils",
        "internal",
        "ui",
        "events"
    )) {
        implementation("org.polyfrost.oneconfig:$module:$oneconfigVersion")
    }

    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$flkCoroutinesVersion")
    for ((module, version) in flkProvidedVersions) {
        compileOnly("$module:$version")
    }
}

val modId = property("mod.id") as String
loomExt.mixin {
    defaultRefmapName.set("mixins.$modId.refmap.json")
}

val modOutputGroup = sourceSets.main.get().output
    .let { files(it.classesDirs, it.resourcesDir) }
    .joinToString(File.pathSeparator) { it.absolutePath }

loomExt.runs.configureEach {
    ideConfigGenerated(true)
    runDir(if (isOrnithe) "../../run/ornithe" else "../../run")
    vmArg("-Dfabric.classPathGroups=$modOutputGroup")
}
loomExt.runs.named("client") {
    client()
    if (isOrnithe && System.getProperty("os.name").startsWith("Mac")) vmArg("-XstartOnFirstThread")
}

tasks.withType<ProcessResources>().configureEach {
    val modName = project.property("mod.name") as String
    val modVersion = project.property("mod.version") as String
    inputs.property("modId", modId)
    inputs.property("modName", modName)
    inputs.property("modVersion", modVersion)
    inputs.property("minorMcVersion", minecraftPredicate)
    inputs.property("oneconfigVersion", oneconfigVersion)
    filesMatching("fabric.mod.json") {
        if (isOrnithe) filter { line ->
            line.replace("\"java\": \">=21\"", "\"java\": \">=25\"")
        }
        expand(
            mapOf(
                "mod_id" to modId,
                "mod_name" to modName,
                "mod_version" to modVersion,
                "mod_description" to "The OneClient main menu, without the rest of PolyPlus",
                "minor_mc_version" to minecraftPredicate,
                "oneconfig_version" to oneconfigVersion,
            ),
        )
    }
}

tasks.matching { it.name == "createMinecraftArtifacts" }.configureEach {
    dependsOn("stonecutterGenerate")
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    description = "Builds mod jars and copies results to `build/libs/{mod version}/`"

    inputs.property("version", project.property("mod.version"))
    from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
    into(rootProject.layout.buildDirectory.dir("libs/${project.property("mod.version")}"))
}

val modVersion = property("mod.version") as String

val modrinthId = optionalProperty("publish.modrinth")
val modrinthToken = optionalProperty("publish.modrinth.token", "modrinth.token")

val minecraftVersion = (property("mod.mc_releases") as String).split(",").map { it.trim() }
val changelogs = rootProject.file("CHANGELOG.md").takeIf { it.exists() }?.readText() ?: "No changelog provided."

publishMods {
    file = loomx.modJar.flatMap { it.archiveFile }

    displayName = modVersion
    version = "v$modVersion"
    changelog = changelogs
    type = STABLE

    modLoaders.add(loader)

    dryRun = modrinthId == null || modrinthToken == null

    if (modrinthId != null) {
        modrinth {
            projectId = modrinthId
            accessToken = modrinthToken.orEmpty()

            minecraftVersions.addAll(minecraftVersion)

            requires("oneconfig")
            requires("fabric-language-kotlin")
        }
    }
}
