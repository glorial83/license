package kr.co.takeit.license.client;

/**
 * 라이센스 검증 클래스에 필요한 매개 변수
 *
 *
 * @date 2018/4/20
 * @since 1.0.0
 */
public class LicenseCheckParam {

    /**
     * 라이센스 subject
     */
    private String subject;

    /**
     * 공개 키 별명
     */
    private String publicAlias;

    /**
     * 공개 키 저장소에 액세스하기위한 비밀번호
     */
    private String storePass;

	/**
	 * 라이센스
	 */
	private byte[] license;

    /**
     * 공개 키
     */
    private byte[] publicKey;

	public LicenseCheckParam() {
    }

    public LicenseCheckParam(String subject, String publicAlias, String storePass, byte[] license, byte[] publicKey) {
        this.subject = subject;
        this.publicAlias = publicAlias;
        this.storePass = storePass;
        this.license = license;
        this.publicKey = publicKey;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPublicAlias() {
        return publicAlias;
    }

    public void setPublicAlias(String publicAlias) {
        this.publicAlias = publicAlias;
    }

    public String getStorePass() {
        return storePass;
    }

    public void setStorePass(String storePass) {
        this.storePass = storePass;
    }

    public byte[] getLicense() {
		return license;
	}

	public void setLicense(byte[] license) {
		this.license = license;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}

    @Override
    public String toString() {
        return "LicenseVerifyParam{" +
                "subject='" + subject + '\'' +
                ", publicAlias='" + publicAlias + '\'' +
                ", storePass='" + storePass + '\'' +
                '}';
    }
}
