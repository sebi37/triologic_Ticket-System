import java.io.*;
import java.util.*;

public class FormSubmissionSimulator {
    public static void main(String[] args) {
        TicketSystem ticketSystem = new TicketSystem();
        WebFormReceiver webFormReceiver = new WebFormReceiver(ticketSystem);

        // Simulierte Formulardaten
        Map<String, String> formData = new HashMap<>();
        formData.put("subject", "Test Ticket");
        formData.put("content", "Dies ist ein Testinhalt für das Ticket.");

        // Formularübermittlung simulieren
        webFormReceiver.receiveFormSubmission(formData);

        // Tickets anzeigen
        ticketSystem.showTickets();
    }
}
