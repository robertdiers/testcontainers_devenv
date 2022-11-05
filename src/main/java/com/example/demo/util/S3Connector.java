package com.example.demo.util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class S3Connector {

    @Value( "${s3.url:}" )
    private String s3Url;

    @Value( "${s3.username:}" )
    private String s3Username;

    @Value( "${s3.password:}" )
    private String s3Password;

    private AmazonS3 s3Client = null;    

    @Getter
    private boolean initialized = false;

    @PostConstruct
    public void init() {
        try {
            if (!s3Url.isEmpty()) {
                AWSCredentials credentials = new BasicAWSCredentials(s3Username, s3Password);
                ClientConfiguration clientConfiguration = new ClientConfiguration();
                clientConfiguration.setSignerOverride("AWSS3V4SignerType");

                String hostname = s3Url;
                if(!hostname.contains("http://")){
                    hostname = "http://" + hostname;
                }
                
                s3Client = AmazonS3ClientBuilder
                        .standard()
                        .withEndpointConfiguration(
                                new AwsClientBuilder.EndpointConfiguration(
                                        hostname, "Europe"))
                        .withPathStyleAccessEnabled(true)
                        .withClientConfiguration(clientConfiguration)
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .build();
                
                this.initialized = true;
            } else {
                log.warn("skipped S3Connector init");
                this.initialized = false;
            }
        } catch (Exception e) {
            log.error("S3Connector init failed", e);
        }
    }

    /**
     * store file in S3
     * @param key filename
     * @param data content
     */
    public void store(String key, String data, String bucket) {        
        s3Client.putObject(bucket, key, data);
    }

    /**
     * read stored file content
     * @param key
     * @return
     */
    public String read(String key, String bucket) {
        try {
            S3Object s3object = s3Client.getObject(bucket, key);
            S3ObjectInputStream inputStream = s3object.getObjectContent();

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * close the client
     */
    @PreDestroy
    private void close() {
        if (this.isInitialized()) s3Client.shutdown();
    }

}