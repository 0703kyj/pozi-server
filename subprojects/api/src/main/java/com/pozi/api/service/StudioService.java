package com.pozi.api.service;

import com.pozi.api.dto.StudioListResponse;
import com.pozi.naver.NaverProvider;
import com.pozi.naver.dto.response.search.NaverSearchResponse;
import com.pozi.naver.dto.response.studio.NaverStudioListResponse;
import com.pozi.naver.dto.response.studio.NaverStudioResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StudioService {

    private final NaverProvider naverProvider;
    private static final List<String> studios = List.of(
            "포토매틱", "하루필름", "셀픽스", "포토드링크", "포토그레이", "포토이즘", "비룸 스튜디오",
            "인생네컷", "포토시그니처", "포토하임", "홍대네컷", "포토아이브", "플랜비 스튜디오", "포니필름"
    );

    @Transactional
    public StudioListResponse searchStudio(String studio, Double latitude, Double longitude){
        NaverSearchResponse searchResponse = naverProvider.getSearchResponse(studio, latitude, longitude);
        NaverStudioListResponse studioListResponse = naverProvider.getStudio(studio, searchResponse);

        return StudioListResponse.of(studioListResponse.studios());
    }

    @Transactional
    public StudioListResponse getDefaultStudios(Double latitude, Double longitude) {
        List<NaverStudioResponse> searchedStudios = new ArrayList<>();

        for (String studio : studios) {
            NaverSearchResponse searchResponse = naverProvider.getSearchResponse(studio, latitude, longitude);
            NaverStudioListResponse studioListResponse = naverProvider.getStudio(studio, searchResponse);
            searchedStudios.addAll(studioListResponse.studios());
        }

        return StudioListResponse.of(searchedStudios);
    }
}
