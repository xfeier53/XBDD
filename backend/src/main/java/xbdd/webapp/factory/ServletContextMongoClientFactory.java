/**
 * Copyright (C) 2015 Orion Health (Orchestral Development Ltd)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xbdd.webapp.factory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.apache.commons.lang.math.NumberUtils;

import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Factory for {@link MongoClient} that retrieves hostname and port parameters from the ServletContext. The {@link MongoClient} is designed
 * to be used a {@link Singleton}. <br>
 * To specify a hostname, port number, username and password for the mongo db connection you can add parameters
 * <code>sample_reports.mongo.hostname</code>, <code>sample_reports.monogo.port</code>, <code>sample_reports.monogo.username</code> and <code>sample_reports.monogo.password</code>.
 * The parameters are optional and will default to the mongo defaults {@link ServerAddress#defaultHost()} and
 * {@link ServerAddress#defaultPort()} respectively.
 */
public class ServletContextMongoClientFactory implements Supplier<MongoDBAccessor> {

	private static final String XBDD_MONGO_HOSTNAME_INIT_PARAMETER = "xbdd.mongo.hostname";
	private static final String XBDD_MONGO_USERNAME_INIT_PARAMETER = "xbdd.mongo.username";
	private static final String XBDD_MONGO_PASSWORD_INIT_PARAMETER = "xbdd.mongo.password";
	private static final String XBDD_MONGO_PORT_INIT_PARAMETER = "xbdd.mongo.port";

	private final String host;
	private final int port;
	private final String username;
	private final char[] password;

	public ServletContextMongoClientFactory(@Context final ServletContext context) {
		this.host = Objects.toString(context.getInitParameter(XBDD_MONGO_HOSTNAME_INIT_PARAMETER), ServerAddress.defaultHost());
		this.username = context.getInitParameter(XBDD_MONGO_USERNAME_INIT_PARAMETER);
		this.password = context.getInitParameter(XBDD_MONGO_PASSWORD_INIT_PARAMETER) != null ?
				context.getInitParameter(XBDD_MONGO_PASSWORD_INIT_PARAMETER).toCharArray() : null;
		this.port = NumberUtils.isNumber(context.getInitParameter(XBDD_MONGO_PORT_INIT_PARAMETER)) ?
				Integer.parseInt(context.getInitParameter(XBDD_MONGO_PORT_INIT_PARAMETER)) : ServerAddress.defaultPort();
	}

	private MongoDBAccessor getMongoDBAccessor() throws UnknownHostException {
		final MongoClient mc;
		if (this.username != null) {
			MongoCredential credentials = MongoCredential.createScramSha1Credential(this.username, "admin", this.password);
			System.out.println(String.format("name: %s, pw: %s", this.username, this.password));
			mc = new MongoClient(new ServerAddress(this.host, this.port), credentials, MongoClientOptions.builder().build());
		} else {
			mc = new MongoClient(this.host, this.port);
		}

		return new MongoDBAccessor(mc);
	}

	@Override
	public MongoDBAccessor get() {
		try {
			return getMongoDBAccessor();
		} catch (final UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

}
