package com.sakura.comtroller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/prompt-demo")
public class PromptDemoController {

    private final ChatClient chatClient;

    public PromptDemoController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * 方式一：只有 user 消息
     * 没有任何约束，模型自由发挥
     * GET /prompt-demo/simple?message=JVM
     * 模型可能解释概念，也可能出题，行为不可控
     */
    @GetMapping("/simple")
    public String simple(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * 方式二：user + system 消息
     * system 固定了模型角色，输出风格稳定
     * GET /prompt-demo/with-system?message=JVM
     * 模型一定会用提问方式来考你，不会跑偏
     */
    @GetMapping("/with-system")
    public String withSystem(@RequestParam String message) {
        return chatClient.prompt()
                .system("你是一个面试官，用提问的方式检验候选人对知识的掌握程度。只出题，不给答案。")
                .user(message)
                .call()
                .content();
    }

    /**
     * 方式三：动态模板变量
     * 同一套 Prompt 模板，通过参数控制输出方向
     * GET /prompt-demo/template?topic=JVM&difficulty=初级
     * GET /prompt-demo/template?topic=Redis&difficulty=高级
     * 一个接口覆盖所有主题和难度组合
     */
    @GetMapping("/template")
    public String template(
            @RequestParam String topic,
            @RequestParam(defaultValue = "中级") String difficulty) {
        return chatClient.prompt()
                .user(u -> u.text("请出一道关于 {topic} 的 {difficulty} 难度 Java 面试题，只出题，不给答案。")
                        .param("topic", topic)
                        .param("difficulty", difficulty))
                .call()
                .content();
    }

    /**
     * 流式输出（打字机效果）
     * GET /api/chat/stream?message=写首诗
     */
    @GetMapping(value = "/stream", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> streamChat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }

    /**
     * 手动构造多轮消息（演示底层用法，实际项目用 ChatMemory）
     * {
     * "previousQuestion": "什么是 Spring Boot",
     * "previousAnswer": "Spring Boot 是基于 Spring 的快速开发框架，通过自动配置简化了项目搭建",
     * "currentQuestion": "它和 Spring 框架有什么区别"
     * }
     * POST /api/chat/history
     */
    @PostMapping("/history")
    public String chatWithHistory(@RequestBody HistoryRequest request) {
        List<Message> messages = List.of(
                new SystemMessage("你是一个 Java 技术助手"),
                new UserMessage(request.previousQuestion()),
                new AssistantMessage(request.previousAnswer()),
                new UserMessage(request.currentQuestion())
        );
        return chatClient.prompt()
                .messages(messages)
                .call()
                .content();
    }

    // DTO
    record ChatRequest(String systemPrompt, String userMessage) {
    }

    record ChatDetailResponse(String content, Long totalTokens) {
    }

    record HistoryRequest(String previousQuestion,
                          String previousAnswer,
                          String currentQuestion) {
    }
}