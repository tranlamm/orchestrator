package com.lamt2.orchestrator;

import com.lamt2.orchestrator.utils.MathUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class OtherTest {
    @Test
    public void test() {
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        String rawPassword = "admin";
//        String encodedPassword = encoder.encode(rawPassword);
//        System.out.println("Encrypted password: " + encodedPassword);

        int totalBatch = 5 * 750;
        int numBatchDone = Math.max(5 - 1, 0) * 750 + 748;
        float tmp = MathUtils.roundFloat((float) numBatchDone / totalBatch * 100);
        System.out.println(tmp);
    }
}
