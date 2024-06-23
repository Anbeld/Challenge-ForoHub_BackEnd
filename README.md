# ForoHub: API REST usando Spring

API tipo CRUD (CREATE, READ, UPDATE, DELETE) centrada el ecosistema de una plataforma de educación en línea.

## Entidades
- [Usuario](#usuario)
- [Curso](#curso)
- [Tópico](#topico)
- [Respuesta](#respuesta)

### Usuario
##### Métodos HTTP
- **POST**
	- Registrar un nuevo por rol:
		- Rol usuario:
			- **ESTUDIANTE**
			- **DOCENTE**
		- Registrar un nuevo estudiante:
			- Información requerida:
				- `nombre`
				- `email` -> no se permiten correos repetidos
				- `contraseña`
		- Registrar un nuevo docente:
			- Información requerida:
				- `nombre`
				- `email` -> no se permiten correos repetidos
				- `contraseña`

	- Login: Inicio de sesión usuario.
		- Información requerida:
			- Usuario registrado
				- `email`
				- `contreseña`
		- Token:
			- Genera un token válido por dos horas para dar acceso al usuario al endpoint.
- **GET**
	- Obtener listado de usuarios por rol:
		- Obtener listado de estudiantes:
			- Retorna un listado de todos los docentes registrados.
		- Obtener listado de docentes:
			- Retorna un listado de todos los estudiantes registrados.
	- Obtener usuario por `id`
		- Usuario registrado:
			- Retorna la información de el usuario cuyo `id` corresponda al suministrado.
				- Solo puede retornar un usuario cuando este existe previo a la solicitud.
- **PUT**
	- Actualizar contraseña usuario:
		- Información requerida:
			- Usuario registrado:
				- `id`
				- `email`
				- `actual_contraseña`
				- `nueva_contraseña`
- **DELETE**
	- Eliminar lógicamente un usuario: desactivar un usuario:
		- Información requerida:
			- Usuario registrado:
				- `id`

### Curso
##### Métodos HTTP
- **POST**
	- Registrar un nuevo curso:
		- Información requerida:
			- `nombre` -> no se permiten nombres repetidios
			- `categoria`
			- `docente_id`
		- Categoria curso
			- **FRONTEND**
			- **BACKEND**
			- **DEVOPS**
			- **DATASCIENCE**
	- Registrar un estudiante a un curso:
		- Información requerida:
			- `curso_id`
			- `estudiante_id`
- **GET**
	- Obtener listado de cursos:
		- Retorna el listado de todos los cursos registrados.
	- Obtener curso por `id`
		- Curso registrado:
			- Retorna la información de el curso cuyo `id` corresponda al suministrado
				- Solo puede retornar un curso cuando este existe previo a la solicitud.

### Tópico
##### Métodos HTTP
- **POST**
	- Registrar un nuevo tópico:
		- Información requerida:
			- `curso_id`
			- `usuario_id`
			- `titulo`
			- `mensaje`
- **GET**
	- Obtener listado de todos los tópicos:
		- Retorna el listado de todos los tópicos registrados.
	- Obtener listado de tópicos activos (no resueltos):
		- Retorna el listado de todos los tópicos activos.
	- Obtener listado de tópicos por `status` (resuelto - no resuelto):
		- Retorna el listado de todos los tópicos que cumplan con el `status` enviado.
- **DELETE**
	- Eliminar lógicamente un tópico: cerrar un tópicos:
		- Información requerida:
			- Tópico registrado:
				- `id`

### Respuesta
##### Métodos HTTP
- **POST**
	- Registrar una nueva respuesta:
		- Información requerida:
			- `topico_id`
			- `autor_id`
			- `mensaje`
- **GET**
	- Obtener listado de respuestas con tópicos activos (no resueltos):
		- Retorna el listado de respuestas con tópicos activos.
	- Obtener listado de respuestas por `autor_id`:
		- Retorna el listado de respuestas donde `autor_id` sea igual al suministrado.
	- Obtener listado de respuestas por `topico_id`:
		- Retorna el listado de respuestas donde `topico_id` sea igual al suministrado.

## Seguridad
ForoHub cuenta con un sistema de seguridad de tipo token, en este caso **Bearer Key**. El token es generado cada vez que un usuario registrado inicia sesión y tiene una válidez de dos horas. Después de estas dos horas el usuario tendrá que generar un nuevo token.

##### Métodos HTTP
###### Requiere token:
- **POST**
	- [Curso](#curso):
		- Registrar un nuevo curso.
		- Registrar un estudiante a un curso.
	- Tópico:
		- Registrar un nuevo tópico.
	- Respuesta:
		- Registrar una nueva respuesta.
- **GET**
	- [Usuario](#usuario):
		- Obtener listado de estudiantes registrados.
		- Obtener listado de docentes registrados.
		- Obtener usuario por `id`
	. [Curso](#curso):
		- Obtener listado de cursos.
		- Obtener curso por `id`
	- [Tópico](#topico):
		- Obtener listado de todos los tópicos.
		- Obtener listado de tópicos activos (no resueltos).
		- Obtener listado de tópicos por `status` (resuelto - no resuelto).
	- [Respuesta](#respuesta):
		- Obtener listado de respuestas con tópicos activos (no resueltos).
		- Obtener listado de respuestas por `autor_id`
		- Obtener listado de respuestas por `topico_id`
- **PUT**
	- [Usuario](#usuario):
		- Actualizar contraseña usuario.
- **DELETE**
	- [Usuario](#usuario):
		Eliminar lógicamente un usuario: desactivar un usuario.
	- Tópico:
		- Eliminar lógicamente un tópico: cerrar un tópicos.

###### No requiere token:
- **POST**
	- [Usuario](#usuario):
		- Registrar un nuevo estudiante.
		- Registrar un nuevo docente.
		- Login: Inicio de sesión usuario.

## Documentación

Documentación realizada con OpenAPI, todos los métodos HTTP están asociados al tag de su entidad y a su respectivo método.

![image](https://github.com/Anbeld/Challenge-ForoHub_BackEnd/assets/147835151/1d44f8dc-ff00-4b0d-8951-add2e320b0a4)
