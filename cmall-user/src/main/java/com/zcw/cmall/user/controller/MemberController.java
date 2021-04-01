package com.zcw.cmall.user.controller;

import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.zcw.cmall.user.exception.PhoneExsitException;
import com.zcw.cmall.user.exception.UsernameExsitException;
import com.zcw.cmall.user.vo.MemberLoginVo;
import com.zcw.cmall.user.vo.MemberRegistVo;
import com.zcw.cmall.user.vo.SocialUser;
import com.zcw.common.exception.ExceCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zcw.cmall.user.entity.MemberEntity;
import com.zcw.cmall.user.service.MemberService;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.R;



/**
 * 会员
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 21:18:22
 */
@RestController
@RequestMapping("user/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 注册
     * @return
     */
    @PostMapping("/register")
    public R register(@RequestBody MemberRegistVo vo){

        try{
            memberService.register(vo);
        }catch (PhoneExsitException e){
            return R.error(ExceCodeEnum.PHONE_EXIST_EXCEPTION.getCode(),ExceCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
        }catch (UsernameExsitException e){
            return R.error(ExceCodeEnum.USERNAME_EXIST_EXCEPTION.getCode(),ExceCodeEnum.USERNAME_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    /**
     * 登录
     * @param vo
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo){

        //vo登录的用户名和密码
        MemberEntity entity = memberService.login(vo);
        if (entity != null){
            return R.ok().put("entity",entity);
        }else{
            return R.error(ExceCodeEnum.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getCode(),ExceCodeEnum.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getMsg());
        }

    }

    /**
     * 社交登录
     * @param
     * @return
     */
    @PostMapping("/oauth2/login")
    public R oauthlogin(@RequestBody SocialUser socialUser)throws Exception{

        MemberEntity entity = memberService.login(socialUser);
        if (entity != null){
            return R.ok().put("memberEntity",entity);
        }else{
            return R.error(ExceCodeEnum.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getCode(),ExceCodeEnum.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getMsg());
        }

    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("user:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("user:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("user:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("user:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("user:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
