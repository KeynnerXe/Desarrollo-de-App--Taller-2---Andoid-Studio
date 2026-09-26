package com.example;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Gestor de la conexión a la base de datos local SQLite.
 * Extiende de SQLiteOpenHelper y provee métodos genéricos CRUD.
 */
public class ConexionBasedatos extends SQLiteOpenHelper {

    private static final String TAG = "ConexionBasedatos";

    // Constantes de configuración de la base de datos
    public static final String DATABASE_NAME = "universidades.db";
    public static final int DATABASE_VERSION = 1;

    // Constantes de la tabla y campos
    public static final String TABLA_UNIVERSIDADES = "Universidades";
    public static final String CAMPO_ID = "id";
    public static final String CAMPO_NOMBRE = "nombre";
    public static final String CAMPO_WWW = "www";

    // Sentencia SQL para la creación de la tabla Universidades
    private static final String SQL_CREAR_TABLA_UNIVERSIDADES =
            "CREATE TABLE " + TABLA_UNIVERSIDADES + " (" +
                    CAMPO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CAMPO_NOMBRE + " VARCHAR(100) NOT NULL, " +
                    CAMPO_WWW + " VARCHAR(100) NOT NULL" +
                    ");";

    /**
     * Constructor estándar de la conexión.
     *
     * @param context Contexto de la aplicación.
     */
    public ConexionBasedatos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Constructor con parámetros adicionales solicitado en arquitecturas académicas.
     *
     * @param context Contexto de la aplicación.
     * @param name    Nombre de la base de datos.
     * @param factory CursorFactory opcional.
     * @param version Versión de la base de datos.
     */
    public ConexionBasedatos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREAR_TABLA_UNIVERSIDADES);
            Log.i(TAG, "Tabla '" + TABLA_UNIVERSIDADES + "' creada exitosamente.");
        } catch (SQLException e) {
            Log.e(TAG, "Error al crear la tabla '" + TABLA_UNIVERSIDADES + "': " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLA_UNIVERSIDADES);
            onCreate(db);
            Log.i(TAG, "Base de datos actualizada de versión " + oldVersion + " a " + newVersion);
        } catch (SQLException e) {
            Log.e(TAG, "Error en onUpgrade: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // MÉTODOS GENÉRICOS DE ACCESO Y OPERACIÓN A LA BASE DE DATOS
    // =========================================================================

    /**
     * Abre y retorna una instancia editable (escritura/lectura) de la base de datos.
     *
     * @return SQLiteDatabase lista para operar.
     * @throws SQLException Si ocurre algún error al abrir la base de datos.
     */
    public SQLiteDatabase conectar() throws SQLException {
        try {
            return this.getWritableDatabase();
        } catch (SQLException e) {
            Log.e(TAG, "Error al conectar con la base de datos: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Cierra de forma segura una conexión abierta a la base de datos.
     *
     * @param db Instancia de SQLiteDatabase a cerrar.
     */
    public void desconectar(SQLiteDatabase db) {
        try {
            if (db != null && db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al cerrar la base de datos: " + e.getMessage(), e);
        }
    }

    /**
     * Inserta un registro genérico utilizando insertOrThrow.
     *
     * @param tabla   Nombre de la tabla donde insertar.
     * @param valores Contenedor con los pares clave-valor a registrar.
     * @return El ID de la fila insertada (long) o -1 en caso de error.
     * @throws SQLException Si ocurre un error durante la inserción.
     */
    public long insertar(String tabla, ContentValues valores) throws SQLException {
        SQLiteDatabase db = null;
        try {
            db = conectar();
            return db.insertOrThrow(tabla, null, valores);
        } catch (SQLException e) {
            Log.e(TAG, "Error al insertar en " + tabla + ": " + e.getMessage(), e);
            throw e;
        } finally {
            desconectar(db);
        }
    }

    /**
     * Actualiza registros de forma genérica utilizando update.
     *
     * @param tabla         Nombre de la tabla a actualizar.
     * @param valores       Nuevos valores para los campos.
     * @param clausulaWhere Condición WHERE (ej: "id = ?").
     * @param argsWhere     Argumentos de reemplazo para la condición WHERE.
     * @return Cantidad de filas afectadas por la actualización.
     */
    public int actualizar(String tabla, ContentValues valores, String clausulaWhere, String[] argsWhere) {
        SQLiteDatabase db = null;
        try {
            db = conectar();
            return db.update(tabla, valores, clausulaWhere, argsWhere);
        } catch (SQLException e) {
            Log.e(TAG, "Error al actualizar en " + tabla + ": " + e.getMessage(), e);
            return 0;
        } finally {
            desconectar(db);
        }
    }

    /**
     * Elimina registros de forma genérica utilizando delete.
     *
     * @param tabla         Nombre de la tabla de donde eliminar.
     * @param clausulaWhere Condición WHERE (ej: "id = ?").
     * @param argsWhere     Argumentos de reemplazo para la condición WHERE.
     * @return Cantidad de filas eliminadas.
     */
    public int eliminar(String tabla, String clausulaWhere, String[] argsWhere) {
        SQLiteDatabase db = null;
        try {
            db = conectar();
            return db.delete(tabla, clausulaWhere, argsWhere);
        } catch (SQLException e) {
            Log.e(TAG, "Error al eliminar en " + tabla + ": " + e.getMessage(), e);
            return 0;
        } finally {
            desconectar(db);
        }
    }

    /**
     * Realiza una consulta SQL genérica utilizando rawQuery.
     * NOTA: El llamador es responsable de cerrar el Cursor y la BD asociada tras leerlo.
     *
     * @param sql  Sentencia SQL a ejecutar (SELECT ...).
     * @param args Argumentos de sustitución para la consulta.
     * @return Cursor con los resultados obtenidos.
     */
    public Cursor consultar(String sql, String[] args) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.rawQuery(sql, args);
        } catch (SQLException e) {
            Log.e(TAG, "Error al consultar SQL [" + sql + "]: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Ejecuta una instrucción SQL que no retorna datos (ej: CREATE, DROP, ALTER) mediante execSQL.
     *
     * @param sql Sentencia SQL a ejecutar.
     * @throws SQLException Si la sentencia no es válida.
     */
    public void ejecutarSQL(String sql) throws SQLException {
        SQLiteDatabase db = null;
        try {
            db = conectar();
            db.execSQL(sql);
        } catch (SQLException e) {
            Log.e(TAG, "Error al ejecutar SQL directo: " + e.getMessage(), e);
            throw e;
        } finally {
            desconectar(db);
        }
    }
}
