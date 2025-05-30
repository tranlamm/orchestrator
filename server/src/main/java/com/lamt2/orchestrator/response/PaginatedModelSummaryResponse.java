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
public class PaginatedModelSummaryResponse {
    private List<ModelSummaryResponse> results;
    private int pageIdx;
    private int numPage;
    private int sizePerPage;

    public void setResults(List<ModelSummaryResponse> results) {
        this.results = results;
    }

    public void setPageIdx(int pageIdx) {
        this.pageIdx = pageIdx;
    }

    public void setNumPage(int numPage) {
        this.numPage = numPage;
    }

    public void setSizePerPage(int sizePerPage) {
        this.sizePerPage = sizePerPage;
    }

    public List<ModelSummaryResponse> getResults() {
        return results;
    }

    public int getPageIdx() {
        return pageIdx;
    }

    public int getNumPage() {
        return numPage;
    }

    public int getSizePerPage() {
        return sizePerPage;
    }
}
