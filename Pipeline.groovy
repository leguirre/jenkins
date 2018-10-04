package opt.repositorio

def create Pipeline(parameters){
    def commons = new opt.repositorio.commons.Commons()
    def docker = new opt.repositorio.commons.Docker()
    def deploy = new opt.repositorio.Deploy()
    def branchName = commons.getBranch

    stage('checkout'){
        commons.checkout()
    }

    if (parameters.doBuild){
        stage('Build'){
            replacePomVersion(parameters)
            commons.build('-DskipTests')
        }

        stage('Unit Tests'){
            commons.runTests()
        }

        stage('Dependency Check'){
            commons.runDependencyCheck()
        }

        stage('SonarQube'){
            def sonarProjectKey = getSonarProjectKey(parameters.envName)
            echo sonarProjectKey
            runSonarMvn('-DskipTests', sonarProjectKey)
        }
    }

    if (parameters.doDockerBuild){
        stage('Docker Build'){
            dokcer.buildAndPush(parameters)
        }
    }

    if (parameters.doDeploy){
        stage('Deploy'){
            if (parameters.envName in ['oc']){
                deploy.deployOpenShift()
            } else {
                deploy.doDeploy(parameters)
            }
        }
    }

    if (parameters.doNexusDeploy){
        stage('Nexus Deploy'){
            def pom = readMavenPom file: 'pom.xml'

            def repositoryType = '-release'
            if(pom.version.contains('SNAPSHOT')){
                repositoryType = '-snapshot'
            }

            repository = "${parameter.group}-${repositoryType}"

            nexusArtifactUpLoader(
                protocol:''
                nexusUrl:''
                

            )        }
    }
}