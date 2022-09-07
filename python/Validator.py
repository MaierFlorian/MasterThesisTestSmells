from smtplib import LMTP_PORT
import pandas as pd


def loadCSVFiles(checkedFilePath, targetFilePath):
    try:
        if(checkedFilePath.endswith(".csv")):
            checkedCSV = pd.read_csv(checkedFilePath, sep=';')
        elif(checkedFilePath.endswith(".xlsx")):
            checkedCSV = pd.read_excel(io=checkedFilePath, engine='openpyxl')
        else:
            print("ERROR! File Format is wrong!")
        targetCSV = pd.read_csv(targetFilePath, sep=',')
    except Exception as e:
        print(e)
        print("ERROR! Could not load csv files. Maybe the path is wrong!")
        return
    return checkedCSV, targetCSV
    
def compareCSVs(checkedCSV, targetCSV):
    # get rows
    checkedRows = []
    targetRows = []
    for index, row in checkedCSV.iterrows():
        rowDict = {"file": row[1] + ".java", "method": row[2], "AT": row[3], "#statements": row[4], "CTL": row[5], "#assertions": row[7].rsplit("/", 1)[1], "RGT": row[6]}
        checkedRows.append(rowDict)
    for index, row in targetCSV.iterrows():
        rowDict = {"file": row.get("Path").rsplit("\\", 1)[1], "method": row.get("Method"), "AT": row.get("Anonymous Test"), "#statements": row.get("#statements"), 
        "CTL": row.get("Conditional Test Logic"), "#assertions": row.get("#assertions"), "RGT": row.get("Rotten Green Test")}
        targetRows.append(rowDict)

    amountTargetRows = len(targetRows)
    correctAT = 0
    falseAT = 0
    AT_TP = 0
    AT_FP = 0
    AT_FN = 0

    correctAmountStatements = 0
    falseAmountStatements = 0
    LT_TP = 0
    LT_FP = 0
    LT_FN = 0

    correctCTL = 0
    falseCTL = 0
    CTL_TP = 0
    CTL_FP = 0
    CTL_FN = 0

    correctAmountAssertions = 0
    falseAmountAssertions = 0
    AR_TP = 0
    AR_FP = 0
    AR_FN = 0

    correctRGT = 0
    falseRGT = 0
    RGT_TP = 0
    RGT_FP = 0
    RGT_FN = 0

    foundMatches = 0
    # compare rows
    for tr in range(len(targetRows)):
        found = False
        for cr in range(len(checkedRows)):
            if checkedRows[cr].get("file") == targetRows[tr].get("file") and checkedRows[cr].get("method") == targetRows[tr].get("method"):
                # AT
                if (checkedRows[cr].get("AT") == targetRows[tr].get("AT")) or (checkedRows[cr].get("AT") == 0 and targetRows[tr].get("AT") == 1) or (checkedRows[cr].get("AT") == 1 and targetRows[tr].get("AT") == 0):
                    correctAT += 1
                else:
                    falseAT += 1
                if checkedRows[cr].get("AT") > 1 and targetRows[tr].get("AT") > 1:
                    AT_TP += 1
                elif checkedRows[cr].get("AT") <= 1 and targetRows[tr].get("AT") > 1:
                    AT_FP += 1
                elif checkedRows[cr].get("AT") > 1 and targetRows[tr].get("AT") <= 1:
                    AT_FN += 1

                # LT
                if checkedRows[cr].get("#statements") == targetRows[tr].get("#statements"):
                    correctAmountStatements += 1
                else:
                    falseAmountStatements += 1
                if checkedRows[cr].get("#statements") > 13 and targetRows[tr].get("#statements") > 13:
                    LT_TP += 1
                elif checkedRows[cr].get("#statements") <= 13 and targetRows[tr].get("#statements") > 13:
                    LT_FP += 1
                elif checkedRows[cr].get("#statements") > 13 and targetRows[tr].get("#statements") <= 13:
                    LT_FN += 1

                # CTL
                if checkedRows[cr].get("CTL") == targetRows[tr].get("CTL"):
                    correctCTL += 1
                else:
                    falseCTL += 1
                if checkedRows[cr].get("CTL") > 0 and targetRows[tr].get("CTL") > 0:
                    CTL_TP += 1
                elif checkedRows[cr].get("CTL") == 0 and targetRows[tr].get("CTL") > 0:
                    CTL_FP += 1
                elif checkedRows[cr].get("CTL") > 0 and targetRows[tr].get("CTL") == 0:
                    CTL_FN += 1

                # AR
                if str(checkedRows[cr].get("#assertions")) == str(targetRows[tr].get("#assertions")):
                    correctAmountAssertions += 1
                else:
                    falseAmountAssertions += 1
                if int(checkedRows[cr].get("#assertions")) > 1 and int(targetRows[tr].get("#assertions")) > 1:
                    AR_TP += 1
                elif int(checkedRows[cr].get("#assertions")) <= 1 and int(targetRows[tr].get("#assertions")) > 1:
                    AR_FP += 1
                elif int(checkedRows[cr].get("#assertions")) > 1 and int(targetRows[tr].get("#assertions")) <= 1:
                    AR_FN += 1

                # RGT
                if checkedRows[cr].get("RGT") == targetRows[tr].get("RGT"):
                    correctRGT += 1
                else:
                    falseRGT += 1
                if checkedRows[cr].get("RGT") > 0 and targetRows[tr].get("RGT") > 0:
                    RGT_TP += 1
                elif checkedRows[cr].get("RGT") == 0 and targetRows[tr].get("RGT") > 0:
                    RGT_FP += 1
                elif checkedRows[cr].get("RGT") > 0 and targetRows[tr].get("RGT") == 0:
                    RGT_FN += 1

                foundMatches += 1
                break

    print("###################################################")
    print("Found matches:\t\t%f" % (foundMatches))
    print("----------------------------")
    print("Anonymous Test:\nAccuracy:\t%f\nPrecision:\t%f\nRecall:\t\t%f\nF1-score:\t%f" % ((correctAT / foundMatches), (AT_TP / (AT_TP + AT_FP)), (AT_TP / (AT_TP + AT_FN)), (2*((AT_TP / (AT_TP + AT_FN)) * (AT_TP / (AT_TP + AT_FP))) / ((AT_TP / (AT_TP + AT_FN)) + (AT_TP / (AT_TP + AT_FP))))))
    print("----------------------------")
    print("Amount Statements:\nAccuracy:\t%f\nPrecision:\t%f\nRecall:\t\t%f\nF1-score:\t%f" % ((correctAmountStatements / foundMatches), (LT_TP / (LT_TP + LT_FP)), (LT_TP / (LT_TP + LT_FN)), 2*((LT_TP / (LT_TP + LT_FN)) * (LT_TP / (LT_TP + LT_FP))) / ((LT_TP / (LT_TP + LT_FN)) + (LT_TP / (LT_TP + LT_FP)))))
    print("----------------------------")
    print("Conditional Test Logic:\nAccuracy:\t%f\nPrecision:\t%f\nRecall:\t\t%f\nF1-score:\t%f" % ((correctCTL / foundMatches), (CTL_TP / (CTL_TP + CTL_FP)), (CTL_TP / (CTL_TP + CTL_FN)), 2*((CTL_TP / (CTL_TP + CTL_FN)) * (CTL_TP / (CTL_TP + CTL_FP))) / ((CTL_TP / (CTL_TP + CTL_FN)) + (CTL_TP / (CTL_TP + CTL_FP)))))
    print("----------------------------")
    print("Assertion Roulette:\nAccuracy:\t%f\nPrecision:\t%f\nRecall:\t\t%f\nF1-score:\t%f" % ((correctAmountAssertions / foundMatches), (AR_TP / (AR_TP + AR_FP)), (AR_TP / (AR_TP + AR_FN)), 2*((AR_TP / (AR_TP + AR_FN)) * (AR_TP / (AR_TP + AR_FP))) / ((AR_TP / (AR_TP + AR_FN)) + (AR_TP / (AR_TP + AR_FP)))))
    print("----------------------------")
    print("Rotten Green Test:\nAccuracy:\t%f\nPrecision:\t%f\nRecall:\t\t%f\nF1-score:\t%f" % ((correctRGT / foundMatches), (RGT_TP / (RGT_TP + RGT_FP)), (RGT_TP / (RGT_TP + RGT_FN)), 2*((RGT_TP / (RGT_TP + RGT_FN)) * (RGT_TP / (RGT_TP + RGT_FP))) / ((RGT_TP / (RGT_TP + RGT_FN)) + (RGT_TP / (RGT_TP + RGT_FP)))))
    print("###################################################")


if __name__ == '__main__':
    print("Path to manually checked csv file: ")
    checkedFilePath = input()
    print("Path to target csv file: ")
    targetFilePath = input()
    checkedCSV, targetCSV = loadCSVFiles(checkedFilePath, targetFilePath)
    compareCSVs(checkedCSV, targetCSV)