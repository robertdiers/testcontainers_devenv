package com.example.demo.devenv.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

public class Postgres extends GenericContainer<Postgres> {

    //see resource folder
    public static final String PATH = "testcontainers/postgre/";

    private static GenericContainer<Postgres> instance = null;

    @SuppressWarnings("resource") //testcontainers will do an automatic cleanup
    public static GenericContainer<Postgres> getInstance(String networkmode, long memory) {

        if (instance == null) {
            instance = new Postgres(
                new ImageFromDockerfile()
                        .withFileFromClasspath("Dockerfile", PATH+"Dockerfile")
                        .withFileFromClasspath("init", PATH+"init"))             
                .withCreateContainerCmdModifier(cmd -> cmd.withName("testcontainers_postgres_"+System.currentTimeMillis()))                
                .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(memory))
                .withNetworkMode(networkmode)
                .withReuse(false);

            //add fixed port
            ((Postgres) instance).configurePorts(5432, 5432);
        }

        return instance;
    }

    public Postgres(ImageFromDockerfile withFileFromClasspath) {
        super(withFileFromClasspath);
    }

    public Postgres(DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    public void configurePorts(int hostPort, int containerPort) {
        super.addFixedExposedPort(hostPort, containerPort);        
    }
    
}