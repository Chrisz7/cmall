package com.zcw.cmall.goods.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zcw.cmall.goods.entity.BrandEntity;
import com.zcw.cmall.goods.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zcw.cmall.goods.entity.CategoryBrandRelationEntity;
import com.zcw.cmall.goods.service.CategoryBrandRelationService;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 17:07:59
 */
@RestController
@RequestMapping("goods/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    ///product/categorybrandrelation/brands/list
    @GetMapping("/brands/list")
    public R brandList(@RequestParam(value = "catId",required = true)Long catId){
        List<BrandEntity> brandEntities = categoryBrandRelationService.getBrandsByCatId(catId);
        List<BrandVo> brandVoList = brandEntities.stream().map(item -> {
            BrandVo brandVo = new BrandVo();
//            BeanUtils.copyProperties(item, brandVo);
            brandVo.setBrandName(item.getName());
            brandVo.setBrandId(item.getBrandId());
            return brandVo;
        }).collect(Collectors.toList());
        return R.ok().put("data",brandVoList);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("goods:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 关联当前品牌关联的所有分类
     */
    //@RequestMapping(value = "/catelog/list",method = RequestMethod.GET)
    @GetMapping("/catelog/list")
    //@RequiresPermissions("goods:categorybrandrelation:list")
    //@RequestParam("brandId")标注作用
    public R catelogList(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id",brandId));

        return R.ok().put("data", data);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("goods:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("goods:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("goods:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("goods:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
