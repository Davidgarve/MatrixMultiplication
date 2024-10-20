import matrix_multiplication
import pytest
from memory_profiler import memory_usage


@pytest.mark.parametrize("n", [10, 50, 100, 200, 500, 1000])
def test_matrix_multiplication(benchmark, n):
    print(f"\nTesting matrix multiplication for size: {n}x{n}")
    memory_before = memory_usage()[0]
    result = benchmark.pedantic(matrix_multiplication.matrix_multiplication, args=(n,), warmup_rounds=5, iterations=5,
                                rounds=5)

    assert result is not None
    memory_after = memory_usage()[0]
    print(f"Memory Used: {memory_after - memory_before:.2f} MiB\n")
