# Gherkin Generator CLI

Herramienta de consola escrita en Java que utiliza la API de OpenAI o Gemini para generar escenarios en lenguaje Gherkin basados en una descripción de caso de prueba y los escenarios existentes en tu proyecto.

## ✅ Requisitos

### 🔑 Cómo obtener tus claves de API

* **OpenAI**: Iniciá sesión en [https://platform.openai.com/account/api-keys](https://platform.openai.com/account/api-keys), hacé clic en “+ Create new secret key” y copiá la clave generada.

* **Gemini (Google AI)**: Accedé a [https://makersuite.google.com/app/apikey](https://makersuite.google.com/app/apikey), generá una clave de API y guardala en un lugar seguro.


* Java 17+
* Maven 3+
* Variables de entorno:
  * `OPENAI_API_KEY` configurada con tu clave de OpenAI (si usás OpenAI)
  * `GEMINI_API_KEY` configurada con tu clave de Gemini (si usás Gemini)

## 🔧 Instalación

1. Clonar o copiar el proyecto.

2. Configurar tu clave de API según el proveedor:

```bash
# Linux/macOS
export OPENAI_API_KEY=sk-...
export GEMINI_API_KEY=tu_clave_gemini

# Windows CMD
set OPENAI_API_KEY=sk-...
set GEMINI_API_KEY=tu_clave_gemini

# PowerShell
$env:OPENAI_API_KEY = "sk-..."
$env:GEMINI_API_KEY = "tu_clave_gemini"
```

3. Compilar el proyecto:

```bash
mvn clean package
```

Se generará el archivo:

```
target/gherkin-generator.jar
```

## ▶️ Uso

```bash
java -jar target/gherkin-generator.jar ^
  --feature "<ruta/carpeta/features>" ^
  --funcionalidad "<archivo.feature>" ^
  --provider "<proveedor>" ^
  --descripcion "<descripcion del nuevo caso de prueba>"
```

> ⚠️ Formato de comandos multilínea:
> * Bash / Linux / macOS: usar `\`
> * CMD (Windows): usar `^`
> * PowerShell: usar `\` o una línea continua sin símbolo

Notas:
* La opción `--provider` es opcional y por defecto usa "openai". Opciones válidas: `openai`, `gemini`.
* La opción `--funcionalidad` también es opcional. Si no se indica, se tomarán los últimos escenarios de cualquier archivo `.feature` encontrado en la carpeta indicada.

### Ejemplo

```bash
java -jar target/gherkin-generator.jar \
  --feature "src/test/resources/features" \
  --provider "openai" \
  --funcionalidad "emision.feature" \
  --descripcion "Validar que el sistema bloquee al usuario luego de 3 intentos fallidos"
```

## 🧠 Qué hace la herramienta

* Busca archivos `.feature` con el nombre indicado (ej. `emision.feature`) dentro de subcarpetas.
* Lee hasta 10 escenarios desde esos archivos.
* Si no se especifica `--funcionalidad`, lee los últimos escenarios encontrados.
* Envía esos escenarios junto con una nueva descripción a la API de OpenAI o Gemini.
* Recibe un escenario Gherkin generado por IA.
* Imprime el resultado en consola.
* Guarda automáticamente el escenario generado en la carpeta:

```
generated-features/escenario_generado_YYYYMMDD_HHmmss.feature
```

## 📁 Estructura recomendada

```
gherkin-generator/
├── src/
│   └── main/java/com/jorge/gpt/
│       ├── Main.java
│       └── GherkinGenerator.java
├── pom.xml
└── generated-features/   # salida automática de los escenarios generados
```

## 📌 Notas

* La carpeta `generated-features` se crea automáticamente si no existe.
* Se puede adaptar fácilmente para guardar los escenarios generados en Git.
* Está pensado para integrarse como herramienta auxiliar dentro de proyectos de automatización con Cucumber.

## 📄 Licencia

Uso interno / libre con atribución.

---
## 📞 Contacto
Para consultas, sugerencias o contribuciones, puedes contactarme a través de:
* [LinkedIn](https://www.linkedin.com/in/jorge-luis-bergandi/)
