# 🧠 Psychological Counseling System — 校园心理咨询系统

一个面向高校的全栈心理健康服务平台，集成 **AI 智能心理咨询**、**人工咨询师预约**、**心理测评** 和 **后台管理** 等功能，为学生、咨询师和管理员提供完整的心理健康服务解决方案。

## 📖 项目简介

本系统旨在为高校学生提供便捷、专业的心理健康服务。学生可以通过 AI 聊天获得即时的心理疏导，也可以预约线下/线上的专业咨询师进行一对一咨询。系统内置标准化心理测评量表（SCL-90），帮助学生了解自身心理状态，并提供多维度的测评报告。

## ✨ 核心功能

### 🎓 学生端

| 功能模块 | 说明 |
|---------|------|
| **AI 智能咨询** | 基于通义千问大模型的 AI 心理咨询师"心语"，提供 24 小时在线心理疏导，支持多轮对话、风险检测 |
| **咨询师浏览** | 查看咨询师列表、专业领域、资质证书、排班时间，支持在线预约 |
| **预约管理** | 预约线下/线上咨询，查看预约状态（待确认/已确认/已完成/已取消） |
| **即时聊天** | 与已预约的咨询师进行实时文字沟通 |
| **心理测评** | 基于 SCL-90 量表的标准化心理测评，涵盖躯体化、抑郁、焦虑、人际关系敏感等多个维度 |
| **个人中心** | 管理个人信息、修改密码、上传头像 |

### 👨‍⚕️ 咨询师端

| 功能模块 | 说明 |
|---------|------|
| **工作台** | 查看待办事项、今日预约、工作统计 |
| **预约管理** | 确认/拒绝/完成预约，记录咨询笔记 |
| **排班管理** | 设置每周可用时间段，管理咨询日程 |
| **患者沟通** | 与学生进行实时文字沟通，查看未读消息 |
| **个人资料** | 管理专业描述、擅长领域标签、资质证书 |

### 🔧 管理员端

| 功能模块 | 说明 |
|---------|------|
| **学生管理** | 查看、搜索、启用/禁用学生账号 |
| **咨询师管理** | 审核咨询师注册申请，管理咨询师状态（待审核/活跃/离线） |
| **公告管理** | 发布、编辑、上下架系统公告 |

## 🛠️ 技术栈

### 后端

| 技术 | 版本 | 说明 |
|-----|------|------|
| **Java** | 21 | 开发语言 |
| **Spring Boot** | 4.0.0 | Web 框架 |
| **MyBatis** | 4.0.0 | ORM 持久层框架 |
| **MySQL** | 8.x | 关系型数据库 |
| **JWT (jjwt)** | 0.12.3 | 身份认证 |
| **通义千问** | qwen-turbo | AI 大语言模型 |
| **Lombok** | — | 简化 Java 代码 |
| **Maven** | — | 项目构建管理 |

### 前端

| 技术 | 版本 | 说明 |
|-----|------|------|
| **Vue** | 3.5.x | 渐进式 JavaScript 框架 (Composition API) |
| **Vite** | 7.x | 下一代前端构建工具 |
| **Vue Router** | 4.x | 路由管理 |
| **Pinia** | 2.x | 状态管理 |
| **Axios** | 1.6.x | HTTP 请求库 |
| **Tailwind CSS** | 3.4.x | 原子化 CSS 框架 |
| **vue-cropper** | — | 头像裁剪组件 |

## 📁 项目结构

```
Psychological-Counseling-System/
├── schema.sql                          # 数据库建表脚本（16 张表）
├── init_assessment_data.sql            # SCL-90 心理测评量表初始数据（100 道题目）
│
├── Psychological Counseling System/    # 📦 后端 (Spring Boot)
│   ├── pom.xml                         # Maven 配置
│   ├── src/main/java/com/cqust/psychologicalcounseling/
│   │   ├── controller/                 # REST 控制器（7 个）
│   │   ├── service/                    # 业务逻辑层
│   │   │   └── impl/                   # 服务实现类
│   │   ├── mapper/                     # MyBatis Mapper 接口（14 个）
│   │   ├── entity/                     # 数据库实体类
│   │   ├── dto/                        # 数据传输对象
│   │   ├── config/                     # 配置类（CORS、MyBatis、Jackson 等）
│   │   ├── common/                     # 统一响应封装
│   │   ├── exception/                  # 全局异常处理
│   │   └── util/                       # 工具类（JWT、密码、AI 调用等）
│   └── src/main/resources/
│       ├── application.yml             # 应用配置
│       └── prompts/                    # AI 系统提示词
│
└── Psychological Counseling Vue3/      # 📦 前端 (Vue 3)
    ├── package.json                    # 依赖配置
    ├── vite.config.js                  # Vite 构建配置
    ├── tailwind.config.js              # Tailwind CSS 配置
    └── src/
        ├── router/index.js             # 路由配置（三端路由守卫）
        ├── api/index.js                # Axios 封装与拦截器
        ├── components/                 # 公共组件
        └── views/
            ├── common/                 # 登录、注册页面
            ├── student/                # 学生端页面（9 个）
            ├── doctor/                 # 咨询师端页面（5 个）
            └── admin/                  # 管理员端页面（2 个）
```

## 🗄️ 数据库设计

系统共包含 **16 张数据表**，核心表结构如下：

