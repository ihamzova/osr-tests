package cucumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsystems.tm.acc.ta.domain.OsrTestContext;

public class TestContext {

    private final OsrTestContext osrTestContext = OsrTestContext.get();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScenarioContext scenarioContext = new ScenarioContext();

    public OsrTestContext getOsrTestContext() {
        return osrTestContext;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public ScenarioContext getScenarioContext() {
        return scenarioContext;
    }

}
