package com.yu.fusionbase.common.exception;

import com.yu.fusionbase.common.result.ResultCodeEnum;
import lombok.Data;

@Data
public class FusionBaseException extends RuntimeException{

    private Integer code;

    public FusionBaseException(Integer code,String message){
        super(message);
        this.code = code;
    }

    public FusionBaseException(ResultCodeEnum resultCodeEnum){
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }
}
