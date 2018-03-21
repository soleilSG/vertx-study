package com.stardrin.soleil.vertx.study;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.CorsHandler;
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSHandler;

public class MainVerticle extends AbstractVerticle {

	private String taskId;

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		EventBus eb = vertx.eventBus();

		SockJSHandlerOptions options = new SockJSHandlerOptions().setHeartbeatInterval(2000);
		SockJSHandler sockJsHandler = SockJSHandler.create(vertx, options);
		BridgeOptions bridgeOptions = new BridgeOptions()
				.addOutboundPermitted(new PermittedOptions().setAddress("eb.task"));
		sockJsHandler.bridge(bridgeOptions);

		Router router = Router.router(vertx);
		router.route().handler(CorsHandler.create("*").allowedMethod(HttpMethod.GET).allowedHeader("Access-Control-Allow-Origin"));
		router.route("/eb/notification/*").handler(sockJsHandler);
		router.get("/eb/task/:taskId").handler(rctx -> {
			taskId = rctx.request().getParam("taskId");
			rctx.response().end();
		});
		router.get("/api/task/emmit").handler(rctx -> {
			JsonObject message = new JsonObject();
			message.put("id", taskId);
			message.put("state", true);
			message.put("msg", "http://www.baidu.com");
			eb.publish("eb.task", message);
			rctx.response().end();
		});
		router.get("/api/test").handler(rctx -> {
			rctx.response().putHeader("Content-Type", "text/plain").end("Hello vertx");
		});

		vertx.createHttpServer().requestHandler(router::accept).listen(8080, ar -> {
			if (ar.succeeded())
				startFuture.complete();
			else
				System.out.println(ar.cause());
		});
	}

}
