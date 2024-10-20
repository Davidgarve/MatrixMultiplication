package org.example;

import org.openjdk.jmh.annotations.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@State(Scope.Thread)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
public class MatrixMultiplicationBenchmarking {

	@Param({"10", "50", "100", "200", "500", "1000"})
	private int n;

	private double totalExecutionTime;
	private double memoryUsedDuringFork;

	@State(Scope.Thread)
	public static class Operands {
		public double[][] a;
		public double[][] b;

		@Setup
		public void setup(MatrixMultiplicationBenchmarking benchmarking) {
			Random random = new Random();
			a = new double[benchmarking.n][benchmarking.n];
			b = new double[benchmarking.n][benchmarking.n];

			System.out.println("Starting benchmark for matrix size: " + benchmarking.n);

			for (int i = 0; i < benchmarking.n; i++) {
				for (int j = 0; j < benchmarking.n; j++) {
					a[i][j] = random.nextDouble();
					b[i][j] = random.nextDouble();
				}
			}
		}
	}

	@Setup(Level.Trial)
	public void setupTrial() {
		totalExecutionTime = 0;
		memoryUsedDuringFork = 0;
	}

	@Benchmark
	public void multiplication(Operands operands) {
		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

		MemoryUsage beforeMemory = memoryMXBean.getHeapMemoryUsage();
		long beforeMemoryUsed = beforeMemory.getUsed();

		long startTime = System.nanoTime();

		new MatrixMultiplication().execute(operands.a, operands.b);

		long endTime = System.nanoTime();

		MemoryUsage afterMemory = memoryMXBean.getHeapMemoryUsage();
		long afterMemoryUsed = afterMemory.getUsed();

		long memoryUsed = afterMemoryUsed - beforeMemoryUsed;
		memoryUsedDuringFork = (double) memoryUsed / (1024 * 1024);

		double executionTime = (endTime - startTime) / 1e6;

		totalExecutionTime += executionTime;
	}

	@TearDown(Level.Trial)
	public void tearDownTrial() {
		if (totalExecutionTime > 0) {
			System.out.printf("\nMemory used during fork: %.2f MB%n", memoryUsedDuringFork);
		}
	}
}
