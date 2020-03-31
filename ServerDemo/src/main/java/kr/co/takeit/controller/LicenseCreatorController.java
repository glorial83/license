package kr.co.takeit.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.takeit.license.server.LicenseCreator;
import kr.co.takeit.license.server.LicenseCreatorParam;
import kr.co.takeit.util.TakeFileUtil;

/**
 *
 * 라이센스 파일을 생성용 Controller
 *
 */
@RestController
@RequestMapping("/license")
public class LicenseCreatorController {

    /**
     * 인증서 DN
     */
    @Value("${license.dn}")
    private String licenseDn;

	/**
	 * 개인키경로
	 */
	@Value("${license.privateKeysStorePath}")
	private String privateKeysStorePath;

	/**
	 * 개인키 암호
	 */
	@Value("${license.keyPass}")
	private String keyPass;
	/**
	 * 개인키 별칭
	 */
	@Value("${license.privateAlias}")
	private String privateAlias;

	/**
	 * 공개키경로
	 */
	@Value("${license.publicKeysStorePath}")
	private String publicKeysStorePath;

	/**
	 * 공개키 암호
	 */
	@Value("${license.storePass}")
	private String storePass;

	/**
	 * 공개키 별칭
	 */
	@Value("${license.publicAlias}")
	private String publicAlias;

	/**
	 * 라이센스가 생성될 경로
	 */
	@Value("${license.licensePath}")
	private String licensePath;

    /**
     * 라이센스 생성
     *
     * @param param
     * 					{
							"subject": "license_demo",
							"issuedTime": "2018-07-10",
							"expiryTime": "2019-12-31",
							"description": "최초테스트",
							"licenseCheckModel": {
								"ipAddress": ["192.168.245.1", "10.0.5.22"],
								"macAddress": ["00-50-56-C0-00-01", "50-7B-9D-F9-18-41"]
							}
						}
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @RequestMapping(value = "/generateLicense",produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public Map<String,Object> generateLicense(@RequestBody(required = true) LicenseCreatorParam param) {

    	Map<String,Object> errorMap = new HashMap<>();
        errorMap.put("result","error");
        errorMap.put("msg","라이센스 파일 생성 실패!");

    	//#1. 서버용 라이센스 생성
        boolean result = systemLicense(param);
        if(!result) {
        	return errorMap;
        } else {
        	createJsonFile(param.getSubject(), licensePath + param.getSubject() + "_system.lic", publicAlias, storePass, publicKeysStorePath);
        }

        //#2. 로컬용 라이센스 생성
        result = userLicense(param);
        if(!result) {
        	return errorMap;
        } else {
        	createJsonFile(param.getSubject(), licensePath + param.getSubject() + "_user.lic", publicAlias, storePass, publicKeysStorePath);
        }

        //#3. 생성파일 링크JSON return
    	Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("result","ok");
        resultMap.put("msg", "라이센스 파일 생성 성공!");

        Map<String, String> localfileMap = new HashMap<String, String>();
        localfileMap.put("filename", param.getSubject() + "_user.lic.json");
        localfileMap.put("filedesc", "개발용(IP무제한)");

        Map<String, String> serverfileMap = new HashMap<String, String>();
        serverfileMap.put("filename", param.getSubject() + "_system.lic.json");
        serverfileMap.put("filedesc", "서버용(IP제한)");

        Map[] licenses = {localfileMap, serverfileMap};
        resultMap.put("licenses", licenses);

        return resultMap;
    }

    public boolean systemLicense(LicenseCreatorParam param) {
    	param.setLicensePath(licensePath + param.getSubject() + "_system.lic");
        param.setStorePass(storePass);
        param.setPrivateAlias(privateAlias);
        param.setKeyPass(keyPass);
        param.setPrivateKeysStorePath(privateKeysStorePath);

        LicenseCreator licenseCreator = new LicenseCreator(param);
        return licenseCreator.generateLicense(licenseDn);
    }

    public boolean userLicense(LicenseCreatorParam param) {
    	//20191202 정해원 개발용은 2개월 유효
    	Calendar localExpiryTime = Calendar.getInstance();
        localExpiryTime.setTime(param.getIssuedTime());
        localExpiryTime.add(Calendar.MONTH, 2);

    	param.setExpiryTime(localExpiryTime.getTime());
    	param.setLicensePath(licensePath + param.getSubject() + "_user.lic");
        param.setLicenseCheckModel(null);	//로컬용은 IP체크 안함

    	LicenseCreator licenseCreator = new LicenseCreator(param);
    	return licenseCreator.generateLicense(licenseDn);
    }

    @RequestMapping(value = "/downloadLicense")
    @ResponseBody
    public void downloadLicense(@RequestParam(required = true) String licenseFileName, HttpServletRequest req, HttpServletResponse res) throws Exception{
    	String licenseFile = licensePath + licenseFileName;

    	res.reset();
		File f = new File(licenseFile);
		long len = f.length();

		res.setHeader("Content-Type", "application/octet-stream");
		res.addHeader("content-disposition","attachment;filename=\""+licenseFileName+ "\"");
		res.addHeader("Content-Length", String.valueOf(len));

		try (ServletOutputStream os = res.getOutputStream(); FileInputStream fs = new FileInputStream(licenseFile)) {
			int no;
			byte[] buffer = new byte[65536];
			while ((no = fs.read(buffer)) > 0) {
				os.write(buffer, 0, no);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     *
     * 통합 라이센스 파일 만들기
     *
     * @param subject
     * @param licenseFilePath
     * @param publicKeyName
     * @param publicKeyPass
     * @param publicKeyPath
     * @return
     */
    public boolean createJsonFile(String subject, String licenseFilePath, String publicKeyName, String publicKeyPass, String publicKeyPath ) {
    	try {
			byte[] license = TakeFileUtil.readBinary(licenseFilePath);
			byte[] publicKey = TakeFileUtil.readBinary(publicKeyPath);

			Map<String, String> licenseMap = new HashMap<String, String>();
			licenseMap.put("publicKeyName"	, publicKeyName);
			licenseMap.put("publicKeyPass"	, publicKeyPass);
			licenseMap.put("publicKey"		, Base64Utils.encodeToUrlSafeString(publicKey));
			licenseMap.put("license"		, Base64Utils.encodeToUrlSafeString(license));
			licenseMap.put("subject"		, subject);

			String json = JSONObject.toJSONString(licenseMap);
			//20191212 정해원 json-simple 버그 처리
			json = StringUtils.replace(json, "\\", "\\\\");

			TakeFileUtil.writeText(licenseFilePath + ".json", json);

			return true;

		} catch (IOException e) {
			e.printStackTrace();
		}

    	return false;
    }
}
