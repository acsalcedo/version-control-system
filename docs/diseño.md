# Diseño del Sistema manejador de Versiones

## Cliente

- Update:
- Commit:
- Checkout: 

## Servidor 

### Principal

- Recibe solicitudes del cliente.
- Puede ejecutar las tareas. 
- Puede pasar las tareas a los servidores de almacenamiento.
- Si falla, se utiliza el algoritmo de grandulón para elegir un nuevo servidor principal.
- Asigna balanceadamente las copias de los archivos.

### Almacenamiento

- Almacena los archivos.
- Puede ejecutar las tareas.
- Cada uno tiene una bitacora que contiene los siguientes datos:
  - Nombre de la máquina
  - Nombres de los archivos que el servidor tiene almacenados con la versión actual.
  
## Archivos

- Estructura: timestamp, datos.
- Semántica de archivos inmutables.
- Al momento de cambiar un archivo, se reemplazan por completo.
- Los archivos deben ser replicados K + 1 veces, K es un parámetro del sistema.

## Comunicación

### Commit

### Checkout y Update

## Dudas

1. Cuando se hace el commit con multicast desde el servidor principal a los servidores de almacenamiento, 
puede haber una falla en la red y uno de ellos no actualiza el archivo a la versión más reciente. 
Qué se debe hacer en este caso?
2. Cómo se puede representar el archivo en el objeto remoto? 
