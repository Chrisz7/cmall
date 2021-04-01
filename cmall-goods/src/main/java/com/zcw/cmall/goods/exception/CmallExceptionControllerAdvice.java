package com.zcw.cmall.goods.exception;

import com.zcw.common.exception.ExceCodeEnum;
import com.zcw.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 集中感知并处理所有异常
 * @author Chrisz
 * @date 2020/10/28 - 16:00
 */
//@ResponseBody
//@ControllerAdvice(basePackages = "com.zcw.cmall.goods.app")
@Slf4j
@RestControllerAdvice(basePackages = "com.zcw.cmall.goods.app")
public class CmallExceptionControllerAdvice {

    @ExceptionHandler(value= MethodArgumentNotValidException.class)
    public R handleVaildException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{}，异常类型：{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();

        Map<String,String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach((fieldError)->{
            errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());
        });
        return R.error(ExceCodeEnum.VAILD_EXCEPTION.getCode(),ExceCodeEnum.VAILD_EXCEPTION.getMsg()).put("data",errorMap);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){

        log.error("错误：",throwable);
        return R.error(ExceCodeEnum.UNKNOW_EXCEPTION.getCode(),ExceCodeEnum.UNKNOW_EXCEPTION.getMsg());
    }

}
