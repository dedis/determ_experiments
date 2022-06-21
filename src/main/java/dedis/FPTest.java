package dedis.fp;

import com.google.common.math.Stats;
import org.kframework.mpfr.BigFloat;
import org.kframework.mpfr.BinaryMathContext;

import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.Random;

import net.dclausen.microfloat.MicroDouble;

public class FPTest {

    static final int WARMUP_COUNT = 1000000;
    static final int EXEC_COUNT = 1000000;
    static final int INPUT_COUNT = 1000;
    static final String EXP_PREFIX = "/Users/alp/workspace/dedis/determ-sandbox/experiments/prelim/";
    static final String STATS_PREFIX = EXP_PREFIX + "java/";
    static final String MATH = "math";
    static final String SMATH = "smath";
    static final String MPFR = "mpfr";
    static final String MF = "mf";

    public static void logPrimitiveStats(String fname, long[] mathMeas, long[] mpfrMeas, long[] mfMeas) throws IOException {
        String out = STATS_PREFIX + fname + ".csv";
        Stats statMath = Stats.of(mathMeas);
        Stats statMpfr = Stats.of(mpfrMeas);
        Stats statMf = Stats.of(mfMeas);
        FileWriter writer = new FileWriter(out);
        writer.write(String.format("%s, %.1f (%f), %.1f (%f), %.1f (%f)\n",
                fname,
                statMath.mean(),
                statMath.sampleStandardDeviation(),
                statMpfr.mean(),
                statMpfr.sampleStandardDeviation(),
                statMf.mean(),
                statMf.sampleStandardDeviation()));
        writer.close();
    }

    public static void logStats(String fname, long[] mathMeas, long[] smathMeas, long[] mpfrMeas, long[] mfMeas) throws IOException {
        String out = STATS_PREFIX + fname + ".csv";
        Stats statMath = Stats.of(mathMeas);
        Stats statSmath = Stats.of(smathMeas);
        Stats statMpfr = Stats.of(mpfrMeas);
        Stats statMf = Stats.of(mfMeas);
        FileWriter writer = new FileWriter(out);
        writer.write(String.format("%s, %.1f (%f), %.1f (%f), %.1f (%f), %.1f (%f)\n",
                fname,
                statMath.mean(),
                statMath.sampleStandardDeviation(),
                statSmath.mean(),
                statSmath.sampleStandardDeviation(),
                statMpfr.mean(),
                statMpfr.sampleStandardDeviation(),
                statMf.mean(),
                statMf.sampleStandardDeviation()));
        writer.close();
    }

    public static void logMeasurements(String outfile, long[] measurements) throws IOException {
        FileWriter writer = new FileWriter(outfile);
        for (long m : measurements) {
            writer.write(m + "\n");
        }
        writer.close();
    }

