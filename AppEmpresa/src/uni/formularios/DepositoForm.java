package uni.formularios;

import uni.controller.CuentaControlador;
import uni.entity.Cuenta;
import uni.entity.Empleado;
import uni.service.OperacionesFacade;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Formulario de proceso: DEPOSITO. Operacion transaccional que actualiza el
 * saldo e inserta el movimiento de forma atomica (ver ProcesoBancarioDAO).
 */
public class DepositoForm extends JFrame {

    private final OperacionesFacade operacion = new OperacionesFacade();
    private final CuentaControlador cuentaCtrl = new CuentaControlador();
    private final Empleado sesion;

    private final JComboBox<Cuenta> cboCuenta = new JComboBox<>();
    private final JTextField txtMonto = new JTextField(12);
    private final JLabel lblSaldo = new JLabel("-");

    public DepositoForm(Empleado sesion) {
        this.sesion = sesion;
        setTitle("Operacion - Deposito");
        setSize(440, 240);
        setLocationRelativeTo(null);
        initComponents();
        cargarCuentas();
    }

    private void initComponents() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6); g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; p.add(new JLabel("Cuenta:"), g);
        g.gridx = 1; p.add(cboCuenta, g);
        g.gridx = 0; g.gridy = 1; p.add(new JLabel("Saldo actual:"), g);
        g.gridx = 1; p.add(lblSaldo, g);
        g.gridx = 0; g.gridy = 2; p.add(new JLabel("Monto:"), g);
        g.gridx = 1; p.add(txtMonto, g);

        JButton btn = new JButton("Depositar");
        btn.setBackground(new Color(0x2E7D32)); btn.setForeground(Color.WHITE);
        g.gridx = 1; g.gridy = 3; p.add(btn, g);

        btn.addActionListener(e -> depositar());
        cboCuenta.addActionListener(e -> mostrarSaldo());
        add(p);
    }

    private void cargarCuentas() {
        try {
            cboCuenta.removeAllItems();
            for (Cuenta c : cuentaCtrl.listar()) cboCuenta.addItem(c);
            mostrarSaldo();
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void mostrarSaldo() {
        Cuenta c = (Cuenta) cboCuenta.getSelectedItem();
        lblSaldo.setText(c == null ? "-" : "S/ " + c.getSaldo() + "  [" + c.getEstado() + "]");
    }

    private void depositar() {
        Cuenta c = (Cuenta) cboCuenta.getSelectedItem();
        if (c == null) { error("Seleccione una cuenta."); return; }
        try {
            BigDecimal monto = new BigDecimal(txtMonto.getText().trim());
            operacion.depositar(c.getNumero(), monto, sesion.getIdEmpleado());
            JOptionPane.showMessageDialog(this, "Deposito realizado correctamente.");
            txtMonto.setText("");
            cargarCuentas();
        } catch (NumberFormatException ex) {
            error("Ingrese un monto numerico valido.");
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}
