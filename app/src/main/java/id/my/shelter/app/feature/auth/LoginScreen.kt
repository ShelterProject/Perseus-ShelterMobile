package id.my.shelter.app.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            AuthHeader(title = "Selamat Datang", subtitle = "Masuk untuk melihat prediksi bencana & cuaca")

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 28.dp, bottom = 24.dp),
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp),
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Kata sandi") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp).padding(top = 12.dp),
                )

                uiState.errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }

                Button(
                    onClick = { viewModel.signIn(email.trim(), password, onLoginSuccess) },
                    enabled = !uiState.isSubmitting && email.isNotBlank() && password.isNotBlank(),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp).padding(top = 24.dp),
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.padding(2.dp))
                    } else {
                        Text(
                            text = "Masuk",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp).padding(vertical = 20.dp),
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text("atau lanjutkan dengan", modifier = Modifier.padding(horizontal = 12.dp), style = MaterialTheme.typography.labelLarge)
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                GoogleSignInButton(
                    text = "Masuk dengan Google",
                    enabled = !uiState.isSubmitting,
                    modifier = Modifier.widthIn(max = 480.dp),
                    onClick = {
                        scope.launch {
                            try {
                                val idToken = requestGoogleIdToken(context)
                                viewModel.signInWithGoogle(idToken, onLoginSuccess)
                            } catch (e: GetCredentialCancellationException) {
                                // user closed the account picker; nothing to report
                            } catch (e: GetCredentialException) {
                                viewModel.reportError(e.message ?: "Gagal masuk dengan Google, coba lagi.")
                            }
                        }
                    },
                )

                TextButton(onClick = onNavigateToRegister, modifier = Modifier.padding(top = 16.dp)) {
                    Text("Belum punya akun? Daftar")
                }
            }
        }
    }
}
