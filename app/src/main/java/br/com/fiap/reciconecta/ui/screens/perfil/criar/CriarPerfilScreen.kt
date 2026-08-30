package br.com.fiap.reciconecta.ui.screens.perfil.criar

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import br.com.fiap.reciconecta.R
import br.com.fiap.reciconecta.data.local.datastore.UserProfilePreferences
import br.com.fiap.reciconecta.data.repository.UserRepositoryImpl
import br.com.fiap.reciconecta.domain.model.UserProfile
import br.com.fiap.reciconecta.ui.components.ProfileBottomActionButtons
import br.com.fiap.reciconecta.ui.theme.ReciconectaTheme
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarPerfilScreen(
    isEditMode: Boolean = false,
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onSaveProfileClick: (nome: String, email: String, telefone: String, cpf: String, bairroCep: String) -> Unit = { _, _, _, _, _ -> }
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

    // Senhas
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

    // Materiais
    val materiaisDisponiveis = listOf("Plástico (PET)", "Papel / Papelão", "Metal / Alumínio", "Vidro", "Eletrônicos")
    val materiaisSelecionados = remember { mutableStateListOf<String>() }
    var outrosMateriais by remember { mutableStateOf("") }
    var showOutrosField by remember { mutableStateOf(false) }

    var nomeError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var telefoneError by remember { mutableStateOf<String?>(null) }
    var cpfError by remember { mutableStateOf<String?>(null) }
    var bairroCepError by remember { mutableStateOf<String?>(null) }
    var materiaisError by remember { mutableStateOf<String?>(null) }
    var senhaError by remember { mutableStateOf<String?>(null) }
    var confirmarSenhaError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    LaunchedEffect(savedProfile) {
        savedProfile?.let { profile ->
            if (profile.tipoPerfil == "PF") {
                nomeCompleto = profile.nome
                email = profile.email
                telefone = profile.telefone
                cpf = profile.cpfOrCnpj
                bairroCep = profile.cep
                materiaisSelecionados.clear()
                profile.materiais.forEach { mat ->
                    if (mat in materiaisDisponiveis) {
                        materiaisSelecionados.add(mat)
                    } else {
                        showOutrosField = true
                        outrosMateriais = mat
                    }
                }
            }
        }
    }

    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePickerDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    fun createImageUri(): Uri {
        val file = File.createTempFile(
            "profile_photo_${System.currentTimeMillis()}",
            ".jpg",
            context.cacheDir
        )
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { profileImageUri = it } }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            profileImageUri = tempCameraUri
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri()
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text(stringResource(R.string.create_profile_title), fontWeight = FontWeight.Bold) },
            text = { Text("Escolha como deseja adicionar sua foto:") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImagePickerDialog = false
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
                    .statusBarsPadding()
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

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEditMode) "Editar Perfil" else stringResource(R.string.create_profile_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pessoa Física",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!isEditMode) {
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
                        Text(text = "👤", fontSize = 48.sp)
                    }
                }
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email),
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
                placeholder = stringResource(R.string.placeholder_phone),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Phone),
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
                placeholder = stringResource(R.string.placeholder_cpf),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
                isError = cpfError != null,
                errorMessage = cpfError
            )

            Spacer(modifier = Modifier.height(16.dp))

            FormLabel(text = stringResource(R.string.label_neighborhood_cep))
            FormTextField(
                value = bairroCep,
                onValueChange = {
                    val filtered = it.filter { char -> char.isDigit() }
                    if (filtered.length <= 8) {
                        bairroCep = filtered
                        if (bairroCepError != null) bairroCepError = null
                    }
                },
                placeholder = stringResource(R.string.placeholder_neighborhood_cep),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
                isError = bairroCepError != null,
                errorMessage = bairroCepError
            )

            Spacer(modifier = Modifier.height(24.dp))

            FormLabel(text = "MATERIAIS QUE VOCÊ RECICLA *")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp)
            ) {
                materiaisDisponiveis.forEach { material ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (material in materiaisSelecionados) {
                                    materiaisSelecionados.remove(material)
                                } else {
                                    materiaisSelecionados.add(material)
                                }
                                if (materiaisError != null) materiaisError = null
                            }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = material in materiaisSelecionados,
                            onCheckedChange = {
                                if (material in materiaisSelecionados) {
                                    materiaisSelecionados.remove(material)
                                } else {
                                    materiaisSelecionados.add(material)
                                }
                                if (materiaisError != null) materiaisError = null
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = material, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showOutrosField = !showOutrosField
                        }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showOutrosField,
                        onCheckedChange = { showOutrosField = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Outros", style = MaterialTheme.typography.bodyMedium)
                }

                if (showOutrosField) {
                    OutlinedTextField(
                        value = outrosMateriais,
                        onValueChange = {
                            outrosMateriais = it
                            if (materiaisError != null) materiaisError = null
                        },
                        placeholder = { Text("Digite os materiais separados por vírgula") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
            if (materiaisError != null) {
                Text(
                    text = materiaisError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

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

            // Senhas
            if (!isEditMode) {
                FormLabel(text = "SENHA *")
                FormPasswordField(
                    value = senha,
                    onValueChange = {
                        senha = it
                        if (senhaError != null) senhaError = null
                    },
                    placeholder = "Mínimo 6 caracteres",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password),
                    isError = senhaError != null,
                    errorMessage = senhaError
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormLabel(text = "CONFIRMAR SENHA *")
                FormPasswordField(
                    value = confirmarSenha,
                    onValueChange = {
                        confirmarSenha = it
                        if (confirmarSenhaError != null) confirmarSenhaError = null
                    },
                    placeholder = "Repita sua senha",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                    isError = confirmarSenhaError != null,
                    errorMessage = confirmarSenhaError
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            ProfileBottomActionButtons(
                primaryButtonText = if (isEditMode) "Salvar Alterações" else stringResource(R.string.create_profile_title),
                isEditMode = isEditMode,
                onPrimaryClick = {
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

                    // Validação Materiais
                    val listaMateriaisFinal = materiaisSelecionados.toMutableList()
                    if (showOutrosField && outrosMateriais.trim().isNotEmpty()) {
                        listaMateriaisFinal.addAll(outrosMateriais.split(",").map { it.trim() })
                    }

                    if (listaMateriaisFinal.isEmpty()) {
                        materiaisError = "Selecione pelo menos um tipo de material"
                        isValid = false
                    }

                    if (!isEditMode) {
                        if (senha.length < 6) {
                            senhaError = "A senha deve ter no mínimo 6 caracteres"
                            isValid = false
                        }
                        if (senha != confirmarSenha) {
                            confirmarSenhaError = "As senhas não coincidem"
                            isValid = false
                        }
                    }

                    if (isValid) {
                        scope.launch {
                            userRepository.saveProfile(
                                UserProfile(
                                    nome = nomeCompleto,
                                    email = email,
                                    telefone = telefone,
                                    cpfOrCnpj = cpf,
                                    cep = bairroCep,
                                    tipoPerfil = "PF",
                                    materiais = listaMateriaisFinal,
                                    senha = if (isEditMode) (savedProfile?.senha ?: "") else senha
                                )
                            )
                            onSaveProfileClick(nomeCompleto, email, telefone, cpf, bairroCep)
                        }
                    }
                },
                onLoginClick = onLoginClick
            )
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

@Composable
fun FormPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
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
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Mostrar senha")
                }
            },
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
