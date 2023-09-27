# CloudBees CD/RO GitHub Actions Demonstration Repository
This repository illustrates the use of [CloudBees CD/RO GitHub Actions](https://github.com/marketplace?type=actions&query=cloudbees-github-actions+) in your GitHub Action Workflows to deploy and release applications. This repository includes is the code and Dockerfile needed to build a very simple deployable application. The repository also includes workflows to set up a CloudBees CD/RO instance and perform deployments and releases to the Kubernetes cluster where the CloudBees CD/RO instance is running.
## What you need to run the demo workflows
- A CloudBees CD/RO instance with the appropriate port (443) reachable by the GitHub runners so that it can accept REST API calls
- An API token from the CloudBees CD/RO instance (see [Manage access tokens via the UI documentation](https://docs.beescloud.com/docs/cloudbees-cd/latest/intro/sign-in-cd#_manage_access_tokens_via_the_ui))
- A read/write repo+packages GitHub API token (see [Personal Access Tokens](https://github.com/settings/tokens))
- A Docker hub API token (Log into https://hub.docker.com/ and navigate to account settings > Security and generate a read/write token)
## CloudBees CD/RO Instance Set up
Most of the CloudBees CD/RO instance setup is handled through the demonstration repository automation. It may be necessary to update a property value so that the deployed application can be easily accessed through a job link.
1. Open and login to the CloudBees CD/RO user interface from a web browser
2. From the left hand menu, navigate to Administration > Server Properties
3. Examine the value set for `app_base_hostname`
4. If this does not matches your CloudBees CD/RO hostname, edit the property to make it so. The edit command is available from the right hand menu.
5. Alternatively, you can set this property through the DSL IDE. Navigate to the DSL IDE from the left hand menu under DevOps Essentials, copy the text `property "/server/app_base_hostname", value: "gha.cb-demos.io"` to the editor, replacing `gha.cb-demos.io` with your instance's FQDN hostname, then press the green button to the right to apply the DSL code.
## Demonstration instance set up
1. Navigate to the demonstration repository, https://github.com/cloudbees-demos/gh-actions-demo
2. Press the "Use this template" button to create a new repository in an organization you have write access to
3. Navigate to Settings > Secrets and variables > Actions and add the following repository secrets:
    - `CDRO_URL`, for example, https://my-cdro-server.example.com
    - `CDRO_TOKEN`
    - `DOCKERHUB_USER`
    - `DOCKERHUB_TOKEN`
    - `GH_PAT`
4. Navigate to the Actions tab
5. Run the "Setup and CD config" Action using the "Run workflow" dropdown button
6. Once that Action is completed, run the “Build and Deploy” to initialize the application in the `demo-dev` environment
Steps 1-6 can be scripted. You'll need the GitHub command line tool, `gl` (See https://cli.github.com/). Edit the repository file [GitHubSetup.sh](GitHubSetup.sh) with your values and run the script from a shell that has been authenticated to GitHub.
## Working with the demonstration
At this point, CloudBees CD/RO should have deployed the application to the `demo-dev` environment (`demo-dev` namespace in the CloudBees CD/RO Kubernetes Instance). Navigate to the application run to see the results:
1. Using the left-hand navigation menu, select Deployment Automation > Applications
2. There, you should see the Application model "Demo App", select the link to view the model
3. Select the "Application process run" tab to view a listing of deployments, select the deployment link
4. Note in the upper right corner, there are two links
  1. Demo App - This like will take you to the `demo-dev`-deployed application
  2. GitHub Action Workflow URL - This will take you to the workflow run that kicked off this deployment
5. Using the left-hand navigation menu, select Deployment Automation > Environment Inventory to view details about what was deployed, namely the image `demo` and its version to the `demo-dev` environment.
6. Using the left-hand navigation menu, select Analytics > Dashboards > Application Deployments to view the deployment report
If a change is made and pushed to the repository, the workflow will rebuild the application image, and a CloudBees CD/RO Release will be initiated to manage the staged release to `demo-prod`.
1. Edit the [README.md](README.md) file by adding a blank line, for example, and commit to `main`. This should start the “Build and Deploy” workflow again, but this time it will create a release model and start it with the new application image.
2. Using the left-hand navigation menu, select Release Orchestration > Releases
3. There you should see a Release model called "Simple Release <SHA>", select the link to view the model
4. Select the Pipeline runs tab to view the Release Pipeline run
5. Once the first stage has been completed, it is ready for approval to promote the application to `demo-prod.`
6. Refresh the application window to see the newly deployed version of the application
7. Go ahead to approve the manual task.
8. Select the Path to product to view the status of the release artifacts
9. Once the `demo-prod` deployment has been completed, examine the Environment Inventory once again
10. You can navigate to the `demo-prod` deployment through the Release Pipeline run Production stage deployment task; there, you can view the newly deployed application in `demo-prod`.
## Security Considerations
- Secrets added to the repository according to the instructions above will be pushed to and used by the CloudBees CD/RO instance to establish authenticated sessions with Docker Hub and GitHub. The secret values are managed as _credentials_ in CloudBees CD/RO. Credentials are encrypted before they are stored in the system. However, users with sufficient access rights on the system can retrieve the credential values.
- By default, SSL certificated are ignored to support self-signed certificate CloudBees CD/RO instances. If this is not desired, update the files [cd-setup.yaml](.github/workflows/cd-setup.yaml) and [build-and-deploy.yml](.github/workflows/build-and-deploy.yml), replacing "true" with false in all the `ignore-unverified-cert` fields.

