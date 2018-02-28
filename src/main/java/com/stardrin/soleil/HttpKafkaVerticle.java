/**
 * 2018年2月27日  上午10:58:10
 * soleil
 */
package com.stardrin.soleil;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;

import io.reactivex.Flowable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.reactivex.FlowableHelper;

/**
 * @author soleil
 * @date 2018年2月27日
 * @time 上午10:58:10
 */
public class HttpKafkaVerticle extends AbstractVerticle {

	private KafkaProducer<String, String> producer;

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		prepareKafka().compose(v -> startHttpServer()).setHandler(startFuture);
	}

	private Future<Void> startHttpServer() {
		Future<Void> future = Future.future();

		vertx.createHttpServer().requestHandler(req -> {
			req.setExpectMultipart(true);

			System.out.println("Request uri: " + req.uri());

			req.headers().forEach(m -> {
				System.out.println(m.getKey() + " : " + m.getValue());
			});
			
			System.out.println(Long.valueOf(req.getHeader("Content-Length")));
			
			if (Long.valueOf(req.getHeader("Content-Length")) > (10L * 1024 * 1024 * 1024)) {
				req.response().setStatusCode(413).end();
			}else {
				req.uploadHandler(upload -> {
					Flowable<Buffer> observalbe = FlowableHelper.toFlowable(upload);
					observalbe.forEach(data -> System.out.println("Read data: " + data.toString("UTF-8")));
				}).endHandler(v -> {
					req.response().putHeader("context-type", "text/plain").end("Hello from vert.x! Testing hot redeployment.");
				});
				
				/*
				req.uploadHandler(upload -> {
					System.out.println("file name: " + upload.filename());
					Buffer totalBuffer = Buffer.buffer();
					upload.handler(buf -> {
						totalBuffer.appendBuffer(buf);
					}).endHandler(v ->{
						producer.write(KafkaProducerRecord.create("test", totalBuffer.toString()));
					});
				}).endHandler(v -> {
					req.response().putHeader("context-type", "text/plain").end("Hello from vert.x! Testing hot redeployment.");
				});
				*/
			}

//			req.uploadHandler(upload -> {
//				System.out.println("file name: " + upload.filename());
//				
//				Buffer totalBuffer = Buffer.buffer();
//				upload.handler(buf -> {
//					System.out.println("Received: " + buf.length());
//					totalBuffer.appendBuffer(buf);
//					try {
//						producer.write(KafkaProducerRecord.create("test", buf.toJsonObject().toString()));
//					}catch(Exception e) {
//						System.err.println("Error: " + e.getMessage());
//						req.response().setStatusCode(500).setStatusMessage(e.getMessage());
//					}
//				});
				
//				upload.endHandler(v -> {
//					producer.write(KafkaProducerRecord.create("test", totalBuffer.toString()));
//				});
//			});

//			req.endHandler(v -> {
//				req.response().putHeader("context-type", "text/plain").end("Hello from vert.x! Testing hot redeployment.");
//			});
		}).connectionHandler(conn -> {
			System.out.println("Connection has been established.");
			conn.closeHandler(v -> System.out.println("Connection has been closed"));
		}).listen(8088, ar -> {
			if (ar.succeeded()) {
				future.complete();
			} else {
				future.fail(ar.cause());
			}
		});

		return future;
	}

	private Future<Void> prepareKafka() {
		Future<Void> future = Future.future();

		Properties config = new Properties();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

		producer = KafkaProducer.create(vertx, config);
		
		future.complete();
		return future;
	}

}
