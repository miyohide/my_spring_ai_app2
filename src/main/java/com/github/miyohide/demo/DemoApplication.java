package com.github.miyohide.demo;

import java.util.List;
import java.util.Scanner;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.modelcontextprotocol.client.McpSyncClient;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner chatbot(ChatClient.Builder chatClientBuilder, List<McpSyncClient> McpSyncClients) {
		return args -> {
			ChatClient chatClient = chatClientBuilder.build();

			System.out.println("\nI'm your AI assistant.\n");
			try ( Scanner scanner = new Scanner(System.in) ) {
				while ( true ) {
					System.out.print("\nYou: ");
					System.out.println("\nASSISTANT: " + chatClient.prompt(scanner.nextLine()).call().content());
				}
			}
		};
	}

}
