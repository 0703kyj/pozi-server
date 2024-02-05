package com.pozi.naver.dto.response.map;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public record NaverMapResponse(
        @SerializedName("addresses")
        List<Address> addresses
) {}
