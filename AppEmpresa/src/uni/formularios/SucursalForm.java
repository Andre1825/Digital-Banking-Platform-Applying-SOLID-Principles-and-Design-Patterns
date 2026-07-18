package uni.formularios;

import uni.controller.SucursalControlador;
import uni.entity.Sucursal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Mantenimiento CRUD de Sucursales: Agregar, Actualizar, Eliminar, Limpiar,
 * con tabla que se refresca tras cada operacion y filtro de busqueda.
 * La vista NO contiene SQL: delega todo en SucursalControlador.
 */
public class SucursalForm extends JFrame {

    private final SucursalControlador controlador = new SucursalControlador();

    private final JTextField txtId = new JTextField(6);
    private final JTextField txtCodigo = new JTextField(8);
    private final JTextField txtNombre = new JTextField(18);
    private final JTextField txtDireccion = new JTextField(18);
    private final JTextField txtTelefono = new JTextField(12);
    private final JTextField txtBuscar = new JTextField(14);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Codigo", "Nombre", "Direccion", "Telefono"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    public SucursalForm() {
        setTitle("Mantenimiento de Sucursales");
        setSize(720, 460);
        setLocationRelativeTo(null);
        initComponents();
        listar();
    }

    private void initComponents() {
        txtId.setEditable(false);
        txtCodigo.setEditable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Datos de la sucursal"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4); g.anchor = GridBagConstraints.WEST;
        int y = 0;
        addCampo(form, g, 0, y, "ID:", txtId);
        addCampo(form, g, 2, y, "Codigo:", txtCodigo); y++;
        addCampo(form, g, 0, y, "Nombre:", txtNombre);
        addCampo(form, g, 2, y, "Telefono:", txtTelefono); y++;
        addCampo(form, g, 0, y, "Direccion:", txtDireccion);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton bAgregar = new JButton("Agregar");
        JButton bActualizar = new JButton("Actualizar");
        JButton bEliminar = new JButton("Eliminar");
        JButton bLimpiar = new JButton("Limpiar");
        botones.add(bAgregar); botones.add(bActualizar);
        botones.add(bEliminar); botones.add(bLimpiar);

        JPanel buscar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buscar.add(new JLabel("Buscar:")); buscar.add(txtBuscar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.CENTER);
        norte.add(botones, BorderLayout.SOUTH);

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

    private void addCampo(JPanel p, GridBagConstraints g, int x, int y, String lbl, JComponent c) {
        g.gridx = x; g.gridy = y; p.add(new JLabel(lbl), g);
        g.gridx = x + 1; p.add(c, g);
    }

    // ----------------- Acciones CRUD -----------------
    private void agregar() {
        try {
            controlador.registrar(leerForm());
            info("Sucursal agregada.");
            limpiar(); listar();
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void actualizar() {
        if (txtId.getText().isEmpty()) { error("Seleccione una sucursal de la tabla."); return; }
        try {
            Sucursal s = leerForm();
            s.setIdSucursal(Integer.parseInt(txtId.getText()));
            controlador.modificar(s);
            info("Sucursal actualizada.");
            limpiar(); listar();
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void eliminar() {
        if (txtId.getText().isEmpty()) { error("Seleccione una sucursal de la tabla."); return; }
        if (JOptionPane.showConfirmDialog(this, "Eliminar la sucursal seleccionada?",
                "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            controlador.eliminar(Integer.parseInt(txtId.getText()));
            info("Sucursal eliminada.");
            limpiar(); listar();
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private Sucursal leerForm() {
        Sucursal s = new Sucursal();
        s.setNombre(txtNombre.getText().trim());
        s.setDireccion(txtDireccion.getText().trim());
        s.setTelefono(txtTelefono.getText().trim());
        return s;
    }

    private void listar() {
        try {
            modelo.setRowCount(0);
            String filtro = txtBuscar.getText().trim().toLowerCase();
            List<Sucursal> lista = controlador.listar();
            for (Sucursal s : lista) {
                if (!filtro.isEmpty()
                        && !(textoFila(s).toLowerCase().contains(filtro))) continue;
                modelo.addRow(new Object[]{s.getIdSucursal(), s.getCodigo(),
                        s.getNombre(), s.getDireccion(), s.getTelefono()});
            }
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private String textoFila(Sucursal s) {
        return s.getCodigo() + " " + s.getNombre() + " " + s.getDireccion() + " " + s.getTelefono();
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        txtId.setText(String.valueOf(modelo.getValueAt(fila, 0)));
        txtCodigo.setText(str(modelo.getValueAt(fila, 1)));
        txtNombre.setText(str(modelo.getValueAt(fila, 2)));
        txtDireccion.setText(str(modelo.getValueAt(fila, 3)));
        txtTelefono.setText(str(modelo.getValueAt(fila, 4)));
    }

    private void limpiar() {
        txtId.setText(""); txtCodigo.setText(""); txtNombre.setText("");
        txtDireccion.setText(""); txtTelefono.setText("");
        tabla.clearSelection();
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
    private void info(String m) { JOptionPane.showMessageDialog(this, m); }
    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}
