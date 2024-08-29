import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Arrays;
import java.util.Comparator;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.List;

public class EmailReceiver {
    private String host;
    private String username;
    private String password;

    public EmailReceiver(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public void checkEmails(TicketSystem ticketSystem) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");

        try {
            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore("imaps");
            store.connect(host, username, password);

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            Message[] messages = emailFolder.getMessages();
            Arrays.sort(messages, Comparator.comparing(message -> {
                try {
                    return message.getReceivedDate();
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }));

            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    String subject = mimeMessage.getSubject();
                    System.out.println("Processing email with subject: " + subject); // Debug statement
                    Object content = mimeMessage.getContent();
                    String contentString = "";
                    List<String> attachments = new ArrayList<>();

                    if (content instanceof String) {
                        contentString = (String) content;
                    } else if (content instanceof Multipart) {
                        Multipart multipart = (Multipart) content;
                        for (int i = 0; i < multipart.getCount(); i++) {
                            BodyPart bodyPart = multipart.getBodyPart(i);
                            if (bodyPart.isMimeType("text/plain")) {
                                contentString += bodyPart.getContent().toString();
                            } else if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                                MimeBodyPart mimeBodyPart = (MimeBodyPart) bodyPart;
                                String fileName = mimeBodyPart.getFileName();
                                try {
                                    File attachmentsDir = new File("attachments");
                                    if (!attachmentsDir.exists()) {
                                        attachmentsDir.mkdir();
                                    }
                                    InputStream is = mimeBodyPart.getInputStream();
                                    File file = new File(attachmentsDir, fileName);
                                    FileOutputStream fos = new FileOutputStream(file);
                                    byte[] buf = new byte[4096];
                                    int bytesRead;
                                    while ((bytesRead = is.read(buf)) != -1) {
                                        fos.write(buf, 0, bytesRead);
                                    }
                                    fos.close();
                                    attachments.add(file.getAbsolutePath());
                                } catch (Exception e) {
                                    System.out.println("Failed to save attachment: " + fileName);
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    System.out.println("Creating ticket for email with subject: " + subject); // Debug statement
                    String ticketDescription = "Subject: " + subject;
                    ticketSystem.createTicket(ticketDescription, contentString, attachments);
                }
            }

            emailFolder.close(false);
            store.close();
        } catch (AuthenticationFailedException e) {
            System.out.println("Authentication failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}