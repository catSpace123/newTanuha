package com.tanhua.commons.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 自定义异常类
 */
@Data
@NoArgsConstructor
public class TanhuaException extends RuntimeException {

    private Object errData;

    public TanhuaException(String errMessage){
        super(errMessage);
    }

    public TanhuaException(Object data){
        super();
        this.errData = data;
    }
}
