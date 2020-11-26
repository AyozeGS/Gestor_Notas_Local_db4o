package controller;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import connection.DataConnection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.Document;
import model.User;
import view.MainView;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ayoze Gil
 */
public class Controller implements ActionListener, ListSelectionListener{
    
    //Attributes
    private static ObjectContainer db;
    private final MainView view;
    private List<Document> current_document;
    private List<User> current_user;
    private List<Document> documents;
    private List<User> users;
    
    //Constructor
    public Controller(){
        db = DataConnection.getInstance();
        view = new MainView();
        view.setVisible(true);
        initEvents();
    }
    
    //Initilize event handlers
    private void initEvents(){
        view.getBtnAccess().addActionListener(this);
        view.getBtnRegister().addActionListener(this);
        view.getBtnClose().addActionListener(this);
        view.getBtnAdd().addActionListener(this);
        view.getBtnDelete().addActionListener(this);
        view.getBtnSave().addActionListener(this);
        view.getBtnExport().addActionListener(this);
        view.getBtnImport().addActionListener(this);
        view.getBtnCloseSession().addActionListener(this);
        view.getBtnShare().addActionListener(this);
        view.getBtnUnshare().addActionListener(this);
        view.getBtnDeleteUser().addActionListener(this);
        view.getListDocuments().addListSelectionListener(this);
    }
 
    //Check selected button for calling methods
    @Override
    public void actionPerformed(ActionEvent ae) {
        switch(ae.getActionCommand()){
            case "Login":
                login();
                break;
            case "Register":
                register();
                break;
            case "Create":
                createDocument();
                break;
            case "Delete":
                deleteDocument();
                break;
            case "Save":
                saveDocument();
                break;
             case "Share":
                share();
                break;
            case "Unshare":
                unshare();
                break;
            case "Import":
                loadFile();
                break;
            case "Export":
                saveFile();
                break;
            case "DeleteUser":
                deleteUser();
                break;
            case "CloseSession":
                view.closeSession();
                break;
            case "Close":
                db.close();
                System.exit(0);
                break;
            default:
                break;
        }
    } 
    
