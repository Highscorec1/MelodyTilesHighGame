
# 🎵 Melody Tiles High Night

Melody Tiles High Night es un juego musical rítmico donde tus reflejos y precisión son clave. 
Inspirado en clásicos como Piano Tiles y OSU!  pero con estética única, niveles propios y canciones originales.

---

## 🕹️ Características

- 🎼 Niveles musicales personalizados con audio local.
- 🟦 Estética visual basada en animaciones XML, drawables y tipografía personalizada
- 🧠 Modo Tutorial con lógica de flujo propia y `ViewModel`
- ⏱️ Temporizador dinámico (`CountDownTimer` + lógica personalizada)
- 💥 Sistema de combos y precisión de toques
- 📊 Registro de progreso en `SharedPreferences` y archivos locales
- 📲 Compatible desde Android 7.0 (SDK 24) hasta Android 14

---

## 🛠️ Tecnologías y herramientas utilizadas

### Lenguaje y Framework
- **Kotlin**
- **Android SDK / Jetpack**
- Android View System (sin Jetpack Compose)
- XML para layouts y animaciones

### Librerías
- `androidx.lifecycle` – ViewModel, LiveData
- `com.google.android.gms:play-services-ads` – Google AdMob
- `com.google.android.material:material` – Componentes UI Material Design
- `Gson` – Manejo de archivos JSON para niveles musicales
- `Mockito` – Para test unitarios
- `Espresso` – Para pruebas UI instrumentadas

### Otros recursos y tecnologías
- Custom Fonts (`.ttf`)
- Archivos `.mp3` incrustados en `res/raw/`
- Diseño multilenguaje (`values/values-es/`)
- Soporte para múltiples resoluciones (`mipmap-hdpi`, `xhdpi`, etc.)
- Animaciones XML y efectos visuales personalizados

---

## 🔧 Cómo compilar

1. Cloná el repositorio:
   ```bash
   git clone https://github.com/Highscorec1/MelodyHighTilesNight.git
   cd MelodyHighTilesNight
   ```

2. Asegurate de tener Android Studio y SDK 24+ instalado

3. **Claves sensibles:**
   - Crear un archivo `ads_config.xml` en `app/src/main/res/values/` con tu clave AdMob:
     ```xml
     <resources>
         <string name="admob_banner_id">ca-app-pub-xxxxxxxxxxxxxxxx/yyyyyyyyyy</string>
     </resources>
     ```

4. Ejecutá la app desde Android Studio o con:
   ```
   ./gradlew installDebug
   ```

---

## 🧠 Dificultad técnica del proyecto (desarrollado solo)

| Área                          | Dificultad | Detalles |
|------------------------------|------------|----------|
| UI/UX                        | 🟨 Media   | Layouts animados y adaptables |
| Audio e interacción          | 🟧 Alta    | Precisión en toques y temporizador |
| Persistencia de datos        | 🟨 Media   | Uso de `SharedPreferences` y JSON |
| Monetización (AdMob)         | 🟨 Media   | Integración y gestión de claves seguras |
| Testing                      | 🟨 Media   | Pruebas unitarias e instrumentadas |
| Diseño visual/audio          | 🟥 Alta    | Si |

---

## 🚀 Valor como proyecto profesional

- Muestra dominio de **Kotlin y Android SDK moderno**
- Refleja habilidades en diseño UI/UX sin frameworks como Compose
- Demuestra comprensión de arquitectura (ViewModel, datos locales)
- Expone conocimientos en **monetización** y publicación móvil
---

## 🛡️ Seguridad y privacidad

- ✅ Claves API sensibles están excluidas del repositorio (`.gitignore`)
- ✅ No se suben archivos compilados ni APKs firmadas
- ✅ Todos los datos del usuario se guardan de forma local en el dispositivo

---

## 📃 Licencia

Este proyecto está licenciado bajo la [MIT License](LICENSE).

---

## 🙌 Autor

Creado con pasión por **Joel - Highscorec1**

- GitHub: [@Highscorec1](https://github.com/Highscorec1)
- Contacto: *(highscorecompany20@gmail.com)*

---

## 🌐 Difusión y redes

> Este proyecto está pensado como parte de mi portafolio profesional.  
> Podés ver más de mis trabajos o contactarme directamente para colaboraciones o propuestas freelance.  
> ¡Estrellas, forks y sugerencias son bienvenidas!

---

## 🚀 Estado del proyecto

📌 En desarrollo activo – Mostrado como portfolio. ¡Se aceptan estrellas ⭐!
