package com.inacap.iotmobileapp.ui.users

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inacap.iotmobileapp.data.database.AppDatabase
import com.inacap.iotmobileapp.data.database.entities.User
import com.inacap.iotmobileapp.data.repository.UserRepository
import com.inacap.iotmobileapp.ui.components.AppTextField
import com.inacap.iotmobileapp.ui.components.AppTopBar
import com.inacap.iotmobileapp.ui.theme.IoTMobileAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun ListUsersScreen(
    onNavigateBack: () -> Unit,
    onNavigateToModify: (Long) -> Unit
) {
    val context = LocalContext.current
    val viewModel: ListUsersViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )
    val users by viewModel.users.collectAsState()

    ListUsersScreenContent(
        users = users,
        onNavigateBack = onNavigateBack,
        onNavigateToModify = onNavigateToModify
    )
}

@Composable
fun ListUsersScreenContent(
    users: List<User>,
    onNavigateBack: () -> Unit,
    onNavigateToModify: (Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppTopBar(title = "LISTADO DE USUARIOS", onNavigateBack = onNavigateBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            AppTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = "Buscar por nombre o apellidos"
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(users.filter {
                    it.nombres.contains(searchQuery, ignoreCase = true) ||
                            it.apellidos.contains(searchQuery, ignoreCase = true)
                }) { user ->
                    UserListItem(user = user, onClick = { onNavigateToModify(user.id) })
                }
            }
        }
    }
}

@Composable
fun UserListItem(user: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${user.nombres} ${user.apellidos}", fontWeight = FontWeight.Bold)
            Text(user.email, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ListUsersScreenPreview() {
    // Datos de prueba
    val dummyUsers = listOf(
        User(id = 1, nombres = "Juan", apellidos = "Pérez", email = "juan@example.com", password = ""),
        User(id = 2, nombres = "María", apellidos = "González", email = "maria@example.com", password = ""),
        User(id = 3, nombres = "Admin", apellidos = "System", email = "admin@inacap.cl", password = "")
    )

    IoTMobileAppTheme {
        ListUsersScreenContent(
            users = dummyUsers,
            onNavigateBack = {},
            onNavigateToModify = {}
        )
    }
}

class ListUsersViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = UserRepository(database.userDao(), database.recoveryCodeDao())

    val users: StateFlow<List<User>> = MutableStateFlow(emptyList())

    init {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getAllUsers().collect { userList ->
                (users as MutableStateFlow).value = userList
            }
        }
    }
}
