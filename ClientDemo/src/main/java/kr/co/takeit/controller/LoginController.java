package kr.co.takeit.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.takeit.license.hardware.HardwareInfo;
import kr.co.takeit.license.hardware.HardwareInfoLinux;
import kr.co.takeit.license.hardware.HardwareInfoParam;
import kr.co.takeit.license.hardware.HardwareInfoWindows;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 로그인
 *
 * @date 2018/7/9
 * @since 1.0.0
 */
@Controller
public class LoginController {

    /**
     * 로그인 인증 시뮬레이션
     *
     * @date 2018/7/9 17:09
     * @since 1.0.0
     * @param username 아이디
     * @param password 비밀번호
     * @return java.util.Map<java.lang.String,java.lang.Object>
     */
    @PostMapping("/check")
    @ResponseBody
    public Map<String,Object> test(@RequestParam(required = true) String username, @RequestParam(required = true) String password){
        Map<String,Object> result = new HashMap<>(1);
        System.out.println(MessageFormat.format("사용자 이름 : {0}, 암호 : {1}", username,password));

        //로그인 시뮬레이션
        System.out.println("로그인 프로세스 시뮬레이션");
        result.put("code",200);

        return result;
    }

    /**
     * 사용자 홈
     *
     * @date 2018/7/9 17:10
     * @since 1.0.0
     * @return java.lang.String
     */
    @RequestMapping("/userIndex")
    public String userIndex(){
        return "userIndex";
    }

    /**
     * 서버 하드웨어 정보 얻기
     *
     * @param osName
     * @return HardwareInfoParam
     */
    @RequestMapping(value = "/getServerInfos",produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @ResponseBody
    public HardwareInfoParam getServerInfos(@RequestParam(value = "osName",required = false) String osName) {
        //운영 체제 유형
        if(StringUtils.isBlank(osName)){
            osName = System.getProperty("os.name");
        }
        osName = osName.toLowerCase();

        HardwareInfo hardwareInfo = null;

        //운영 체제 유형에 따라 다른 데이터 수집 방법을 선택합니다
        if (osName.startsWith("windows")) {
            hardwareInfo = new HardwareInfoWindows();
        } else if (osName.startsWith("linux")) {
            hardwareInfo = new HardwareInfoLinux();
        }else{//다른 서버 유형
            hardwareInfo = new HardwareInfoLinux();
        }

        return hardwareInfo.getServerInfos();
    }
}
