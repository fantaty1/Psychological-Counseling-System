-- 心理测评量表初始化数据
-- 将原 SCL-90 (100道题) 拆分为两个各 50 道题的量表
-- 执行前请确保已创建 assessment_scale 和 assessment_question 表

-- 清除旧数据（如需要）
-- DELETE FROM assessment_answer;
-- DELETE FROM assessment_record;
-- DELETE FROM assessment_question;
-- DELETE FROM assessment_scale;

-- ============================================
-- 量表1: 心理健康自测量表A (SCL-50A) - 情绪与躯体化
-- ============================================
INSERT INTO assessment_scale (id, name, description, category, question_count, estimated_time, instructions, status, sort) VALUES
(1, '心理健康自测量表A(情绪篇)', 
'本量表用于评估您近期的情绪状态和躯体化症状，包括焦虑、抑郁、躯体不适等维度。通过回答50道题目，帮助您了解自己的心理健康状况。', 
'情绪', 50, 10, 
'请根据您最近一周的实际感受，选择最符合您情况的选项。每道题目有5个选项：没有、有一点、中等程度、相当多、非常多。请仔细阅读每道题目，凭第一感觉作答，不要在某一题上花费太多时间。', 
1, 1);

-- ============================================
-- 量表2: 心理健康自测量表B (SCL-50B) - 人际与适应
-- ============================================
INSERT INTO assessment_scale (id, name, description, category, question_count, estimated_time, instructions, status, sort) VALUES
(2, '心理健康自测量表B(人际篇)', 
'本量表用于评估您的人际关系、社会适应和心理调适能力，包括人际敏感、偏执、敌对等维度。通过回答50道题目，帮助您了解自己的心理健康状况。', 
'人际', 50, 10, 
'请根据您最近一周的实际感受，选择最符合您情况的选项。每道题目有5个选项：没有、有一点、中等程度、相当多、非常多。请仔细阅读每道题目，凭第一感觉作答，不要在某一题上花费太多时间。', 
1, 2);

-- ============================================
-- 量表A 题目 (1-50题) - 情绪与躯体化
-- 维度：躯体化(SOM)、抑郁(DEP)、焦虑(ANX)
-- ============================================

-- 躯体化维度题目 (1-15)
INSERT INTO assessment_question (scale_id, question_no, content, question_type, options, reverse_score, dimension) VALUES
(1, 1, '头痛', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '躯体化'),
(1, 2, '神经过敏，心中不踏实', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '躯体化'),
(1, 3, '头脑中有不必要的想法或字句盘旋', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '躯体化'),
(1, 4, '头晕或昏倒', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '躯体化'),
(1, 5, '对异性的兴趣减退', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '躯体化'),
(1, 6, '对旁人责备求全', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '躯体化'),
(1, 7, '感到别人能控制您的思想', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '躯体化'),
(1, 8, '责怪别人制造麻烦', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '躯体化'),
(1, 9, '忘记性大', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '躯体化'),
(1, 10, '担心自己的衣饰整齐及仪态的端正', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '躯体化'),
(1, 11, '容易烦恼和激动', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '躯体化'),
(1, 12, '胸痛', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '躯体化'),
(1, 13, '害怕空旷的场所或街道', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '躯体化'),
(1, 14, '感到自己的精力下降，活动减慢', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '躯体化'),
(1, 15, '想结束自己的生命', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '躯体化'),

-- 抑郁维度题目 (16-32)
(1, 16, '听到旁人听不到的声音', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 17, '发抖', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 18, '感到大多数人都不可信任', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 19, '胃口不好', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 20, '容易哭泣', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 21, '同异性相处时感到害羞不自在', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 22, '感到受骗、中了圈套或有人想抓住您', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 23, '无缘无故地突然感到害怕', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 24, '自己不能控制地大发脾气', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 25, '怕单独出门', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 26, '经常责怪自己', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 27, '腰痛', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 28, '感到难以完成任务', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 29, '感到孤独', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 30, '感到苦闷', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 31, '过分担忧', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),
(1, 32, '对事物不感兴趣', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '抑郁'),

