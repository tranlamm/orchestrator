package com.lamt2.orchestrator;

import com.lamt2.orchestrator.entity.ModelFinalInfo;
import com.lamt2.orchestrator.entity.ModelParam;
import com.lamt2.orchestrator.entity.ModelResult;
import com.lamt2.orchestrator.repository.ModelResultRepository;
import com.lamt2.orchestrator.response.PaginatedModelSummaryResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
@DataMongoTest
public class MongoDBServiceMemTest {
    @Autowired
    public ModelResultRepository modelResultRepository;

    @Test
    public void testMongoService() {
        modelResultRepository.deleteAll();
        List<ModelResult> list = new ArrayList<>();
        for (int i = 0; i < 101; ++i) {
            ModelResult modelResult = new ModelResult();
            modelResult.setModelId("00" + i);
            modelResult.setStartTime(1748533920911L);
            modelResult.setEndTime(1748533920911L + i);
            ModelParam modelParam = new ModelParam(10, 0.001f, 64, 100);
            modelResult.setParam(modelParam);
            modelResult.setLogInterval(10);
            modelResult.setTrainingInfo(new ArrayList<>());
            modelResult.setValidationInfo(new ArrayList<>());
            ModelFinalInfo modelFinalInfo = new ModelFinalInfo(98.98f, 0.001f, 92.83f);
            modelResult.setFinalResult(modelFinalInfo);
            list.add(modelResult);
        }
        modelResultRepository.saveAll(list);

        PaginatedModelSummaryResponse paginatedModelSummaryResponse = modelResultRepository.findAllSortedByDuration(8, false, "duration");
        Assertions.assertEquals(10, paginatedModelSummaryResponse.getSizePerPage());
        Assertions.assertEquals(11, paginatedModelSummaryResponse.getNumPage());
        Assertions.assertEquals(8, paginatedModelSummaryResponse.getPageIdx());
        Assertions.assertEquals("0020", paginatedModelSummaryResponse.getResults().get(0).getModelId());
    }
}
