package com.pozi.api.controller;

import com.pozi.api.dto.StudioListResponse;
import com.pozi.api.service.StudioService;
import com.pozi.common.exception.badrequest.BadRequestStudioException;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/studio")
@RequiredArgsConstructor
public class StudioController {

    private final StudioService studioService;

    @GetMapping
    public StudioListResponse getDefaultStudios(
            @Parameter(description = "위도", example = "37.4199696")
            @RequestParam("latitude") Double latitude,
            @Parameter(description = "경도", example = "127.126651")
            @RequestParam("longitude") Double longitude
    ) {
        return studioService.getDefaultStudios(latitude,longitude);
    }

    @GetMapping("/search")
    public StudioListResponse searchStudio(
            @Parameter(description = "스튜디오 명", example = "인생 네컷")
            @RequestParam("studio") String studio,
            @Parameter(description = "위도", example = "37.4199696")
            @RequestParam("latitude") Double latitude,
            @Parameter(description = "경도", example = "127.126651")
            @RequestParam("longitude") Double longitude
    ) {
        if(studio.isBlank()){
            throw new BadRequestStudioException();
        }
        return studioService.searchStudio(studio,latitude,longitude);
    }
}
