/**
 *  \file fifo.c (implementation file)
 *
 *  \brief Problem name: Producers / Workers.
 *
 *  Synchronization based on monitors.
 *  Both threads and the monitor are implemented using the pthread library which enables the creation of a
 *  monitor of the Lampson / Redell type.
 *
 *  Data transfer region implemented as a monitor.
 *
 *  Definition of the operations carried out by the producers / Workers:
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
#include "2cle1.h"
#include "probConst.h"
 
 /** \brief Struct that will hold the results of  each file */
struct Result {
    int count;
    int counters[50];
    int V_counters[50][50];
    int largestWord;
    char * filename;
};

/** \brief Results of all files*/
struct Result results[N];

/** \brief Current file index */
int fileIndex = -1;

/** \brief Total number of files inserted */
int nFiles = 0;

/** \brief Current file reader */
FILE * currentFile = NULL;

/** \brief Current word's length */
int tempIndex = 0;

/** \brief Current word */
unsigned char temp[50];

/** \brief threads return status array */
extern int status[T + 1];

/** \brief locking flag which warrants mutual exclusion inside the monitor */
pthread_mutex_t accessCR = PTHREAD_MUTEX_INITIALIZER;

/** \brief array containing the path of the files */
static char *fileNames[N];


/**
 *  \brief Close the file.
 *
 *  Operation carried out by the Workers.
 *
 */
void closeFile() {
    fclose(currentFile);
    currentFile = NULL;
}

/**
 *  \brief Opens the next file.
 *
 *  Operation carried out by the Workers.
 *
 */
void openNextFile() {
    if (fileIndex > -1) {
        fclose(currentFile);
    }
    if ((currentFile = fopen(fileNames[++fileIndex], "r")) == NULL) {
        perror("Error on file opening");
        exit(EXIT_FAILURE);
    }
    results[fileIndex].count = 0;
    results[fileIndex].largestWord = 0;
    for (int i = 0; i < 50; i++) {
        results[fileIndex].counters[i] = 0;
        for (int j = 0; j < 50; j++)
            results[fileIndex].V_counters[i][j] = 0;
    }
    results[fileIndex].filename = fileNames[fileIndex];
}

/** getTextChunk
 *  \brief Read the next chunk, upto B bytes.
 *
 *  Operation carried out by the Workers.
 *
 *  \param id Workers identification
 *  \param chunk, the Worker's chunk reference
 *  \param chunkCounter, the Worker's chunkCounter reference, represents the size of the chunk.
 *  \param fileId, the Worker's fileId reference, indicates the current file that the chunk belongs to.
 *
 *  \return 1 if there is more work to do. Otherwise 0.
 *
 */
int getTextChunk(unsigned int id, char chunk[B], int *chunkCounter, int *fileId) {
  
    unsigned char c;
    *chunkCounter = 0;
    if ((status[id] = pthread_mutex_lock( & accessCR)) != 0) /* enter monitor */ {
        errno = status[id]; /* save error in errno */
        perror("error on entering monitor(CF)");
        status[id] = EXIT_FAILURE;
        pthread_exit( & status[id]);
    }
  
    /* Begin Critical Region */
    if (currentFile == NULL && fileIndex == -1) {
        openNextFile();
    }

    if (tempIndex > 0) {
        for (int i = 0; i < tempIndex; i++) {
            chunk[( *chunkCounter) ++] = temp[i];

        }
        chunk[( * chunkCounter) ++] = 0x20;
    }
    tempIndex = 0;
    if (currentFile != NULL)
        while ((fscanf(currentFile, "%c", & c)) != EOF) {
            unsigned char normalizedChar = formatChar(c, currentFile);
            if (isWhitespaceOrSeparationOrPunctuationSymbol(normalizedChar)) {
                if (tempIndex == 0) {
                    continue;
                }
                if ( * chunkCounter + tempIndex + 1 >= B)
                    break;
                for (int i = 0; i < tempIndex; i++)
                    chunk[( *chunkCounter) ++] = temp[i];
                chunk[( * chunkCounter) ++] = 0x20;
                tempIndex = 0;
            } else
                temp[tempIndex++] = normalizedChar;

        }
    if ( *chunkCounter + tempIndex < B) {
        for (int i = 0; i < tempIndex; i++)
            chunk[( *chunkCounter) ++] = temp[i];
        tempIndex = 0;
    }

    if (!( *chunkCounter) && fileIndex + 1 < nFiles) {
        openNextFile();
        * chunkCounter = -1;
    } else if (!(*chunkCounter) && currentFile != NULL) { // CLose everything
        closeFile();
    }

    *fileId = fileIndex;

    /* End Critical Region */
    if ((status[id] = pthread_mutex_unlock( & accessCR)) != 0) /* exit monitor */ {
        errno = status[id]; /* save error in errno */
        perror("error on exiting monitor(CF)");
        status[id] = EXIT_FAILURE;
        pthread_exit( & status[id]);
    }

    return *chunkCounter; 
}

