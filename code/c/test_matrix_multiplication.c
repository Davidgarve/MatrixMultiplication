#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/resource.h>

void matrix_multiplication(int n);  

void print_memory_usage() {
    struct rusage usage;
    getrusage(RUSAGE_SELF, &usage);
    long memory_usage = usage.ru_maxrss; 
    printf("Uso de memoria: %.2f MB\n", memory_usage / 1024.0);
}

void test_matrix_multiplication() {
    int sizes[] = {10, 50, 100, 200, 500, 1000};  
    for (int i = 0; i < sizeof(sizes) / sizeof(sizes[0]); i++) {
        int size = sizes[i];

        struct rusage usage_before, usage_after;
        getrusage(RUSAGE_SELF, &usage_before);
        long memory_before = usage_before.ru_maxrss; 

        clock_t start = clock();  
        matrix_multiplication(size);
        clock_t end = clock();  

        getrusage(RUSAGE_SELF, &usage_after);
        long memory_after = usage_after.ru_maxrss; 

        long memory_used = memory_after - memory_before; 
        double memory_used_mb = memory_used / 1024.0; 

        double time_taken = ((double)(end - start)) / CLOCKS_PER_SEC;  

        printf("Tamaño de matriz: %dx%d, Tiempo de ejecución: %f segundos, Uso de memoria: %.2f MB\n",
               size, size, time_taken, memory_used_mb);
    }
}

int main() {
    test_matrix_multiplication();
    return 0;
}
