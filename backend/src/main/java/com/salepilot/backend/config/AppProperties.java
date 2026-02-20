package com.salepilot.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Ai ai = new Ai();
    private Email email = new Email();
    private Storage storage = new Storage();
    private Firebase firebase = new Firebase();

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private long expiration;
        private long refreshExpiration;
    }

    @Getter
    @Setter
    public static class Ai {
        private String googleApiKey;
        private String model;
        private double temperature;
        private int maxTokens;
    }

    @Getter
    @Setter
    public static class Email {
        private boolean enabled;
        private String from;
        private String fromName;
        private Verification verification = new Verification();

        @Getter
        @Setter
        public static class Verification {
            private String baseUrl;
        }
    }

    @Getter
    @Setter
    public static class Storage {
        private String type;
        private String uploadDir;
        private long maxFileSize;
        private String allowedExtensions;
    }

    @Getter
    @Setter
    public static class Firebase {
        private boolean enabled;
        private String credentialsPath;
        private String storageBucket;
    }
}
