package com.pozi.common.exception.badrequest;

public class BadRequestStudioException extends BadRequestException {

    public BadRequestStudioException() {
        super("스튜디오 이름은 공백일 수 없습니다.",4001);
    }
}
