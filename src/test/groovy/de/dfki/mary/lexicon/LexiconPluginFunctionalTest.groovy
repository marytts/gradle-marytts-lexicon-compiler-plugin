package de.dfki.mary.lexicon

import org.gradle.testkit.runner.GradleRunner
import org.testng.annotations.BeforeClass
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

class LexiconPluginFunctionalTest {

    GradleRunner gradle

    @BeforeClass
    void setUp() {
        def projectDir = File.createTempDir()
        new File(projectDir, 'settings.gradle').createNewFile()
        gradle = GradleRunner.create().withProjectDir(projectDir).withPluginClasspath().forwardOutput()
        def resourceNames = [
                'build.gradle',
                'gradle.properties',
                'allophones.xy.xml',
                'xy.txt'
        ]
        resourceNames.each { resourceName ->
            new File(projectDir, resourceName).withWriter {
                it << this.class.getResourceAsStream(resourceName)
            }
        }
    }

    @DataProvider
    Object[][] taskNames() {
        // task name to run, and whether to chase it with a test task named "testName"
        [
                ['help', false],
                ['testPlugin', false],
                ['compileLexicon', true],
                ['testLexicon', true]
        ]
    }

    @Test(dataProvider = 'taskNames')
    void testTasks(String taskName, boolean runTestTask) {
        def gradleArgs = ['--warning-mode', 'all']
        def result = gradle.withArguments(gradleArgs + [taskName]).build()
        assert result.task(":$taskName").outcome in [SUCCESS, UP_TO_DATE]
        if (runTestTask) {
            def testTaskName = 'test' + taskName.capitalize()
            result = gradle.withArguments(gradleArgs + [testTaskName]).build()
            assert result.task(":$taskName").outcome == UP_TO_DATE
            assert result.task(":$testTaskName").outcome == SUCCESS
        }
    }
}
