package com.alliander.osgp.platform.cucumber.support.ws;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

public class ContentLengthHeaderRemoveInterceptor implements HttpRequestInterceptor{
    
    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        //Remove Content Length Header from request
        request.removeHeaders(HTTP.CONTENT_LEN);
    }
}