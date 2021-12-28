package ru.skillbox.diplom;

import com.cloudinary.Cloudinary;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@EnableScheduling
@SpringBootApplication
public class Application {

	@Value("${values.socketio.host}")
	private String host;

	@Value("${values.socketio.port}")
	private Integer port;

    @Value("${values.cloudinary.secret}")
    private String cloudinary_api_secret;
    @Value("${values.cloudinary.key}")
    private String cloudinary_api_key;
    @Value("${values.cloudinary.name}")
    private String cloudinary_api_name;

	@Bean
	public SocketIOServer socketIOServer() throws NoSuchFieldException, IOException, IllegalAccessException {

		Configuration config = new Configuration();
		config.setHostname(host);
		config.setPort(port);
		SocketIOServer server = new SocketIOServer(config);
		return server;
	}

    @Bean
    public Cloudinary cloudinaryConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudinary_api_name);
        config.put("api_key", cloudinary_api_key);
        config.put("api_secret", cloudinary_api_secret);
        return new Cloudinary(config);
    }

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
