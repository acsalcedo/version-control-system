JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:   
	$(JC) $*.java
	
all:
	make class
	rmic OpClienteServidorImple
	mkdir -p cliente servidor servidor/repositorios servidorAlmacenamiento1 servidorAlmacenamiento2
	cp $(CLIENTE) cliente
	cp $(SERVIDOR) servidor
	cp $(SERVIDORALMACENAMIENTO) servidorAlmacenamiento1
	cp $(SERVIDORALMACENAMIENTO) servidorAlmacenamiento2
	cp -a repopruebas/* servidor/repositorios
	rm *.class


CLIENTE = \
		Cliente.class \
		Coleccion.class \
		Documento.class \
		OpClienteServidor.class \
		OpClienteServidorImple_Stub.class

SERVIDOR = \
		Coleccion.class \
		Documento.class \
		OpClienteServidor.class \
		OpServidorServidor.class \
		OpClienteServidorImple.class \
		ServAlmacenamiento.class \
		ServPrincipal.class

SERVIDORALMACENAMIENTO = \
		Coleccion.class \
		Documento.class \
		ServAlmacenamiento.class \

CLASSES = \
		Cliente.java \
		Coleccion.java \
		Documento.java \
		OpClienteServidorImple.java \
		OpServidorServidor.java \
		OpClienteServidor.java \
		ServAlmacenamiento.java \
		ServPrincipal.java

class: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) -f *.class cliente/*.class servidor/*.class
	$(RM) -r cliente servidor