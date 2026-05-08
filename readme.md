# 🛒 SuperAhorro

Aplicación Android para registrar, consultar y analizar gastos de supermercado, permitiendo llevar un mejor control de las compras y detectar oportunidades de ahorro.

> Trabajo Práctico Integrador — Tecnologías Móviles 2026 — IUA

---

## Descripción

**SuperAhorro** es una aplicación móvil Android desarrollada con Jetpack Compose que permite al usuario gestionar sus compras de supermercado de forma simple e intuitiva. El usuario puede registrar compras con sus productos, adjuntar fotos del ticket, consultar su historial y visualizar estadísticas de gasto.

---

## 📱 Pantallas

| Pantalla | Descripción |
|---|---|
| 🌟 Splash | Pantalla de bienvenida inicial |
| 🔐 Login / Registro | Flujo de autenticación de usuario |
| 🏠 Home | Listado de últimas compras |
| ➕ Nueva Compra | Registro de una compra con productos y ticket |
| 📦 Nuevo Producto | Alta de productos al catálogo |
| 🔍 Detalle de Compra | Vista completa de una compra y sus productos |
| 📜 Historial | Historial de compras ordenado por fecha |
| 📊 Estadísticas | Gráficos y métricas de gasto |
| 👤 Mi Perfil | Datos y configuración del usuario |
| ⚙️ Settings | Configuración de la aplicación |

> 📸 *Próximamente: capturas de pantalla o GIF demostrativo*

---

## 🚀 Funcionalidades implementadas

- ✅ Registro de compras con fecha, hora y supermercado
- ✅ Gestión de productos por compra (agregar, editar, eliminar)
- ✅ Catálogo de productos con precio
- ✅ Cálculo automático del total de la compra
- ✅ Adjuntar imagen del ticket desde **galería o cámara**
- ✅ Validaciones de formularios (campos vacíos, cantidades inválidas)
- ✅ Listado de últimas compras en el Home
- ✅ Historial de compras
- ✅ Pantalla de estadísticas
- ✅ Navegación entre pantallas con Navigation Compose
- ✅ Arquitectura MVVM

---

## 🛠️ Tecnologías utilizadas

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Navegación:** Navigation Compose
- **Arquitectura:** MVVM (Model - View - ViewModel)
- **Imágenes:** Coil (`coil-compose`)
- **Cámara / Galería:** ActivityResultContracts
- **Persistencia:** DataStore Preferences
- **Íconos:** Material Icons Extended

---

## 📦 Estructura del proyecto

```
com.example.superahorro/
├── model/
│   ├── Compra.kt
│   ├── Producto.kt
│   └── CatalogoProducto.kt
├── ui/
│   ├── screens/
│   │   ├── SplashScreen.kt
│   │   ├── LoginScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── NuevaCompraScreen.kt
│   │   ├── NuevoProductoScreen.kt
│   │   ├── DetalleCompraScreen.kt
│   │   ├── HistorialScreen.kt
│   │   ├── EstadisticasScreen.kt
│   │   ├── PerfilScreen.kt
│   │   └── SettingsScreen.kt
│   ├── components/
│   │   └── (componentes reutilizables)
│   └── viewmodel/
│       └── HomeViewModel.kt
└── MainActivity.kt
```

---

## ⚙️ Cómo correr el proyecto

### Requisitos previos
- Android Studio Hedgehog o superior
- JDK 11
- Android SDK 24+

### Pasos

1. Cloná el repositorio:
```bash
git clone https://github.com/Lauti00/SuperAhorro.git
```

2. Abrí el proyecto en **Android Studio**

3. Esperá a que Gradle sincronice las dependencias

4. Ejecutá en un emulador o dispositivo físico con **Android 7.0 (API 24)** o superior

---

# 🛒 SuperAhorro

Aplicación Android para registrar, consultar y analizar gastos de supermercado, permitiendo llevar un mejor control de las compras y detectar oportunidades de ahorro.

