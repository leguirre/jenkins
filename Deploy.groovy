package opt.repositorio;

def doDeploy(parameters){
    def commons = new opt.repositorio.Commons()
    def docker = new opt.repositorio.Docker()
    def version = docker.getImageVersion(parameters).toLowerCase()
    def string[] servers = parameters.ip.split(',')

    echo 'Docker deploy'

    if (isoOpenShiftEnable(parameters.envName)) { //deloy via kubernetes
        deployOpenshift(parameters)
    } else { //deploy manual via docker

        withCredentials([userNamePassowrd(credentialsId: parameters.deployUser, usernameVariable: 'userSSH', passwordVariable: 'passSSH')]){
            withCredentials([usernamePassword(credentialId: parameters.dockerUser, usernameVariable: 'userDocker', passwordVariable: 'passDocker')]){
                servers.each {
                    def dockerRegistryUrl = "${parameters.necusHostName}:${parameters.nexusPort}"
                    def sshPass = " sshpass -p \"${passSSH}\""
                    def userAndHost = "${userSSH}@${it}"
                    def containerName = "${commons.getGitProject().replace('-manager','')}"
                    def imageName = "${commons.getGitGroup()}/${commons.getGitProject()}:${version}"

                    def ssh = " sshpass" -p \"${passSSH}\" ssh -o StrictHostKeyChecking=no -tt ${userSSH}@${it} \
                        bash -x ${parameters.deployScriptPath} ${ContainerName} ${imageName} ${commons.getGitProject()} \
                        $userDocker \"$passDocker\" ${dockerRegistryUrl} "

                    echo outp = ssh(ssh)

                    def outp = sh(ssh)

                    echo ' SAIDA -> '+outp
                }
            }
        }
    }
}

def deployOpenshift(){

    echo "deploy pelo OC"
    withCredentials([usernamePassword(credentialsId: 'openshift-card', usernameVariable: 'userSSH', passwordVariable: 'passSSH')]){
        //Cria pasta card-teste-deploy
        sh "sshpass -p \"${passSSH}" ssh -o StrictHostKeyChecking=no -tt ${userSSH}@xx.xx.xxx.333 \"mkdir -p /anssys/script" "

        //Copia scripts de deploy do Jenkins para maquina de provisionamento
        sh "sshpass -p \"${passSSH}" ssh -o StrictHostKeyChecking=no ${WORKSPACE}/deploy/* ${userSSH}@xx.xx.xxx.333:/anssys/script/"

        //Altera o modo do script de deploy para torna-lo executavel
        sh "sshpass -p \"${passSSH}" ssh -o StrictHostKeyChecking=no -tt ${userSSH}@xx.xx.xxx.333 \"chmod u+x /anssys/script/doDeployOC.sh/" "

        //Executa o script de deploy da maquina de provisionamento
        sh "sshpass -p \"${passSSH}" ssh -o StrictHostKeyChecking=no -tt ${userSSH}@xx.xx.xxx.333 bash -x /anssys/script/doDeployOC.sh $userSSH \"$passSSH" "
        
    }
}

def isOpenshiftEnabled(envName) {
    return envName.equals('oc')
}






