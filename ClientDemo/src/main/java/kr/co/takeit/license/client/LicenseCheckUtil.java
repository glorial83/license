package kr.co.takeit.license.client;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.DefaultCipherParam;
import de.schlichtherle.license.DefaultLicenseParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;
import kr.co.takeit.license.certificate.CertStoreParam;

/**
 * 라이센스 검증 클래스
 *
 * @date 2018/4/20
 * @since 1.0.0
 */
public class LicenseCheckUtil {

    private static Logger logger = LogManager.getLogger(LicenseCheckUtil.class);

    /**
     * 라이센스 라이센스 설치
     *
     * @date 2018/4/20 16:26
     * @since 1.0.0
     */
    public synchronized LicenseContent install(LicenseCheckParam param){
        LicenseContent result = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        //1. 라이센스 설치
        try{
        	LicenseCheckManager licenseManager = LicenseCheckManagerHolder.getInstance(this.initLicenseParam(param));
            licenseManager.uninstall();

            result = licenseManager.install(param.getLicense());
            logger.info(MessageFormat.format("라이센스 설치 성공：{0} - {1}",format.format(result.getNotBefore()),format.format(result.getNotAfter())));
        }catch (Exception e){
            logger.error("라이센스 설치 실패！",e);
        }

        return result;
    }

    /**
     * 라이센스 라이센스 확인
     *
     * @date 2018/4/20 16:26
     * @since 1.0.0
     * @return boolean
     */
    public boolean verify(){
        LicenseManager licenseManager = LicenseCheckManagerHolder.getInstance(null);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            LicenseContent licenseContent = licenseManager.verify();
            logger.info(MessageFormat.format("라이센스 확인 통과, 라이센스 유효 기간：{0} - {1}",format.format(licenseContent.getNotBefore()),format.format(licenseContent.getNotAfter())));
            return true;
        }catch (Exception e){
            logger.error("라이센스 확인에 실패！",e);
            return false;
        }
    }

    /**
     * 라이센스 생성 매개 변수 초기화
     *
     * @param param LicenseVerifyParam
     * @return de.schlichtherle.license.LicenseParam
     */
    private LicenseParam initLicenseParam(LicenseCheckParam param){
        Preferences preferences = Preferences.userNodeForPackage(LicenseCheckUtil.class);
        CipherParam cipherParam = new DefaultCipherParam(param.getStorePass());

        KeyStoreParam publicStoreParam = new CertStoreParam(LicenseCheckUtil.class
                ,param.getPublicKey()
                ,param.getPublicAlias()
                ,param.getStorePass()
                ,null);

        return new DefaultLicenseParam(param.getSubject()
                ,preferences
                ,publicStoreParam
                ,cipherParam);
    }

}
