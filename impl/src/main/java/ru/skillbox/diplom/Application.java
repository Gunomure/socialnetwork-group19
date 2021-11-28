package ru.skillbox.diplom;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class Application {

	@Value("${values.socketio.host}")
	private String host;

	@Value("${values.socketio.port}")
	private Integer port;

	@Bean
	public SocketIOServer socketIOServer() throws NoSuchFieldException, IOException, IllegalAccessException {

		Configuration config = new Configuration();
		config.setHostname(host);
		config.setPort(port);
		SocketIOServer server = new SocketIOServer(config);
		return server;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
