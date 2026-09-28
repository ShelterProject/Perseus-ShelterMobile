package id.my.shelter.app.feature.auth

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import id.my.shelter.app.domain.model.Gender
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(Gender.MALE) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            AuthHeader(title = "Buat Akun", subtitle = "Langkah pertama menuju Shelter")

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 28.dp, bottom = 24.dp),
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nama lengkap") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp),
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp).padding(top = 12.dp),
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

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp).padding(top = 16.dp),
                ) {
                    Gender.entries.forEachIndexed { index, option ->
                        SegmentedButton(
                            selected = gender == option,
                            onClick = { gender = option },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = Gender.entries.size),
                        ) {
                            Text(if (option == Gender.MALE) "Laki-laki" else "Perempuan")
                        }
                    }
                }

                uiState.errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 12.dp),
                    )
                }

                Button(
                    onClick = {
                        viewModel.signUp(email.trim(), password, fullName.trim(), gender, onRegisterSuccess)
                    },
                    enabled = !uiState.isSubmitting && email.isNotBlank() && password.isNotBlank() && fullName.isNotBlank(),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp).padding(top = 24.dp),
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.padding(2.dp))
                    } else {
                        Text(
                            text = "Daftar",
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
                    text = "Daftar dengan Google",
                    enabled = !uiState.isSubmitting,
                    modifier = Modifier.widthIn(max = 480.dp),
                    onClick = {
                        scope.launch {
                            try {
                                val idToken = requestGoogleIdToken(context)
                                viewModel.signInWithGoogle(idToken, onRegisterSuccess)
                            } catch (e: GetCredentialCancellationException) {
                                // user closed the account picker; nothing to report
                            } catch (e: GetCredentialException) {
                                viewModel.reportError(e.message ?: "Gagal daftar dengan Google, coba lagi.")
                            }
                        }
                    },
                )

                TextButton(onClick = onNavigateToLogin, modifier = Modifier.padding(top = 16.dp)) {
                    Text("Sudah punya akun? Masuk")
                }
            }
        }
    }
}
