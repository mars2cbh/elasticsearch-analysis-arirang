package org.elasticsearch.plugin.analysis.arirang;

import static java.util.Collections.singletonMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.ArirangAnalyzerProvider;
import org.elasticsearch.index.analysis.ArirangTokenFilterFactory;
import org.elasticsearch.index.analysis.ArirangTokenizerFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.ReloadablePlugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;
import org.elasticsearch.rest.action.analysis.ArirangAnalyzerRestInfoAction;
import org.elasticsearch.rest.action.analysis.ArirangAnalyzerRestReloadAction;


public class AnalysisArirangPlugin extends Plugin implements AnalysisPlugin, ActionPlugin, ReloadablePlugin {

    private static final Logger LOGGER = LogManager.getLogger(AnalysisArirangPlugin.class);

    private final Settings settings;
    final private ArirangAnalyzerRestReloadAction arirangAnalyzerRestReloadAction;
    final private ArirangAnalyzerRestInfoAction arirangAnalyzerRestInfoAction;

    public AnalysisArirangPlugin(Settings settings) {
        this.settings = settings;
        this.arirangAnalyzerRestReloadAction = new ArirangAnalyzerRestReloadAction();
        this.arirangAnalyzerRestInfoAction = new ArirangAnalyzerRestInfoAction();

        reload(settings);
    }

    @Override
    public List<Setting<?>> getSettings() {
        return Arrays.asList(
            ArirangSettings.MARS_NAME_SETTING
        );
    }

    @Override
    public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        return singletonMap("arirang_filter", ArirangTokenFilterFactory::new);
    }

    @Override
    public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
        Map<String, AnalysisProvider<TokenizerFactory>> extra = new HashMap<>();
        extra.put("arirang_tokenizer", ArirangTokenizerFactory::new);

        return extra;
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
            arirangAnalyzerRestReloadAction,
            arirangAnalyzerRestInfoAction
        );
    }

    @Override
    public void reload(Settings settings) {

        LOGGER.info("Reload Arirang Plugin.");

        final ArirangSettings arirangSettings = ArirangSettings.getSettings(settings);
        arirangAnalyzerRestInfoAction.refreshAndClearCache(arirangSettings);
    }
}