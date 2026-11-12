package com.assistaplus.assista_plus_api.config;

import com.assistaplus.assista_plus_api.model.ApiKey;
import com.assistaplus.assista_plus_api.repository.ApiKeyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ApiKeyRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                ApiKey masterKey = new ApiKey();
                masterKey.setKeyValue("minha-chave-mestra-123");
                masterKey.setOwnerName("Administrador Padrão");
                masterKey.setRole("ADMIN");
                masterKey.setActive(true);
                repository.save(masterKey);
            }
        };
    }
}