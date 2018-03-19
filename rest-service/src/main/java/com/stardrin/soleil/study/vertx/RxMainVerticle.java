/**
 * 2018年3月16日  下午5:25:32
 * soleil
 */
package com.stardrin.soleil.study.vertx;

import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.ServiceReference;

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
		router.get("/rest").handler(this::restHandler);
		vertx.createHttpServer().requestHandler(router::accept).listen(8090);

		discovery = ServiceDiscovery.create(vertx);

	}

	private void eventBusHandler(RoutingContext rct) {
		EventBus eb = vertx.eventBus();
		eb.<JsonObject>rxSend("hello", "Luke").subscribe(x -> rct.response().end(x.body().encodePrettily()), e -> {
			e.printStackTrace();
			rct.response().setStatusCode(500).end(e.getMessage());
		});
	}

	private void restHandler(RoutingContext rct) {
		discovery.getRecord(new JsonObject().put("name", "hello-service"), ar -> {
			if (ar.succeeded() && ar.result() != null) {
				ServiceReference ref = discovery.getReference(ar.result());
				HttpClient client = ref.getAs(HttpClient.class);
				client.getNow("/", response -> {
					response.bodyHandler(buf -> {
						rct.response().end(buf.toString());
						ref.release();
					});
				});
			}
		});
	}
}
