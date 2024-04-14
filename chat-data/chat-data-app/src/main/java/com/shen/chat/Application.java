package com.shen.chat;

import com.shen.chat.data.config.ChatGLMSDKConfig;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Configurable
@Import({ChatGLMSDKConfig.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
