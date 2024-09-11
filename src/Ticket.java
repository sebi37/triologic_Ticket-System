import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Ticket implements Serializable {
    private static int idCounter = 1;
    private int id;
    private String description;
    private String content;
    private boolean closed;
    private boolean processing;
    private String senderEmail;
    private List<String> attachments;

    public Ticket(String description, String content, List<String> attachments, String senderEmail) {
        this.id = idCounter++;
        this.description = description;
        this.content = content;
        this.closed = false;
        this.processing = false;
        this.senderEmail = senderEmail;
        this.attachments = attachments != null ? attachments : new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isClosed() {
        return closed;
    }

    public void closeTicket() {
        this.closed = true;
    }

    public boolean isProcessing() {
        return processing;
    }

    public void startProcessing() {
        this.processing = true;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void addAttachment(String filePath) {
        attachments.add(filePath);
    }

    public static void setIdCounter(int idCounter) {
        Ticket.idCounter = idCounter;
    }

    @Override
    public String toString() {
        return "Ticket ID: " + id + ", Description: " + description + ", Status: " + (closed ? "Closed" : (processing ? "In Bearbeitung" : "Open"));
    }
}