package org.elasticsearch.plugin.analysis.arirang;

import java.util.List;
import java.util.function.Function;
import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;

public class ArirangSynonymFilterFactory extends AbstractTokenFilterFactory {


  public ArirangSynonymFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
    super(indexSettings, name, settings);

    logger.info("init ArirangSynonymFilterFactory");

  }

  @Override
  public TokenStream create(TokenStream tokenStream) {
    throw new IllegalStateException("analysis chain first");
  }

  @Override
  public TokenFilterFactory getChainAwareTokenFilterFactory(TokenizerFactory tokenizer, List<CharFilterFactory> charFilters,
      List<TokenFilterFactory> previousTokenFilters, Function<String, TokenFilterFactory> allFilters) {
    return super.getChainAwareTokenFilterFactory(tokenizer, charFilters, previousTokenFilters, allFilters);
  }

}
