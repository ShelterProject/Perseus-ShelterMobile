package sidev.app.shelter.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import sidev.app.shelter.domain.model.Gender

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

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "Buat Akun", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Nama lengkap") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp).padding(top = 24.dp),
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp).padding(top = 8.dp),
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Kata sandi") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp).padding(top = 8.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp).padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Gender.entries.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.selectable(
                            selected = gender == option,
                            onClick = { gender = option },
                        ),
                    ) {
                        RadioButton(selected = gender == option, onClick = { gender = option })
                        Text(if (option == Gender.MALE) "Laki-laki" else "Perempuan")
                    }
                }
            }

            uiState.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }

            Button(
                onClick = {
                    viewModel.signUp(email.trim(), password, fullName.trim(), gender, onRegisterSuccess)
                },
                enabled = !uiState.isSubmitting && email.isNotBlank() && password.isNotBlank() && fullName.isNotBlank(),
                modifier = Modifier.fillMaxWidth().widthIn(max = 480.dp).padding(top = 20.dp),
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.padding(2.dp))
                } else {
                    Text("Daftar")
                }
            }

            TextButton(onClick = onNavigateToLogin, modifier = Modifier.padding(top = 4.dp)) {
                Text("Sudah punya akun? Masuk")
            }
        }
    }
}
