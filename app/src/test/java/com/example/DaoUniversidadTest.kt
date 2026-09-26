package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DaoUniversidadTest {

  private lateinit var dao: DaoUniversidad

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    // Limpiar BD previa si existe
    context.deleteDatabase(ConexionBasedatos.DATABASE_NAME)
    dao = DaoUniversidad(context)
  }

  @Test
  fun testAgregarYConsultarUniversidad() {
    val uni = Universidad("Universidad Nacional", "https://www.unal.edu.co")
    val insertado = dao.agregarUniversidad(uni)

    assertTrue("La inserción debe ser exitosa", insertado)
    assertNotNull("El ID generado no debe ser nulo", uni.id)

    val encontrada = dao.consultarUnaUniversidad(uni.id)
    assertNotNull("Debe encontrar la universidad", encontrada)
    assertEquals("Universidad Nacional", encontrada.nombre)
    assertEquals("https://www.unal.edu.co", encontrada.www)
  }

  @Test
  fun testEditarUniversidad() {
    val uni = Universidad("Universidad de Antioquia", "https://www.udea.edu.co")
    dao.agregarUniversidad(uni)
    val id = uni.id

    // Modificar datos
    uni.nombre = "UdeA Actualizada"
    uni.www = "https://actualizada.udea.edu.co"
    val modificado = dao.editarUniversidad(uni)

    assertTrue("La edición debe ser exitosa", modificado)

    val verificada = dao.consultarUnaUniversidad(id)
    assertNotNull(verificada)
    assertEquals("UdeA Actualizada", verificada.nombre)
    assertEquals("https://actualizada.udea.edu.co", verificada.www)
  }

  @Test
  fun testBorrarUniversidad() {
    val uni = Universidad("Universidad del Valle", "https://www.univalle.edu.co")
    dao.agregarUniversidad(uni)
    val id = uni.id

    val borrado = dao.borrarUniversidad(id)
    assertTrue("El borrado debe retornar true", borrado)

    val consultada = dao.consultarUnaUniversidad(id)
    assertNull("Ya no debe existir tras ser borrada", consultada)
  }

  @Test
  fun testProximoId() {
    val proximoInicial = dao.proximoId()
    assertEquals("1", proximoInicial)

    val uni = Universidad("Universidad Javeriana", "https://www.javeriana.edu.co")
    dao.agregarUniversidad(uni)

    val proximoSiguiente = dao.proximoId()
    assertEquals("2", proximoSiguiente)
  }
}
