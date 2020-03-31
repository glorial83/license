package kr.co.takeit.license.hardware;

import java.io.Serializable;
import java.util.List;

/**
 * 하드웨어 정보용 VO
 *
 */
public class HardwareInfoParam implements Serializable{

    private static final long serialVersionUID = 8600137500316662317L;
    /**
     * 허용 가능한 IP 주소
     */
    private List<String> ipAddress;

    /**
     * 허용 된 MAC 주소
     */
    private List<String> macAddress;

    /**
     * 허용 가능한 CPU 일련 번호
     */
    private String cpuSerial;

    /**
     * 허용 마더 보드 일련 번호
     */
    private String mainBoardSerial;

    public List<String> getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(List<String> ipAddress) {
        this.ipAddress = ipAddress;
    }

    public List<String> getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(List<String> macAddress) {
        this.macAddress = macAddress;
    }

    public String getCpuSerial() {
        return cpuSerial;
    }

    public void setCpuSerial(String cpuSerial) {
        this.cpuSerial = cpuSerial;
    }

    public String getMainBoardSerial() {
        return mainBoardSerial;
    }

    public void setMainBoardSerial(String mainBoardSerial) {
        this.mainBoardSerial = mainBoardSerial;
    }

    @Override
    public String toString() {
        return "LicenseCheckModel{" +
                "ipAddress=" + ipAddress +
                ", macAddress=" + macAddress +
                ", cpuSerial='" + cpuSerial + '\'' +
                ", mainBoardSerial='" + mainBoardSerial + '\'' +
                '}';
    }
}
