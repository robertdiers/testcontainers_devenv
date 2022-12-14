package com.example.demo.devenv.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

public class MinioS3 extends GenericContainer<MinioS3> {

    //see resource folder
    public static final String PATH = "testcontainers/minio-s3/";

    private static GenericContainer<MinioS3> instance = null;

    @SuppressWarnings("resource") //testcontainers will do an automatic cleanup
    public static GenericContainer<MinioS3> getInstance(String networkmode, long memory) {

        if (instance == null) {
            instance = new MinioS3(
                new ImageFromDockerfile()
                        .withFileFromClasspath("Dockerfile", PATH+"Dockerfile"))
                .withCreateContainerCmdModifier(cmd -> cmd.withName("testcontainers_S3_"+System.currentTimeMillis()))
                .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(memory))
                .withNetworkMode(networkmode)
                .withReuse(false);

            //add fixed port
            ((MinioS3) instance).configurePorts(29000, 9000);
            ((MinioS3) instance).configurePorts(29001, 9001);
        }

        return instance;
    }

    public MinioS3(ImageFromDockerfile withFileFromClasspath) {
        super(withFileFromClasspath);
    }

    public MinioS3(DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    public void configurePorts(int hostPort, int containerPort) {
        super.addFixedExposedPort(hostPort, containerPort);        
    }
    
}