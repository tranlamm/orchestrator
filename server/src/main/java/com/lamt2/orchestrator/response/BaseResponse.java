package com.lamt2.orchestrator.response;

import lombok.Data;

import java.util.Map;

@Data
public class BaseResponse {
    Map<String, String> params;

    public BaseResponse(Map<String, String> params) {
        this.params = params;
    }
}
