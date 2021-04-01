package com.zcw.cmall.thirdserver.controller;

import com.zcw.cmall.thirdserver.component.SmsComponent;
import com.zcw.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信服务
 * @author Chrisz
 * @date 2020/12/4 - 17:18
 */
@RestController
@RequestMapping("sms")
public class SmsSendController {

    @Autowired
    SmsComponent smsComponent;


    /**
     * 给别的后台服务调用
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code){

        smsComponent.sendSmsCode(phone,code);
        return R.ok();
    }
}