/** saveResults
 *  \brief Saves the results of the chunk.
 *
 *  Operation carried out by the Workers.
 *
 *  \param id Workers identification.
 *  \param temp_count, the Worker's count reference, represents the number of words encountered.
 *  \param temp_V_counters, the Worker's V_counters reference, represents the frequency of vowels within a word size appeared in the chunk.
 *  \param temp_counters, the Worker's counters reference, represents the frequency of word sizes appeared in the chunk.
 *  \param temp_largestWord, the Worker's largestWord reference, indicates the largestWord encountered.
 *  \param fileId, the Worker's fileId reference, indicates the current file that the chunk belongs to.
 *
 *  \return 1 if there is more work to do. Otherwise 0.
 *
 */
void saveResults(int id, int *temp_count, int temp_V_counters[50][50], int temp_counters[50], int *temp_largestWord, int *fileId) {

    if ((status[id] = pthread_mutex_lock( &accessCR)) != 0) /* enter monitor */ {
        errno = status[id]; /* save error in errno */
        perror("error on entering monitor(CF)");
        status[id] = EXIT_FAILURE;
        pthread_exit( & status[id]);
    }

    /* Begin Critical Region */

    results[ *fileId].count = results[ *fileId].count + * temp_count;
    if ( *temp_largestWord > results[ *fileId].largestWord)
        results[ * fileId].largestWord = *temp_largestWord;
    for (int i = 0; i < 50; i++) {
        results[ *fileId].counters[i] = results[ * fileId].counters[i] + temp_counters[i];
        for (int j = 0; j < 50; j++)
            results[ *fileId].V_counters[i][j] = results[ *fileId].V_counters[i][j] + temp_V_counters[i][j];
    }
    *temp_count = 0;
    *temp_largestWord = 0;
    for (int i = 0; i < 50; i++) {
        temp_counters[i] = 0;
        for (int j = 0; j < 50; j++)
            temp_V_counters[i][j] = 0;
    }

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

    for (int k = 0; k < nFiles; k++) {
        printf("File name: %s\nTotal number of words = %i\nWord Length\n", results[k].filename, results[k].count);
        for (int i = 1; i <= results[k].largestWord; i++) {
            printf("\t%i", i);
        }
        printf("\n");
        for (int i = 1; i <= results[k].largestWord; i++) {
            printf("\t%i", results[k].counters[i]);
        }
        printf("\n");

        for (int i = 1; i <= results[k].largestWord; i++) {
            printf("\t%0.2f", (results[k].counters[i] * 100 / (float) results[k].count));
        }

        for (int i = 0; i <= results[k].largestWord; i++) {
            printf("\n");
            printf("%i", i);
            for (int j = 1; j <= results[k].largestWord; j++) {
                if (i <= j)
                    if (results[k].counters[j] > 0)
                        printf("\t%0.1f", results[k].V_counters[i][j] * 100 / (float) results[k].counters[j]);
                    else
                        printf("\t%0.1f", (float) 0);
                else
                    printf("\t-");
            }

        }

        printf("\n");
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
    }
    nFiles = numOfFiles;
}