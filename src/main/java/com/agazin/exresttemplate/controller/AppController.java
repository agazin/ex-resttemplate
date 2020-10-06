package com.agazin.exresttemplate.controller;



import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.agazin.exresttemplate.handler.RestTemplateErrorHandler;
import com.agazin.exresttemplate.interceptor.RestTemplateHeaderModifierInterceptor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.service.UADetectorServiceFactory;

@RestController
@RequestMapping
@Slf4j
@AllArgsConstructor
public class AppController {

	private final RestTemplateBuilder restTemplateBuilder;
	private final RestTemplateErrorHandler restTemplateErrorHandler;
	private final RestTemplateHeaderModifierInterceptor interceptor;
	
	private RestTemplate getRestTemplate() {
		return restTemplateBuilder
				.additionalInterceptors(interceptor)
				.errorHandler(restTemplateErrorHandler)
				.build();
	}
	
	@GetMapping("/hello")
	public ResponseEntity<Object> hello(@RequestHeader Map<String, String> headers) {
		headers.forEach((key, value) -> {
	        log.info(String.format("Header '%s' = %s", key, value));
	    });
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		ReadableUserAgent ua = UADetectorServiceFactory.getResourceModuleParser().parse(request.getHeader("User-Agent"));
		String ipAddress = request.getRemoteAddr();
		String forwardedFor = request.getHeader("X-FORWARDED-FOR");
		if (forwardedFor != null) {
			ipAddress = forwardedFor.split(",")[0];
		}
		log.info("ipAddress " + ipAddress);
		if(new Random().nextInt(10) % 2 == 0) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok("hello");
	}
	
	@PostMapping("/hello")
	public String helloForm(@RequestParam String name) {
		return "hello : " + name;
	}
	
	@GetMapping("/hello/api")
	public String helloApi() {
		return getRestTemplate().getForObject("http://localhost:8080/hello", String.class);
	}

}
