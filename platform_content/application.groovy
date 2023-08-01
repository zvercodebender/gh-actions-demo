project 'GHA', {
  environment 'insurance-dev', {
    projectName = 'GHA'

    cluster 'insurance-dev', {
      definitionParameter = [
        'config': '/projects/GHA/pluginConfigurations/helm',
        'namespace': 'insurance-dev',
      ]
      environmentName = 'insurance-dev'
      pluginKey = 'EC-Helm'
    }

    utilityResource 'k8s-agent', {
      resourceName = 'k8s-agent'
    }

    // Custom properties
    domain_identifier = 'simple-dev'
  }

  environment 'insurance-prod', {
    projectName = 'GHA'

    cluster 'insurance-prod', {
      definitionParameter = [
        'config': '/projects/GHA/pluginConfigurations/helm',
        'namespace': 'insurance-prod',
      ]
      environmentName = 'insurance-prod'
      pluginKey = 'EC-Helm'
    }

    utilityResource 'k8s-agent', {
      resourceName = 'k8s-agent'
    }

    // Custom properties
    domain_identifier = 'simple-prod'
  }

  application 'Simple Insurance', {
    applicationType = 'microservice'

    microservice 'Insurance Frontend', {
      applicationName = 'Simple Insurance'
      definitionSource = 'git'
      definitionSourceParameter = [
        'branch': 'main',
        'config': '/projects/GHA/pluginConfigurations/github',
        'repoUrl': 'https://github.com/cloudbees-demos/gh-actions-demo',
      ]
      definitionType = 'helm'
      deployParameter = [
        'additionalOptions': '--create-namespace',
        'chart': './chart',
        'releaseName': 'simple-insurance',
        'values': '''ingress:
  enabled: true
  annotations:
    kubernetes.io/ingress.class: nginx
    kubernetes.io/tls-acme: "true"
  hosts:
    - host: simple-insurance.$[/myEnvironment/domain_identifier].$[hostName]
      paths:
        - path: /
          pathType: ImplementationSpecific

image:
  repository: $[imageRepository]
  pullPolicy: IfNotPresent
  tag: $[imageTag]

backendUrl: https://insurance-backend.$[/myEnvironment/domain_identifier].example.com
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
        microserviceName = 'Insurance Frontend'
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

        processDependency 'Retrieve Artifact', targetProcessStepName: 'Deploy Microservice'
      }
    }

    process 'Deploy Application', {
      description = 'System generated process for microservice application'
      applicationName = 'Simple Insurance'
      processType = 'DEPLOY'

      formalParameter 'imageTag', defaultValue: 'latest', {
        label = 'Image Tag'
        orderIndex = '1'
        required = '1'
        type = 'entry'
      }

      formalParameter 'imageRepository', defaultValue: 'ldonleycb/insurance-action', {
        label = 'Image Repository'
        orderIndex = '1'
        required = '1'
        type = 'entry'
      }

      formalParameter 'hostName', defaultValue: 'example.com', {
        label = 'Host name'
        orderIndex = '1'
        required = '1'
        type = 'entry'
      }

      formalParameter 'ec_Insurance Frontend-run', defaultValue: '1', {
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

      processStep 'Insurance Frontend', {
        description = 'System generated step to invoke microservice process'
        processStepType = 'process'
        submicroservice = 'Insurance Frontend'
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
      applicationName = 'Simple Insurance'
      environmentName = 'insurance-dev'
      environmentProjectName = 'GHA'
      projectName = 'GHA'

      microserviceMapping 'ee27fc10-1ecd-f147-89d9-daf345ab12c5', {
        clusterName = 'insurance-dev'
        clusterProjectName = 'GHA'
        microserviceName = 'Insurance Frontend'
        tierMapName = 'ee27fbc4-d015-f1f5-8bec-daf345ab12c5'
      }
    }

    tierMap 'ee27fbd1-4468-f1e1-bb21-daf345ab12c5', {
      applicationName = 'Simple Insurance'
      environmentName = 'insurance-prod'
      environmentProjectName = 'GHA'
      projectName = 'GHA'

      microserviceMapping 'ee27fc10-2276-f1c9-89d9-daf345ab12c5', {
        clusterName = 'insurance-prod'
        clusterProjectName = 'GHA'
        microserviceName = 'Insurance Frontend'
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