-- Psychological Counseling System schema
-- All tables numbered; indexes declared after each table.

-- 1) admin
CREATE TABLE admin (
    id BIGINT AUTO_INCREMENT COMMENT '管理员ID，主键' PRIMARY KEY,
    username VARCHAR(50) NOT NULL COMMENT '用户名（唯一）',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    name VARCHAR(50) NULL COMMENT '姓名',
    role VARCHAR(20) NOT NULL DEFAULT 'admin' COMMENT '角色：admin-管理员 super-超级管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    last_login_at DATETIME NULL COMMENT '最后登录时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_username UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 2) announcement
CREATE TABLE announcement (
    id BIGINT AUTO_INCREMENT COMMENT '公告ID，主键' PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT NULL COMMENT '公告内容',
    date DATE NOT NULL COMMENT '发布日期',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-下架 1-发布',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序权重',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';
CREATE INDEX idx_status_date ON announcement (status, date);

-- 3) appointment
CREATE TABLE appointment (
    id BIGINT AUTO_INCREMENT COMMENT '预约ID，主键' PRIMARY KEY,
    student_id BIGINT NOT NULL COMMENT '学生用户ID',
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    appointment_date DATE NOT NULL COMMENT '预约日期',
    time_slot VARCHAR(10) NOT NULL COMMENT '时间段（如：14:00）',
    type VARCHAR(20) NOT NULL DEFAULT 'offline' COMMENT '咨询方式：offline-线下 online-线上',
    status VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending/confirmed/completed/cancelled',
    location VARCHAR(200) NULL COMMENT '咨询地点',
    description TEXT NULL COMMENT '问题描述',
    reject_reason VARCHAR(500) NULL COMMENT '拒绝原因',
    notes TEXT NULL COMMENT '咨询备注（完成后填写）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME NULL COMMENT '删除时间（软删除）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约记录表';
CREATE INDEX idx_appointment_date ON appointment (appointment_date);
CREATE INDEX idx_doctor_id ON appointment (doctor_id);
CREATE INDEX idx_status ON appointment (status);
CREATE INDEX idx_student_id ON appointment (student_id);

-- 4) assessment_answer
CREATE TABLE assessment_answer (
    id BIGINT AUTO_INCREMENT COMMENT '答案ID，主键' PRIMARY KEY,
    record_id BIGINT NOT NULL COMMENT '测评记录ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    answer_value INT NOT NULL COMMENT '选择的答案值',
    score INT NOT NULL DEFAULT 0 COMMENT '得分',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT uk_record_question UNIQUE (record_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户答题记录表';
CREATE INDEX idx_question_id ON assessment_answer (question_id);
CREATE INDEX idx_record_id ON assessment_answer (record_id);

-- 5) assessment_question
CREATE TABLE assessment_question (
    id BIGINT AUTO_INCREMENT COMMENT '题目ID，主键' PRIMARY KEY,
    scale_id BIGINT NOT NULL COMMENT '所属量表ID',
    question_no INT NOT NULL COMMENT '题目序号',
    content TEXT NOT NULL COMMENT '题目内容',
    question_type VARCHAR(20) NOT NULL DEFAULT 'single' COMMENT '题目类型：single-单选 multiple-多选',
    options JSON NOT NULL COMMENT '选项（JSON数组，如：[{"label":"完全没有","value":0},...]）',
    reverse_score TINYINT NOT NULL DEFAULT 0 COMMENT '是否反向计分：0-否 1-是',
    dimension VARCHAR(50) NULL COMMENT '维度/因子（用于分类统计）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测评题目表';
CREATE INDEX idx_question_no ON assessment_question (scale_id, question_no);
CREATE INDEX idx_scale_id_question ON assessment_question (scale_id);

-- 6) assessment_record
CREATE TABLE assessment_record (
    id BIGINT AUTO_INCREMENT COMMENT '记录ID，主键' PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    scale_id BIGINT NOT NULL COMMENT '量表ID',
    total_score INT NULL COMMENT '总分',
    result_level VARCHAR(50) NULL COMMENT '结果等级（如：正常、轻度、中度、重度）',
    result_summary TEXT NULL COMMENT '结果摘要',
    duration INT NULL COMMENT '答题用时（秒）',
    status VARCHAR(20) NOT NULL DEFAULT 'in_progress' COMMENT '状态：in_progress-进行中 completed-已完成',
    started_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    completed_at DATETIME NULL COMMENT '完成时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户测评记录表';
CREATE INDEX idx_created_at_record ON assessment_record (created_at);
CREATE INDEX idx_scale_id_record ON assessment_record (scale_id);
CREATE INDEX idx_status_record ON assessment_record (status);
CREATE INDEX idx_user_id_record ON assessment_record (user_id);

-- 7) assessment_scale
CREATE TABLE assessment_scale (
    id BIGINT AUTO_INCREMENT COMMENT '量表ID，主键' PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '量表名称',
    description TEXT NULL COMMENT '量表描述',
    category VARCHAR(50) NULL COMMENT '分类（如：情绪、人际、压力）',
    question_count INT NOT NULL DEFAULT 0 COMMENT '题目数量',
    estimated_time INT NULL DEFAULT 10 COMMENT '预计完成时间（分钟）',
    instructions TEXT NULL COMMENT '答题说明',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序权重',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='心理测评量表表';
CREATE INDEX idx_category ON assessment_scale (category);
CREATE INDEX idx_status_scale ON assessment_scale (status);

-- 8) chat_message
CREATE TABLE chat_message (
    id BIGINT AUTO_INCREMENT COMMENT '消息ID，主键' PRIMARY KEY,
    session_id VARCHAR(50) NOT NULL COMMENT '会话ID',
    role VARCHAR(20) NOT NULL COMMENT '角色：user-用户 assistant-AI',
    content TEXT NOT NULL COMMENT '消息内容',
    type VARCHAR(20) NOT NULL DEFAULT 'text' COMMENT '消息类型：text/image/card',
    risk_level VARCHAR(20) NULL COMMENT '风险等级：low/medium/high',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI聊天消息表';
CREATE INDEX idx_created_at_chat_message ON chat_message (created_at);
CREATE INDEX idx_session_id ON chat_message (session_id);

-- 9) chat_session
CREATE TABLE chat_session (
    id BIGINT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    session_id VARCHAR(50) NOT NULL COMMENT '会话UUID（唯一）',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(100) NULL DEFAULT '新会话' COMMENT '会话标题',
    last_message TEXT NULL COMMENT '最后一条消息内容',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已删除 1-正常',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_session_id UNIQUE (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI会话表';
CREATE INDEX idx_updated_at_chat_session ON chat_session (updated_at);
CREATE INDEX idx_user_id_chat_session ON chat_session (user_id);

-- 10) doctor
CREATE TABLE doctor (
    id BIGINT AUTO_INCREMENT COMMENT '医生ID，主键' PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    title VARCHAR(50) NULL COMMENT '职称（如：注册心理师）',
    avatar VARCHAR(500) NULL COMMENT '头像URL',
    password VARCHAR(255) NOT NULL COMMENT '登录密码（加密）',
    phone VARCHAR(20) NULL COMMENT '联系电话',
    email VARCHAR(100) NULL COMMENT '电子邮箱',
    gender TINYINT NULL COMMENT '性别：0-未知 1-男 2-女',
    years INT NULL DEFAULT 0 COMMENT '从业年限',
    description TEXT NULL COMMENT '个人简介',
    tags TEXT NULL COMMENT '擅长领域标签（JSON数组）',
    methods VARCHAR(500) NULL COMMENT '咨询方法（JSON数组）',
    certifications TEXT NULL COMMENT '资质认证（JSON数组）',
    rating DECIMAL(2,1) NOT NULL DEFAULT 5.0 COMMENT '评分（1-5）',
    total_hours INT NOT NULL DEFAULT 0 COMMENT '累计咨询时长',
    helped_count INT NOT NULL DEFAULT 0 COMMENT '帮助人数',
    positive_rate INT NOT NULL DEFAULT 100 COMMENT '好评率（百分比）',
    location VARCHAR(200) NULL COMMENT '线下咨询地址',
    available TINYINT NOT NULL DEFAULT 1 COMMENT '是否可预约：0-否 1-是',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待审核 1-正常 2-下架',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME NULL COMMENT '删除时间（软删除）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='咨询师/医生表';
CREATE INDEX idx_available_doctor ON doctor (available);
CREATE INDEX idx_rating ON doctor (rating);
CREATE INDEX idx_status_doctor ON doctor (status);

-- 11) doctor_certification
CREATE TABLE doctor_certification (
    id BIGINT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    certification VARCHAR(200) NOT NULL COMMENT '资质名称',
    cert_number VARCHAR(100) NULL COMMENT '证书编号',
    cert_image VARCHAR(500) NULL COMMENT '证书图片URL',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生资质认证表';
CREATE INDEX idx_doctor_id_certification ON doctor_certification (doctor_id);

-- 12) doctor_chat_message
CREATE TABLE doctor_chat_message (
    id BIGINT AUTO_INCREMENT COMMENT '消息ID，主键' PRIMARY KEY,
    session_id VARCHAR(50) NOT NULL COMMENT '会话ID',
    role VARCHAR(20) NOT NULL COMMENT '角色：student-学生 doctor-咨询师',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    content TEXT NOT NULL COMMENT '消息内容',
    is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读 1-已读',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生-学生聊天消息表';
CREATE INDEX idx_created_at_doctor_chat_message ON doctor_chat_message (created_at);
CREATE INDEX idx_is_read ON doctor_chat_message (is_read);
CREATE INDEX idx_session_id_doctor_chat_message ON doctor_chat_message (session_id);

-- 13) doctor_chat_session
CREATE TABLE doctor_chat_session (
    id BIGINT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    session_id VARCHAR(50) NOT NULL COMMENT '会话UUID（唯一）',
    doctor_id BIGINT NOT NULL COMMENT '咨询师ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    last_message VARCHAR(200) NULL COMMENT '最后一条消息内容',
    doctor_unread_count INT NOT NULL DEFAULT 0 COMMENT '咨询师未读消息数',
    student_unread_count INT NOT NULL DEFAULT 0 COMMENT '学生未读消息数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_doctor_student UNIQUE (doctor_id, student_id),
    CONSTRAINT uk_session_id_doctor_chat_session UNIQUE (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生-学生聊天会话表';
CREATE INDEX idx_doctor_id_chat_session ON doctor_chat_session (doctor_id);
CREATE INDEX idx_student_id_chat_session ON doctor_chat_session (student_id);
CREATE INDEX idx_updated_at_doctor_chat_session ON doctor_chat_session (updated_at);

-- 14) doctor_schedule
CREATE TABLE doctor_schedule (
    id BIGINT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    schedule_date DATE NOT NULL COMMENT '日期',
    time_slot VARCHAR(10) NOT NULL COMMENT '时间段（如：09:00、14:00）',
    available TINYINT NOT NULL DEFAULT 1 COMMENT '是否可预约：0-已约/不可用 1-可约',
    booked TINYINT NOT NULL DEFAULT 0 COMMENT '是否已被预约：0-否 1-是',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_doctor_date_time UNIQUE (doctor_id, schedule_date, time_slot)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生排班时间表';
CREATE INDEX idx_available_doctor_schedule ON doctor_schedule (available);
CREATE INDEX idx_schedule_date ON doctor_schedule (schedule_date);

-- 15) doctor_tag
CREATE TABLE doctor_tag (
    id BIGINT AUTO_INCREMENT COMMENT '主键' PRIMARY KEY,
    doctor_id BIGINT NOT NULL COMMENT '医生ID',
    tag VARCHAR(50) NOT NULL COMMENT '标签名称（如：焦虑抑郁、个人成长）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='医生擅长领域标签表';
CREATE INDEX idx_doctor_id_doctor_tag ON doctor_tag (doctor_id);
CREATE INDEX idx_tag_doctor_tag ON doctor_tag (tag);

-- 16) user
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT COMMENT '用户ID，主键' PRIMARY KEY,
    username VARCHAR(50) NOT NULL COMMENT '用户名/姓名',
    student_id VARCHAR(20) NOT NULL COMMENT '学号（唯一）',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    email VARCHAR(100) NULL COMMENT '电子邮箱',
    phone VARCHAR(20) NULL COMMENT '手机号码',
    avatar VARCHAR(500) NULL COMMENT '头像URL',
    college VARCHAR(100) NULL COMMENT '学院',
    major_class VARCHAR(100) NULL COMMENT '专业班级',
    gender TINYINT NULL COMMENT '性别：0-未知 1-男 2-女',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    checkin_streak INT NOT NULL DEFAULT 0 COMMENT '连续签到天数',
    counseling_hours DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '累计咨询时长（小时）',
    appointments_count INT NOT NULL DEFAULT 0 COMMENT '累计预约次数',
    allow_peer_view TINYINT NOT NULL DEFAULT 1 COMMENT '是否允许心理委员查看信息',
    allow_notification TINYINT NOT NULL DEFAULT 1 COMMENT '是否允许接收通知',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME NULL COMMENT '删除时间（软删除）',
    CONSTRAINT uk_email UNIQUE (email),
    CONSTRAINT uk_student_id UNIQUE (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表（学生）';
CREATE INDEX idx_phone ON user (phone);
-- 管理员初始化SQL脚本
-- 插入默认管理员账号
-- 用户名: admin
-- 密码: admin123 (使用SHA-256加密，与PasswordUtil.encode()方式一致)
-- 注意：生产环境请修改密码

INSERT INTO admin (username, password, name, role, status, created_at, updated_at)
VALUES (
    'admin',
    'GsQjMvC4GN0UsLHDC6GtwQ9jpGfWl6Em0xPkr+2v57HsWRCODOL+xWWD18X+073Z', -- admin123 的SHA-256加密值
    '系统管理员',
    'admin',
    1,
    NOW(),
    NOW()
);

-- 插入测试公告数据
INSERT INTO announcement (title, content, date, status, sort, created_at, updated_at)
VALUES 
(
    '关于期末考试期间心理咨询中心延长开放时间的通知',
    '为帮助同学们缓解期末考试压力，心理咨询中心将在考试周期间延长开放时间至晚间22:00，欢迎同学们预约咨询。',
    '2025-12-15',
    1,
    10,
    NOW(),
    NOW()
),
(
    '"拥抱阳光"冬季心理健康讲座报名开启',
    '由我校心理健康教育中心主办的"拥抱阳光"冬季心理健康系列讲座即将开始，欢迎同学们踊跃报名参加。',
    '2025-12-10',
    1,
    5,
    NOW(),
    NOW()
),
(
    '心理咨询预约系统维护公告',
    '为提升系统服务质量，心理咨询预约系统将于12月1日进行升级维护，维护期间暂停在线预约服务，请同学们合理安排预约时间。',
    '2025-12-01',
    1,
    0,
    NOW(),
    NOW()
);