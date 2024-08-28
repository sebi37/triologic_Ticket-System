import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;

public class SupportTicketSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TicketSystem ticketSystem = new TicketSystem();
        EmailReceiver emailReceiver = new EmailReceiver("mail.triologic.at", "s.buresch@triologic.at", "Camelblue93!");

        while (true) {
            System.out.println("\n1. Create Ticket");
            System.out.println("2. Show Tickets");
            System.out.println("3. Close Ticket");
            System.out.println("4. Save Tickets");
            System.out.println("5. Load Tickets");
            System.out.println("6. Check Emails for Tickets");
            System.out.println("7. Simulate Form Submission");
            System.out.println("8. Delete Closed Tickets");
            System.out.println("9. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter ticket description: ");
                    String description = scanner.nextLine();
                    ticketSystem.createTicket(description, "", new ArrayList<>());
                    break;
                case 2:
                    ticketSystem.showTickets();
                    break;
                case 3:
                    System.out.print("Enter ticket ID to close: ");
                    int id = scanner.nextInt();
                    ticketSystem.closeTicket(id);
                    break;
                case 4:
                    try {
                        ticketSystem.saveTickets("tickets.dat");
                    } catch (IOException e) {
                        System.out.println("Error saving tickets: " + e.getMessage());
                    }
                    break;
                case 5:
                    try {
                        ticketSystem.loadTickets("tickets.dat");
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Error loading tickets: " + e.getMessage());
                    }
                    break;
                case 6:
                    emailReceiver.checkEmails(ticketSystem);
                    break;
                case 7:
                    simulateFormSubmission(ticketSystem);
                    break;
                case 8:
                    ticketSystem.deleteClosedTickets();
                    break;
                case 9:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please choose again.");
            }
        }
    }

    private static void simulateFormSubmission(TicketSystem ticketSystem) {
        WebFormReceiver webFormReceiver = new WebFormReceiver(ticketSystem);

        // Simulated form data
        Map<String, String> formData = new HashMap<>();
        formData.put("subject", "Test Ticket");
        formData.put("content", "Dies ist ein Testinhalt für das Ticket.");

        // Simulate form submission
        webFormReceiver.receiveFormSubmission(formData);

        // Show tickets
        ticketSystem.showTickets();
    }
}