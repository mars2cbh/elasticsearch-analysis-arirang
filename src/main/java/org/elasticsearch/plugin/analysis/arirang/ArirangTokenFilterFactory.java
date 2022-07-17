package org.elasticsearch.plugin.analysis.arirang;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ko.KoreanFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class ArirangTokenFilterFactory extends AbstractTokenFilterFactory {

    private final boolean bigrammable;

    public ArirangTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);

        bigrammable = settings.getAsBoolean("bigrammable", false);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new KoreanFilter(tokenStream, bigrammable);
    }
}