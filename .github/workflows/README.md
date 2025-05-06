# GXF github workflows
## build.yml
Responsible for building docker containers as artifacts and running cucumber tests.

### Run build.yml for testing
To run build.yml for testing you need to add a label to your PR containing the text cucumber_testing. You can add a label by opening your PR in the github interface and clicking labels in the right side bar according to https://docs.github.com/en/issues/using-labels-and-milestones-to-track-work/managing-labels.
Afterwards you can remove the label again.

### Error logs on cucumber test timeout
If cucumber tests time out error logs are generated and uploaded as artifacts.