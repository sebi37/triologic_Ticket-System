import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TicketListCellRenderer extends JPanel implements ListCellRenderer<Ticket> {
    private JLabel statusLabel;
    private JLabel descriptionLabel;

    public TicketListCellRenderer() {
        setLayout(new BorderLayout(5, 5));
        statusLabel = new JLabel();
        descriptionLabel = new JLabel();
        add(statusLabel, BorderLayout.WEST);
        add(descriptionLabel, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Ticket> list, Ticket ticket, int index, boolean isSelected, boolean cellHasFocus) {
        descriptionLabel.setText(ticket.getDescription());

        if (ticket.isClosed()) {
            statusLabel.setIcon(createStatusIcon(Color.GREEN));
        } else if (ticket.isProcessing()) {
            statusLabel.setIcon(createStatusIcon(Color.ORANGE));
        } else {
            statusLabel.setIcon(createStatusIcon(Color.RED));
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }

    private Icon createStatusIcon(Color color) {
        int size = 10;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(color);
        g2.fillOval(0, 0, size, size);
        g2.dispose();
        return new ImageIcon(image);
    }
}