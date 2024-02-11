package com.pozi.naver.dto.response.map;

import com.google.gson.annotations.SerializedName;

public record Address(
        @SerializedName("x")
        Double latitude,
        @SerializedName("y")
        Double longitude
) {

}
