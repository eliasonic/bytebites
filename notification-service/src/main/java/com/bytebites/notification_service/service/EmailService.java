package com.bytebites.notification_service.service;

import com.bytebites.notification_service.config.MailConfig;
import com.bytebites.notification_service.event.OrderPlacedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MailConfig mailConfig;

    public void sendOrderConfirmation(OrderPlacedEvent event) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        Context context = new Context();
        context.setVariable("orderId", event.orderId());
        context.setVariable("totalAmount", event.totalAmount());
        context.setVariable("orderDate", event.createdAt());

        String htmlContent = templateEngine.process("email-order-placed", context);

        helper.setTo(event.customerId());
        helper.setFrom(mailConfig.getFrom(), "ByteBites Inc");
        helper.setSubject("Order Confirmation #" + event.orderId());
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}