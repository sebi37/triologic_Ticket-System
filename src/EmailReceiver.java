import java.util.Properties;
import javax.mail.*;
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

    public void checkEmails(TicketSystem ticketSystem) {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");

        try {
            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore("imaps");
            store.connect(host, username, password);

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            Message[] messages = emailFolder.getMessages();
            for (Message message : messages) {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    String subject = mimeMessage.getSubject();
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
                                contentString = bodyPart.getContent().toString();
                            } else if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                                attachments.add(bodyPart.getFileName());
                            }
                        }
                    }

                    if (subject != null && subject.contains("Ticket")) {
                        String ticketDescription = "Subject: " + subject + "\nContent: " + contentString;
                        ticketSystem.createTicket(ticketDescription, attachments);
                    }
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