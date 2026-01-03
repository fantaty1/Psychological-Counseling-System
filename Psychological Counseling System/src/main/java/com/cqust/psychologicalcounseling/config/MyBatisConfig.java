package com.cqust.psychologicalcounseling.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 * 配置Mapper扫描路径
 */
@Configuration
@MapperScan("com.cqust.psychologicalcounseling.mapper")
public class MyBatisConfig {
}
