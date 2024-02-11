package com.pozi.api.dto;

import com.pozi.api.domain.Studio;
import lombok.Builder;

@Builder
public record StudioResponse(
        String name,
        String address,
        String roadAddress,
        Double mapx,
        Double mapy
) {
    public static StudioResponse from(Studio studio) {
        return StudioResponse.builder()
                .name(studio.getName())
                .address(studio.getAddress())
                .roadAddress(studio.getRoadAddress())
                .mapx(studio.getMapx())
                .mapy(studio.getMapy())
                .build();
    }
}
