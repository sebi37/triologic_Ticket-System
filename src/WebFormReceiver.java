import java.util.Map;
import java.util.ArrayList;

public class WebFormReceiver {
    private TicketSystem ticketSystem;

    public WebFormReceiver(TicketSystem ticketSystem) {
        this.ticketSystem = ticketSystem;
    }

    public void receiveFormSubmission(Map<String, String> formData) {
        String subject = formData.get("subject");
        String content = formData.get("content");
        String senderEmail = formData.get("senderEmail");

        if (subject != null && !subject.isEmpty() && senderEmail != null && !senderEmail.isEmpty()) {
            String ticketDescription = "Subject: " + subject;
            ticketSystem.createTicket(ticketDescription, content, new ArrayList<>(), senderEmail);
        } else {
            System.out.println("Form submission is missing a subject or sender email.");
        }
    }
}