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
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.ArrayUtils;
import org.ic4j.agent.AgentError;
import org.ic4j.agent.ReplicaResponse;
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

	public CompletableFuture<ReplicaResponse> status() {
		
		HttpRequest httpRequest;
		try {
			httpRequest = HttpRequest.newBuilder().uri(new URI(uri.toString() + ReplicaHttpProperties.API_VERSION_URL_PART + ReplicaHttpProperties.STATUS_URL_PART)).GET().header(ReplicaHttpProperties.CONTENT_TYPE, ReplicaHttpProperties.DFINITY_CONTENT_TYPE).build();

		} catch (URISyntaxException e) {
			throw AgentError.create(AgentError.AgentErrorCode.URL_PARSE_ERROR, e);
		}		

		return this.execute(httpRequest);		
	}

	public CompletableFuture<ReplicaResponse> query(Principal containerId, byte[] envelope, Map<String,String> headers) {			
		try {
			HttpRequest.Builder builder = HttpRequest.newBuilder().uri(new URI(uri.toString() + ReplicaHttpProperties.API_VERSION_URL_PART + String.format(ReplicaHttpProperties.QUERY_URL_PART, containerId.toString())))
					.header(ReplicaHttpProperties.CONTENT_TYPE, ReplicaHttpProperties.DFINITY_CONTENT_TYPE)
					.POST(BodyPublishers.ofByteArray(envelope));	

			if(headers != null)
			{
				Iterator<String> names = headers.keySet().iterator();
				
				while(names.hasNext())
				{
					String name = names.next();
					builder.header(name, headers.get(name));
				}			
			}
			
			HttpRequest httpRequest = builder.build();		
			
			return this.execute(httpRequest);
		
		} catch (URISyntaxException e) {
			throw AgentError.create(AgentError.AgentErrorCode.URL_PARSE_ERROR, e);
		}		

	}

	public CompletableFuture<ReplicaResponse> call(Principal containerId, byte[] envelope, RequestId requestId, Map<String,String> headers) {		
		try {
			HttpRequest.Builder builder = HttpRequest.newBuilder().uri(new URI(uri.toString() + ReplicaHttpProperties.API_VERSION_URL_PART + String.format(ReplicaHttpProperties.CALL_URL_PART, containerId.toString()))).header(ReplicaHttpProperties.CONTENT_TYPE, ReplicaHttpProperties.DFINITY_CONTENT_TYPE).POST(BodyPublishers.ofByteArray(envelope));

			if(headers != null)
			{
				Iterator<String> names = headers.keySet().iterator();
				
				while(names.hasNext())
				{
					String name = names.next();
					builder.header(name, headers.get(name));
				}			
			}
			
			HttpRequest httpRequest = builder.build();
			
			return this.execute(httpRequest);
		
		} catch (URISyntaxException e) {
			throw AgentError.create(AgentError.AgentErrorCode.URL_PARSE_ERROR, e);
		}		
				
				

	}

	public CompletableFuture<ReplicaResponse> readState(Principal containerId, byte[] envelope, Map<String,String> headers) {
		try {
			HttpRequest.Builder builder = HttpRequest.newBuilder().uri(new URI(uri.toString() + ReplicaHttpProperties.API_VERSION_URL_PART + String.format(ReplicaHttpProperties.READ_STATE_URL_PART, containerId.toString()))).header(ReplicaHttpProperties.CONTENT_TYPE, ReplicaHttpProperties.DFINITY_CONTENT_TYPE).POST(BodyPublishers.ofByteArray(envelope));
		
			if(headers != null)
			{
				Iterator<String> names = headers.keySet().iterator();
				
				while(names.hasNext())
				{
					String name = names.next();
					builder.header(name, headers.get(name));
				}			
			}
			
			HttpRequest httpRequest = builder.build();
			
			return this.execute(httpRequest);
		} catch (URISyntaxException e) {
			throw AgentError.create(AgentError.AgentErrorCode.URL_PARSE_ERROR, e);
		}		
				
		

	}

	CompletableFuture<ReplicaResponse> execute(HttpRequest httpRequest) throws AgentError {

		URI requestUri = httpRequest.uri();
		try {		
			LOG.debug("Executing request " + httpRequest.method() + " " + requestUri);

			CompletableFuture<ReplicaResponse> response = new CompletableFuture<ReplicaResponse>();
			
	        CompletableFuture<HttpResponse<byte[]>> httpResponseFuture =
	                client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());

	        httpResponseFuture.whenComplete((httpResponse, ex) -> {
				try {
		        	if (ex == null) {
						if (httpResponse == null)
							response.completeExceptionally(
									AgentError.create(AgentError.AgentErrorCode.HTTP_ERROR ));
	
						ReplicaResponse replicaResponse = new ReplicaResponse();
						
						replicaResponse.headers = new HashMap<String,String>();
						
						HttpHeaders headers = httpResponse.headers();					
						
						for(String name : headers.map().keySet())
						{	
							String value = null;
							
							if(headers.firstValue(name).isPresent())
								value = headers.firstValue(name).get();
													
							replicaResponse.headers.put(name, value);	
						}
						
						byte[] bytes = httpResponse.body();
						if (bytes == null)
							bytes = ArrayUtils.EMPTY_BYTE_ARRAY;
						
						replicaResponse.payload = bytes;
						response.complete(replicaResponse);	
					}
					else 
						response.completeExceptionally(ex);
	        }catch(Throwable t)
			{
				LOG.debug(requestUri + "->" + t);
				response.completeExceptionally(
						AgentError.create(AgentError.AgentErrorCode.HTTP_ERROR, t, t.getLocalizedMessage()));						
			}
	        });

			return response;

		} catch (Exception e) {
			LOG.debug(requestUri + "->" + e);
			throw AgentError.create(AgentError.AgentErrorCode.URL_PARSE_ERROR, e);
		}

	}
	
	public void close()
	{	
	}

}
