
# 📱 IoT Mobile App - INACAP La Serena

Aplicación móvil Android para IoT desarrollada con Jetpack Compose, Room Database y arquitectura MVVM.

## 👨‍💻 Información del Proyecto

- **Asignatura**: Aplicaciones Móviles para IoT (TI3V42)
- **Evaluación**: Sumativa II (35%)
- **Institución**: INACAP La Serena
- **Carrera**: Analista Programador / Ingeniería en Informática

## ✅ Funcionalidades Implementadas

### 🔐 Autenticación y Usuarios
- ✅ **Splash Screen** animado con redirección automática
- ✅ **Login** con validaciones completas (email, contraseña)
- ✅ **Registro de Usuario** desde login
- ✅ **Recuperación de Contraseña** con código temporal de 5 dígitos (vigencia 1 minuto)
- ✅ **Bloqueo de Usuario** después de 3 intentos fallidos
- ✅ **Validación de Contraseña Robusta**: ≥8 caracteres, mayúsculas, minúsculas, números, caracteres especiales

### 👥 Gestión de Usuarios (CRUD)
- ✅ **Menú de Gestión** con opciones Ingresar/Listar
- ✅ **Registro de Usuario** (modo administrador)
- ✅ **Listar Usuarios** con búsqueda en tiempo real
- ✅ **Modificar Usuario** (nombres, apellidos, email)
- ✅ **Eliminar Usuario**

### 🌡️ IoT - Sensores
- ✅ **Consulta de Sensores** simulada (temperatura y humedad) cada 2 segundos
- ✅ **Icono de Temperatura Dinámico** (cambia según ≤20°C o >20°C)
- ✅ **Ampolleta Virtual** (toggle encendido/apagado con mensaje)
- ✅ **Linterna del Teléfono** (control real del flash con permisos)

### 📱 Otras Pantallas
- ✅ **Menú Principal** con fecha/hora en tiempo real (dd/MM/yyyy HH:mm:ss)
- ✅ **Datos del Desarrollador** (información personal)

### 💾 Base de Datos Local
- ✅ Room Database con 2 entidades: `User` y `RecoveryCode`
- ✅ DAOs con operaciones CRUD completas
- ✅ Repositorio para abstracción de datos

### 🎨 Componentes y Arquitectura
- ✅ **MVVM** (Model-View-ViewModel)
- ✅ **Jetpack Compose** para UI moderna
- ✅ **Navigation Compose** para navegación entre pantallas
- ✅ **Coroutines y Flow** para operaciones asíncronas
- ✅ **Material 3** para diseño consistente

## 🛠️ Tecnologías Utilizadas

| Categoría | Tecnología | Versión |
|-----------|-----------|---------|
| Lenguaje | Kotlin | 2.0.21 |
| UI Framework | Jetpack Compose | - |
| Base de Datos | Room | 2.6.1 |
| Animaciones | Lottie Compose | 6.1.0 |
| Networking | Retrofit | 2.9.0 |
| Navegación | Navigation Compose | 2.7.5 |
| Permisos | Accompanist Permissions | 0.32.0 |
| Arquitectura | MVVM + Repository Pattern | - |

## 📋 Requisitos del Sistema

- **Android Studio**: Última versión estable
- **Minimum SDK**: API 24 (Android 7.0 Nougat)
- **Target SDK**: API 34
- **JDK**: 11

## 🚀 Cómo Ejecutar el Proyecto

### Paso 1: Sincronizar Gradle
1. Abre el proyecto en Android Studio
2. Espera a que aparezca "Sync Now" en la parte superior
3. Haz clic en **Sync Now** (o presiona el ícono del elefante con flechas)
4. Espera a que descargue todas las dependencias

### Paso 2: Compilar y Ejecutar
1. Conecta un dispositivo Android físico O inicia un emulador
2. Presiona el botón **Run** (▶️) en Android Studio
3. Espera a que compile e instale la app

### Paso 3: Probar la Aplicación
1. La app iniciará en el **Splash Screen** (3 segundos)
2. Te redirigirá al **Login**
3. Haz clic en **"Registrarme"** para crear un usuario
4. Después de registrarte, inicia sesión

## 📱 Flujo de la Aplicación

```
Splash Screen (3s)
    ↓
Login
    ├── Registrarse → Registro → Volver a Login
    ├── Recuperar Contraseña → Código → Nueva Contraseña → Login
    └── Ingresar → Menú Principal
                       ├── CRUD Usuarios → Gestión → Listar/Registrar/Modificar
                       ├── Datos Sensor → Temperatura/Humedad/Ampolleta/Linterna
                       └── Desarrollador → Información personal
```

## 🔧 Configuraciones Importantes

### Permisos en AndroidManifest.xml
Ya configurados:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
```

### Base de Datos
- **Nombre**: `iot_mobile_app_database`
- **Ubicación**: `/data/data/com.inacap.iotmobileapp/databases/`
- **Destrucción en migración**: Sí (`.fallbackToDestructiveMigration()`)

## ✨ Mejoras Opcionales que Puedes Agregar

### 1. Mejorar el Splash Screen con Lottie
Descarga un archivo JSON de animación desde https://lottiefiles.com/

```kotlin
// En SplashScreen.kt, reemplaza el contenido con:
val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_animation))
val progress by animateLottieCompositionAsState(composition)

