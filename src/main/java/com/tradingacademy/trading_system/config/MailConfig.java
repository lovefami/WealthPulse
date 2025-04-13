//package com.tradingacademy.trading_system.config;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//import java.util.Properties;
//
//@Configuration
//public class MailConfig {
//    @Bean
//    public JavaMailSender javaMailSender(){
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587);
//        mailSender.setUsername("tmi.investment2015@gmail.com");
//        mailSender.setPassword("TongMaximaIvanna@1983");
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.smtp.auth","true");
//        props.put("mail.smtp.starttls.eanble","true");
//
//        return mailSender;
//
//
//
//    }
//
//
//}
