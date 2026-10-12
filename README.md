# CityReport

App **Android nativa** de reporte ciudadano. Permite a las personas reportar incidencias urbanas (baches, luminarias, fallas de servicios), consultar una agenda comunitaria y acceder a servicios locales.

Proyecto Integrador de la materia **Programación de Dispositivos Móviles** - LCI 2022, UPIICSA · IPN. Docente: Dr. Francisco Javier Bueno Vásquez.

---

## Equipo y roles

| Integrante                      | Rol | Responsabilidad principal |
|---------------------------------|---|---|
| AVILES SALAZAR BRAULIO EMILIANO | Project Manager (PM) | Backlog, sprints y entregas |
| BALDERAS ROCHA JOSE ANTONIO     | Analista / Product Owner | Historias de usuario y requerimientos |
| MARIN SALDIVAR GAEL ALEJANDRO   | Arquitecto de Software | Estructura técnica y modelo de datos |
| GONZALEZ CAMPOS LUIS ALBERTO    | Desarrollador Android | Código, componentes y lógica |
| RUIZ CRUZ ESTEPHANIE            | Diseñador UI/UX + QA | Interfaz, flujos y pruebas |

---

## Estructura del proyecto (MVP)

La app sigue el patrón **Modelo-Vista-Presentador**: cada pantalla tiene un contrato con dos
interfaces (`View` y `Presenter`); el Fragment implementa la vista y el Presenter tiene la lógica.

```
app/src/main/java/com/equipo3/hellocityreport/
├─ model/            # Modelo: Incidencia, enums y repositorio (interfaz + datos en memoria)
├─ contract/         # Contratos MVP: interfaces View y Presenter de cada pantalla
├─ presenter/        # Lógica de cada pantalla (Kotlin puro, sin código de Android)
├─ view/             # Vista: Fragments, adapter del RecyclerView y chip de estado
├─ MainActivity.kt   # Top App Bar + BottomNavigationView + NavHostFragment
└─ CityReportApp.kt  # Activa los colores dinámicos de Material 3

app/src/test/java/com/equipo3/hellocityreport/presenter/   # Pruebas unitarias de los Presenters
```

Secciones de la barra inferior: **Inicio** (lista y detalle), **Reportar** (formulario) y **Resumen** (conteos por estado y categoría).

---

## Cómo correr el proyecto

1. Clona el repositorio:
   ```bash
   git clone https://github.com/T3-Programacion-Dispositivos-Moviles/CityReport.git
   ```
2. Ábrelo en **Android Studio** (última versión estable).
3. Espera el **Gradle Sync**.
4. Selecciona un emulador (AVD, API 30+) o un dispositivo físico con **depuración USB** activada.
5. Pulsa **Run**.
6. Para correr las pruebas unitarias de los Presenters: `./gradlew test`.

---
