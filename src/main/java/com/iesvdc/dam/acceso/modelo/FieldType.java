package com.iesvdc.dam.acceso.modelo;

/**
 * Tipos de datos posibles para los campos
 */
public enum FieldType {
    INTEGER,
    FLOAT,
    VARCHAR,
    DATE,
    BOOLEAN,
    UNKNOWN;

    /**
     * Determina si el tipo representa un valor numérico.
     * @return true si es INTEGER o FLOAT.
     */
    public boolean isNumeric() {
        return this == INTEGER || this == FLOAT;
    }
}