    public static void runExperiment(String fname) throws IOException {
        int idx;
        long start;
        long end;
        double val;
        boolean isPrimitive = false;

        final double[] xs = new double[INPUT_COUNT];
        final double[] ys = new double[INPUT_COUNT];
        final BigFloat[] bfXs = new BigFloat[INPUT_COUNT];
        final BigFloat[] bfYs = new BigFloat[INPUT_COUNT];
        final Random random = new Random();

        long lx, ly, lval;
        BigFloat bfVal;
        BinaryMathContext mc = BinaryMathContext.BINARY64.withRoundingMode(RoundingMode.HALF_EVEN);

        final long[] mathTimes = new long[EXEC_COUNT];
        final long[] smathTimes = new long[EXEC_COUNT];
        final long[] mpfrTimes = new long[EXEC_COUNT];
        final long[] mfTimes = new long[EXEC_COUNT];
        String outMath = EXP_PREFIX + MATH + "_" + fname + ".csv";
        String outSmath = EXP_PREFIX + SMATH + "_" + fname + ".csv";
        String outMpfr = EXP_PREFIX + MPFR + "_" + fname + ".csv";
        String outMf = EXP_PREFIX + MF + "_" + fname + ".csv";

        for (int i = 0; i < INPUT_COUNT; i++) {
            xs[i] = random.nextDouble();
            ys[i] = random.nextDouble();
            bfXs[i] = new BigFloat(xs[i], mc);
            bfYs[i] = new BigFloat(ys[i], mc);
        }
        switch (fname) {
            case "add":
                // Warmup for Math
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = xs[idx] + ys[idx];
                }
                // Measure Math
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = xs[idx] + ys[idx];
                    end = System.nanoTime();
                    mathTimes[i] = end - start;
                }
                // Warmup for MPFR
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    bfVal = bfXs[idx].add(bfYs[idx], mc);
                }
                // Measure MPFR
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    bfVal = bfXs[idx].add(bfYs[idx], mc);
                    end = System.nanoTime();
                    mpfrTimes[i] = end - start;
                }
                // Warmup for MicroFloat
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    ly = Double.doubleToLongBits(ys[idx]);
                    lval = MicroDouble.add(lx, ly);
                }
                // Measure MicroFloat
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    ly = Double.doubleToLongBits(ys[idx]);
                    start = System.nanoTime();
                    lval = MicroDouble.add(lx, ly);
                    end = System.nanoTime();
                    mfTimes[i] = end - start;
                }
                isPrimitive = true;
                break;
            case "sub":
                // Warmup for Math
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = xs[idx] - ys[idx];
                }
                // Measure Math
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = xs[idx] - ys[idx];
                    end = System.nanoTime();
                    mathTimes[i] = end - start;
                }
                // Warmup for MPFR
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    bfVal = bfXs[idx].subtract(bfYs[idx], mc);
                }
                // Measure MPFR
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    bfVal = bfXs[idx].subtract(bfYs[idx], mc);
                    end = System.nanoTime();
                    mpfrTimes[i] = end - start;
                }
                // Warmup for MicroFloat
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    ly = Double.doubleToLongBits(ys[idx]);
                    lval = MicroDouble.sub(lx, ly);
                }
                // Measure MicroFloat
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    ly = Double.doubleToLongBits(ys[idx]);
                    start = System.nanoTime();
                    lval = MicroDouble.sub(lx, ly);
                    end = System.nanoTime();
                    mfTimes[i] = end - start;
                }
                isPrimitive = true;
                break;
            case "mul":
                // Warmup for Math
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = xs[idx] * ys[idx];
                }
                // Measure Math
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = xs[idx] * ys[idx];
                    end = System.nanoTime();
                    mathTimes[i] = end - start;
                }
                // Warmup for MPFR
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    bfVal = bfXs[idx].multiply(bfYs[idx], mc);
                }
                // Measure MPFR
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    bfVal = bfXs[idx].multiply(bfYs[idx], mc);
                    end = System.nanoTime();
                    mpfrTimes[i] = end - start;
                }
                // Warmup for MicroFloat
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    ly = Double.doubleToLongBits(ys[idx]);
                    lval = MicroDouble.mul(lx, ly);
                }
                // Measure MicroFloat
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    ly = Double.doubleToLongBits(ys[idx]);
                    start = System.nanoTime();
                    lval = MicroDouble.mul(lx, ly);
                    end = System.nanoTime();
                    mfTimes[i] = end - start;
                }
                isPrimitive = true;
                break;
            case "div":
                // Warmup for Math
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = xs[idx] / ys[idx];
                }
                // Measure Math
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = xs[idx] / ys[idx];
                    end = System.nanoTime();
                    mathTimes[i] = end - start;
                }
                // Warmup for MPFR
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    bfVal = bfXs[idx].divide(bfYs[idx], mc);
                }
                // Measure MPFR
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    bfVal = bfXs[idx].divide(bfYs[idx], mc);
                    end = System.nanoTime();
                    mpfrTimes[i] = end - start;
                }
                // Warmup for MicroFloat
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    ly = Double.doubleToLongBits(ys[idx]);
                    lval = MicroDouble.div(lx, ly);
                }
                // Measure MicroFloat
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    ly = Double.doubleToLongBits(ys[idx]);
                    start = System.nanoTime();
                    lval = MicroDouble.div(lx, ly);
                    end = System.nanoTime();
                    mfTimes[i] = end - start;
                }
                isPrimitive = true;
                break;
            case "sqrt":
                // Warmup for Math
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = Math.sqrt(xs[idx]);
                }
                // Measure Math
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = Math.sqrt(xs[idx]);
                    end = System.nanoTime();
                    mathTimes[i] = end - start;
                }
                // Warmup for StrictMath
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = StrictMath.sqrt(xs[idx]);
                }
                // Measure StrictMath
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = StrictMath.sqrt(xs[idx]);
                    end = System.nanoTime();
                    smathTimes[i] = end - start;
                }
                // Warmup for MPFR
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    bfVal = bfXs[idx].sqrt(mc);
                }
                // Measure MPFR
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    bfVal = bfXs[idx].sqrt(mc);
                    end = System.nanoTime();
                    mpfrTimes[i] = end - start;
                }
                // Warmup for MicroFloat
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    lval = MicroDouble.sqrt(lx);
                }
                // Measure MicroFloat
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    ly = Double.doubleToLongBits(ys[idx]);
                    start = System.nanoTime();
                    lval = MicroDouble.sqrt(lx);
                    end = System.nanoTime();
                    mfTimes[i] = end - start;
                }
                break;
            case "log":
                // Warmup for Math
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = Math.log(xs[idx]);
                }
                // Measure Math
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = Math.log(xs[idx]);
                    end = System.nanoTime();
                    mathTimes[i] = end - start;
                }
                // Warmup for StrictMath
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = StrictMath.log(xs[idx]);
                }
                // Measure StrictMath
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = StrictMath.log(xs[idx]);
                    end = System.nanoTime();
                    smathTimes[i] = end - start;
                }
                // Warmup for MPFR
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    bfVal = bfXs[idx].log(mc);
                }
                // Measure MPFR
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    bfVal = bfXs[idx].log(mc);
                    end = System.nanoTime();
                    mpfrTimes[i] = end - start;
                }
                // Warmup for MicroFloat
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    lval = MicroDouble.log(lx);
                }
                // Measure MicroFloat
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    ly = Double.doubleToLongBits(ys[idx]);
                    start = System.nanoTime();
                    lval = MicroDouble.log(lx);
                    end = System.nanoTime();
                    mfTimes[i] = end - start;
                }
                break;
            case "exp":
                // Warmup for Math
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = Math.exp(xs[idx]);
                }
                // Measure Math
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = Math.exp(xs[idx]);
                    end = System.nanoTime();
                    mathTimes[i] = end - start;
                }
                // Warmup for StrictMath
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = StrictMath.exp(xs[idx]);
                }
                // Measure StrictMath
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = StrictMath.exp(xs[idx]);
                    end = System.nanoTime();
                    smathTimes[i] = end - start;
                }
                // Warmup for MPFR
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    bfVal = bfXs[idx].exp(mc);
                }
                // Measure MPFR
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    bfVal = bfXs[idx].exp(mc);
                    end = System.nanoTime();
                    mpfrTimes[i] = end - start;
                }
                // Warmup for MicroFloat
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    lval = MicroDouble.exp(lx);
                }
                // Measure MicroFloat
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    start = System.nanoTime();
                    lval = MicroDouble.exp(lx);
                    end = System.nanoTime();
                    mfTimes[i] = end - start;
                }
                break;
            case "pow":
                // Warmup for Math
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = Math.pow(xs[idx], ys[idx]);
                }
                // Measure Math
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = Math.pow(xs[idx], ys[idx]);
                    end = System.nanoTime();
                    mathTimes[i] = end - start;
                }
                // Warmup for StrictMath
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = StrictMath.pow(xs[idx], ys[idx]);
                }
                // Measure StrictMath
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = StrictMath.pow(xs[idx], ys[idx]);
                    end = System.nanoTime();
                    smathTimes[i] = end - start;
                }
                // Warmup for MPFR
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    bfVal = bfXs[idx].pow(bfYs[idx], mc);
                }
                // Measure MPFR
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    bfVal = bfXs[idx].pow(bfYs[idx], mc);
                    end = System.nanoTime();
                    mpfrTimes[i] = end - start;
                }
                // Warmup for MicroFloat
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    ly = Double.doubleToLongBits(ys[idx]);
                    lval = MicroDouble.pow(lx, ly);
                }
                // Measure MicroFloat
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    ly = Double.doubleToLongBits(ys[idx]);
                    start = System.nanoTime();
                    lval = MicroDouble.pow(lx, ly);
                    end = System.nanoTime();
                    mfTimes[i] = end - start;
                }
                break;
            case "sin":
                // Warmup for Math
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = Math.sin(xs[idx]);
                }
                // Measure Math
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = Math.sin(xs[idx]);
                    end = System.nanoTime();
                    mathTimes[i] = end - start;
                }
                // Warmup for StrictMath
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = StrictMath.sin(xs[idx]);
                }
                // Measure StrictMath
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = StrictMath.sin(xs[idx]);
                    end = System.nanoTime();
                    smathTimes[i] = end - start;
                }
                // Warmup for MPFR
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    bfVal = bfXs[idx].sin(mc);
                }
                // Measure MPFR
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    bfVal = bfXs[idx].sin(mc);
                    end = System.nanoTime();
                    mpfrTimes[i] = end - start;
                }
                // Warmup for MicroFloat
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    lval = MicroDouble.sin(lx);
                }
                // Measure MicroFloat
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    start = System.nanoTime();
                    lval = MicroDouble.sin(lx);
                    end = System.nanoTime();
                    mfTimes[i] = end - start;
                }
                break;
            case "cos":
                // Warmup for Math
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = Math.cos(xs[idx]);
                }
                // Measure Math
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = Math.cos(xs[idx]);
                    end = System.nanoTime();
                    mathTimes[i] = end - start;
                }
                // Warmup for StrictMath
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = StrictMath.cos(xs[idx]);
                }
                // Measure StrictMath
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = StrictMath.cos(xs[idx]);
                    end = System.nanoTime();
                    smathTimes[i] = end - start;
                }
                // Warmup for MPFR
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    bfVal = bfXs[idx].cos(mc);
                }
                // Measure MPFR
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    bfVal = bfXs[idx].cos(mc);
                    end = System.nanoTime();
                    mpfrTimes[i] = end - start;
                }
                // Warmup for MicroFloat
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    lval = MicroDouble.cos(lx);
                }
                // Measure MicroFloat
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    start = System.nanoTime();
                    lval = MicroDouble.cos(lx);
                    end = System.nanoTime();
                    mfTimes[i] = end - start;
                }
                break;
            case "tan":
                // Warmup for Math
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = Math.tan(xs[idx]);
                }
                // Measure Math
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = Math.tan(xs[idx]);
                    end = System.nanoTime();
                    mathTimes[i] = end - start;
                }
                // Warmup for StrictMath
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    val = StrictMath.tan(xs[idx]);
                }
                // Measure StrictMath
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    val = StrictMath.tan(xs[idx]);
                    end = System.nanoTime();
                    smathTimes[i] = end - start;
                }
                // Warmup for MPFR
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    bfVal = bfXs[idx].tan(mc);
                }
                // Measure MPFR
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    start = System.nanoTime();
                    bfVal = bfXs[idx].tan(mc);
                    end = System.nanoTime();
                    mpfrTimes[i] = end - start;
                }
                // Warmup for MicroFloat
                for (int i = 0; i < WARMUP_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    lval = MicroDouble.tan(lx);
                }
                // Measure MicroFloat
                for (int i = 0; i < EXEC_COUNT; i++) {
                    idx = i % INPUT_COUNT;
                    lx = Double.doubleToLongBits(xs[idx]);
                    start = System.nanoTime();
                    lval = MicroDouble.tan(lx);
                    end = System.nanoTime();
                    mfTimes[i] = end - start;
                }
                break;
            default:
                break;
        }

        if (isPrimitive) {
            logPrimitiveStats(fname, mathTimes, mpfrTimes, mfTimes);
        } else {
            logStats(fname, mathTimes, smathTimes, mpfrTimes, mfTimes);
        }
    }

    public static void main(String[] args) {
        int argCount = args.length;
        if (argCount != 1) {
            System.out.println("Missing argument");
            System.exit(1);
        }

        String funcName = args[0];
        try {
            runExperiment(funcName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
