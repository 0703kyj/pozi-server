package com.pozi.naver;

import com.pozi.naver.dto.response.search.NaverSearchResponse;
import com.pozi.naver.dto.response.studio.NaverStudioListResponse;

public interface NaverProvider {

    public NaverSearchResponse getSearchResponse(String studio, Double latitude, Double longitude);

    public NaverStudioListResponse getStudio(NaverSearchResponse response);
}
