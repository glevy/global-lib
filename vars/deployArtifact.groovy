#!groovy

import java.util.Map

def call(Map args){

	String jobName = args?.env ?: 'jobName'
	String buildNumber = args?.env ?: 'buildNumber'
	String artifactId = args?.env ?: 'artifactId'
	String artifactVersion = args?.env ?: 'artifactVersion'

		try{
			rtServer (
			id: "Artifactory-test",
			url: "http://artifactory:8081/artifactory",
			credentialsId: "deployer"
			)

		rtBuildInfo (
			captureEnv: true,
			maxBuilds:	10
		)


		rtMavenDeployer (
			id: 'lib-deploy',
			serverId: 'Artifactory-test',
			releaseRepo: 'lib',
			snapshotRepo: "lib"
		)

		rtMavenResolver (
			id: 'lib-resolve',
			serverId: 'Artifactory-test',
			releaseRepo: 'repo',
			snapshotRepo: "repo"
		) 

		copyArtifacts(
			projectName: jobName,
			filter: "${artifactId}/**/*.*",
			fingerprintArtifacts: true,
			//target: artifactId,
			flatten: false,
			selector: specific(buildNumber)
		)
        
		def source_dir = "${workspace}\\${artifactId}"
    
		rtMavenRun (
			// Tool name from Jenkins configuration.
			tool: 'MAVEN_TOOL',
			pom: libraryResource("var/deploy/pom.xml"),
			//goals: 'package',
			goals: " -B clean -Dartifactory.publish.artifacts=true -Dartifactory.publish.buildInfo=true -Ddeploy.dir='${source_dir}' -Ddeploy.name='${artifactId}' -Ddeploy.version='${artifactVersion}' -P complex_artifact_deploy",
			// Maven options.
			opts: "-Xms1024m -Xmx4096m",
			resolverId: 'lib-resolve',
			deployerId: 'lib-deploy'
		)

	}
	catch(Error e){
		println "failed with ${e}"
		throw e
	}
	
}
