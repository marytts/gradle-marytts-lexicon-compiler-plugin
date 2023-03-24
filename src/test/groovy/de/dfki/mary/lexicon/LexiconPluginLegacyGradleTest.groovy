package de.dfki.mary.lexicon

import org.gradle.testkit.runner.GradleRunner
import org.testng.annotations.BeforeSuite
import org.testng.annotations.Test

class LexiconPluginLegacyGradleTest {

    GradleRunner gradle

    @BeforeSuite
    void setup() {
        def projectDir = File.createTempDir()
        new File(projectDir, 'settings.gradle').createNewFile()
        gradle = GradleRunner.create()
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .forwardOutput()
        def resourceNames = [
                'build.gradle',
                'gradle.properties'
        ]
        resourceNames.each { resourceName ->
            new File(projectDir, resourceName).withWriter {
                it << this.class.getResourceAsStream(resourceName)
            }
        }
    }

    @Test
    void 'Gradle v5-0 cannot apply plugin'() {
        gradle.withGradleVersion('5.0')
                .buildAndFail()
    }

    @Test
    void 'Gradle v5-1 can apply plugin'() {
        gradle.withGradleVersion('5.1')
                .build()
    }
}
