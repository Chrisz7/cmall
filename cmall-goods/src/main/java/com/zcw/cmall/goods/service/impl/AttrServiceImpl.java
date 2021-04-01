package com.zcw.cmall.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zcw.cmall.goods.dao.AttrAttrgroupRelationDao;
import com.zcw.cmall.goods.dao.AttrGroupDao;
import com.zcw.cmall.goods.dao.CategoryDao;
import com.zcw.cmall.goods.entity.AttrAttrgroupRelationEntity;
import com.zcw.cmall.goods.entity.AttrGroupEntity;
import com.zcw.cmall.goods.entity.CategoryEntity;
import com.zcw.cmall.goods.service.CategoryService;
import com.zcw.cmall.goods.vo.AttrGroupRelationVo;
import com.zcw.cmall.goods.vo.AttrRespVo;
import com.zcw.cmall.goods.vo.AttrVo;
import com.zcw.common.constant.GoodsConstant;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.Query;

import com.zcw.cmall.goods.dao.AttrDao;
import com.zcw.cmall.goods.entity.AttrEntity;
import com.zcw.cmall.goods.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    CategoryService categoryService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //分页方法
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        //将从页面来的值VO复制到PO里面
        BeanUtils.copyProperties(attr,attrEntity);
        //1.保存基本数据
        this.save(attrEntity);
        //2.保存关联关系
        if(attr.getAttrType() == GoodsConstant.AttrEnum.ATTR_TYPR_BASE.getCode() && attr.getAttrGroupId()!=null){
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }

    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type","base".equalsIgnoreCase(attrType)?GoodsConstant.AttrEnum.ATTR_TYPR_BASE.getCode():GoodsConstant.AttrEnum.ATTR_SALE.getCode());
        if(catelogId != 0){                                                                                 //无论大小写
            queryWrapper.eq("catelog_id",catelogId);
        }
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("attr_id",key).or().like("attr_name",key);
            });
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        //得到获取到的记录,是AttrEntity的List
        List<AttrEntity> attrEntityList = page.getRecords();
        List<AttrRespVo> baseListVos = attrEntityList.stream().map((attrEntity) -> {
            AttrRespVo baseListVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, baseListVo);

            Long id = attrEntity.getCatelogId();
            CategoryEntity categoryEntity = categoryDao.selectById(id);
            if (categoryEntity != null) {
                String entityName = categoryEntity.getName();
                baseListVo.setCatelogName(entityName);
            }

            if("base".equalsIgnoreCase(attrType)){
                Long attrId = attrEntity.getAttrId();
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectById(attrId);
                if (attrAttrgroupRelationEntity != null && attrAttrgroupRelationEntity.getAttrGroupId()!=null) {
                    Long attrGroupId = attrAttrgroupRelationEntity.getAttrGroupId();
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);

                    baseListVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
//            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
//            if(relationEntity != null){
//                Long groupId = relationEntity.getAttrGroupId();
//                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(groupId);
//                baseListVo.setGroupName(attrGroupEntity.getAttrGroupName());
//            }
            }

            return baseListVo;
        }).collect(Collectors.toList());
        pageUtils.setList(baseListVos);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo attrRespVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity,attrRespVo);

//        attrRespVo.setCatelogPath();
//        attrRespVo.setAttrGroupId();

        AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
        if(relationEntity != null){
            attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
            AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
            if(attrGroupEntity != null){
                attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
        }

        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrRespVo.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if( categoryEntity != null){
            attrRespVo.setCatelogName(categoryEntity.getName());
        }
        return attrRespVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);

        if(attrEntity.getAttrType() == GoodsConstant.AttrEnum.ATTR_TYPR_BASE.getCode()){
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());
            Integer count = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if(count>0){
                attrAttrgroupRelationDao.update(relationEntity,new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
            }
            else{
                attrAttrgroupRelationDao.insert(relationEntity);
            }
        }

    }

    /**
     * 根据分组id查找关联的所有基本属性
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getAttrRelation(Long attrgroupId) {

        List<AttrAttrgroupRelationEntity> entities = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        List<Long> ids = entities.stream().map((entity) -> {
            return entity.getAttrId();
        }).collect(Collectors.toList());
        if(ids == null || ids.size()==0){
            return  null;
        }
        //要判断下参数的非空
        List<AttrEntity> attrEntities = this.listByIds(ids);
        return attrEntities;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo... vos) {
        List<AttrAttrgroupRelationEntity> relationEntities = Arrays.asList(vos).stream().map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationDao.deleteBatchRelation(relationEntities);
    }

    /**
     * 获取当前分组没有关联的所有属性
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //当前属性分组只能关联自己所属的分类里面的属性（规格参数、销售属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //当前属性分组只能关联别的分组没有引用的属性（规格参数、销售属性
        ///当前分类下的其他分组
        //条件不在这里面.ne("attr_group_id",attrgroupId)
        List<AttrGroupEntity> group = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> otherGroupIds = group.stream().map(item -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());
        ///这些分组关联的属性
        List<AttrAttrgroupRelationEntity> entities = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", otherGroupIds));
        List<Long> ids = entities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        ///移除这些属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type",GoodsConstant.AttrEnum.ATTR_TYPR_BASE.getCode());
        if(ids!=null && ids.size()>0){
            wrapper.notIn("attr_id",ids);
        }
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w -> {
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }

    /**
     * 挑选出可以被检索的属性
     * @param ids
     * @return
     */
    @Override
    public List<Long> selectSearchAttrs(List<Long> ids) {
        return this.baseMapper.selectSearchAttrs(ids);
    }

}
