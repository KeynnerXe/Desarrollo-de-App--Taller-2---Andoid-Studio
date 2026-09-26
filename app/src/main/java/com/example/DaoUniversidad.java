package com.example;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Universidad.
 * Encapsula todas las operaciones de acceso a datos SQLite
 * según los requerimientos del taller académico.
 */
public class DaoUniversidad {

    private static final String TAG = "DaoUniversidad";
    private final ConexionBasedatos conexion;
    private final Context context;

    /**
     * Constructor del DAO.
     *
     * @param context Contexto de la actividad o aplicación.
     */
    public DaoUniversidad(Context context) {
        this.context = context;
        this.conexion = new ConexionBasedatos(context);
    }

    /**
     * Agrega una nueva universidad a la base de datos.
     * Si la inserción es exitosa, asigna el ID generado al objeto recibido.
     *
     * @param u Objeto Universidad con los datos a registrar (nombre, www).
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public boolean agregarUniversidad(Universidad u) {
        if (u == null) {
            return false;
        }

        try {
            ContentValues valores = new ContentValues();
            valores.put(ConexionBasedatos.CAMPO_NOMBRE, u.getNombre());
            valores.put(ConexionBasedatos.CAMPO_WWW, u.getWww());

            long idGenerado = conexion.insertar(ConexionBasedatos.TABLA_UNIVERSIDADES, valores);

            if (idGenerado > 0) {
                u.setId(String.valueOf(idGenerado));
                Log.i(TAG, "Universidad agregada con éxito, ID asignado: " + idGenerado);
                return true;
            } else {
                Log.w(TAG, "No se pudo insertar la universidad.");
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción en agregarUniversidad: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Consulta una universidad a partir de su ID.
     *
     * @param id Identificador numérico como String de la universidad.
     * @return Objeto Universidad si se encuentra, o null si no existe.
     */
    public Universidad consultarUnaUniversidad(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        Cursor cursor = null;
        try {
            String sql = "SELECT " + ConexionBasedatos.CAMPO_ID + ", "
                    + ConexionBasedatos.CAMPO_NOMBRE + ", "
                    + ConexionBasedatos.CAMPO_WWW
                    + " FROM " + ConexionBasedatos.TABLA_UNIVERSIDADES
                    + " WHERE " + ConexionBasedatos.CAMPO_ID + " = ?";

            cursor = conexion.consultar(sql, new String[]{id.trim()});

            if (cursor != null && cursor.moveToFirst()) {
                String idEncontrado = cursor.getString(cursor.getColumnIndexOrThrow(ConexionBasedatos.CAMPO_ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ConexionBasedatos.CAMPO_NOMBRE));
                String www = cursor.getString(cursor.getColumnIndexOrThrow(ConexionBasedatos.CAMPO_WWW));

                Universidad universidad = new Universidad(idEncontrado, nombre, www);
                Log.i(TAG, "Universidad encontrada: " + universidad);
                return universidad;
            } else {
                Log.d(TAG, "No se encontró registro para el ID: " + id);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción en consultarUnaUniversidad: " + e.getMessage(), e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Calcula y retorna el próximo ID secuencial estimado que asignará la base de datos.
     *
     * @return El próximo ID disponible como String ("1" si la tabla está vacía).
     */
    public String proximoId() {
        Cursor cursor = null;
        try {
            String sql = "SELECT MAX(" + ConexionBasedatos.CAMPO_ID + ") FROM " + ConexionBasedatos.TABLA_UNIVERSIDADES;
            cursor = conexion.consultar(sql, null);

            if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) {
                int maxId = cursor.getInt(0);
                return String.valueOf(maxId + 1);
            } else {
                return "1";
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción en proximoId: " + e.getMessage(), e);
            return "1";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =========================================================================
    // MÓDULOS REQUERIDOS PARA EL ENTREGABLE FINAL (MODIFICAR Y ELIMINAR)
    // =========================================================================

    /**
     * MODIFICAR / ACTUALIZAR: Modifica los datos de una universidad existente en la BD.
     *
     * @param u Objeto Universidad con el ID existente y los nuevos valores a actualizar.
     * @return true si se actualizó al menos una fila, false si falló o no existe el ID.
     */
    public boolean editarUniversidad(Universidad u) {
        if (u == null || u.getId() == null || u.getId().trim().isEmpty()) {
            Log.w(TAG, "No se puede editar: objeto o ID nulo/vacío.");
            return false;
        }

        try {
            ContentValues valores = new ContentValues();
            valores.put(ConexionBasedatos.CAMPO_NOMBRE, u.getNombre());
            valores.put(ConexionBasedatos.CAMPO_WWW, u.getWww());

            String clausulaWhere = ConexionBasedatos.CAMPO_ID + " = ?";
            String[] argsWhere = new String[]{u.getId().trim()};

            int filasAfectadas = conexion.actualizar(ConexionBasedatos.TABLA_UNIVERSIDADES, valores, clausulaWhere, argsWhere);
            Log.i(TAG, "Filas modificadas: " + filasAfectadas + " para el ID: " + u.getId());

            return filasAfectadas > 0;
        } catch (Exception e) {
            Log.e(TAG, "Excepción en editarUniversidad: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * ELIMINAR / BORRAR: Elimina el registro de una universidad por su ID.
     *
     * @param id Identificador numérico de la universidad a borrar.
     * @return true si la eliminación afectó al menos un registro, false en caso contrario.
     */
    public boolean borrarUniversidad(String id) {
        if (id == null || id.trim().isEmpty()) {
            Log.w(TAG, "No se puede borrar: ID nulo o vacío.");
            return false;
        }

        try {
            String clausulaWhere = ConexionBasedatos.CAMPO_ID + " = ?";
            String[] argsWhere = new String[]{id.trim()};

            int filasEliminadas = conexion.eliminar(ConexionBasedatos.TABLA_UNIVERSIDADES, clausulaWhere, argsWhere);
            Log.i(TAG, "Filas eliminadas: " + filasEliminadas + " para el ID: " + id);

            return filasEliminadas > 0;
        } catch (Exception e) {
            Log.e(TAG, "Excepción en borrarUniversidad: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Método complementario para obtener todas las universidades registradas.
     * Útil para visualización o auditoría.
     *
     * @return Lista de objetos Universidad.
     */
    public List<Universidad> listarUniversidades() {
        List<Universidad> lista = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "SELECT " + ConexionBasedatos.CAMPO_ID + ", "
                    + ConexionBasedatos.CAMPO_NOMBRE + ", "
                    + ConexionBasedatos.CAMPO_WWW
                    + " FROM " + ConexionBasedatos.TABLA_UNIVERSIDADES
                    + " ORDER BY " + ConexionBasedatos.CAMPO_ID + " DESC";

            cursor = conexion.consultar(sql, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(ConexionBasedatos.CAMPO_ID));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow(ConexionBasedatos.CAMPO_NOMBRE));
                    String www = cursor.getString(cursor.getColumnIndexOrThrow(ConexionBasedatos.CAMPO_WWW));
                    lista.add(new Universidad(id, nombre, www));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción en listarUniversidades: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return lista;
    }
}
