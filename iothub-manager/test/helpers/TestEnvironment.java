// Copyright (c) Microsoft. All rights reserved.

package helpers;

public class TestEnvironment {

    /**
     * Use this method to know whether application secrets are available, e.g.
     * during a Pull Request credentials are not.
     * Add this at the beginning of the tests that need credentials:
     * > if (!TestEnvironment.KnowsSecrets()) return;
     */
    public static boolean KnowsSecrets() {
        return !IsPullRequest();
    }

    public static boolean IsPullRequest() {
        String travisPR = System.getenv("TRAVIS_PULL_REQUEST");
        return travisPR != null && !travisPR.equalsIgnoreCase("false");
    }
}