-- 焦虑维度题目 (33-50)
(1, 33, '感到害怕', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 34, '您的感情容易受到伤害', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 35, '旁人能知道您的私下想法', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 36, '感到别人不理解您、不同情您', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 37, '感到人们对您不友好，不喜欢您', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 38, '做事必须做得很慢以保证做得正确', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 39, '心跳得很厉害', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 40, '恶心或胃部不舒服', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 41, '感到比不上他人', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 42, '肌肉酸痛', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 43, '感到有人在监视您、谈论您', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 44, '难以入睡', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 45, '做事必须反复检查', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 46, '难以做出决定', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 47, '怕乘电车、公共汽车、地铁或火车', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 48, '呼吸有困难', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 49, '一阵阵发冷或发热', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑'),
(1, 50, '因为感到害怕而避开某些东西、场合或活动', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '焦虑');

-- ============================================
-- 量表B 题目 (1-50题) - 人际与适应
-- 维度：人际敏感(INT)、敌对(HOS)、偏执(PAR)、强迫(OBS)、恐惧(PHO)
-- ============================================

INSERT INTO assessment_question (scale_id, question_no, content, question_type, options, reverse_score, dimension) VALUES
-- 人际敏感维度题目 (1-12)
(2, 1, '感到别人占了您的便宜', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '人际敏感'),
(2, 2, '在人群中感到不自在', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '人际敏感'),
(2, 3, '感到自卑', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '人际敏感'),
(2, 4, '太多时候感觉到别人对您有敌意', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '人际敏感'),
(2, 5, '感到别人不尊重您', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '人际敏感'),
(2, 6, '与他人比较时，总觉得自己不如他们', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '人际敏感'),
(2, 7, '感到坐立不安心神不定', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '人际敏感'),
(2, 8, '感到自己没有什么价值', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '人际敏感'),
(2, 9, '感到有人对您存心不良', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '人际敏感'),
(2, 10, '大多数时候总感到有罪过', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '人际敏感'),
(2, 11, '感到自己的思维被别人知道', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '人际敏感'),
(2, 12, '除了一两个亲近的人，再没有人值得您信任', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '人际敏感'),

-- 敌对维度题目 (13-22)
(2, 13, '脾气暴躁，不可控制', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '敌对'),
(2, 14, '想要打人或伤害他人', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '敌对'),
(2, 15, '想要砸碎什么东西', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '敌对'),
(2, 16, '经常与人争论', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '敌对'),
(2, 17, '大叫或摔东西', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '敌对'),
(2, 18, '有想要杀人的冲动', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '敌对'),
(2, 19, '想要摧毁或伤害某些东西', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '敌对'),
(2, 20, '经常威胁别人', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '敌对'),
(2, 21, '感到紧张或容易紧张', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '敌对'),
(2, 22, '感到别人总是故意刁难您', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '敌对'),

-- 偏执维度题目 (23-32)
(2, 23, '感到大多数人对您不可信任', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '偏执'),
(2, 24, '感到您需要时刻保持警惕', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '偏执'),
(2, 25, '感到别人在背后议论您', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '偏执'),
(2, 26, '感到别人对您有不良企图', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '偏执'),
(2, 27, '感到别人在暗中观察和监视您', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '偏执'),
(2, 28, '感到自己被跟踪或有人企图伤害您', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '偏执'),
(2, 29, '别人对您的成就不给予适当的评价', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '偏执'),
(2, 30, '感觉别人会利用您', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '偏执'),
(2, 31, '感到必须反复洗手、点数目或触摸某些东西', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '偏执'),
(2, 32, '脑子变空了', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '偏执'),

-- 强迫维度题目 (33-42)
(2, 33, '感到脑子里有些不受控制的念头', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '强迫'),
(2, 34, '必须一遍遍地重复做某些动作', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '强迫'),
(2, 35, '对做完的事不放心，反复检查', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '强迫'),
(2, 36, '难以集中注意力', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '强迫'),
(2, 37, '必须很慢地做事才能确保正确', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '强迫'),
(2, 38, '感到事事都很困难', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '强迫'),
(2, 39, '感到前途没有希望', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '强迫'),
(2, 40, '要花很多时间才能做完事情', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '强迫'),
(2, 41, '感到自己毫无价值', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '强迫'),
(2, 42, '感到熟悉的东西变成陌生或不真实', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '强迫'),

-- 恐惧维度题目 (43-50)
(2, 43, '害怕在公共场合晕倒', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '恐惧'),
(2, 44, '害怕独处', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '恐惧'),
(2, 45, '害怕乘坐公共交通工具', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '恐惧'),
(2, 46, '必须避开某些事物、场所或活动因为它们使您害怕', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '恐惧'),
(2, 47, '想到有关死亡的事', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '恐惧'),
(2, 48, '睡眠不深不稳', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '恐惧'),
(2, 49, '早醒', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '恐惧'),
(2, 50, '睡得不安稳', 'single', '[{"label":"没有","value":0},{"label":"有一点","value":1},{"label":"中等程度","value":2},{"label":"相当多","value":3},{"label":"非常多","value":4}]', 0, '恐惧');

-- ============================================
-- 评分说明 (50题量表)
-- 总分范围: 0-200 (每题0-4分，共50题)
-- 正常: 总分 < 90
-- 轻度: 90-110
-- 中度: 110-140
-- 重度: >= 140
-- ============================================
