/**
 * 2018年3月16日  下午5:25:32
 * soleil
 */
package com.stardrin.soleil.vertx.study;

import com.stardrin.soleil.vertx.study.service.HelloService;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.types.EventBusService;

/**
 * @author soleil
 * @date 2018年3月16日
 * @time 下午5:25:32
 */
public class RxMainVerticle extends AbstractVerticle {

	private ServiceDiscovery discovery;

	@Override
	public void start() throws Exception {
		Router router = Router.router(vertx);
		router.get("/eventBus").handler(this::eventBusHandler);
		vertx.createHttpServer().requestHandler(router::accept).listen(8090);

		discovery = ServiceDiscovery.create(vertx);

	}

	private void eventBusHandler(RoutingContext rct) {
		// Option 1
		/*
		discovery.getRecord(new JsonObject().put("name", "hello-service-eb"), ar -> {
			if(ar.succeeded() && ar.result() != null) {
				ServiceReference ref = discovery.getReference(ar.result());
				ref.getAs(HelloService.class).hello(r -> {
					if(r.succeeded()) {
						rct.response().end(r.result().toString());
					} else {
						rct.response().setStatusCode(500).end();
					}
				});
				ref.release();
			} else {
				rct.response().setStatusCode(500).end();
			}
		});
		*/

		// Option 2
		EventBusService.getServiceProxyWithJsonFilter(discovery, new JsonObject().put("name", "hello-service-eb"),
				HelloService.class, ar -> {
					if (ar.succeeded()) {
						HelloService service = ar.result();
						service.hello(r -> {
							rct.response().end(r.result().toString());
						});
						ServiceDiscovery.releaseServiceObject(discovery, service);
					} else
						rct.response().setStatusCode(500).end();
				});
	}
}
