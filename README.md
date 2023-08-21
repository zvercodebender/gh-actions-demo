# CloudBees CD/RO GitHub Actions Demontration Repository

This repository is provided to illustrate how to use [CloudBees CD/RO GitHub Actions](https://github.com/cloudbees-actions) in your GitHub Action Workflows to deploy and release applications. Included in this repository is the code and Dockerfile needed to build a very simple deployable application. The repository also includes workflows to set up a CloudBees CD/RO instance and perform deployments and releases to the Kubernetes cluster where the CloudBees CD/RO instance is running.

## What you need to run the demo workflows

- A CloudBees CD/RO instance with the appropriate port (443) reachable by the GitHub runners so that it can accept REST API calls
- An API token from the CloudBees CD/RO instance (see [Manage access tokens via the UI documentation](https://docs.beescloud.com/docs/cloudbees-cd/latest/intro/sign-in-cd#_manage_access_tokens_via_the_ui))
- A read/write repo+packages GitHub API token (see [Personal Access Tokens](https://github.com/settings/tokens))
- A Docker hub API token (Log into https://hub.docker.com/ and navigate to account settings > Security and generate a read/write token)

## Demonstration repository set up

### CloudBees CD/RO Instance Set up
Most of the CloudBees CD/RO instance set up is handled through the demonstration repository automation. There is one manual task that is needed and that is to run a DSL command through the DSL IDE:
1. Navigate the the DSL IDE in the CloudBees CD/RO UI (left-hand menu > DevOps Essentials > DSL IDE)
2. Copy the CloudBees CD/RO server FQDN or IP address (what follows `https://` and preceeds the following `/`)
3. Run the following DSL from the DSL IDE, replacing the text `gha.cb-demos.io` with your FQDN or IP address: `property "/server/app_base_hostname", value: "gha.cb-demos.io"`. The green button to the right is used to apply the DSL code in the editor.

### Demonstration instance set up
1. Navigate to the demonstration repostitory, https://github.com/cloudbees-demos/gh-actions-demo
2. Press the "Use this template" button to create a new repository in an organization you have write access to
3. Navigate to Settings > Secrets and variables > Actions add the following respository secrets:
    - `CDRO_URL`, for example https://my-cdro-server.example.com
    - `CDRO_TOKEN`
    - `DOCKERHUB_USER`
    - `DOCKERHUB_TOKEN`
    - `GH_PAT`
4. Navigate to the Actions tab
5. Run the "Setup and CD config" Action using the "Run workflow" dropdown button
6. Once that Action is completed, run the “Build and Deploy” to to initialize the application in the `demo-dev` environment

Steps 1-6 can be scripted. You'll need the GitHub command line tool, `gl` (See https://cli.github.com/). Edit the repository file [GitHubSetup.sh](GitHubSetup.sh) with your values and the run the script from a shell that has been authenticated to GitHub.

## Working with the demonstration
At this point CloudBees CD/RO should have deployed the application to the `demo-dev` environment (`demo-dev` namespace in the CloudBees CD/RO Kubernetes Instance). Navigate to the application run to see the results:
1. Using the left hand navigation menu, select Deployment Automation > Applications
2. There you should see the Application model "Demo App", select the link to view the model
3. Select the "Application process run" tab to view a listing of deployments, select the deployment link
4. Note in the upper right corner there are two link
  1. Demo App - This like will take you to the `demo-dev`-deployed application
  2. GitHub Action Workflow URL - This will take you to the workflow run the kicked off this deployment
5. Using the left hand navigation menu, select Deployment Automation > Environment Inventory to view details about what was deployed, namely the image `demo` and  its version to the `demo-dev` environment.
6. Using the left hand navigation menu, select Analytics > Dashboards > Application Deployments to view the deployment report

If a change is made and pushed to the repository, the application image will be rebuilt by the workflow and a CloudBees CD/RO Release will be initiated to manage the staged release to `demo-prod`.
1. Edit the README.md file by adding a blank line, for example, and commit to `main`. This should start the “Build and Deploy” workflow again, but this time it will create a release model and start it with the new application image.
2. Using the left hand navigation menu, select Release Orchestration > Releases
3. There you should see a Release model called "Simple Release <SHA>", select the link to view the model
4. Select the Pipeline runs tab to view the Release Pipeline run
5. Once the first stage has completed, it is ready for approval to promote the application to `demo-prod`. Go ahead to approve the manual task.
6. Select the Path to product to view the status of the release artifacts
7. Once the `demo-prod` deployment has completed, examine the Environment Inventory once again
8. 
