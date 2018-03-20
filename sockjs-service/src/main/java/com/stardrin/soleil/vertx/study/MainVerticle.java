package com.stardrin.soleil.vertx.study;

import io.vertx.core.Future;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSHandler;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		SockJSHandlerOptions options = new SockJSHandlerOptions().setHeartbeatInterval(2000);
		SockJSHandler sockJsHandler = SockJSHandler.create(vertx, options);
		sockJsHandler.socketHandler(s -> {
			s.handler(s::write);
		});

		Router router = Router.router(vertx);
		// router.route().handler(CorsHandler.create("*").allowedMethod(HttpMethod.GET).allowedHeader("Access-Control-Allow-Origin"));
		router.get("/eb/echo/*").handler(sockJsHandler);

		vertx.createHttpServer().requestHandler(router::accept).listen(8080, ar -> {
			if (ar.succeeded())
				startFuture.complete();
			else
				System.out.println(ar.cause());
		});
	}

}
