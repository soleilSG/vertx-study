/**
 * 2018年3月26日  上午11:05:18
 * soleil
 */
package com.stardrin.soleil.vertx.study.elasticsearch;

import com.hubrick.vertx.elasticsearch.ElasticSearchService;
import com.hubrick.vertx.elasticsearch.model.IndexOptions;
import com.hubrick.vertx.elasticsearch.model.OpType;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

/**
 * @author soleil
 * @date 2018年3月26日
 * @time 上午11:05:18
 */
public class HttpVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		ElasticSearchService elasticSearchService = ElasticSearchService.createEventBusProxy(vertx, "et.elasticsearch");
		vertx.createHttpServer().requestHandler(req -> {
			IndexOptions indexOptions = new IndexOptions().setId("123").setOpType(OpType.INDEX);
			elasticSearchService.index("twitter", "tokamak",
					new JsonObject().put("user", "hubrick").put("message", "love elastic search!"), indexOptions,
					indexResponse -> {
						if (indexResponse.failed())
							req.response().putHeader("context-type", "text/plain")
									.end(indexResponse.cause().toString());
						else
							req.response().putHeader("context-type", "text/plain")
									.end(indexResponse.result().toString());
					});
		}).listen(8080);
	}

}
