# 🔧 Solución: Error "device offline" en Emulador

## ❌ Error que estás viendo:
```
'device offline' error on device serial #emulator-5554
```

Este error significa que **Android Studio perdió la conexión con el emulador**.

---

## ✅ SOLUCIÓN RÁPIDA (Hazlo en este orden):

### Paso 1: Reinicia el Emulador con Cold Boot

**Método Más Fácil:**
1. En Android Studio: `Tools → Device Manager`
2. Busca tu emulador (ej: `Pixel 8 API 35`)
3. Haz clic en el **menú de tres puntos (⋮)**
4. Selecciona: `Cold Boot Now`
5. Espera 30-60 segundos a que arranque completamente

### Paso 2: Reinicia ADB desde Terminal de Android Studio

1. En la parte inferior de Android Studio, haz clic en la pestaña **"Terminal"**
2. Escribe estos comandos uno por uno:

```bash
adb kill-server
adb start-server
adb devices
```

**Debes ver:**
```
List of devices attached
emulator-5554    device    ← Debe decir "device", NO "offline"
```

### Paso 3: Verifica y Ejecuta

1. Verifica que en la barra superior veas tu emulador:
   ```
   [▶️ Run] [Pixel 8 API 35 ▼]
   ```

2. Haz clic en **▶️ Run** de nuevo

---

## 🔄 SOLUCIÓN COMPLETA (Si la rápida no funciona):

### Opción A: Cerrar Todo y Reiniciar

1. **Cierra el emulador** completamente
2. **Cierra Android Studio**
3. Abre Android Studio de nuevo
4. Espera a que cargue
5. Ve a: `File → Sync Project with Gradle Files`
6. Abre el emulador: `Tools → Device Manager → ▶️ Play`
7. Espera 1-2 minutos a que arranque
8. Intenta ejecutar de nuevo

### Opción B: Limpiar Datos del Emulador

1. Ve a: `Tools → Device Manager`
2. Haz clic en el menú **⋮** junto a tu emulador
3. Selecciona: `Wipe Data`
4. Confirma: `Wipe Data`
5. Espera a que se reinicie

### Opción C: Actualizar Platform-Tools

1. Ve a: `Tools → SDK Manager`
2. Pestaña: `SDK Tools`
3. Busca: `Android SDK Platform-Tools`
4. Si no está marcado, márcalo
5. Haz clic en: `Apply`
6. Espera a que termine
7. Reinicia Android Studio

---

## 📋 CHECKLIST RÁPIDO

Marca cada paso:

- [ ] ✅ Reinicié el emulador (Cold Boot)
- [ ] ✅ Reinicié ADB (`adb kill-server` + `adb start-server`)
- [ ] ✅ `adb devices` muestra "device" (no "offline")
- [ ] ✅ El emulador aparece en el dropdown de Run
- [ ] ✅ Intenté ejecutar la app de nuevo

---

## 🎯 SOLUCIÓN MÁS RÁPIDA (Haz esto primero):

**1. Cold Boot del Emulador:**
- `Tools → Device Manager → ⋮ → Cold Boot Now`

**2. Espera 1 minuto**

**3. Ejecuta la app (▶️ Run)**

**¡Eso resuelve el 90% de los casos!**

---

## 🆘 Si Sigue Sin Funcionar

Dime:
1. ¿Qué ves cuando ejecutas `adb devices` en la terminal de Android Studio?
2. ¿El emulador aparece en el dropdown de Run?
3. ¿Qué mensaje exacto ves ahora?

¡Te ayudo inmediatamente! 🚀
