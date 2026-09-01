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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.compose.ui.res.stringResource
import br.com.fiap.reciconecta.R
import br.com.fiap.reciconecta.data.local.datastore.UserProfilePreferences
import br.com.fiap.reciconecta.data.repository.UserRepositoryImpl
import br.com.fiap.reciconecta.domain.model.UserProfile
import br.com.fiap.reciconecta.ui.components.ProfileBottomActionButtons
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarPerfilColetorScreen(
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

    val materiaisOpcoes = remember {
        listOf(
            "Plástico (PET)" to R.string.material_plastic,
            "Papel / Papelão" to R.string.material_paper,
            "Metal / Alumínio" to R.string.material_metal,
            "Vidro" to R.string.material_glass,
            "Eletrônicos" to R.string.material_electronics
        )
    }
    val materiaisDisponiveis = remember { materiaisOpcoes.map { it.first } }
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

    LaunchedEffect(Unit) { delay(100); focusRequester.requestFocus(); keyboardController?.show() }

    LaunchedEffect(savedProfile) {
        savedProfile?.let { profile ->
            if (profile.tipoPerfil == "COLETOR") {
                nomeCompleto = profile.nome; email = profile.email; telefone = profile.telefone; cpf = profile.cpfOrCnpj; bairroCep = profile.cep
                materiaisSelecionados.clear()
                profile.materiais.forEach { mat -> if (mat in materiaisDisponiveis) materiaisSelecionados.add(mat) else { showOutrosField = true; outrosMateriais = mat } }
            }
        }
    }

    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    fun createImageUri(): Uri {
        val file = File.createTempFile("profile_coletor_photo_${System.currentTimeMillis()}", ".jpg", context.cacheDir)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> uri?.let { profileImageUri = it } }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success -> if (success && tempCameraUri != null) profileImageUri = tempCameraUri }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted -> if (isGranted) { val uri = createImageUri(); tempCameraUri = uri; cameraLauncher.launch(uri) } }

    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text(stringResource(R.string.create_profile_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.choose_photo_source)) },
            confirmButton = {
                TextButton(onClick = { showImagePickerDialog = false; if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) { val uri = createImageUri(); tempCameraUri = uri; cameraLauncher.launch(uri) } else { permissionLauncher.launch(Manifest.permission.CAMERA) } }) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(6.dp)); Text(stringResource(R.string.camera)) }
                }
            },
            dismissButton = {
                TextButton(onClick = { showImagePickerDialog = false; galleryLauncher.launch("image/*") }) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Photo, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(6.dp)); Text(stringResource(R.string.gallery)) }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().statusBarsPadding().background(MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(CircleShape).border(1.dp, MaterialTheme.colorScheme.outline, CircleShape).clickable { onBackClick() }, contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back), tint = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = if (isEditMode) stringResource(R.string.action_edit_profile) else stringResource(R.string.create_profile_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = stringResource(R.string.profile_option_collector_desc), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (!isEditMode) {
                        Box(modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 12.dp, vertical = 6.dp)) {
                            Text(text = stringResource(R.string.step_badge_text), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                HorizontalDivider(thickness = 4.dp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth())
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).background(MaterialTheme.colorScheme.background).verticalScroll(scrollState).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            Box(modifier = Modifier.size(110.dp), contentAlignment = Alignment.BottomEnd) {
                Box(modifier = Modifier.size(100.dp).align(Alignment.Center).clip(MaterialTheme.shapes.large).border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.large).background(MaterialTheme.colorScheme.surfaceVariant).clickable { showImagePickerDialog = true }, contentAlignment = Alignment.Center) {
                    if (profileImageUri != null) { AsyncImage(model = profileImageUri, contentDescription = stringResource(R.string.selected_photo), modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) } else { Text(text = "👷", fontSize = 48.sp) }
                }
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary).clickable { showImagePickerDialog = true }, contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = stringResource(R.string.change_avatar), tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(R.string.avatar_change_instruction), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(24.dp))

            FormLabel(text = stringResource(R.string.label_full_name))
            FormTextField(value = nomeCompleto, onValueChange = { nomeCompleto = it; if (nomeError != null) nomeError = null }, placeholder = stringResource(R.string.placeholder_full_name), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text), modifier = Modifier.focusRequester(focusRequester), isError = nomeError != null, errorMessage = nomeError)

            Spacer(modifier = Modifier.height(16.dp))
            FormLabel(text = stringResource(R.string.label_email))
            FormTextField(value = email, onValueChange = { email = it; if (emailError != null) emailError = null }, placeholder = stringResource(R.string.placeholder_email), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Email), isError = emailError != null, errorMessage = emailError)

            Spacer(modifier = Modifier.height(16.dp))
            FormLabel(text = stringResource(R.string.label_phone))
            FormTextField(value = telefone, onValueChange = { val filtered = it.filter { char -> char.isDigit() }; if (filtered.length <= 11) { telefone = filtered; if (telefoneError != null) telefoneError = null } }, placeholder = stringResource(R.string.placeholder_phone), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Phone), isError = telefoneError != null, errorMessage = telefoneError)

            Spacer(modifier = Modifier.height(16.dp))
            FormLabel(text = stringResource(R.string.label_cpf))
            FormTextField(value = cpf, onValueChange = { val filtered = it.filter { char -> char.isDigit() }; if (filtered.length <= 11) { cpf = filtered; if (cpfError != null) cpfError = null } }, placeholder = stringResource(R.string.placeholder_cpf), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number), isError = cpfError != null, errorMessage = cpfError)

            Spacer(modifier = Modifier.height(16.dp))
            FormLabel(text = stringResource(R.string.label_neighborhood_cep))
            FormTextField(value = bairroCep, onValueChange = { val filtered = it.filter { char -> char.isDigit() }; if (filtered.length <= 8) { bairroCep = filtered; if (bairroCepError != null) bairroCepError = null } }, placeholder = stringResource(R.string.placeholder_neighborhood_cep), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number), isError = bairroCepError != null, errorMessage = bairroCepError)

            Spacer(modifier = Modifier.height(24.dp))
            FormLabel(text = stringResource(R.string.label_materials_collected))
            Column(modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.surface).padding(8.dp)) {
                materiaisOpcoes.forEach { (materialKey, resId) ->
                    Row(modifier = Modifier.fillMaxWidth().clickable { if (materialKey in materiaisSelecionados) materiaisSelecionados.remove(materialKey) else materiaisSelecionados.add(materialKey); if (materiaisError != null) materiaisError = null }.padding(vertical = 8.dp, horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = materialKey in materiaisSelecionados, onCheckedChange = { if (materialKey in materiaisSelecionados) materiaisSelecionados.remove(materialKey) else materiaisSelecionados.add(materialKey); if (materiaisError != null) materiaisError = null })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(resId), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().clickable { showOutrosField = !showOutrosField }.padding(vertical = 8.dp, horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = showOutrosField, onCheckedChange = { showOutrosField = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.others), style = MaterialTheme.typography.bodyMedium)
                }
                if (showOutrosField) {
                    OutlinedTextField(value = outrosMateriais, onValueChange = { outrosMateriais = it; if (materiaisError != null) materiaisError = null }, placeholder = { Text(stringResource(R.string.others_materials_placeholder)) }, modifier = Modifier.fillMaxWidth().padding(8.dp))
                }
            }
            if (materiaisError != null) { Text(text = materiaisError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp, start = 4.dp)) }

            Spacer(modifier = Modifier.height(24.dp))

            // Senhas
            if (!isEditMode) {
                FormLabel(text = stringResource(R.string.label_password))
                FormPasswordField(value = senha, onValueChange = { senha = it; if (senhaError != null) senhaError = null }, placeholder = stringResource(R.string.placeholder_min_characters), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Password), isError = senhaError != null, errorMessage = senhaError)

                Spacer(modifier = Modifier.height(16.dp))
                FormLabel(text = stringResource(R.string.label_confirm_password))
                FormPasswordField(value = confirmarSenha, onValueChange = { confirmarSenha = it; if (confirmarSenhaError != null) confirmarSenhaError = null }, placeholder = stringResource(R.string.placeholder_repeat_password), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password), isError = confirmarSenhaError != null, errorMessage = confirmarSenhaError)

                Spacer(modifier = Modifier.height(24.dp))
            }

            ProfileBottomActionButtons(
                primaryButtonText = if (isEditMode) stringResource(R.string.action_save_changes) else stringResource(R.string.create_profile_title),
                isEditMode = isEditMode,
                onPrimaryClick = {
                    var isValid = true
                    if (nomeCompleto.trim().isEmpty()) { nomeError = context.getString(R.string.error_name_empty); isValid = false }
                    if (email.trim().isEmpty() || !email.contains("@")) { emailError = context.getString(R.string.error_email_invalid); isValid = false }
                    if (cpf.trim().length != 11) { cpfError = context.getString(R.string.error_cpf_invalid); isValid = false }
                    if (telefone.trim().length < 10) { telefoneError = context.getString(R.string.error_phone_invalid); isValid = false }
                    if (bairroCep.trim().length != 8) { bairroCepError = context.getString(R.string.error_cep_invalid); isValid = false }

                    val listaMateriaisFinal = materiaisSelecionados.toMutableList()
                    if (showOutrosField && outrosMateriais.trim().isNotEmpty()) { listaMateriaisFinal.addAll(outrosMateriais.split(",").map { it.trim() }) }
                    if (listaMateriaisFinal.isEmpty()) { materiaisError = context.getString(R.string.error_materials_empty); isValid = false }

                    if (!isEditMode) {
                        if (senha.length < 6) { senhaError = context.getString(R.string.error_password_min); isValid = false }
                        if (senha != confirmarSenha) { confirmarSenhaError = context.getString(R.string.error_passwords_dont_match); isValid = false }
                    }

                    if (isValid) {
                        scope.launch {
                            userRepository.saveProfile(
                                UserProfile(
                                    nome = nomeCompleto, email = email, telefone = telefone, cpfOrCnpj = cpf, cep = bairroCep, tipoPerfil = "COLETOR", materiais = listaMateriaisFinal,
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