package uk.gov.hmcts.futurehearings.pip.unit.testing.testsuites;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.futurehearings.pip.unit.testing.utils.TestReporter;
import uk.gov.hmcts.futurehearings.pip.unit.testing.utils.TestUtilities;
import uk.gov.hmcts.reform.demo.Application;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static uk.gov.hmcts.futurehearings.pip.unit.testing.utils.PublicationsResponseVerifier.thenValidateResponseForPostPublications;

@Slf4j
@SpringBootTest(classes = { Application.class })
@ActiveProfiles("pip-test")
@ExtendWith(TestReporter.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("POST /publications - PIP Publication Creation")
public class PostPublications {

    @Value("${publicationsApiRootContext}")
    private String publicationsApiRootContext;
    @Value("${targetInstance}")
    private String targetInstance;

    @Value("${tokenURL}")
    private String tokenUrl;

    @Value("${clientID}")
    private String clientID;

    @Value("${clientSecret}")
    private String clientSecret;

    @Value("${scope}")
    private String scope;

    @Value("${grantType}")
    private String grantType;

    private static String accessToken;

    private final Map<String, Object> headersAsMap = new HashMap<>();

    @BeforeAll
    void setToken() {
        accessToken = TestUtilities.getToken(grantType, clientID, clientSecret, tokenUrl, scope);
    }

    @BeforeEach
    void initialiseValues() {
        headersAsMap.put("Content-Type", "application/json");
    }

    @Test
    @DisplayName("Test for PIP Create Publications")
    void testInvokeGetPublications() {
        final Response response = whenGetPublicationsIsInvoked();
        thenValidateResponseForPostPublications(response);
    }

    private Response whenGetPublicationsIsInvoked() {
        return retrieveResourcesResponseForGetPublications(targetInstance, publicationsApiRootContext, headersAsMap);
    }

    private Response retrieveResourcesResponseForGetPublications(final String basePath, final String api,
            final Map<String, Object> headersAsMap) {

        return given().auth().oauth2(accessToken).headers(headersAsMap).baseUri(basePath).basePath(api).when().post()
                .then().extract().response();
    }

}
