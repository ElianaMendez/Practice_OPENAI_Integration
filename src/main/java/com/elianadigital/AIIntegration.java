package com.elianadigital;

import io.github.sashirestela.cleverclient.client.JavaHttpClientAdapter;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.ChatMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;

public class AIIntegration {
    public static void main(String[] args) {
        var user = "Genera 5 productos";
        var system = """
                Eres un generador de productos digitales altamente rentables, que se venden
                en plataformas digitales como Hotmart.
                Debes responder solo el nombre del producto a crear para la próxima campaña.
                Debe ser un nombre atractivo para la audiencia, acorde a la solución propuesta,
                y debe ser en idioma inglés.
                """;

        String apiKeyOpenAI = System.getenv("OPENAI_API_KEY");
        if (apiKeyOpenAI == null || apiKeyOpenAI.isEmpty()) {
            throw new IllegalStateException("No se encontró la variable de entorno OPENAI_API_KEY.");
        }

        var httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .executor(Executors.newFixedThreadPool(3))
                .build();

        var openAI = SimpleOpenAI.builder()
                .apiKey(apiKeyOpenAI)
                .clientAdapter(new JavaHttpClientAdapter(httpClient))
                .build();

        var chatRequest = ChatRequest.builder()
                .model("gpt-4o-mini")
                .message(ChatMessage.SystemMessage.of(system))
                .message(ChatMessage.UserMessage.of(user))
                .temperature(0.0)
                .maxCompletionTokens(300)
                .build();

        var chatResponse = openAI.chatCompletions().create(chatRequest).join();
        System.out.println(chatResponse.firstContent());
    }
}

