JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:   
	$(JC) $*.java
	
all:
	make class
	rmic OpClienteServidorImple
	mkdir -p cliente servidor servidor/repositorios servidorAlm servidorAlm/repositorios
	cp $(CLIENTE) cliente
	cp $(SERVIDOR) servidor
	cp $(SERVIDOR) servidorAlm 
	cp -a pruebas/* servidorAlm/repositorios
	cp -a pruebas/* cliente/
	rm *.class


CLIENTE = \
		Cliente.class \
		Coleccion.class \
		Documento.class \
		OpClienteServidorImple_Stub.class \
		OpClienteServidor.class

SERVIDOR = \
		Coleccion.class \
		Documento.class \
		OpClienteServidor.class \
		OpClienteServidorImple.class \
		OpClienteServidorImple_Stub.class \
		ServAlmacenamiento.class \
		ServPrincipal.class \
		OpServidorServidor.class

CLASSES = \
		Cliente.java \
		Coleccion.java \
		Documento.java \
		OpClienteServidorImple.java \
		OpClienteServidor.java \
		ServAlmacenamiento.java \
		ServPrincipal.java \
		OpServidorServidor.java

class: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) -f *.class 
	$(RM) -r cliente servidor servidorAlm