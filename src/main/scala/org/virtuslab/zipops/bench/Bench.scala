package org.virtuslab.zipops.bench

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole

object Bench {

  @Benchmark
  def benchmark(hole: Blackhole): Unit = {
    hole.consume(5 * 7)
  }

}
