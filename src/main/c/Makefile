all: fp_test

fp_test: fp_test.o
	gcc -lmpfr -lgmp -o fp_test fp_test.o

fp_test.o: fp_test.c
	gcc -c fp_test.c

clean:
	rm *.o fp_test
