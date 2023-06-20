def imageTag = args.imageTag

runProcess (
  applicationName: "Simple Insurance",
  processName: "Deploy Application",
  projectName: "ldonley Demo",
  environmentName: "Insurance QA",
  actualParameter: [ imageTag: imageTag ]
)
