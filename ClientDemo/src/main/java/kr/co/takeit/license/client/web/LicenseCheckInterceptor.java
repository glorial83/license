package kr.co.takeit.license.client.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;

import kr.co.takeit.license.client.LicenseCheckUtil;

/**
 * LicenseCheckInterceptor
 *
 */
public class LicenseCheckInterceptor extends HandlerInterceptorAdapter{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LicenseCheckUtil licenseVerify = new LicenseCheckUtil();

        boolean verifyResult = licenseVerify.verify();

        if(verifyResult){
            return true;
        }else{
            response.setCharacterEncoding("utf-8");
            Map<String,String> result = new HashMap<>(1);
            result.put("result", "라이센스가 유효하지 않습니다.");

            response.getWriter().write(JSON.toJSONString(result));

            return false;
        }
    }

}
