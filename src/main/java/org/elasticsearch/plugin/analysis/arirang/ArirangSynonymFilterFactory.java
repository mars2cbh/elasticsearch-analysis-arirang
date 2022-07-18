package org.elasticsearch.plugin.analysis.arirang;

import java.util.List;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.AnalysisMode;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.index.analysis.CustomAnalyzer;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.plugin.analysis.arirang.synonym.S3SynonymFile;
import org.elasticsearch.plugin.analysis.arirang.synonym.SynonymFile;

public class ArirangSynonymFilterFactory extends AbstractTokenFilterFactory {

  private final boolean expand;
  private final String format;
  protected SynonymMap synonymMap;
  protected final Environment environment;
  protected final AnalysisMode analysisMode;
  private final String region;
  private final String bucket;
  private final String key;

  public ArirangSynonymFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
    super(indexSettings, name, settings);

    logger.info("init ArirangSynonymFilterFactory");

    this.expand = settings.getAsBoolean("expand", true);
    this.format = settings.get("format", "");
    boolean updateable = settings.getAsBoolean("updateable", false);
    this.analysisMode = updateable ? AnalysisMode.SEARCH_TIME : AnalysisMode.ALL;
    this.environment = env;

    this.region = settings.get("region", "ap-northeast-2");
    this.bucket = settings.get("bucket", "");
    this.key = settings.get("key", "");

  }

  @Override
  public AnalysisMode getAnalysisMode() {
    return this.analysisMode;
  }

  @Override
  public TokenStream create(TokenStream tokenStream) {
    throw new IllegalStateException(
        "Call getChainAwareTokenFilterFactory to specialize this factory for an analysis chain first");
  }

  @Override
  public TokenFilterFactory getChainAwareTokenFilterFactory(TokenizerFactory tokenizer, List<CharFilterFactory> charFilters,
      List<TokenFilterFactory> previousTokenFilters, Function<String, TokenFilterFactory> allFilters) {
    final Analyzer analyzer = buildSynonymAnalyzer(tokenizer, charFilters, previousTokenFilters, allFilters);
    synonymMap = buildSynonyms(analyzer);

    logger.info("{} synonym updated", synonymMap.words.size());

    final String name = name();
    return new TokenFilterFactory() {
      @Override
      public String name() {
        return name;
      }

      @Override
      public TokenStream create(TokenStream tokenStream) {
        // fst is null means no synonyms
        if (synonymMap.fst == null) {
          return tokenStream;
        }

        return new SynonymGraphFilter(tokenStream, synonymMap, false);
      }

      @Override
      public TokenFilterFactory getSynonymFilter() {
        // In order to allow chained synonym filters, we return IDENTITY here to
        // ensure that synonyms don't get applied to the synonym map itself,
        // which doesn't support stacked input tokens
        return IDENTITY_FILTER;
      }

      @Override
      public AnalysisMode getAnalysisMode() {
        return analysisMode;
      }
    };
  }

  Analyzer buildSynonymAnalyzer(
      TokenizerFactory tokenizer,
      List<CharFilterFactory> charFilters,
      List<TokenFilterFactory> tokenFilters,
      Function<String, TokenFilterFactory> allFilters
  ) {
    return new CustomAnalyzer(
        tokenizer,
        charFilters.toArray(new CharFilterFactory[0]),
        tokenFilters.stream().map(TokenFilterFactory::getSynonymFilter).toArray(TokenFilterFactory[]::new)
    );
  }

  SynonymMap buildSynonyms(Analyzer analyzer) {
    try {
      return getSynonymFile(analyzer).reloadSynonymMap();
    } catch (Exception e) {
      logger.error("failed to build synonyms", e);
      throw new IllegalArgumentException("failed to build synonyms", e);
    }
  }

  SynonymFile getSynonymFile(Analyzer analyzer) {
    try {
      return new S3SynonymFile(environment, analyzer, expand, format, region, bucket, key);
    } catch (Exception e) {
      logger.error("failed to get synonyms : ", e);
      throw new IllegalArgumentException("failed to get synonyms : ", e);
    }
  }

}
