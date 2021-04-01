package com.zcw.cmall.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zcw.cmall.user.dao.MemberLevelDao;
import com.zcw.cmall.user.entity.MemberLevelEntity;
import com.zcw.cmall.user.exception.PhoneExsitException;
import com.zcw.cmall.user.exception.UsernameExsitException;
import com.zcw.cmall.user.vo.MemberLoginVo;
import com.zcw.cmall.user.vo.MemberRegistVo;
import com.zcw.cmall.user.vo.SocialUser;
import com.zcw.common.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.Query;

import com.zcw.cmall.user.dao.MemberDao;
import com.zcw.cmall.user.entity.MemberEntity;
import com.zcw.cmall.user.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 注册
     * @param vo
     */
    @Override
    public void register(MemberRegistVo vo) {
        MemberEntity memberEntity = new MemberEntity();
        //设置默认会员等级
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(levelEntity.getId());

        //设置vo中有的信息
        //要检查用户名和手机号是否唯一
        chechPhoneUnique(vo.getPhone());
        chechUsernameUnique(vo.getUsername());
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUsername());
        memberEntity.setNickname(vo.getUsername());

        //密码加密存储
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);
        //其他默认信息

        this.baseMapper.insert(memberEntity);
    }

    /**
     * 检查手机号是否唯一
     * @param
     * @return
     */
    @Override
    public void chechPhoneUnique(String phone) throws PhoneExsitException {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));

        if(count>0){
            throw new PhoneExsitException();
        }
    }

    /**
     * 检查用户名的唯一性
     * @param username
     * @throws UsernameExsitException
     */
    @Override
    public void chechUsernameUnique(String username) throws UsernameExsitException {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));

        if(count>0){
            throw new UsernameExsitException();
        }
    }

    /**
     * 用户名密码登录
     * @param vo
     * @return
     */
    @Override
    public MemberEntity login(MemberLoginVo vo) {

        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();

        MemberDao memberDao = this.baseMapper;
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct)
                .or().eq("mobile", loginacct));

        if(memberEntity == null){
            return null;
        }else{
            String entityPassword = memberEntity.getPassword();
            //解密 MD5 盐值加密
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean matches = bCryptPasswordEncoder.matches(password, entityPassword);

            if(matches){
                return memberEntity;
            }else{
                return null;
            }
        }
    }

    /**
     * 社交登录
     * @param socialUser
     * @return
     * @throws Exception
     */
    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("social_uid", socialUser.getUid()));

        if (memberEntity == null) {
            //1 如果之前未登陆过，则查询其社交信息进行注册
            MemberEntity register = new MemberEntity();
                try {
                    //查询微博开放信息所需的条件参数
                    Map<String, String> query = new HashMap<>();
                    query.put("access_token",socialUser.getAccess_token());
                    query.put("uid", socialUser.getUid());
                    //调用微博api接口获取用户信息
                    HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);
                    if (response.getStatusLine().getStatusCode() == 200){
                        String json = EntityUtils.toString(response.getEntity());
                        JSONObject jsonObject = JSON.parseObject(json);
                        //获得昵称，性别，头像
                        String name = jsonObject.getString("name");
                        String gender = jsonObject.getString("gender");
                        //.........
                        register.setNickname(name);
                        register.setGender("m".equals(gender)?1:0);
                    }
                }catch (Exception e){ }
            register.setSocialUid(socialUser.getUid());
            register.setAccessToken(socialUser.getAccess_token());
            register.setExpiresIn(socialUser.getExpires_in());

            this.baseMapper.insert(register);
            return register;
        }else {
            //这个用户已经注册
            //updata用来更新数据库中的用户信息
            MemberEntity updata = new MemberEntity();
            updata.setId(memberEntity.getId());
            updata.setAccessToken(socialUser.getAccess_token());
            updata.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.updateById(updata);

            //memberEntity用来返回
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        }
    }


}
