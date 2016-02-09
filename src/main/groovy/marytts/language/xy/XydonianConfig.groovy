package marytts.language.xy

import marytts.config.LanguageConfig

class XydonianConfig extends LanguageConfig {

    public XydonianConfig() {
        super(XydonianConfig.class.getResourceAsStream("xy.config"))
    }

}
