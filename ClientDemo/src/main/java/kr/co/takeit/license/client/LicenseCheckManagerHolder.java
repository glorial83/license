package kr.co.takeit.license.client;

import de.schlichtherle.license.LicenseParam;

/**
 * LicenseCheckManager 인스턴스
 *
 */
public class LicenseCheckManagerHolder {

    private static volatile LicenseCheckManager LICENSE_MANAGER;

    public static LicenseCheckManager getInstance(LicenseParam param){
        if(LICENSE_MANAGER == null){
            synchronized (LicenseCheckManagerHolder.class){
                if(LICENSE_MANAGER == null){
                    LICENSE_MANAGER = new LicenseCheckManager(param);
                }
            }
        }

        return LICENSE_MANAGER;
    }

}
