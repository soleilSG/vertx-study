/**
 * 2018年3月16日  下午5:25:32
 * soleil
 */
package com.stardrin.soleil.study.vertx;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;

/**
 * @author soleil
 * @date 2018年3月16日
 * @time 下午5:25:32
 */
public class RxMainVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		vertx.createHttpServer().requestHandler(req -> {
			EventBus eb = vertx.eventBus();

			eb.<JsonObject>rxSend("hello", "Luke").subscribe(x -> req.response().end(x.body().encodePrettily()), e -> {
				e.printStackTrace();
				req.response().setStatusCode(500).end(e.getMessage());
			});

		}).listen(8090);

	}
}
