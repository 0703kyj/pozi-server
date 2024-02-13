package com.pozi.naver.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.pozi.common.exception.badrequest.studio.BadRequestStudioRegionException;
import com.pozi.naver.NaverProvider;
import com.pozi.naver.dto.response.map.NaverMapResponse;
import com.pozi.naver.dto.response.search.Item;
import com.pozi.naver.dto.response.search.NaverSearchResponse;
import com.pozi.naver.dto.response.studio.NaverStudioListResponse;
import com.pozi.naver.dto.response.studio.NaverStudioResponse;
import com.pozi.naver.service.HttpService;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NaverProviderImpl implements NaverProvider {

    private final Gson gson;
    private final HttpService httpService;

    @Value("${naver.client.id}")
    public String clientId;
    @Value("${naver.client.secret}")
    public String clientSecret;
    @Value("${naver.map.id}")
    public String mapId;
    @Value("${naver.map.secret}")
    public String mapSecret;

    @Override
    public NaverSearchResponse getSearchResponse(String studio, Double latitude, Double longitude) {

        String coordinate = String.join(",", Double.toString(longitude), Double.toString(latitude));
        String query = "%s %s".formatted(getArea(coordinate), studio);
        String text = getSearchQueryText(query);

        String apiURL = "https://openapi.naver.com/v1/search/local?query=%s&display=10".formatted(
                text);

        Map<String, String> requestHeaders = new ConcurrentHashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

        String searchResult = httpService.get(apiURL, requestHeaders);
        if (getJsonObject(searchResult).has("items")) {
            return gson.fromJson(searchResult, NaverSearchResponse.class);
        }

        log.info("search:{}", searchResult);
        threadSleep(100);
        return getSearchResponse(studio, latitude, longitude);
    }

    @Override
    public NaverStudioListResponse getStudio(String studio, NaverSearchResponse response) {

        List<Item> items = response.items();
        List<NaverStudioResponse> studios = new ArrayList<>();

        for (Item item : items) {
            if (!item.title().contains(studio.split(" ")[0])) {
                continue;
            }

            studios.add(NaverStudioResponse.of(
                    item.title(),
                    item.address(),
                    item.roadAddress(),
                    item.mapx(),
                    item.mapy()
            ));
        }

        return NaverStudioListResponse.from(studios);
    }

    private static String getSearchQueryText(String query) {
        String text;
        try {
            text = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패", e);
        }
        return text;
    }

    private void threadSleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String getArea(String coordinates) {
        String text = getSearchQueryText(coordinates);

        String apiURL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=%s&output=json".formatted(
                text);

        Map<String, String> requestHeaders = new ConcurrentHashMap<>();
        requestHeaders.put("X-NCP-APIGW-API-KEY-ID", mapId);
        requestHeaders.put("X-NCP-APIGW-API-KEY", mapSecret);

        JsonObject jsonObject = getJsonObject(httpService.get(apiURL, requestHeaders));

        validateAreaResult(jsonObject);

        return getSubArea("2", jsonObject) + " " + getSubArea("3", jsonObject);
    }

    private String getSubArea(String number, JsonObject jsonObject) {
        return jsonObject.getAsJsonArray("results")
                .get(0).getAsJsonObject()
                .getAsJsonObject("region")
                .getAsJsonObject("area" + number)
                .get("name").getAsString();
    }

    private JsonObject getJsonObject(String result) {

        JsonObject jsonObject;
        try {
            JsonElement jsonElement = JsonParser.parseString(result);
            jsonObject = jsonElement.getAsJsonObject();
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("JSON 형식으로 변환할 수 없습니다.");
        }

        return jsonObject;
    }

    private void validateAreaResult(JsonObject jsonObject) {
        String status = jsonObject.getAsJsonObject("status").get("code").getAsString();
        if (status.equals("3")) {
            throw new BadRequestStudioRegionException();
        }
    }

    @Deprecated
    private NaverMapResponse getCoordinate(String query) {
        String text;
        try {
            text = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패", e);
        }

        String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=%s".formatted(
                text);

        Map<String, String> requestHeaders = new ConcurrentHashMap<>();
        requestHeaders.put("X-NCP-APIGW-API-KEY-ID", mapId);
        requestHeaders.put("X-NCP-APIGW-API-KEY", mapSecret);

        String coordinate = httpService.get(apiURL, requestHeaders);
        return gson.fromJson(coordinate, NaverMapResponse.class);
    }
}
