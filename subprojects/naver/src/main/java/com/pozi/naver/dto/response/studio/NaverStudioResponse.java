package com.pozi.naver.dto.response.studio;

import lombok.Builder;

@Builder
public record NaverStudioResponse(
    String title,
    String address,
    String roadAddress,
    Double latitude,
    Double longitude
) {
    public static NaverStudioResponse of(String title, String address, String roadAddress, Double latitude, Double longitude) {
        return NaverStudioResponse.builder()
                .title(title)
                .address(address)
                .roadAddress(roadAddress)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
