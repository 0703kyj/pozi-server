package com.pozi.api.controller;

import com.pozi.api.dto.StudioListResponse;
import com.pozi.api.service.StudioService;
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
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude
    ) {
        return studioService.getDefaultStudios(latitude,longitude);
    }

    @GetMapping("/search")
    public StudioListResponse searchStudio(
            @RequestParam("studio") String studio,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitude") Double longitude
    ) {
        return studioService.searchStudio(studio,latitude,longitude);
    }
}
