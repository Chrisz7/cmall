package com.zcw.cmall.order.config;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.zcw.cmall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000116686072";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCnHMtUeuQifFOdttgrtIvHphv0gqTMRAyGvXgEhhwb3XDrzEIY0g9f8bYIzgbT7CPyaRCw8/JRE+y8XaI6IXPZFG4W93xMpPhXgJet5g7NiZbR9xbh2uhLp8/FLjzbddCli3AAsv4LAHF38XV5fbxh0iiXZdOu/IEteuVHpgOv0XdbtBUL7UN9FLhkU5oa5raersPFraiacR6qSbI55u2dkxnVEO+Ezj4t4EzYHA7JLuczhFSwjKP0vN1cEcYZe1867yiJpd6CeiDgeDrUj3ByyPDV1g9vj2zj0j6VWWAlpgoBFzSJan5q4OCmSDi/caenhzjK/tekUiRfw17RfrqrAgMBAAECggEAP5Q+78dJoK04itqT6BzqyWyzWhWSaXzCX0karm3vp9JaMfwmcZcJ7nDh1vu7sUk4a/P5O7m0SZCREyF0pTUxBDg3ollKW0awjWoJS0op+50LWprtg8RreM1ByT1MPhrW5QTl+f4flUP357BgB3cFAxqYAvHk1cCPzSPis52/7VXdvB3kyABxmcEmsl+CgfBse1yJdueTeremClGDAXFBOs5TBSRE/wPfTILs/19wOemhoTmMrImEn/3f7QHgotOrxkT1f9qjCtOYwO3zxNg1DEfAfMKwYdN2rV89+KOtZt/N0YMYUsDmpVxq93GYfSisS8wT/78aEDy5wQ51hAU3wQKBgQD9L6PuOS0HIlyzBpqpIo00oPYDhz82XCgurAJqfL2Il2vfbkaJuU317EbcHpfogZltLY98Mr9mY+5qRfjZMyEUIpsW1JaJEodTWT05Utj6+bXMCT2HAwiZ60mNoBptHRTZV8zhrzSdGzi0ji/h3ndH9ot93asUsTD29nrxbD8okQKBgQCo+EJT+qLlfhyyEE5r2jynI8LYZdivsv1iY8Na2LIFuWFh/zktpCYNPKs6Xhfq9oQrx3VukzeIvxXeTpGGxqUNXqvLLzdth4kjWxhgxvmQ1w041dg9I+YTv2iaXx+3ZX4bUA9PXPWyQ7x06OwWviPbxlXGWIuoWBvWrU+BeRjtewKBgQCqaHw/hMAnwxr9a/6AxD6iI4wxx7/mCJULBL8DiT3QR4pVcdWMMOqmEQeIdBbuaPwpHbcrcsScV7pKURoyplUnRjt4XFGNpKjskm3AvWBJW/TehYGh84rAfjqdVhszQZWcy5mcdN5Gt+GstN7JeYBzMR+l8aKKZNB+CFurTp4b4QKBgA0HyCWtyG4mQZRByaprlA3XpikGAaXbCctSojgZSfFDEI18Hy6z3/NZYGMJ6pJGHH87zISQedHj2o3gRTy9iPD1Ag6TP5UExMCjP8gG0CwpSW2+oARnBlEzi5+MAmgkPYthqyyVoKT0sE3bwIkodJ94Nhkrrul2kf8GMoFrk06NAoGBAL+CKygB/SPoWv8AthhlU/p9wO6WkPLh5uag6g+cXweJDgAmhkTbLz9CgjJWofc7W5+Mn7t88syK9imHc9bH4bEXVrPB0iUhDeN8MFLBkzt/OK4bcROCkloBpc/2DZpdPWMlxNvfeRV7o+5ZE3MK3jtMgfHoEJ2tcHF4jRyK6zHf";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnImaI60vQ2JjrjKpowp/3cjoQrM2ymWtNWGRVSORVJrvTnbcpHrbMsHgUgP00dAPP7N7liqCnWtF3Zr3mZh+k+Yk0k74q8CdcSVj/Xu3RvEu7y72xxHZfaJYMit6EI6vGG4lf7hO4mzApZ75pL1oAS0YQL7iGZWFKwVYNeAShG6NpQJnIlqUJ82+36W9io+EmdkuUYXIULpjArIYpCcgQfvoVhArcLbW7coz+QgxfV6rP0JMD5Q7An3/drPYPxsQd/37hK0m7Ezv57jfSd82Y0myhufYrdYU3JWNXvpFP0Qx0QnPnUU/b+uOmR2fNgHBtS+Pr6lMHMnK63YBPE9VbQIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付成功，支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    //="http://alipaycmallcom.free.vipnps.vip.natappfree.cc/payed/notify"
    //支付成功后，支付宝会持续异步通知我们的服务器，25小时内，8次，直到返回给支付宝success，要保证支付宝能访问通我们的服务
    private  String notify_url = "http://order.free.vipnps.vip/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    //但是如果浏览器崩溃，就使用不了
    private  String return_url = "http://member.cmall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    private String timeout = "30m";
    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\""+timeout+"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
