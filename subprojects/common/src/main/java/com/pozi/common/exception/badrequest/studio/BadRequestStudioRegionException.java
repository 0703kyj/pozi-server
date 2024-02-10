package com.pozi.common.exception.badrequest.studio;

import com.pozi.common.exception.badrequest.BadRequestException;

public class BadRequestStudioRegionException extends BadRequestException {

    public BadRequestStudioRegionException() {
        super("요청하신 위치 죄표 값에 대한 정보가 없습니다.", 4000);
    }
}
