package com.usha.secureprocesssandbox.config;

//import com.github.docker-java.api.DockerClient;
//import com.github.docker-java.core.DefaultDockerClientConfig;
//import com.github.docker-java.core.DockerClientImpl;
//import com.github.docker-java.httpclient5.ApacheDockerHttpClient;
//import com.github.docker-java.transport.DockerHttpClient;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class DockerClientConfig {

    @Bean
    public DockerClient dockerClient() {
        // Automatically discovers local Docker environment settings (Windows Named Pipes or Linux Sockets)
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        // Instantiate the HTTP transport layer required to speak with the daemon
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(3))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }
}
