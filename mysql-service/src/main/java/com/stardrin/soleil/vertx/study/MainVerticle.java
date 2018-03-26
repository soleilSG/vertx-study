package com.stardrin.soleil.vertx.study;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;

public class MainVerticle extends AbstractVerticle {
	private SQLClient mySQLClient;

    @Override
    public void start() {
    	JsonObject mySQLClientConfig = new JsonObject();
    	mySQLClientConfig.put("username", "root").put("password", "soleilMySQL123").put("database", "test").put("sslMode", "disable");
    	
    	mySQLClient = MySQLClient.createShared(vertx, mySQLClientConfig);
    	
		mySQLClient.getConnection(ar -> {
			if (ar.succeeded()) {
				SQLConnection con = ar.result();
				con.query("SELECT * FROM Pages", ar1 -> {
					if(ar1.succeeded()) {
						ResultSet rset = ar1.result();
						for(JsonObject row : rset.getRows())
							System.out.println(row);
					}
				});
			} else {
				System.out.println(ar.cause().toString());
			}
		});
    }

	@Override
	public void stop() throws Exception {
		mySQLClient.close();
	}
}
