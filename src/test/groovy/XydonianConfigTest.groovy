package marytts.language.xy

import marytts.LocalMaryInterface

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

}
