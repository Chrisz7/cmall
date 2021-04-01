package com.zcw.cmall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcw.cmall.user.exception.PhoneExsitException;
import com.zcw.cmall.user.exception.UsernameExsitException;
import com.zcw.cmall.user.vo.MemberLoginVo;
import com.zcw.cmall.user.vo.MemberRegistVo;
import com.zcw.cmall.user.vo.SocialUser;
import com.zcw.common.utils.PageUtils;
import com.zcw.cmall.user.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 21:18:22
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 注册
     * @param vo
     */
    void register(MemberRegistVo vo);

    /**
     * 检查是否唯一
     * @param username phone
     * @return
     */
    void chechPhoneUnique(String phone) throws PhoneExsitException;

    void chechUsernameUnique(String username) throws UsernameExsitException;

    /**
     * 登录
     * @param vo
     * @return
     */
    MemberEntity login(MemberLoginVo vo);

    /**
     * 社交登录
     * @param socialUser
     * @return
     */
    MemberEntity login(SocialUser socialUser) throws Exception;
}

