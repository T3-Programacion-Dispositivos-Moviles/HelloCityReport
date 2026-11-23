package com.equipo3.hellocityreport.view

import android.app.Activity
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.equipo3.hellocityreport.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

/** Lo que puede pasar al pedir la cuenta de Google. */
sealed interface ResultadoGoogle {
    data class Token(val idToken: String) : ResultadoGoogle
    data object Cancelado : ResultadoGoogle
    data object SinCuentas : ResultadoGoogle
    data object Error : ResultadoGoogle
}

/**
 * Sign in with Google con Credential Manager. Muestra la hoja del sistema con
 * las cuentas de Google del teléfono y regresa el ID token de la elegida.
 * Necesita una Activity porque la hoja se dibuja sobre ella.
 */
class CuentaGoogle(private val activity: Activity) {

    private val credentialManager = CredentialManager.create(activity)

    suspend fun pedirToken(): ResultadoGoogle {
        // Opción para el botón "Iniciar sesión con Google": deja elegir o agregar una cuenta.
        // El server client ID es el cliente Web de OAuth; lo genera el plugin de google-services.
        val opcion = GetSignInWithGoogleOption.Builder(
            serverClientId = activity.getString(R.string.default_web_client_id)
        ).build()
        val solicitud = GetCredentialRequest.Builder().addCredentialOption(opcion).build()

        return try {
            val credencial = credentialManager.getCredential(activity, solicitud).credential
            if (credencial is CustomCredential &&
                credencial.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                ResultadoGoogle.Token(GoogleIdTokenCredential.createFrom(credencial.data).idToken)
            } else {
                Log.w(TAG, "Tipo de credencial inesperado: ${credencial.type}")
                ResultadoGoogle.Error
            }
        } catch (e: GetCredentialCancellationException) {
            ResultadoGoogle.Cancelado
        } catch (e: NoCredentialException) {
            ResultadoGoogle.SinCuentas
        } catch (e: GetCredentialException) {
            Log.w(TAG, "Credential Manager falló", e)
            ResultadoGoogle.Error
        } catch (e: GoogleIdTokenParsingException) {
            Log.w(TAG, "No se pudo leer el token de Google", e)
            ResultadoGoogle.Error
        }
    }

    /** Al cerrar sesión se avisa a Credential Manager para que no entre solo la próxima vez. */
    suspend fun olvidar() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: ClearCredentialException) {
            Log.w(TAG, "No se pudo limpiar el estado de credenciales", e)
        }
    }

    private companion object {
        const val TAG = "CuentaGoogle"
    }
}
