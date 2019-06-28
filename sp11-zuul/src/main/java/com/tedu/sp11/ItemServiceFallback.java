package com.tedu.sp11;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.tedu.web.util.JsonResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ItemServiceFallback implements FallbackProvider {
	@Override
	public String getRoute() {
		/**
		 * 当执行item-service失败执行当前降级类
		 * 
		 * 针对哪个微服务进行降级
		 * 		*和null表示都降级
		 * 写具体的服务Id就只应用当前的
		 */
		return "item-service"; //"*"; //null;
	}
	//该方法返回封装响应 的降级对象
	@Override
	public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        return response();
	}

	private ClientHttpResponse response() {
		//j就是对http协议进行封装
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }
            @Override
            public int getRawStatusCode() throws IOException {
                return HttpStatus.OK.value();
            }
            @Override
            public String getStatusText() throws IOException {
                return HttpStatus.OK.getReasonPhrase();
            }

            @Override
            public void close() {
            }

            @Override
            public InputStream getBody() throws IOException {
            	log.info("fallback body");
            	String s = JsonResult.err().msg("后台服务错误").toString();
                return new ByteArrayInputStream(s.getBytes("UTF-8"));
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}