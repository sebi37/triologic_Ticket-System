import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SupportTicketSystemGUI extends JFrame {
    private TicketSystem ticketSystem;
    private EmailReceiver emailReceiver;
    private JTextArea consoleOutput;
    private JList<Ticket> ticketList;
    private DefaultListModel<Ticket> ticketListModel;
    private JTextArea ticketDetails;
    private JPanel attachmentsPanel;
    private JPanel buttonPanel;
    private boolean isDarkMode = false;

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
                    displayAttachments(selectedTicket);
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

        // Initialize attachmentsPanel
        attachmentsPanel = new JPanel();
        attachmentsPanel.setLayout(new GridLayout(0, 1)); // One column layout

        // Create a JScrollPane for attachmentsPanel
        JScrollPane attachmentsScrollPane = new JScrollPane(attachmentsPanel);
        attachmentsScrollPane.setBorder(BorderFactory.createTitledBorder("Attachments"));
        attachmentsScrollPane.setPreferredSize(new Dimension(800, 200)); // Decrease the height to 150

        // Add the JScrollPane to the main frame
        add(attachmentsScrollPane, BorderLayout.SOUTH);

        // Adjust the ticket details scroll pane size
        detailsScrollPane.setPreferredSize(new Dimension(800, 200)); // Decrease the height to 200

        // Initialize buttonPanel
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        // Create a JScrollPane for buttonPanel
        JScrollPane buttonScrollPane = new JScrollPane(buttonPanel);
        buttonScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        buttonScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Add the JScrollPane to the main frame
        add(buttonScrollPane, BorderLayout.EAST);

        JButton createTicketButton = new JButton("Create Ticket");
        JButton showTicketsButton = new JButton("Show Tickets");
        JButton closeTicketButton = new JButton("Close Ticket");
        JButton saveTicketsButton = new JButton("Save Tickets");
        JButton loadTicketsButton = new JButton("Load Tickets");
        JButton checkEmailsButton = new JButton("Check Emails for Tickets");
        JButton simulateFormButton = new JButton("Simulate Form Submission");
        JButton deleteClosedTicketsButton = new JButton("Delete Closed Tickets");
        JButton exitButton = new JButton("Exit");
        JButton toggleDarkModeButton = new JButton("Toggle Dark Mode");

        createTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String description = JOptionPane.showInputDialog("Enter ticket description:");
                if (description != null) {
                    String senderEmail = JOptionPane.showInputDialog("Enter sender email:");
                    if (senderEmail != null) {
                        ticketSystem.createTicket(description, "", new ArrayList<>(), senderEmail);
                        updateTicketList();
                    }
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
                try {
                    emailReceiver.checkEmails(ticketSystem);
                } catch (MessagingException ex) {
                    throw new RuntimeException(ex);
                }
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

        toggleDarkModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isDarkMode = !isDarkMode;
                applyColorScheme();
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
        buttonPanel.add(toggleDarkModeButton);
        buttonPanel.add(Box.createVerticalStrut(5));
        buttonPanel.add(exitButton);

        // Redirect System.out to JTextArea
        PrintStream printStream = new PrintStream(new CustomOutputStream(consoleOutput));
        System.setOut(printStream);
        System.setErr(printStream);

        applyColorScheme();
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

    private void displayAttachments(Ticket ticket) {
        attachmentsPanel.removeAll(); // Alte Anhänge entfernen
        Set<String> displayedAttachments = new HashSet<>(); // Set zum Verfolgen der angezeigten Anhänge
        for (String filePath : ticket.getAttachments()) {
            if (!displayedAttachments.contains(filePath)) {
                new ImageLoaderWorker(filePath).execute();
                displayedAttachments.add(filePath);
            }
        }

        // Verwende SwingUtilities, um sicherzustellen, dass die Aktualisierung im EDT erfolgt
        SwingUtilities.invokeLater(() -> {
            attachmentsPanel.revalidate();  // Stellt sicher, dass das Layout neu berechnet wird
            attachmentsPanel.repaint();     // Erzwingt das Neuzeichnen des Panels
            validate();                     // Stellt sicher, dass das Layout des gesamten Fensters neu berechnet wird
        });
    }

    private class ImageLoaderWorker extends SwingWorker<ImageIcon, Void> {
        private String filePath;

        public ImageLoaderWorker(String filePath) {
            this.filePath = filePath;
        }

        @Override
        protected ImageIcon doInBackground() throws Exception {
            File file = new File(filePath);
            if (file.exists() && isImageFile(file)) {
                BufferedImage img = ImageIO.read(file);
                Image scaledImg = ImageUtils.getScaledImage(img, ImageUtils.MAX_WIDTH, ImageUtils.MAX_HEIGHT);
                return new ImageIcon(scaledImg);
            } else {
                return null;
            }
        }

        @Override
        protected void done() {
            try {
                ImageIcon icon = get();
                if (icon != null) {
                    JPanel attachmentPanel = new JPanel(new BorderLayout());
                    JLabel imgLabel = new JLabel(icon);
                    attachmentPanel.add(imgLabel, BorderLayout.CENTER);

                    JButton openButton = new JButton("Open");
                    openButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            showImageInNewFrame(icon.getImage());
                        }
                    });
                    attachmentPanel.add(openButton, BorderLayout.SOUTH);

                    // Füge die Komponenten im EDT hinzu
                    SwingUtilities.invokeLater(() -> {
                        attachmentsPanel.add(attachmentPanel);
                        attachmentsPanel.revalidate();
                        attachmentsPanel.repaint();
                    });
                } else {
                    JLabel fileLabel = new JLabel(filePath);
                    SwingUtilities.invokeLater(() -> {
                        attachmentsPanel.add(fileLabel);
                        attachmentsPanel.revalidate();
                        attachmentsPanel.repaint();
                    });
                }
            } catch (Exception e) {
                JLabel errorLabel = new JLabel("Error loading attachment: " + filePath);
                SwingUtilities.invokeLater(() -> {
                    attachmentsPanel.add(errorLabel);
                    attachmentsPanel.revalidate();
                    attachmentsPanel.repaint();
                });
                e.printStackTrace();
            }
        }
    }

    private void showImageInNewFrame(Image img) {
        JFrame frame = new JFrame("Image Viewer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        JLabel imgLabel = new JLabel(new ImageIcon(img));
        frame.add(new JScrollPane(imgLabel), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        frame.add(closeButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private boolean isImageFile(File file) {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
        for (String ext : imageExtensions) {
            if (file.getName().toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    private void applyColorScheme() {
        Color background = isDarkMode ? ColorScheme.DARK_BACKGROUND : ColorScheme.LIGHT_BACKGROUND;
        Color foreground = isDarkMode ? ColorScheme.DARK_FOREGROUND : ColorScheme.LIGHT_FOREGROUND;

        getContentPane().setBackground(background);
        consoleOutput.setBackground(background);
        consoleOutput.setForeground(foreground);
        ticketList.setBackground(background);
        ticketList.setForeground(foreground);
        ticketDetails.setBackground(background);
        ticketDetails.setForeground(foreground);
        attachmentsPanel.setBackground(background);

        // Apply to all buttons
        for (Component component : buttonPanel.getComponents()) {
            if (component instanceof JButton) {
                component.setBackground(background);
                component.setForeground(foreground);
            }
        }

        // Repaint the frame to apply changes
        repaint();
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