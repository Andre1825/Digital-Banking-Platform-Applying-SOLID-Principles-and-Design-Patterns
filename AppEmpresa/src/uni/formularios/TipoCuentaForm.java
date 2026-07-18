package uni.formularios;

import uni.controller.TipoCuentaControlador;
import uni.entity.TipoCuenta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Mantenimiento CRUD de Tipos de Cuenta (Ahorros, Corriente, ...). */
public class TipoCuentaForm extends JFrame {

    private final TipoCuentaControlador controlador = new TipoCuentaControlador();

    private final JTextField txtId = new JTextField(6);
    private final JTextField txtCodigo = new JTextField(8);
    private final JTextField txtNombre = new JTextField(16);
    private final JTextField txtTasa = new JTextField(8);
    private final JTextField txtBuscar = new JTextField(14);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Codigo", "Nombre", "Tasa interes"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    public TipoCuentaForm() {
        setTitle("Mantenimiento de Tipos de Cuenta");
        setSize(640, 440);
        setLocationRelativeTo(null);
        initComponents();
        listar();
    }

    private void initComponents() {
        txtId.setEditable(false);
        txtCodigo.setEditable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Datos del tipo de cuenta"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4); g.anchor = GridBagConstraints.WEST;
        int y = 0;
        campo(form, g, 0, y, "ID:", txtId);
        campo(form, g, 2, y, "Codigo:", txtCodigo); y++;
        campo(form, g, 0, y, "Nombre:", txtNombre);
        campo(form, g, 2, y, "Tasa (ej 0.025):", txtTasa);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bAgregar = new JButton("Agregar");
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

        bAgregar.addActionListener(e -> agregar());
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

    private void agregar() {
        try { controlador.registrar(leerForm()); info("Tipo de cuenta agregado."); limpiar(); listar(); }
        catch (Exception ex) { error(ex.getMessage()); }
    }

    private void actualizar() {
        if (txtId.getText().isEmpty()) { error("Seleccione un registro."); return; }
        try {
            TipoCuenta t = leerForm();
            t.setIdTipoCuenta(Integer.parseInt(txtId.getText()));
            controlador.modificar(t); info("Tipo de cuenta actualizado."); limpiar(); listar();
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void eliminar() {
        if (txtId.getText().isEmpty()) { error("Seleccione un registro."); return; }
        if (JOptionPane.showConfirmDialog(this, "Eliminar el tipo de cuenta?",
                "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { controlador.eliminar(Integer.parseInt(txtId.getText())); info("Eliminado."); limpiar(); listar(); }
        catch (Exception ex) { error(ex.getMessage()); }
    }

    private TipoCuenta leerForm() {
        TipoCuenta t = new TipoCuenta();
        t.setNombre(txtNombre.getText().trim());
        try {
            t.setTasaInteres(txtTasa.getText().trim().isEmpty() ? 0
                    : Double.parseDouble(txtTasa.getText().trim()));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("La tasa debe ser numerica (ej. 0.025).");
        }
        return t;
    }

    private void listar() {
        try {
            modelo.setRowCount(0);
            String filtro = txtBuscar.getText().trim().toLowerCase();
            List<TipoCuenta> lista = controlador.listar();
            for (TipoCuenta t : lista) {
                String texto = (t.getCodigo() + " " + t.getNombre()).toLowerCase();
                if (!filtro.isEmpty() && !texto.contains(filtro)) continue;
                modelo.addRow(new Object[]{t.getIdTipoCuenta(), t.getCodigo(),
                        t.getNombre(), t.getTasaInteres()});
            }
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        txtId.setText(String.valueOf(modelo.getValueAt(fila, 0)));
        txtCodigo.setText(str(modelo.getValueAt(fila, 1)));
        txtNombre.setText(str(modelo.getValueAt(fila, 2)));
        txtTasa.setText(str(modelo.getValueAt(fila, 3)));
    }

    private void limpiar() {
        txtId.setText(""); txtCodigo.setText(""); txtNombre.setText(""); txtTasa.setText("");
        tabla.clearSelection();
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
    private void info(String m) { JOptionPane.showMessageDialog(this, m); }
    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}
