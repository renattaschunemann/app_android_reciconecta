package br.com.fiap.reciconecta.ui.screens.perfil.criar

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.foundation.text.KeyboardActions
import kotlinx.coroutines.delay
import br.com.fiap.reciconecta.R
import br.com.fiap.reciconecta.ui.theme.ReciconectaTheme
import coil.compose.AsyncImage
import java.io.File
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import br.com.fiap.reciconecta.domain.model.UserProfile
import br.com.fiap.reciconecta.data.local.datastore.UserProfilePreferences
import br.com.fiap.reciconecta.data.repository.UserRepositoryImpl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarPerfilScreen(
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onCreateProfileClick: (nome: String, email: String, telefone: String, cpf: String, bairroCep: String) -> Unit = { _, _, _, _, _ -> }
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val preferences = remember { UserProfilePreferences(context) }
    val userRepository = remember { UserRepositoryImpl(preferences) }
    val savedProfile by userRepository.userProfile.collectAsState(initial = null)

    var nomeCompleto by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var bairroCep by remember { mutableStateOf("") }

    var nomeError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var telefoneError by remember { mutableStateOf<String?>(null) }
    var cpfError by remember { mutableStateOf<String?>(null) }
    var bairroCepError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    LaunchedEffect(savedProfile) {
        savedProfile?.let { profile ->
            nomeCompleto = profile.nome
            email = profile.email
            telefone = profile.telefone
            cpf = profile.cpf
            bairroCep = profile.cep
        }
    }

    // Estado unificado para a foto de perfil
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePickerDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // 💡 Criação do arquivo na pasta de cache do app
    fun createImageUri(): Uri {
        val file = File.createTempFile(
            "profile_photo_${System.currentTimeMillis()}",
            ".jpg",
            context.cacheDir
        )
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }

    // Launcher para Galeria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { profileImageUri = it }
    }

    // Launcher para Câmera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            profileImageUri = tempCameraUri
        }
    }

    // Launcher para pedir permissão em tempo de execução
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val uri = createImageUri()
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    // Modal para escolha da origem da imagem
    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = {
                Text(
                    text = "Foto de Perfil",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text("Escolha como deseja adicionar sua foto:")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImagePickerDialog = false
                        // Checa permissão antes de abrir a câmera
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            val uri = createImageUri()
                            tempCameraUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Câmera")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImagePickerDialog = false
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Photo, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Galeria")
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.create_profile_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.profile_option_individual),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.step_badge_text),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(
                    thickness = 4.dp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Container do Avatar com exibição direta pela Coil
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .clip(MaterialTheme.shapes.large)
                        .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { showImagePickerDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Foto selecionada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "👦",
                            fontSize = 48.sp
                        )
                    }
                }

                // Botão flutuante de câmera
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { showImagePickerDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Alterar avatar",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.avatar_change_instruction),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            FormLabel(text = stringResource(R.string.label_full_name))
            FormTextField(
                value = nomeCompleto,
                onValueChange = {
                    nomeCompleto = it
                    if (nomeError != null) nomeError = null
                },
                placeholder = stringResource(R.string.placeholder_full_name),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                modifier = Modifier.focusRequester(focusRequester),
                isError = nomeError != null,
                errorMessage = nomeError
            )

            Spacer(modifier = Modifier.height(16.dp))

            FormLabel(text = stringResource(R.string.label_email))
            FormTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError != null) emailError = null
                },
                placeholder = stringResource(R.string.placeholder_email),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                isError = emailError != null,
                errorMessage = emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            FormLabel(text = stringResource(R.string.label_phone))
            FormTextField(
                value = telefone,
                onValueChange = {
                    val filtered = it.filter { char -> char.isDigit() }
                    if (filtered.length <= 11) {
                        telefone = filtered
                        if (telefoneError != null) telefoneError = null
                    }
                },
                placeholder = "Ex.: 11999999999",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                isError = telefoneError != null,
                errorMessage = telefoneError
            )

            Spacer(modifier = Modifier.height(16.dp))

            FormLabel(text = stringResource(R.string.label_cpf))
            FormTextField(
                value = cpf,
                onValueChange = {
                    val filtered = it.filter { char -> char.isDigit() }
                    if (filtered.length <= 11) {
                        cpf = filtered
                        if (cpfError != null) cpfError = null
                    }
                },
                placeholder = "Ex.: 00000000000",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                isError = cpfError != null,
                errorMessage = cpfError
            )

            Spacer(modifier = Modifier.height(16.dp))

            FormLabel(text = "CEP *")
            FormTextField(
                value = bairroCep,
                onValueChange = {
                    val filtered = it.filter { char -> char.isDigit() }
                    if (filtered.length <= 8) {
                        bairroCep = filtered
                        if (bairroCepError != null) bairroCepError = null
                    }
                },
                placeholder = "Ex.: 04101000",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Text),
                isError = bairroCepError != null,
                errorMessage = bairroCepError
            )

            Spacer(modifier = Modifier.height(24.dp))

            val consentText = buildAnnotatedString {
                append(stringResource(R.string.create_profile_consent_part1))
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                    append(stringResource(R.string.terms_of_use))
                }
                append(stringResource(R.string.create_profile_consent_part2))
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                    append(stringResource(R.string.privacy_policy))
                }
                append(".")
            }

            Text(
                text = consentText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    var isValid = true

                    // Validação Nome Completo
                    if (nomeCompleto.trim().isEmpty()) {
                        nomeError = "Nome completo não pode ser vazio"
                        isValid = false
                    } else if (nomeCompleto.trim().split(" ").size < 2) {
                        nomeError = "Digite seu nome completo (Nome e Sobrenome)"
                        isValid = false
                    } else {
                        nomeError = null
                    }

                    // Validação E-mail
                    val emailTrimmed = email.trim()
                    if (emailTrimmed.isEmpty()) {
                        emailError = "E-mail não pode ser vazio"
                        isValid = false
                    } else if (!emailTrimmed.contains("@") || !emailTrimmed.contains(".com")) {
                        emailError = "E-mail inválido (deve conter @ e .com)"
                        isValid = false
                    } else {
                        emailError = null
                    }

                    // Validação Telefone
                    val phoneDigits = telefone.filter { it.isDigit() }
                    if (telefone.trim().isEmpty()) {
                        telefoneError = "Telefone não pode ser vazio"
                        isValid = false
                    } else if (phoneDigits.length < 10 || phoneDigits.length > 11) {
                        telefoneError = "Telefone inválido (deve conter o DDD e 10 ou 11 dígitos)"
                        isValid = false
                    } else {
                        telefoneError = null
                    }

                    // Validação CPF
                    val cpfDigits = cpf.filter { it.isDigit() }
                    if (cpf.trim().isEmpty()) {
                        cpfError = "CPF não pode ser vazio"
                        isValid = false
                    } else if (cpfDigits.length != 11) {
                        cpfError = "CPF inválido (deve conter exatamente 11 dígitos)"
                        isValid = false
                    } else {
                        cpfError = null
                    }

                    // Validação CEP
                    val cepDigits = bairroCep.filter { it.isDigit() }
                    if (bairroCep.trim().isEmpty()) {
                        bairroCepError = "CEP não pode ser vazio"
                        isValid = false
                    } else if (cepDigits.length != 8) {
                        bairroCepError = "CEP inválido (deve conter exatamente 8 números)"
                        isValid = false
                    } else {
                        bairroCepError = null
                    }

                    if (isValid) {
                        scope.launch {
                            userRepository.saveProfile(
                                UserProfile(
                                    nome = nomeCompleto,
                                    email = email,
                                    telefone = telefone,
                                    cpf = cpf,
                                    cep = bairroCep
                                )
                            )
                            onCreateProfileClick(nomeCompleto, email, telefone, cpf, bairroCep)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.create_profile_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color(0xFFEBF3EF))
                    .border(1.dp, Color(0xFF52B788), MaterialTheme.shapes.medium)
                    .clickable { onLoginClick() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.already_has_account_text),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D2D2D)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.login_here),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B4332)
                )
            }
        }
    }
}

@Composable
fun FormLabel(text: String) {
    val annotatedString = buildAnnotatedString {
        val parts = text.split("*")
        append(parts[0])
        if (text.contains("*")) {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.error)) {
                append("*")
            }
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    )
}

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            },
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            singleLine = true,
            isError = isError,
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Next)
                },
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CriarPerfilScreenPreview() {
    ReciconectaTheme {
        CriarPerfilScreen()
    }
}
