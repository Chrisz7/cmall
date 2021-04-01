package com.zcw.cmall.goods.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zcw.cmall.goods.entity.AttrEntity;
import com.zcw.cmall.goods.service.AttrAttrgroupRelationService;
import com.zcw.cmall.goods.service.AttrService;
import com.zcw.cmall.goods.service.CategoryService;
import com.zcw.cmall.goods.vo.AttrGroupRelationVo;
import com.zcw.cmall.goods.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zcw.cmall.goods.entity.AttrGroupEntity;
import com.zcw.cmall.goods.service.AttrGroupService;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.R;



/**
 * 属性分组
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 17:07:59
 */
@RestController
@RequestMapping("goods/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private AttrAttrgroupRelationService relationService;

    //17、获取分类下所有分组&关联属性
    ///product/attrgroup/{catelogId}/withattr
    @GetMapping("/{catelogId}/withattr")
    public R getAttrgroupWithAttrs(@PathVariable("catelogId")Long catelogId){
        List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrgroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data",vos);
    }
    ///product/attrgroup/attr/relation
    //请求参数是一个数组[{
    //  "attrGroupId": 0, //分组id
    //  "attrId": 0, //属性id
    //}]  使用RequestBody 将json封装成 AttrGroupRelationVo 数组、List
    @PostMapping("/attr/relation")
    public R addAttrRelation(@RequestBody List<AttrGroupRelationVo> vos){
        relationService.saveBatch(vos);
        return R.ok();
    }

    //删除属性与分组的关联关系/product/attrgroup/attr/relation/delete
    //[{"attrId":1,"attrGroupId":2}] 传过来的是数组
    @PostMapping("/attr/relation/delete")
    public R attrRelationDelete(@RequestBody AttrGroupRelationVo[] vos){
        attrService.deleteRelation(vos);
        return R.ok();
    }
    /**
     * 获取属性分组的关联的所有属性
     * @param attrgroupId
     * @return
     */
    ///product/attrgroup/{attrgroupId}/attr/relation
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId")Long attrgroupId){
        List<AttrEntity> entities =  attrService.getAttrRelation(attrgroupId);
        return R.ok().put("data",entities);
    }
    /**
     * 获取属性分组没有关联的其他属性
     * @param attrgroupId 要新增关联的属性分组的id
     * @param params 分页所有参数信息
     * @return
     */
    ///product/attrgroup/{attrgroupId}/noattr/relation
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R noattrRelation(@PathVariable("attrgroupId") Long attrgroupId,
                            @RequestParam Map<String, Object> params){
        PageUtils page = attrService.getNoRelationAttr(params,attrgroupId);
        return R.ok().put("page",page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("goods:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId")Long catelogId){
        //PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("goods:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        Long catelogId = attrGroup.getCatelogId();
        Long[] catelogIds = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(catelogIds);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("goods:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("goods:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("goods:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
