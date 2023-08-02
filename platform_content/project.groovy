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
      passwordRecoveryAllowed = '1'
      password = args.githubPAT
    }
  }

  pluginConfiguration 'helm', {
    field = [
      'checkConnectionResource': 'k8s-agent',
      'createKubeconfig': '0',
      'debugLevel': '0',
      'helmPath': '/usr/local/bin/helm',
      'helmVersion': '3',
    ]
    pluginKey = 'EC-Helm'
  }
}
