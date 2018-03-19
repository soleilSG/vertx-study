/**
 * 2018年3月19日  下午2:04:41
 * soleil
 */
package com.stardrin.soleil.vertx.study.service;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author soleil
 * @date 2018年3月19日
 * @time 下午2:04:41
 */
@ProxyGen
public interface HelloService {

	static HelloService create() {
		return new HelloServiceImpl();
	}

	static HelloService createProxy(Vertx vertx, String address) {
		return new HelloServiceVertxEBProxy(vertx, address);
	}

	void hello(Handler<AsyncResult<JsonObject>> resultHandler);

}
