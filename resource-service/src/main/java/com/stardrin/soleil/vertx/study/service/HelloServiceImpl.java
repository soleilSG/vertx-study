/**
 * 2018年3月19日  下午2:41:13
 * soleil
 */
package com.stardrin.soleil.vertx.study.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * @author soleil
 * @date 2018年3月19日
 * @time 下午2:41:13
 */
public class HelloServiceImpl implements HelloService {

	@Override
	public void hello(Handler<AsyncResult<JsonObject>> resultHandler) {
		JsonObject result = new JsonObject();
		result.put("message", "hello");
		resultHandler.handle(Future.succeededFuture(result));
	}
}
