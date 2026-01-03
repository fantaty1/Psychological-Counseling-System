package com.cqust.psychologicalcounseling.util;

/**
 * 临时密码生成工具
 * 运行此类生成管理员密码，然后更新数据库
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        String password = "admin123";
        String encoded = PasswordUtil.encode(password);
        System.out.println("原始密码: " + password);
        System.out.println("加密后密码: " + encoded);
        System.out.println();
        System.out.println("SQL更新语句:");
        System.out.println("UPDATE admin SET password = '" + encoded + "' WHERE username = 'admin';");
    }
}
