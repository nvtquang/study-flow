package com.example.studyflow.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.example.studyflow.ui.components.PrimaryButton
import com.example.studyflow.ui.components.SecondaryButton
import com.example.studyflow.ui.components.StudyCard
import com.example.studyflow.ui.theme.SkyBlue
import com.example.studyflow.viewmodel.AuthUiState

@Composable
fun RegisterScreen(
    authState: AuthUiState,
    onRegisterClick: (String, String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    onGoogleClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    AuthContainer {
        StudyFlowLogo()
        Text(
            text = "Tạo tài khoản StudyFlow",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Bắt đầu xây dựng kế hoạch học tập thông minh.",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        StudyCard {
            AuthTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Họ và tên"
            )
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                keyboardType = KeyboardType.Email
            )
            PasswordField(
                value = password,
                onValueChange = { password = it },
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible },
                label = "Mật khẩu"
            )
            PasswordField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                passwordVisible = confirmPasswordVisible,
                onTogglePassword = { confirmPasswordVisible = !confirmPasswordVisible },
                label = "Xác nhận mật khẩu"
            )
            AuthError(authState.errorMessage)
            PrimaryButton(
                text = if (authState.isLoading) "Đang đăng ký..." else "Đăng ký",
                onClick = { onRegisterClick(fullName, email, password, confirmPassword) }
            )
            SecondaryButton(text = "Tiếp tục với Google", onClick = onGoogleClick)
            AuthFooter(
                normalText = "Đã có tài khoản?",
                actionText = "Đăng nhập",
                onClick = onLoginClick
            )
        }

        if (authState.isLoading) {
            CircularProgressIndicator(color = SkyBlue)
        }
    }
}
