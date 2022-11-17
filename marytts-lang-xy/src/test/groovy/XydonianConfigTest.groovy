package marytts.language.xy

import marytts.LocalMaryInterface

import static marytts.datatypes.MaryDataType.*

import org.testng.annotations.*

class XydonianConfigTest {

    final Locale XYDONIAN = new Locale('xy')

    @Test
    void hasXydonianLocale() {
        def config = new XydonianConfig()
        assert config.locales.contains(XYDONIAN)
    }

    @Test
    void canSetXydonianLocale() {
        def mary = new LocalMaryInterface()
        mary.locale = XYDONIAN
        assert mary.locale == XYDONIAN
    }

    @Test
    void canProcessTextToPhonemes() {
        def mary = new LocalMaryInterface()
        mary.locale = XYDONIAN
        mary.outputType = PHONEMES
        assert mary.generateXML('xy')
    }

}
