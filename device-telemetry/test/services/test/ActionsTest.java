package services.test;

import com.microsoft.azure.iotsolutions.devicetelemetry.services.exceptions.InvalidInputException;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.EmailServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.IActionServiceModel;
import com.microsoft.azure.iotsolutions.devicetelemetry.webservice.v1.models.ActionApiModel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ActionsTest {

    private static final String PARAM_TEMPLATE = "Chiller pressure is at 250 which is high";
    private static final String PARAM_SUBJECT = "Alert Notification";
    private static final String PARAM_EMAIL = "sampleEmail@gmail.com";
    private static final String PARAM_SUBJECT_KEY = "Subject";
    private static final String PARAM_TEMPLATE_KEY = "Template";
    private static final String PARAM_EMAIL_KEY = "Email";

    @Test
    public void ShouldReturnActionModelWhenValidActionType() throws InvalidInputException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT);
        parameters.put(PARAM_TEMPLATE_KEY, PARAM_TEMPLATE);

        ArrayList<String> list = new ArrayList<>();
        list.add(PARAM_EMAIL);
        parameters.put(PARAM_EMAIL_KEY, list);

        EmailServiceModel targetAction = new EmailServiceModel(IActionServiceModel.Type.Email, parameters);
        assertEquals(this.isEmailServiceModelReadProperly(targetAction), true);
    }

    @Test(expected = InvalidInputException.class)
    public void ShouldThrowInvalidInputExceptionWhenActionTypeIsEmailAndInvalidEmail() throws InvalidInputException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT);
        parameters.put(PARAM_TEMPLATE_KEY, PARAM_TEMPLATE);

        ArrayList<String> list = new ArrayList<>();
        list.add("sampleEmailgmail.com");
        parameters.put(PARAM_EMAIL_KEY, list);

        EmailServiceModel model = new EmailServiceModel(IActionServiceModel.Type.Email, parameters);
    }

    @Test(expected = InvalidInputException.class)
    public void ShouldThrowInvalidInputExceptionWhenActionTypeIsEmailandNoEmailField() throws InvalidInputException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT);
        parameters.put(PARAM_TEMPLATE_KEY, PARAM_TEMPLATE);

        EmailServiceModel model = new EmailServiceModel(IActionServiceModel.Type.Email, parameters);
    }

    @Test(expected = Exception.class)
    public void ShouldThrowExceptionWhenActionTypeIsEmailandEmailIsString() throws InvalidInputException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT);
        parameters.put(PARAM_TEMPLATE_KEY, PARAM_TEMPLATE);
        parameters.put(PARAM_EMAIL_KEY, PARAM_EMAIL);

        EmailServiceModel model = new EmailServiceModel(IActionServiceModel.Type.Email, parameters);
    }

    @Test
    public void ShouldReturnProperServiceModelForApiModelToServiceModelCallWhenValidActionType() throws InvalidInputException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT);
        parameters.put(PARAM_TEMPLATE_KEY, PARAM_TEMPLATE);

        ArrayList<String> list = new ArrayList<>();
        list.add(PARAM_EMAIL);
        parameters.put(PARAM_EMAIL_KEY, list);

        ActionApiModel model = new ActionApiModel(PARAM_EMAIL_KEY, parameters);
        IActionServiceModel returnedModel = model.toServiceModel();
        assertEquals(this.doesEmailServiceModelCorrespondToApiModel(model, returnedModel), true);
    }

    @Test(expected = InvalidInputException.class)
    public void ShouldThrowInvalidInputExceptionOnToServiceModelWhenInvalidActionType() throws InvalidInputException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_SUBJECT_KEY, PARAM_SUBJECT);
        parameters.put(PARAM_TEMPLATE_KEY, PARAM_TEMPLATE);

        ArrayList<String> list = new ArrayList<>();
        list.add(PARAM_EMAIL);
        parameters.put(PARAM_EMAIL_KEY, list);

        ActionApiModel model = new ActionApiModel("", parameters);
        IActionServiceModel returnedModel = model.toServiceModel();
    }

    private Boolean doesEmailServiceModelCorrespondToApiModel(ActionApiModel apiModel, IActionServiceModel serviceModel){
        Boolean equalTemplate = apiModel.getParameters().get(PARAM_TEMPLATE_KEY).equals(serviceModel.getParameters().get(PARAM_TEMPLATE_KEY));
        Boolean equalSubject = apiModel.getParameters().get(PARAM_SUBJECT_KEY).equals(serviceModel.getParameters().get(PARAM_SUBJECT_KEY));
        Boolean correctType = serviceModel.getType().toString().equals(PARAM_EMAIL_KEY);
        return equalTemplate && equalSubject && correctType;
    }

    private Boolean isEmailServiceModelReadProperly(EmailServiceModel emailServiceModel){
        return emailServiceModel.getType() == IActionServiceModel.Type.Email
                && emailServiceModel.getParameters().get(PARAM_TEMPLATE_KEY).equals(PARAM_TEMPLATE)
                && this.isListOfEmailEqual((List<String>) emailServiceModel.getParameters().get(PARAM_EMAIL_KEY));
    }

    private Boolean isListOfEmailEqual(List<String> emailList){
        ArrayList<String> checkList = new ArrayList<>();
        checkList.add(PARAM_EMAIL);
        for(String email : checkList){
            if(!emailList.contains(email)){
                return false;
            }
        }
        return true;
    }
}
