package com.stardrin.soleil.vertx.study;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start() {
		vertx.eventBus().<String>consumer("hello", message -> {
			JsonObject json = new JsonObject().put("served-by", this.toString());

			if (message.body().isEmpty()) {
				message.reply(json.put("message", "hello"));
			} else {
				message.reply(json.put("message", "hello " + message.body()));
			}
		});

		Router router = Router.router(vertx);
		router.get("/").handler(rc -> rc.response().end("hello"));

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);

		ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
		Record record = HttpEndpoint.createRecord("hello-service", "localhost", 8080, "/");
		discovery.publish(record, ar -> {
			if (ar.succeeded()) {
				System.out.println("hello-service has been published successfuly");
			} else {
				System.out.println(ar.cause());
			}
		});

	}
}
