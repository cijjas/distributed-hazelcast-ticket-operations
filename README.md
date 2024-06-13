# tpe2-g7

## Descripción
Este trabaja tiene como objetivo demostrar el funcionamiento de un sistema distribuido que permite realizar consultas sobre datos de multas de tránsito de dos ciudades (New York City y Chicago). Para ello, se implementó un sistema distribuido utilizando Hazelcast, el cual permite realizar consultas sobre los datos de multas de tránsito de ambas ciudades.

## Contenidos
- [Instalación](#instalación)
   - [Prerrequisitos](#prerrequisitos)
   - [Pasos](#pasos)
- [Uso](#uso)
   - [Servidor](#servidor)
      - [Mancenter](#mancenter)
   - [Cliente](#cliente)
      - [Query 1: Total de Multas por Tipo de Infracción](#query-1-total-de-multas-por-tipo-de-infracción)
      - [Query 2: Top 3 infracciones más populares de cada barrio](#query-2-top-3-infracciones-más-populares-de-cada-barrio)
      - [Query 3: Top N agencias con mayor porcentaje de recaudación](#query-3-top-n-agencias-con-mayor-porcentaje-de-recaudación)
      - [Query 4: Patente con más infracciones de cada barrio en el rango [from, to]](#query-4-patente-con-más-infracciones-de-cada-barrio-en-el-rango-from-to)
      - [Query 5: Pares de infracciones que tienen, en grupos de a cientos, el mismo promedio de monto de multa](#query-5-pares-de-infracciones-que-tienen-en-grupos-de-a-cientos-el-mismo-promedio-de-monto-de-multa)
- [Integrantes del Grupo](#integrantes-del-grupo)

## Instalación

### Prerrequisitos
- Java 17 o mayor
- Maven
- Hazelcast 3.6.8

### Pasos
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Lucaseggi/tpe2-g7.git
   ```
   
2. Moverse al directrio del proyecto:
   ```bash
   cd tpe2-g7
   ```
   
3. Compilar el proyecto:
   ```bash
    mvn clean install
    ```
   
   
## Uso
Moverse a la carpeta de los scripts de ejecución.
```bash
cd ./resources/scripts
```
Aquí se encuentran los scripts de ejecución para levantar un nodo en el cluster de hazelcast y los scripts para ejecutar las queries.

### Servidor

Para lanzar una instancia/nodo de hazelcast:
```bash
sh server.sh -Dname=<cluster_name> -Dpass=<cluster_password> -Dinterfaces='<ip1>;<ip2>;...' -Dport=<port_number> 
```

| Parametro      | Opciones            | Descripción                                            | Opcional | Valor por defecto |
|----------------|---------------------|--------------------------------------------------------|----------|-------------------|
| `-Dname`       | `cluster_name`      | Define el nombre del cluster al cual se desea conectar | SI       | `g7`              |
| `-Dpass`       | `cluster_password`  | Contraseña de cluster de hazelcast seleccionado        | SI       | `g7-pass`         |
| `-Dinterfaces` | `'<ip1>;<ip2>;...'` | Interfaces que probará Hazelcast para conectarse       | SI       | `192.168.0.*`     |
| `-Dport`       | `port_number`       | Puerto donde se desea correr la instancia              | SI       | `5701`            |

#### Mancenter
Adicionalmente si se desea tener una interfaz gráfica se puede hacer uso del Hazelcast Managment Center, para esto:
##### Paso 1

Descargarse el [ZIP que contiene el WAR](https://docs.hazelcast.org/docs/latest/javadoc/com/hazelcast/collection/package-summary.html) de Mancenter

##### Paso 2

Descargar la imagen de tomcat en docker
```bash
docker pull tomcat:9.0.82-jre8 
```
##### Paso 3

Correr el contenedor
```bash
docker run -d -p 8080:8080 --name Mytomcat tomcat:9.0.82-jre8
```

##### Paso 4

Copiar el WAR descargado a webapps de tomcat en docker
```bash
docker cp /ruta/al/archivo.war  Mytomcat:/usr/local/tomcat/webapps/
```
Con esto hecho ya debería poderse ver el Mancenter en `http://localhost:8080/mancenter-3.8.5/main.html`

> [!Note]
> Para que el servidor haga uso del Mancenter se deben descomentar las líneas en el archivo `Server.java` dentro del módulo `server`


### Cliente

Las diferentes queries requeiren de una fuente de información para ser procesadas (debe tener ambos archivos `ticketsX.csv` y `infractionsX.csv` donde `X` es o bien NYC o CHI). Cada query generará como resultado un archivo en formato CSV con lo obtenido por la query y otro en formato TXT con los tiempos de ejecución. Estos archivos se generan en la carpeta especificada que **debe crearse previamente (debe existir)**.
#### Query 1: Total de Multas por Tipo de Infracción
```bash
sh query1.sh -Daddresses='<ip1>:<port1>;<ip2>:<port2>;...' -Dcity=[NYC | CHI] -DinPath=<input_path> -DoutPath=<output_path>
```


| Parametro       | Opciones                            | Descripción                                                      | Opcional | Valor por defecto |
|-----------------|-------------------------------------|------------------------------------------------------------------|----------|-------------------|
| `-Daddresses`   | `'<ip1>:<port1>;<ip2>:<port2>;...'` | Direcciones y puertos de los nodos que se quieren usar           | NO       | Ninguno           |
| `-Dcity`        | `city`                              | Ciudad de interés                                                | NO       | Ninguno           |
| `-DinPath`      | `input_path`                        | Ruta al directorio de los archivos fuente                        | NO       | Ninguno     |
| `-DoutPath`     | `output_path`                       | Ruta al direcotrio donde se generarán los archivos de resultados | NO       | Ninguno            |
| `-DclusterName` | `cluster_name`      | Define el nombre del cluster al cual se desea conectar | SI       | `g7`              |
| `-DclusterPass` | `cluster_password`  | Contraseña de cluster de hazelcast seleccionado        | SI       | `g7-pass`         |


#### Query 2: Top 3 infracciones más populares de cada barrio
```bash
sh query1.sh -Daddresses='<ip1>:<port1>;<ip2>:<port2>;...' -Dcity=[NYC | CHI] -DinPath=<input_path> -DoutPath=<output_path>
```


| Parametro       | Opciones                            | Descripción                                                      | Opcional | Valor por defecto |
|-----------------|-------------------------------------|------------------------------------------------------------------|----------|-------------------|
| `-Daddresses`   | `'<ip1>:<port1>;<ip2>:<port2>;...'` | Direcciones y puertos de los nodos que se quieren usar           | NO       | Ninguno           |
| `-Dcity`        | `city`                              | Ciudad de interés                                                | NO       | Ninguno           |
| `-DinPath`      | `input_path`                        | Ruta al directorio de los archivos fuente                        | NO       | Ninguno     |
| `-DoutPath`     | `output_path`                       | Ruta al direcotrio donde se generarán los archivos de resultados | NO       | Ninguno            |
| `-DclusterName` | `cluster_name`      | Define el nombre del cluster al cual se desea conectar | SI       | `g7`              |
| `-DclusterPass` | `cluster_password`  | Contraseña de cluster de hazelcast seleccionado        | SI       | `g7-pass`         |

#### Query 3: Top N agencias con mayor porcentaje de recaudación
```bash
sh query1.sh -Daddresses='<ip1>:<port1>;<ip2>:<port2>;...' -Dcity=[NYC | CHI] -DinPath=<input_path> -DoutPath=<output_path> -Dn=<number>
```

| Parametro       | Opciones                            | Descripción                                                      | Opcional | Valor por defecto |
|-----------------|-------------------------------------|------------------------------------------------------------------|----------|-------------------|
| `-Daddresses`   | `'<ip1>:<port1>;<ip2>:<port2>;...'` | Direcciones y puertos de los nodos que se quieren usar           | NO       | Ninguno           |
| `-Dcity`        | `city`                              | Ciudad de interés                                                | NO       | Ninguno           |
| `-DinPath`      | `input_path`                        | Ruta al directorio de los archivos fuente                        | NO       | Ninguno     |
| `-DoutPath`     | `output_path`                       | Ruta al direcotrio donde se generarán los archivos de resultados | NO       | Ninguno            |
| `-Dn`           | `number`                            | Cantidad de agencias que se quiere ver en el Top                 | NO       | Ninguno            |
| `-DclusterName` | `cluster_name`                      | Define el nombre del cluster al cual se desea conectar           | SI       | `g7`              |
| `-DclusterPass` | `cluster_password`                  | Contraseña de cluster de hazelcast seleccionado                  | SI       | `g7-pass`         |


#### Query 4: Patente con más infracciones de cada barrio en el rango [from, to]
```bash
sh query1.sh -Daddresses='<ip1>:<port1>;<ip2>:<port2>;...' -Dcity=[NYC | CHI] -DinPath=<input_path> -DoutPath=<output_path> -Dfrom=<from_date> -Dto=<to_date>
```

| Parametro       | Opciones                            | Descripción                                                             | Opcional | Valor por defecto |
|-----------------|-------------------------------------|-------------------------------------------------------------------------|----------|-------------------|
| `-Daddresses`   | `'<ip1>:<port1>;<ip2>:<port2>;...'` | Direcciones y puertos de los nodos que se quieren usar                  | NO       | Ninguno           |
| `-Dcity`        | `city`                              | Ciudad de interés                                                       | NO       | Ninguno           |
| `-DinPath`      | `input_path`                        | Ruta al directorio de los archivos fuente                               | NO       | Ninguno     |
| `-DoutPath`     | `output_path`                       | Ruta al direcotrio donde se generarán los archivos de resultados        | NO       | Ninguno            |
| `-Dfrom`        | `from_date`                         | Fecha a partir de la cual es de interes analizar en formato (dd/MM/yyy) | NO       | Ninguno            |
| `-Dto`          | `to_date`                           | Fecha hasta la cual es de interes analizar formato (dd/MM/yyy)          | NO       | Ninguno            |
| `-DclusterName` | `cluster_name`                      | Define el nombre del cluster al cual se desea conectar                  | SI       | `g7`              |
| `-DclusterPass` | `cluster_password`                  | Contraseña de cluster de hazelcast seleccionado                         | SI       | `g7-pass`         |


#### Query 5: Pares de infracciones que tienen, en grupos de a cientos, el mismo promedio de monto de multa
```bash
sh query1.sh -Daddresses='<ip1>:<port1>;<ip2>:<port2>;...' -Dcity=[NYC | CHI] -DinPath=<input_path> -DoutPath=<output_path>
```

| Parametro       | Opciones                            | Descripción                                                      | Opcional | Valor por defecto |
|-----------------|-------------------------------------|------------------------------------------------------------------|----------|-------------------|
| `-Daddresses`   | `'<ip1>:<port1>;<ip2>:<port2>;...'` | Direcciones y puertos de los nodos que se quieren usar           | NO       | Ninguno           |
| `-Dcity`        | `city`                              | Ciudad de interés                                                | NO       | Ninguno           |
| `-DinPath`      | `input_path`                        | Ruta al directorio de los archivos fuente                        | NO       | Ninguno     |
| `-DoutPath`     | `output_path`                       | Ruta al direcotrio donde se generarán los archivos de resultados | NO       | Ninguno            |
| `-DclusterName` | `cluster_name`      | Define el nombre del cluster al cual se desea conectar | SI       | `g7`              |
| `-DclusterPass` | `cluster_password`  | Contraseña de cluster de hazelcast seleccionado        | SI       | `g7-pass`         |


## Integrantes del Grupo
- [Christian Ijjas](https://github.com/cijjas) - Legajo: 63555
- [Luca Seggiaro](https://github.com/Lucaseggi) - Legajo: 62855
- [Manuel Dithurbide](https://github.com/manudithur) - Legajo: 62057
- [Tobias Perry](https://github.com/TobiasPerry) - Legajo: 62064
