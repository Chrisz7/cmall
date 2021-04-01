/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.zcw.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;


	//R是继承的HashMap，只能存key-value，不能使用私有属性，泛型对HashMap没有作用


	//利用fastjson进行逆转，要在方法中使用泛型就得先在public<T> T 声明泛型
	public<T> T getData(TypeReference<T> t){
		//从map中获取key为data的值，data是map类型的（因为put的是一个对象，逆转得到的是map
		Object data = get("data");
		String s = JSON.toJSONString(data);
		//将字符串转化成指定类型的对象
		T t1 = JSON.parseObject(s, t);
		return t1;
	}
	public <T> T getData(String key, TypeReference<T> tTypeReference) {
		Object data = this.get(key);
		String toJSONString = JSON.toJSONString(data);
		T t = JSON.parseObject(toJSONString, tTypeReference);
		return t;
	}
	//R类型的数据，能方便进行链式调用
	public R setData(Object data){
		put("data",data);
		return this;
	}

	public R() {
		put("code", 0);
		put("msg", "success");
	}

	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}

	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}

	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}

	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}

	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}
	public  Integer getCode() {
		return (Integer) this.get("code");
	}

}
