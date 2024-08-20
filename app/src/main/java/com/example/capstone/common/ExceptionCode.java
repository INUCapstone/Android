package com.example.capstone.common;

import lombok.Getter;

@Getter
public enum ExceptionCode {

    SERVER_INNER_ERROR("서버 내부에 문제가 발생헀습니다.",500),
    NETWORK_ERROR("네트워크가 불안정합니다.",500),
    INPUT_VAILDATION_ERORR("입력 형식이 올바르지 않습니다.",400);

    private final String exceptionMessage;
    private final int exceptionCode;

    ExceptionCode(String exceptionMessage, int exceptionCode){
        this.exceptionMessage = exceptionMessage;
        this.exceptionCode = exceptionCode;
    }
}
