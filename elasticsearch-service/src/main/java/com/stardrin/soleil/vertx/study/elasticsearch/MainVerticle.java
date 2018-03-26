package com.stardrin.soleil.vertx.study.elasticsearch;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class MainVerticle extends AbstractVerticle {

	/*
    @Override
    public void start() {
    	
//    	DeploymentOptions options = new DeploymentOptions();
    	vertx.deployVerticle("service:com.hubrick.vertx.vertx-elasticsearch-service", ar -> {
    		if(ar.succeeded())
    			System.out.println("Es service has been deployed successfully");
    		else
    			System.out.println(ar.cause().toString());
    	});
    	RxElasticSearchService rxElasticSearchService = RxElasticSearchService.createEventBusProxy(vertx, "et.elasticsearch");
    	
    	IndexOptions indexOptions = new IndexOptions().setId("124").setOpType(OpType.INDEX);
    	
    	rxElasticSearchService.index("twitter", "_doc", new JsonObject().put("user", "hubrick").put("message", "love elastic search!"), indexOptions)
        .subscribe(indexResponse -> {
        	System.out.println(indexResponse.toString());
        }, err ->{
        	err.printStackTrace();
        });
    	System.setProperty("es.set.netty.runtime.available.processors", "false");
    	DefaultElasticSearchService es = new DefaultElasticSearchService(new DefaultTransportClientFactory(), new JsonElasticSearchConfigurator(vertx));
    	new ServiceBinder(vertx).setAddress("eventbus-address").register(ElasticSearchService.class, es);
    	es.start();
    	 */
    	
    	/*
    	 ElasticSearchService elasticSearchService = ElasticSearchService.createEventBusProxy(vertx, "eventbus-address");
    	    if(elasticSearchService != null) {
    	    	IndexOptions indexOptions = new IndexOptions()
    	    			.setId("123")
    	    			.setOpType(OpType.INDEX);
    	    	elasticSearchService.index("twitter", "tokamak", new JsonObject().put("user", "hubrick").put("message", "love elastic search!"), indexOptions, indexResponse -> {
    	    		if(indexResponse.failed())
    	    			System.out.println(indexResponse.cause().toString());
    	    		else
    	    			System.out.println(indexResponse.result().toString());
    	    	});
    	    	
    	    	GetOptions getOptions = new GetOptions()
    	    	        .setFetchSource(true)
    	    	        .addField("id")
    	    	        .addField("message");
    	    	    
    	    	    elasticSearchService.get("twitter", "tweet", "123", getOptions, getResponse -> {
    	    	    	if(getResponse.failed())
    	    	    		System.out.println(getResponse.cause().toString());
    	    	    	else
    	    	    		System.out.println(getResponse.result().toString());
    	    	    });
    	    }
    	    
    }
    	 */

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		Future<String> esVerticleDeployment = Future.future();
		vertx.deployVerticle("service:com.hubrick.vertx.vertx-elasticsearch-service", esVerticleDeployment.completer());
		
		esVerticleDeployment.compose(id -> {
			Future<String> httpVerticleDeployment = Future.future();
			vertx.deployVerticle("com.stardrin.soleil.vertx.study.elasticsearch.HttpVerticle", httpVerticleDeployment.completer());
			return httpVerticleDeployment;
		}).setHandler(ar -> {
			if(ar.succeeded())
				startFuture.complete();
			else
				startFuture.fail(ar.cause());
		});
	}
}
