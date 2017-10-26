package com.movilizer.ps.utils

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.movilizer.maf.bo.json.MAFJsonElement
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.ContentType
import org.apache.http.impl.client.DefaultHttpClient

import java.nio.charset.Charset

class MafDropInReplacements {
	
	static int TIMEOUT_MILLIS = 5000
	
	static RequestConfig requestConfig = RequestConfig.custom()
	.setSocketTimeout(TIMEOUT_MILLIS)
	.setConnectTimeout(TIMEOUT_MILLIS)
	.setConnectionRequestTimeout(TIMEOUT_MILLIS)
	.build();

    static MAFJsonElement parseJSON(String input) {
        JsonElement json = (new JsonParser()).parse(input)
        return new MAFJsonElement(json)
    }

    static Object doRESTCall(String address, Object data, String dataformat, boolean doPost, String charsetname, HashMap httpHeaders) {
        HttpResponse response
        if (doPost) {
            response = postCall(address, data, dataformat, httpHeaders)
        } else {
            response = getCall(address, httpHeaders)
        }
		
		if(dataformat.equals("BYTES"))
			return response.getEntity().getContent().getBytes()
		else
			return response.getEntity().getContent().getText()
    }
	
	static Object doRESTCall(String address, Object data, String dataformat, String method, String charsetname, HashMap httpHeaders) {
		HttpResponse response
		if (method.equals("POST")) {
			response = postCall(address, data, dataformat, httpHeaders)
		} else if(method.equals("GET")) {
			response = getCall(address, httpHeaders)
		} else if(method.equals("PUT")) {
			response = putCall(address, data, dataformat, httpHeaders)
		}
		
		if(dataformat.equals("BYTES"))
			return response.getEntity().getContent().getBytes()
		else
			return response.getEntity().getContent().getText()
	}

    private static HttpResponse postCall(String url, Object body, String dataformat, HashMap<String, String> headers) {
        HttpClient client = new DefaultHttpClient()
        Charset charset = Charset.forName("UTF-8")

        HttpPost post = new HttpPost(url)
		post.setConfig(requestConfig)
		if(dataformat.equals("BYTES")) {
			post.setEntity(new ByteArrayEntity(body, ContentType.APPLICATION_OCTET_STREAM))
		} else {
			post.setEntity(new ByteArrayEntity(((String) body).getBytes(charset), ContentType.DEFAULT_TEXT))
		}

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            post.addHeader((String) entry.getKey(), (String) entry.getValue())
        }

        return client.execute(post)
    }
	
	private static HttpResponse putCall(String url, Object body, String dataformat, HashMap<String, String> headers) {
		HttpClient client = new DefaultHttpClient()
		Charset charset = Charset.forName("UTF-8")

		HttpPut put = new HttpPut(url)
		put.setConfig(requestConfig)
		if(dataformat.equals("BYTES")) {
			put.setEntity(new ByteArrayEntity(body, ContentType.APPLICATION_OCTET_STREAM))
		} else {
			put.setEntity(new ByteArrayEntity(((String) body).getBytes(charset), ContentType.DEFAULT_TEXT))
		}

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			put.addHeader((String) entry.getKey(), (String) entry.getValue())
		}

		return client.execute(put)
	}
	
    private static HttpResponse getCall(String url, HashMap<String, String> headers) {
        HttpClient client = new DefaultHttpClient()
        HttpGet get = new HttpGet(url)
		get.setConfig(requestConfig)
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            get.addHeader((String) entry.getKey(), (String) entry.getValue())
        }

        return client.execute(get)
    }
}
