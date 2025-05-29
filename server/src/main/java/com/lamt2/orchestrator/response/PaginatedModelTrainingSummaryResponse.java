package com.lamt2.orchestrator.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedModelTrainingSummaryResponse {
    private List<ModelTrainingSummaryResponse> results;
    private int pageIdx;
    private int numPage;
    private int sizePerPage;
}
