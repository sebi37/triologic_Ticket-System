import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TicketSystem {
    private List<Ticket> tickets;

    public TicketSystem() {
        tickets = new ArrayList<>();
    }

    public void createTicket(String description) {
        Ticket ticket = new Ticket(description, new ArrayList<>());
        tickets.add(ticket);
        System.out.println("Ticket created: " + ticket);
    }

    public void createTicket(String description, List<String> attachments) {
        Ticket ticket = new Ticket(description, attachments);
        tickets.add(ticket);
        System.out.println("Ticket created: " + ticket);
    }

    public void showTickets() {
        if (tickets.isEmpty()) {
            System.out.println("No tickets available.");
        } else {
            tickets.forEach(System.out::println);
        }
    }

    public void closeTicket(int id) {
        boolean found = false;
        for (Ticket ticket : tickets) {
            if (ticket.getId() == id) {
                ticket.closeTicket();
                System.out.println("Ticket closed: " + ticket);
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Ticket ID not found.");
        }
    }

    public void saveTickets(String fileName) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(tickets);
            System.out.println("Tickets saved to " + fileName);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadTickets(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            tickets = (List<Ticket>) ois.readObject();
            Ticket.setIdCounter(tickets.stream().mapToInt(Ticket::getId).max().orElse(0) + 1);
            System.out.println("Tickets loaded from " + fileName);
        }
    }

    public void deleteClosedTickets() {
        Iterator<Ticket> iterator = tickets.iterator();
        while (iterator.hasNext()) {
            Ticket ticket = iterator.next();
            if (ticket.isClosed()) {
                iterator.remove();
                System.out.println("Ticket deleted: " + ticket);
            }
        }
    }

    public List<Ticket> getTickets() {
        return tickets;
    }
}