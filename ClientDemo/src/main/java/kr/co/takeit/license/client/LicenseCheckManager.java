package kr.co.takeit.license.client;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseContentException;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseNotary;
import de.schlichtherle.license.LicenseParam;
import de.schlichtherle.xml.GenericCertificate;
import kr.co.takeit.license.hardware.HardwareInfo;
import kr.co.takeit.license.hardware.HardwareInfoLinux;
import kr.co.takeit.license.hardware.HardwareInfoParam;
import kr.co.takeit.license.hardware.HardwareInfoWindows;

/**
 * Client용 라이센스관리 클래스
 *  - 설치 / 검증을 담당
 *
 */
public class LicenseCheckManager extends LicenseManager {
    private static Logger LOGGER = LogManager.getLogger(LicenseCheckManager.class);

    //XML 인코딩
    private static final String XML_CHARSET = "UTF-8";

    //기본 BUFSIZE
    private static final int DEFAULT_BUFSIZE = 8 * 1024;

    public LicenseCheckManager() {

    }

    public LicenseCheckManager(LicenseParam param) {
        super(param);
    }

    /**
     * 라이센스 설치.
     *
     * @param licensePath
     * @return
     * @throws Exception
     */
    @Deprecated
    public synchronized LicenseContent install(String licensePath) throws Exception {
    	return install(loadLicenseKey(new File(licensePath)), getLicenseNotary());
    }

    /**
     * 라이센스 설치.
     *
     * @param licensePath
     * @return
     * @throws Exception
     */
    public synchronized LicenseContent install(byte[] key) throws Exception {
    	return install(key, getLicenseNotary());
    }

    /**
     * 라이센스 설치.
     *
     * @param
     * @return de.schlichtherle.license.LicenseContent
     */
    @Override
    protected synchronized LicenseContent install(final byte[] key, final LicenseNotary notary) throws Exception {
    	//인증서 로드
    	final GenericCertificate certificate = getPrivacyGuard().key2cert(key);

        //인증서 검증
        notary.verify(certificate);

        //라이센스 로드
        final LicenseContent content = (LicenseContent)this.load(certificate.getEncoded());

        //라이센스 추가 검증
        this.validate(content);

        //라이센스 설치
        setLicenseKey(key);
        setCertificate(certificate);

        return content;
    }

    /**
     * 라이센스 기본검증 + 추가검증
     *
     * @param content LicenseContent
     */
    @Override
    protected synchronized void validate(final LicenseContent content) throws LicenseContentException {
        //라이센스 기본검증
        super.validate(content);

        //라이센스 추가검증정보 로드
        HardwareInfoParam licenseHwInfo = (HardwareInfoParam) content.getExtra();

        //현재 서버정보 조회
        HardwareInfoParam serverHwInfo = this.getHardwareInfo();

        if(licenseHwInfo != null && serverHwInfo != null){
            //IP 주소 확인
            if(!checkIpAddress(licenseHwInfo.getIpAddress(), serverHwInfo.getIpAddress())){

            	StringBuffer sb = new StringBuffer();
            	sb.append("현재서버의 IP가 승인 된 범위 내에 있지 않습니다");
            	sb.append("\n");
            	sb.append("현재서버정보:");
            	sb.append(serverHwInfo.toString());
            	sb.append("\n");
            	sb.append("라이센스발급정보:");
            	sb.append(licenseHwInfo.toString());

                throw new LicenseContentException(sb.toString());
            }

            ////Mac 주소 확인
            //if(!checkIpAddress(licenseInfo.getMacAddress(),serverCheckModel.getMacAddress())){
            //    throw new LicenseContentException("서버의 현재 MAC 주소 인증의 범위 내에 있지");
            //}
            //
            ////메인 보드 일련 번호 확인
            //if(!checkSerial(licenseInfo.getMainBoardSerial(),serverCheckModel.getMainBoardSerial())){
            //    throw new LicenseContentException("현재 서버의 마더 보드 일련 번호가 승인 된 범위 내에 있지 않습니다");
            //}
            //
            ////CPU 일련 번호 확인
            //if(!checkSerial(licenseInfo.getCpuSerial(),serverCheckModel.getCpuSerial())){
            //    throw new LicenseContentException("서버의 현재 CPU 시리얼 번호는인가 된 범위 내에 있지 않은");
            //}
        }
        /*
        else{
            throw new LicenseContentException("서버 하드웨어 정보를 얻을 수 없습니다");
        }
        */
    }

    /**
     * XML을 구문 분석하기 위해 XMLDecoder를 다시 작성
     *
     * @date 2018/4/25 14:02
     * @since 1.0.0
     * @param encoded XML
     * @return java.lang.Object
     */
	private Object load(String encoded) {
		try (
			BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(encoded.getBytes(XML_CHARSET)));
			XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(inputStream, DEFAULT_BUFSIZE), null, null);
		){
			return decoder.readObject();
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("XMLDecoder 구문 분석 XML 실패", e);
		} catch (IOException e) {
			LOGGER.error("XMLDecoder 구문 분석 XML 실패", e);
		}

		return null;
	}

    /**
     * 서버 머신정보 조회(IP,Mac,CPU,M/B 등등)
     *
     * @return HardwareInfoParam
     */
    private HardwareInfoParam getHardwareInfo(){
        //운영 체제 유형
        String osName = System.getProperty("os.name").toLowerCase();
        HardwareInfo hardwareInfo = null;

        //운영 체제 유형에 따라 다른 데이터 수집 방법을 선택합니다
        if (osName.startsWith("windows")) {
            hardwareInfo = new HardwareInfoWindows();
        } else if (osName.startsWith("linux")) {
            hardwareInfo = new HardwareInfoLinux();
        }else{//다른 서버 유형
            hardwareInfo = new HardwareInfoLinux();
        }

        return hardwareInfo.getServerInfos();
    }

    /**
     * IP 또는 Mac 정보 비교
     *
     * @param expectedList
     * @param serverList
     * @return boolean
     */
    private boolean checkIpAddress(List<String> expectedList,List<String> serverList){
		if (expectedList != null && expectedList.size() > 0) {
			if (serverList != null && serverList.size() > 0) {
				for (String expected : expectedList) {
					if (serverList.contains(expected.trim())) {
						return true;
					}
				}
			}

			return false;
		} else {
			return true;
		}
    }

    /**
     * Cpu, M/B Serial 비교
     *
     * @param expectedSerial
     * @param serverSerial
     * @return boolean
     */
    private boolean checkSerial(String expectedSerial,String serverSerial){
        if(StringUtils.hasText(expectedSerial)){
            if(StringUtils.hasText(serverSerial)){
                if(expectedSerial.equals(serverSerial)){
                    return true;
                }
            }

            return false;
        }else{
            return true;
        }
    }

}
