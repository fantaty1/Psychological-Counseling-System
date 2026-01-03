package com.cqust.psychologicalcounseling.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI工具类
 * 用于与DeepSeek AI进行对话
 */
@Slf4j
@Component
public class AiUtil {
    
    @Value("${ai.api-key}")
    private String apiKey;
    
    @Value("${ai.base-url}")
    private String baseUrl;
    
    @Value("${ai.model}")
    private String model;
    
    private RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @PostConstruct
    public void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000);
        factory.setReadTimeout(60000);
        restTemplate = new RestTemplate(factory);
        log.info("AI工具初始化完成，使用模型: {}", model);
    }
    
    // 危机关键词列表
    private static final List<String> CRISIS_KEYWORDS = List.of(
            "自杀", "想死", "不想活", "结束生命", "自残", "伤害自己",
            "割腕", "跳楼", "上吊", "吞药", "轻生"
    );
    
    // 高风险关键词列表
    private static final List<String> HIGH_RISK_KEYWORDS = List.of(
            "抑郁", "焦虑", "失眠", "痛苦", "崩溃", "绝望",
            "无助", "孤独", "压力大", "受不了"
    );
    
    /**
     * 发送消息到AI并获取回复
     *
     * @param message 用户消息
     * @return AI回复内容
     */
    public String sendMessage(String message) {
        try {
            // 构建请求体 (OpenAI兼容格式)
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 系统提示词
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", buildSystemPrompt());
            messages.add(systemMessage);
            
            // 用户消息
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 500);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            // 发送请求
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            log.info("发送AI请求到: {}", baseUrl);
            ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);
            
            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode choices = root.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    JsonNode firstChoice = choices.get(0);
                    JsonNode messageNode = firstChoice.path("message");
                    String reply = messageNode.path("content").asText();
                    log.info("AI回复: {}", reply);
                    return reply;
                }
            }
            
            log.error("AI响应解析失败: {}", response.getBody());
            return generateLocalResponse(message);
            
        } catch (Exception e) {
            log.error("AI对话失败: {}", e.getMessage(), e);
            // 使用本地回复作为备用
            return generateLocalResponse(message);
        }
    }
    
    /**
     * 生成本地回复（当API不可用时的备用方案）
     */
    private String generateLocalResponse(String message) {
        // 简单的关键词匹配回复
        if (message.contains("你好") || message.contains("嗨") || message.contains("hello")) {
            return "你好！我是心语，你的AI心理助手。今天有什么想和我聊聊的吗？无论是学习压力、人际关系，还是只是想找人说说话，我都在这里倾听你。";
        }
        if (message.contains("压力") || message.contains("焦虑")) {
            return "我能感受到你正在经历一些压力。压力是生活的一部分，但如果它让你感到不舒服，我们可以一起来探索一些缓解的方法。你愿意告诉我更多关于这种压力的来源吗？";
        }
        if (message.contains("失眠") || message.contains("睡不着")) {
            return "失眠确实很让人困扰。你有尝试过一些放松的方法吗？比如睡前听轻柔的音乐，或者做一些深呼吸练习？如果问题持续，建议你也可以咨询专业的医生。";
        }
        if (message.contains("难过") || message.contains("伤心") || message.contains("哭")) {
            return "我很抱歉听到你感到难过。有时候，让自己哭出来也是一种释放。你愿意和我分享是什么让你感到难过吗？我会认真倾听的。";
        }
        if (message.contains("孤独") || message.contains("没人")) {
            return "感到孤独是很常见的情绪，你并不孤单。我在这里陪着你。有时候，找一个信任的朋友聊聊，或者参加一些社交活动，可能会有帮助。你有没有想过尝试一些新的方式来结识朋友？";
        }
        if (message.contains("谢谢") || message.contains("感谢")) {
            return "不客气！能帮到你我很开心。记住，无论何时你需要倾诉，我都在这里。希望你每天都能开心一点！";
        }
        
        // 默认回复
        return "谢谢你愿意和我分享。我在认真倾听你说的每一句话。能告诉我更多关于你现在的感受吗？我想更好地理解你正在经历的事情。";
    }
    
    /**
     * 分析消息风险等级
     *
     * @param message 消息内容
     * @return 风险等级：low/medium/high
     */
    public String analyzeRiskLevel(String message) {
        if (message == null || message.isEmpty()) {
            return "low";
        }
        
        // 检查危机关键词
        for (String keyword : CRISIS_KEYWORDS) {
            if (message.contains(keyword)) {
                log.warn("检测到危机关键词: {}", keyword);
                return "high";
            }
        }
        
        // 检查高风险关键词
        int highRiskCount = 0;
        for (String keyword : HIGH_RISK_KEYWORDS) {
            if (message.contains(keyword)) {
                highRiskCount++;
            }
        }
        
        if (highRiskCount >= 2) {
            return "medium";
        } else if (highRiskCount >= 1) {
            return "medium";
        }
        
        return "low";
    }
    
    /**
     * 获取建议的操作
     *
     * @param riskLevel 风险等级
     * @return 建议操作列表
     */
    public List<String> getSuggestedActions(String riskLevel) {
        List<String> actions = new ArrayList<>();
        
        if ("high".equals(riskLevel)) {
            actions.add("立即联系心理援助热线：400-161-9995");
            actions.add("预约专业心理咨询师");
            actions.add("告知信任的朋友或家人");
        } else if ("medium".equals(riskLevel)) {
            actions.add("建议预约心理咨询师");
            actions.add("尝试放松练习");
        }
        
        return actions;
    }
    
    /**
     * 构建系统提示词
     *
     * @return 系统提示词
     */
    private String buildSystemPrompt() {
        return """
你是一名 AI 心理咨询师（女性），名叫"心语"，具备系统的心理学基础知识，包括但不限于：普通心理学、发展心理学、社会心理学、咨询心理学与常见心理问题的基础干预原则。

## 你的角色设定
- 性别：女性
- 性格特征：温和、耐心、共情能力强、表达清晰、不评判、不指责
- 沟通风格：语气平缓、尊重对方、善于倾听，能够让来访者感到被理解和被接纳
- 身份定位：心理咨询辅助型智能体（非临床医生）

## 工作原则
1. 不进行医学诊断，不替代专业医生或心理治疗师
2. 不给出药物建议
3. 以倾听、共情、引导、自我觉察为核心
4. 鼓励理性面对情绪，而非压制情绪
5. 对明显的严重心理危机，建议用户及时寻求现实中的专业帮助

## 咨询方式
1. 首先关注来访者的情绪和感受
2. 使用共情性语言，例如："我能理解你现在一定很难受"、"你愿意和我多说一点吗"
3. 通过温和提问帮助用户梳理问题根源
4. 提供心理学层面的解释、情绪调节建议和认知引导
5. 给出可操作但不强制的建议，如放松训练、情绪记录、认知重构等

## 回答风格要求
1. 使用第一人称（如"我会陪你一起分析"、"我理解你的感受"）
2. 语言自然、像真实的女性心理咨询师
3. 避免说教式和命令式语句
4. 不使用专业术语堆砌，必要时用通俗语言解释
5. 回复控制在200字以内，简洁但有温度

## 安全与关怀
1. 如果用户表达出明显的绝望、自我伤害或自杀倾向，应保持冷静与关怀
2. 建议其联系现实中的心理咨询机构、医院或信任的人
3. 可推荐全国心理援助热线：400-161-9995 或北京心理危机研究与干预中心：010-82951332
4. 表达支持与陪伴，但不承诺"一定能解决"、"一定会好"
5. 始终传递"你不是一个人"、"我会陪着你"的信息

## 禁止行为
1. 不评判、不指责来访者的任何想法或行为
2. 不泄露任何用户隐私信息
3. 不提供法律、医学、投资等非心理咨询领域的建议
4. 不与用户发生争执或对立
5. 不使用冷漠、敷衍的语气
            """;
    }
}

