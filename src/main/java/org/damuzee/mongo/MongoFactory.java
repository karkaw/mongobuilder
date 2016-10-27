package org.damuzee.mongo;

import com.mongodb.client.MongoDatabase;

public interface MongoFactory {


	/**
	 * Creates a default {@link MongoDatabase} instance.
	 */
	MongoDatabase getDatabase();

	/**
	 * 销毁
	 *
	 * @throws Exception
	 */
	 void destroy() throws Exception;
}
