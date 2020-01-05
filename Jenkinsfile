pipeline{
    agent any

    environment{
        MAVEN_CREDENTIAL = credentials('heartpattern-maven-repository')
    }

    stages{
        stage('start daemon'){
            steps{
                sh './gradlew --daemon'
            }
        }
        stage('compile'){
            steps{
                sh './gradlew build'
            }
        }
        stage('test'){
            steps{
                sh './gradlew -PnexusUser=${MAVEN_CREDENTIAL_USR} -PnexusPassword=${MAVEN_CREDENTIAL_PSW} clean test'
            }
        }
        stage('create plugin'){
            steps{
                sh './gradlew -PnexusUser=${MAVEN_CREDENTIAL_USR} -PnexusPassword=${MAVEN_CREDENTIAL_PSW} createPlugin'
            }
        }
    }
    post{
        always{
            archiveArtifacts artifacts: 'build/libs/SpikotAdapter-S1151-Plugin.jar'
        }
    }
}