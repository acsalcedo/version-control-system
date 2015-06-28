# version-control-system
Sistema manejador de versiones (SMV) para Sistemas de Operacion 2 - Abril-Julio 2015.

### Compilación y Organización

Para compilar todas las clases y copiar los repositorios de prueba en las carpetas especificadas:
    
    make

### Corrida de Cliente y Servidores

##### Cliente:
    
    java Cliente <IPHost> <PuertoServicio>

##### Servidor Principal:
    
    java ServPrincipal <PuertoServicio> <PuertoServicioAlmacenamiento> <IPMulticast> <NroReplicasArchivos> <NroServidoresAlmacenamiento>

##### Servidor de Almacenamiento:
    
    java ServAlmacenamiento <nombreServidor> <PuertoServicioAlmacenamiento> <IPMulticast>


### Ejemplo

##### Correr:
        
    java ServPrincipal 5000 8500 224.0.0.3 1 1
    java ServAlmacenamiento 1 8500 224.0.0.3
    java Cliente localhost 5000


##### En el cliente:
    
    Opción 1 Checkout del repositorio: "repo1"
    Opción 2 Agregar repositorio: "test"
    Opción 3 Agregar archivo: "test/archivo1.txt"
    Opción 3 Agregar archivo: "test/archivo2.txt"
    Opción 4 Commit
  
