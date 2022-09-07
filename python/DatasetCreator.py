from github import Github
import os
import requests
from datetime import date
import base64
import pandas as pd

ACCESS_TOKEN = "######################"

g = Github(ACCESS_TOKEN)

def check_rate_limit():
    rate_limit = g.get_rate_limit()
    rate = rate_limit.search
    if rate.remaining == 0:
        print(f'You have 0/{rate.limit} API calls remaining. Reset time: {rate.reset}')
        return False
    else:
        print(f'You have {rate.remaining}/{rate.limit} API calls remaining')
        return True

def search_github(keywords, startFromIndex: int=89):
    
    query = '+'.join(keywords) + '+in:readme+in:description+license:mit+license:bsd-3-clause-clear+license:bsd-3-clause+license:gpl-3.0+license:cc-by-sa-4.0'
    result = g.search_repositories(query, 'stars', 'desc')
    repos_with_JunitTests = []
    result_dict = {"Name": [], "Link": [], "Accessed": [], "CreatedAt": [], "LastPush": [], "#Forks": [], "#Stars": [], "License": []}
 
    print(f'Found {result.totalCount} repo(s)')
    i = 0
    for repos in result:
        i += 1
        if (i<startFromIndex):
            continue
        try:
            includeTests = False
            testfiles = []
            path = "d:/MEGA/Masterarbeit/Dataset/%s/" % (repos.full_name)
            contents = repos.get_contents("")
            while contents:
                file_content = contents.pop(0)
                if file_content.type == "dir":
                    contents.extend(repos.get_contents(file_content.path))
                else:
                    if(file_content.path.lower().endswith("test.java")):
                        if("assert" in str(repos.get_contents(file_content.path).decoded_content).lower() and "junit" in str(repos.get_contents(file_content.path).decoded_content).lower()):
                            includeTests = True
                            try:
                                os.makedirs(path)
                            except OSError as error:
                                #print("Directory '%s' can not be created" % path)
                                pass
                            open('%s%s' % (path, file_content.path.rsplit('/', 1)[-1]), 'wb').write(repos.get_contents(file_content.path).decoded_content)
                            testfiles.append(file_content.path)
            if includeTests:
                file=open('%scontents.txt' % (path),'w')
                file.writelines("You can find the following files in %s\n" % (repos.clone_url))
                for items in testfiles:
                    file.writelines(items+'\n')
                file.close()
                # content for csv file:
                repos_with_JunitTests.append(repos.clone_url)
                result_dict["Name"].append(repos.full_name)
                result_dict["Link"].append(repos.clone_url)
                result_dict["Accessed"].append(date.today())
                result_dict["CreatedAt"].append(repos.created_at)
                result_dict["LastPush"].append(repos.pushed_at)
                result_dict["#Forks"].append(repos.forks)
                result_dict["#Stars"].append(repos.stargazers_count)
                result_dict["License"].append(base64.b64decode(repos.get_license().content.encode()).decode())
        except:
            print("[WARNING] Stopped github search due to search limit!")
            with open('stoppedAt.txt', 'w') as f:
                f.write('Github search stopped at i = %s' % (i))
            break

    return repos_with_JunitTests, result_dict

def download_files_from_repo(repository, testOnly=True):
    query = '%s' % (repository)
    r = g.search_repositories(query)
    for repos in r:
        if(repos.full_name == repository):
            print("[INFO] Repository found")
            contents = repos.get_contents("")
            while contents:
                file_content = contents.pop(0)
                if file_content.type == "dir":
                    contents.extend(repos.get_contents(file_content.path))
                else:
                    if(testOnly):
                        if(file_content.path.lower().endswith("test.java")):
                            print(file_content.path)
                            return

def save_dict_as_csv(dict, filename):
    dataFrame = pd.DataFrame(dict)
    dataFrame.to_csv("./%s.csv" % (filename))


if __name__ == '__main__':
    # check_rate_limit()
    keywords = ["java"]
    repos, result = search_github(keywords)
    for r in repos:
        print(r)
    save_dict_as_csv(result, "Dataset")

    # download_files_from_repo("iluwatar/java-design-patterns")
