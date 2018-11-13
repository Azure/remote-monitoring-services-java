package com.microsoft.azure.iotsolutions.uiconfig.webservice.controllers;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.microsoft.azure.iotsolutions.uiconfig.services.IStorage;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.BaseException;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.PackageConfigType;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.PackageType;
import com.microsoft.azure.iotsolutions.uiconfig.services.models.Package;
import com.microsoft.azure.iotsolutions.uiconfig.webservice.v1.controllers.PackageController;
import helpers.Random;
import helpers.UnitTest;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public class PackageControllerTest {

    private IStorage mockStorage;
    private PackageController controller;
    private Random rand;
    private final string DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:sszzz";

    @Before
    public void setUp() {
        mockStorage = Mockito.mock(IStorage.class);
        rand = new Random();
    }

    @Category({UnitTest.class})
    public void getAllPackageTest() throws BaseException, ExecutionException, InterruptedException
    {
        // Arrange
        final String id = "packageId";
        final String name = "packageName";
        final PackageType type = PackageType.edgeManifest;
        String config = "";
        final String content = "{}";
        //String dateCreated = DateTime.UtcNow.ToString(DATE_FORMAT);

        Package[] packages = new Package[3];

        IntStream.range(0, packages.length)
                .forEach(i -> { packages[i] = new Package(
                       Integer.toString(i),
                       Integer.toString(i),
                       PackageType.deviceConfiguration,
                       config+i,
                       content
                    );
                });
    }


}
