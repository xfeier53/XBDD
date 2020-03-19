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
package xbdd.webapp.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.message.internal.AbstractMessageReaderWriterProvider;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces({ "application/json", "text/plain", "*/*" })
@Consumes({ "application/json", "text/plain", "*/*" })
public class BasicDBReader extends AbstractMessageReaderWriterProvider<BasicDBObject> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return DBObject.class.isAssignableFrom(type);
	}

	@Override
	public void writeTo(BasicDBObject myBean,
			Class<?> type,
			Type genericType,
			Annotation[] annotations,
			MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream)
			throws IOException, WebApplicationException {
		writeToAsString(myBean.toJson(), entityStream, mediaType);
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType) {
		return DBObject.class.isAssignableFrom(type);
	}

	@Override
	public BasicDBObject readFrom(Class<BasicDBObject> arg0, Type arg1,
			Annotation[] arg2, MediaType arg3,
			MultivaluedMap<String, String> arg4, InputStream entityStream)
			throws IOException, WebApplicationException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(entityStream, writer, "UTF-8");
		String theString = writer.toString();
		return BasicDBObject.parse(theString);
	}

}
