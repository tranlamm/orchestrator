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
}
