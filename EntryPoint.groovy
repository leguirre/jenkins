package opt.repositorio;

def setParameters(){
    def pipeline = new opt.repositorio()
    def commons = new opt.repositorio.commons()
    def projectUrl = commons.getGiUrl()

    def parameters = [
	branch.common.getBranch(),
	enName.getEnvName(commons.getBranch()),
	lider:'',//não utilizado
	aprovvers:'',//não utilizado
	aproversPrd:''.//não utilizado
	deployScriptPath:'/beasys/docker-compose/doDeploy.sh',
	deployUser:'srs-card-deploy',
	dockerUser:'srv-card-lib',
	group:'card',
	project: commons.getGitProject()
	projectType:'java',
	nexusHostaName:'xxx-nexus.xxx.local',
	nexusUrl:'xx.xxx.xx.387',
	//os parâmetros abaixo são obtidos de getProjEnvParameters - valores default abaixo
	nexusPort:'',
	ip:'',
	ambiente:'',
	doBuild: true,
	doDockerBuild: true,
	doDeploy: true,
	library: false,
	doNexusDeploy: false,
	runComponentTests: true,
	componentTestsJob: '../VSC_VSC-component-tests/master',
	isComponentTestsProj: false
]
	
	parameters - parameters + getEnviProjParameters(parameters.project, parameters.envName)

	if (parameters.enName in ['dev','hm','prd','oc']){
	    pipeline.createPipeline(parameters)
	} else {
	  echo "Nao ha pipeline configurado para a branch ${parameters.branch}"
	}
}

def getProjEnvParameters(project, envName){
	def allParameters = [:]

	//Parametros genericos por tipo de ambiente, para qualquer projeto
	//key = nome do ambiente (envNallParameters["dev"] = [
		nexusPort:'88XX'
		ip:'xx.xxx.xxx.357,xx.xxx.xxx.358'
	]
	//dev
	allParameters["dev"] = [
		nexusPort:'88XX'
		ip:'xx.xxx.xxx.357,xx.xxx.xxx.358'
	]
	//ti
	allParameters["ti"] = [
		nexusPort:'88XX'
		ip:'xx.xxx.xxx.357,xx.xxx.xxx.358'
	]
	//hm
	allParameters["hm"] = [
		nexusPort:'88XX'
		ip:'xx.xxx.xxx.357,xx.xxx.xxx.358'
	]
	//prd
	allParameters["prd"] = [
		nexusPort:'88XX'
		ip:'xx.xxx.xxx.357,xx.xxx.xxx.358'
	]
	//oc
	allParameters["oc"] = [
		nexusPort:'88XX'
		ip:'xx.xxx.xxx.357,xx.xxx.xxx.358'
	]

	////Parametros genericos por projeto, para qualquer ambiente
	////Sobrescreve parametros genericos por ambiente
	////key = nome do projeto (project)
	//allParameters["projeto"] = [:]

	////Parametros genericos por projeto, para qualquer ambiente
	////Sobrescreve parametros genericos por ambiente
	////key = nome do projeto (project)
	//allParameters["projeto-ambiente"] = [:]

	////Combina os parametros genericos por ambiente, genericos por projeto e 
	////parametros especificos de projeto+ambiente
	////somente parametros duplicados sao sobrescritos
	def result = [:]

	if(allParameters[envName])
		result = result + allParameters[envName]

	if(allParameters[envName])
		result = result + allParameters[project]

	if(allParameters["${project}-${envName}"])
		result = result + allParameters["${project}-${envName}"]

	return result
}

def getEnvName(branchName){
	def envName = ''
		if (
			branchName.startsWith('feature/kubernetes')||
			branchName.startsWith('feature/kubernetes-contract')||
			branchName.startsWith('feature/kubernetes-integration')||
			branchName.startsWith('feature/kubernetes-kyx')||
		) {
		envName = 'oc'
		} else if(
			branchName.startsEquals('develop')||
			branchName.startsEquals('development')||
			branchName.startsWith('featuredevelop/')||
			branchName.startsWith('featurerelease/')||
			branchName.startsWith('sprint_')||
			branchName.startsWith('feature_')||
		) {
		envName = 'dev'
		} else if (
			branchName.startsEquals('release')||
			branchName.startsWith('release/')||
			branchName.startsWith('hotfix')||
		) {
		envName = 'hm'
		} else if (branchName.equals('master')){
			envName = 'prd'
		} else{
			echo 'Branch Desconhecida'
		}
		return envName
	
}








