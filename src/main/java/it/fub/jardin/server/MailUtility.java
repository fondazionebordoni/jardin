/*
 * Copyright (c) 2010 Jardin Development Group <jardin.project@gmail.com>.
 * 
 * Jardin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Jardin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jardin.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.fub.jardin.server;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtility {

  private String mailSmtpHost;
  private String mailSmtpAuth;
  private String mailSmtpUser;
  private String mailSmtpPass;
  private String mailSmtpSender;
  private String mailSmtpSysadmin;

  private final static int smtpPort = 25;

  public MailUtility(String mailSmtpHost, String mailSmtpAuth,
      String mailSmtpUser, String mailSmtpPass, String mailSmtpSender, String mailSmtpSysadmin) {
    // TODO Auto-generated constructor stub
    setMailSmtpHost(mailSmtpHost);
    setMailSmtpAuth(mailSmtpAuth);
    setMailSmtpUser(mailSmtpUser);
    setMailSmtpPass(mailSmtpPass);
    setMailSmtpSender(mailSmtpSender);
    setMailSmtpSysadmin(mailSmtpSysadmin);
  }

  public void sendMail(final String dest, final String mitt,
      final String mailObject, final String mailText) throws MessagingException {
    // Creazione di una mail session
    Properties props = new Properties();
    props.put("mail.smtp.host", getMailSmtpHost());
    props.put("mail.smtp.auth", getMailSmtpAuth());
    props.put("mail.smtp.user", getMailSmtpUser());
    props.put("mail.smtp.pass", getMailSmtpPass());
    props.put("mail.smtp.sender", getMailSmtpSender());
    props.put("mail.smtp.sysadmin", getMailSmtpSysadmin());
    Session session = Session.getDefaultInstance(props);

    if (getMailSmtpAuth().compareToIgnoreCase("true") == 0) {
      session.setPasswordAuthentication(new URLName("smtp", getMailSmtpHost(),
          getSmtpPort(), "INBOX", getMailSmtpUser(), getMailSmtpPass()),
          new PasswordAuthentication(getMailSmtpUser(), getMailSmtpPass()));
    }

//    System.out.println("mail props: a-->" + getMailSmtpAuth() + ", h-->"
//        + getMailSmtpHost() + ", u-->" + getMailSmtpUser() + ", p-->"
//        + getMailSmtpPass() + ", s-->" + getMailSmtpSender());

    // Creazione del messaggio da inviare
    MimeMessage message = new MimeMessage(session);
    message.setSubject(mailObject);
    message.setText(mailText);

    // Aggiunta degli indirizzi del mittente e del destinatario
    InternetAddress fromAddress = new InternetAddress(getMailSmtpSender());
    InternetAddress toAddress = new InternetAddress(dest);
    message.setFrom(fromAddress);
    message.setRecipient(Message.RecipientType.TO, toAddress);

    // Invio del messaggio
    Transport tr = session.getTransport("smtp");
    tr.connect(getMailSmtpHost(), getMailSmtpUser(), getMailSmtpPass());
    message.saveChanges(); // don't forget this
    tr.sendMessage(message, message.getAllRecipients());
    tr.close();
//    Transport.send(message);
  }

  /**
   * @return the mailSmtpHost
   */
  public String getMailSmtpHost() {
    return mailSmtpHost;
  }

  /**
   * @param mailSmtpHost
   *          the mailSmtpHost to set
   */
  public void setMailSmtpHost(String mailSmtpHost) {
    this.mailSmtpHost = mailSmtpHost;
  }

  /**
   * @return the mailSmtpAuth
   */
  public String getMailSmtpAuth() {
    return mailSmtpAuth;
  }

  /**
   * @param mailSmtpAuth
   *          the mailSmtpAuth to set
   */
  public void setMailSmtpAuth(String mailSmtpAuth) {
    this.mailSmtpAuth = mailSmtpAuth;
  }

  /**
   * @return the mailSmtpUser
   */
  public String getMailSmtpUser() {
    return mailSmtpUser;
  }

  /**
   * @param mailSmtpUser
   *          the mailSmtpUser to set
   */
  public void setMailSmtpUser(String mailSmtpUser) {
    this.mailSmtpUser = mailSmtpUser;
  }

  /**
   * @return the mailSmtpPass
   */
  public String getMailSmtpPass() {
    return mailSmtpPass;
  }

  /**
   * @param mailSmtpPass
   *          the mailSmtpPass to set
   */
  public void setMailSmtpPass(String mailSmtpPass) {
    this.mailSmtpPass = mailSmtpPass;
  }

  /**
   * @return the mailSmtpSender
   */
  public String getMailSmtpSender() {
    return mailSmtpSender;
  }

  /**
   * @param mailSmtpSender
   *          the mailSmtpSender to set
   */
  public void setMailSmtpSender(String mailSmtpSender) {
    this.mailSmtpSender = mailSmtpSender;
  }

  /**
   * @return the smtpport
   */
  public static int getSmtpPort() {
    return smtpPort;
  }

  /**
   * @return the mailSmtpSysadmin
   */
  public String getMailSmtpSysadmin() {
    return mailSmtpSysadmin;
  }

  /**
   * @param mailSmtpSysadmin the mailSmtpSysadmin to set
   */
  public void setMailSmtpSysadmin(String mailSmtpSysadmin) {
    this.mailSmtpSysadmin = mailSmtpSysadmin;
  }
}
