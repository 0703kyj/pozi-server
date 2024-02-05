package com.pozi.api.service;

import com.pozi.api.dto.StudioListResponse;
import com.pozi.naver.NaverProvider;
import com.pozi.naver.dto.response.search.NaverSearchResponse;
import com.pozi.naver.dto.response.studio.NaverStudioListResponse;
import com.pozi.naver.dto.response.studio.NaverStudioResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudioService {

    private final NaverProvider naverProvider;
    private static final List<String> studios = List.of(
            "인생 네컷", "포토매니아", "포토이즘", "하루필름",
            "홍대네컷", "포토시그니처", "포토메틱", "포토아이브",
            "셀픽스", "비룸스튜디오", "포토드링크", "플랜비스튜디오","포토그레이"
    );

    @Transactional
    public StudioListResponse searchStudio(String studio, Double latitude, Double longitude){
        NaverSearchResponse searchResponse = naverProvider.getSearchResponse(studio, latitude, longitude);
        NaverStudioListResponse studioListResponse = naverProvider.getStudio(searchResponse);

        return StudioListResponse.of(studioListResponse.studios());
    }

    @Transactional
    public StudioListResponse getDefaultStudios(Double latitude, Double longitude) {
        List<NaverStudioResponse> searchedStudios = new ArrayList<>();

        for (String studio : studios) {
            NaverSearchResponse searchResponse = naverProvider.getSearchResponse(studio, latitude, longitude);
            NaverStudioListResponse studioListResponse = naverProvider.getStudio(searchResponse);
            searchedStudios.addAll(studioListResponse.studios());
        }

        return StudioListResponse.of(searchedStudios);
    }
}
