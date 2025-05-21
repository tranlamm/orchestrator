package com.lamt2.orchestrator.response;

import com.lamt2.orchestrator.entity.ModelResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PaginatedModelSummaryResponse {
    private List<ModelSummaryResponse> results;
    private int pageIdx;
    private int numPage;
    private int sizePerPage;

    public PaginatedModelSummaryResponse(List<ModelSummaryResponse> results, int pageIdx, int numPage, int sizePerPage) {
        this.results = results;
        this.pageIdx = pageIdx;
        this.numPage = numPage;
        this.sizePerPage = sizePerPage;
    }
}
