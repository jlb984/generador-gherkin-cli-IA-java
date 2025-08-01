# Gherkin Generator CLI

Herramienta de consola escrita en Java que utiliza la API de OpenAI para generar escenarios en lenguaje Gherkin basados en una descripción de caso de prueba y los escenarios existentes en tu proyecto.

## ✅ Requisitos

* Java 17+
* Maven 3+
* Variable de entorno `OPENAI_API_KEY` configurada con tu clave de OpenAI

## 🔧 Instalación

1. Clonar o copiar el proyecto.

2. Configurar tu clave de OpenAI:

```bash
export OPENAI_API_KEY=sk-...  # en Linux/macOS
set OPENAI_API_KEY=sk-...     # en Windows CMD
$env:OPENAI_API_KEY = "sk-p"  # en PowerShell
```

3. Compilar el proyecto:

```bash
mvn clean package
```

Se generará el archivo:

```
target/gherkin-generator-1.0-SNAPSHOT.jar
```

## ▶️ Uso

```bash
java -jar target/gherkin-generator-0.1.0.jar \
  --feature "<ruta/carpeta/features>" \
  --funcionalidad "<archivo.feature>" \
  --provider "<proveedor>" \ 
  --descripcion "<descripcion del nuevo caso de prueba>"
```
Nota: La opción `--provider` es opcional y por defecto usa "openai". Opciones válidas: `openai`, `gemini`.

### Ejemplo

```bash
java -jar target/gherkin-generator-0.1.0.jar \
  --feature "src/test/resources/features" \
  --provider "openai" \
  --funcionalidad "emision.feature" \
  --descripcion "Validar que el sistema bloquee al usuario luego de 3 intentos fallidos"
```

## 🧠 Qué hace la herramienta

* Busca archivos `.feature` con el nombre indicado (ej. `emision.feature`) dentro de subcarpetas.
* Lee hasta 10 escenarios desde esos archivos.
* Si no se especifica `--funcionalidad`, lee los últimos escenarios encontrados.
* Envía esos escenarios junto con una nueva descripción a la API de OpenAI.
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