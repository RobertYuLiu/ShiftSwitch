package com.jos.dem.springboot.h2.controller;

import com.jos.dem.springboot.h2.TestHelper;
import com.jos.dem.springboot.h2.service.DealerService;
import com.jos.dem.springboot.h2.service.UtilityService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@WebMvcTest(controllers = DealerController.class)
public class DealerControllerIntegrationTest {
//https://github.com/kriscfoster/spring-boot-testing-pyramid/blob/master/src/test/java/com/kriscfoster/controllertesting/controller/WelcomeControllerIntegrationTest.java
    //need to list all beans used by controller, such as DealerService and UtilityService
    // , otherwise controller bean can not be initialized
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealerService dealerService;

    @MockBean
    private UtilityService utilityService;

    @Test
    void testGetDealersEndpointWithStatusInput() throws Exception {
        when(utilityService.getAllDealers(Mockito.anyString())).thenReturn(TestHelper.getAllDealersWithoutAnyDuplication());
        mockMvc.perform(get("/dealers")
                .param("status", "ass"))
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Successfully updated 4 records.")));
        verify(utilityService).getAllDealers(Mockito.anyString());
    }

    @Test
    void testGetDealersEndpointWithoutStatusInput() throws Exception {
        when(utilityService.getAllDealers(Mockito.anyString())).thenReturn(TestHelper.getAllDealersWithoutAnyDuplication());
        mockMvc.perform(get("/dealers"))
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Successfully updated 0 records.")));
    }

}