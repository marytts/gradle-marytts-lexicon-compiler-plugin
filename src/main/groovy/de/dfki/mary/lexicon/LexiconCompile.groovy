package de.dfki.mary.lexicon

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

import marytts.fst.AlignerTrainer
import marytts.fst.TransducerTrie
import marytts.modules.phonemiser.AllophoneSet
import marytts.tools.newlanguage.LTSTrainer

class LexiconCompile extends DefaultTask {
    @InputFile
    final RegularFileProperty allophonesFile = project.objects.fileProperty()

    @InputFile
    final RegularFileProperty lexiconFile = project.objects.fileProperty()

    @Input
    final Property<String> delimiter = project.objects.property(String)
            .convention('\\s+')

    @Input
    final MapProperty<String, String> phoneMapping = project.objects.mapProperty(String, String)

    @OutputFile
    final RegularFileProperty ltsFile = project.objects.fileProperty()

    @OutputFile
    final RegularFileProperty fstFile = project.objects.fileProperty()

    @OutputFile
    final RegularFileProperty sampaLexiconFile = project.objects.fileProperty()

    @TaskAction
    void compile() {
        // load allophoneset
        def allophoneSet = AllophoneSet.getAllophoneSet(allophonesFile.get().asFile.newInputStream(), project.locale)

        // read transcriptions
        def lexicon = [:]
        lexiconFile.get().asFile.eachLine('UTF-8') { line ->
            def fields = line.trim().split(delimiter.get())
            if (fields.first().startsWith('#')) {
                // a comment
            } else if (fields.size() == 1) {
                project.logger.info "No transcription found in line: $line"
            } else {
                def (lemma, transcription) = fields.take(2)
                // remap phones
                phoneMapping.get().each { before, after ->
                    transcription = transcription.replaceAll ~/$before/, after
                }
                if (allophoneSet.checkAllophoneSyntax(transcription)) {
                    // store valid transcription
                    lexicon[lemma] = transcription
                } else {
                    try {
                        allophoneSet.splitIntoAllophoneList(transcription, false)
                    } catch (all) {
                        project.logger.info all.message
                    }
                    project.logger.warn "Invalid transcription for '$lemma': [$transcription]"
                }
            }
        }
        assert lexicon

        // adapted code from LTSLexiconPOSBuilder#trainLTS and LTSLexiconPOSBuilder#saveTranscription
        project.logger.lifecycle "train and predict"
        def ltsTrainer = new LTSTrainer(allophoneSet, true, true, 2)
        ltsTrainer.readLexicon(lexicon)
        5.times {
            project.logger.lifecycle "iteration ${it + 1}"
            ltsTrainer.alignIteration()
        }
        def tree = ltsTrainer.trainTree(100)
        ltsTrainer.save(tree, ltsFile.get().asFile.path)

        project.logger.lifecycle "save transcription"
        sampaLexiconFile.get().asFile.withWriter('UTF-8') { writer ->
            lexicon.each { lemma, transcription ->
                def transcriptionStr = allophoneSet.splitAllophoneString(transcription)
                writer.println "$lemma|$transcriptionStr"
            }
        }
        def aligner = new AlignerTrainer(false, true)
        aligner.readLexicon(sampaLexiconFile.get().asFile.newReader('UTF-8'), '\\|')
        4.times { aligner.alignIteration() }

        def trie = new TransducerTrie()
        aligner.lexiconSize().times {
            trie.add(aligner.getAlignment(it))
            trie.add(aligner.getInfoAlignment(it))
        }
        trie.computeMinimization()
        trie.writeFST(fstFile.get().asFile.newDataOutputStream(), 'UTF-8')
    }
}
