package com.equipo3.hellocityreport.model

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

/**
 * Cuenta con Firebase Authentication (proveedor Google). Al iniciar sesión
 * también guarda el perfil en Firestore, en usuarios/{uid}, con los mismos
 * campos de la tabla usuario del diagrama ER.
 */
class CuentaFirebase : CuentaRepository {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    override val usuarioActual: UsuarioSesion?
        get() = auth.currentUser?.aUsuario()

    override suspend fun iniciarSesionConGoogle(idToken: String): UsuarioSesion {
        // El token de Google se convierte en una credencial que Firebase sabe validar.
        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        val resultado = auth.signInWithCredential(credencial).await()
        val usuario = checkNotNull(resultado.user).aUsuario()
        guardarPerfil(usuario)
        return usuario
    }

    override fun cerrarSesion() {
        auth.signOut()
    }

    // No se espera la respuesta: sin internet Firestore la guarda en su caché
    // y la envía al recuperar la conexión.
    private fun guardarPerfil(usuario: UsuarioSesion) {
        val perfil = mapOf(
            "nombre" to usuario.nombre,
            "correo" to usuario.correo,
            "rol" to "CIUDADANO",
            "ultimoAcceso" to FieldValue.serverTimestamp()
        )
        firestore.collection("usuarios").document(usuario.uid)
            .set(perfil, SetOptions.merge())
            .addOnSuccessListener { Log.d(TAG, "Perfil guardado en Firestore: ${usuario.uid}") }
            .addOnFailureListener { Log.w(TAG, "No se pudo guardar el perfil en Firestore", it) }
    }

    private fun FirebaseUser.aUsuario() = UsuarioSesion(
        uid = uid,
        nombre = displayName ?: "Sin nombre",
        correo = email ?: ""
    )

    private companion object {
        const val TAG = "CuentaFirebase"
    }
}
