/*
    Clase que representa una conjunto de Documentos.
    Esta clase es la estructura de datos para entre el Cliente  ServPrincipal
*/


import java.util.*;
import java.io.*;

public class Coleccion implements Serializable {

    // Posiblemente haya que hacerlo concurrente
    HashMap<String,Documento> almacen;
    String nombreProyecto;
    // Una fecha. Usar un java.util.Date

    public Coleccion(){
        almacen = new HashMap<String,Documento>();
    }
    public Coleccion(String nombreProyecto){
        almacen = new HashMap<String,Documento>();
        this.nombreProyecto = nombreProyecto;
    }

    public boolean agregarDocumento(String camino) {
        Documento doc = new Documento(camino);
        if (doc != null){
            // Falta implementar la semantica de archivos inmutables
            almacen.put(doc.obtNombre(),doc);
            return true;
        }
        else
            return false;

    }

    public boolean agregarDocumento(Documento doc) {
         if (doc != null){
            // Falta implementar la semantica de archivos inmutables
            almacen.put(doc.obtNombre(),doc);
            return true;
        }
        else
            return false;
    }

    public Collection<Documento> obtDocumentos() {
        return almacen.values();
    }

    public boolean coleccionVacia() {
        return almacen.isEmpty();
    }

    public int obtNroArchivos() {
        return almacen.size();
    }

    public String obtNombreProyecto(){
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

}