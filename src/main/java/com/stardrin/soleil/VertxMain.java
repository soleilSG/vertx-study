/**
 * 2018年2月23日  上午10:40:19
 * soleil
 */
package com.stardrin.soleil;

import io.vertx.core.Vertx;

/**
 * @author soleil
 * @date 2018年2月23日
 * @time 上午10:40:19
 */
public class VertxMain {
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		vertx.setPeriodic(1000, (id) -> System.out.println("timer fired!"));

		vertx.deployVerticle("com.stardrin.soleil.MyHttpServerVerticle", ar -> {
			if (ar.succeeded())
				System.out.println("verticle has been deployed successfully!");
			else
				System.out.println("verticle deployment failed!");
		});

		while (true) {
		}
	}
}
