[![CI](https://github.com/marytts/gradle-marytts-lexicon-compiler-plugin/actions/workflows/main.yml/badge.svg)](https://github.com/marytts/gradle-marytts-lexicon-compiler-plugin/actions/workflows/main.yml)

Lexicon compiler plugin for MaryTTS
===================================
(incubating)

A Gradle plugin to compile MaryTTS lexicon resources

Installation
------------

See https://plugins.gradle.org/plugin/de.dfki.mary.lexicon-compiler

Usage
-----

Ensure that the project has a `locale` property that matches its locale for MaryTTS.
This can be set inside a `gradle.properties` file.

Prepare an XML file describing the allophone set.

The lexicon source should be a text file with one entry per line:
```
lemma tran-'scrip-tion
```
where the lemma and transcription are separated by whitespace, and the transcription uses the symbols defined in the `allophones.XY.xml` file.

Compile the lexicon and build the artifact by running `./gradlew build` (or `gradlew build` on Windows).
