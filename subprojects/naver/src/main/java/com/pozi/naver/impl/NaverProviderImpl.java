package com.pozi.naver.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.pozi.common.exception.badrequest.studio.BadRequestStudioRegionException;
import com.pozi.naver.NaverProvider;
import com.pozi.naver.dto.response.map.Address;
import com.pozi.naver.dto.response.map.NaverMapResponse;
import com.pozi.naver.dto.response.search.Item;
import com.pozi.naver.dto.response.search.NaverSearchResponse;
import com.pozi.naver.dto.response.studio.NaverStudioListResponse;
import com.pozi.naver.dto.response.studio.NaverStudioResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

        String text;
        String coordinate = String.join(",", Double.toString(longitude), Double.toString(latitude));
        String query = "%s %s".formatted(getArea(coordinate),studio);
        try {
            text = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }
        log.info("{}", query);

        String apiURL = "https://openapi.naver.com/v1/search/local?query=%s&display=10".formatted(text);

        Map<String, String> requestHeaders = new ConcurrentHashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

        String searchResult = get(apiURL, requestHeaders);
        log.info("search:{}", searchResult);
        if(getJsonObject(searchResult).has("items")){
            return gson.fromJson(searchResult, NaverSearchResponse.class);
        }
        return getSearchResponse(studio, latitude, longitude);
    }

    @Override
    public NaverStudioListResponse getStudio(NaverSearchResponse response) {

        List<Item> items = response.items();
        List<NaverStudioResponse> studios = new ArrayList<>();

        for (Item item : items) {
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

    private String getArea(String coordinates) {
        String text;
        try {
            text = URLEncoder.encode(coordinates, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }

        String apiURL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=%s&output=json".formatted(text);

        Map<String, String> requestHeaders = new ConcurrentHashMap<>();
        requestHeaders.put("X-NCP-APIGW-API-KEY-ID", mapId);
        requestHeaders.put("X-NCP-APIGW-API-KEY", mapSecret);

        JsonObject jsonObject = getJsonObject(get(apiURL, requestHeaders));

        validateAreaResult(jsonObject);

        return jsonObject.getAsJsonArray("results")
                .get(0).getAsJsonObject()
                .getAsJsonObject("region")
                .getAsJsonObject("area2")
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
        if (status.equals("3")){
            throw new BadRequestStudioRegionException();
        }
    }

    @Deprecated
    private NaverMapResponse getCoordinate(String query) {
        String text;
        try {
            text = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }

        String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=%s".formatted(text);

        Map<String, String> requestHeaders = new ConcurrentHashMap<>();
        requestHeaders.put("X-NCP-APIGW-API-KEY-ID", mapId);
        requestHeaders.put("X-NCP-APIGW-API-KEY", mapSecret);

        String coordinate = get(apiURL, requestHeaders);
        return gson.fromJson(coordinate, NaverMapResponse.class);
    }

    private String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }


            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 오류 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }


    private HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }


    private String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }


            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }
}
