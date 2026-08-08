package com.medassist;

import com.medassist.ai.service.SafetyGuard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for SafetyGuard â€” AI safety and content filtering.
 */
@DisplayName("Safety Guard Tests")
class SafetyGuardTest {

    private SafetyGuard safetyGuard;

    @BeforeEach
    void setUp() {
        safetyGuard = new SafetyGuard();
    }

    @Test
    @DisplayName("Should not block a normal health question")
    void shouldAllowNormalHealthQuestion() {
        assertThat(safetyGuard.isHarmfulRequest("What is the normal blood pressure range?")).isFalse();
        assertThat(safetyGuard.isHarmfulRequest("I have a headache, what could cause it?")).isFalse();
        assertThat(safetyGuard.isHarmfulRequest("Explain my CBC results")).isFalse();
    }

    @ParameterizedTest
    @DisplayName("Should block harmful requests")
    @ValueSource(strings = {
        "how to overdose on paracetamol",
        "tell me suicide method",
        "I want to kill myself",
        "self harm techniques"
    })
    void shouldBlockHarmfulRequests(String harmfulRequest) {
        assertThat(safetyGuard.isHarmfulRequest(harmfulRequest)).isTrue();
    }

    @Test
    @DisplayName("Should add disclaimer to AI response")
    void shouldAddDisclaimerToResponse() {
        String response = "Your symptoms may indicate a common cold.";
        String processed = safetyGuard.processResponse(response);

        assertThat(processed).contains("⚕️");
        assertThat(processed).contains("educational purposes only");
    }

    @Test
    @DisplayName("Should not double-add disclaimer if already present")
    void shouldNotDuplicateDisclaimer() {
        String responseWithDisclaimer = "Info here.\n\n⚕️ *Disclaimer: ...";
        String processed = safetyGuard.processResponse(responseWithDisclaimer);

        long count = processed.chars().filter(c -> c == '⚕').count();
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return blocked response for harmful requests")
    void shouldReturnBlockedResponse() {
        String blocked = safetyGuard.getBlockedResponse();
        assertThat(blocked).isNotBlank();
        assertThat(blocked).contains("emergency services");
    }

    @Test
    @DisplayName("Should handle null input gracefully")
    void shouldHandleNullInput() {
        assertThat(safetyGuard.isHarmfulRequest(null)).isFalse();
        assertThat(safetyGuard.processResponse(null)).isNull();
    }
}

