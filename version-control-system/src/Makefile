JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:   
	$(JC) $*.java
	
all:
	make class
	rmic OpClienteServidorImple
	mkdir -p cliente servidor servidor/repositorios
	cp $(CLIENTE) cliente
	cp $(SERVIDOR) servidor
	cp -a pruebas/* servidor/repositorios
	rm *.class


CLIENTE = \
		Cliente.class \
		Coleccion.class \
		Documento.class \
		OpClienteServidorImple_Stub.class

SERVIDOR = \
		Coleccion.class \
		Documento.class \
		OpClienteServidor.class \
		OpClienteServidorImple.class \
		OpClienteServidorImple_Stub.class \
		ServAlmacenamiento.class \
		ServPrincipal.class

CLASSES = \
		Cliente.java \
		Coleccion.java \
		Documento.java \
		OpClienteServidorImple.java \
		OpClienteServidor.java \
		ServAlmacenamiento.java \
		ServPrincipal.java

class: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) -f *.class cliente/*.class servidor/*.class
	$(RM) -r cliente servidor