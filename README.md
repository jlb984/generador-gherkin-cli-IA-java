# Gherkin Generator CLI

Herramienta de consola escrita en Java que utiliza la API de OpenAI o Gemini para generar escenarios en lenguaje Gherkin basados en una descripción de caso de prueba y los escenarios existentes en tu proyecto.

## Requisitos

* Java 17+
* Maven 3+
* Variable de entorno `OPENAI_API_KEY` y/o `GEMINI_API_KEY` según el proveedor que uses

## Instalación

1. Clonar o copiar el proyecto.

2. Configurar la clave de API:

```bash
# Linux / macOS
export OPENAI_API_KEY=sk-...
export GEMINI_API_KEY=AIza...

# Windows CMD
set OPENAI_API_KEY=sk-...
set GEMINI_API_KEY=AIza...

# PowerShell
$env:OPENAI_API_KEY = "sk-..."
$env:GEMINI_API_KEY = "AIza..."
```

3. Compilar el proyecto:

```bash
mvn clean package
```

Se generará el archivo:

```
target/gherkin-generator.jar
```

## Uso

```bash
java -jar target/gherkin-generator.jar ^
  --feature "<ruta/carpeta/features>" ^
  --descripcion "<descripcion del nuevo caso de prueba>" ^
  [--funcionalidad "<archivo.feature>"] ^
  [--provider "openai|gemini"] ^
  [--debug]
```

> En Bash, reemplazá `^` por `\` para multilínea.

### Parámetros

- `--feature`: Carpeta raíz donde están los archivos `.feature`
- `--descripcion`: Descripción del nuevo caso de prueba
- `--funcionalidad`: (opcional) nombre del archivo `.feature` a buscar (ej. `emision.feature`). Si no se indica, se leen los últimos 10 escenarios de todos los archivos.
- `--provider`: (opcional) `openai` (por defecto) o `gemini`
- `--debug`: (opcional) Muestra información detallada de la solicitud y respuesta a la API

### Ejemplo

```bash
java -jar target/gherkin-generator.jar ^
  --feature "src/test/resources/features" ^
  --descripcion "Validar que el sistema bloquee al usuario luego de 3 intentos fallidos" ^
  --funcionalidad "emision.feature" ^
  --provider "gemini" ^
  --debug
```

## Qué hace la herramienta

- Busca archivos `.feature` con el nombre indicado (ej. `emision.feature`) dentro de subcarpetas.
- Lee hasta 10 escenarios desde esos archivos (o los últimos si no se indica `--funcionalidad`).
- Envía esos escenarios junto con una nueva descripción a la API de IA.
- Recibe un escenario Gherkin generado por IA.
- Imprime el resultado en consola.
- Guarda automáticamente el escenario generado en la carpeta:

```
generated-features/escenario_generado_YYYYMMDD_HHmmss.feature
```

## Estructura recomendada

```
gherkin-generator/
├── src/
│   └── main/java/com/jorge/gpt/
│       ├── Main.java
│       ├── GherkinGenerator.java
│       ├── OpenAIProvider.java
│       └── GeminiProvider.java
├── pom.xml
└── generated-features/   # salida automática de los escenarios generados
```

## Notas

- La carpeta `generated-features` se crea automáticamente si no existe.
- Compatible con proyectos de automatización que usen Cucumber.
- El parámetro `--debug` es útil para depurar la solicitud y respuesta de las APIs.

## Licencia

Uso interno / libre con atribución.

## Contacto

Para consultas, sugerencias o contribuciones, contactame vía:
- LinkedIn: https://www.linkedin.com/in/jorge-luis-bergandi/