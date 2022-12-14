package com.example.demo.devenv;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class ConfigureDevEnv {

    private String networkMode;
    private String dockerSocket;

    private String processCmd(String cmd){
        try {
            Runtime run = Runtime.getRuntime();
            Process process = run.exec(cmd);
            process.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = buf.readLine()) != null) {
                output.append(line + "\n");
            }
            return output.toString();

        } catch (IOException | InterruptedException e) {
            log.debug(e.getMessage());
        }
        return null;
    }

    private Boolean hasInstalled(String cmd, String responseContains){
        String output = processCmd(cmd);
        if (output != null &&
                output.toUpperCase().contains(responseContains))
            return true;

        return false;
    }

    private void init(){
        String os = System.getProperty("os.name");
        log.info(os);
        Boolean docker = hasInstalled("docker ps", "CONTAINER ID");
        log.info("Docker installed: " + docker);
        Boolean podman = hasInstalled("podman ps", "CONTAINER ID");
        log.info("Podman installed: " + podman);

        if(docker){
            networkMode = "bridge";
        } else if (podman){
            networkMode = "slirp4netns";
        } else {
            networkMode = "";
            throw new RuntimeException("Supported Container Environment Missing (Docker/Podman)");
        }

        if(os.toLowerCase().contains("win")){
            dockerSocket = "tcp://localhost:2375";
        } else if(os.toLowerCase().contains("linux")){
            if(docker) {
                dockerSocket = "unix:///var/run/docker.sock";
            } else if (podman) {
                String uid = processCmd("id -u");
                uid = uid.replaceAll("[^\\d.]", "");
                log.info("UID: " + uid);                
                dockerSocket = "unix:///run/user/"+uid+"/podman/podman.sock";
            }
        }
    }

    public String getNetworkmode(){
        if(networkMode == null)
            init();
        return networkMode;
    }

    public String getDockersocket(){
        if(dockerSocket == null)
            init();
        return dockerSocket;
    }
}
