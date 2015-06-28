/*
    Clase Documento
    Representa un archivo almacenado en bytes.
*/
import java.io.*;
import java.util.Date;
public class Documento implements Serializable {

    private byte[] contenido;
    private String camino;
    private String nombre;
    private Date fecha;
    // Atributos utiles para el documento

    public Documento(String camino) {
        File archivo = new File(camino);
        try {
            byte[] temp = new byte[(int) archivo.length()];
            BufferedInputStream entrada = new
                BufferedInputStream(new FileInputStream(archivo));

            entrada.read(temp,0,temp.length);
            entrada.close();
            contenido = temp;
        } catch(Exception e) {
            System.out.println("El archivo dado no existe.");
        }

        this.camino = camino;
        nombre = archivo.getName();

    }

    public String obtNombre() {
        return nombre;
    }

    public String obtCamino() {
        return camino;
    }

    /*public File obtContenidoArchivo() {
         File temp = new File(//Nombre o camino del archivo(Investigar));
         BufferedOutputStream salida = new
           BufferedOutputStream(new FileOutputStream(temp.getName()));
         salida.write(temp,0,temp.length);
         salida.flush();
         salida.close();
         return temp;
    }*/
    
    public Date obtFecha() {
        return fecha;
    }

    public byte[] obtContenidoByte() {
        return contenido;
    }

    public String toString() {
        try {
            if (contenido != null) {
           String cont = new String(contenido, "UTF-8");
           return cont;
            }
       } catch (UnsupportedEncodingException uee) {
            System.out.println("Documento: "+ uee.getMessage());
       }

        return null;

    }

    // Main de prueba de Documento
    public static void main(String[] args) {
        System.out.println("Archivo a abrir: " +args[0]);
        Documento doc = new Documento(args[0]);
        System.out.println(doc.obtNombre());
        System.out.println(doc.obtCamino());
        if (doc != null)  {
            System.out.println(doc.toString());
        }
    }
}