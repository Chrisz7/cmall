package com.zcw.cmall.auth.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zcw.cmall.auth.server.feign.ThirdServerFeignService;
import com.zcw.cmall.auth.server.feign.UserFeignService;
import com.zcw.cmall.auth.server.vo.UserLoginVo;
import com.zcw.cmall.auth.server.vo.UserRegistVo;
import com.zcw.common.constant.AuthServerConstant;
import com.zcw.common.exception.ExceCodeEnum;
import com.zcw.common.utils.R;
import com.zcw.common.vo.MemberRespVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Chrisz
 * @date 2020/12/4 - 14:39
 */
@Controller
public class LoginController {

    @Autowired
    UserFeignService userFeignService;
    //操作Redis的客户端
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ThirdServerFeignService thirdServerFeignService;

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone){

        String s = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if(StringUtils.isNotEmpty(s)){
            //Long.parseLong 包装类型里面的方法 转成Long类型
            long l = Long.parseLong(s.split("_")[1]);
            //后台控制在60秒内，这个phone不能重复发
            if(System.currentTimeMillis() - l < 7000){
                //出现错误，直接 return 自定义的异常信息，
                return R.error(ExceCodeEnum.SMS_CODE_EXCEPTION.getCode(),ExceCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        //['1564', '15']_54554121ms
        String code = UUID.randomUUID().toString().substring(0,6);
        code = "['"+code+"',"+"'15']";
        String redisCode = code+"_"+System.currentTimeMillis();
        ValueOperations<String, String> sms = redisTemplate.opsForValue();
        sms.set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,redisCode,15, TimeUnit.MINUTES);

        thirdServerFeignService.sendCode(phone,code);
        return R.ok();
    }

    /**
     * 注册
     * @param vo
     * @param result
     * @param attributes
     * @return
     */
    @PostMapping("/register")
    public String register(@Valid UserRegistVo vo ,
                           BindingResult result ,
                           RedirectAttributes attributes){
        if (result.hasErrors()){

            Map<String ,String > errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,FieldError::getDefaultMessage));
            //model.addAttribute("errors",errors);
            //重定向携带数据,利用session原理，将数据放在session中，
            //只要跳到下一个页面取出这个数据后，session里面的数据就会删除
            //addFlashAttribute一闪而过的数据
            attributes.addFlashAttribute("errors",errors);
            //return "forward:register.html"; 转发默认是get方式的
            //直接渲染
            //return "register";
            //重定向,要完整路径
            return "redirect:http://auth.cmall.com/register.html";
        }
        else {
            //2.若JSR303校验通过
            //判断验证码是否正确
            String code = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX +" 86"+ vo.getPhone());
            //2.1 如果对应手机的验证码不为空且与提交上的相等-》验证码正确  //['1564', '15']_54554121ms
            String rediscode = vo.getCode();
            rediscode = "['"+rediscode+"','15']";
            if (!StringUtils.isEmpty(code) && rediscode.equals(code.split("_")[0])) {
                //2.1.1 使得验证后的验证码失效
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX +" 86"+ vo.getPhone());

                //2.1.2 远程调用会员服务注册
                R r = userFeignService.register(vo);
                if (r.getCode() == 0) {
                    //路径重定向
                    return "redirect:/login.html";
                    //调用成功，重定向登录页
                    //return "redirect:http://auth.gulimall.com/login.html";
                }else {
                    //调用失败，返回注册页并显示错误信息
                    Map<String ,String> errors = new HashMap<>();
                    //errors.put("code", r.getData(new TypeReference<String>(){}));
                    errors.put("code", (String) r.get("msg"));
                    attributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.cmall.com/register.html";
                }
            }else {
                //2.2 验证码错误
                Map<String ,String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                attributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.cmall.com/register.html";
            }
        }


    }

    /**
     * 登陆过再去登录页自动跳转到首页
     * @param session
     * @return
     */
    @GetMapping("/login.html")
    public String loginPage(HttpSession session){

        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute==null){
            return "login";
        }else{
            return "redirect:http://cmall.com";
        }

    }


    /**
     * 处理登录页的登录请求
     * @param vo
     * @param redirectAttributes
     * @param session
     * @return
     */
    @PostMapping("/login")
    //这个地方不能使用@RequestBody，只有前端发的请求是json或xml。。，用@RequestBody能够接收数据
    //Content type 'application/x-www-form-urlencoded;charset=UTF-8' not supported
    //这要前端和我后端UserLoginVo格式一致，Spring就会自动转换接收
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes,
                        HttpSession session){

        R login = userFeignService.login(vo);
        if (login.getCode() == 0){
            String json = JSON.toJSONString(login.get("entity"));
            MemberRespVo memberRespVo = JSON.parseObject(json, new TypeReference<MemberRespVo>(){});

            session.setAttribute(AuthServerConstant.LOGIN_USER,memberRespVo);
            return "redirect:http://cmall.com";
        }else{
            Map<String ,String > errors = new HashMap<>();

            errors.put("msg", (String) login.get("msg"));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.cmall.com/login.html";
        }

    }







    /**
     * SpringMVC 提供的viewcontroller功能，将请求和页面映射过来，不需要再写下面这些空方法，来为了页面跳转
     */

    /**
     * 登录
     * @return
     */
//    @GetMapping("/login.html")
//    public String loginPage(){
//        return "login";
//    }
//
//
//    /**
//     * 注册
//     * @return
//     */
//    @GetMapping("/register.html")
//    public String registerPage(){
//        return "register";
//    }
}
