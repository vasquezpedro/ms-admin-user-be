# ms-admin-usuario


Desarrollador : Pedro Vasquez


## instrucciones de construcción y ejecución del proyecto

Abrir terminal dentro del directorio 02-fuentes/ms-admin-usuario-be, ejecutar las siguientes lineas de comando:

**Compilar fuentes y ejecutar test unitarios: ./gradlew build 

Compilar fuentes sin test unitarios: ./gradlew build -x test 

**Despliegue de la aplicación: ./gradlew bootRun

### TEST UNITARIOS

**Compilar test unitarios: ./gradlew testClasses

**Ejecución de test: ./gradlew test

### REST API
**Puerto:8081

**URL LOCAL: http://localhost:8081/api

**ENDPOINTS:
  
  **USER
  - POST create user:http://localhost:8081/api/user/sign-up
		- request:{ "name": "Test TESTER", "email": "tetst@test.cl", "password": "h1nter2L", "phones": [ { "number": 975386422, "citycode": 25, "contrycode": "32" } ] }
  - GET user:http://localhost:8081/api/user/login - requiere bearer token
  - PUT user:http://localhost:8081/api/user - requiere bearer token
		- request:{ "id": "6a8b8947-8fb4-44f2-9ef2-2de6fbc0bcbd", "name": "Test TESTER", "email": "tetst@test.cl", "password": "h1nter2L", "phones": [ { "number": 975386422, "citycode": 25, "contrycode": "32" } ] }
  - DELETE user:http://localhost:8081/api/user/{iduser} - requiere bearer token
  
  **PHONE
  - POST create phones:http://localhost:8081/api/phone - requiere bearer token
		-request:{ "idUser": "6a8b8947-8fb4-44f2-9ef2-2de6fbc0bcbd", "number": 975486422, "cityCode": 255,  "contrycode": "32"  }
  - GET phones user:http://localhost:8081/api/phone/{iduser} - requiere bearer token
  - PUT user:http://localhost:8081/api/phone - requiere bearer token
		-request:{ "id": 4, "idUser": "6a8b8947-8fb4-44f2-9ef2-2de6fbc0bcbd", "number": 975386422, "cityCode": 25,  "contrycode": "32"  }
  - DELETE user:http://localhost:8081/api/phone/{iduser}/{idphone} - requiere bearer token
 
 ### DOCUMENTACION 
 03-documentacion/diagrama_componentes.png
 03-documentacion/diagrama_secuencia.png


