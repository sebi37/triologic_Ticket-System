import java.util.Map;

public class WebFormReceiver {
    private TicketSystem ticketSystem;

    public WebFormReceiver(TicketSystem ticketSystem) {
        this.ticketSystem = ticketSystem;
    }

    public void receiveFormSubmission(Map<String, String> formData) {
        String subject = formData.get("subject");
        String content = formData.get("content");

        if (subject != null && !subject.isEmpty()) {
            String ticketDescription = "Subject: " + subject + "\nContent: " + content;
            ticketSystem.createTicket(ticketDescription);
        } else {
            System.out.println("Form submission is missing a subject.");
        }
    }
}