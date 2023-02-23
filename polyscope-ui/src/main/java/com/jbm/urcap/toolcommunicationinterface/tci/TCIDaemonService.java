package com.jbm.urcap.toolcommunicationinterface.tci;

import com.ur.urcap.api.contribution.docker.DockerRegistrationService;
import com.ur.urcap.api.contribution.docker.DockerRegistry;
import com.ur.urcap.api.contribution.docker.DockerContribution;

public class TCIDaemonService implements DockerRegistrationService {

	private static String DAEMON_ID = "daemon-py";
	private DockerContribution dockerContribution;

	public TCIDaemonService(){		
	}
	
	public DockerContribution getDaemon() {
		return dockerContribution;
	}

	@Override
	public void registerDockerContributions(DockerRegistry dockerRegistry) {
		dockerContribution = dockerRegistry.registerDockerContribution(DAEMON_ID);
	}
}
