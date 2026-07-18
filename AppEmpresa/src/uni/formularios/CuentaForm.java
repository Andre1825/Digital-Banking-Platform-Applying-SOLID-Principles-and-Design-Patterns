package uni.formularios;

import uni.controller.ClienteControlador;
import uni.controller.CuentaControlador;
import uni.controller.TipoCuentaControlador;
import uni.entity.Cliente;
import uni.entity.Cuenta;
import uni.entity.TipoCuenta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/** Mantenimiento CRUD de Cuentas (apertura y administracion). */
public class CuentaForm extends JFrame {

    private final CuentaControlador controlador = new CuentaControlador();
    private final ClienteControlador clienteCtrl = new ClienteControlador();
    private final TipoCuentaControlador tipoCtrl = new TipoCuentaControlador();

    private final JTextField txtNumero = new JTextField(12);
    private final JComboBox<Cliente> cboCliente = new JComboBox<>();
    private final JComboBox<TipoCuenta> cboTipo = new JComboBox<>();
    private final JTextField txtSaldo = new JTextField(10);
    private final JComboBox<String> cboEstado = new JComboBox<>(new String[]{"ACTIVA", "BLOQUEADA"});
    private final JTextField txtBuscar = new JTextField(14);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Numero", "Cliente", "Tipo", "Saldo", "Apertura", "Estado"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    public CuentaForm() {
        setTitle("Mantenimiento de Cuentas");
        setSize(860, 500);
        setLocationRelativeTo(null);
        initComponents();
        cargarCombos();
        listar();
    }

    private void initComponents() {
        txtNumero.setEditable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Datos de la cuenta"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4); g.anchor = GridBagConstraints.WEST;
        int y = 0;
        campo(form, g, 0, y, "Numero:", txtNumero);
        campo(form, g, 2, y, "Estado:", cboEstado); y++;
        campo(form, g, 0, y, "Cliente:", cboCliente);
        campo(form, g, 2, y, "Tipo cuenta:", cboTipo); y++;
        campo(form, g, 0, y, "Saldo inicial:", txtSaldo);

        JLabel nota = new JLabel("(El saldo solo se usa al aperturar; luego cambia por operaciones)");
        nota.setForeground(Color.GRAY);
        g.gridx = 2; g.gridy = y; g.gridwidth = 2; form.add(nota, g); g.gridwidth = 1;

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bAgregar = new JButton("Aperturar");
        JButton bActualizar = new JButton("Actualizar");
        JButton bEliminar = new JButton("Eliminar");
        JButton bLimpiar = new JButton("Limpiar");
        botones.add(bAgregar); botones.add(bActualizar);
        botones.add(bEliminar); botones.add(bLimpiar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

        JPanel buscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscar.add(new JLabel("Buscar:")); buscar.add(txtBuscar);
        JPanel centro = new JPanel(new BorderLayout());
        centro.add(buscar, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);

        add(norte, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        bAgregar.addActionListener(e -> aperturar());
        bActualizar.addActionListener(e -> actualizar());
        bEliminar.addActionListener(e -> eliminar());
        bLimpiar.addActionListener(e -> limpiar());
        txtBuscar.getDocument().addDocumentListener(new SimpleDoc(this::listar));
        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccion());
    }

    private void campo(JPanel p, GridBagConstraints g, int x, int y, String lbl, JComponent c) {
        g.gridx = x; g.gridy = y; p.add(new JLabel(lbl), g);
        g.gridx = x + 1; p.add(c, g);
    }

    private void cargarCombos() {
        try {
            cboCliente.removeAllItems();
            for (Cliente c : clienteCtrl.listar()) cboCliente.addItem(c);
            cboTipo.removeAllItems();
            for (TipoCuenta t : tipoCtrl.listar()) cboTipo.addItem(t);
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void aperturar() {
        try {
            Cuenta c = new Cuenta();
            Cliente cli = (Cliente) cboCliente.getSelectedItem();
            TipoCuenta tip = (TipoCuenta) cboTipo.getSelectedItem();
            c.setIdCliente(cli == null ? 0 : cli.getIdCliente());
            c.setIdTipoCuenta(tip == null ? 0 : tip.getIdTipoCuenta());
            c.setSaldo(parseSaldo());
            controlador.aperturar(c, tip == null ? "" : tip.getNombre());
            info("Cuenta aperturada: " + c.getNumero());
            limpiar(); listar();
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void actualizar() {
        if (txtNumero.getText().isEmpty()) { error("Seleccione una cuenta."); return; }
        try {
            Cuenta c = new Cuenta();
            c.setNumero(txtNumero.getText());
            Cliente cli = (Cliente) cboCliente.getSelectedItem();
            TipoCuenta tip = (TipoCuenta) cboTipo.getSelectedItem();
            c.setIdCliente(cli == null ? 0 : cli.getIdCliente());
            c.setIdTipoCuenta(tip == null ? 0 : tip.getIdTipoCuenta());
            c.setEstado((String) cboEstado.getSelectedItem());
            controlador.modificar(c);
            info("Cuenta actualizada."); limpiar(); listar();
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void eliminar() {
        if (txtNumero.getText().isEmpty()) { error("Seleccione una cuenta."); return; }
        if (JOptionPane.showConfirmDialog(this, "Eliminar la cuenta seleccionada?",
                "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { controlador.eliminar(txtNumero.getText()); info("Cuenta eliminada."); limpiar(); listar(); }
        catch (Exception ex) { error(ex.getMessage()); }
    }

    private BigDecimal parseSaldo() {
        String s = txtSaldo.getText().trim();
        if (s.isEmpty()) return BigDecimal.ZERO;
        try { return new BigDecimal(s); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("El saldo debe ser numerico."); }
    }

    private void listar() {
        try {
            modelo.setRowCount(0);
            String filtro = txtBuscar.getText().trim().toLowerCase();
            List<Cuenta> lista = controlador.listar();
            for (Cuenta c : lista) {
                String texto = (c.getNumero() + " " + c.getNombreCliente() + " "
                        + c.getNombreTipoCuenta()).toLowerCase();
                if (!filtro.isEmpty() && !texto.contains(filtro)) continue;
                modelo.addRow(new Object[]{c.getNumero(), c.getNombreCliente(),
                        c.getNombreTipoCuenta(), c.getSaldo(), c.getFechaApertura(), c.getEstado()});
            }
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        txtNumero.setText(str(modelo.getValueAt(fila, 0)));
        seleccionarCombo(cboCliente, str(modelo.getValueAt(fila, 1)), true);
        seleccionarCombo(cboTipo, str(modelo.getValueAt(fila, 2)), false);
        txtSaldo.setText(str(modelo.getValueAt(fila, 3)));
        cboEstado.setSelectedItem(str(modelo.getValueAt(fila, 5)));
    }

    private void seleccionarCombo(JComboBox<?> combo, String valor, boolean cliente) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object it = combo.getItemAt(i);
            String comp = cliente ? ((Cliente) it).getNombreCompleto().trim()
                                  : ((TipoCuenta) it).getNombre();
            if (valor.contains(comp) || comp.equals(valor)) { combo.setSelectedIndex(i); return; }
        }
    }

    private void limpiar() {
        txtNumero.setText(""); txtSaldo.setText("");
        if (cboCliente.getItemCount() > 0) cboCliente.setSelectedIndex(0);
        if (cboTipo.getItemCount() > 0) cboTipo.setSelectedIndex(0);
        cboEstado.setSelectedIndex(0);
        tabla.clearSelection();
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
    private void info(String m) { JOptionPane.showMessageDialog(this, m); }
    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}
