package com.zstu.exception;

/**
 * CRC异常
 *
 * @auther Stiles-JKY
 * @date 2021/4/19-13:46
 */
public class CRCException extends Exception {

    private static final long serialVersionUID = 20210419;

    public CRCException(String CRCCode) {
        super(CRCCode);
    }


}
