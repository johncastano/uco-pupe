# UCO-PUPE
###### Version 1.0

## Table of Contents

* [Propósito](#propósito)
* [Base de datos](#base de datos)
* [Correr el servicio](#correr el servicio)

## Propósito
El enfoque de este repositorio es ...

## Base de datos
Como gestor de datos se está usando Postgresql.
Para generar las tablas seguir los siguientes pasos:
1. Descargar el motor https://www.postgresql.org/download/
2. Hacer los ajustes nesarios dependiendo del OS que uses
3. Levantar el servicio, por defecto corre en el puerto 5432
4. Crear la BD MANUALMENTE desde el prompt de Postgres con el siguiente comando: ```bash createdb pupe```

## Correr el servicio

Para correr el servicio localmente seguir los siguientes pasos:
1. Descargar sbt
2. Clonar el repo localmente
3. Ubicandote el el directorio del repo('/home/<user>/.../uco-pupe' ejecuta el comando sbt
4. Una vez estes en el prompt de sbt ejecuta el comando ```bash sbt:pupe> run ```

Si seguiste los pasos correctamente de la base de datos, el servicio quedará corriendo el puerto 8080 del localhost listo para ser usado :).
