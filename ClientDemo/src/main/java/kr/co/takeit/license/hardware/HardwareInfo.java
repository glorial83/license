package kr.co.takeit.license.hardware;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *  하드웨어 정보 조회
 *
 */
public abstract class HardwareInfo {
    private static Logger logger = LogManager.getLogger(HardwareInfo.class);

    /**
     * 추가 검증이 필요한 라이센스 매개 변수를 조립합니다
     *
     * @return HardwareInfoParam
     */
    public HardwareInfoParam getServerInfos(){
        HardwareInfoParam result = new HardwareInfoParam();

        try {
            result.setIpAddress(this.getIpAddress());
            //20191202 정해원 불필요 메소드 주석
            //result.setMacAddress(this.getMacAddress());
            //result.setCpuSerial(this.getCPUSerial());
            //result.setMainBoardSerial(this.getMainBoardSerial());
        }catch (Exception e){
            logger.error("하드웨어 정보 조회 오류", e);
        }

        return result;
    }

    /**
     * IP 주소 받기
     *
     * @return java.util.List<java.lang.String>
     */
    protected abstract List<String> getIpAddress() throws Exception;

    /**
     * 맥 주소 받기
     *
     * @return java.util.List<java.lang.String>
     */
    protected abstract List<String> getMacAddress() throws Exception;

    /**
     * CPU 일련 번호 받기
     *
     * @return java.lang.String
     */
    protected abstract String getCPUSerial() throws Exception;

    /**
     * 마더 보드 일련 번호 받기
     *
     * @return java.lang.String
     */
    protected abstract String getMainBoardSerial() throws Exception;

    /**
     * IP 조회
     *
     * @return java.util.List<java.net.InetAddress>
     */
    protected List<InetAddress> getLocalAllInetAddress() throws Exception {
        List<InetAddress> result = new ArrayList<>(4);

        // 모든 네트워크 인터페이스를 통과
        for (Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces.hasMoreElements(); ) {
            NetworkInterface iface = (NetworkInterface) networkInterfaces.nextElement();

            // 모든 인터페이스에서 IP 추출
            for (Enumeration inetAddresses = iface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                InetAddress inetAddr = (InetAddress) inetAddresses.nextElement();

                //LoopbackAddress、SiteLocalAddress, LinkLocalAddress、MulticastAddress 유형 IP 주소 제외
                if(		!inetAddr.isLoopbackAddress()
                	 /*&& !inetAddr.isSiteLocalAddress()*/
                	 && !inetAddr.isLinkLocalAddress()
                     && !inetAddr.isMulticastAddress()
                ){
                    result.add(inetAddr);
                }
            }
        }

        return result;
    }

    /**
     * 네트워크 인터페이스의 Mac 주소를 얻습니다
     *
     * @param InetAddress
     * @return String
     */
    protected String getMacByInetAddress(InetAddress inetAddr){
        try {
            byte[] mac = NetworkInterface.getByInetAddress(inetAddr).getHardwareAddress();
            StringBuffer stringBuffer = new StringBuffer();

            for(int i=0;i<mac.length;i++){
                if(i != 0) {
                    stringBuffer.append("-");
                }

                //16 진수 바이트를 문자열로 변환
                String temp = Integer.toHexString(mac[i] & 0xff);
                if(temp.length() == 1){
                    stringBuffer.append("0" + temp);
                }else{
                    stringBuffer.append(temp);
                }
            }

            return stringBuffer.toString().toUpperCase();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }

}
