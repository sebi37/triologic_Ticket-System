import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TicketServlet extends HttpServlet {
    private TicketSystem ticketSystem;

    public TicketServlet(TicketSystem ticketSystem) {
        this.ticketSystem = ticketSystem;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String subject = req.getParameter("subject");
        String content = req.getParameter("content");

        Map<String, String> formData = new HashMap<>();
        formData.put("subject", subject);
        formData.put("content", content);

        WebFormReceiver webFormReceiver = new WebFormReceiver(ticketSystem);
        webFormReceiver.receiveFormSubmission(formData);

        resp.getWriter().println("Ticket erfolgreich erstellt!");
    }
}