```
┌─────────────────────────────────────────────────────────┐
│                      用户体系                            │
│  user (学生)  │  doctor (咨询师)  │  admin (管理员)      │
├─────────────────────────────────────────────────────────┤
│                    预约系统                              │
│  appointment (预约记录)  │  doctor_schedule (排班)       │
├─────────────────────────────────────────────────────────┤
│                  AI 咨询系统                             │
│  chat_session (会话)  │  chat_message (消息)            │
├─────────────────────────────────────────────────────────┤
│                师生聊天系统                              │
│  doctor_chat_session (会话)  │  doctor_chat_message      │
├─────────────────────────────────────────────────────────┤
│                心理测评系统                              │
│  assessment_scale (量表)  │  assessment_question (题目)  │
│  assessment_record (记录)  │  assessment_answer (答案)   │
├─────────────────────────────────────────────────────────┤
│                    其他                                  │
│  doctor_tag  │  doctor_certification  │  announcement    │
└─────────────────────────────────────────────────────────┘
```

## 🚀 快速开始

### 环境要求

- **JDK** 21+
- **Node.js** >= 20.19.0 或 >= 22.12.0
- **MySQL** 8.0+
- **Maven** 3.9+（或使用项目自带的 Maven Wrapper）

### 1. 克隆项目

```bash
git clone https://github.com/fantaty1/Psychological-Counseling-System.git
cd Psychological-Counseling-System
```

### 2. 初始化数据库

```sql
-- 创建数据库
CREATE DATABASE psychological_counseling DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 导入表结构
mysql -u root -p psychological_counseling < schema.sql

-- 导入心理测评初始数据（可选）
mysql -u root -p psychological_counseling < init_assessment_data.sql
```

### 3. 配置后端

编辑 `Psychological Counseling System/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/psychological_counseling?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
    username: root          # 修改为你的 MySQL 用户名
    password: 1234          # 修改为你的 MySQL 密码

# AI 配置（需要阿里云通义千问 API Key）
ai:
  api-key: your-api-key-here
```

### 4. 启动后端

```bash
cd "Psychological Counseling System"
./mvnw spring-boot:run
```

后端服务将启动在 `http://localhost:8080`

### 5. 启动前端

```bash
cd "Psychological Counseling Vue3"
npm install
npm run dev
```

前端开发服务器将启动在 `http://localhost:5173`

### 6. 访问系统

| 角色 | 入口地址 |
|------|---------|
| 学生登录 | `http://localhost:5173/login` |
| 咨询师登录 | `http://localhost:5173/login` |
| 管理员登录 | `http://localhost:5173/admin/login` |

## 🔐 API 接口概览

| 模块 | 路径前缀 | 说明 |
|------|---------|------|
| 认证 | `/api/auth/*` | 登录、注册、Token 刷新 |
| 学生 | `/api/user/*` | 个人信息、预约、聊天 |
| 咨询师 | `/api/doctor/*` | 排班、预约管理、患者沟通 |
| AI 咨询 | `/api/ai/*` | AI 对话、会话管理 |
| 心理测评 | `/api/assessment/*` | 量表、答题、测评记录 |
| 管理 | `/api/admin/*` | 用户管理、公告管理 |
| 文件 | `/api/file/*` | 头像上传、文件上传 |

## 🤖 AI 咨询说明

系统集成阿里云 **通义千问 (qwen-turbo)** 大语言模型，打造了名为 **"心语"** 的 AI 心理咨询师角色：

- **共情沟通**：采用非评判、温暖的对话风格
- **风险检测**：基于关键词分析，自动识别对话中的心理风险等级（低/中/高）
- **危机干预**：检测到高风险关键词时，自动引导至专业危机干预资源
- **降级策略**：当 AI 服务不可用时，自动切换至本地关键词匹配的兜底回复

## 📊 心理测评说明

内置基于 **SCL-90（症状自评量表）** 的标准化心理测评：

| 量表 | 题目数 | 测评维度 |
|------|--------|---------|
| 量表 A — 情绪与躯体化 | 50 题 | 躯体化、抑郁、焦虑 |
| 量表 B — 人际与适应 | 50 题 | 人际关系敏感、敌对、偏执、强迫、恐怖 |

**评分标准**：
| 等级 | 分数范围 | 说明 |
|------|---------|------|
| 正常 | < 90 | 心理状态良好 |
| 轻度 | 90 - 110 | 存在轻度困扰，建议关注 |
| 中度 | 110 - 140 | 建议寻求专业帮助 |
| 重度 | ≥ 140 | 强烈建议寻求专业心理咨询 |

## 📸 项目截图

> 建议在此处添加项目截图，展示各端主要页面效果。

<!-- 示例：
![学生首页](screenshots/student-home.png)
![AI 咨询](screenshots/ai-chat.png)
![心理测评](screenshots/assessment.png)
![咨询师管理](screenshots/doctor-panel.png)
-->

## 📝 开发说明

### 角色与路由

前端通过 Vue Router 的 `beforeEach` 导航守卫实现三端路由隔离，每种角色使用独立的 JWT Token 存储键：

- `student_token` — 学生端
- `doctor_token` — 咨询师端
- `admin_token` — 管理端

### 密码安全

用户密码使用 SHA-256 算法进行哈希处理后存储，不保存明文密码。

### 软删除

`user`、`doctor`、`appointment` 表采用软删除策略，通过 `deleted_at` 字段标记删除时间。

## 📄 开源协议

本项目仅供学习交流使用。

## 👨‍💻 作者

- GitHub: [@Fantaty1](https://github.com/Fantaty1)

---

> 💡 如果这个项目对你有帮助，欢迎给一个 ⭐ Star 支持一下！
