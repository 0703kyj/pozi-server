package com.pozi.naver.dto.response.search;

import com.google.gson.annotations.SerializedName;

public record Item(
        @SerializedName("title")
        String title,
        @SerializedName("link")
        String link,
        @SerializedName("address")
        String address,
        @SerializedName("roadAddress")
        String roadAddress,
        @SerializedName("mapx")
        String mapx,
        @SerializedName("mapy")
        String mapy
) {

}
