package kr.co.takeit.license.client.web;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import de.schlichtherle.license.LicenseContent;
import kr.co.takeit.license.client.LicenseCheckParam;
import kr.co.takeit.license.client.LicenseCheckUtil;
import kr.co.takeit.util.TakeFileUtil;

/**
 * 프로젝트 시작시 라이센스 설치
 *
 */
@Component
public class LicenseCheckListener implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger logger = LogManager.getLogger(LicenseCheckListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //root application context 부모가 없습니다
        ApplicationContext context = event.getApplicationContext().getParent();
        if(context == null){
        	String licensePath = "D:\\projects\\java\\SeAH_201911\\ClientDemo\\src\\main\\resources\\license\\seah_system.lic.json";
            if(StringUtils.hasText(licensePath)){
                logger.info("++++++++ 라이센스 설치 ++++++++");

                LicenseCheckParam param;
				try {
					JSONObject json = (JSONObject)new JSONParser().parse(TakeFileUtil.readText(licensePath));
					String subject = (String)json.get("subject");
					String publicAlias = (String)json.get("publicKeyName");
					String storePass = (String)json.get("publicKeyPass");
					String publicKey = (String)json.get("publicKey");
					String license = (String)json.get("license");

					param = new LicenseCheckParam();
					param.setSubject(subject);
					param.setPublicAlias(publicAlias);
					param.setStorePass(storePass);
					param.setLicense(Base64Utils.decodeFromUrlSafeString(license));
					param.setPublicKey(Base64Utils.decodeFromUrlSafeString(publicKey));

					//라이센스 설치
	                LicenseCheckUtil licenseCheckUtil = new LicenseCheckUtil();
	                LicenseContent result = licenseCheckUtil.install(param);
	                if(result == null) {
	                	System.exit(-1);
	                } else {
	                	boolean verifyResult = licenseCheckUtil.verify();
	                	if(!verifyResult){
	                		System.exit(-1);
	                    }
	                }

				} catch (ParseException e) {
					logger.error("라이센스 로드 실패", e);
					System.exit(-1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error("라이센스 로드 실패", e);
					System.exit(-1);
				}

                logger.info("++++++++ 라이센스 설치 ++++++++");
            }
        }
    }
}
