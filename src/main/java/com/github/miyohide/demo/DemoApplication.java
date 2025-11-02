package com.github.miyohide.demo;

import io.modelcontextprotocol.client.McpSyncClient;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @Bean
  public CommandLineRunner chatbot(
      ChatClient.Builder chatClientBuilder,
      List<McpSyncClient> mcpSyncClients,
      ConfigurableApplicationContext context) {
    return args -> {
      ChatClient chatClient =
          chatClientBuilder
              .defaultSystem("You are an AWS CDK expert")
              .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients))
              .defaultAdvisors(
                  MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build())
                      .build(),
                  new SimpleLoggerAdvisor())
              .build();

      System.out.println("\nI'm your AI assistant.\n");
      System.out.println("\nYou: ");
      String result = chatClient.prompt("AgentCoreをCDKで作るにはどうすれば良い？").call().content();
      System.out.println("\nASSISTANT: " + result);

      context.close();
    };
  }
}
