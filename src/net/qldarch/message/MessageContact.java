package net.qldarch.message;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Properties;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;
import net.qldarch.configuration.Cfg;
import net.qldarch.guice.Bind;
import net.qldarch.security.UserStore;

import org.apache.commons.lang3.StringUtils;

@Bind
@Slf4j
public class MessageContact {

  @Inject
  private UserStore users;

  @Inject @Cfg("smtp.host")
  private String smtpHost;
  @Inject @Cfg("smtp.port")
  private String smtpPort;
  @Inject @Cfg("smtp.tls")
  private String smtptls;
  @Inject @Cfg("smtp.auth")
  private String smtpAuth;
  @Inject @Cfg("email.username")
  private String username;
  @Inject @Cfg("email.password")
  private String password;

  public MessageContactResponse send(String content, String senderName, String from, boolean newsletter) {
    try {
      String subject = "Qldarch Message from " + senderName + " <" + from + ">";
      String msg = (content + (newsletter ? "<br/>(Please send me the latest news via email)" : StringUtils.EMPTY))
          .replace("\n", "<br/>");
      final Properties properties = new Properties();
      properties.setProperty("mail.smtp.host", smtpHost);
      properties.setProperty("mail.smtp.port", smtpPort);
      properties.setProperty("mail.smtp.auth", smtpAuth);
      properties.setProperty("mail.smtp.starttls.enable", smtptls);
      properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
      properties.put("mail.smtp.ssl.trust", smtpHost);

      //char[] passwordArray = password.toCharArray();
      Session session; 

      if(smtpAuth.equalsIgnoreCase("true")) {
        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
      }else {
        session = Session.getDefaultInstance(properties);
      }
      MimeMessage message = new MimeMessage(session);
      users.all().forEach(user -> {
        if(user.isContact()) {
          try {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
          } catch(Exception e) {
            log.warn("adding a contact to recipient failed", e);
          }
        }
      });
      //message.setFrom(new InternetAddress("no-reply@uq.edu.au"));
      message.setFrom(new InternetAddress(username));
      message.setSubject(subject);
      message.setContent(msg, "text/html; charset=utf-8");
      Transport.send(message);
      return MessageContactResponse.ok();
    } catch(Exception e) {
      log.warn("message contact failed", e);
      return MessageContactResponse.failed("Message contact failed");
    }
  }
}
