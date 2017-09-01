package com.microsoft.azure.iotsolutions.storageadapter.webservice.filters;

import com.microsoft.azure.documentdb.DocumentClientException;
import com.microsoft.azure.iotsolutions.storageadapter.webservice.v1.exceptions.BadRequestException;
import play.http.HttpErrorHandler;
import play.mvc.Http.RequestHeader;
import play.mvc.Http.Status;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

@Singleton
public class ErrorHandler implements HttpErrorHandler {

    public CompletionStage<Result> onClientError(RequestHeader request, int statusCode, String message) {
        String errorMessage;
        switch (statusCode) {
            case 400:
                errorMessage = "BadRequest";
                break;
            case 403:
                errorMessage = "Forbidden";
                break;
            case 404:
                errorMessage = "NotFound";
                break;
            default:
                errorMessage = "OtherClientError";
        }
        HashMap<String, Object> errorResult = new HashMap<String, Object>() {
            {
                put("Message", "Client error occurred.");
                put("ExceptionMessage", errorMessage);
            }
        };
        return CompletableFuture.completedFuture(
                Results.status(statusCode, toJson(errorResult))
        );
    }

    public CompletionStage<Result> onServerError(RequestHeader request, Throwable e) {
        String errorMessage;
        int errorCode;
        if (e instanceof DocumentClientException) {
            DocumentClientException ex = (DocumentClientException) e;
            errorCode = ex.getStatusCode();
            switch (errorCode) {
                case Status.NOT_FOUND:
                    errorMessage = "The resource requested doesn't exist.";
                    break;
                case Status.CONFLICT:
                    errorMessage = "There is already a key with the Id specified.";
                    break;
                case Status.PRECONDITION_FAILED:
                    errorMessage = "ETag mismatch: the resource has been updated by another client.";
                    break;
                default:
                    errorMessage = ex.getMessage();
                    break;
            }
        } else if (e instanceof BadRequestException) {
            errorCode = Status.BAD_REQUEST;
            errorMessage = e.getMessage();
        } else {
            errorCode = Status.INTERNAL_SERVER_ERROR;
            errorMessage = e.getMessage();
        }

        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put("Message", "An error has occurred.");
        errorResult.put("ExceptionType", e.getClass().getName());
        errorResult.put("ExceptionMessage", errorMessage);
        //No StackTrace.
        if (false) {
            errorResult.put("StackTrace", e.getStackTrace());
            Throwable innerException = e.getCause();
            if (innerException != null) {
                errorResult.put("InnerExceptionMessage", innerException.getMessage());
                errorResult.put("InnerExceptionType", innerException.getClass().getName());
                errorResult.put("InnerExceptionStackTrace", innerException.getStackTrace());
            }
        }
        return CompletableFuture.completedFuture(
                Results.status(errorCode, toJson(errorResult))
        );
    }
}
