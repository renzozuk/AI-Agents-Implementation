package com.seap.gemini.model.config;

import com.embabel.agent.openai.OpenAiCompatibleModelFactory;
import com.embabel.common.ai.model.Llm;
import com.embabel.common.ai.model.OptionsConverter;
import com.embabel.common.ai.model.PerTokenPricingModel;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class CompatibleModelsConfig extends OpenAiCompatibleModelFactory {


    public CompatibleModelsConfig(
            @Value("${OPENAI_BASE_URL}") String baseUrl,
            @Value("${OPENAI_API_KEY}") String apiKey,
            @Value("${OPENAI_COMPLETIONS_PATH}") String completionsPath){
        super(baseUrl, apiKey, completionsPath, null, ObservationRegistry.create());
    }

    @Bean
    public Llm geminiModel01(
            @Value("${gemini.models.chat.name}") String model
    ){
        return openAiCompatibleLlm(
                model,
                new PerTokenPricingModel(0.40, 1.6),
                "Google GEMINI",
                LocalDate.of(2025, 1, 1)
        );
    }

    @Bean
    public Llm geminiModel02(
            @Value("gemini-2.5-flash") String model
    ){
        return openAiCompatibleLlm(
                model,
                new PerTokenPricingModel(0.40, 1.6),
                "Google GEMINI",
                LocalDate.of(2025, 1, 1)
        );
    }

    @Bean
    public Llm geminiModel03(
            @Value("gemini-3-flash-preview") String model
    ){
        return openAiCompatibleLlm(
                model,
                new PerTokenPricingModel(0.40, 1.6),
                "Google GEMINI",
                LocalDate.of(2025, 1, 1)
        );
    }

    @Bean
    public Llm geminiModel04(
            @Value("gemini-2.0-flash") String model
    ){
        return openAiCompatibleLlm(
                model,
                new PerTokenPricingModel(0.40, 1.6),
                "Google GEMINI",
                LocalDate.of(2025, 1, 1)
        );
    }

    @Bean
    public Llm geminiModel05(
            @Value("gemini-3-pro-preview") String model
    ){
        return openAiCompatibleLlm(
                model,
                new PerTokenPricingModel(0.40, 1.6),
                "Google GEMINI",
                LocalDate.of(2025, 1, 1)
        );
    }

}