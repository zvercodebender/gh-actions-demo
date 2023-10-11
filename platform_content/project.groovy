def resource_name = 'rancher'

project 'GHA', {
  pluginConfiguration 'github', {
    field = [
      'authType': 'token',
      'debugLevel': '0',
      'ignoreSSLErrors': 'false',
      'library': 'jgit',
      'token_credential': 'token_credential',
    ]
    pluginKey = 'EC-Git'

    addCredential 'token_credential', {
      passwordRecoveryAllowed = '0'
      password = args.githubPAT
    }
  }

  pluginConfiguration 'helm', {
    field = [
      'checkConnectionResource': resource_name,
      'createKubeconfig': '0',
      'debugLevel': '0',
      'helmPath': '/opt/cbflow/third-party/bin/helm',
      'helmVersion': '3',
    ]
    pluginKey = 'EC-Helm'
  }
}
