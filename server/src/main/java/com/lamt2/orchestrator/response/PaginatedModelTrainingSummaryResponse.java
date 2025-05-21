package com.lamt2.orchestrator.response;

import java.util.List;

public class PaginatedModelTrainingSummaryResponse {
    private List<ModelTrainingSummaryResponse> results;
    private int pageIdx;
    private int numPage;
    private int sizePerPage;

    public PaginatedModelTrainingSummaryResponse(List<ModelTrainingSummaryResponse> results, int pageIdx, int numPage, int sizePerPage) {
        this.results = results;
        this.pageIdx = pageIdx;
        this.numPage = numPage;
        this.sizePerPage = sizePerPage;
    }
}
