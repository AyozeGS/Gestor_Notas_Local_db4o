/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;

/**
 *
 * @author Ayoze Gil
 */
public class DataConnection {
    
    private static DataConnection INSTANCE = null;
    private final String FILE = "AGS_ODB.dbo";
    private static ObjectContainer db;

    //Constructor privado
    private DataConnection() {
    }
    
    //Crear nueva instancia
    private synchronized static void createInstance(){
        if (INSTANCE == null) {
            INSTANCE = new DataConnection();
            INSTANCE.performConnection();
        }
    }
    
    //Método getter de la instancia, llama al método para crea si no existe
    public static ObjectContainer getInstance() {
        if (INSTANCE == null)
            createInstance();
        return db;
    }
    
    //Iniciar conexión con la base de datos.
    public void performConnection(){
        EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
        db = Db4oEmbedded.openFile(config,FILE);
    }

    //Cerrar conexión con la base de datos
    public void closeConnection(){
        db.close();
    }
    
    
    
}

