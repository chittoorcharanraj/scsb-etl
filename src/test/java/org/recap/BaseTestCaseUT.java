package org.recap;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource("classpath:application.properties")
@RunWith(MockitoJUnitRunner.Silent.class)
public class BaseTestCaseUT {

    @Test
    public void contextLoads() {
    }
}