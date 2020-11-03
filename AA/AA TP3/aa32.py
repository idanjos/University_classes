import gentext as gt
import argparse
import counter as c


# Argument parser
def parseArguments():
    # Create argument parser
    parser = argparse.ArgumentParser(add_help=False)
    parser.add_argument("letters", help="Num. of letters.", type=int)

    # Optional arguments
    parser.add_argument("-help", "--help", action="help",
                        help="-[letter] [prob of letter, default is 1. 2 appear twice as much]")
    parser.add_argument("-a", "--a", help="Probability of A", type=int, default=1)
    parser.add_argument("-b", "--b", help="Probability of B", type=int, default=1)
    parser.add_argument("-c", "--c", help="Probability of C", type=int, default=1)
    parser.add_argument("-d", "--d", help="Probability of D", type=int, default=1)
    parser.add_argument("-e", "--e", help="Probability of E", type=int, default=1)
    parser.add_argument("-f", "--f", help="Probability of F", type=int, default=1)
    parser.add_argument("-g", "--g", help="Probability of G", type=int, default=1)
    parser.add_argument("-h", "--h", help="Probability of H", type=int, default=1)
    parser.add_argument("-i", "--i", help="Probability of I", type=int, default=1)
    parser.add_argument("-j", "--j", help="Probability of J", type=int, default=1)
    parser.add_argument("-k", "--k", help="Probability of K", type=int, default=1)
    parser.add_argument("-l", "--l", help="Probability of L", type=int, default=1)
    parser.add_argument("-m", "--m", help="Probability of M", type=int, default=1)
    parser.add_argument("-n", "--n", help="Probability of N", type=int, default=1)
    parser.add_argument("-o", "--o", help="Probability of O", type=int, default=1)
    parser.add_argument("-p", "--p", help="Probability of P", type=int, default=1)
    parser.add_argument("-q", "--q", help="Probability of Q", type=int, default=1)
    parser.add_argument("-r", "--r", help="Probability of R", type=int, default=1)
    parser.add_argument("-s", "--s", help="Probability of S", type=int, default=1)
    parser.add_argument("-t", "--t", help="Probability of T", type=int, default=1)
    parser.add_argument("-u", "--u", help="Probability of U", type=int, default=1)
    parser.add_argument("-v", "--v", help="Probability of V", type=int, default=1)
    parser.add_argument("-w", "--w", help="Probability of W", type=int, default=1)
    parser.add_argument("-x", "--x", help="Probability of X", type=int, default=1)
    parser.add_argument("-y", "--y", help="Probability of Y", type=int, default=1)
    parser.add_argument("-z", "--z", help="Probability of Z", type=int, default=1)
    parser.add_argument("-kappa", "--kappa", help="Kappa parameter", type=int, default=10)
    parser.add_argument("-err", "--error", help="Relative error parameter", type=float, default=0.0001)
    parser.add_argument("-factor", "--factor", help="Factor parameter", type=float, default=0.999)

    # Print version
    parser.add_argument("--version", action="version", version='%(prog)s - Version 1.0')

    # Parse arguments
    args = parser.parse_args()

    return args


def scoreCalc(ec, counter_i, smallest):
    divider = 0
    toppart = 0

    for i in range(0, smallest):
        divider += 1
        for ec_obj in ec[0]:
            if ec_obj[1] == counter_i[0][i][1]:
                if abs(counter_i[0][i][0] - ec_obj[0]) < ec_obj[0]:
                    toppart += (ec_obj[0] - abs(counter_i[0][i][0] - ec_obj[0])) / ec_obj[0]
               
                break
    return toppart / divider


def accuracy(ec, counter_i, smallets):
    exists = 0
    for i in range(0, smallets):
        if any(e[1] == counter_i[i][-1] for e in ec[:smallets]):
            exists = exists + 1

    if exists == 0:
        return 0
    return (exists/smallets) * 100


def main():
    # Argument parsing
    args = parseArguments()

    # Counter class, holds all 4 counters
    counter = c.Counter()

    # Parse inputted weights for generator
    weights = [args.__dict__[chr(97 + x)] / 26 for x in range(0, 26)]

    # Initializing and generating text
    gen = gt.GenText()
    dataset = gen.generate(args.letters, weights)
    # All 4 counters plus the exact counter
    ec = counter.ExactCount(dataset)
    mg = counter.MisraGries(dataset, args.kappa)
    mm = counter.MankuMotwani(dataset, args.kappa)
    m = counter.Metwally(dataset, args.kappa)
    cms = counter.Count_Min_Sketch(dataset, args.error, args.factor)
    smallest = 3

    print("Exact Count")
    print((sorted(ec[0][:smallest],reverse=True), ec[1], ec[2]))

    print("MisraGries Score")
    print(scoreCalc(ec, mg, smallest))
    print("Accuracy: " + str(accuracy(ec[0], mg[0], smallest)) + "%")
    print((mg[0][:smallest], mg[1], mg[2]))

    print("MankuMotwani Score")
    print(scoreCalc(ec, mm, smallest))
    print("Accuracy: " + str(accuracy(ec[0], mm[0], smallest)) + "%")
    print((mm[0][:smallest], mm[1], mm[2]))

    print("Metwally Score")
    print(scoreCalc(ec, m, smallest))
    print("Accuracy: " + str(accuracy(ec[0], m[0], smallest)) + "%")
    print((m[0][:smallest], m[1], m[2]))

    print("Count_Min_Sketch Score")
    print(scoreCalc(ec, cms, smallest))
    print("Accuracy: " + str(accuracy(ec[0], cms[0], smallest)) + "%")
    print((cms[0][:smallest], cms[1], cms[2]))


if __name__ == "__main__":
    main()