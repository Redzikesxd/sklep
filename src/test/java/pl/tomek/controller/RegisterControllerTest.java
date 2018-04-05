package pl.tomek.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;





@RunWith(SpringJUnit4ClassRunner.class)
public class RegisterControllerTest {


    @InjectMocks
    private RegisterController registerController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(registerController).build();
    }


    @Test
    public void register() throws Exception {

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }
}