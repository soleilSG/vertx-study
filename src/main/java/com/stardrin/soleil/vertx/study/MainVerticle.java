package com.stardrin.soleil.vertx.study;

import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start() {
		vertx.createHttpServer().requestHandler(req -> {
			req.response().putHeader("context-type", "text/plain").end("Hello from vert.x! Tesing hot deployment");
		}).listen(8080);
	}

}
