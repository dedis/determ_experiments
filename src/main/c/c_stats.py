import sys
import pandas as pd
from os.path import join

BASE_DIR = "./prelim/c/"
OUT_DIR = join(BASE_DIR, "stats/")
MATH = "math"
MPFR = "mpfr"

def compute_stats(fname):
    in_math = join(BASE_DIR, MATH + "_" + fname + ".csv")
    in_mpfr = join(BASE_DIR, MPFR + "_" + fname + ".csv")
    # outfile = join(OUT_DIR, fname + ".txt")
    outfile = join(OUT_DIR, fname + ".csv")

    fd_math = pd.read_csv(in_math, header=None, names=['time'])
    fd_mpfr = pd.read_csv(in_mpfr, header=None, names=['time'])
    m_vals = [fd_math['time'].mean(), fd_math['time'].std()]
    mpfr_vals = [fd_mpfr['time'].mean(), fd_mpfr['time'].std()]

    f = open(outfile, "w")
    f.write("%s,%s,%s\n" % ("op", MATH, MPFR))
    f.write("%s,%f (%f),%f (%f)\n" % (fname, m_vals[0], m_vals[1], mpfr_vals[0],
        mpfr_vals[1]))

    # print("%s %f (%f) %f (%f)" % (fname, m_vals[0], m_vals[1], mpfr_vals[0], mpfr_vals[1]))

def main():
    args = sys.argv
    if len(args) != 2:
        sys.exit("Missing argument")
    compute_stats(sys.argv[1])

if __name__ == "__main__":
    main()
