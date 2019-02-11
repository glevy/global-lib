#!groovy

import java.util.Map

def call(Map args){

	//String jobName = args.jobName
	//String buildNumber = args.buildNumber
	String artifactId = args.artifactId
	String artifactVersion = args.artifactVersion
	String targetPath = args.targetPath

		try{
			rtServer (
			id: "Artifactory-test",
			url: "http://artifactory:8081/artifactory",
			credentialsId: "deployer"
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

        
			
		writeFile file: "${workspace}\\download\\pom.xml", text: libraryResource("download/pom.xml")
		

		rtMavenRun (
			// Tool name from Jenkins configuration.
			tool: 'MAVEN_TOOL',
			pom: "${workspace}\\download\\pom.xml",
			goals: " -B -U dependency:unpack -Ddownload.artifactId='${artifactId}' -Ddownload.version='${artifactVersion}' -Ddownload.outDir='${targetPath}' -DoverWriteReleases=true",
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
