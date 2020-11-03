/**
 *  \file producersConsumers.c (implementation file)
 *
 *  \brief Problem name: Multithreading counter
 *
 *  Synchronization based on monitors.
 *  Both threads and the monitor are implemented using the pthread library which enables the creation of a
 *  monitor of the Lampson / Redell type.
 *
 *  Generator thread of the intervening entities.
 *
 *  \author Borys Chystov, Isaac dos Anjos
 */

#include <stdio.h>
#include <stdlib.h>
#include <libgen.h>
#include <unistd.h>

/* Allusion to internal functions */

static void printUsage(char * cmdName);
static void readFile(char * fName);
/**
 *  \brief Main function.
 *
 *  \param argc number of words of the command line
 *  \param argv list of words of the command line
 *
 *  \return status of operation
 */

int main(int argc, char * argv[]) {
    /* process command line options */
    int opt; /* selected option */
    char * fName; /*File Names*/
    opterr = 0;
    do {
        switch ((opt = getopt(argc, argv, "f:h"))) {
            case 'f':
                /* file name */
                fName = optarg;
                readFile(fName);
                break;
            case 'h':
                /* help mode */
                printUsage(basename(argv[0]));
                return EXIT_SUCCESS;
            case '?':
                /* invalid option */
                fprintf(stderr, "%s: invalid option\n", basename(argv[0]));
                printUsage(basename(argv[0]));
                return EXIT_FAILURE;
            case -1:
                break;
        }
    } while (opt != -1);

    if (argc == 1) {
        fprintf(stderr, "%s: invalid format\n", basename(argv[0]));
        printUsage(basename(argv[0]));
        return EXIT_FAILURE;
    }
    int o; /* counting variable */
    for (o = 0; o < argc; o++)
        printf("Word %d = %s\n", o, argv[o]);
    return EXIT_SUCCESS;

} /* end of main */

/**
 *  \brief Print command usage.
 *
 *  A message specifying how the program should be called is printed.
 *
 *  \param cmdName string with the name of the command
 */

static void printUsage(char * cmdName) {
    fprintf(stderr, "\nSynopsis: %s [OPTIONS] [filename1 filename2 ...]\n"
        "  OPTIONS:\n"
        "  -h      --- print this help\n", cmdName);
}

/**
 *  \brief Function readFile.
 *
 *  Read and process file.
 *
 *  \param fName filename
 */
static void readFile(char * fName) {

    /* Array of signals, the reference corresponds to the solution*/
    double * sig[3];

    /* Size of the sigtnal */
    int nElem;

    /* Handle current file */
    FILE * f;
    if ((f = fopen(fName, "rb")) == NULL) {
        perror("Error on file opening");
        exit(EXIT_FAILURE);
    }

    if (fread( & nElem, sizeof(int), 1, f) != 1) { //lê os primeiros 4 bytes, corresponde a um inteiro
        fprintf(stderr, "Erro na leitura do numero de elementos dos sinais\n");
        exit(EXIT_FAILURE);
    }
    printf("%i\n", nElem);
    for (size_t i = 0; i < 3; i++) {
        if ((sig[i] = malloc(nElem * sizeof(double))) == NULL) {
            perror("Error retrieving doubles");
            exit(EXIT_FAILURE);
        }
        if (fread(sig[i], sizeof(double), nElem, f) != nElem) {
            fprintf(stderr, "Erro na leitura dos valores de um sinal\n");
            exit(EXIT_FAILURE);
        }
    }

    /* Process the signal */
    double sigRes[nElem];
    for (size_t t = 0; t < nElem; t++) {
        for (size_t k = 0; k < nElem; k++) {
            sigRes[t] += sig[0][k] * sig[1][(t + k) % nElem];
        }
    }

    for (size_t i = 0; i < nElem; i++) {

        printf("%f\n", sigRes[i]);
    }
    fclose(f);
}