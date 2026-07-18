package uni.formularios;

import uni.controller.DashboardControlador;
import uni.entity.Empleado;
import uni.formularios.ui.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Ventana principal del Sistema Bancario. Panel de indicadores (Dashboard) y
 * menu de acceso habilitado/deshabilitado segun el ROL del empleado logueado.
 *  - ADMIN  : todos los mantenimientos + operaciones + banca digital.
 *  - CAJERO : clientes, cuentas, operaciones y estado de cuenta.
 */
public class MenuPrincipal extends JFrame {

    private final Empleado sesion;
    private final DashboardControlador dashboard = new DashboardControlador();

    private JLabel lblClientes, lblCuentas, lblDepositos;

    public MenuPrincipal(Empleado sesion) {
        this.sesion = sesion;
        setTitle("Banco Andino del Sur · Sistema Bancario");
        setSize(860, 560);
        setMinimumSize(new Dimension(780, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(crearMenu());
        setContentPane(crearContenido());
        refrescarIndicadores();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowActivated(java.awt.event.WindowEvent e) {
                refrescarIndicadores();
            }
        });
    }

    // ============================ MENU ============================
    private JMenuBar crearMenu() {
        boolean admin = sesion.esAdmin();
        JMenuBar barra = new JMenuBar();
        barra.setBackground(Tema.SIDEBAR);
        barra.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        JMenu mant = menu("Mantenimientos");
        mant.add(item("Sucursales",     admin, () -> new SucursalForm().setVisible(true)));
        mant.add(item("Empleados",      admin, () -> new EmpleadoForm().setVisible(true)));
        mant.add(item("Tipos de Cuenta",admin, () -> new TipoCuentaForm().setVisible(true)));
        mant.addSeparator();
        mant.add(item("Clientes", true, () -> new ClienteForm().setVisible(true)));
        mant.add(item("Cuentas",  true, () -> new CuentaForm().setVisible(true)));
        barra.add(mant);

        JMenu oper = menu("Operaciones");
        oper.add(item("Deposito",        true, () -> new DepositoForm(sesion).setVisible(true)));
        oper.add(item("Retiro",          true, () -> new RetiroForm(sesion).setVisible(true)));
        oper.add(item("Transferencia",   true, () -> new TransferenciaForm(sesion).setVisible(true)));
        oper.addSeparator();
        oper.add(item("Estado de Cuenta",true, () -> new EstadoCuentaForm().setVisible(true)));
        barra.add(oper);

        JMenu sesionMenu = menu("Sesion");
        sesionMenu.add(item("Actualizar indicadores", true, this::refrescarIndicadores));
        sesionMenu.add(item("Cerrar sesion",          true, this::cerrarSesion));
        sesionMenu.add(item("Salir",                  true, () -> System.exit(0)));
        barra.add(sesionMenu);

        return barra;
    }

    private JMenu menu(String texto) {
        JMenu m = new JMenu(texto);
        m.setForeground(Color.WHITE);
        m.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return m;
    }

    private JMenuItem item(String texto, boolean habilitado, Runnable accion) {
        JMenuItem it = new JMenuItem(texto);
        it.setEnabled(habilitado);
        it.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        it.addActionListener(e -> {
            try { accion.run(); }
            catch (Exception ex) { error(ex.getMessage()); }
        });
        return it;
    }

