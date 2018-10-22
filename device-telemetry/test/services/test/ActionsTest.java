package services.test;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.EmailAction;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.actions.IAction;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.ActionApiModel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ActionsTest {

    private static final String PARAM_SUBJECT_KEY = "Subject";
    private static final String PARAM_SUBJECT_VALUE = "Alert Notification";
    private static final String PARAM_NOTES_KEY = "Notes";
    private static final String PARAM_NOTES_VALUE = "Chiller pressure is at 250 which is high";
    private static final String PARAM_RECIPIENTS_KEY = "Recipients";
    private static final String PARAM_RECIPIENTS_VALUE = "sampleEmail@gmail.com";
    private static final String PARAM_ACTION_TYPE = "Email";

    @Test
    public void ShouldReturnActionModelWhenValidActionType() throws InvalidInputException {
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
        assertEquals(IAction.ActionType.Email, emailAction.getType());
        assertEquals(PARAM_NOTES_VALUE, emailAction.getParameters().get(PARAM_NOTES_KEY));
        assertTrue(emailAction.getParameters().containsKey(PARAM_RECIPIENTS_KEY));
    }

    @Test(expected = InvalidInputException.class)
    public void ShouldThrowInvalidInputException_WhenActionTypeIsEmailAndInvalidEmail() throws InvalidInputException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT_VALUE);
        parameters.put(PARAM_NOTES_KEY, PARAM_NOTES_VALUE);

        ArrayList<String> list = new ArrayList<>();
        list.add("sampleEmailgmail.com");
        parameters.put(PARAM_RECIPIENTS_KEY, list);

        EmailAction model = new EmailAction(IAction.ActionType.Email, parameters);
    }

    @Test(expected = InvalidInputException.class)
    public void ShouldThrowInvalidInputException_WhenActionTypeIsEmailandNoEmailField() throws InvalidInputException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT_VALUE);
        parameters.put(PARAM_NOTES_KEY, PARAM_NOTES_VALUE);

        EmailAction model = new EmailAction(IAction.ActionType.Email, parameters);
    }

    @Test(expected = Exception.class)
    public void ShouldThrowException_WhenActionTypeIsEmailandEmailIsString() throws InvalidInputException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT_VALUE);
        parameters.put(PARAM_NOTES_KEY, PARAM_NOTES_VALUE);
        parameters.put(PARAM_RECIPIENTS_KEY, PARAM_RECIPIENTS_VALUE);

        EmailAction model = new EmailAction(IAction.ActionType.Email, parameters);
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
        IAction returnedModel = model.toServiceModel();
        assertEquals(this.doesEmailServiceModelCorrespondToApiModel(model, returnedModel), true);
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
        IAction returnedModel = model.toServiceModel();
    }

    private Boolean doesEmailServiceModelCorrespondToApiModel(ActionApiModel apiModel, IAction serviceModel) {
        Boolean equalTemplate = apiModel.getParameters().get(PARAM_NOTES_KEY).equals(serviceModel.getParameters().get(PARAM_NOTES_KEY));
        Boolean equalSubject = apiModel.getParameters().get(PARAM_SUBJECT_KEY).equals(serviceModel.getParameters().get(PARAM_SUBJECT_KEY));
        Boolean correctType = serviceModel.getType().toString().equals(PARAM_ACTION_TYPE);
        return equalTemplate && equalSubject && correctType;
    }

    private Boolean isListOfEmailEqual(List<String> emailList) {
        ArrayList<String> checkList = new ArrayList<>();
        checkList.add(PARAM_RECIPIENTS_VALUE);
        for (String email : checkList) {
            if (!emailList.contains(email)) {
                return false;
            }
        }
        return true;
    }
}
