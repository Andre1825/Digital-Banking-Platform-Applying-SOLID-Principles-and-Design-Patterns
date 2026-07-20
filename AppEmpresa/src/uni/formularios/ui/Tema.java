package uni.formularios.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Sistema de diseno (design tokens + componentes) de la Banca Digital.
 * Centraliza colores, tipografias y widgets con estetica "flat" moderna para
 * que toda la interfaz luzca consistente y agradable, usando solo Swing puro.
 */
public final class Tema {

    private Tema() { }

    // ===================== Paleta =====================
    public static final Color BG          = new Color(0xEEF2F7);
    public static final Color SIDEBAR     = new Color(0x0E2138);
    public static final Color SIDEBAR_2   = new Color(0x0A1929);
    public static final Color SIDEBAR_SEL = new Color(0x1976D2);
    public static final Color SIDEBAR_TXT = new Color(0xB7C4D6);
    public static final Color CARD        = Color.WHITE;
    public static final Color BORDE       = new Color(0xE1E7EF);

    public static final Color PRIMARY     = new Color(0x1565C0);
    public static final Color ACCENT      = new Color(0x0FB58B);
    public static final Color TXT         = new Color(0x1F2A37);
    public static final Color TXT_MUTED   = new Color(0x6B7280);
    public static final Color OK          = new Color(0x18A957);
    public static final Color WARN        = new Color(0xE39A0C);
    public static final Color DANGER      = new Color(0xE5484D);
    public static final Color CONSOLE_BG  = new Color(0x0B1B2B);

    // ===================== Tipografia =====================
    private static final String FAM = "Segoe UI";
    public static final Font H1     = new Font(FAM, Font.BOLD, 22);
    public static final Font H2     = new Font(FAM, Font.BOLD, 16);
    public static final Font H3     = new Font(FAM, Font.BOLD, 14);
    public static final Font BODY   = new Font(FAM, Font.PLAIN, 13);
    public static final Font BODY_B = new Font(FAM, Font.BOLD, 13);
    public static final Font SMALL  = new Font(FAM, Font.PLAIN, 11);
    public static final Font BTN    = new Font(FAM, Font.BOLD, 13);
    public static final Font KPI    = new Font(FAM, Font.BOLD, 26);
    public static final Font MONO   = new Font("Consolas", Font.PLAIN, 12);

    // ===================== Utilidades de color =====================
    public static Color aclarar(Color c, double f) {
        int r = (int) Math.min(255, c.getRed()   + 255 * f);
        int g = (int) Math.min(255, c.getGreen() + 255 * f);
        int b = (int) Math.min(255, c.getBlue()  + 255 * f);
        return new Color(r, g, b);
    }
    public static Color oscurecer(Color c, double f) {
        return new Color((int) (c.getRed() * (1 - f)), (int) (c.getGreen() * (1 - f)), (int) (c.getBlue() * (1 - f)));
    }

    // ===================== Etiquetas =====================
    public static JLabel label(String t, Font f, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(f); l.setForeground(c);
        return l;
    }
    public static JLabel titulo(String t)  { return label(t, H1, TXT); }
    public static JLabel h2(String t)       { return label(t, H2, TXT); }
    public static JLabel h3(String t)       { return label(t, H3, TXT); }
    public static JLabel body(String t)     { return label(t, BODY, TXT); }
    public static JLabel muted(String t)    { return label(t, BODY, TXT_MUTED); }

    // ===================== Botones =====================
    public static BotonRedondo botonPrimario(String t)   { return new BotonRedondo(t, PRIMARY, Color.WHITE); }
    public static BotonRedondo botonAcento(String t)     { return new BotonRedondo(t, ACCENT, Color.WHITE); }
    public static BotonRedondo botonPeligro(String t)    { return new BotonRedondo(t, DANGER, Color.WHITE); }
    public static BotonRedondo botonNeutro(String t) {
        BotonRedondo b = new BotonRedondo(t, new Color(0xE7EDF5), TXT);
        return b;
    }

    // ===================== Tarjetas =====================
    public static RoundedPanel tarjeta() {
        RoundedPanel p = new RoundedPanel(18, CARD, BORDE);
        p.setBorder(new EmptyBorder(18, 20, 18, 20));
        return p;
    }

    /** Tarjeta con titulo y subtitulo, lista para recibir contenido en el centro. */
    public static RoundedPanel tarjetaTitulada(String titulo, String subt) {
        RoundedPanel p = tarjeta();
        p.setLayout(new BorderLayout(0, 12));
        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel t = h2(titulo); t.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(t);
        if (subt != null) {
            JLabel s = muted(subt); s.setAlignmentX(Component.LEFT_ALIGNMENT);
            head.add(Box.createVerticalStrut(2));
            head.add(s);
        }
        p.add(head, BorderLayout.NORTH);
        return p;
    }