    // ========================= CONTENIDO =========================
    private JComponent crearContenido() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Tema.BG);

        raiz.add(cabecera(), BorderLayout.NORTH);
        raiz.add(cuerpo(),   BorderLayout.CENTER);
        raiz.add(pie(),      BorderLayout.SOUTH);
        return raiz;
    }

    private JComponent cabecera() {
        Tema.PanelDegradado head = new Tema.PanelDegradado(new Color(0x0E2138), Tema.PRIMARY);
        head.setLayout(new BorderLayout());
        head.setBorder(new EmptyBorder(18, 28, 18, 28));
        head.setPreferredSize(new Dimension(0, 96));

        JPanel izq = new JPanel();
        izq.setOpaque(false);
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Sistema Bancario");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Banco Andino del Sur · " + sesion.getNombreCompleto().trim()
                + "  |  " + sesion.getRol());
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(0xCFE0F5));
        sub.setAlignmentX(LEFT_ALIGNMENT);

        izq.add(titulo);
        izq.add(Box.createVerticalStrut(4));
        izq.add(sub);

        Tema.BotonRedondo btnSalir = Tema.botonNeutro("Cerrar sesion");
        btnSalir.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSalir.addActionListener(e -> cerrarSesion());

        head.add(izq, BorderLayout.WEST);
        head.add(btnSalir, BorderLayout.EAST);
        return head;
    }

    private JComponent cuerpo() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(28, 28, 20, 28));

        p.add(seccionKpis(), BorderLayout.NORTH);
        p.add(seccionAccesos(), BorderLayout.CENTER);
        return p;
    }

    private JComponent seccionKpis() {
        lblClientes  = kpiValor();
        lblCuentas   = kpiValor();
        lblDepositos = kpiValor();

        return Tema.grilla(3, 18,
                kpiCard("Clientes registrados", lblClientes,  Tema.PRIMARY),
                kpiCard("Cuentas activas",       lblCuentas,   Tema.ACCENT),
                kpiCard("Total depositado (S/)", lblDepositos, new Color(0xE39A0C)));
    }

    private JComponent kpiCard(String etiqueta, JLabel valor, Color acento) {
        Tema.RoundedPanel card = new Tema.RoundedPanel(16, Tema.CARD, Tema.BORDE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        Tema.RoundedPanel barra = new Tema.RoundedPanel(6, acento, null);
        barra.setPreferredSize(new Dimension(6, 10));

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        txt.setBorder(new EmptyBorder(0, 14, 0, 0));
        valor.setAlignmentX(LEFT_ALIGNMENT);
        JLabel lbl = Tema.muted(etiqueta);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.add(valor);
        txt.add(Box.createVerticalStrut(4));
        txt.add(lbl);

        card.add(barra, BorderLayout.WEST);
        card.add(txt,   BorderLayout.CENTER);
        return card;
    }

    private static JLabel kpiValor() {
        JLabel l = new JLabel("-");
        l.setFont(new Font("Segoe UI", Font.BOLD, 28));
        l.setForeground(Tema.TXT);
        return l;
    }

    private JComponent seccionAccesos() {
        boolean admin = sesion.esAdmin();
        Tema.RoundedPanel card = Tema.tarjetaTitulada("Acceso rapido",
                "Use el menu superior para todas las operaciones");

        JPanel grid = new JPanel(new GridLayout(2, 3, 14, 14));
        grid.setOpaque(false);
        grid.add(botonAcceso("Clientes",      true,  () -> new ClienteForm().setVisible(true)));
        grid.add(botonAcceso("Cuentas",       true,  () -> new CuentaForm().setVisible(true)));
        grid.add(botonAcceso("Deposito",      true,  () -> new DepositoForm(sesion).setVisible(true)));
        grid.add(botonAcceso("Retiro",        true,  () -> new RetiroForm(sesion).setVisible(true)));
        grid.add(botonAcceso("Transferencia", true,  () -> new TransferenciaForm(sesion).setVisible(true)));
        grid.add(botonAcceso("Estado de Cuenta", true, () -> new EstadoCuentaForm().setVisible(true)));
        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JComponent botonAcceso(String texto, boolean habilitado, Runnable accion) {
        Tema.BotonRedondo b = habilitado ? Tema.botonPrimario(texto) : Tema.botonNeutro(texto);
        b.setEnabled(habilitado);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        b.addActionListener(e -> { try { accion.run(); } catch (Exception ex) { error(ex.getMessage()); } });
        return b;
    }

    private JComponent pie() {
        JLabel l = Tema.muted("Vista → Controlador → DAO → ConexionBD → MySQL · Patron MVC");
        l.setBorder(new EmptyBorder(4, 28, 10, 28));
        return l;
    }

    // ========================= LOGICA =========================
    private void refrescarIndicadores() {
        try {
            NumberFormat money = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));
            lblClientes.setText(String.valueOf(dashboard.totalClientes()));
            lblCuentas.setText(String.valueOf(dashboard.totalCuentas()));
            lblDepositos.setText(money.format(dashboard.totalDepositado()));
        } catch (Exception ex) {
            error("No se pudieron cargar los indicadores: " + ex.getMessage());
        }
    }

    private void cerrarSesion() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginBanco().setVisible(true));
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