> Trabajo Práctico Integrador — Tecnologías Móviles 2026 — IUA

---

## 📋 Descripción

**SuperAhorro** es una aplicación móvil Android desarrollada con Jetpack Compose que permite al usuario gestionar sus compras de supermercado de forma simple e intuitiva. El usuario puede registrar compras con sus productos, adjuntar fotos del ticket, consultar su historial y visualizar estadísticas de gasto.

---

## 📱 Pantallas

| Pantalla | Descripción |
|---|---|
| 🌟 Splash | Pantalla de bienvenida inicial |
| 🔐 Login / Registro | Flujo de autenticación de usuario |
| 🏠 Home | Listado de últimas compras |
| ➕ Nueva Compra | Registro de una compra con productos y ticket |
| 📦 Nuevo Producto | Alta de productos al catálogo |
| 🔍 Detalle de Compra | Vista completa de una compra y sus productos |
| 📜 Historial | Historial de compras ordenado por fecha |
| 📊 Estadísticas | Gráficos y métricas de gasto |
| 👤 Mi Perfil | Datos y configuración del usuario |
| ⚙️ Settings | Configuración de la aplicación |

<video src="/screenshots/demo.mp4" autoplay loop muted width="700"></video>

---

## 🚀 Funcionalidades implementadas

- ✅ Registro de compras con fecha, hora y supermercado
- ✅ Gestión de productos por compra (agregar, editar, eliminar)
- ✅ Catálogo de productos con precio
- ✅ Cálculo automático del total de la compra
- ✅ Adjuntar imagen del ticket desde **galería o cámara**
- ✅ Validaciones de formularios (campos vacíos, cantidades inválidas)
- ✅ Listado de últimas compras en el Home
- ✅ Historial de compras
- ✅ Pantalla de estadísticas
- ✅ Navegación entre pantallas con Navigation Compose
- ✅ Arquitectura MVVM

---

## 🛠️ Tecnologías utilizadas

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Navegación:** Navigation Compose
- **Arquitectura:** MVVM (Model - View - ViewModel)
- **Imágenes:** Coil (`coil-compose`)
- **Cámara / Galería:** ActivityResultContracts
- **Persistencia:** DataStore Preferences
- **Íconos:** Material Icons Extended

---

## 📦 Estructura del proyecto

```
com.example.superahorro/
├── model/
│   ├── Compra.kt
│   ├── Producto.kt
│   └── CatalogoProducto.kt
├── ui/
│   ├── screens/
│   │   ├── SplashScreen.kt
│   │   ├── LoginScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── NuevaCompraScreen.kt
│   │   ├── NuevoProductoScreen.kt
│   │   ├── DetalleCompraScreen.kt
│   │   ├── HistorialScreen.kt
│   │   ├── EstadisticasScreen.kt
│   │   ├── PerfilScreen.kt
│   │   └── SettingsScreen.kt
│   ├── components/
│   │   └── (componentes reutilizables)
│   └── viewmodel/
│       └── HomeViewModel.kt
└── MainActivity.kt
```

---

## ⚙️ Cómo correr el proyecto

### Requisitos previos
- Android Studio Hedgehog o superior
- JDK 11
- Android SDK 24+

### Pasos

1. Cloná el repositorio:
```bash
git clone https://github.com/Lauti00/SuperAhorro.git
```

2. Abrí el proyecto en **Android Studio**

3. Esperá a que Gradle sincronice las dependencias

4. Ejecutá en un emulador o dispositivo físico con **Android 7.0 (API 24)** o superior

---

## 👥 Integrantes

| Nombre | GitHub |
|---|---|
| Lautaro Niccolini | [@Lauti00](https://github.com/Lauti00) |
| Emanuel Benitez | [@emanuelsimon] (https://github.com/emanuelsimon) |

---

## 📄 Licencia

Proyecto académico IUA — Asignatura: Tecnologías Móviles 2026##

