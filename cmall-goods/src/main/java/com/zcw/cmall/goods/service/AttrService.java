package com.zcw.cmall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcw.cmall.goods.vo.AttrGroupRelationVo;
import com.zcw.cmall.goods.vo.AttrRespVo;
import com.zcw.cmall.goods.vo.AttrVo;
import com.zcw.common.utils.PageUtils;
import com.zcw.cmall.goods.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 15:27:36
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getAttrRelation(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo... vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /**
     *挑选出可以被检索的属性
     * @param ids
     * @return
     */
    List<Long> selectSearchAttrs(List<Long> ids);
}

