package org.elasticsearch.plugin.analysis.arirang;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ko.KoreanTokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;

public class ArirangTokenizerFactory extends AbstractTokenizerFactory {

    private final String KEY_STORAGE_TYPE = "storage_type";
    private final String KEY_STORAGE_PATH = "storage_path";

    public ArirangTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, settings, name);

        String storageType = settings.get(KEY_STORAGE_TYPE);
        String storagePath = settings.get(KEY_STORAGE_PATH);

        if (storageType != null && storagePath != null) {
            logger.info("Arirang Analyzer");
            logger.info("Arirang Analyzer - storageType : " + storageType);
            logger.info("Arirang Analyzer - storagePath : " + storagePath);
        }

    }

    @Override
    public Tokenizer create() {
        return new KoreanTokenizer();
    }
}