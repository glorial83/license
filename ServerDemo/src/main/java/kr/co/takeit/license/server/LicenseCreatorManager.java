package kr.co.takeit.license.server;

import java.io.File;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseContentException;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseNotary;
import de.schlichtherle.license.LicenseParam;
import de.schlichtherle.xml.GenericCertificate;

/**
 * 라이센스 생성 클래스
 *
 */
public class LicenseCreatorManager extends LicenseManager{

    private static Logger LOGGER = LogManager.getLogger(LicenseCreatorManager.class);

    public LicenseCreatorManager() {

    }

    public LicenseCreatorManager(LicenseParam param) {
        super(param);
    }

    /**
     * 라이센스 생성
     * ---> 내부에서 create가 호출된다
     *
     * @param content
     * @param licensePath
     * @throws Exception
     */
    public synchronized void store(LicenseContent content, String licensePath) throws Exception{
    	super.store(content, new File(licensePath));
    }

    /**
     * 라이센스 생성
     *
     * @return byte[]
     */
    @Override
    protected synchronized byte[] create(LicenseContent content, LicenseNotary notary) throws Exception {
        initialize(content);
        this.validateCreate(content);
        final GenericCertificate certificate = notary.sign(content);
        return getPrivacyGuard().cert2key(certificate);
    }

    /**
     * 생성 된 라이센스의 매개 변수 정보를 확인하십시오.
     *
     * @param LicenseContent 라이센스 정보
     */
    protected synchronized void validateCreate(final LicenseContent content) throws LicenseContentException {
        final LicenseParam param = getLicenseParam();

        final Date now = new Date();
        final Date notBefore = content.getNotBefore();
        final Date notAfter = content.getNotAfter();
        if (null != notAfter && now.after(notAfter)){
            throw new LicenseContentException("이미 지난 만료일입니다");
        }
        if (null != notBefore && null != notAfter && notAfter.before(notBefore)){
            throw new LicenseContentException("발급일자가 만료일자 이전입니다.");
        }
        final String consumerType = content.getConsumerType();
        if (null == consumerType){
            throw new LicenseContentException("사용자 유형은 null 일 수 없습니다\"");
        }
    }
}
