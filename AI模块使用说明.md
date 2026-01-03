# AI医生模块使用说明

## 功能概述

AI医生模块已集成到心理咨询系统，当前使用阿里云通义大模型（Qwen / OpenAI 兼容接口）提供智能对话服务。

## 主要功能

### 1. 智能对话
- **AI模型**: 通义 Qwen（通过 OpenAI 兼容接口，模型名由配置决定，如 `qwen-plus`）
- **对话功能**: 提供心理健康相关的咨询和建议
- **系统提示词**: AI助手名为"心语"，以专业、温和的语气与用户交流

### 2. 风险检测
自动检测用户消息中的危机关键词：
- **高风险关键词**: 自杀、想死、不想活、结束生命、自残等
- **中风险关键词**: 抑郁、焦虑、失眠、痛苦、崩溃等
- **风险等级**: low（低）、medium（中）、high（高）

### 3. 会话管理
- 创建新会话
- 查看历史会话列表
- 加载会话消息记录
- 删除会话

## 技术实现

### 后端实现

#### 1. 实体类
- `ChatSession`: 会话实体（id, sessionId, userId, title, lastMessage, status等）
- `ChatMessage`: 消息实体（id, sessionId, role, content, type, riskLevel等）

#### 2. Mapper层
- `ChatSessionMapper`: 会话数据访问（findBySessionId, findByUserId, insert, update, softDelete）
- `ChatMessageMapper`: 消息数据访问（findBySessionId, insert, batchInsert, deleteBySessionId）

#### 3. DTO类
- `ChatSessionDTO`: 会话传输对象
- `ChatMessageDTO`: 消息传输对象
- `SendMessageRequest`: 发送消息请求
- `AiMessageResponse`: AI回复响应（包含reply, riskLevel, suggestedActions）
- `CreateSessionResponse`: 创建会话响应

#### 4. 工具类
`AiUtil`: 通义（OpenAI 兼容）工具类
- `sendMessage(String message)`: 发送消息到AI并获取回复
- `analyzeRiskLevel(String message)`: 分析消息风险等级
- `getSuggestedActions(String riskLevel)`: 获取建议操作
- `buildSystemPrompt()`: 构建系统提示词

#### 5. 服务层
`AiChatService` 接口和 `AiChatServiceImpl` 实现类：
- `createSession(Long userId)`: 创建新会话
- `getSessions(Long userId)`: 获取用户的会话列表
- `getMessages(String sessionId, Long userId)`: 获取会话消息记录
- `sendMessage(SendMessageRequest request, Long userId)`: 发送消息并获取AI回复
- `deleteSession(String sessionId, Long userId)`: 删除会话

#### 6. 控制器
`AiChatController` 提供REST API：
- `POST /api/chat/sessions`: 创建新会话
- `GET /api/chat/sessions`: 获取会话列表
- `GET /api/chat/sessions/{sessionId}/messages`: 获取消息记录
- `POST /api/chat/send`: 发送消息
- `DELETE /api/chat/sessions/{sessionId}`: 删除会话

### 前端实现

#### AiChatView.vue
使用Vue 3 Composition API实现：

**核心功能**:
- 会话列表展示和切换
- 实时消息显示（用户消息和AI回复）
- 风险提示（高风险消息显示特殊样式）
- 消息发送（支持Enter键发送）
- 加载状态显示
- 自动滚动到底部

**主要方法**:
- `createNewSession()`: 创建新会话
- `loadSessions()`: 加载会话列表
- `selectSession(sessionId)`: 选择会话
- `loadMessages(sessionId)`: 加载消息记录
- `sendMessage()`: 发送消息
- `formatTime(dateString)`: 格式化时间显示

## 配置信息

### application.yml
```yaml
# AI配置 (阿里云通义千问)
ai:
  api-key: sk-84427029c5ca47ebaaabb368cabbcf0e
  base-url: https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions
  model: qwen-turbo
```

> 说明：当前实现基于 OpenAI 兼容协议调用通义 Qwen，无需额外 SDK 依赖，只需配置 `ai.api-key`、`ai.base-url` 和 `ai.model` 即可。

## API接口文档

### 1. 创建新会话
**请求**: `POST /api/chat/sessions`  
**Headers**: `Authorization: Bearer {token}`  
**响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "sessionId": "uuid",
    "title": "新会话"
  }
}
```

### 2. 获取会话列表
**请求**: `GET /api/chat/sessions`  
**Headers**: `Authorization: Bearer {token}`  
**响应**:
```json
{
  "code": 200,
  "data": [
    {
      "sessionId": "uuid",
      "title": "考前焦虑缓解",
      "lastMessage": "最近感觉压力很大...",
      "createdAt": "2024-01-01T10:00:00",
      "updatedAt": "2024-01-01T10:30:00"
    }
  ]
}
```

### 3. 发送消息
**请求**: `POST /api/chat/send`  
**Headers**: `Authorization: Bearer {token}`  
**请求体**:
```json
{
  "sessionId": "uuid",
  "content": "我最近压力很大",
  "type": "text"
}
```
**响应**:
```json
{
  "code": 200,
  "data": {
    "reply": "听起来你正经历着一些压力...",
    "riskLevel": "medium",
    "suggestedActions": ["建议预约心理咨询师", "尝试放松练习"]
  }
}
```

### 4. 获取消息记录
**请求**: `GET /api/chat/sessions/{sessionId}/messages`  
**Headers**: `Authorization: Bearer {token}`  
**响应**:
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "role": "user",
      "content": "我最近压力很大",
      "type": "text",
      "riskLevel": "medium",
      "createdAt": "2024-01-01T10:00:00"
    },
    {
      "id": 2,
      "role": "assistant",
      "content": "听起来你正经历着一些压力...",
      "type": "text",
      "riskLevel": "medium",
      "createdAt": "2024-01-01T10:00:05"
    }
  ]
}
```

### 5. 删除会话
**请求**: `DELETE /api/chat/sessions/{sessionId}`  
**Headers**: `Authorization: Bearer {token}`  
**响应**:
```json
{
  "code": 200,
  "message": "删除成功"
}
```

## 使用流程

1. **用户登录**: 使用已注册的账号登录系统
2. **访问AI医生**: 点击导航栏的"AI医生"进入对话页面
3. **创建会话**: 点击"新建会话"按钮或直接输入消息自动创建
4. **对话交流**: 在输入框输入消息，按Enter或点击发送按钮
5. **查看历史**: 左侧会话列表显示所有历史会话，点击可切换
6. **风险提示**: 如果检测到高风险内容，系统会显示特殊提示

## 注意事项

1. **API密钥安全**: 生产环境应使用环境变量存储API密钥
2. **风险评估**: AI风险检测仅供参考，不替代专业评估
3. **数据隐私**: 用户对话数据应妥善保护，遵守隐私政策
4. **性能优化**: 高并发场景建议使用消息队列处理AI请求
5. **错误处理**: 网络异常时应有友好的错误提示

## 未来改进

1. 支持图片和语音输入
2. 添加对话导出功能
3. 集成情绪分析图表
4. 支持多轮对话上下文优化
5. 添加专业咨询师人工介入功能

---

开发完成时间: 2024年  
技术栈: Spring Boot 3.x + Vue 3 + Google Gemini AI  
开发者: GitHub Copilot
