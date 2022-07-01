from asyncio.windows_events import NULL
from contextlib import nullcontext
import pandas as pd


def loadCSVFiles(checkedFilePath, targetFilePath):
    try:
        checkedCSV = pd.read_csv(checkedFilePath)
        targetCSV = pd.read_csv(targetFilePath)
    except:
        print("ERROR! Could not load csv files. Maybe the path is wrong!")
        return
    return checkedCSV, targetCSV
    
def compareCSVs(checkedCSV, targetCSV):
    for index, row in checkedCSV.iterrows():
        print(row[0])


if __name__ == '__main__':
    print("Path to manually checked csv file: ")
    checkedFilePath = input()
    print("Path to target csv file: ")
    targetFilePath = input()
    checkedCSV, targetCSV = loadCSVFiles(checkedFilePath, targetFilePath)
    compareCSVs(checkedCSV, targetCSV)