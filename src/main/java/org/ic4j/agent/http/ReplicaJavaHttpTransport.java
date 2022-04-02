/*
 * Copyright 2021 Exilor Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.ic4j.agent.http;


import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.ArrayUtils;
import org.ic4j.agent.AgentError;
import org.ic4j.agent.ReplicaTransport;
import org.ic4j.agent.requestid.RequestId;
import org.ic4j.types.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReplicaJavaHttpTransport implements ReplicaTransport {
	protected static final Logger LOG = LoggerFactory.getLogger(ReplicaOkHttpTransport.class);

	final HttpClient client;

	URI uri;
	
	ReplicaJavaHttpTransport(URI url) {

		//check if url ends with /	
		if('/' == url.toString().charAt(url.toString().length() - 1))
			this.uri = URI.create(url.toString().substring(0, url.toString().length() - 1));
		else	
			this.uri = url;

		client = HttpClient.newHttpClient();
	}

	ReplicaJavaHttpTransport(URI url, int timeout) {	
		//check if url ends with /	
		if('/' == url.toString().charAt(url.toString().length() - 1))
			this.uri = URI.create(url.toString().substring(0, url.toString().length() - 1));
		else	
			this.uri = url;

		client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(timeout)).build();
	}



	public static ReplicaTransport create(String url) throws URISyntaxException {
		return new ReplicaJavaHttpTransport(new URI(url));
	}



	public static ReplicaTransport create(String url,  int timeout)
			throws URISyntaxException {
		return new ReplicaJavaHttpTransport(new URI(url), timeout);
	}

	public CompletableFuture<byte[]> status() {
		
		HttpRequest httpRequest;
		try {
			httpRequest = HttpRequest.newBuilder().uri(new URI(uri.toString() + ReplicaHttpProperties.API_VERSION_URL_PART + ReplicaHttpProperties.STATUS_URL_PART)).GET().header(ReplicaHttpProperties.CONTENT_TYPE, ReplicaHttpProperties.DFINITY_CONTENT_TYPE).build();

		} catch (URISyntaxException e) {
			throw AgentError.create(AgentError.AgentErrorCode.URL_PARSE_ERROR, e);
		}		

		return this.execute(httpRequest);		
	}

	public CompletableFuture<byte[]> query(Principal containerId, byte[] envelope) {			
		HttpRequest httpRequest;
		try {
			httpRequest = HttpRequest.newBuilder().uri(new URI(uri.toString() + ReplicaHttpProperties.API_VERSION_URL_PART + String.format(ReplicaHttpProperties.QUERY_URL_PART, containerId.toString()))).header(ReplicaHttpProperties.CONTENT_TYPE, ReplicaHttpProperties.DFINITY_CONTENT_TYPE).POST(BodyPublishers.ofByteArray(envelope)).build();
		} catch (URISyntaxException e) {
			throw AgentError.create(AgentError.AgentErrorCode.URL_PARSE_ERROR, e);
		}		
				
		return this.execute(httpRequest);

	}

	public CompletableFuture<byte[]> call(Principal containerId, byte[] envelope, RequestId requestId) {		
		HttpRequest httpRequest;
		try {
			httpRequest = HttpRequest.newBuilder().uri(new URI(uri.toString() + ReplicaHttpProperties.API_VERSION_URL_PART + String.format(ReplicaHttpProperties.CALL_URL_PART, containerId.toString()))).header(ReplicaHttpProperties.CONTENT_TYPE, ReplicaHttpProperties.DFINITY_CONTENT_TYPE).POST(BodyPublishers.ofByteArray(envelope)).build();
		} catch (URISyntaxException e) {
			throw AgentError.create(AgentError.AgentErrorCode.URL_PARSE_ERROR, e);
		}		
				
		return this.execute(httpRequest);		

	}

	public CompletableFuture<byte[]> readState(Principal containerId, byte[] envelope) {
		HttpRequest httpRequest;
		try {
			httpRequest = HttpRequest.newBuilder().uri(new URI(uri.toString() + ReplicaHttpProperties.API_VERSION_URL_PART + String.format(ReplicaHttpProperties.READ_STATE_URL_PART, containerId.toString()))).header(ReplicaHttpProperties.CONTENT_TYPE, ReplicaHttpProperties.DFINITY_CONTENT_TYPE).POST(BodyPublishers.ofByteArray(envelope)).build();
		} catch (URISyntaxException e) {
			throw AgentError.create(AgentError.AgentErrorCode.URL_PARSE_ERROR, e);
		}		
				
		return this.execute(httpRequest);

	}

	CompletableFuture<byte[]> execute(HttpRequest httpRequest) throws AgentError {

		try {
			URI requestUri = httpRequest.uri();

			LOG.debug("Executing request " + httpRequest.method() + " " + requestUri);

			CompletableFuture<byte[]> response = new CompletableFuture<byte[]>();
			
	        CompletableFuture<HttpResponse<byte[]>> httpResponseFuture =
	                client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());

	        httpResponseFuture.whenComplete((httpResponse, ex) -> {
				if (ex == null) {
					if (httpResponse == null)
						response.completeExceptionally(
								AgentError.create(AgentError.AgentErrorCode.HTTP_ERROR ));

					byte[] bytes = httpResponse.body();
					if (bytes == null)
						bytes = ArrayUtils.EMPTY_BYTE_ARRAY;
					response.complete(bytes);	
				}
				else 
					response.completeExceptionally(ex);
	        });

			return response;

		} catch (Exception e) {
			throw AgentError.create(AgentError.AgentErrorCode.URL_PARSE_ERROR, e);
		}

	}

}
