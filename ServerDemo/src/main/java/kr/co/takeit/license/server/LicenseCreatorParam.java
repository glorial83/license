package kr.co.takeit.license.server;

import com.fasterxml.jackson.annotation.JsonFormat;

import kr.co.takeit.license.hardware.HardwareInfoParam;

import java.io.Serializable;
import java.util.Date;

/**
 * License 생성 클래스에 필요한 매개 변수
 *
 */
public class LicenseCreatorParam implements Serializable {

    private static final long serialVersionUID = -7793154252684580872L;
    /**
     * 라이센스 subject
     */
    private String subject;

    /**
     * 키 별칭
     */
    private String privateAlias;

    /**
     * 키와 비밀번호
     */
    private String keyPass;

    /**
     * 키 저장소에 액세스하기위한 비밀번호
     */
    private String storePass;

    /**
     * 라이센스 생성 경로
     */
    private String licensePath;

    /**
     * 키 라이브러리 스토리지 경로
     */
    private String privateKeysStorePath;

    /**
     * 라이센스 유효 시간
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date issuedTime = new Date();

    /**
     * 라이센스 만료 시간
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date expiryTime;

    /**
     * 사용자 유형
     */
    private String consumerType = "user";

    /**
     * 사용자 수
     */
    private Integer consumerAmount = 1;

    /**
     * 설명
     */
    private String description = "";

    /**
     * 추가 서버 하드웨어 확인 정보
     */
    private HardwareInfoParam licenseCheckModel;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPrivateAlias() {
        return privateAlias;
    }

    public void setPrivateAlias(String privateAlias) {
        this.privateAlias = privateAlias;
    }

    public String getKeyPass() {
        return keyPass;
    }

    public void setKeyPass(String keyPass) {
        this.keyPass = keyPass;
    }

    public String getStorePass() {
        return storePass;
    }

    public void setStorePass(String storePass) {
        this.storePass = storePass;
    }

    public String getLicensePath() {
        return licensePath;
    }

    public void setLicensePath(String licensePath) {
        this.licensePath = licensePath;
    }

    public String getPrivateKeysStorePath() {
        return privateKeysStorePath;
    }

    public void setPrivateKeysStorePath(String privateKeysStorePath) {
        this.privateKeysStorePath = privateKeysStorePath;
    }

    public Date getIssuedTime() {
        return issuedTime;
    }

    public void setIssuedTime(Date issuedTime) {
        this.issuedTime = issuedTime;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(String consumerType) {
        this.consumerType = consumerType;
    }

    public Integer getConsumerAmount() {
        return consumerAmount;
    }

    public void setConsumerAmount(Integer consumerAmount) {
        this.consumerAmount = consumerAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HardwareInfoParam getLicenseCheckModel() {
        return licenseCheckModel;
    }

    public void setLicenseCheckModel(HardwareInfoParam licenseCheckModel) {
        this.licenseCheckModel = licenseCheckModel;
    }

    @Override
    public String toString() {
        return "LicenseCreatorParam{" +
                "subject='" + subject + '\'' +
                ", privateAlias='" + privateAlias + '\'' +
                ", keyPass='" + keyPass + '\'' +
                ", storePass='" + storePass + '\'' +
                ", licensePath='" + licensePath + '\'' +
                ", privateKeysStorePath='" + privateKeysStorePath + '\'' +
                ", issuedTime=" + issuedTime +
                ", expiryTime=" + expiryTime +
                ", consumerType='" + consumerType + '\'' +
                ", consumerAmount=" + consumerAmount +
                ", description='" + description + '\'' +
                ", licenseCheckModel=" + licenseCheckModel +
                '}';
    }
}
