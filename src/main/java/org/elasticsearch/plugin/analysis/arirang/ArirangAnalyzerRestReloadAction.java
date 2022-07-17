package org.elasticsearch.plugin.analysis.arirang;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestRequest.Method.POST;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.analysis.ko.morph.MorphException;
import org.apache.lucene.analysis.ko.utils.DictionaryUtil;
import org.elasticsearch.client.internal.node.NodeClient;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;

/**
 * Created by henry on 2018.8.28
 */
public class ArirangAnalyzerRestReloadAction extends BaseRestHandler {
  @Override
  public String getName() {
    return "arirang_reload_action";
  }

  @Override
  public List<Route> routes() {
    return List.of(new Route(GET, "/_arirang/reload"), new Route(POST, "/_arirang/reload"));
  }

  @Override
  protected RestChannelConsumer prepareRequest(RestRequest restRequest, NodeClient client) throws IOException {

    try {
      DictionaryUtil.loadDictionary();
    } catch (MorphException me) {
      return channel -> channel.sendResponse(new BytesRestResponse(RestStatus.NOT_ACCEPTABLE, "Failed which reload arirang analyzer dictionary!!"));
    }

    return channel -> channel.sendResponse(new BytesRestResponse(RestStatus.OK, "Reloaded arirang analyzer dictionary!!"));
  }
}
