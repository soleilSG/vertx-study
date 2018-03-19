package com.stardrin.soleil.vertx.study;

import com.stardrin.soleil.vertx.study.service.HelloService;
import com.stardrin.soleil.vertx.study.service.HelloServiceImpl;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ServiceProxyBuilder;

public class MainVerticle extends AbstractVerticle {

	@Override
	public void start() {
		new ServiceBinder(vertx).setAddress("eb.hello").register(HelloService.class, new HelloServiceImpl());

		ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
		Record record = EventBusService.createRecord("hello-service-eb", "eb.hello", HelloService.class);
		discovery.publish(record, ar -> {
			if (ar.succeeded())
				System.out.println("hello-service-eb has been published successfully!");
			else
				System.out.println(ar.cause());
		});

		ServiceProxyBuilder builder = new ServiceProxyBuilder(vertx).setAddress("eb.hello");
		HelloService helloService = builder.build(HelloService.class);

		Router router = Router.router(vertx);
		router.get("/").handler(rc -> {
			helloService.hello(r -> {
				if (r.succeeded())
					rc.response().end(r.result().toString());
				else
					rc.response().setStatusCode(500).end();
			});
		});

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
	}
}
