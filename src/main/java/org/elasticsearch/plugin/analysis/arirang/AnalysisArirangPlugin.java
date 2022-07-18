package org.elasticsearch.plugin.analysis.arirang;

import static java.util.Collections.singletonMap;

import com.amazonaws.util.json.Jackson;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.SpecialPermission;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;


public class AnalysisArirangPlugin extends Plugin implements AnalysisPlugin, ActionPlugin {

    final private ArirangAnalyzerRestReloadAction arirangAnalyzerRestReloadAction;

    public AnalysisArirangPlugin(Settings settings) {
        this.arirangAnalyzerRestReloadAction = new ArirangAnalyzerRestReloadAction();
    }

    @Override
    public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        Map<String, AnalysisProvider<TokenFilterFactory>> extra = new HashMap<>();
        extra.put("arirang_filter", ArirangTokenFilterFactory::new);
        extra.put("arirang_synonym_filter", ArirangSynonymFilterFactory::new);
        return extra;
    }

    @Override
    public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
        return singletonMap("arirang_tokenizer", ArirangTokenizerFactory::new);
    }

    @Override
    public Map<String, AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        return singletonMap("arirang_analyzer", ArirangAnalyzerProvider::new);
    }

    @Override
    public List<RestHandler> getRestHandlers(final Settings settings,
      final RestController restController,
      final ClusterSettings clusterSettings,
      final IndexScopedSettings indexScopedSettings,
      final SettingsFilter settingsFilter,
      final IndexNameExpressionResolver indexNameExpressionResolver,
      final Supplier<DiscoveryNodes> nodesInCluster) {

        return List.of(
            arirangAnalyzerRestReloadAction
        );
    }

}