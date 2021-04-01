package com.zcw.cmall.goods;



import com.zcw.cmall.goods.dao.AttrGroupDao;
import com.zcw.cmall.goods.dao.SkuSaleAttrValueDao;
import com.zcw.cmall.goods.entity.BrandEntity;
import com.zcw.cmall.goods.service.BrandService;
import com.zcw.cmall.goods.vo.SkuItemSaleAttrsVo;
import com.zcw.cmall.goods.vo.SpuItemAttrGroupVo;
import org.junit.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;

@SpringBootTest
class CmallGoodsApplicationTests {

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    BrandService brandService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedissonClient redissonClient;

    @Test
    public void test1(){
        List<SkuItemSaleAttrsVo> saleAttrsBySpuId = skuSaleAttrValueDao.getSaleAttrsBySpuId(15L);
        System.out.println(saleAttrsBySpuId);
    }
    @Test
    public void test(){
        List<SpuItemAttrGroupVo> vos = attrGroupDao.getAttrGroupWithAttrsBySpuId(15L,225L);
        System.out.println(vos);
    }
    @Test
    public void redissonTest(){
        System.out.println(redissonClient);
    }
    @Test
    void redisTest(){
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();

        opsForValue.set("hello","redis");
        String s = opsForValue.get("hello");
        System.out.println(s);
    }
    @Test
    void contextLoads() {

        BrandEntity brandEntity = new BrandEntity();

        brandEntity.setName("苹果");

        brandService.save(brandEntity);

    }

//    @Test
//    public void testUpload() throws FileNotFoundException {
//        // Endpoint以杭州为例，其它Region请按实际情况填写。
//        String endpoint = "oss-cn-beijing.aliyuncs.com";
//        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
//        String accessKeyId = "LTAI4G4GwAfYSzts2hpHZcze";
//        String accessKeySecret = "S9rTagkicAEP2hf97GIwOhgx9DCCaC";
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//        // 上传文件流。
//        InputStream inputStream = new FileInputStream("D:\\手机\\vlight\\img0d93e17a21bcda41b9d49e9a5ae21e20.jpg");
//        ossClient.putObject("cmall-chrisz", "vlight.jpg", inputStream);
//
//        // 关闭OSSClient。
//        ossClient.shutdown();
//
//        System.out.println("上传成功");
//    }

//    @Autowired
//    private OSSClient ossClient;
//
//    @Test
//    void testUpload() throws FileNotFoundException {
//        // 上传文件流。
//        InputStream inputStream = new FileInputStream("D:\\手机\\网易云相册\\  晚上一个人写作业听的歌_109951163245712817.jpg");
//        ossClient.putObject("cmall-chrisz", "晚上一个人写作业听的歌.jpg", inputStream);
//
//        // 关闭OSSClient。
//        ossClient.shutdown();
//
//        System.out.println("上传成功");
//    }

}
