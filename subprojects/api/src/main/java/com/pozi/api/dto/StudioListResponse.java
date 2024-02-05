package com.pozi.api.dto;

import com.pozi.naver.dto.response.studio.NaverStudioResponse;
import java.util.List;

public record StudioListResponse(
        List<NaverStudioResponse> studios
) {
    public static StudioListResponse of(List<NaverStudioResponse> studios) {
        return new StudioListResponse(studios);
    }
}
