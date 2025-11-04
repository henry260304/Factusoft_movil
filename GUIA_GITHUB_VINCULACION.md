# 🔗 Guía: Vincular Proyecto con GitHub

## ✅ Estado Actual

Tu configuración de Git:
- ✅ Git instalado: v2.49.0
- ✅ Usuario configurado: henry rodriguez
- ✅ Email: henry.rodriguez23048818@estu.unan.edu.ni
- ❌ Repositorio: NO inicializado

---

## 📋 OPCIÓN A: Ya Tienes Repositorio en GitHub

Si ya creaste el repositorio en GitHub, seguiremos estos pasos:

### Paso 1: Inicializar Git Local
```bash
git init
```

### Paso 2: Crear .gitignore
Se creará automáticamente para excluir archivos innecesarios.

### Paso 3: Hacer el Primer Commit
```bash
git add .
git commit -m "Initial commit: Proyecto FactuSoft con Jetpack Compose"
```

### Paso 4: Conectar con GitHub
```bash
git remote add origin [URL_DE_TU_REPOSITORIO]
git branch -M main
git push -u origin main
```

---

## 📋 OPCIÓN B: NO Tienes Repositorio (Crear Nuevo)

### Paso 1: Crear Repositorio en GitHub (Manual)

1. Ve a: https://github.com/
2. Inicia sesión
3. Haz clic en el botón **"+"** (arriba derecha) → **"New repository"**
4. Llena el formulario:
   - **Repository name**: `factusoft` (o el nombre que prefieras)
   - **Description**: "Sistema de facturación con Android y Jetpack Compose"
   - **Public** o **Private**: Selecciona lo que prefieras
   - ❌ **NO** marques "Add a README file"
   - ❌ **NO** marques "Add .gitignore"
   - ❌ **NO** marques "Choose a license"
5. Haz clic en: **"Create repository"**
6. Copia la URL que aparece (ejemplo: `https://github.com/tuusuario/factusoft.git`)

### Paso 2: Vincular desde tu Computadora

Una vez que tengas la URL, ejecutaré estos comandos automáticamente:

```bash
# Inicializar repositorio local
git init

# Crear .gitignore apropiado
# (ya lo haré automáticamente)

# Agregar todos los archivos
git add .

# Hacer el primer commit
git commit -m "Initial commit: Proyecto FactuSoft con Jetpack Compose"

# Conectar con GitHub
git remote add origin [URL_QUE_ME_DARÁS]

# Cambiar rama a main
git branch -M main

# Subir el código
git push -u origin main
```

---

## 📝 Archivos que se Subirán

### ✅ Se subirán:
- Todo el código fuente (`app/src/`)
- Archivos de configuración (Gradle, manifests)
- Recursos (layouts, drawables, etc.)
- Documentación (.md files)

### ❌ NO se subirán (gracias a .gitignore):
- `/build/` - Archivos compilados
- `/.gradle/` - Caché de Gradle
- `/.idea/` - Configuración de Android Studio
- `*.apk` - Aplicaciones compiladas
- `local.properties` - Propiedades locales
- Archivos temporales

---

## 🔐 Autenticación con GitHub

Cuando ejecutes `git push`, GitHub te pedirá autenticación:

### Método 1: Personal Access Token (Recomendado)

1. Ve a: https://github.com/settings/tokens
2. Haz clic en: **"Generate new token (classic)"**
3. Selecciona permisos:
   - ✅ `repo` (todos los sub-permisos)
   - ✅ `workflow`
4. Copia el token generado
5. Cuando Git pida contraseña, pega el token

### Método 2: GitHub Desktop (Más Fácil)

1. Descarga: https://desktop.github.com/
2. Instala y inicia sesión
3. Abre el proyecto desde GitHub Desktop
4. Haz commits y push desde la interfaz gráfica

### Método 3: GitHub CLI

```bash
# Instalar GitHub CLI primero
winget install --id GitHub.cli

# Autenticarse
gh auth login

# Seguir las instrucciones interactivas
```

---

## 📦 Estructura del .gitignore

Crearé automáticamente un `.gitignore` con esto:

