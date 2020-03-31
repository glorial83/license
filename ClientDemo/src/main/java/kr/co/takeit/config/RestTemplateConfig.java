package kr.co.takeit.config;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class RestTemplateConfig {

	/**
	 * RestTemplate로 돌아 가기
	 * @param factory ClientHttpRequestFactory
	 * @return RestTemplate
	 */
	@Bean
	public RestTemplate restTemplate(ClientHttpRequestFactory factory){
		return new RestTemplate(factory);
	}

	/**
	 * ClientHttpRequestFactory 인터페이스의 첫 번째 구현은 다음과 같습니다.
	 * SimpleClientHttpRequestFactory : 하위 계층은 java.net 패키지에서 제공하는 메소드를 사용하여 HTTP 연결 요청을 작성합니다.
	 * @return
	 */
//	@Bean
//	public SimpleClientHttpRequestFactory simpleClientHttpRequestFactory(){
//		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//
//		requestFactory.setReadTimeout(5000);
//		requestFactory.setConnectTimeout(5000);
//
//		return requestFactory;
//	}

	/**
	 * ClientHttpRequestFactory 인터페이스의 또 다른 구현 (권장) :
	 HttpComponentsClientHttpRequestFactory : 맨 아래 계층은 Httpclient 연결 풀을 사용하여 Http 연결 요청을 작성합니다.
	 * @return HttpComponentsClientHttpRequestFactory
	 */
	@Bean
	public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory(){
		//Httpclient 연결 풀, 긴 연결은 30 초 동안 유지됩니다
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(30, TimeUnit.SECONDS);

		//총 연결 수 설정
		connectionManager.setMaxTotal(1000);
		//같은 경로에 대한 동시성 수를 설정합니다
		connectionManager.setDefaultMaxPerRoute(1000);

		//헤더 설정
		List<Header> headers = new ArrayList<Header>();
		headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.04"));
		headers.add(new BasicHeader("Accept-Encoding", "gzip, deflate"));
		headers.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3"));
		headers.add(new BasicHeader("Connection", "keep-alive"));

		//HttpClient 만들기
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultHeaders(headers)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true)) //재시도 횟수를 설정
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy()) //세트는 긴 연결을 유지하기
                .build();

        //HttpComponentsClientHttpRequestFactory 인스턴스 생성
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        //클라이언트와 서버가 연결을 설정하기위한 시간 초과를 설정합니다
        requestFactory.setConnectTimeout(5000);
		//클라이언트가 서버에서 데이터를 읽는 시간 초과를 설정합니다.
        requestFactory.setReadTimeout(5000);
        //연결 풀에서 연결을 얻기위한 시간 초과를 설정합니다. 너무 길지 않아야합니다.
        requestFactory.setConnectionRequestTimeout(200);
        //버퍼 요청 데이터, 기본값은 true입니다. POST 또는 PUT을 통해 많은 양의 데이터를 보낼 때 메모리 부족을 피하기 위해 false로 변경하는 것이 좋습니다.
        requestFactory.setBufferRequestBody(false);

        return requestFactory;
	}

}
