/**
 * 2018年3月1日  下午6:30:51
 * soleil
 */
package com.stardrin.soleil;

import io.reactivex.Observable;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerRequest;

/**
 * @author soleil
 * @date 2018年3月1日
 * @time 下午6:30:51
 */
public class HttpRxVerticle extends AbstractVerticle {
	@Override
	public void start() throws Exception {
		HttpServer server = vertx.createHttpServer();
		
		server.connectionHandler(conn -> {
			System.out.println("Connection has been established.");
			conn.closeHandler(v -> System.out.println("Connection has been closed"));
		}).listen(8088);
		
		
		Observable<HttpServerRequest> requestObservable = server.requestStream().toObservable();
		
		requestObservable.subscribe(request -> {
			System.out.println("Request uri: " + request.uri());
			request.setExpectMultipart(true);
			
			request.toObservable().subscribe(buf -> {
				System.out.println("request buf: " + buf.toString());
			}, v -> {}, () -> {
				request.response().putHeader("context-type", "text/plain").end("Hello from vert.x! Testing hot redeployment.");
			});
		});

	}
}
