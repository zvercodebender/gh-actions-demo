project 'GHA', {
  catalog 'GHA', {
    projectName = 'GHA'

    catalogItem 'Simple Release', {
      description = '''<xml>
  <title>
    
  </title>

  <htmlData>
    <![CDATA[
      
    ]]>
  </htmlData>
</xml>'''
      buttonLabel = 'Create'
      catalogName = 'GHA'
      dslString = '''def ProjectName = "GHA"
  def StartDate = (new Date())
  def StartDateStr = (String) StartDate.format("yyyy-MM-dd")
  def EndDateStr = (String) (StartDate + 14).format("yyyy-MM-dd")
  def ReleaseName = args.releaseName
  def ImageTag = args.imageTag
  def ImageRepo = args.imageRepository


  release ReleaseName, {
    plannedEndDate = EndDateStr
    plannedStartDate = StartDateStr
    projectName = ProjectName

    pipeline \'pipeline_Simple Release\', {
      releaseName = ReleaseName

      formalParameter \'ec_stagesToRun\', {
        expansionDeferred = \'1\'
      }

      stage \'Development\', {
        colorCode = \'#289ce1\'
        pipelineName = \'pipeline_Simple Release\'
        gate \'POST\', {
          task \'Manual approval\', {
            gateType = \'POST\'
            notificationEnabled = \'0\'
            notificationTemplate = \'ec_default_gate_task_notification_template\'
            subproject = ProjectName
            taskType = \'APPROVAL\'
            approver = [
              \'Everyone\',
            ]
          }
        }

        task \'Deploy to dev\', {
          deployerRunType = \'serial\'
          subproject = ProjectName
          taskType = \'DEPLOYER\'
        }
      }

      stage \'Production\', {
        colorCode = \'#ff7f0e\'
        pipelineName = \'pipeline_Simple Release\'
        task \' Deploy to production\', {
          deployerRunType = \'serial\'
          subproject = ProjectName
          taskType = \'DEPLOYER\'
        }
      }

      // Custom properties

      property \'ec_counters\', {

        // Custom properties
        pipelineCounter = \'1\'
      }
    }

    deployerApplication \'Demo App\', {
      orderIndex = \'1\'
      processName = \'Deploy Application\'

      deployerConfiguration \'Deploy to dev\', {
        actualParameter = [
          \'imageTag\': ImageTag,
          \'imageRepository\': ImageRepo,
        ]
        deployerTaskName = \'Deploy to dev\'
        environmentName = \'demo-dev\'
        processName = \'Deploy Application\'
        rolloutNotificationEnabled = \'0\'
        stageName = \'Development\'
      }

      deployerConfiguration \'ee116750-9947-f10a-9dc5-da7e38253710\', {
        actualParameter = [
          \'imageTag\': ImageTag,
          \'imageRepository\': ImageRepo,
        ]
        deployerTaskName = \' Deploy to production\'
        environmentName = \'demo-prod\'
        processName = \'Deploy Application\'
        rolloutNotificationEnabled = \'0\'
        stageName = \'Production\'
      }
    }
  }

  property "/myUser/SimpleReleaseName", value: ReleaseName
  '''
      endTargetJson = '''{
        "source": "property",
        "object": "release",
        "objectName": "$[/myUser/SimpleReleaseName]",
        "objectProjectName": "GHA"
  }'''
      iconUrl = 'logo-cloudbees.svg'
      projectName = 'GHA'
      useFormalParameter = '1'

      formalParameter 'releaseName', {
        label = 'Release Name'
        orderIndex = '1'
        required = '1'
        type = 'entry'
      }

      formalParameter 'imageTag', defaultValue: 'latest', {
        label = 'Image Tag'
        orderIndex = '2'
        required = '1'
        type = 'entry'
      }

      formalParameter 'imageRepository', defaultValue: args.dockerHubUser + '/demo-app', {
        label = 'Image Repository'
        orderIndex = '3'
        required = '1'
        type = 'entry'
      }

    }
  }
}
