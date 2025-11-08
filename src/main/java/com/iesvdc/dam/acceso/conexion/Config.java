package com.iesvdc.dam.acceso.conexion;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {

    /**
     * Método para cargar las propiedades de un fichero.
     * Ejemplo de archivo de propiedades:
     * <ul>
     *     <li>host=localhost</li>
     *     <li>port=33307</li>
     *     <li>database=agenda</li>
     *     <li>user=root</li>
     *     <li>password=s83n38DGB8d72</li>
     *     <li>driver=MySQL</li>
     *     <li>useUnicode=yes</li>
     *     <li>useJDBCCompliantTimezoneShift=true</li>
     *     <li>useSSL=false</li>
     *     <li>serverTimezone=Europe/Madrid</li>
     *     <li>allowPublicKeyRetrieval=true</li>
     *     <li>inputFile=datos/entrada.xlsx</li>
     *     <li>outputFile=datos/salida.xlsx</li>
     * </ul>
     * @param nombreArchivo el nombre del archivo que contiene la configuración.
     * @return Un objeto del tipo {@link java.util.Properties} con todas las propiedades cargadas.
     */
    static public Properties getProperties(String nombreArchivo) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(nombreArchivo)) {
            props.load(fis);            
        } catch (FileNotFoundException fnfe) {
            System.out.println(
                "No encuentro el fichero de propiedades: " +
                fnfe.getLocalizedMessage());
        } catch (IOException ioe){
            System.out.println(
                "Error al leer el fichero de propiedades: " +
                ioe.getLocalizedMessage());
        }
        
        return props;
    }
}
