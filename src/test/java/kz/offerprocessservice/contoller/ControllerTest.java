package kz.offerprocessservice.contoller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kz.offerprocessservice.model.dto.BaseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import util.Fields;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.testcontainers.containers.wait.strategy.Wait.forListeningPort;
import static org.testcontainers.containers.wait.strategy.Wait.forLogMessage;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class ControllerTest {

    protected static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String POSTGRES_IMAGE_NAME = "postgres:17";
    private static final String TEST = "test";

    @Autowired
    protected MockMvc mockMvc;

    @Container
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME)
            .withDatabaseName(TEST)
            .withUsername(TEST)
            .withPassword(TEST)
            .waitingFor(forListeningPort())
            .withStartupTimeout(Duration.ofSeconds(10))
            .waitingFor(forLogMessage(".*database system is ready to accept connections.*\\n", 1));

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Test
    void contextLoads() {
        assertNotNull(container.getJdbcUrl());
    }

    protected <T extends BaseDTO> MvcResult sendPostRequest(String url, T body, ResultMatcher expectedStatus) {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.post(url)
                                           .contentType(APPLICATION_JSON)
                                           .content(objectMapper.writeValueAsString(body)))
                    .andExpect(expectedStatus)
                    .andReturn();
        } catch (Exception e) {
            throw new RuntimeException("MockMvc POST request failed", e);
        }
    }

    protected MvcResult sendGetRequest(String url, ResultMatcher expectedStatus) {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.get(url)
                                           .contentType(APPLICATION_JSON)
                                           .characterEncoding(UTF_8))
                    .andExpect(expectedStatus)
                    .andReturn();

        } catch (Exception e) {
            throw new RuntimeException("MockMvc GET request failed", e);
        }
    }

    protected MvcResult sendDeleteRequest(String url, ResultMatcher expectedStatus) {
        try {
            return mockMvc.perform(MockMvcRequestBuilders.delete(url)
                                           .contentType(APPLICATION_JSON)
                                           .characterEncoding(UTF_8))
                    .andExpect(expectedStatus)
                    .andReturn();
        } catch (Exception e) {
            throw new RuntimeException("MockMvc DELETE request failed", e);
        }
    }

    protected <T> T readMvcResultAsString(MvcResult mvcResult, Class<T> valueType) {
        try {
            String json = mvcResult.getResponse().getContentAsString(UTF_8);
            return objectMapper.readValue(json, valueType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse MVC response", e);
        }
    }

    protected void assertSystemValuesIsNotNull(MvcResult mvcResult) {
        try {
            Map<String, Object> systemValues = objectMapper.readValue(
                    mvcResult.getResponse()
                            .getContentAsString(), new TypeReference<HashMap<String, Object>>() {
                    }
            );
            assertNotNull(systemValues.get(Fields.ID));
            assertNotNull(systemValues.get(Fields.CREATED_AT));
            assertNotNull(systemValues.get(Fields.UPDATED_AT));
        } catch (Exception e) {
            throw new RuntimeException("Failed to assert system values from MvcResult", e);
        }
    }
}
