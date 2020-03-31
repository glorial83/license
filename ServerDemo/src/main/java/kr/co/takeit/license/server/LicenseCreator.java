package kr.co.takeit.license.server;

import java.text.MessageFormat;
import java.util.prefs.Preferences;

import javax.security.auth.x500.X500Principal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.DefaultCipherParam;
import de.schlichtherle.license.DefaultLicenseParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseParam;
import kr.co.takeit.license.certificate.CertStoreParam;

/**
 * License 생성 클래스
 *
 */
public class LicenseCreator {
    private static Logger logger = LogManager.getLogger(LicenseCreator.class);
    private LicenseCreatorParam param;

    public LicenseCreator(LicenseCreatorParam param) {
        this.param = param;
    }

    /**
     * 라이센스 생성
     *
     * @return boolean
     */
    public boolean generateLicense(String licenseDn){
        try {
        	LicenseCreatorManager licenseManager = new LicenseCreatorManager(initLicenseParam());
            LicenseContent licenseContent = initLicenseContent(licenseDn);

            licenseManager.store(licenseContent,param.getLicensePath());

            return true;
        }catch (Exception e){
            logger.error(MessageFormat.format("라이센스 생성 실패：{0}",param),e);
            return false;
        }
    }

    /**
     * 라이센스 생성 매개 변수 초기화
     *
     * @return LicenseParam
     */
    private LicenseParam initLicenseParam(){
        Preferences preferences = Preferences.userNodeForPackage(LicenseCreator.class);

        //라이센스 내용을 암호화하기위한 키 설정
        CipherParam cipherParam = new DefaultCipherParam(param.getStorePass());

        KeyStoreParam privateStoreParam = new CertStoreParam(LicenseCreator.class
                ,param.getPrivateKeysStorePath()
                ,param.getPrivateAlias()
                ,param.getStorePass()
                ,param.getKeyPass());

        LicenseParam licenseParam = new DefaultLicenseParam(param.getSubject()
                ,preferences
                ,privateStoreParam
                ,cipherParam);

        return licenseParam;
    }

    /**
     * 라이센스 생성 텍스트 정보 설정
     *
     * @return de.schlichtherle.license.LicenseContent
     */
    private LicenseContent initLicenseContent(String licenseDn){

    	X500Principal DEFAULT_HOLDER_AND_ISSUER = new X500Principal(licenseDn);

        LicenseContent licenseContent = new LicenseContent();
        licenseContent.setHolder(DEFAULT_HOLDER_AND_ISSUER);
        licenseContent.setIssuer(DEFAULT_HOLDER_AND_ISSUER);

        licenseContent.setSubject(param.getSubject());
        licenseContent.setIssued(param.getIssuedTime());
        licenseContent.setNotBefore(param.getIssuedTime());
        licenseContent.setNotAfter(param.getExpiryTime());
        licenseContent.setConsumerType(param.getConsumerType());
        licenseContent.setConsumerAmount(param.getConsumerAmount());
        licenseContent.setInfo(param.getDescription());

        //확장 검증 서버 하드웨어 정보
        licenseContent.setExtra(param.getLicenseCheckModel());

        return licenseContent;
    }

}
