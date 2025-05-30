package com.lamt2.orchestrator;

import com.lamt2.orchestrator.entity.ModelFinalInfo;
import com.lamt2.orchestrator.entity.ModelParam;
import com.lamt2.orchestrator.entity.ModelResult;
import com.lamt2.orchestrator.repository.ModelResultRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import(MongoDBServiceTest.MongoDBServiceConfiguration.class)
public class MongoDBServiceTest {
    @TestConfiguration
    public static class MongoDBServiceConfiguration {
        @Bean
        public ModelResultRepository modelResultRepository() {
            return Mockito.mock(ModelResultRepository.class);
        }
    }

    @Autowired
    ModelResultRepository modelResultRepository;

    @Test
    public void testMongo() {
        ModelResult modelResult = new ModelResult();
        modelResult.setModelId("001");
        modelResult.setStartTime(1748533920911L);
        modelResult.setEndTime(1748533921911L);
        ModelParam modelParam = new ModelParam(10, 0.001, 64, 100);
        modelResult.setParam(modelParam);
        modelResult.setLogInterval(10);
        modelResult.setTrainingInfo(new ArrayList<>());
        modelResult.setValidationInfo(new ArrayList<>());
        ModelFinalInfo modelFinalInfo = new ModelFinalInfo(98.98f, 0.001f, 92.83f);
        modelResult.setFinalResult(modelFinalInfo);
        Mockito.when(modelResultRepository.findByModelId("001")).thenReturn(modelResult);

        ModelResult res = modelResultRepository.findByModelId("001");
        Assertions.assertEquals(res.getParam().getNumBatchPerEpoch(), 100);
    }

}
