import java.io.Serializable;
import java.util.List;

public class Ticket implements Serializable {
    private static int idCounter = 1;
    private int id;
    private String description;
    private boolean closed;
    private List<String> attachments;

    public Ticket(String description, List<String> attachments) {
        this.id = idCounter++;
        this.description = description;
        this.closed = false;
        this.attachments = attachments;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isClosed() {
        return closed;
    }

    public void closeTicket() {
        this.closed = true;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public static void setIdCounter(int idCounter) {
        Ticket.idCounter = idCounter;
    }

    @Override
    public String toString() {
        return "Ticket ID: " + id + ", Description: " + description + ", Closed: " + closed;
    }
}