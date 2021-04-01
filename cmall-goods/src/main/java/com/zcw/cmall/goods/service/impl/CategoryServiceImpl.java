package com.zcw.cmall.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zcw.cmall.goods.service.CategoryBrandRelationService;
import com.zcw.cmall.goods.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.Query;

import com.zcw.cmall.goods.dao.CategoryDao;
import com.zcw.cmall.goods.entity.CategoryEntity;
import com.zcw.cmall.goods.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
//CategoryDao继承了BaseMapper
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    RedissonClient redisson;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    //可以使用这样的方式，另一种方式，因为继承了ServiceImpl，而这里面有baseMapper，就是泛型指定的CategoryDao
//    @Autowired
//    CategoryDao categoryDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    //显示也是逻辑显示
    @Override
    public List<CategoryEntity> listWithTree() {

        //可以使用这样的方式，另一种方式，因为继承了ServiceImpl，而这里面有baseMapper，就是泛型指定的CategoryDao
//    @Autowired
//    CategoryDao categoryDao;
        //查所有分类                                                   ，括号中是查询条件  where 之后的
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        //组装成树形结构
        //使用stream流简化操作，
        // 使用filter过滤器，过滤ParentCid() == 0，再用collect(Collectors.toList())收集起来
        //使用map映射再对父级分类的子分类操作，设置它的子分类，getChilds 中menu是父级分类，再传一个所有的分类集合
        //使用sorted排序，{}中是排序规则
        List<CategoryEntity> menusLevel1 = categoryEntities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map(menu -> {
            menu.setChild(getChilds(menu,categoryEntities));
            return menu;
        }).sorted((menu1,menu2) ->{
            //排序的規則
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return menusLevel1;
    }
    /**
     * 得到子分类
     * @param root 父级分类
     * @param all  所有的分类集合
     * @return 返回该分类的所有子分类
     *
     * 对所有的分类的集合进行stream流的操作
     * filter过滤所有分类中的ParentCid是当前root分类的Id的，在收集起来
     * 使用map在对该父级分类的子分类操作，设置它的子分类，getChilds 中menu是父级分类，再传一个所有的分类集合
     * 使用sorted排序，{}中是排序规则
     */
    private List<CategoryEntity> getChilds(CategoryEntity root,List<CategoryEntity> all){

        List<CategoryEntity> child = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            categoryEntity.setChild(getChilds(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return child;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查是否被别的地方引用

        //批量删除  只把显示状态改为了0，使用的是逻辑删除，在yml和Entity中进行了逻辑删除字段的配置
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();

        List<Long> path = findPath(catelogId, paths);
        Collections.reverse(path);
        return path.toArray(new Long[path.size()]);
    }

    /**
     *级联更新所有的数据
     * @param category
     */
    @CacheEvict(value = {"category"},key = "'getLevel1Categories'")
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    /**
     * 查一级分类
     * @return
     */
    @Cacheable(value = {"category"} ,key = "#root.method.name")
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        System.out.println("执行方法");
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    /**
     * 首页三级分类中的剩下二级分类
     *
     * 性能优化，将多次查数据库，变为一次
     *
     * 使用Redis
     * @return
     */

    //也可以只使用注解
//    @Cacheable(value = {"category"},key = "#root.methodName",sync = true)
//    public Map<String, List<Catalog2Vo>> getCatalogJsonDbWithSpringCache() {
//        return getDataFromDb();
//    }
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        //空结果短暂缓存
        //随机的缓存失效时间
        //加锁
        //缓存中存的都是JSON字符串,从JSON中取出的字符串要逆转成指定的对象
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        //使用的springframework的
        if (StringUtils.isEmpty(catalogJson)){
            System.out.println("Redis不命中，查询数据库...");
            Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonWithRedissonLock();

        }
        System.out.println("Redis命中...");
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        return result;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonWithRedissonLock() {
        //锁的粒度：粒度越小越快
        RLock lock = redisson.getLock("catalogJson");
        lock.lock();
        Map<String, List<Catelog2Vo>> dataFromDb = null;
        try{
            Thread.sleep(30000);
            dataFromDb = getDataFromDb();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return dataFromDb;
    }

    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //查出所有一级分类，获取到id
        //List<CategoryEntity> level1Categories = getLevel1Categories();
        List<CategoryEntity> level1Categories = getParent_cid(selectList, 0L);
        //.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {})); 收集成Map
        //Map<String, List<Catelog2Vo>>指定k v 都是什么类型
        Map<String, List<Catelog2Vo>> data = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {

            //二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2VoList = null;
            //从数据库查出的进行一下非空判断
            if (categoryEntities != null) {
                catelog2VoList = categoryEntities.stream().map(item -> {
                    //使用全参构造出 catelog2Vo
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());
                    //三级分类
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, item.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> catelog3VoStream = level3Catelog.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(item.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catelog3VoStream);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2VoList;
        }));
        //alibaba提供的JSON工具，将任意对象转成JSON字符串
        String s = JSON.toJSONString(data);
        redisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);
        return data;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithSynchronized() {

        //加锁
        // 1.synchronized (什么样的锁){
        // }  也可以给方法修饰为synchronized
        //只要是同一把锁，就能锁住这个锁的所有线程 synchronized(this)：spring boot所有的组件都是单例的,this当前实例,本地锁，效率高点，适用于单体项目
        //本地锁（synchronized,JUC包下的）是进程锁（一个服务一个进程）也能在分布式下使用，数据库的并发也不是很高，分布式锁重量级锁，更为专业
        //2.分布式锁
        synchronized (this){
            String catalogJson = redisTemplate.opsForValue().get("catalogJson");
            //使用的springframework的
            if (!StringUtils.isEmpty(catalogJson)){
                Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
                return result;
            }
            System.out.println("查询数据库");
            //不传任何查询条件，就是查所有
            return getDataFromDb();
        }

    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,Long parent_cid) {
        List<CategoryEntity> list = selectList.stream().filter(item -> item.getParentCid() == parent_cid
        ).collect(Collectors.toList());
        return  list;
        //return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
    }

    private List<Long> findPath(Long catelogId,List<Long> paths){
        paths.add(catelogId);
        CategoryEntity currentCategory = this.getById(catelogId);
        if(currentCategory.getParentCid() != 0){
            findPath(currentCategory.getParentCid(),paths);
        }
        return paths;
    }

}
