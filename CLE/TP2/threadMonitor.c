/**
 *  \file fifo.c (implementation file)
 *
 *  \brief Problem name: Producers / Consumers.
 *
 *  Synchronization based on monitors.
 *  Both threads and the monitor are implemented using the pthread library which enables the creation of a
 *  monitor of the Lampson / Redell type.
 *
 *  Data transfer region implemented as a monitor.
 *
 *  Definition of the operations carried out by the producers / consumers:
 *     \li putVal
 *     \li getVal.
 *
 *  \author Borys Chystov , Isaac dos Anjos
 */
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <unistd.h>
#include <pthread.h>
#include <errno.h>
#include <string.h>
#include "threadMonitor.h"
#include "probConst.h"

/** \brief Struct that will hold the results of  each file */
struct Results {
    char * fname;
    double * sigResult;
    int nElem;
};

/** \brief Results of all files*/
struct Results results[N];

/** \brief threads return status array */
extern int status[T + 1];

/** \brief locking flag which warrants mutual exclusion inside the monitor */
pthread_mutex_t accessCR = PTHREAD_MUTEX_INITIALIZER;

/** \brief array containing the path of the files */
static char * fileNames[N];
FILE * currentFile = NULL;
int fileIndex = -1;
int nFiles;;
double * sig[2]; //para os dois sig e para o res
int nElem;
int k, t;


/**
 *  \brief Opens the next file.
 *
 *  Operation carried out by the Workers.
 *
 */
void openNextFile() {
    if (fileIndex > -1) {
        fclose(currentFile);
        free(sig[0]);
        free(sig[1]);
        //free(sigResult[fileIndex]);
    }

    if ((currentFile = fopen(fileNames[++fileIndex], "r")) == NULL) {
        perror("Error on file opening");
        exit(EXIT_FAILURE);
    }

    if (fread( & (results[fileIndex].nElem), sizeof(int), 1, currentFile) != 1) { //lê os primeiros 4 bytes, corresponde a um inteiro
        fprintf(stderr, "Erro na leitura do numero de elementos dos sinais\n");
        exit(EXIT_FAILURE);
    }

    results[fileIndex].sigResult = (double * ) malloc(results[fileIndex].nElem * sizeof(double));
    for (size_t i = 0; i < 2; i++) {
        if ((sig[i] = malloc(results[fileIndex].nElem * sizeof(double))) == NULL) {
            perror("Error retrieving doubles");
            exit(EXIT_FAILURE);
        }
        if (fread(sig[i], sizeof(double), results[fileIndex].nElem, currentFile) != results[fileIndex].nElem) {
            fprintf(stderr, "Erro na leitura dos valores de um sinal\n");
            exit(EXIT_FAILURE);
        }
    }
    results[fileIndex].fname = fileNames[fileIndex];
    for (int i = 0; i < results[fileIndex].nElem; i++)
        results[fileIndex].sigResult[i] = 0;
    t = 0;
    k = 0;
}

/** getData
 *  \brief Read the next data.
 *
 *  Operation carried out by the Workers.
 *
 *  \param id consumer identification
 *  \param index, the Worker's index reference
 *  \param a, the Worker's "a" reference.
 *  \param b, the Worker's "b" reference.
 *  \param fileId, the Worker's fileId reference, indicates the current file that the chunk belongs to.
 *
 *  \return 1 if there is more work to do. Otherwise 0.
 *
 */
int getData(unsigned int id, int *index, double *a, double *b, int *fileId) {
    int termination = 0;
    * a = 0;
    * b = 0;
    * fileId = 0;
    if ((status[id] = pthread_mutex_lock( & accessCR)) != 0) /* enter monitor */ {
        errno = status[id]; /* save error in errno */
        perror("error on entering monitor(CF)");
        status[id] = EXIT_FAILURE;
        pthread_exit( & status[id]);
    }
    /* Begin Critical Region */
  
    if (currentFile == NULL)
        openNextFile();
    if (k == results[fileIndex].nElem) {
        k = 0;
        t++;
    }
    if (t < results[fileIndex].nElem) {
        * a = sig[0][k];
        * b = sig[1][(t + k) % results[fileIndex].nElem];

        * fileId = fileIndex;
        * index = t;
        termination++;
        k++;
    } else if (fileIndex + 1 < nFiles) {
        openNextFile();
        termination++;
    }
  
    /* End Critical Region */
    if ((status[id] = pthread_mutex_unlock( & accessCR)) != 0) /* exit monitor */ {
        errno = status[id]; /* save error in errno */
        perror("error on exiting monitor(CF)");
        status[id] = EXIT_FAILURE;
        pthread_exit( & status[id]);
    }
    return termination;
}

/** saveResults
 *  \brief Saves the results of the chunk.
 *
 *  Operation carried out by the Workers.
 *
 *  \param id consumer identification.
 *  \param index, the Worker's index reference
 *  \param result, the Worker's result reference, the result of the operation.
 *  \param fileId, the Worker's fileId reference, indicates the current file that the chunk belongs to.
 *
 *  \return 1 if there is more work to do. Otherwise 0.
 *
 */
void saveResults(int id, int *index, double *result, int *fileId) {

    if ((status[id] = pthread_mutex_lock( & accessCR)) != 0) /* enter monitor */ {
        errno = status[id]; /* save error in errno */
        perror("error on entering monitor(CF)");
        status[id] = EXIT_FAILURE;
        pthread_exit( & status[id]);
    }

    /* Begin Critical Region */
 
    results[ * fileId].sigResult[ * index] += * result;

    /* End Critical Region */
    if ((status[id] = pthread_mutex_unlock( & accessCR)) != 0) /* exit monitor */ {
        errno = status[id]; /* save error in errno */
        perror("error on exiting monitor(CF)");
        status[id] = EXIT_FAILURE;
        pthread_exit( & status[id]);
    }
}

/**
 *  \brief Function printResults.
 *
 *  Operation carried out by the Main thread.
 *
 */
void printResults() {
    for (int i = 0; i < nFiles; i++) {
        printf("%s\n", results[fileIndex].fname);
        for (int j = 0; j < results[i].nElem; j++)
            printf("%f\n", results[i].sigResult[j]);
    }
}

/**
 *  \brief Function setFileNames.
 *
 *  Operation carried out by the Main thread.
 *
 *  \param numOfFiles, correspondes the number of input files given by the user
 *  \param filePaths, array of input files.
 *
 */
void setFileNames(unsigned int numOfFiles, char *filePaths[]) {
    for (size_t i = 0; i < numOfFiles; i++) {
        fileNames[i] = filePaths[i];
        printf("%s\n", filePaths[i]);
    }
    nFiles = numOfFiles;
}