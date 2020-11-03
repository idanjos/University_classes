/**
 *  \file producersWorkerss.c (implementation file)
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
#include <stdbool.h>
#include <pthread.h>
#include <math.h>
#include <time.h>
#include "textProcMonitor.h"
#include "probConst.h"

/* Allusion to internal functions */

static void printUsage(char * cmdName);
static void * worker(void * par);

int status[6];

/**
 *  \brief Main function.
 *
 *  \param argc number of words of the command line
 *  \param argv list of words of the command line
 *
 *  \return status of operation
 */

/** \brief array containing the path of the files */

int main(int argc, char * argv[]) {
    char * filePaths[10];
    /* process command line options */

    int opt; /* selected option */
    opterr = 0;
    do {
        switch ((opt = getopt(argc, argv, "f:h"))) {
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
  
    if (argc == 1 || argc > N + 1) {
        fprintf(stderr, "%s: invalid format\n", basename(argv[0]));
        printUsage(basename(argv[0]));
        return EXIT_FAILURE;
    }

    for (int i = 1; i < argc; i++)
        filePaths[i - 1] = argv[i];

    setFileNames(argc - 1, filePaths);
    pthread_t tIdWorker[T]; /* worker internal thread id array */
    unsigned int workerId[T]; /* worker application defined thread id array */
    int i; /* counting variable */
    int * status_p; /* pointer to execution status */
    double t0, t1; /* time limits */

    //Attributing ids
    for (i = 0; i < T; i++)
        workerId[i] = i;

    t0 = ((double) clock()) / CLOCKS_PER_SEC;

    /* generation of intervening entities threads */

    for (i = 0; i < T; i++)
        if (pthread_create( & tIdWorker[i], NULL, worker, & workerId[i]) != 0) /* thread Workers */ {
            perror("error on creating thread Workers");
            exit(EXIT_FAILURE);
        }

    /* waiting for the termination of the intervening entities threads */

    for (i = 0; i < T; i++) {
        if (pthread_join(tIdWorker[i], (void * ) & status_p) != 0) /* thread Workers */ {
            perror("error on waiting for thread customer");
            exit(EXIT_FAILURE);
        }
        printf("thread Workers, with id %u, has terminated: ", i);
        printf("its status was %d\n", * status_p);
    }
    printResults();
    t1 = ((double) clock()) / CLOCKS_PER_SEC;
    printf("\nElapsed time = %.6f s\n", t1 - t0);
    exit(EXIT_SUCCESS);

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
 *  \brief Function Worker.
 *
 *  Its role is to simulate the life cycle of a Worker.
 *
 *  \param par pointer to application defined Worker identification
 */

static void * worker(void * par) {
    /* Chunk reference */
    char chunk[B] = {0x20};

    /* Chunk length */
    int chunkCounter = 0;
    
    /* Number of words in current chunk */
    int count = 0;

    /* Frequency of words of each word length */
    int counters[50] = {0};

    /* Frequency of vowels in each word length */
    int V_counters[50][50];

    /* Largest word encountered */
    int largestWord = 0;

    /* Current file id */
    int fileId;
    
    /* Worker id */
    unsigned int id = * ((unsigned int * ) par); 
    
    /* get Next chunk */
    while (getTextChunk(id, chunk, & chunkCounter, & fileId)) {
        processChunk(chunk, chunkCounter, & count, V_counters, counters, & largestWord);
        saveResults(id, & count, V_counters, counters, & largestWord, & fileId);
    }
    status[id] = EXIT_SUCCESS;
    pthread_exit( & status[id]);
}
