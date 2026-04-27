package com.saasauth.multitenant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;

  @Value("${MAIL_FROM}")
  private String fromEmail;

  @Async
  public void sendWelcomeEmail(String to, String name, String tenantName) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(fromEmail);
      helper.setTo(to);
      helper.setSubject("Welcome to " + tenantName);
      helper.setText(buildWelcomeEmail(name, tenantName), true);
      mailSender.send(message);
      log.info("Welcome email sent to {}", to);
    } catch (MessagingException e) {
      log.error("Failed to send welcome email to {}: {}", to, e.getMessage());
    }
  }

  @Async
  public void sendPasswordChangedEmail(String to, String name) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(fromEmail);
      helper.setTo(to);
      helper.setSubject("Your Password Has Been Changed");
      helper.setText(buildPasswordChangedEmail(name), true);
      mailSender.send(message);
      log.info("Password changed email sent to {}", to);
    } catch (MessagingException e) {
      log.error("Failed to send password changed email to {}: {}", to, e.getMessage());
    }
  }

  @Async
  public void sendPasswordResetEmail(String to, String name, String resetToken) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(fromEmail);
      helper.setTo(to);
      helper.setSubject("Password Reset Request");
      helper.setText(buildPasswordResetEmail(name, resetToken), true);
      mailSender.send(message);
      log.info("Password reset email sent to {}", to);
    } catch (MessagingException e) {
      log.error("Failed to send password reset email to {}: {}", to, e.getMessage());
    }
  }

  private String buildWelcomeEmail(String name, String tenantName) {
    return """
        <!DOCTYPE html>
        <html>
        <body style="margin:0; padding:0; background-color:#f0f2f5; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0">
            <tr>
              <td align="center" style="padding:48px 16px;">
                <table width="560" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:6px; border:1px solid #e2e5ea;">

                  <!-- Top Accent Bar -->
                  <tr>
                    <td style="background-color:#6366f1; height:4px; border-radius:6px 6px 0 0;"></td>
                  </tr>

                  <!-- Header -->
                  <tr>
                    <td style="padding:36px 48px 24px;">
                      <p style="margin:0; font-size:11px; font-weight:600; letter-spacing:1.5px; color:#6366f1; text-transform:uppercase;">%s</p>
                      <h1 style="margin:10px 0 0; font-size:22px; font-weight:600; color:#1e1e2d;">Welcome Aboard</h1>
                    </td>
                  </tr>

                  <!-- Divider -->
                  <tr>
                    <td style="padding:0 48px;">
                      <hr style="border:none; border-top:1px solid #f0f2f5; margin:0;">
                    </td>
                  </tr>

                  <!-- Body -->
                  <tr>
                    <td style="padding:28px 48px 36px;">
                      <p style="margin:0 0 16px; font-size:15px; color:#374151; line-height:1.7;">Dear <strong>%s</strong>,</p>
                      <p style="margin:0 0 16px; font-size:15px; color:#374151; line-height:1.7;">
                        Your account on <strong>%s</strong> has been successfully created. You now have full access to the platform.
                      </p>
                      <p style="margin:0; font-size:15px; color:#374151; line-height:1.7;">
                        Should you have any questions or need assistance, please do not hesitate to contact us.
                      </p>
                    </td>
                  </tr>

                  <!-- Footer -->
                  <tr>
                    <td style="padding:20px 48px 32px; border-top:1px solid #f0f2f5;">
                      <p style="margin:0 0 4px; font-size:13px; color:#9ca3af;">This email was sent by</p>
                      <p style="margin:0; font-size:13px; color:#6b7280; font-weight:600;">Mohamed Hassan &mdash; Software Engineer</p>
                      <p style="margin:8px 0 0; font-size:12px; color:#d1d5db;">If you did not create this account, please ignore this email.</p>
                    </td>
                  </tr>

                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """
        .formatted(tenantName, name, tenantName);
  }

  private String buildPasswordChangedEmail(String name) {
    return """
        <!DOCTYPE html>
        <html>
        <body style="margin:0; padding:0; background-color:#f0f2f5; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0">
            <tr>
              <td align="center" style="padding:48px 16px;">
                <table width="560" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:6px; border:1px solid #e2e5ea;">

                  <!-- Top Accent Bar -->
                  <tr>
                    <td style="background-color:#f59e0b; height:4px; border-radius:6px 6px 0 0;"></td>
                  </tr>

                  <!-- Header -->
                  <tr>
                    <td style="padding:36px 48px 24px;">
                      <p style="margin:0; font-size:11px; font-weight:600; letter-spacing:1.5px; color:#f59e0b; text-transform:uppercase;">Security Notice</p>
                      <h1 style="margin:10px 0 0; font-size:22px; font-weight:600; color:#1e1e2d;">Password Changed</h1>
                    </td>
                  </tr>

                  <!-- Divider -->
                  <tr>
                    <td style="padding:0 48px;">
                      <hr style="border:none; border-top:1px solid #f0f2f5; margin:0;">
                    </td>
                  </tr>

                  <!-- Body -->
                  <tr>
                    <td style="padding:28px 48px 36px;">
                      <p style="margin:0 0 16px; font-size:15px; color:#374151; line-height:1.7;">Dear <strong>%s</strong>,</p>
                      <p style="margin:0 0 16px; font-size:15px; color:#374151; line-height:1.7;">
                        Your account password has been successfully updated.
                      </p>

                      <!-- Info Box -->
                      <table width="100%%" cellpadding="0" cellspacing="0" style="margin:8px 0 20px;">
                        <tr>
                          <td style="background-color:#fffbeb; border-left:3px solid #f59e0b; border-radius:4px; padding:14px 18px;">
                            <p style="margin:0; font-size:14px; color:#92400e; line-height:1.6;">
                              If you did not make this change, please contact support immediately as your account may be compromised.
                            </p>
                          </td>
                        </tr>
                      </table>

                    </td>
                  </tr>

                  <!-- Footer -->
                  <tr>
                    <td style="padding:20px 48px 32px; border-top:1px solid #f0f2f5;">
                      <p style="margin:0 0 4px; font-size:13px; color:#9ca3af;">This email was sent by</p>
                      <p style="margin:0; font-size:13px; color:#6b7280; font-weight:600;">Mohamed Hassan &mdash; Software Engineer</p>
                      <p style="margin:8px 0 0; font-size:12px; color:#d1d5db;">This is an automated message. Please do not reply to this email.</p>
                    </td>
                  </tr>

                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """
        .formatted(name);
  }

  private String buildPasswordResetEmail(String name, String resetToken) {
    return """
        <!DOCTYPE html>
        <html>
        <body style="margin:0; padding:0; background-color:#f0f2f5; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0">
            <tr>
              <td align="center" style="padding:48px 16px;">
                <table width="560" cellpadding="0" cellspacing="0" style="background-color:#ffffff; border-radius:6px; border:1px solid #e2e5ea;">

                  <!-- Top Accent Bar -->
                  <tr>
                    <td style="background-color:#6366f1; height:4px; border-radius:6px 6px 0 0;"></td>
                  </tr>

                  <!-- Header -->
                  <tr>
                    <td style="padding:36px 48px 24px;">
                      <p style="margin:0; font-size:11px; font-weight:600; letter-spacing:1.5px; color:#6366f1; text-transform:uppercase;">Account Security</p>
                      <h1 style="margin:10px 0 0; font-size:22px; font-weight:600; color:#1e1e2d;">Password Reset</h1>
                    </td>
                  </tr>

                  <!-- Divider -->
                  <tr>
                    <td style="padding:0 48px;">
                      <hr style="border:none; border-top:1px solid #f0f2f5; margin:0;">
                    </td>
                  </tr>

                  <!-- Body -->
                  <tr>
                    <td style="padding:28px 48px 36px;">
                      <p style="margin:0 0 16px; font-size:15px; color:#374151; line-height:1.7;">Dear <strong>%s</strong>,</p>
                      <p style="margin:0 0 24px; font-size:15px; color:#374151; line-height:1.7;">
                        We received a request to reset your password. Please use the verification token below to continue.
                      </p>

                      <!-- Token Box -->
                      <table width="100%%" cellpadding="0" cellspacing="0">
                        <tr>
                          <td style="background-color:#f8f9fb; border:1px solid #e2e5ea; border-radius:6px; padding:24px; text-align:center;">
                            <p style="margin:0 0 6px; font-size:11px; font-weight:600; letter-spacing:1.5px; color:#9ca3af; text-transform:uppercase;">Your Reset Token</p>
                            <p style="margin:0; font-size:30px; font-weight:700; letter-spacing:8px; color:#1e1e2d; font-family:monospace;">%s</p>
                          </td>
                        </tr>
                      </table>

                      <!-- Expiry Notice -->
                      <table width="100%%" cellpadding="0" cellspacing="0" style="margin-top:16px;">
                        <tr>
                          <td style="background-color:#f8f9fb; border-left:3px solid #6366f1; border-radius:4px; padding:12px 16px;">
                            <p style="margin:0; font-size:13px; color:#6b7280; line-height:1.6;">
                              This token expires in <strong>15 minutes</strong>. Do not share it with anyone.
                            </p>
                          </td>
                        </tr>
                      </table>

                      <p style="margin:20px 0 0; font-size:14px; color:#9ca3af; line-height:1.7;">
                        If you did not request a password reset, no action is required.
                      </p>
                    </td>
                  </tr>

                  <!-- Footer -->
                  <tr>
                    <td style="padding:20px 48px 32px; border-top:1px solid #f0f2f5;">
                      <p style="margin:0 0 4px; font-size:13px; color:#9ca3af;">This email was sent by</p>
                      <p style="margin:0; font-size:13px; color:#6b7280; font-weight:600;">Mohamed Hassan &mdash; Software Engineer</p>
                      <p style="margin:8px 0 0; font-size:12px; color:#d1d5db;">This is an automated message. Please do not reply to this email.</p>
                    </td>
                  </tr>

                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """
        .formatted(name, resetToken);
  }
}