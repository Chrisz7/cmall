package com.zcw.common.exception;

/**
 * @author Chrisz
 * @date 2020/12/26 - 17:12
 */
public class NoStockException extends RuntimeException {

    private Long skuId;

    public NoStockException(){

    }
    public NoStockException(Long skuId){

        super("商品ID:"+skuId+",没有足够的库存了");
    }

    public NoStockException(String message) {
        super(message);
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
