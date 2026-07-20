package uni.formularios;

import uni.controller.SeguridadControlador;
import uni.database.ConexionBD;
import uni.entity.Empleado;
import uni.formularios.ui.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Formulario de inicio de sesion del Sistema Bancario, rediseniado con el
 * sistema de diseno {@link Tema}. Flujo: Vista -> SeguridadControlador ->
 * EmpleadoDAO -> ConexionBD -> MySQL. Tras autenticar abre el MenuPrincipal
 * segun el rol del empleado.
 */
public class LoginBanco extends JFrame {

    private final JTextField txtUsuario = Tema.campo(18);
    private final JPasswordField txtClave = new JPasswordField(18);
    private final JLabel lblEstado = Tema.muted(" ");
    private final SeguridadControlador seguridad = new SeguridadControlador();

    public LoginBanco() {
        setTitle("Banco Andino del Sur · Ingreso");
        setSize(880, 560);
        setMinimumSize(new Dimension(760, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon appIcon = Tema.cargarIcono("account_balance_new.png", 32, 32);
        if (appIcon != null) setIconImage(appIcon.getImage());

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Tema.BG);
        raiz.add(panelMarca(), BorderLayout.WEST);
        raiz.add(panelFormulario(), BorderLayout.CENTER);
        setContentPane(raiz);

        Tema.estilizarCampo(txtClave);
        getRootPane().setDefaultButton(botonIngresar);
        verificarConexion();
    }

    // ===================== Panel izquierdo (marca) =====================
    private JComponent panelMarca() {
        Tema.PanelDegradado marca = new Tema.PanelDegradado(new Color(0x0E2138), Tema.PRIMARY);
        marca.setPreferredSize(new Dimension(360, 10));
        marca.setLayout(new BoxLayout(marca, BoxLayout.Y_AXIS));
        marca.setBorder(new EmptyBorder(48, 40, 40, 40));

        ImageIcon iconoLogo = Tema.cargarIcono("account_balance_new.png", 44, 44);
        JLabel logo = iconoLogo != null
                ? new JLabel("  BANCO ANDINO DEL SUR", iconoLogo, JLabel.LEFT)
                : new JLabel("◆  BANCO ANDINO DEL SUR");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("<html>Sistema<br>Bancario</html>");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        titulo.setBorder(new EmptyBorder(28, 0, 8, 0));

        JLabel sub = new JLabel("<html>Plataforma de gestion bancaria<br>y Banca Digital.</html>");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(0xCFE0F5));
        sub.setAlignmentX(LEFT_ALIGNMENT);

        marca.add(logo);
        marca.add(titulo);
        marca.add(sub);
        marca.add(Box.createVerticalStrut(34));
        marca.add(vinieta("Mantenimientos y operaciones sobre MySQL"));
        marca.add(Box.createVerticalStrut(10));
        marca.add(vinieta("Acceso por rol (Administrador / Cajero)"));
        marca.add(Box.createVerticalStrut(10));
        marca.add(vinieta("Portal de Banca Digital del cliente"));
        marca.add(Box.createVerticalGlue());

        JLabel pie = new JLabel("© 2026 · Proyecto de Patrones de Diseno");
        pie.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pie.setForeground(new Color(0x9FB3CC));
        pie.setAlignmentX(LEFT_ALIGNMENT);
        marca.add(pie);
        return marca;
    }

    private JComponent vinieta(String texto) {
        JPanel fila = new JPanel();
        fila.setOpaque(false);
        fila.setLayout(new BoxLayout(fila, BoxLayout.X_AXIS));
        fila.setAlignmentX(LEFT_ALIGNMENT);

        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0x4FC3A0));
                g2.fillOval(0, (getHeight() - 8) / 2, 8, 8);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(8, 18); }
            @Override public Dimension getMaximumSize()   { return new Dimension(8, 18); }
            @Override public Dimension getMinimumSize()   { return new Dimension(8, 18); }
        };
        dot.setOpaque(false);

        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(new Color(0xE3ECF7));

        fila.add(dot);
        fila.add(Box.createHorizontalStrut(10));
        fila.add(l);
        fila.add(Box.createHorizontalGlue());
        return fila;
    }

    // ===================== Panel derecho (formulario) =====================
    private final Tema.BotonRedondo botonIngresar = Tema.botonPrimario("Ingresar");

    private JComponent panelFormulario() {
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(Tema.BG);

        Tema.RoundedPanel card = new Tema.RoundedPanel(20, Tema.CARD, Tema.BORDE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(36, 40, 36, 40));
        card.setPreferredSize(new Dimension(400, 430));

        JLabel h1 = Tema.titulo("Iniciar sesion");
        h1.setAlignmentX(LEFT_ALIGNMENT);
        JLabel h1s = Tema.muted("Ingrese sus credenciales de empleado");
        h1s.setAlignmentX(LEFT_ALIGNMENT);

        card.add(h1);
        card.add(Box.createVerticalStrut(4));
        card.add(h1s);
        card.add(Box.createVerticalStrut(26));
        card.add(campoEtiquetado("Usuario", txtUsuario));
        card.add(Box.createVerticalStrut(16));
        card.add(campoEtiquetado("Clave", txtClave));
        card.add(Box.createVerticalStrut(24));

        botonIngresar.setAlignmentX(LEFT_ALIGNMENT);
        botonIngresar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        botonIngresar.addActionListener(e -> autenticar());
        txtClave.addActionListener(e -> autenticar());
        card.add(botonIngresar);

        card.add(Box.createVerticalStrut(14));
        lblEstado.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblEstado);

        card.add(Box.createVerticalGlue());
        JLabel hint = Tema.muted("Credenciales por defecto:  admin / admin");
        hint.setFont(Tema.SMALL);
        hint.setAlignmentX(LEFT_ALIGNMENT);
        card.add(hint);

        fondo.add(card);
        return fondo;
    }

    private JComponent campoEtiquetado(String etiqueta, JComponent campo) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        JLabel l = Tema.h3(etiqueta);
        l.setAlignmentX(LEFT_ALIGNMENT);
        campo.setAlignmentX(LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.add(l);
        p.add(Box.createVerticalStrut(6));
        p.add(campo);
        return p;
    }

    // ===================== Logica =====================
    private void verificarConexion() {
        if (!ConexionBD.getInstancia().probarConexion()) {
            lblEstado.setForeground(Tema.DANGER);
            lblEstado.setText("Sin conexion a MySQL. Inicie la BD con Docker.");
        } else {
            lblEstado.setForeground(Tema.OK);
            lblEstado.setText("Conectado a la base de datos.");
        }
    }

    private void autenticar() {
        try {
            Empleado emp = seguridad.iniciarSesion(
                    txtUsuario.getText(), new String(txtClave.getPassword()));
            dispose();
            SwingUtilities.invokeLater(() -> new MenuPrincipal(emp).setVisible(true));
        } catch (SecurityException | IllegalArgumentException ex) {
            lblEstado.setForeground(Tema.DANGER);
            lblEstado.setText(ex.getMessage());
        } catch (Exception ex) {
            lblEstado.setForeground(Tema.DANGER);
            lblEstado.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginBanco().setVisible(true));
    }
}
