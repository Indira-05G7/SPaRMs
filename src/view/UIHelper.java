package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;

public class UIHelper {
    public static final Color NAVY = new Color(0x1E, 0x3A, 0x5F);
    public static final Color BLUE = new Color(0x25, 0x63, 0xEB);
    public static final Color BG_SOFT = new Color(0xF8, 0xFA, 0xFC);
    public static final Color WHITE = Color.WHITE;
    public static final Color SUCCESS = new Color(0x16, 0xA3, 0x4A);
    public static final Color WARNING = new Color(0xF5, 0x9E, 0x0B);
    public static final Color ERROR = new Color(0xDC, 0x26, 0x26);
    public static final Color TEXT_DARK = new Color(0x1F, 0x29, 0x37);
    public static final Color BORDER = new Color(0xD1, 0xD5, 0xDB);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_KPI = new Font("Segoe UI", Font.BOLD, 28);

    public static JLabel createHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(NAVY);
        label.setBorder(new EmptyBorder(10, 0, 15, 0));
        return label;
    }

    public static JLabel createSubHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBTITLE);
        label.setForeground(TEXT_DARK);
        label.setBorder(new EmptyBorder(10, 0, 8, 0));
        return label;
    }

    public static JButton createButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, BLUE, WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        return createButton(text, WHITE, TEXT_DARK);
    }

    public static JPanel createKPICard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                // Draw accent border on the left
                g2.setColor(accentColor);
                g2.fillRect(0, 0, 6, getHeight());
                
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(180, 85));
        card.setBorder(new EmptyBorder(12, 18, 12, 12));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_SMALL);
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(FONT_KPI);
        lblValue.setForeground(TEXT_DARK);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    public static JScrollPane createTable(TableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_BODY);
        table.setRowHeight(35);
        table.setGridColor(BORDER);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(0xE0, 0xF2, 0xFE)); // Light blue highlight
        table.setSelectionForeground(TEXT_DARK);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(NAVY);
        header.setForeground(WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        
        // Alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.LEFT);
        centerRenderer.setBorder(new EmptyBorder(0, 10, 0, 10));
        table.setDefaultRenderer(Object.class, centerRenderer);
        table.setDefaultRenderer(Number.class, centerRenderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(WHITE);
        scroll.setBorder(new LineBorder(BORDER));
        return scroll;
    }

    public static JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setFont(FONT_BODY);
        tf.setPreferredSize(new Dimension(200, 35));
        return tf;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_BODY);
        pf.setPreferredSize(new Dimension(200, 35));
        return pf;
    }
}
