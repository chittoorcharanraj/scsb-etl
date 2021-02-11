package org.recap.config;

import brave.sampler.Sampler;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertNotNull;

public class SamplerConfigUT extends BaseTestCaseUT {

    @InjectMocks
    SamplerConfig samplerConfig;

    @Test
    public void defaultSampler() {
        Sampler sampler = samplerConfig.defaultSampler();
        assertNotNull(sampler);
    }

}
