package org.elasticsearch.rest.action.analysis;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestRequest.Method.POST;

import java.io.IOException;
import java.util.List;
import org.elasticsearch.client.internal.node.NodeClient;
import org.elasticsearch.plugin.analysis.arirang.ArirangSettings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;

/**
 * Created by henry on 2018.8.28
 */
public class ArirangAnalyzerRestInfoAction extends BaseRestHandler {

  private ArirangSettings arirangSettings;

  @Override
  public String getName() {
    return "arirang_test_action";
  }

  @Override
  public List<Route> routes() {
    return List.of(new Route(GET, "/_arirang/test"), new Route(POST, "/_arirang/test"));
  }

  @Override
  protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
    return channel -> channel.sendResponse(
        new BytesRestResponse(RestStatus.OK, "Arirang Info...: " + arirangSettings.getMarsName())
    );
  }

  public void refreshAndClearCache(ArirangSettings arirangSettings) {
    this.arirangSettings = arirangSettings;
  }

}