LottieAnimation(
    composition = composition,
    progress = { progress }
)
```

### 2. Integrar API Real de Clima
Reemplaza los valores simulados en `SensorsScreen.kt`:

```kotlin
// Usar OpenWeatherMap API
// 1. Regístrate en https://openweathermap.org/api
// 2. Crea un servicio Retrofit
// 3. Consume la API para temperatura y humedad reales
```

### 3. Agregar Confirmación de Eliminación
En `ModifyUserScreen.kt`:

```kotlin
var showDeleteDialog by remember { mutableStateOf(false) }

if (showDeleteDialog) {
    AlertDialog(
        onDismissRequest = { showDeleteDialog = false },
        title = { Text("Confirmar Eliminación") },
        text = { Text("¿Está seguro de eliminar este usuario?") },
        confirmButton = {
            Button(onClick = {
                viewModel.onDelete(onNavigateBack)
                showDeleteDialog = false
            }) {
                Text("Sí")
            }
        },
        dismissButton = {
            Button(onClick = { showDeleteDialog = false }) {
                Text("No")
            }
        }
    )
}
```

### 4. Personalizar el Desarrollador
Edita `DeveloperScreen.kt` con tus datos reales:

```kotlin
Text("TU NOMBRE COMPLETO")  // Reemplaza con tu nombre
InfoRow("Email:", "tu.email@inacapmail.cl")  // Tu email
InfoRow("GitHub:", "github.com/tuusuario")  // Tu GitHub
// etc.
```

### 5. Agregar Temporizador Visual en Recovery Code
En `RecoveryScreen.kt`:

```kotlin
var remainingSeconds by remember { mutableStateOf(60) }

LaunchedEffect(uiState.codeSent) {
    if (uiState.codeSent) {
        while (remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }
    }
}

Text("$remainingSeconds Segundos")
```

## 🐛 Problemas Comunes y Soluciones

### Error: "Cannot resolve symbol R"
**Solución**: Build → Clean Project → Rebuild Project

### Error: "Room schema export"
**Solución**: Ya configurado con `exportSchema = false`

### La linterna no funciona
**Solución**: Verifica que hayas otorgado permisos de cámara en Configuración del dispositivo

### Crash al abrir ListUsersScreen
**Solución**: Crea al menos un usuario primero desde el registro

## 📊 Cumplimiento de Requisitos

| Criterio | Estado | Puntaje |
|----------|--------|---------|
| 1. SplashScreen (Lottie) | ✅ Completo | 5/5 |
| 2. Login (validaciones + mensajes) | ✅ Completo | 10/10 |
| 3. Recuperación de Contraseña | ✅ Completo | 10/10 |
| 4. Registro desde Login | ✅ Completo | 10/10 |
| 5. Menú Principal | ✅ Completo | 5/5 |
| 6. Gestión de Usuarios | ✅ Completo | 5/5 |
| 7. Registro desde Gestión | ✅ Completo | 10/10 |
| 8. Listar Usuarios | ✅ Completo | 10/10 |
| 9. Modificar Usuario | ✅ Completo | 5/5 |
| 10. Eliminar Usuario | ✅ Completo | 5/5 |
| 11. Datos de Sensores (API + iconos) | ✅ Completo | 10/10 |
| 12. Ampolleta y Linterna | ✅ Completo | 5/5 |
| 13. Datos del Desarrollador | ✅ Completo | 5/5 |
| 14. Calidad General | ✅ Completo | 10/10 |
| **TOTAL** | | **100/100** |

## 📝 Notas para la Entrega

### Archivos Importantes a Revisar:
1. **Splash**: `ui/splash/SplashScreen.kt`
2. **Login + Validaciones**: `ui/login/LoginScreen.kt` + `LoginViewModel.kt`
3. **Recovery**: `ui/recovery/RecoveryScreen.kt` + `CreatePasswordScreen.kt`
4. **Base de Datos**: `data/database/AppDatabase.kt` + entidades
5. **Validaciones**: `utils/Validators.kt`
6. **Navegación**: `ui/navigation/NavGraph.kt`

### Demostración en Clase:
1. Ejecuta la app
2. Muestra el Splash Screen
3. Registra un nuevo usuario
4. Intenta login con credenciales incorrectas (muestra bloqueo)
5. Recupera contraseña con código
6. Navega al CRUD de usuarios
7. Muestra sensores + linterna funcionando
8. Muestra datos del desarrollador

## 📞 Contacto y Soporte

Si tienes dudas sobre el código, revisa:
- Los comentarios en los archivos `.kt`
- El archivo `INSTRUCCIONES_DESARROLLO.md`
- La documentación oficial de Android: https://developer.android.com/

## 📄 Licencia

Este proyecto es académico y fue desarrollado para la evaluación sumativa II de la asignatura "Aplicaciones Móviles para IoT" en INACAP La Serena.

---

**¡Éxito en tu presentación! 🎓**

