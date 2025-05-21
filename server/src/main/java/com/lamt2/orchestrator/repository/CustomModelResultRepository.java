package com.lamt2.orchestrator.repository;

import com.lamt2.orchestrator.response.PaginatedModelSummaryResponse;

public interface CustomModelResultRepository {
    PaginatedModelSummaryResponse findAllSortedByDuration(int page, boolean ascending, String sortedField);
}
