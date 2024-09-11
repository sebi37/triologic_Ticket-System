import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TicketSystem implements Serializable {
    private List<Ticket> tickets;

    public TicketSystem() {
        tickets = new ArrayList<>();
    }

    public void createTicket(String description, String content, List<String> attachments, String senderEmail) {
        Ticket ticket = new Ticket(description, content, attachments, senderEmail);
        tickets.add(ticket);
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void closeTicket(int id) {
        for (Ticket ticket : tickets) {
            if (ticket.getId() == id) {
                ticket.closeTicket();
                break;
            }
        }
    }

    public void deleteClosedTickets() {
        tickets.removeIf(Ticket::isClosed);
    }

    public void showTickets() {
        for (Ticket ticket : tickets) {
            System.out.println(ticket);
        }
    }

    public void saveTickets(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(tickets);
        }
    }

    public void loadTickets(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            tickets = (List<Ticket>) ois.readObject();
        }
    }
}