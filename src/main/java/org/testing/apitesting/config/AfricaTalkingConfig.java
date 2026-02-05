package org.testing.apitesting.config;


import com.africastalking.AfricasTalking;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AfricaTalkingConfig {

    @Value("${africastalking.username}")
    private String username;

    @Value("${africastalking.api-key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        AfricasTalking.initialize(username, apiKey);
    }
}
