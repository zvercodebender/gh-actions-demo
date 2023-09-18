property "/server/app_base_hostname", value: args["cdro_url"].split("//")[1]

project 'GHA', {
  environment 'demo-dev', {
    projectName = 'GHA'

    cluster 'demo-dev', {
      definitionParameter = [
        'config': '/projects/GHA/pluginConfigurations/helm',
        'namespace': 'demo-dev',
      ]
      environmentName = 'demo-dev'
      pluginKey = 'EC-Helm'
    }

    utilityResource 'local', {
      resourceName = 'local'
    }

    // Custom properties
    domain_identifier = 'dev'
  }

  environment 'demo-prod', {
    projectName = 'GHA'

    cluster 'demo-prod', {
      definitionParameter = [
        'config': '/projects/GHA/pluginConfigurations/helm',
        'namespace': 'demo-prod',
      ]
      environmentName = 'demo-prod'
      pluginKey = 'EC-Helm'
    }

    utilityResource 'local', {
      resourceName = 'local'
    }

    // Custom properties
    domain_identifier = 'prod'
  }

  application 'Demo App', {
    applicationType = 'microservice'

    microservice 'Demo App', {
      applicationName = 'Demo App'
      definitionSource = 'git'
      definitionSourceParameter = [
        'branch': 'main',
        'config': '/projects/GHA/pluginConfigurations/github',
        'repoUrl': args.repoUrl,
      ]
      definitionType = 'helm'
      deployParameter = [
        'additionalOptions': '--create-namespace',
        'chart': './chart',
        'releaseName': 'demo-app',
        'values': '''ingress:
  enabled: true
  annotations:
    kubernetes.io/ingress.class: nginx
    kubernetes.io/tls-acme: "true"
  hostname: demo.$[/myEnvironment/domain_identifier].$[/server/app_base_hostname]

image:
  repository: $[imageRepository]
  pullPolicy: IfNotPresent
  tag: $[imageTag]

environment: $[/myEnvironment/domain_identifier]

serviceAccount:
  create: false''',
        'version': '',
      ]
      projectName = 'GHA'
      rollbackParameter = [
        'rollbackEnabled': 'false',
        'waitTimeout': '300',
      ]

      process 'Deploy Microservice Process', {
        description = 'System generated process for microservice deployment'
        microserviceName = 'Demo App'
        processType = 'DEPLOY'

        processStep 'Retrieve Artifact', {
          description = 'System generated step to retrieve microservice definition artifact'
          processStepType = 'plugin'
          subprocedure = 'Source Provider'
          subproject = '/plugins/EC-Git/project'
        }

        processStep 'Deploy Microservice', {
          description = 'System generated step to deploy microservice'
          processStepType = 'plugin'
          subprocedure = 'Deploy Service'
          subproject = '/plugins/EC-Helm/project'
        }

        processStep 'Update properties', {
          description = 'Set the summary values'
          processStepType = 'command'
          actualParameter = [
          'commandToRun': '''ectool setProperty "/myJob/report-urls/Demo App" "http://demo.$[/myEnvironment/domain_identifier].$[/server/app_base_hostname]"

    $[/javascript
                                        // Only run in Pipeline context
                                        getProperty("/myStageRuntime")?"":"#"
                                    ] ectool setProperty "/myStageRuntime/ec_summary/Demo App" "<html><a target="_blank" href=\'http://demo.$[/myEnvironment/domain_identifier].$[/server/app_base_hostname]\'>View deployed app</a></html>"''',
          ]
          subprocedure = 'RunCommand'
          subproject = '/plugins/EC-Core/project'
        }

        processDependency 'Retrieve Artifact', targetProcessStepName: 'Deploy Microservice'
      }
    }

    process 'Deploy Application', {
      description = 'System generated process for microservice application'
      applicationName = 'Demo App'
      processType = 'DEPLOY'

      formalParameter 'imageTag', defaultValue: 'latest', {
        label = 'Image Tag'
        orderIndex = '1'
        required = '1'
        type = 'entry'
      }

      formalParameter 'imageRepository', defaultValue: args.dockerHubUser + '/demo-app', {
        label = 'Image Repository'
        orderIndex = '1'
        required = '1'
        type = 'entry'
      }

      formalParameter 'ec_Demo App-run', defaultValue: '1', {
        expansionDeferred = '1'
        type = 'checkbox'
      }

      formalParameter 'ec_rolloutApprovers', defaultValue: '', {
        expansionDeferred = '1'
        type = 'assigneeList'
      }

      formalParameter 'ec_rolloutNotificationEnabled', defaultValue: '0', {
        expansionDeferred = '1'
        type = 'checkbox'
      }

      processStep 'Demo App', {
        description = 'System generated step to invoke microservice process'
        processStepType = 'process'
        submicroservice = 'Demo App'
        submicroserviceProcess = 'Deploy Microservice Process'
        useUtilityResource = '1'

        // Custom properties

        property 'ec_deploy', {

          // Custom properties
          ec_notifierStatus = '0'
        }
      }

      // Custom properties

      property 'ec_deploy', {

        // Custom properties
        ec_notifierStatus = '0'
      }
    }

    tierMap 'ee27fbc4-d015-f1f5-8bec-daf345ab12c5', {
      applicationName = 'Demo App'
      environmentName = 'demo-dev'
      environmentProjectName = 'GHA'
      projectName = 'GHA'

      microserviceMapping 'ee27fc10-1ecd-f147-89d9-daf345ab12c5', {
        clusterName = 'demo-dev'
        clusterProjectName = 'GHA'
        microserviceName = 'Demo App'
        tierMapName = 'ee27fbc4-d015-f1f5-8bec-daf345ab12c5'
      }
    }

    tierMap 'ee27fbd1-4468-f1e1-bb21-daf345ab12c5', {
      applicationName = 'Demo App'
      environmentName = 'demo-prod'
      environmentProjectName = 'GHA'
      projectName = 'GHA'

      microserviceMapping 'ee27fc10-2276-f1c9-89d9-daf345ab12c5', {
        clusterName = 'demo-prod'
        clusterProjectName = 'GHA'
        microserviceName = 'Demo App'
        tierMapName = 'ee27fbd1-4468-f1e1-bb21-daf345ab12c5'
      }
    }

    // Custom properties

    property 'ec_deploy', {

      // Custom properties
      ec_notifierStatus = '0'
    }

    property 'jobCounter', value: '2', {
      suppressValueTracking = '1'
    }
  }
}
