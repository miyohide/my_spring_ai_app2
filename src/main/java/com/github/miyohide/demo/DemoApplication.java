package com.github.miyohide.demo;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper;

import java.time.Duration;
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
              .defaultSystem("You are an AWS expert")
              .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients))
              .defaultAdvisors(
                //   MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build())
                //       .build(),
                  new SimpleLoggerAdvisor())
              .build();

      System.out.println("\nI'm your AI assistant.\n");
      System.out.println("\nYou: ");
      String result = chatClient.prompt("AgentCoreをCDKで作るにはどうすれば良い？").call().content();
      System.out.println("\nASSISTANT: " + result);

      context.close();
    };
  }

  @Bean(destroyMethod = "close")
  public McpSyncClient mcpClient() {
	var stdioParams = ServerParameters.builder("uvx")
	.args("awslabs.aws-documentation-mcp-server@latest")
	.addEnvVar("FASTMCP_LOG_LEVEL", "ERROR")
	.addEnvVar("AWS_DOCUMENTATION_PARTITION", "aws")
	.addEnvVar("MCP_USER_AGENT", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
	.build();

	var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams, McpJsonMapper.createDefault()))
	.requestTimeout(Duration.ofSeconds(10)).build();

	var init = mcpClient.initialize();

	System.out.println("MCP Initialized: " + init);

	return mcpClient;
  }
}