    //Check changes in document list for user and update if changed
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if (!view.getListDocuments().isSelectionEmpty()){
            view.getTxtTitle().setText(documents.get(
                    view.getListDocuments().getSelectedIndex()).getTitle());
            view.getTxtContent().setText(documents.get(
                    view.getListDocuments().getSelectedIndex()).getContent());
            current_document = db.query(new Predicate<Document>() {
                @Override
                public boolean match(Document d) {
                    return d.getId() == documents.get(
                            view.getListDocuments().getSelectedIndex()).getId();
                }
            });
            view.getLblUserMessage().setText(
                    "Seleccionado documento "+current_document.get(0).getId());   
        }
        else
        {
            view.getTxtTitle().setText("");
            view.getTxtContent().setText("");
            view.getLblUserMessage().setText("");
            current_document = null;
        }
    }
    
    //Method for login session
    private void login(){
        
        if (view.getTxtUser().getText().equals("") || 
                String.valueOf(view.getTxtPassword().getPassword()).equals(""))
            view.getLblMessage().setText("No ha introducido los datos");
        else {
            current_user = db.query(new Predicate<User>() {
                @Override
                public boolean match(User u) {
                    return (u.getName().equals(view.getTxtUser().getText()) && 
                    u.getPassword().equals(new String(view.getTxtPassword().getPassword())));
                }
            });
            if (current_user.size()>0){
                view.login();
                view.setTitle(current_user.get(0).getName());
                loadUsers();
                loadUserDocuments();
            }
            else
                view.getLblMessage().setText("Login Incorrecto");
        }
    }
    
    //Method for register a new user
    private void register() {

        if (view.getTxtUser().getText().equals("") || 
                String.valueOf(view.getTxtPassword().getPassword()).equals(""))
            view.getLblMessage().setText("No ha introducido los datos");
        else {
            current_user = db.query(new Predicate<User>() {
                @Override
                public boolean match(User u) {
                    return u.getName().equals(view.getTxtUser().getText());
                }
            });
            if(current_user.size()>0){
                view.getLblMessage().setText("El usuario ya existe");
            }
            else{
                db.store(new User(view.getTxtUser().getText(),
                        new String(view.getTxtPassword().getPassword())));
                db.commit();
                view.getLblMessage().setText("Usuario Registrado");
                //view.getTxtUser().setText("");
                //view.getTxtPassword().setText("");
            }
        }   
    }
    
    //Method for update users list in Combobox
    private void loadUsers(){
        users = db.query(new Predicate<User>() {
                @Override
                public boolean match(User u) {
                    return (!u.getName().equals(view.getTitle()));
                }
            });
        DefaultComboBoxModel combobox = new DefaultComboBoxModel();
        combobox.addElement("");
        users.forEach((u) -> {
            combobox.addElement(u.getName());
        });
        view.getCmbUsers().setModel(combobox);
    }
    
    //Method for update documents list in JList
    private void loadUserDocuments(){
        documents = db.query(new Predicate<Document>() {
                @Override
                public boolean match(Document d) {
                    return d.getUsers().contains(view.getTitle());
                }
            });
        DefaultListModel list = new DefaultListModel();
        documents.forEach((d) -> {
            list.addElement(d.getTitle());
        });
        view.getListDocuments().setModel(list);
        view.getTxtTitle().setText("");
        view.getTxtContent().setText("");
    }
    
    //Method for create a new document
    private void createDocument(){
        if (!view.getTxtTitle().getText().equals("")){
            Document doc = new Document(view.getTxtTitle().getText(),
                    view.getTxtContent().getText());
            doc.getUsers().add(current_user.get(0).getName());
            db.store(doc);
            db.commit();
            current_document = db.query(new Predicate<Document>() {
                @Override
                public boolean match(Document d) {
                    return (d.getId() == doc.getId());
                }
            });
            current_user = db.query(new Predicate<User>() {
                @Override
                public boolean match(User u) {
                    return (u.getName().equals(view.getTitle()));
                }
            });
            if (current_user.size()>0){
                current_user.get(0).getDocuments().add(doc.getId());
            }
            loadUserDocuments();
            view.getListDocuments().setSelectedIndex(view.getListDocuments().getModel().getSize()-1); 
            view.getLblUserMessage().setText(
                    "Creado documento "+current_document.get(0).getId());  
        }
        else
            view.getLblUserMessage().setText("Inserte nombre de archivo");
    }
    
    //Method for delete a document
    private void deleteDocument(){
        
        if (current_document.get(0) != null){
            
            Document doc = new Document();
            int index_document = view.getListDocuments().getSelectedIndex();
            doc.setDocument(documents.get(index_document));
            
            Iterator<String> iter_s = doc.getUsers().iterator();
            while (iter_s.hasNext()) {
                String s = iter_s.next();
                if(view.getTitle().equalsIgnoreCase(s)){
                    iter_s.remove();
                }
            }
            db.delete(documents.get(index_document));
            db.commit();
            if (!doc.getUsers().isEmpty()){
                db.store(doc);
                db.commit();
                loadUserDocuments();
                view.getLblUserMessage().setText("Borrados permisos");  
            }
            else{
                loadUserDocuments();
                view.getLblUserMessage().setText("Borrado documento");  
            }
        }
        else
            view.getLblUserMessage().setText("Seleccione documento"); 
            
    }
    
    //Method for edit a document
    private void saveDocument(){
        if (current_document != null 
                && !view.getTxtTitle().getText().equals("")){
            int index = view.getListDocuments().getSelectedIndex();
            current_document.get(0).setTitle(view.getTxtTitle().getText());
            current_document.get(0).setContent(view.getTxtContent().getText());
            db.store(current_document.get(0));
            db.commit();
            loadUserDocuments();
            view.getListDocuments().setSelectedIndex(index);
            view.getLblUserMessage().setText(
                "Editado documento "+current_document.get(0).getId());   
        }
        else
            view.getLblUserMessage().setText(
                    "No se encuentra título del documento"); 
    }
    
    //Method for share a document with other user
    private void share() {
        if (view.getCmbUsers().getSelectedIndex()>0){
            Document doc = new Document();
            int index_document = view.getListDocuments().getSelectedIndex();
            String name_user = view.getCmbUsers().getSelectedItem().toString();
            doc.setDocument(documents.get(index_document));
            if (!doc.getUsers().
                    contains(view.getCmbUsers().getSelectedItem().toString())){
                doc.getUsers().add(name_user);
                db.delete(documents.get(index_document));
                db.commit();
                db.store(doc);
                db.commit();
                loadUserDocuments();
                view.getListDocuments().setSelectedIndex(
                        view.getListDocuments().getModel().getSize()-1);
                view.getLblUserMessage().setText("Fichero Compartido");
                
            }
            else
                view.getLblUserMessage().setText("El usuario ya posee permiso");
        }
        else
            view.getLblUserMessage().setText("Seleccione fichero y usuario");
    }

    //Method for unshare a document to other user
    private void unshare(){
        if (view.getCmbUsers().getSelectedIndex()>0){
            Document doc = new Document();
            int index_document = view.getListDocuments().getSelectedIndex();
            String name_user = view.getCmbUsers().getSelectedItem().toString();
            doc.setDocument(documents.get(index_document));
            if (doc.getUsers().contains(name_user)){
                Iterator<String> iter_s = doc.getUsers().iterator();
                while (iter_s.hasNext()) {
                    String s = iter_s.next();
                    if(name_user.equalsIgnoreCase(s))
                        iter_s.remove();
                }
                db.delete(documents.get(index_document));
                db.commit();
                db.store(doc);
                db.commit();
                loadUserDocuments();
                view.getLblUserMessage().setText("Permisos borrados");
            }
            else
                view.getLblUserMessage().setText("El usuario no tiene permiso");
        }
        else
            view.getLblUserMessage().setText("Seleccione fichero y usuario");
    }
    
    //Method for load a document from a txt file
    private void loadFile(){
        File file;
        FileReader fr;
        String line;
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
        fileChooser.setFileFilter(filter);
        int selection = fileChooser.showOpenDialog(view);
        if (selection == JFileChooser.APPROVE_OPTION){
            try {
                file = fileChooser.getSelectedFile();
                view.getTxtTitle().
                        setText(file.getName().substring(0, file.getName().length()-4));
                fr = new FileReader (file);
                BufferedReader br = new BufferedReader(fr);
                view.getTxtContent().setText("");
                while((line=br.readLine())!=null)
                    view.getTxtContent().append(line+"\n");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            view.getLblUserMessage().setText("Cargado desde fichero de texto");
        }
        else
            view.getLblUserMessage().setText("Fichero no cargado");
    }
    
    //Method for save a document to a txt file
    private void saveFile(){
        File file;
        PrintWriter pw = null;
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Selecciona fihero a guardar...");  
        fileChooser.setSelectedFile(new File(view.getTxtTitle().getText()+".txt"));
        int selection = fileChooser.showSaveDialog(view);
        if (selection == JFileChooser.APPROVE_OPTION){
            try {
                file = fileChooser.getSelectedFile();
                pw = new PrintWriter(new PrintWriter(file));
                pw.println(view.getTxtContent().getText());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (null != pw)
                    pw.close();
            }
            view.getLblUserMessage().setText("Guardado en fichero de texto");
        }
        else
            view.getLblUserMessage().setText("No se ha guardado");
    }

    //Method for delete user
    private void deleteUser() {
        if (documents.size() > 0){
            for (int i=0; i<=documents.size();i++){
                view.getListDocuments().setSelectedIndex(0);
                deleteDocument();
            }
        }
        
        current_user = db.query(new Predicate<User>() {
                @Override
                public boolean match(User u) {
                    return (u.getName().equals(view.getTitle()));
                }
            });
        db.delete(current_user.get(0));
        db.commit();
        view.closeSession();
    }
}
