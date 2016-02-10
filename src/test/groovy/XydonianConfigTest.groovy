package marytts.language.xy

import org.testng.annotations.*

class XydonianConfigTest {

    final Locale XYDONIAN = new Locale('xy')

    @Test
    void hasXydonianLocale() {
        def config = new XydonianConfig()
        assert config.locales.contains(XYDONIAN)
    }

}
