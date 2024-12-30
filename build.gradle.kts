import groovy.json.JsonSlurper
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Properties
import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    alias(libs.plugins.multiplatform).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.compose).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.mavenPublish).apply(false)
}

tasks.register("ciIos") {
    doLast {
        if (Os.isFamily(Os.FAMILY_MAC)) {
            runExec(listOf("brew", "install", "kdoctor"))
            runExec(listOf("kdoctor"))
            val devicesJson = runExec(
                listOf(
                    "xcrun",
                    "simctl",
                    "list",
                    "devices",
                    "available",
                    "-j"
                )
            )
            @Suppress("UNCHECKED_CAST")
            val devicesList = (JsonSlurper().parseText(devicesJson) as Map<String, *>)
                .let { it["devices"] as Map<String, *> }
                .let { devicesMap ->
                    devicesMap.keys
                        .filter { it.startsWith("com.apple.CoreSimulator.SimRuntime.iOS") }
                        .map { devicesMap[it] as List<*>}
                }
                .map { jsonArray -> jsonArray.map { it as Map<String, *> } }
                .flatten()
                .filter { it["isAvailable"] as Boolean }
                .filter {
                    listOf("iphone 1").any { device ->
                        (it["name"] as String).contains(device, true)
                    }
                }
            println("Devices:${devicesList.joinToString { "\n" + it["udid"] + ": " + it["name"] }}")
            val device = devicesList.firstOrNull()
            println("Selected:\n${device?.get("udid")}: ${device?.get("name")}")
            runExec(
                listOf(
                    "xcodebuild",
                    "-project",
                    "${rootDir.path}/iosApp/iosApp.xcodeproj",
                    "-scheme",
                    "iosApp",
                    "-configuration",
                    "Debug",
                    "OBJROOT=${rootDir.path}/build/ios",
                    "SYMROOT=${rootDir.path}/build/ios",
                    "-destination",
                    "id=${device?.get("udid")}",
                    "-allowProvisioningDeviceRegistration",
                    "-allowProvisioningUpdates"
                )
            )
        }
    }
}

tasks.register("ciSdkManagerLicenses") {
    doLast {
        val sdkDirPath = getAndroidSdkPath(rootDir = rootDir)
        getSdkManagerFile(rootDir = rootDir)?.let { sdkManagerFile ->
            val yesInputStream = object : InputStream() {
                private val yesString = "y\n"
                private var counter = 0
                override fun read(): Int = yesString[counter % 2].also { counter++ }.code
            }
            providers.exec {
                executable = sdkManagerFile.absolutePath
                args = listOf("--list", "--sdk_root=$sdkDirPath")
                println("exec: ${this.commandLine.joinToString(separator = " ")}")
            }.apply { println("ExecResult: ${this.result.get()}") }
            @Suppress("DEPRECATION")
            exec {
                executable = sdkManagerFile.absolutePath
                args = listOf("--licenses", "--sdk_root=$sdkDirPath")
                standardInput = yesInputStream
                println("exec: ${this.commandLine.joinToString(separator = " ")}")
            }.apply { println("ExecResult: $this") }
        }
    }
}

fun runExec(commands: List<String>): String = object : ByteArrayOutputStream() {
    override fun write(p0: ByteArray, p1: Int, p2: Int) {
        print(String(p0, p1, p2))
        super.write(p0, p1, p2)
    }
}.let { resultOutputStream ->
    @Suppress("DEPRECATION")
    exec {
        if (System.getenv("JAVA_HOME") == null) {
            System.getProperty("java.home")?.let { javaHome ->
                environment = environment.toMutableMap().apply {
                    put("JAVA_HOME", javaHome)
                }
            }
        }
        commandLine = commands
        standardOutput = resultOutputStream
        println("commandLine: ${this.commandLine.joinToString(separator = " ")}")
    }.apply { println("ExecResult: $this") }
    String(resultOutputStream.toByteArray())
}

fun getAndroidSdkPath(rootDir: File): String? = Properties().apply {
    val propertiesFile = File(rootDir, "local.properties")
    if (propertiesFile.exists()) {
        load(propertiesFile.inputStream())
    }
}.getProperty("sdk.dir").let { propertiesSdkDirPath ->
    (propertiesSdkDirPath ?: System.getenv("ANDROID_HOME"))
}

fun getSdkManagerFile(rootDir: File): File? = getAndroidSdkPath(rootDir = rootDir)?.let { sdkDirPath ->
    println("sdkDirPath: $sdkDirPath")
    val files = File(sdkDirPath).walk().filter { file ->
        file.path.contains("cmdline-tools") && file.path.endsWith("sdkmanager")
    }
    files.forEach { println("walk: ${it.absolutePath}") }
    val sdkmanagerFile = files.firstOrNull()
    println("sdkmanagerFile: $sdkmanagerFile")
    sdkmanagerFile
}
