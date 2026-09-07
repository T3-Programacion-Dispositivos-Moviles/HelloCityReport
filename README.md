# CityReport

App **Android nativa** de reporte ciudadano. Permite a las personas reportar incidencias urbanas (baches, luminarias, fallas de servicios), consultar una agenda comunitaria y acceder a servicios locales.

Proyecto Integrador de la materia **Programación de Dispositivos Móviles** - LCI 2022, UPIICSA · IPN. Docente: Dr. Francisco Javier Bueno Vásquez.

---

## Equipo y roles

| Integrante | Rol | Responsabilidad principal |
|---|---|---|
| AVILES SALAZAR BRAULIO EMILIANO | Project Manager (PM) | Backlog, sprints y entregas |
| RIVAS HERNANDEZ EDUARDO | Analista / Product Owner | Historias de usuario y requerimientos |
| MARIN SALDIVAR GAEL ALEJANDRO | Arquitecto de Software | Estructura técnica y modelo de datos |
| GONZALEZ CAMPOS LUIS ALBERTO | Desarrollador Android | Código, componentes y lógica |
| RUIZ CRUZ ESTEPHANIE | Diseñador UI/UX + QA | Interfaz, flujos y pruebas |

---

## Estructura del proyecto

```
app/src/main/java/com/equipo3/HelloCityReport/
├─ mode/            # Datos y lógica de negocio
├─ view/             # Activities, Fragments, adapters, layouts
├─ controller/       # Coordina model <-> view (MVC)
```
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

---
