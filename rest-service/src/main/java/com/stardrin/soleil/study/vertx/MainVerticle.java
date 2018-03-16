package com.stardrin.soleil.study.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

public class MainVerticle extends AbstractVerticle {

	private WebClient client;

	@Override
	public void start() {
		client = WebClient.create(vertx);

		Router router = Router.router(vertx);
		router.get("/").handler(this::invokeResourceService);

		vertx.createHttpServer().requestHandler(router::accept).listen(8090);
	}

	private void invokeResourceService(RoutingContext rc) {
		HttpRequest<JsonObject> request = client.get(8080, "localhost", "/soleil").as(BodyCodec.jsonObject());
		request.send(ar -> {
			if (ar.failed()) {
				rc.fail(ar.cause());
			} else {
				rc.response().end(ar.result().body().encode());
			}
		});
	}

}