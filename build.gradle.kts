plugins {
    scala
    groovy
    jacoco
}

repositories {
    jcenter()
    maven { url = uri("https://repo.jenkins-ci.org/releases/") }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Test> {
    //useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
}

sourceSets {
    main {
        withConvention(ScalaSourceSet::class) {
            scala {
                setSrcDirs(listOf("src"))
            }
        }
        withConvention(GroovySourceSet::class) {
            groovy {
                setSrcDirs(listOf("src"))
            }
        }
    }
    test {
        withConvention(ScalaSourceSet::class) {
            scala {
                setSrcDirs(listOf("test/scala"))
            }
        }
        resources.srcDir(file("test/resources"))
    }
}

dependencies {
    implementation("org.codehaus.groovy:groovy-all:3.0.5")
    implementation("org.scala-lang:scala-library:2.13.3")

    testImplementation("junit:junit:4.13")
    testImplementation("org.scalatest:scalatest_2.13:3.2.2")
    testImplementation("org.scalatestplus:junit-4-13_2.13:3.2.2.0")
    testImplementation("com.lesfurets:jenkins-pipeline-unit:1.7")

    testRuntimeOnly("org.scala-lang.modules:scala-xml_2.13:1.2.0")
}

tasks.withType<JacocoReport> {
//    classDirectories.setFrom(
//            sourceSets.main.get().output.asFileTree.matching {
//                exclude("org/example/B.class")
//            }
//    )
    reports {
        xml.isEnabled = true
        html.isEnabled = true
        //html.destination file("${buildDir}/jacocoHtml")
    }
}
