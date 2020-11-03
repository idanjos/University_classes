def main():
    # Argument parsing
    args = parseArguments()

    # Counter class, holds all 4 counters
    counter = c.Counter()

    # Parse inputted weights for generator
    weights = [args.__dict__[chr(97 + x)] / 26 for x in range(0, 26)]
    dic = dict()

    # criação da média de comparações para análise de resultados
    for j in range(1, 10):
        gen = gt.GenText()
        dataset = gen.generate(args.letters, weights)
        # All 4 counters plus the exact counter
        ec = counter.ExactCount(dataset)
        mg = counter.MisraGries(dataset, args.kappa)
        mm = counter.MankuMotwani(dataset, args.kappa)
        m = counter.Metwally(dataset, args.kappa)
        cms = counter.Count_Min_Sketch(dataset, args.error, args.factor)
        smallest = min([len(mg[0]), len(mm[0]), len(m[0]), len(cms[0])])

        # print("Exact Count")
        # print((ec[0][:smallest], ec[1], ec[2]))
        dic.setdefault('ec_size', []).append(ec[1])
        dic.setdefault('ec_time', []).append(ec[2])


        # print("MisraGries Score")

        mg_score = scoreCalc(ec, mg, smallest)
        mg_accuracy = accuracy(ec[0], mg[0], smallest)
        dic.setdefault('mg_score', []).append(mg_score)
        dic.setdefault('mg_accuracy', []).append(mg_accuracy)
        dic.setdefault('mg_size', []).append(mg[1])
        dic.setdefault('mg_time', []).append(mg[2])

        # print(mg_score)
        # print("Accuracy: " + str(mg_accuracy) + "%")
        # print((mg[0][:smallest], mg[1], mg[2]))

        # print("MankuMotwani Score")
        mm_score = scoreCalc(ec, mm, smallest)
        mm_accuracy = accuracy(ec[0], mm[0], smallest)
        dic.setdefault('mm_score', []).append(mm_score)
        dic.setdefault('mm_accuracy', []).append(mm_accuracy)
        dic.setdefault('mm_size', []).append(mm[1])
        dic.setdefault('mm_time', []).append(mm[2])

        # print(mm_score)
        # print("Accuracy: " + str(mm_accuracy) + "%")
        # print((mm[0][:smallest], mm[1], mm[2]))

        # print("Metwally Score")
        m_score = scoreCalc(ec, m, smallest)
        m_accuracy = accuracy(ec[0], m[0], smallest)
        dic.setdefault('m_score', []).append(m_score)
        dic.setdefault('m_accuracy', []).append(m_accuracy)
        dic.setdefault('m_size', []).append(m[1])
        dic.setdefault('m_time', []).append(m[2])
        # print(m_score)
        # print("Accuracy: " + str(m_accuracy) + "%")
        # print((m[0][:smallest], m[1], m[2]))

        cms_score = scoreCalc(ec, cms, smallest)
        cms_accuracy = accuracy(ec[0], cms[0], smallest)
        dic.setdefault('cms_score', []).append(cms_score)
        dic.setdefault('cms_accuracy', []).append(cms_accuracy)
        dic.setdefault('cms_size', []).append(cms[1])
        dic.setdefault('cms_time', []).append(cms[2])
        # print("Count_Min_Sketch Score")
        # print(cms_score)
        # print("Accuracy: " + str(cms_accuracy) + "%")
        # print((cms[0][:smallest], cms[1], cms[2]))

    # Initializing and generating text

    for key, value in dic.items():
        dic[key] = statistics.mean(value)

    pprint.pprint(dic