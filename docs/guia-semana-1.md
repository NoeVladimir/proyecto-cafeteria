# Guia de Semana 1: entorno y workflow

Esta guia explica como completar la configuracion de Semana 1 del proyecto COOPUES. Cada integrante debe completar los pasos correspondientes en su propia computadora y cuenta de GitHub.

## 1. Preparar el entorno

Cada integrante debe tener instalado:

- Docker Desktop.
- VS Code.
- Extension Dev Containers de VS Code.
- Git.

Comprobar desde PowerShell:

```powershell
docker --version
git --version
node --version
python --version
```

Docker Desktop debe estar abierto antes de iniciar el Dev Container.

## 2. Abrir el Dev Container

1. Abrir el repositorio en VS Code.
2. Presionar `Ctrl+Shift+P`.
3. Ejecutar `Dev Containers: Reopen in Container`.
4. Esperar a que termine la construccion.
5. Confirmar que el terminal muestra `/workspace`.

El contenedor instala automaticamente las dependencias mediante `.devcontainer/setup.sh`.

Dentro del contenedor ejecutar:

```bash
node --version
java --version
git --version
psql postgresql://dev:dev@db:5432/appdb -c "select 1;"
```

La consulta de PostgreSQL debe devolver una fila con el valor `1`.

## 3. Configurar SSH en cada computadora

Cada persona debe crear y registrar su propia clave. Nunca se debe compartir la clave privada.

Crear la clave en PowerShell:

```powershell
ssh-keygen -t ed25519 -C "correo-de-github@example.com"
```

Presionar Enter para aceptar la ruta predeterminada. Se puede usar una passphrase o dejarla vacia presionando Enter dos veces.

Mostrar la clave publica:

```powershell
Get-Content $HOME\.ssh\id_ed25519.pub
```

Copiar la linea completa, que comienza con `ssh-ed25519`.

En GitHub:

1. Abrir la foto de perfil.
2. Entrar en `Settings`.
3. Entrar en `SSH and GPG keys`.
4. Pulsar `New SSH key`.
5. En `Title`, escribir el nombre del equipo.
6. En `Key type`, dejar `Authentication Key`.
7. Pegar la clave publica en `Key`.
8. Pulsar `Add SSH key`.

Probar la autenticacion:

```powershell
ssh -T git@github.com
```

Si pregunta si se confia en el host, escribir `yes`. Debe aparecer un mensaje indicando que la autenticacion fue exitosa.

Configurar el remoto SSH dentro del proyecto:

```powershell
git remote set-url origin git@github.com:NoeVladimir/proyecto-cafeteria.git
git remote -v
git fetch origin
```

Las URLs de `fetch` y `push` deben comenzar con `git@github.com:`.

No compartir nunca este archivo:

```text
C:\Users\TU_USUARIO\.ssh\id_ed25519
```

## 4. Agregar al compañero

El propietario del repositorio debe:

1. Abrir `Settings` del repositorio.
2. Entrar en `Collaborators`.
3. Pulsar `Add people`.
4. Buscar al compañero por usuario de GitHub.
5. Dar permiso `Write`.
6. Esperar que el compañero acepte la invitacion.

El compañero debe comprobar que puede obtener el proyecto:

```powershell
git clone git@github.com:NoeVladimir/proyecto-cafeteria.git
cd proyecto-cafeteria
git switch main
git pull origin main
```

## 5. Usar la rama `main` protegida

En GitHub, el propietario debe abrir:

`Settings` -> `Rules` -> `Rulesets` -> `New branch ruleset`

Configurar:

- Nombre: `Protect main`.
- Estado: `Active`.
- Rama objetivo: `main`.
- `Restrict deletions`.
- `Require a pull request before merging`.
- Aprobaciones requeridas: `1`.
- `Require status checks to pass`.
- Checks requeridos: `frontend` y `backend`.
- `Block force pushes`.

Opcional: activar `Require branches to be up to date before merging`.

No es necesario activar signed commits, deployments, code scanning ni code coverage para Semana 1.

Todo cambio debe seguir este flujo:

```text
crear rama -> hacer cambios -> push -> Pull Request -> revision -> merge a main
```

## 6. Crear el tablero

En el repositorio de GitHub:

1. Abrir `Projects`.
2. Pulsar `New project`.
3. Seleccionar vista `Board`.
4. Nombrar el proyecto `COOPUES - M1`.
5. Crear estas columnas:

```text
Todo
In progress
In review
Done
```

Agregar como primeras tareas:

- Configurar Dev Container.
- Completar Project Proposal.
- Crear modelo de datos.
- Construir catalogo.
- Crear endpoints iniciales.

Mover cada tarea segun su estado. Las tareas nuevas van en `Todo`; una tarea en desarrollo va en `In progress`; una tarea terminada esperando revision va en `In review`; y una tarea revisada va en `Done`.

## 7. Crear la issue Project Proposal

1. Abrir `Issues` en el repositorio.
2. Pulsar `New issue`.
3. Seleccionar la plantilla disponible o una issue en blanco.
4. Usar exactamente este titulo:

```text
Project Proposal
```

5. Copiar el contenido de `docs/project-proposal.md` en la descripcion.
6. Pulsar `Submit new issue`.
7. Agregar la issue al proyecto `COOPUES - M1` en la columna `Todo`.

La issue debe incluir:

- El problema que resuelve el proyecto.
- Una explicacion de la solucion en 30 segundos.
- Una entidad principal con al menos seis campos.
- Una entidad relacionada.
- La relacion uno-a-muchos.
- El alcance inicial.

## 8. Acuerdo de trabajo en pareja

Ambos integrantes deben conocer estas reglas:

- Ambos hacen frontend y backend.
- Se cambia Driver y Navigator cada 20 minutos.
- El Driver escribe y ejecuta; el Navigator revisa y propone.
- Los cambios entran mediante Pull Request.
- Cada sesion se registra en `docs/pair-log.md`.

En cada entrada del registro indicar fecha, participantes, Driver, Navigator, trabajo realizado y siguiente rotacion.

## 9. Verificacion final

Cada integrante debe poder ejecutar dentro del contenedor:

```bash
node --version
java --version
git --version
psql postgresql://dev:dev@db:5432/appdb -c "select 1;"
mvn -f backend/pom.xml verify
npm --prefix frontend run build
```

Resultados esperados:

- Node 22.x.
- Java 25.x.
- Git disponible.
- PostgreSQL devuelve `1`.
- Maven termina con `BUILD SUCCESS`.
- Vite termina con `built`.
- El repositorio usa SSH.
- Ambos integrantes pueden trabajar con `main` mediante Pull Requests.

## Checklist para entregar

- [ ] Docker Desktop funciona.
- [ ] Ambos integrantes abren el Dev Container.
- [ ] Las dependencias se instalan automaticamente.
- [ ] PostgreSQL responde desde el contenedor.
- [ ] Cada integrante tiene SSH configurado.
- [ ] Ambos pueden clonar por SSH.
- [ ] El repositorio tiene protegida la rama `main`.
- [ ] CI exige los checks `frontend` y `backend`.
- [ ] Existe el tablero `COOPUES - M1`.
- [ ] El tablero tiene cuatro columnas.
- [ ] Existe la issue `Project Proposal`.
- [ ] Existe la primera entrada de `docs/pair-log.md`.
