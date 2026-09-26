package com.example;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

/**
 * Actividad Principal para el CRUD de Universidades en Android (Java + XML).
 * Desarrollada para el taller académico con SQLite.
 *
 * Incluye los módulos base (Guardar, Limpiar, Buscar) y los módulos nuevos
 * requeridos para el entregable final: (Modificar y Eliminar).
 */
public class ActividadCrudUniversidad extends AppCompatActivity {

    private static final String TAG = "ActividadCrudUni";

    // Componentes de la interfaz de usuario (XML)
    private EditText etId;
    private EditText etNombre;
    private EditText etWww;

    private Button btnGuardar;
    private Button btnLimpiar;
    private Button btnBuscar;
    private Button btnModificar;
    private Button btnEliminar;

    private TextView tvEstado;
    private TextView tvRegistrosTotales;

    // Instancia de la clase DAO para la comunicación con SQLite
    private DaoUniversidad daoUniversidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_actividad_crud_universidad);

        try {
            // 1. Enlazar componentes de la vista mediante findViewById
            inicializarVistas();

            // 2. Instanciar la clase DAO
            daoUniversidad = new DaoUniversidad(this);

            // 3. Configurar eventos de clic para los 5 botones de acción
            configurarEventosBotones();

            // 4. Actualizar información de estado inicial
            actualizarEstado("Listo para registrar una nueva universidad.");
            actualizarContadorRegistros();

        } catch (Exception e) {
            Log.e(TAG, "Error durante la inicialización de la actividad: " + e.getMessage(), e);
            Toast.makeText(this, "Error al inicializar la aplicación: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Enlaza las referencias de los componentes visuales declarados en el archivo XML.
     */
    private void inicializarVistas() {
        etId = findViewById(R.id.etId);
        etNombre = findViewById(R.id.etNombre);
        etWww = findViewById(R.id.etWww);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnModificar = findViewById(R.id.btnModificar);
        btnEliminar = findViewById(R.id.btnEliminar);

        tvEstado = findViewById(R.id.tvEstado);
        tvRegistrosTotales = findViewById(R.id.tvRegistrosTotales);
    }

    /**
     * Asigna los escuchadores (Listeners) onClick a los 5 botones requeridos.
     */
    private void configurarEventosBotones() {
        // 1. BOTÓN GUARDAR
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarUniversidad();
            }
        });

        // 2. BOTÓN LIMPIAR
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarCampos();
                Toast.makeText(ActividadCrudUniversidad.this, getString(R.string.msg_campos_limpios), Toast.LENGTH_SHORT).show();
            }
        });

        // 3. BOTÓN BUSCAR
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoBuscar();
            }
        });

        // 4. BOTÓN MODIFICAR (MÓDULO NUEVO DE ENTREGA)
        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarUniversidad();
            }
        });

        // 5. BOTÓN ELIMINAR (MÓDULO NUEVO DE ENTREGA)
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarYEliminarUniversidad();
            }
        });
    }

    // =========================================================================
    // 1. GUARDAR (MÓDULO BASE)
    // =========================================================================
    private void guardarUniversidad() {
        try {
            String nombre = etNombre.getText().toString().trim();
            String www = etWww.getText().toString().trim();

            // Validación de campos obligatorios
            if (nombre.isEmpty() || www.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_campo_vacio), Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear objeto entidad
            Universidad universidad = new Universidad(nombre, www);

            // Insertar mediante el DAO
            boolean insertado = daoUniversidad.agregarUniversidad(universidad);

            if (insertado) {
                etId.setText(universidad.getId());
                String mensaje = String.format(getString(R.string.msg_guardar_exito), universidad.getId());
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
                actualizarEstado("Universidad guardada (ID: " + universidad.getId() + ")");
                actualizarContadorRegistros();
            } else {
                Toast.makeText(this, getString(R.string.msg_guardar_error), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción al guardar universidad: " + e.getMessage(), e);
            Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // =========================================================================
    // 2. LIMPIAR (MÓDULO BASE)
    // =========================================================================
    private void limpiarCampos() {
        etId.setText("");
        etNombre.setText("");
        etWww.setText("");
        actualizarEstado("Formulario listo para nuevo registro.");
    }

    // =========================================================================
    // 3. BUSCAR (MÓDULO BASE CON ALERTDIALOG)
    // =========================================================================
    private void mostrarDialogoBuscar() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.dialog_buscar_titulo));
            builder.setMessage(getString(R.string.dialog_buscar_mensaje));

            // Campo de entrada numérico dentro del diálogo
            final EditText inputId = new EditText(this);
            inputId.setInputType(InputType.TYPE_CLASS_NUMBER);
            inputId.setHint("Ej: 1");
            inputId.setBackgroundResource(R.drawable.bg_edittext);

            // Contenedor con margen para el EditText
            LinearLayout container = new LinearLayout(this);
            container.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(48, 16, 48, 16);
            inputId.setLayoutParams(lp);
            container.addView(inputId);
            builder.setView(container);

            builder.setPositiveButton(getString(R.string.btn_buscar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String idIngresado = inputId.getText().toString().trim();
                    if (idIngresado.isEmpty()) {
                        Toast.makeText(ActividadCrudUniversidad.this, "Debe ingresar un ID para buscar", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ejecutarBusqueda(idIngresado);
                }
            });

            builder.setNegativeButton(getString(R.string.dialog_btn_cancelar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "Excepción al mostrar diálogo de búsqueda: " + e.getMessage(), e);
            Toast.makeText(this, "Error al abrir búsqueda: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void ejecutarBusqueda(String id) {
        try {
            Universidad u = daoUniversidad.consultarUnaUniversidad(id);

            if (u != null) {
                etId.setText(u.getId());
                etNombre.setText(u.getNombre());
                etWww.setText(u.getWww());

                Toast.makeText(this, getString(R.string.msg_buscar_exito), Toast.LENGTH_SHORT).show();
                actualizarEstado("Universidad cargada para edición (ID: " + u.getId() + ")");
            } else {
                Toast.makeText(this, getString(R.string.msg_buscar_no_encontrado), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción al consultar universidad: " + e.getMessage(), e);
            Toast.makeText(this, "Error en la consulta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // =========================================================================
    // 4. MODIFICAR (MÓDULO NUEVO ENTREGABLE)
    // =========================================================================
    private void modificarUniversidad() {
        try {
            String id = etId.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();
            String www = etWww.getText().toString().trim();

            // 1. Validar que exista un ID cargado
            if (id.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_modificar_sin_id), Toast.LENGTH_LONG).show();
                return;
            }

            // 2. Validar campos obligatorios
            if (nombre.isEmpty() || www.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_campo_vacio), Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Crear objeto con los datos a actualizar
            Universidad u = new Universidad(id, nombre, www);

            // 4. Ejecutar modificación en la BD mediante el DAO
            boolean modificado = daoUniversidad.editarUniversidad(u);

            if (modificado) {
                Toast.makeText(this, getString(R.string.msg_modificar_exito), Toast.LENGTH_SHORT).show();
                actualizarEstado("Universidad actualizada correctamente (ID: " + id + ")");
                actualizarContadorRegistros();
            } else {
                Toast.makeText(this, getString(R.string.msg_modificar_error), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción al modificar universidad: " + e.getMessage(), e);
            Toast.makeText(this, "Error al modificar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // =========================================================================
    // 5. ELIMINAR (MÓDULO NUEVO ENTREGABLE)
    // =========================================================================
    private void confirmarYEliminarUniversidad() {
        try {
            final String id = etId.getText().toString().trim();
            String nombre = etNombre.getText().toString().trim();

            // 1. Validar que haya un ID seleccionado
            if (id.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_eliminar_sin_id), Toast.LENGTH_LONG).show();
                return;
            }

            // 2. Diálogo de confirmación antes de eliminar
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.dialog_eliminar_titulo));
            String mensaje = getString(R.string.dialog_eliminar_mensaje) + "\n\nID: " + id + "\nNombre: " + nombre;
            builder.setMessage(mensaje);

            builder.setPositiveButton(getString(R.string.btn_eliminar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ejecutarEliminacion(id);
                }
            });

            builder.setNegativeButton(getString(R.string.dialog_btn_cancelar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "Excepción al preparar eliminación: " + e.getMessage(), e);
            Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void ejecutarEliminacion(String id) {
        try {
            boolean eliminado = daoUniversidad.borrarUniversidad(id);

            if (eliminado) {
                limpiarCampos();
                Toast.makeText(this, getString(R.string.msg_eliminar_exito), Toast.LENGTH_SHORT).show();
                actualizarEstado("Universidad (ID: " + id + ") eliminada con éxito.");
                actualizarContadorRegistros();
            } else {
                Toast.makeText(this, getString(R.string.msg_eliminar_error), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Excepción al eliminar universidad: " + e.getMessage(), e);
            Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE ESTADO Y FEEDBACK VISUAL
    // =========================================================================
    private void actualizarEstado(String mensaje) {
        if (tvEstado != null) {
            tvEstado.setText("💡 Estado: " + mensaje);
        }
    }

    private void actualizarContadorRegistros() {
        try {
            if (tvRegistrosTotales != null && daoUniversidad != null) {
                List<Universidad> lista = daoUniversidad.listarUniversidades();
                int total = (lista != null) ? lista.size() : 0;
                tvRegistrosTotales.setText("Universidades registradas en SQLite: " + total);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al contar registros: " + e.getMessage(), e);
        }
    }
}
