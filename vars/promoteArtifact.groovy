#!groovy

import java.util.Map

def call(){

	try{
			rtServer (
			id: "Artifactory-test",
			url: "http://artifactory:8081/artifactory",
			credentialsId: "deployer"
			)

		
		rtPromote (
		// Mandatory parameter
 
		buildName: "${JOB_NAME}",
		buildNumber: "${BUILD_NUMBER}",
    // Artifactory server ID from Jenkins configuration, or from configuration in the pipeline script
		serverId: 'Artifactory-test',
    // Name of target repository in Artifactory
    targetRepo: 'lib-release',
 
    // Optional parameters
 
    // Comment and Status to be displayed in the Build History tab in Artifactory
    comment: 'this is the promotion comment',
    status: 'Released',
    // Specifies the source repository for build artifacts.
    sourceRepo: 'lib',
    // Indicates whether to promote the build dependencies, in addition to the artifacts. False by default.
    includeDependencies: true,
    // Indicates whether to fail the promotion process in case of failing to move or copy one of the files. False by default
    failFast: true,
    // Indicates whether to copy the files. Move is the default.
    copy: true
)

	}
	catch(Error e){
		println "failed with ${e}"
		throw e
	}
	
}
