package com.zcw.cmall.thirdserver.component;

import com.zcw.cmall.thirdserver.util.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信服务组件
 * @author Chrisz
 * @date 2020/12/4 - 16:36
 */
//使用注解，可在配置文件中进行相关自定义节点的配置，要和@Data一起使用，才能得到配置文件的内容
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
@Data
//提取成组件，放到容器中@Component，可以直接@Autowired注入使用
@Component
public class SmsComponent {


    private String host;
    private String path;
    private String appcode;
    private String templateID;

    public void sendSmsCode(String mobile,String code){
//        String host = "https://intlsms.market.alicloudapi.com";
//        String path = "/comms/sms/sendmsgall";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode);
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("callbackUrl", "http://test.dev.esandcloud.com");
        bodys.put("channel", "0");
        bodys.put("mobile", mobile);
        bodys.put("templateID", templateID);
        bodys.put("templateParamSet", code);
        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
