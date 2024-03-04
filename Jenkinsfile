pipeline {
  agent none
  options {
    copyArtifactPermission('*');
    skipDefaultCheckout true
  }
  environment { plugin = "Mergic" }
  triggers {
    upstream '/Runsafe/Framework/' + env.BRANCH_NAME
    pollSCM '@monthly'
  }
  stages {
    stage('Ant Build') {
      agent { label 'ant' }
      tools {
        ant 'Default'
        jdk 'Default'
      }
      steps { buildPluginWithAnt env.plugin, 'WorldGuardBridge', 'build/jar/*.jar' }
    }
    stage('Deploy to test server') {
      when { not { branch 'master' } }
      agent { label 'server4' }
      steps {
        installPlugin "${env.plugin}.tar"
        buildReport env.plugin, 'Deployed to test server'
      }
    }
    stage('Deploy to production') {
      when { branch 'master' }
      agent { label 'server1' }
      steps {
        stagePlugin "${env.plugin}.tar"
        buildReport env.plugin, 'Staged on production server'
      }
    }
  }
  post { failure { buildReport env.plugin, 'Build failed' } }
}
