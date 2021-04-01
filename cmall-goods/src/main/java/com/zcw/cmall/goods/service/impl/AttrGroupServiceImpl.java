package com.zcw.cmall.goods.service.impl;

import com.zcw.cmall.goods.entity.AttrEntity;
import com.zcw.cmall.goods.service.AttrService;
import com.zcw.cmall.goods.vo.AttrGroupWithAttrsVo;
import com.zcw.cmall.goods.vo.SkuItemVo;
import com.zcw.cmall.goods.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.Query;

import com.zcw.cmall.goods.dao.AttrGroupDao;
import com.zcw.cmall.goods.entity.AttrGroupEntity;
import com.zcw.cmall.goods.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrService attrService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String  key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> attrGroupEntityQueryWrapper = new QueryWrapper<AttrGroupEntity>();
        if(!StringUtils.isEmpty(key)){
            attrGroupEntityQueryWrapper.and ((obj)->{
                obj.eq("attr_group_id",key).or().like("attr_group_name",key).or().eq("catelog_id",key);
            });
        }
        if(catelogId == 0){
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    attrGroupEntityQueryWrapper);
            return new PageUtils(page);
        }
        else{
            attrGroupEntityQueryWrapper.eq("catelog_id",catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), attrGroupEntityQueryWrapper);
            return new PageUtils(page);
        }

    }

    /**
     * 查出所有的分组和分组关联的属性
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo>   getAttrgroupWithAttrsByCatelogId(Long catelogId) {
        List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<AttrGroupWithAttrsVo> attrsVos = groupEntities.stream().map(item -> {
            AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(item,vo);
            List<AttrEntity> attrEntities = attrService.getAttrRelation(vo.getAttrGroupId());
            vo.setAttrs(attrEntities);
            return vo;
        }).collect(Collectors.toList());
        return attrsVos;
    }

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {

        //this AttrGroupService
        List<SpuItemAttrGroupVo> vos = this.baseMapper.getAttrGroupWithAttrsBySpuId(spuId,catalogId);
        return vos;
    }

}
