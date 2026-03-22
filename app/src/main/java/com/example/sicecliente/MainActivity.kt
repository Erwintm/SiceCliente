package com.example.sicecliente

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                PantallaCliente(
                    onQueryKardex = { consultarProvider("content://com.example.marsphotos.provider/kardex") },
                    onQueryCarga = { consultarProvider("content://com.example.marsphotos.provider/carga") }
                )
            }
        }
    }

    @SuppressLint("Range")
    private fun consultarProvider(uriString: String): List<String> {
        val lista = mutableListOf<String>()
        val uri = Uri.parse(uriString)

        // Aquí ocurre la magia del ContentResolver
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                // Intentamos sacar una columna genérica (ajusta según tus nombres de columna)
                val nombre = if (uriString.contains("kardex")) {
                    "Materia: ${it.getString(it.getColumnIndex("materia"))} - Cal: ${it.getInt(it.getColumnIndex("calificacion"))}"
                } else {
                    "Carga: ${it.getString(it.getColumnIndex("Materia"))} - Docente: ${it.getString(it.getColumnIndex("Docente"))}"
                }
                lista.add(nombre)
            }
        } ?: lista.add("No se encontraron datos o falta permiso")

        return lista
    }
}

@Composable
fun PantallaCliente(onQueryKardex: () -> List<String>, onQueryCarga: () -> List<String>) {
    var resultados by remember { mutableStateOf(listOf<String>()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "SICE Content Provider Cliente", style = MaterialTheme.typography.headlineMedium)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { resultados = onQueryKardex() }) {
                Text("Ver Kardex")
            }
            Button(onClick = { resultados = onQueryCarga() }) {
                Text("Ver Carga")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {
            items(resultados) { item ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(text = item, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}