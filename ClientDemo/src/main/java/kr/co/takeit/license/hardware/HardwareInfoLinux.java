package kr.co.takeit.license.hardware;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

/**
 * Linux용 하드웨어 정보 조회
 *
 */
public class HardwareInfoLinux extends HardwareInfo{

    @Override
    protected List<String> getIpAddress() throws Exception {
        List<String> result = null;

        //모든 네트워크 인터페이스를 가져옵니다
        List<InetAddress> inetAddresses = getLocalAllInetAddress();

        if(inetAddresses != null && inetAddresses.size() > 0){
            result = inetAddresses.stream().map(InetAddress::getHostAddress).distinct().map(String::toLowerCase).collect(Collectors.toList());
        }

        return result;
    }

    @Override
    protected List<String> getMacAddress() throws Exception {
        List<String> result = null;

        //1. 모든 네트워크 인터페이스를 가져옵니다
        List<InetAddress> inetAddresses = getLocalAllInetAddress();

        if(inetAddresses != null && inetAddresses.size() > 0){
            //2. 모든 네트워크 인터페이스의 Mac 주소를 얻습니다
            result = inetAddresses.stream().map(this::getMacByInetAddress).distinct().collect(Collectors.toList());
        }

        return result;
    }

    @Override
    protected String getCPUSerial() throws Exception {
        //일련 번호
        String serialNumber = "";

        //dmidecode 명령을 사용하여 CPU 일련 번호를 얻습니다
        String[] shell = {"/bin/bash","-c","dmidecode -t processor | grep 'ID' | awk -F ':' '{print $2}' | head -n 1"};
        Process process = Runtime.getRuntime().exec(shell);
        process.getOutputStream().close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = reader.readLine().trim();
        if(StringUtils.hasText(line)){
            serialNumber = line;
        }

        reader.close();
        return serialNumber;
    }

    @Override
    protected String getMainBoardSerial() throws Exception {
        //일련 번호
        String serialNumber = "";

        //dmidecode 명령을 사용하여 마더 보드 일련 번호를 얻습니다.
        String[] shell = {"/bin/bash","-c","dmidecode | grep 'Serial Number' | awk -F ':' '{print $2}' | head -n 1"};
        Process process = Runtime.getRuntime().exec(shell);
        process.getOutputStream().close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = reader.readLine().trim();
        if(StringUtils.hasText(line)){
            serialNumber = line;
        }

        reader.close();
        return serialNumber;
    }
}
