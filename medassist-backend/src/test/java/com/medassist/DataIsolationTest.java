package com.medassist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medassist.auth.dto.request.LoginRequest;
import com.medassist.auth.dto.request.RegisterRequest;
import com.medassist.auth.dto.response.AuthResponse;
import com.medassist.common.response.ApiResponse;
import com.medassist.ai.dto.request.ChatRequest;
import com.medassist.health.dto.response.HealthTwinResponse;
import com.medassist.health.repository.HealthDataRepository;
import com.medassist.ai.repository.ChatRepository;
import com.medassist.medicine.model.Medicine;
import com.medassist.medicine.repository.MedicineRepository;
import com.medassist.report.model.MedicalReport;
import com.medassist.report.repository.MedicalReportRepository;
import com.medassist.user.model.MedicalProfile;
import com.medassist.user.repository.MedicalProfileRepository;
import com.medassist.user.model.User;
import com.medassist.user.repository.UserRepository;
import com.medassist.ai.service.OllamaClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test") // Make sure application-test.properties exists or mongo is embedded
@DisplayName("Data Isolation & User Integrity Tests")
class DataIsolationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private HealthDataRepository healthDataRepository;
    @Autowired private MedicineRepository medicineRepository;
    @Autowired private MedicalReportRepository medicalReportRepository;
        @Autowired private MedicalProfileRepository medicalProfileRepository;
        @Autowired private ChatRepository chatRepository;

        @MockBean private OllamaClient ollamaClient;

    private String userAToken;
    private String userBToken;
    private User userA;
    private User userB;

    @BeforeEach
    void setUp() throws Exception {
        cleanDb();
        userA = registerAndLogin("usera@example.com", "User", "A");
        userAToken = getAccessToken("usera@example.com");

        userB = registerAndLogin("userb@example.com", "User", "B");
        userBToken = getAccessToken("userb@example.com");
    }

    @AfterEach
    void tearDown() {
        cleanDb();
    }

    private void cleanDb() {
        userRepository.deleteAll();
        healthDataRepository.deleteAll();
        medicineRepository.deleteAll();
        medicalReportRepository.deleteAll();
                medicalProfileRepository.deleteAll();
                chatRepository.deleteAll();
    }

    private User registerAndLogin(String email, String firstName, String lastName) throws Exception {
        RegisterRequest registerReq = RegisterRequest.builder()
                .email(email).password("Password123!").firstName(firstName).lastName(lastName)
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        return userRepository.findByEmail(email).orElseThrow();
    }

    private String getAccessToken(String email) throws Exception {
        LoginRequest loginReq = new LoginRequest(email, "Password123!");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<?> response = objectMapper.readValue(result.getResponse().getContentAsString(), ApiResponse.class);
        AuthResponse authRes = objectMapper.convertValue(response.getData(), AuthResponse.class);
        return authRes.getAccessToken();
    }

    @Test
    @DisplayName("New user should have no mock or demo data")
    void newUserShouldHaveNoData() throws Exception {
        // Timeline
        mockMvc.perform(get("/api/timeline")
                .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());

        // Medical profile
        mockMvc.perform(get("/api/profile")
                .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.heightCm").doesNotExist())
                .andExpect(jsonPath("$.data.userId").doesNotExist());

        // Medicines
        mockMvc.perform(get("/api/medicines")
                .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());

        // Reports
        mockMvc.perform(get("/api/reports")
                .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
                
        // Health Twin (Should return 0 score and empty structure, not 404 but empty state)
        mockMvc.perform(get("/api/health/twin")
                .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.healthScore").doesNotExist())
                .andExpect(jsonPath("$.data.sleepData").doesNotExist());
    }

    @Test
    @DisplayName("User B cannot access User A profile, report, or AI chat session")
    void userCannotAccessOtherUsersPrivateData() throws Exception {
        MedicalProfile profile = MedicalProfile.builder()
                .userId(userA.getId())
                .heightCm(182.0)
                .build();
        medicalProfileRepository.save(profile);

        MedicalReport report = MedicalReport.builder()
                .userId(userA.getId())
                .fileName("report-a.pdf")
                .originalFileName("report-a.pdf")
                .status("COMPLETED")
                .build();
        report = medicalReportRepository.save(report);

        when(ollamaClient.chat(anyList(), anyString())).thenReturn("Mock AI response");

        MvcResult userAChat = mockMvc.perform(post("/api/ai/chat")
                .header("Authorization", "Bearer " + userAToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new ChatRequest("Hello", "shared-session", null, "en"))))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<?> userAChatWrapper = objectMapper.readValue(userAChat.getResponse().getContentAsString(), ApiResponse.class);
        String userASessionId = objectMapper.convertValue(userAChatWrapper.getData(), com.medassist.ai.dto.response.ChatResponse.class).getSessionId();

        MvcResult userBChat = mockMvc.perform(post("/api/ai/chat")
                .header("Authorization", "Bearer " + userBToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new ChatRequest("Hello", userASessionId, null, "en"))))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<?> userBChatWrapper = objectMapper.readValue(userBChat.getResponse().getContentAsString(), ApiResponse.class);
        String userBSessionId = objectMapper.convertValue(userBChatWrapper.getData(), com.medassist.ai.dto.response.ChatResponse.class).getSessionId();

        assertThat(userBSessionId).isNotEqualTo(userASessionId);
        assertThat(chatRepository.findByIdAndUserId(userASessionId, userB.getId())).isEmpty();

        mockMvc.perform(get("/api/profile")
                .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.heightCm").doesNotExist());

        mockMvc.perform(get("/api/reports/" + report.getId())
                .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("User A cannot access User B's medicines (IDOR check)")
    void userCannotAccessOtherUsersMedicine() throws Exception {
        // User A creates medicine
        Medicine med = new Medicine();
        med.setUserId(userA.getId());
        med.setMedicineName("Panadol");
        med.setActive(true);
        med.setFrequency("ONCE_DAILY");
        med.setReminderTimes(List.of(java.time.LocalTime.of(8, 0)));
        med = medicineRepository.save(med);

        // User B tries to get it directly
        // Not exposed via direct ID GET in API yet, but check if it's in B's list
        mockMvc.perform(get("/api/medicines")
                .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
                
        // User B tries to delete User A's medicine
        mockMvc.perform(delete("/api/medicines/" + med.getId())
                .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isNotFound()); // Or Forbidden depending on implementation
    }
}
