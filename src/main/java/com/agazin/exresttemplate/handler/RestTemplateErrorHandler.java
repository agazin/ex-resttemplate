package com.agazin.exresttemplate.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.agazin.exresttemplate.exception.RestException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RestTemplateErrorHandler extends DefaultResponseErrorHandler {

	@Autowired
	ObjectMapper objectMapper;

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		log.error("error : {}" , response.getStatusCode());
		if (response.getStatusCode().value() == 400) {
			throw new RestException();
		}
		super.handleError(response);
	}

}
