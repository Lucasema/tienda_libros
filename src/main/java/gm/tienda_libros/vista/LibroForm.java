package gm.tienda_libros.vista;

import gm.tienda_libros.modelo.Libro;
import gm.tienda_libros.servicio.LibroServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Component
public class LibroForm extends JFrame {
    LibroServicio libroServicio;
    private JPanel panel;
    private JTable tablaLibros;
    private JTextField idTexto;
    private JTextField libroTexto;
    private JTextField autorTexto;
    private JTextField precioTexto;
    private JTextField existenciasTexto;
    private JButton agregarButton;
    private JButton modificarButton;
    private JButton eliminarButton;
    private DefaultTableModel tablaModeloLibros;

    @Autowired
    public LibroForm(LibroServicio libroServicio){
        this.libroServicio = libroServicio;
        iniciarForma();
        agregarButton.addActionListener(e -> agregarLibro());

        tablaLibros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                cargarLibroSeleccionado();
            }
        });
        modificarButton.addActionListener(e -> modificarLibro());

        eliminarButton.addActionListener(e -> eliminarLibro());
    }

    private void eliminarLibro() {
        // Los indices de las columnas inician en 0
        var renglon = tablaLibros.getSelectedRow();
        if(renglon != -1){ //Regresa -1 si no se selecciono ningun registro

            String idLibro = tablaLibros.getModel().getValueAt(renglon,0).toString(); // 0 -> idLibro
            idTexto.setText(idLibro);

            var libro = new Libro();
            libro.setIdLibro(Integer.parseInt(idLibro));
            libroServicio.eliminarLibro(libro);
            mostrarMensaje("Libro " + idLibro + " eliminado.");
            limpiarFormulario();
            listarLibros();

        }
        else{
            mostrarMensaje("No se ha seleccionado ningun registro a eliminar...");
        }
    }

    private void modificarLibro() {
        if (this.idTexto.getText().equals("")){
            mostrarMensaje("Debe seleccionar un registro...");
        }
        else{
            //Verificamos que el nombre del libro no sea nulo
            if(libroTexto.getText().equals("")){
                mostrarMensaje("Proporciona el nombre del libro...");
                libroTexto.requestFocusInWindow();
                return;
            }
            //Llenamos el objeto del libro a actualizar
            int idLibro = Integer.parseInt(idTexto.getText());
            var nombreLibro = libroTexto.getText();
            var autor = autorTexto.getText();
            var precio = Double.parseDouble(precioTexto.getText());
            var existencias = Integer.parseInt(existenciasTexto.getText());
            var libro = new Libro(idLibro,nombreLibro,autor,precio,existencias);

            libroServicio.guardarLibro(libro);
            mostrarMensaje("Se modifico el libro...");
            limpiarFormulario();
            listarLibros();
        }
    }

    private void iniciarForma(){
        setContentPane(panel); // panel es el del form
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // para que se cierre la aplicacion
        setVisible(true); // para que se visibilise el form
        setSize(900,700);
        Toolkit toolkit = Toolkit.getDefaultToolkit(); // obtener las dimensiones de la ventana
        Dimension tamanioPantalla = toolkit.getScreenSize();
        int x = (tamanioPantalla.width - getWidth()/2);
        int y = (tamanioPantalla.height - getHeight()/2);
        setLocation(x,y); // centrar la ventana
    }

    private void agregarLibro(){
        // Leer los valores del formulario
        if(libroTexto.getText().equals("")){
            mostrarMensaje("Proporciona el nombre del Libro");
            libroTexto.requestFocusInWindow();
            return;
        }
        var nombreLibro = libroTexto.getText();
        var autor = autorTexto.getText();
        var precio = Double.parseDouble(precioTexto.getText());
        var existencias =  Integer.parseInt(existenciasTexto.getText());

        //Crear el objeto libro
        var libro = new Libro(null, nombreLibro, autor, precio, existencias);
        this.libroServicio.guardarLibro(libro);
        mostrarMensaje("Se agrego el libro...");
        limpiarFormulario();
        listarLibros();
    }

    private void cargarLibroSeleccionado(){
        // Los indices de las columnas inician en 0
        var renglon = tablaLibros.getSelectedRow();
        if(renglon != -1){ //Regresa -1 si no se selecciono ningun registro

            String idLibro = tablaLibros.getModel().getValueAt(renglon,0).toString(); // 0 -> idLibro
            idTexto.setText(idLibro);

            String nombreLibro =  tablaLibros.getModel().getValueAt(renglon,1).toString();
            libroTexto.setText(nombreLibro);

            String autor =  tablaLibros.getModel().getValueAt(renglon,2).toString();
            autorTexto.setText(autor);

            String precio =  tablaLibros.getModel().getValueAt(renglon,3).toString();
            precioTexto.setText(precio);

            String existencias =  tablaLibros.getModel().getValueAt(renglon,4).toString();
            existenciasTexto.setText(existencias);

        }
    }

    private void limpiarFormulario(){
        libroTexto.setText("");
        autorTexto.setText("");
        precioTexto.setText("");
        existenciasTexto.setText("");

    }

    private void mostrarMensaje(String mensaje){
        JOptionPane.showMessageDialog(this, mensaje);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        //Creamos el elemento idTexto oculto
        idTexto = new JTextField("");
        idTexto.setVisible(false);

        this.tablaModeloLibros = new DefaultTableModel(0,5){
            @Override
            public boolean isCellEditable(int row, int column){ //campos de la tabla no editables
                return false;
            }
        };
        String[] cabeceros = {"Id","Libro","Autor","Precio","Existencia"}; // cabeceras de las columnas
        tablaModeloLibros.setColumnIdentifiers(cabeceros);

        //Instanciar el objeto JTable
        this.tablaLibros = new JTable(tablaModeloLibros);

        // Evitar que se seleccionen varios registros a la vez
        tablaLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listarLibros();
    }

    private void listarLibros(){
        //Limpiar la tabla
        tablaModeloLibros.setRowCount(0);
        //Obtener los libros
        var libros = libroServicio.listarLibros();
        libros.forEach((libro) ->{
            Object[] renglonLibro = { //Crear el registro
                    libro.getIdLibro(),
                    libro.getNombreLibro(),
                    libro.getAutor(),
                    libro.getPrecio(),
                    libro.getExistencias()
            };
            this.tablaModeloLibros.addRow(renglonLibro); //Agregarlo a la tabla
        });
    }


}


