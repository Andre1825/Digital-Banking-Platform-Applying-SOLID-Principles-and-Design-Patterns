package uni.formularios;

import uni.controller.EmpleadoControlador;
import uni.controller.SucursalControlador;
import uni.entity.Empleado;
import uni.entity.Sucursal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Mantenimiento CRUD de Empleados (usuarios del sistema y sus roles). */
public class EmpleadoForm extends JFrame {

    private final EmpleadoControlador controlador = new EmpleadoControlador();
    private final SucursalControlador sucursalCtrl = new SucursalControlador();

    private final JTextField txtId = new JTextField(6);
    private final JTextField txtCodigo = new JTextField(8);
    private final JTextField txtNombres = new JTextField(16);
    private final JTextField txtApellidos = new JTextField(16);
    private final JTextField txtEmail = new JTextField(18);
    private final JTextField txtUsuario = new JTextField(12);
    private final JPasswordField txtClave = new JPasswordField(12);
    private final JComboBox<String> cboRol = new JComboBox<>(new String[]{"CAJERO", "ADMIN"});
    private final JComboBox<Sucursal> cboSucursal = new JComboBox<>();
    private final JTextField txtBuscar = new JTextField(14);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Codigo", "Nombres", "Apellidos", "Email",
                    "Usuario", "Rol", "Sucursal"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    public EmpleadoForm() {
        setTitle("Mantenimiento de Empleados");
        setSize(900, 520);
        setLocationRelativeTo(null);
        initComponents();
        cargarSucursales();
        listar();
    }

    private void initComponents() {
        txtId.setEditable(false);
        txtCodigo.setEditable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Datos del empleado"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4); g.anchor = GridBagConstraints.WEST;
        int y = 0;
        campo(form, g, 0, y, "ID:", txtId);
        campo(form, g, 2, y, "Codigo:", txtCodigo); y++;
        campo(form, g, 0, y, "Nombres:", txtNombres);
        campo(form, g, 2, y, "Apellidos:", txtApellidos); y++;
        campo(form, g, 0, y, "Email:", txtEmail);
        campo(form, g, 2, y, "Usuario:", txtUsuario); y++;
        campo(form, g, 0, y, "Clave:", txtClave);
        campo(form, g, 2, y, "Rol:", cboRol); y++;
        campo(form, g, 0, y, "Sucursal:", cboSucursal);

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

    private void cargarSucursales() {
        try {
            cboSucursal.removeAllItems();
            Sucursal vacio = new Sucursal(); vacio.setIdSucursal(0);
            vacio.setNombre("(Sin asignar)");
            cboSucursal.addItem(vacio);
            for (Sucursal s : sucursalCtrl.listar()) cboSucursal.addItem(s);
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void agregar() {
        try { controlador.registrar(leerForm()); info("Empleado agregado."); limpiar(); listar(); }
        catch (Exception ex) { error(ex.getMessage()); }
    }

    private void actualizar() {
        if (txtId.getText().isEmpty()) { error("Seleccione un empleado."); return; }
        try {
            Empleado e = leerForm();
            e.setIdEmpleado(Integer.parseInt(txtId.getText()));
            controlador.modificar(e); info("Empleado actualizado."); limpiar(); listar();
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void eliminar() {
        if (txtId.getText().isEmpty()) { error("Seleccione un empleado."); return; }
        if (JOptionPane.showConfirmDialog(this, "Eliminar el empleado seleccionado?",
                "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { controlador.eliminar(Integer.parseInt(txtId.getText())); info("Empleado eliminado."); limpiar(); listar(); }
        catch (Exception ex) { error(ex.getMessage()); }
    }

    private Empleado leerForm() {
        Empleado e = new Empleado();
        e.setNombres(txtNombres.getText().trim());
        e.setApellidos(txtApellidos.getText().trim());
        e.setEmail(txtEmail.getText().trim());
        e.setUsuario(txtUsuario.getText().trim());
        e.setClave(new String(txtClave.getPassword()));
        e.setRol((String) cboRol.getSelectedItem());
        Sucursal s = (Sucursal) cboSucursal.getSelectedItem();
        e.setIdSucursal(s == null ? 0 : s.getIdSucursal());
        return e;
    }

    private void listar() {
        try {
            modelo.setRowCount(0);
            String filtro = txtBuscar.getText().trim().toLowerCase();
            List<Empleado> lista = controlador.listar();
            for (Empleado e : lista) {
                String texto = (e.getCodigo() + " " + e.getNombreCompleto() + " "
                        + e.getUsuario() + " " + e.getRol()).toLowerCase();
                if (!filtro.isEmpty() && !texto.contains(filtro)) continue;
                modelo.addRow(new Object[]{e.getIdEmpleado(), e.getCodigo(), e.getNombres(),
                        e.getApellidos(), e.getEmail(), e.getUsuario(), e.getRol(),
                        e.getNombreSucursal()});
            }
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        txtId.setText(String.valueOf(modelo.getValueAt(fila, 0)));
        txtCodigo.setText(str(modelo.getValueAt(fila, 1)));
        txtNombres.setText(str(modelo.getValueAt(fila, 2)));
        txtApellidos.setText(str(modelo.getValueAt(fila, 3)));
        txtEmail.setText(str(modelo.getValueAt(fila, 4)));
        txtUsuario.setText(str(modelo.getValueAt(fila, 5)));
        txtClave.setText("");
        cboRol.setSelectedItem(str(modelo.getValueAt(fila, 6)));
        seleccionarSucursalPorNombre(str(modelo.getValueAt(fila, 7)));
    }

    private void seleccionarSucursalPorNombre(String nombre) {
        for (int i = 0; i < cboSucursal.getItemCount(); i++) {
            if (cboSucursal.getItemAt(i).getNombre().equals(nombre)) {
                cboSucursal.setSelectedIndex(i); return;
            }
        }
        cboSucursal.setSelectedIndex(0);
    }

    private void limpiar() {
        txtId.setText(""); txtCodigo.setText(""); txtNombres.setText("");
        txtApellidos.setText(""); txtEmail.setText(""); txtUsuario.setText("");
        txtClave.setText(""); cboRol.setSelectedIndex(0); cboSucursal.setSelectedIndex(0);
        tabla.clearSelection();
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
    private void info(String m) { JOptionPane.showMessageDialog(this, m); }
    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}
