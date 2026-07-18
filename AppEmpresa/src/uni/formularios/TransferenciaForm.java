package uni.formularios;

import uni.controller.CuentaControlador;
import uni.entity.Cuenta;
import uni.entity.Empleado;
import uni.service.OperacionesFacade;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Formulario de proceso: TRANSFERENCIA entre cuentas. Operacion atomica:
 * debita el origen, acredita el destino e inserta dos movimientos; si algo
 * falla (saldo insuficiente, cuenta bloqueada) se revierte todo (rollback).
 */
public class TransferenciaForm extends JFrame {

    private final OperacionesFacade operacion = new OperacionesFacade();
    private final CuentaControlador cuentaCtrl = new CuentaControlador();
    private final Empleado sesion;

    private final JComboBox<Cuenta> cboOrigen = new JComboBox<>();
    private final JComboBox<Cuenta> cboDestino = new JComboBox<>();
    private final JTextField txtMonto = new JTextField(12);
    private final JLabel lblSaldoOrigen = new JLabel("-");
    private final JCheckBox chkExterna = new JCheckBox("Interbancaria (otra entidad, via camara de compensacion)");

    public TransferenciaForm(Empleado sesion) {
        this.sesion = sesion;
        setTitle("Operacion - Transferencia");
        setSize(480, 280);
        setLocationRelativeTo(null);
        initComponents();
        cargarCuentas();
    }

    private void initComponents() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6); g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; p.add(new JLabel("Cuenta origen:"), g);
        g.gridx = 1; p.add(cboOrigen, g);
        g.gridx = 0; g.gridy = 1; p.add(new JLabel("Saldo origen:"), g);
        g.gridx = 1; p.add(lblSaldoOrigen, g);
        g.gridx = 0; g.gridy = 2; p.add(new JLabel("Cuenta destino:"), g);
        g.gridx = 1; p.add(cboDestino, g);
        g.gridx = 0; g.gridy = 3; p.add(new JLabel("Monto:"), g);
        g.gridx = 1; p.add(txtMonto, g);
        g.gridx = 0; g.gridy = 4; g.gridwidth = 2; p.add(chkExterna, g); g.gridwidth = 1;

        JButton btn = new JButton("Transferir");
        btn.setBackground(new Color(0x1F4E79)); btn.setForeground(Color.WHITE);
        g.gridx = 1; g.gridy = 5; p.add(btn, g);

        btn.addActionListener(e -> transferir());
        cboOrigen.addActionListener(e -> mostrarSaldo());
        add(p);
    }

    private void cargarCuentas() {
        try {
            cboOrigen.removeAllItems();
            cboDestino.removeAllItems();
            for (Cuenta c : cuentaCtrl.listar()) {
                cboOrigen.addItem(c);
                cboDestino.addItem(c);
            }
            if (cboDestino.getItemCount() > 1) cboDestino.setSelectedIndex(1);
            mostrarSaldo();
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void mostrarSaldo() {
        Cuenta c = (Cuenta) cboOrigen.getSelectedItem();
        lblSaldoOrigen.setText(c == null ? "-" : "S/ " + c.getSaldo() + "  [" + c.getEstado() + "]");
    }

    private void transferir() {
        Cuenta origen = (Cuenta) cboOrigen.getSelectedItem();
        Cuenta destino = (Cuenta) cboDestino.getSelectedItem();
        if (origen == null || destino == null) { error("Seleccione cuentas origen y destino."); return; }
        try {
            BigDecimal monto = new BigDecimal(txtMonto.getText().trim());
            operacion.transferir(origen.getNumero(), destino.getNumero(), monto,
                    sesion.getIdEmpleado(), chkExterna.isSelected());
            JOptionPane.showMessageDialog(this, chkExterna.isSelected()
                    ? "Transferencia interbancaria enviada correctamente."
                    : "Transferencia realizada correctamente.");
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
