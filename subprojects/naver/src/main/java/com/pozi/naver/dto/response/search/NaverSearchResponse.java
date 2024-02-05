package com.pozi.naver.dto.response.search;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public record NaverSearchResponse(
        @SerializedName("items")
        List<Item> items
) {}
