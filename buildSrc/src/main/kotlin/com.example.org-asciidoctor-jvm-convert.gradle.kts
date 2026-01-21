plugins {
    id("org.asciidoctor.jvm.convert")
}

val asciidoctorExt = configurations.create("asciidoctorExt")
val snippetsDir = file("build/generated-snippets")

asciidoctorj {
    setVersion("3.0.0")
}

tasks.asciidoctor {
    configurations(asciidoctorExt.name)

    baseDirFollowsSourceFile()
    sources {
        include("**/index.adoc")
    }

    jvm {
        jvmArgs("--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED")
        jvmArgs("--add-opens", "java.base/java.io=ALL-UNNAMED")
        jvmArgs("--enable-native-access=ALL-UNNAMED")
    }
}

afterEvaluate {
    tasks.findByName("test")?.let { test ->
        test.outputs.dir(snippetsDir)

        tasks.asciidoctor {
            inputs.dir(snippetsDir)
        }
    }
}