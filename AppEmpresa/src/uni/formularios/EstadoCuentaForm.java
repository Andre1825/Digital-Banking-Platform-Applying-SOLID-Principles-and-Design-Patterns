package uni.formularios;

import uni.controller.CuentaControlador;
import uni.controller.OperacionControlador;
import uni.entity.Cuenta;
import uni.entity.Movimiento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Estado de cuenta: filtra los movimientos de una cuenta por rango de fechas
 * y los muestra en una tabla. Fechas en formato yyyy-MM-dd.
 */
public class EstadoCuentaForm extends JFrame {

    private final OperacionControlador operacion = new OperacionControlador();
    private final CuentaControlador cuentaCtrl = new CuentaControlador();

    private final JComboBox<Cuenta> cboCuenta = new JComboBox<>();
    private final JTextField txtDesde = new JTextField(10);
    private final JTextField txtHasta = new JTextField(10);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Codigo", "Fecha", "Tipo", "Monto", "Cta. relacionada", "Saldo resultante"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    public EstadoCuentaForm() {
        setTitle("Estado de Cuenta - Historial de Movimientos");
        setSize(820, 480);
        setLocationRelativeTo(null);
        initComponents();
        cargarCuentas();
    }

    private void initComponents() {
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        filtros.add(new JLabel("Cuenta:")); filtros.add(cboCuenta);
        filtros.add(new JLabel("Desde:"));  txtDesde.setText(LocalDate.now().withDayOfMonth(1).toString());
        filtros.add(txtDesde);
        filtros.add(new JLabel("Hasta:"));  txtHasta.setText(LocalDate.now().toString());
        filtros.add(txtHasta);
        JButton bConsultar = new JButton("Consultar");
        filtros.add(bConsultar);

        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        bConsultar.addActionListener(e -> consultar());
    }

    private void cargarCuentas() {
        try {
            cboCuenta.removeAllItems();
            for (Cuenta c : cuentaCtrl.listar()) cboCuenta.addItem(c);
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void consultar() {
        Cuenta c = (Cuenta) cboCuenta.getSelectedItem();
        if (c == null) { error("Seleccione una cuenta."); return; }
        try {
            modelo.setRowCount(0);
            List<Movimiento> movs = operacion.estadoCuenta(
                    c.getNumero(), txtDesde.getText().trim(), txtHasta.getText().trim());
            if (movs.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay movimientos en el rango indicado.");
            }
            for (Movimiento m : movs) {
                modelo.addRow(new Object[]{m.getCodigo(), m.getFecha(), m.getTipo(),
                        m.getMonto(), m.getCuentaRelacionada(), m.getSaldoResultante()});
            }
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}
