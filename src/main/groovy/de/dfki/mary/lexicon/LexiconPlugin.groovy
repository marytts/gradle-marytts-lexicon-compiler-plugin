package de.dfki.mary.lexicon

import org.gradle.api.*
import org.gradle.api.plugins.JavaPlugin

class LexiconPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.pluginManager.apply(JavaPlugin)

        def lexiconSrcDir = project.layout.projectDirectory.dir("modules/$project.locale/lexicon")

        def compileLexiconTask = project.tasks.register('compileLexicon', LexiconCompile) {
            allophonesFile.set lexiconSrcDir.file("allophones.${project.locale}.xml")
            lexiconFile.set lexiconSrcDir.file("${project.locale}.txt")
            ltsFile.set project.layout.buildDirectory.file("${project.locale}.lts")
            fstFile.set project.layout.buildDirectory.file("${project.locale}_lexicon.fst")
            sampaLexiconFile.set project.layout.buildDirectory.file("${project.locale}_lexicon.dict")
        }

        project.tasks.named('processResources').configure {
            from compileLexiconTask.get().allophonesFile
            from compileLexiconTask
            eachFile {
                it.path = "marytts/language/$project.locale/lexicon/$it.name"
            }
        }

        def testLexiconTask = project.tasks.register('testLexicon', LexiconTest) {
            inputs.files compileLexiconTask
            reportFile.set project.layout.buildDirectory.file("report.txt")
        }

        project.tasks.named('test').configure {
            dependsOn testLexiconTask
        }
    }
}