```gitignore
# Built application files
*.apk
*.ap_
*.aab

# Files for the ART/Dalvik VM
*.dex

# Java class files
*.class

# Generated files
bin/
gen/
out/
release/

# Gradle files
.gradle/
build/

# Local configuration file (sdk path, etc)
local.properties

# Android Studio
*.iml
.idea/
.DS_Store
/captures
.externalNativeBuild
.cxx

# Signing files
.signing/

# Google Services (e.g. APIs or Firebase)
google-services.json

# Keystore files
*.jks
*.keystore
```

---

## 🚀 Comandos para Trabajo Diario

Una vez vinculado, estos serán tus comandos frecuentes:

### Ver estado de archivos modificados
```bash
git status
```

### Agregar cambios
```bash
git add .                    # Agregar todos los archivos
git add archivo.kt           # Agregar un archivo específico
```

### Hacer commit
```bash
git commit -m "Descripción del cambio"
```

### Subir cambios a GitHub
```bash
git push
```

### Descargar cambios de GitHub
```bash
git pull
```

### Ver historial
```bash
git log --oneline
```

### Crear una nueva rama
```bash
git checkout -b feature/nueva-funcionalidad
```

### Cambiar de rama
```bash
git checkout main
```

---

## 🎯 Flujo de Trabajo Recomendado

### Cada vez que hagas cambios:

1. **Verifica el estado:**
   ```bash
   git status
   ```

2. **Agrega los cambios:**
   ```bash
   git add .
   ```

3. **Haz commit con mensaje descriptivo:**
   ```bash
   git commit -m "Agregada funcionalidad de reportes"
   ```

4. **Sube a GitHub:**
   ```bash
   git push
   ```

### Mensajes de Commit Recomendados:

✅ Buenos mensajes:
- `"Implementado login con validación"`
- `"Corregido error en cálculo de totales"`
- `"Agregada pantalla de reportes"`
- `"Actualizado diseño del dashboard"`
- `"Refactorizado código de API"`

❌ Malos mensajes:
- `"cambios"`
- `"fix"`
- `"update"`
- `"asdf"`

---

## 🔒 Archivo .gitignore Detallado

```gitignore
# Android
*.apk
*.ap_
*.aab
*.dex
*.class
bin/
gen/
out/
release/

# Gradle
.gradle/
build/
!gradle-wrapper.jar

# Local Properties
local.properties

# IDE (Android Studio, IntelliJ)
*.iml
.idea/
.DS_Store
/captures
.externalNativeBuild
.cxx
*.log

# OS
Thumbs.db
.DS_Store

# Signing
*.jks
*.keystore
.signing/

# Firebase/Google Services
google-services.json
firebase-adminsdk.json

# NDK
obj/
*.so

# Misc
*.swp
*~
.navigation/
```

---

## 📚 Recursos Útiles

### Documentación:
- Git: https://git-scm.com/doc
- GitHub: https://docs.github.com/

### Herramientas:
- GitHub Desktop: https://desktop.github.com/
- GitHub CLI: https://cli.github.com/
- GitKraken: https://www.gitkraken.com/

### Tutoriales:
- Git Básico: https://try.github.io/
- GitHub Skills: https://skills.github.com/

---

## 🆘 Solución de Problemas

### Error: "Permission denied (publickey)"

**Solución:** Configura SSH o usa HTTPS con token.
```bash
# Cambiar a HTTPS si estás usando SSH
git remote set-url origin https://github.com/usuario/repo.git
```

### Error: "Updates were rejected"

**Solución:** Hacer pull primero.
```bash
git pull --rebase origin main
git push
```

### Error: "fatal: refusing to merge unrelated histories"

**Solución:**
```bash
git pull origin main --allow-unrelated-histories
```

### Deshacer último commit (local)

```bash
git reset --soft HEAD~1
```

### Deshacer cambios en un archivo

```bash
git checkout -- archivo.kt
```

---

## ✅ Checklist Final

Antes de hacer push, verifica:

- [ ] ✅ Código compila sin errores
- [ ] ✅ App funciona en emulador
- [ ] ✅ No hay credenciales sensibles en el código
- [ ] ✅ .gitignore está configurado
- [ ] ✅ Mensaje de commit es descriptivo
- [ ] ✅ No hay archivos grandes innecesarios

---

## 🎯 Siguiente Paso

**Dame la URL de tu repositorio de GitHub** y ejecutaré todos los comandos automáticamente para vincularlo.

Si no tienes repositorio, primero créalo en GitHub y luego dame la URL.

