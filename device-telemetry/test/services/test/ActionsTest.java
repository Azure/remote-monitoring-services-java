package services.test;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.ActionType;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailAction;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IAction;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.ActionApiModel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ActionsTest {

    private static final String PARAM_SUBJECT_KEY = "Subject";
    private static final String PARAM_SUBJECT_VALUE = "Alert ActionManager";
    private static final String PARAM_NOTES_KEY = "Notes";
    private static final String PARAM_NOTES_VALUE = "Chiller pressure is at 250 which is high";
    private static final String PARAM_RECIPIENTS_KEY = "Recipients";
    private static final String PARAM_RECIPIENTS_VALUE = "sampleEmail@gmail.com";
    private static final String PARAM_ACTION_TYPE = "Email";

    @Test
    public void Should_ReturnActionModel_When_ValidActionType() throws InvalidInputException {
        // Arrange
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT_VALUE);
        parameters.put(PARAM_NOTES_KEY, PARAM_NOTES_VALUE);

        ArrayList<String> list = new ArrayList<>();
        list.add(PARAM_RECIPIENTS_VALUE);
        parameters.put(PARAM_RECIPIENTS_KEY, list);

        // Act
        EmailAction emailAction = new EmailAction(parameters);

        // Assert
        assertEquals(ActionType.Email, emailAction.getType());
        assertEquals(PARAM_NOTES_VALUE, emailAction.getParameters().get(PARAM_NOTES_KEY));
        assertTrue(emailAction.getParameters().containsKey(PARAM_RECIPIENTS_KEY));
    }

    @Test(expected = InvalidInputException.class)
    public void Should_ThrowInvalidInputException_When_ActionTypeIsEmailAndInvalidEmail() throws InvalidInputException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT_VALUE);
        parameters.put(PARAM_NOTES_KEY, PARAM_NOTES_VALUE);

        ArrayList<String> list = new ArrayList<>();
        list.add("sampleEmailgmail.com");
        parameters.put(PARAM_RECIPIENTS_KEY, list);

        EmailAction emailAction = new EmailAction(ActionType.Email, parameters);
    }

    @Test(expected = InvalidInputException.class)
    public void ShouldThrowInvalidInputException_WhenActionTypeIsEmailandNoEmailField() throws InvalidInputException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT_VALUE);
        parameters.put(PARAM_NOTES_KEY, PARAM_NOTES_VALUE);

        EmailAction emailAction = new EmailAction(ActionType.Email, parameters);
    }

    @Test(expected = Exception.class)
    public void ShouldThrowException_WhenActionTypeIsEmailandEmailIsString() throws InvalidInputException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT_VALUE);
        parameters.put(PARAM_NOTES_KEY, PARAM_NOTES_VALUE);
        parameters.put(PARAM_RECIPIENTS_KEY, PARAM_RECIPIENTS_VALUE);

        EmailAction emailAction = new EmailAction(ActionType.Email, parameters);
    }

    @Test
    public void ShouldReturnProperServiceModelForApiModelToServiceModelCall_WhenValidActionType() throws InvalidInputException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT_VALUE);
        parameters.put(PARAM_NOTES_KEY, PARAM_NOTES_VALUE);

        ArrayList<String> list = new ArrayList<>();
        list.add(PARAM_RECIPIENTS_VALUE);
        parameters.put(PARAM_RECIPIENTS_KEY, list);

        ActionApiModel model = new ActionApiModel(PARAM_ACTION_TYPE, parameters);
        IAction action = model.toServiceModel();
        assertEquals(model.getParameters().get(PARAM_NOTES_KEY), action.getParameters().get(PARAM_NOTES_KEY));
        assertEquals(model.getParameters().get(PARAM_SUBJECT_KEY), action.getParameters().get(PARAM_SUBJECT_KEY));
        assertEquals(model.getType(), PARAM_ACTION_TYPE);
    }

    @Test(expected = InvalidInputException.class)
    public void ShouldThrowInvalidInputExceptionOnToServiceModel_WhenInvalidActionType() throws InvalidInputException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT_VALUE);
        parameters.put(PARAM_NOTES_KEY, PARAM_NOTES_VALUE);

        ArrayList<String> list = new ArrayList<>();
        list.add(PARAM_RECIPIENTS_VALUE);
        parameters.put(PARAM_RECIPIENTS_KEY, list);

        ActionApiModel model = new ActionApiModel("", parameters);
        IAction action = model.toServiceModel();
    }
}
