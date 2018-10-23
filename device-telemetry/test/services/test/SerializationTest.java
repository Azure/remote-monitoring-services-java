package services.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IActionServiceModel;
import org.junit.Test;

import java.io.IOException;

public class SerializationTest {

    private static final String PARAM_NOTES = "Chiller pressure is at 250 which is high";
    private static final String PARAM_SUBJECT = "Alert Notification";
    private static final String PARAM_RECIPIENTS = "sampleEmail@gmail.com";
    private static final String PARAM_NOTES_KEY = "Notes";
    private static final String PARAM_RECIPIENTS_KEY = "Recipients";

    @Test
    public void TestSerialization() throws IOException {
        String testString = "{\"Type\":\"Email\"," +
                "\"Parameters\":{\"Notes\":\"" + PARAM_NOTES +
                "\",\"Subject\":\"" + PARAM_SUBJECT +
                "\",\"Recipients\":[\"" + PARAM_RECIPIENTS + "\"]}}";

        ObjectMapper mapper = new ObjectMapper();
        IActionServiceModel serviceModel = mapper.readValue(testString, IActionServiceModel.class);

        IActionServiceModel.ActionType type = serviceModel.getType();
    }
}
