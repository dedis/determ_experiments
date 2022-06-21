#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <math.h>
#include <gmp.h>
#include <mpfr.h>

#define WARMUP_COUNT 1000000
#define EXEC_COUNT 1000000
#define INPUT_COUNT 1000

void print_measurements(char* op, long *measurements, char* prefix)
{
    char *ext = ".csv";
    char *fname = malloc(strlen(prefix) + strlen(op) + strlen(ext) + 1);
    strcpy(fname, prefix);
    strcat(fname, op);
    strcat(fname, ext);
    printf("%s\n", fname);
    FILE *fp;
    fp = fopen(fname, "w+");
    if(fp == NULL)
    {
        printf("Error!");
        exit(1);
    }
    for (int i = 0; i < EXEC_COUNT; i++) {
        /*printf("%ld\n", measurements[i]);*/
        fprintf(fp, "%ld\n", measurements[i]);
    }
    fclose(fp);
    free(fname);
}

void run_experiment(char *op)
{
    int idx;
    double val;
    mpfr_t m_val;

    double *xs = (double*) malloc(sizeof(double) * INPUT_COUNT);
    double *ys = (double*) malloc(sizeof(double) * INPUT_COUNT);
    mpfr_t *mxs = (mpfr_t*) malloc(sizeof(mpfr_t) * INPUT_COUNT);
    mpfr_t *mys = (mpfr_t*) malloc(sizeof(mpfr_t) * INPUT_COUNT);

    struct timespec start, end;
    long *mathTimes = (long*) malloc(sizeof(long) * EXEC_COUNT);
    long *mpfrTimes = (long*) malloc(sizeof(long) * EXEC_COUNT);

    time_t t;
    srand((unsigned) time(&t));
    for (int i = 0; i < INPUT_COUNT; i++) {
        xs[i] = (double) rand() / RAND_MAX;
        ys[i] = (double) rand() / RAND_MAX;
        mpfr_init_set_d(mxs[i], xs[i], MPFR_RNDN);
        mpfr_init_set_d(mys[i], ys[i], MPFR_RNDN);
    }
    mpfr_init(m_val);

    if (strcmp(op, "add") == 0)
    {
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            val = xs[idx] + ys[idx];
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mathTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mathTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            mpfr_add(m_val, mxs[idx], mys[idx], MPFR_RNDN);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mpfrTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mpfrTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
    }
    else if (strcmp(op, "sub") == 0)
    {
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            val = xs[idx] - ys[idx];
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mathTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mathTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            mpfr_sub(m_val, mxs[idx], mys[idx], MPFR_RNDN);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mpfrTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mpfrTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
    }
    else if (strcmp(op, "mul") == 0)
    {
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            val = xs[idx] * ys[idx];
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mathTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mathTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            mpfr_mul(m_val, mxs[idx], mys[idx], MPFR_RNDN);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mpfrTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mpfrTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
    }
    else if (strcmp(op, "div") == 0)
    {
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            val = xs[idx] / ys[idx];
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mathTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mathTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            mpfr_div(m_val, mxs[idx], mys[idx], MPFR_RNDN);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mpfrTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mpfrTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
    }
    else if (strcmp(op, "sqrt") == 0)
    {
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            val = sqrt(xs[idx]);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mathTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mathTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            mpfr_sqrt(m_val, mxs[idx], MPFR_RNDN);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mpfrTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mpfrTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
    }
    else if (strcmp(op, "exp") == 0)
    {
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            val = exp(xs[idx]);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mathTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mathTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            mpfr_exp(m_val, mxs[idx], MPFR_RNDN);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mpfrTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mpfrTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
    }
    else if (strcmp(op, "pow") == 0)
    {
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            val = pow(xs[idx], ys[idx]);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mathTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mathTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            mpfr_pow(m_val, mxs[idx], mys[idx], MPFR_RNDN);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mpfrTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mpfrTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
    }
    else if (strcmp(op, "log") == 0)
    {
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            val = log(xs[idx]);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mathTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mathTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            mpfr_log(m_val, mxs[idx], MPFR_RNDN);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mpfrTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mpfrTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
    }
    else if (strcmp(op, "sin") == 0)
    {
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            val = sin(xs[idx]);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mathTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mathTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            mpfr_sin(m_val, mxs[idx], MPFR_RNDN);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mpfrTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mpfrTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
    }
    else if (strcmp(op, "cos") == 0)
    {
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            val = cos(xs[idx]);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mathTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mathTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            mpfr_cos(m_val, mxs[idx], MPFR_RNDN);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mpfrTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mpfrTimes[i] += (end.tv_nsec - start.tv_nsec);
            /*printf("%ld %ld\n", end.tv_nsec, start.tv_nsec);*/
        }
    }
    else if (strcmp(op, "tan") == 0)
    {
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            val = tan(xs[idx]);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mathTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mathTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
        for (int i = 0; i < EXEC_COUNT; i++) {
            idx = i % INPUT_COUNT;
            clock_gettime(CLOCK_MONOTONIC_RAW, &start);
            mpfr_tan(m_val, mxs[idx], MPFR_RNDN);
            clock_gettime(CLOCK_MONOTONIC_RAW, &end);
            mpfrTimes[i] = (end.tv_sec - start.tv_sec) * 1e9;
            mpfrTimes[i] += (end.tv_nsec - start.tv_nsec);
        }
    }
    else
    {
        printf("Invalid argument\n");
    }
    print_measurements(op, mathTimes, "./prelim/c/math_");
    print_measurements(op, mpfrTimes, "./prelim/c/mpfr_");
    // Clean-up
    free(xs);
    free(ys);
    free(mathTimes);
    free(mpfrTimes);
    for (int i = 0; i < INPUT_COUNT; i++) {
        mpfr_clear(mxs[i]);
        mpfr_clear(mys[i]);
    }
    mpfr_clear(m_val);
    mpfr_free_cache();
}

int main(int argc, char **argv)
{
    if (argc != 2) {
        printf("Error: missing command line arguments\n");
        return 1;
    }
    char *op = argv[1];
    run_experiment(op);
    return 0;
}
