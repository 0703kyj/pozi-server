package com.pozi.naver.dto.response.studio;

import lombok.Builder;

@Builder
public record NaverStudioResponse(
    String title,
    String address,
    String roadAddress,
    Double mapx,
    Double mapy
) {
    public static NaverStudioResponse of(String title, String address, String roadAddress, Double mapx, Double mapy) {
        return NaverStudioResponse.builder()
                .title(title)
                .address(address)
                .roadAddress(roadAddress)
                .mapx(mapx)
                .mapy(mapy)
                .build();
    }
}
