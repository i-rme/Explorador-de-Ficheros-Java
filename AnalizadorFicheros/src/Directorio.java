import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Directorio {

    ArrayList<File> contenido = new ArrayList<File>();
    File fileRaiz;
    Long tamRaiz;

    Directorio(File raiz){
        fileRaiz = raiz;
        tamRaiz = obtenerTamano(raiz);
    }

    public void inicializarContenido(File fdir, long tiempoInicio) {    //limite de 15 segundos
        if (fdir.isDirectory()) {
            contenido.add(fdir);
            File[] files = fdir.listFiles();
            if(files!=null) {
                for (File f : fdir.listFiles()) {    //Lanza excepción si contiene demasiados archivos
                    if (System.currentTimeMillis()-tiempoInicio < 15000) inicializarContenido(f, tiempoInicio);
                }
            }
        }else{
            contenido.add(fdir);
        }
    }

    public void inicializarContenido() {   //De esta manera obviamos el directorio raiz
        for (File f : fileRaiz.listFiles()) {	//Lanza excepción si contiene demasiados archivos
            inicializarContenido(f, System.currentTimeMillis());
        }
    }

    public void ordenarSegunTamano() {
        if(contenido.size() >= 2500) {
            //No ordenar ya que es una tarea con un coste mayor a O(n^2) y no acabaría nunca
        }else{
            Collections.sort(contenido, comparadorTamanosASC);
        }
    }


    public static void obtenerContenido(File fdir) {
        if (fdir.isDirectory()) {
            System.out.println("DIR> " + fdir + " TAMAÑO> " + obtenerTamano(fdir));
            for (File f : fdir.listFiles()) {
                obtenerContenido(f);
            }
        }else{
            System.out.println("FILE> " + fdir + " TAMAÑO> " + fdir.length());
        }
    }


    public static long obtenerTamano(File fdir){
        //System.out.println(fdir.getPath());
        if (fdir.isDirectory()) {
            long length = 0;
            File[] files = fdir.listFiles();

            if(files==null) return 0;

            int count = files.length;

            for (int i = 0; i < count; i++) {
                if (files[i].isFile()) {
                    length += files[i].length();
                } else {
                    length += obtenerTamano(files[i]);
                }
            }
            return length;
        }else{
            return fdir.length();
        }

    }

    public static String unidadesLegibles(long bytes) {
        //Fuente: https://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = ("KMGTPE").charAt(exp-1) + ("");
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }


    public static String obtenerExtension(File fdir){
        String extension = "";

        int i = fdir.getName().lastIndexOf('.');
        int p = Math.max(fdir.getName().lastIndexOf('/'), fdir.getName().lastIndexOf('\\'));

        if (i > p) {
            extension = fdir.getName().substring(i+1);
        }

        return extension;
    }

    public static Comparator<File> comparadorTamanosASC = new Comparator<File>() {
        public int compare(File f1, File f2) {
            long tamF1 = obtenerTamano(f1);
            long tamF2 = obtenerTamano(f2);

            if(tamF1 == tamF2) return 0;
            else if(tamF1 < tamF2) return 1;
            else return -1;
        }
    };

    public static Comparator<File> comparadorTamanosDSC = new Comparator<File>() {
        public int compare(File f1, File f2) {
            long tamF1 = obtenerTamano(f1);
            long tamF2 = obtenerTamano(f2);

            if(tamF1 == tamF2) return 0;
            else if(tamF1 < tamF2) return -1;
            else return 1;
        }
    };

}

