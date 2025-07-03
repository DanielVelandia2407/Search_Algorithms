# Cómo generar un ejecutable de este código

Este documento explica cómo generar un archivo JAR ejecutable para la aplicación de Algoritmos de Búsqueda.

## Requisitos previos

- Java Development Kit (JDK) instalado en tu sistema
- El código fuente compilado (los archivos .class deben estar en la carpeta out/production/search_algorithms)

## Pasos para generar el ejecutable

1. Asegúrate de que el código esté compilado correctamente. Si usas IntelliJ IDEA, puedes compilar el proyecto con Build > Build Project.

2. Ejecuta el archivo batch `build_jar.bat` incluido en el proyecto:
   - Haz doble clic en el archivo `build_jar.bat`
   - O abre una ventana de comandos, navega hasta la carpeta del proyecto y ejecuta `build_jar.bat`

3. Se creará un archivo llamado `SearchAlgorithms.jar` en la carpeta raíz del proyecto.

## Cómo ejecutar la aplicación

Hay dos formas de ejecutar la aplicación:

1. **Doble clic**: En la mayoría de los sistemas, puedes hacer doble clic en el archivo `SearchAlgorithms.jar` para ejecutarlo.

2. **Línea de comandos**: Abre una ventana de comandos, navega hasta la carpeta del proyecto y ejecuta:
   ```
   java -jar SearchAlgorithms.jar
   ```

## Solución de problemas

- Si el archivo JAR no se ejecuta con doble clic, intenta ejecutarlo desde la línea de comandos para ver los mensajes de error.
- Asegúrate de tener Java instalado y configurado correctamente en tu sistema.
- Verifica que la variable de entorno PATH incluya la ruta a la carpeta bin de Java.