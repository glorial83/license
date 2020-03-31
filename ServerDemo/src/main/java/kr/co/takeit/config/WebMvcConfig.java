package kr.co.takeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 관련 구성
 *
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 뷰 컨트롤러
     */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("index");
	}
}
