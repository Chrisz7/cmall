package com.zcw.cmall.goods.app;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zcw.cmall.goods.entity.CategoryEntity;
import com.zcw.cmall.goods.service.CategoryService;
import com.zcw.common.utils.R;



/**
 * 商品三级分类
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 17:07:59
 */
@RestController
@RequestMapping("goods/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查出所有分类以及子分类，用树形结构组装起来
     */
    @RequestMapping("/list/tree")
    public R list(){

        List<CategoryEntity> categoryEntities = categoryService.listWithTree();
        //public class R extends HashMap<String, Object>
        return R.ok().put("data", categoryEntities);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("goods:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("goods:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改单个
     */
    @RequestMapping("/update")
    //@RequiresPermissions("goods:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateCascade(category);

        return R.ok();
    }

    /**
     * 修改数组
     */
    @RequestMapping("/update/sort")
    //@RequiresPermissions("goods:category:update")
    public R update(@RequestBody CategoryEntity[] category){
        //数组转成集合
        categoryService.updateBatchById(Arrays.asList(category));

        return R.ok();
    }

    /**
     * 删除
     * @RequestBody： 获取请求体,必须发送POST请求
     * SpringMVC会自动将请求体的数据（json），转为对应的对象（这里为Long类型的数组
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("goods:category:delete")
    public R delete(@RequestBody Long[] catIds){
		//categoryService.removeByIds(Arrays.asList(catIds));

		categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
