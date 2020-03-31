package kr.co.takeit.license.certificate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import de.schlichtherle.license.AbstractKeyStoreParam;

/**
 * 인증서관련 VO
 */
public class CertStoreParam extends AbstractKeyStoreParam {

    private byte[] storeKey;
    private String alias;
    private String storePwd;
    private String keyPwd;

    public CertStoreParam(Class clazz, byte[] resource, String alias, String storePwd, String keyPwd) {
        super(clazz, "");
        this.storeKey = resource;
        this.alias = alias;
        this.storePwd = storePwd;
        this.keyPwd = keyPwd;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public String getStorePwd() {
        return storePwd;
    }

    @Override
    public String getKeyPwd() {
        return keyPwd;
    }

    /**
     * 공개 키 및 개인 키 Stream
     * @return java.io.ByteArrayInputStream
     */
    @Override
    public InputStream getStream() throws IOException {
    	return new ByteArrayInputStream(storeKey);
    }
}
