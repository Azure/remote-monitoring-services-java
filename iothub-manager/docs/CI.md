Continuous Integration
======================

CI runs via [Travis CI](https://travis-ci.org) for each pull request and 
each code change.

### Preparing CI for a new project

Copy the `.travis.yml` file from the template. The file already contains 
all the steps to test both .NET and Java codebase.

Go to https://travis-ci.org and click "Add new repository" on the left "+". 
For private repositories use https://travis-ci.com instead. Note that trial 
subscriptions for private repos don't support parallelism, so builds will
take longer.

Enable CI on the desired repository.

In Travis, go to the build settings:
  * Enable "build only if .travis.yml is present"
  * Enable "limit concurrent jobs" and set the value to 1
  * Enable "build branch updates"
  * Enable "build pull request updates"

### Enabling Slack notifications

Slack already has an active token to accept notifications and forward them
to \#pcs-notifications Slack channel. Every new project needs to be enabled
manually in Travis, using the following instructions.

First of all, install Travis CI CLI: 
https://github.com/travis-ci/travis.rb#installation

Then, open a terminal in the root of the project and execute:

```
travis login --auto
travis encrypt "azureiot:...key...#pcs-notifications" --add notifications.slack
```

If you already installed Travis CLI and get some errors, try reinstalling 
Ruby and restart the terminal.

If the command is successful, it will modify `.travis.yml` which you need 
to check into the repository.

Note that the integration key is encrypted, to avoid unauthorized messages. 
The non-encrypted tokens can be found in Slack, starting from the channel 
integrations.

### Microsoft Teams notifications

Microsoft Teams doesn't support encrypted keys, so we cannot enable 
notifications on public repositories.
