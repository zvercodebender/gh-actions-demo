# Fill in the following six value with your information
NEW_REPO=FILLINVALUE #<the name of the demo repo copy, e.g., myor/myrepo>
CDRO_URL=FILLINVALUE #<your cdro instance url, e.g., https://cdro.example.com>
GH_PAT=FILLINVALUE #<your CDRO API token>
DOCKERHUB_USER=FILLINVALUE #<your Docker hub user name>
DOCKERHUB_TOKEN=FILLINVALUE #<your Docker hub r/w API token>
CDRO_TOKEN=FILLINVALUE #<your GitHub r/w API token>

gh repo create $NEW_REPO --template cloudbees-demos/gh-actions-demo --private
sleep 30
gh --repo $NEW_REPO secret set CDRO_URL --body $CDRO_URL
gh --repo $NEW_REPO secret set CDRO_TOKEN --body $CDRO_TOKEN
gh --repo $NEW_REPO secret set DOCKERHUB_USER --body $DOCKERHUB_USER
gh --repo $NEW_REPO secret set DOCKERHUB_TOKEN --body $DOCKERHUB_TOKEN
gh --repo $NEW_REPO secret set GH_PAT --body $GH_PAT

gh --repo $NEW_REPO workflow run "Setup CD config"
sleep 30
gh --repo $NEW_REPO workflow run "Build and deploy"
