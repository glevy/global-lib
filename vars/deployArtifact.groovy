#!groovy

import java.util.Map

def call(Map args = [:]){

String artifactId = args.artifactId ?: "${JOB_BASE_NAME}"
String artifactVersion = args.artifactVersion ?: "1.0-SNAPSHOT"
String sourcePath = args.sourcePath ?: "${JOB_BASE_NAME}"
String groupId = "build.artifacts.${JOB_NAME.replaceAll('/','.')}"

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
			releaseRepo: 'lib-release',
			snapshotRepo: "lib-release"
		)

		rtMavenResolver (
			id: 'lib-resolve',
			serverId: 'Artifactory-test',
			releaseRepo: 'repo',
			snapshotRepo: "lib-release"
		) 

        
		writeFile file: "${workspace}/deploy/pom.xml", text: libraryResource("deploy/pom.xml")
		writeFile file: "${workspace}/deploy/zip.xml", text: libraryResource("deploy/zip.xml")
		
		rtMavenRun (
			// Tool name from Jenkins configuration.
			tool: 'MAVEN_TOOL',
			pom: "${workspace}/deploy/pom.xml",
			//goals: 'package',
			goals: " -B clean -Dartifactory.publish.artifacts=true -Dartifactory.publish.buildInfo=true -Ddeploy.dir='${workspace}/${sourcePath}' -Ddeploy.group='${groupId}' -Ddeploy.name='${artifactId}' -Ddeploy.version='${artifactVersion}' -P complex_artifact_deploy",
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
