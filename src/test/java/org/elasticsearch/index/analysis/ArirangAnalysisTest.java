package org.elasticsearch.index.analysis;

import static org.hamcrest.Matchers.instanceOf;

import java.io.IOException;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.plugin.analysis.arirang.AnalysisArirangPlugin;
import org.elasticsearch.plugin.analysis.arirang.ArirangTokenizerFactory;
import org.elasticsearch.test.ESTestCase;
import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by hwjeong on 2015. 11. 3..
 */
public class ArirangAnalysisTest extends ESTestCase {

  @Test
  public void test() {
    System.out.println("hello world");
  }

  @Ignore
  public void testArirangAnalysis() throws IOException {

    Settings settings = Settings.builder().build();

    final TestAnalysis analysisService = createTestAnalysis(new Index("test", "_na_"), Settings.EMPTY, new AnalysisArirangPlugin(settings));
    TokenizerFactory tokenizerFactory = analysisService.tokenizer.get("arirang_tokenizer");
    MatcherAssert.assertThat(tokenizerFactory, instanceOf(ArirangTokenizerFactory.class));
  }
}
