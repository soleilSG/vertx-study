/**
 * 2018年3月16日  下午5:25:32
 * soleil
 */
package com.stardrin.soleil.study.vertx;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.codec.BodyCodec;

/**
 * @author soleil
 * @date 2018年3月16日
 * @time 下午5:25:32
 */
public class RxMainVerticle extends AbstractVerticle {

	private WebClient client;

	@Override
	public void start() throws Exception {
		client = WebClient.create(vertx);

		Router router = Router.router(vertx);
		router.get("/").handler(this::invokeResourceService);

		vertx.createHttpServer().requestHandler(router::accept).listen(8090);
	}

	private void invokeResourceService(RoutingContext rc) {
		HttpRequest<JsonObject> request1 = client.get(8080, "localhost", "/Luke").as(BodyCodec.jsonObject());
		HttpRequest<JsonObject> request2 = client.get(8080, "localhost", "/Soleil").as(BodyCodec.jsonObject());

		Single<JsonObject> s1 = request1.rxSend().map(HttpResponse::body);
		Single<JsonObject> s2 = request2.rxSend().map(HttpResponse::body);

		Single.zip(s1, s2, (luke, soleil) -> {
			return new JsonObject().put("Luke", luke.getString("message")).put("Soleil", soleil.getString("message"));
		}).subscribe(result -> rc.response().end(result.encodePrettily()), error -> {
			error.printStackTrace();
			rc.response().setStatusCode(500).end(error.getMessage());
		});
	}

}
