package com.zcw.common.constant;

/**
 * @author Chrisz
 * @date 2020/11/1 - 11:48
 */
public class GoodsConstant {

    public enum AttrEnum{
        ATTR_TYPR_BASE(1,"基本属性"),ATTR_SALE(0,"销售属性");
        private int code;
        private String msg;
        AttrEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }


    public enum StatusEnum{
        NEW_SPU(0,"新建"),UP_SPU(1,"上架"),
        DOWN_SPU(1,"下架");
        private int code;
        private String msg;
        StatusEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