    /** Tarjeta KPI (valor grande + etiqueta + barra de acento). */
    public static RoundedPanel kpi(String valor, String etiqueta, Color acento) {
        RoundedPanel p = new RoundedPanel(16, CARD, BORDE);
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(16, 18, 16, 18));

        RoundedPanel barra = new RoundedPanel(6, acento, null);
        barra.setPreferredSize(new Dimension(6, 10));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        txt.setBorder(new EmptyBorder(0, 12, 0, 0));
        JLabel v = label(valor, KPI, TXT); v.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel e = label(etiqueta, SMALL, TXT_MUTED); e.setAlignmentX(Component.LEFT_ALIGNMENT);
        txt.add(v); txt.add(Box.createVerticalStrut(2)); txt.add(e);

        p.add(barra, BorderLayout.WEST);
        p.add(txt, BorderLayout.CENTER);
        return p;
    }

    /** Etiqueta tipo "chip" para marcar los RF/CP que cubre un modulo. */
    public static JComponent chip(String texto, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font(FAM, Font.BOLD, 11));
        l.setForeground(color);
        l.setOpaque(false);
        l.setBorder(new EmptyBorder(4, 10, 4, 10));
        RoundedPanel wrap = new RoundedPanel(10, aclarar(color, 0.82), aclarar(color, 0.55));
        wrap.setLayout(new BorderLayout());
        wrap.add(l);
        return wrap;
    }

    // ===================== Campos =====================
    public static JTextField campo(int cols) {
        JTextField t = new JTextField(cols);
        estilizarCampo(t);
        return t;
    }
    public static void estilizarCampo(JComponent c) {
        c.setFont(BODY);
        c.setBackground(Color.WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
    }

    /** Area de texto de solo lectura para resultados (fondo suave). */
    public static JTextArea areaResultado(int filas) {
        JTextArea a = new JTextArea(filas, 10);
        a.setEditable(false);
        a.setFont(MONO);
        a.setForeground(TXT);
        a.setBackground(new Color(0xF6F8FB));
        a.setBorder(new EmptyBorder(10, 12, 10, 12));
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        return a;
    }

    public static JScrollPane scroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(BORDE, 1, true));
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    /** Fila horizontal de componentes con separacion uniforme. */
    public static JPanel fila(int gap, JComponent... comps) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, gap, 0));
        p.setOpaque(false);
        for (JComponent c : comps) p.add(c);
        return p;
    }

    /** Grilla de KPIs/tarjetas de igual ancho. */
    public static JPanel grilla(int cols, int gap, JComponent... comps) {
        JPanel p = new JPanel(new GridLayout(0, cols, gap, gap));
        p.setOpaque(false);
        for (JComponent c : comps) p.add(c);
        return p;
    }

    /** Carga un PNG desde /imagen/ y lo escala; retorna null si no existe. */
    public static ImageIcon cargarIcono(String nombre, int w, int h) {
        try {
            java.net.URL url = Tema.class.getResource("/imagen/" + nombre);
            if (url == null) return null;
            Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    // ===================== Componentes personalizados =====================

    /** Panel con esquinas redondeadas y borde opcional (antialiasing). */
    public static class RoundedPanel extends JPanel {
        private final int arco;
        private final Color fondo;
        private final Color borde;
        public RoundedPanel(int arco, Color fondo, Color borde) {
            this.arco = arco; this.fondo = fondo; this.borde = borde;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arco, arco);
            if (borde != null) {
                g2.setColor(borde);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arco, arco);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Boton "flat" con esquinas redondeadas y efecto hover. */
    public static class BotonRedondo extends JButton {
        private final Color base;
        private final Color hover;
        private final int arco = 12;
        public BotonRedondo(String texto, Color base, Color colorTexto) {
            super(texto);
            this.base = base;
            this.hover = aclarar(base, 0.08);
            setForeground(colorTexto);
            setFont(BTN);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 18, 10, 18));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = getModel().isPressed() ? oscurecer(base, 0.10)
                    : getModel().isRollover() ? hover : base;
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arco, arco);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Panel con degradado horizontal (usado en la cabecera). */
    public static class PanelDegradado extends JPanel {
        private final Color a, b;
        public PanelDegradado(Color a, Color b) { this.a = a; this.b = b; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, a, getWidth(), 0, b));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
