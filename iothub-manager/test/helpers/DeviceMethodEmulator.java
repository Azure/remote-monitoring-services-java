// Copyright (c) Microsoft. All rights reserved.

package helpers;

import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodData;

public class DeviceMethodEmulator {
    private static final int METHOD_SUCCESS = 200;
    private static final int METHOD_NOT_DEFINED = 404;

    public static int rebootMethod(Object data) {
        System.out.println("invoking reboot method on this device");
        return METHOD_SUCCESS;
    }

    public static int defaultMethod(Object data) {
        System.out.println("invoking default method for this device");
        return METHOD_NOT_DEFINED;
    }

    public static class DeviceMethodStatusCallBack implements IotHubEventCallback {
        public void execute(IotHubStatusCode status, Object context) {
            System.out.println("IoT Hub responded to device method operation with status " + status.name());
        }
    }

    public static class DeviceMethodCallback implements com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodCallback {
        @Override
        public DeviceMethodData call(String methodName, Object methodData, Object context) {
            DeviceMethodData deviceMethodData;
            switch (methodName) {
                case "Reboot": {
                    int status = rebootMethod(methodData);
                    deviceMethodData = new DeviceMethodData(status, methodName + " accepted");
                    break;
                }
                default: {
                    int status = defaultMethod(methodData);
                    deviceMethodData = new DeviceMethodData(status, methodName + " rejected");
                }
            }

            return deviceMethodData;
        }
    }
}