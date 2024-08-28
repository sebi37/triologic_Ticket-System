import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SupportTicketSystemGUI extends JFrame {
    private TicketSystem ticketSystem;
    private EmailReceiver emailReceiver;
    private JTextArea consoleOutput;
    private JList<Ticket> ticketList;
    private DefaultListModel<Ticket> ticketListModel;
    private JTextArea ticketDetails;

    public SupportTicketSystemGUI() {
        ticketSystem = new TicketSystem();
        emailReceiver = new EmailReceiver("mail.triologic.at", "s.buresch@triologic.at", "Camelblue93!");

        setTitle("Support Ticket System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        consoleOutput = new JTextArea();
        consoleOutput.setEditable(false);
        JScrollPane consoleScrollPane = new JScrollPane(consoleOutput);
        consoleScrollPane.setBorder(BorderFactory.createTitledBorder("Console Output"));
        add(consoleScrollPane, BorderLayout.SOUTH);

        ticketListModel = new DefaultListModel<>();
        ticketList = new JList<>(ticketListModel);
        ticketList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ticketList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Ticket selectedTicket = ticketList.getSelectedValue();
                if (selectedTicket != null) {
                    StringBuilder details = new StringBuilder(selectedTicket.getDescription());
                    details.append("\n\nContent:\n").append(selectedTicket.getContent());
                    details.append("\n\nAttachments:\n");
                    for (String attachment : selectedTicket.getAttachments()) {
                        details.append(attachment).append("\n");
                    }
                    ticketDetails.setText(details.toString());
                }
            }
        });
        JScrollPane listScrollPane = new JScrollPane(ticketList);
        listScrollPane.setPreferredSize(new Dimension(200, 0));
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Tickets"));
        add(listScrollPane, BorderLayout.WEST);

        ticketDetails = new JTextArea();
        ticketDetails.setEditable(false);
        JScrollPane detailsScrollPane = new JScrollPane(ticketDetails);
        detailsScrollPane.setBorder(BorderFactory.createTitledBorder("Ticket Details"));
        add(detailsScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton createTicketButton = new JButton("Create Ticket");
        JButton showTicketsButton = new JButton("Show Tickets");
        JButton closeTicketButton = new JButton("Close Ticket");
        JButton saveTicketsButton = new JButton("Save Tickets");
        JButton loadTicketsButton = new JButton("Load Tickets");
        JButton checkEmailsButton = new JButton("Check Emails for Tickets");
        JButton simulateFormButton = new JButton("Simulate Form Submission");
        JButton deleteClosedTicketsButton = new JButton("Delete Closed Tickets");
        JButton exitButton = new JButton("Exit");

        createTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String description = JOptionPane.showInputDialog("Enter ticket description:");
                if (description != null) {
                    ticketSystem.createTicket(description, "", new ArrayList<>());
                    updateTicketList();
                }
            }
        });

        showTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ticketSystem.showTickets();
            }
        });

        closeTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idStr = JOptionPane.showInputDialog("Enter ticket ID to close:");
                if (idStr != null) {
                    int id = Integer.parseInt(idStr);
                    ticketSystem.closeTicket(id);
                    updateTicketList();
                }
            }
        });

        saveTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ticketSystem.saveTickets("tickets.dat");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error saving tickets: " + ex.getMessage());
                }
            }
        });

        loadTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ticketSystem.loadTickets("tickets.dat");
                    updateTicketList();
                } catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Error loading tickets: " + ex.getMessage());
                }
            }
        });

        checkEmailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emailReceiver.checkEmails(ticketSystem);
                updateTicketList();
            }
        });

        simulateFormButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulateFormSubmission(ticketSystem);
                updateTicketList();
            }
        });

        deleteClosedTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ticketSystem.deleteClosedTickets();
                updateTicketList();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        buttonPanel.add(createTicketButton);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(showTicketsButton);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(closeTicketButton);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(saveTicketsButton);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(loadTicketsButton);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(checkEmailsButton);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(simulateFormButton);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(deleteClosedTicketsButton);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.EAST);

        // Redirect System.out to JTextArea
        PrintStream printStream = new PrintStream(new CustomOutputStream(consoleOutput));
        System.setOut(printStream);
        System.setErr(printStream);
    }

    private void simulateFormSubmission(TicketSystem ticketSystem) {
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

    private void updateTicketList() {
        ticketListModel.clear();
        for (Ticket ticket : ticketSystem.getTickets()) {
            ticketListModel.addElement(ticket);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SupportTicketSystemGUI().setVisible(true);
            }
        });
    }

    private class CustomOutputStream extends OutputStream {
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            textArea.append(String.valueOf((char) b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }
}