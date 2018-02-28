/**
 * 2018年2月23日  下午2:20:34
 * soleil
 */
package com.stardrin.soleil;

import java.util.Date;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.streams.Pump;

/**
 * @author soleil
 * @date 2018年2月23日
 * @time 下午2:20:34
 */
public class MyHttpServerVerticle extends AbstractVerticle {
	private HttpServer server;

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		server = vertx.createHttpServer().requestHandler(req -> {
			req.setExpectMultipart(true);
			
			System.out.println("Request uri: " + req.uri());
			
			req.headers().forEach(m -> {
				System.out.println(m.getKey() + " : " + m.getValue());
			});
			
			req.uploadHandler(upload -> {
				System.out.println("file name: " + upload.filename());
				upload.streamToFileSystem(upload.filename());
			});
			
			req.endHandler(v -> {
				req.response().putHeader("context-type", "text/plain")
						.end("Hello from vert.x! Testing hot redeployment.");
			});
			
//			AsyncFile output = vertx.fileSystem().openBlocking(String.valueOf(new Date().getTime()), new OpenOptions());
			
//			if(req.method() == HttpMethod.POST /*&& req.isExpectMultipart()*/) {
//				req.uploadHandler(f -> {
//					System.out.println("file name: " + f.filename());
//				});
//				System.out.println("i am here");
//				req.headers().forEach(m -> {
//					System.out.println(m.getKey() + " : " + m.getValue());
//				}); 
//				Pump.pump(req, output).start();
//			}
//			final Map<String, Integer> counter = new HashMap<String, Integer>();
			
			
//			counter.put("count", 0);
//			req.handler(buf -> System.out.println("I have received a chunk of the body of length " + buf.length()));
//			req.handler(buf -> {
//				int i = 0; 
//				buf.length();
//				Integer count = counter.get("count");
//				count += 1;
//				System.out.println("Data read: " + count);
//				counter.put("count", count);
//			});
			
//			req.endHandler(v -> {
//				System.out.println("Data read: " + counter.get("count"));
//				req.response().putHeader("context-type", "text/plain").end("Hello from vert.x! Testing hot redeployment.");
//			});
			// System.out.println("version: " + req.version().toString());
			// System.out.println("url: " + req.uri());
//			req.response().putHeader("context-type", "text/plain").end("Hello from vert.x! Testing hot redeployment.");
			

			
			/*
			req.bodyHandler(b -> {
				System.err.println(req.formAttributes().size());
				req.formAttributes().forEach(m -> {
					System.out.println(m.getKey() + " : " + m.getValue());
				});
				System.out.println("Read file: " + b.length());
				req.response().putHeader("context-type", "text/plain").end("Hello from vert.x! Testing hot redeployment.");
			});
			*/
		});
		
		server.connectionHandler(conn -> {
			System.out.println("Connection has been established.");
			conn.closeHandler((Void) -> System.out.println("Connection has been closed"));
		});

		server.listen(8080, ar -> {
			if (ar.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(ar.cause());
			}
		});
	}
}
