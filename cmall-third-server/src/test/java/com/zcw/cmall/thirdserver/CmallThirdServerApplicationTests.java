package com.zcw.cmall.thirdserver;

import com.zcw.cmall.thirdserver.component.SmsComponent;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CmallThirdServerApplicationTests {


    @Autowired
    SmsComponent smsComponent;

    @Test
    public void testSms(){
        smsComponent.sendSmsCode("+8619137933079","['1564', '15']");
    }
    @Test
    void contextLoads() {
    }

}
