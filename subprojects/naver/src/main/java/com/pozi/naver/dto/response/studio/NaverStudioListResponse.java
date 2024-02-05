package com.pozi.naver.dto.response.studio;

import java.util.List;

public record NaverStudioListResponse(
        List<NaverStudioResponse> studios
) {
    public static NaverStudioListResponse from(List<NaverStudioResponse> studios) {
        return new NaverStudioListResponse(studios);
    }
}
