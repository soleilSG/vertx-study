/**
 * 2018年3月2日  上午11:57:12
 * soleil
 */
package com.stardrin.soleil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * @author soleil
 * @date 2018年3月2日
 * @time 上午11:57:12
 */
public class WebVerticle extends AbstractVerticle {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		HttpServer server = vertx.createHttpServer();

		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create().setBodyLimit(1024).setDeleteUploadedFilesOnEnd(true));
		router.get("/").handler(this::indexHandler);
		router.post("/upload").handler(this::uploadHandler);

		server.connectionHandler(conn -> {
			logger.debug("Http connection has bean established!");
			conn.closeHandler(v -> {
				logger.debug("Http connection has been closed!");
			});
		}).requestHandler(router::accept).listen(8088, ar -> {
			if (ar.succeeded()) {
				logger.info("web server started!");
				startFuture.complete();
			} else {
				logger.error("web server start error");
				startFuture.fail(ar.cause());
			}
		});
	}

	private void indexHandler(RoutingContext rctx) {
		HttpServerRequest req = rctx.request();
		logger.debug("Request uri: " + req.uri());
		if (logger.isDebugEnabled()) {
			req.headers().forEach(m -> {
				logger.debug(m.getKey() + " : " + m.getValue());
			});
		}
		rctx.response().putHeader("content-type", "text/plain").end("Hello world from Vert.x-web!");
	}

	private void uploadHandler(RoutingContext rctx) {
		HttpServerRequest req = rctx.request();
		req.setExpectMultipart(true);

		logger.debug("Request uri: " + req.uri());
		if (logger.isDebugEnabled()) {
			req.headers().forEach(m -> {
				logger.debug(m.getKey() + " : " + m.getValue());
			});
		}
	}
}
