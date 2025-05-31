package com.lamt2.orchestrator;

import com.lamt2.orchestrator.controller.ModelQueryController;
import com.lamt2.orchestrator.repository.ModelResultRepository;
import com.lamt2.orchestrator.response.ModelSummaryResponse;
import com.lamt2.orchestrator.response.PaginatedModelSummaryResponse;
import com.lamt2.orchestrator.service.model_query.ModelQueryService;
import com.lamt2.orchestrator.service.model_training.ModelTrainingService;
import com.mongodb.client.gridfs.GridFSBucket;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class ModelQueryControllerTest {
    @Autowired
    public MockMvc mockMvc;

    @MockBean
    public ModelResultRepository modelResultRepository;

    @Test
    public void testModelQuery() throws Exception {
        PaginatedModelSummaryResponse paginatedModelSummaryResponse = new PaginatedModelSummaryResponse();
        ModelSummaryResponse modelSummaryResponse = new ModelSummaryResponse("abc", 1748484288828L, 1748484288928L, 98.99f, 0.0012f, 94.59f, 1000L);
        paginatedModelSummaryResponse.setNumPage(1);
        paginatedModelSummaryResponse.setPageIdx(0);
        paginatedModelSummaryResponse.setSizePerPage(1);
        paginatedModelSummaryResponse.setResults(Collections.singletonList(modelSummaryResponse));
        Mockito.when(modelResultRepository.findAllSortedByDuration(0, true, "test")).thenReturn(paginatedModelSummaryResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/result/all?page=0&ascending=true&sortedField=test"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageIdx").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.numPage").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sizePerPage").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results", Matchers.hasSize(1)));
    }
}
