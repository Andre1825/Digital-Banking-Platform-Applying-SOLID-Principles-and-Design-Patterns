package uni.formularios;

import uni.controller.ClienteControlador;
import uni.entity.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Mantenimiento CRUD de Clientes, con busqueda parametrizada en la BD. */
public class ClienteForm extends JFrame {

    private final ClienteControlador controlador = new ClienteControlador();

    private final JTextField txtId = new JTextField(6);
    private final JTextField txtCodigo = new JTextField(8);
    private final JTextField txtDni = new JTextField(12);
    private final JTextField txtNombres = new JTextField(16);
    private final JTextField txtApellidos = new JTextField(16);
    private final JTextField txtDireccion = new JTextField(20);
    private final JTextField txtTelefono = new JTextField(12);
    private final JTextField txtEmail = new JTextField(18);
    private final JTextField txtBuscar = new JTextField(16);

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Codigo", "DNI", "Nombres", "Apellidos",
                    "Direccion", "Telefono", "Email"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(modelo);

    public ClienteForm() {
        setTitle("Mantenimiento de Clientes");
        setSize(900, 500);
        setLocationRelativeTo(null);
        initComponents();
        listar();
    }

    private void initComponents() {
        txtId.setEditable(false);
        txtCodigo.setEditable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Datos del cliente"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4); g.anchor = GridBagConstraints.WEST;
        int y = 0;
        campo(form, g, 0, y, "ID:", txtId);
        campo(form, g, 2, y, "Codigo:", txtCodigo); y++;
        campo(form, g, 0, y, "DNI:", txtDni);
        campo(form, g, 2, y, "Telefono:", txtTelefono); y++;
        campo(form, g, 0, y, "Nombres:", txtNombres);
        campo(form, g, 2, y, "Apellidos:", txtApellidos); y++;
        campo(form, g, 0, y, "Direccion:", txtDireccion);
        campo(form, g, 2, y, "Email:", txtEmail);

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
        buscar.add(new JLabel("Buscar (DNI / nombres / apellidos):"));
        buscar.add(txtBuscar);
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
        try { controlador.registrar(leerForm()); info("Cliente agregado."); limpiar(); listar(); }
        catch (Exception ex) { error(ex.getMessage()); }
    }

    private void actualizar() {
        if (txtId.getText().isEmpty()) { error("Seleccione un cliente."); return; }
        try {
            Cliente c = leerForm();
            c.setIdCliente(Integer.parseInt(txtId.getText()));
            controlador.modificar(c); info("Cliente actualizado."); limpiar(); listar();
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void eliminar() {
        if (txtId.getText().isEmpty()) { error("Seleccione un cliente."); return; }
        if (JOptionPane.showConfirmDialog(this, "Eliminar el cliente seleccionado?",
                "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { controlador.eliminar(Integer.parseInt(txtId.getText())); info("Cliente eliminado."); limpiar(); listar(); }
        catch (Exception ex) { error(ex.getMessage()); }
    }

    private Cliente leerForm() {
        Cliente c = new Cliente();
        c.setDni(txtDni.getText().trim());
        c.setNombres(txtNombres.getText().trim());
        c.setApellidos(txtApellidos.getText().trim());
        c.setDireccion(txtDireccion.getText().trim());
        c.setTelefono(txtTelefono.getText().trim());
        c.setEmail(txtEmail.getText().trim());
        return c;
    }

    private void listar() {
        try {
            modelo.setRowCount(0);
            List<Cliente> lista = controlador.buscar(txtBuscar.getText());
            for (Cliente c : lista) {
                modelo.addRow(new Object[]{c.getIdCliente(), c.getCodigo(), c.getDni(),
                        c.getNombres(), c.getApellidos(), c.getDireccion(),
                        c.getTelefono(), c.getEmail()});
            }
        } catch (Exception ex) { error(ex.getMessage()); }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        txtId.setText(String.valueOf(modelo.getValueAt(fila, 0)));
        txtCodigo.setText(str(modelo.getValueAt(fila, 1)));
        txtDni.setText(str(modelo.getValueAt(fila, 2)));
        txtNombres.setText(str(modelo.getValueAt(fila, 3)));
        txtApellidos.setText(str(modelo.getValueAt(fila, 4)));
        txtDireccion.setText(str(modelo.getValueAt(fila, 5)));
        txtTelefono.setText(str(modelo.getValueAt(fila, 6)));
        txtEmail.setText(str(modelo.getValueAt(fila, 7)));
    }

    private void limpiar() {
        txtId.setText(""); txtCodigo.setText(""); txtDni.setText("");
        txtNombres.setText(""); txtApellidos.setText(""); txtDireccion.setText("");
        txtTelefono.setText(""); txtEmail.setText("");
        tabla.clearSelection();
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
    private void info(String m) { JOptionPane.showMessageDialog(this, m); }
    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}
