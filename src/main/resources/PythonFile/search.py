import jieba
import jieba.analyse
import sys
import importlib as imp
import pymysql as mysql
import numpy as np
imp.reload(sys)
def check_language(str):
    for each in str.encode('utf-8').decode('utf-8'):
        if u'\u4e00' <= each <= u'\u9fff':
            return True
    return False
def processor(msg_list,key):
    point_counts_list=[]
    key_words=jieba.analyse.extract_tags(sentence=key,topK=3)
    # print(key_words)
    for msg in msg_list[:,1]:
        msg_keys = jieba.lcut(msg)
        point=0
        for i in range(len(key_words)):
            if key_words[i] in msg_keys:
                point+=(3-i)
        point_counts_list.append(point)
    point_list=np.array(point_counts_list)
    # msg_list = np.vstack((point_list,msg_list))
    # # msg_list = msg_list[msg_list[:,0].argsort()]
    # print(msg_list)
    # print(point_list)
    msg_list = np.insert(msg_list, 0, values=point_list, axis=1)
    msg_list = msg_list[msg_list[:, 0].argsort()]
    new_array = msg_list.tolist()
    new_array.reverse()
    final_array=[]
    for each in new_array:
        if each[0] == '0':
            break
        final_array.append(int(each[1]))
    print(final_array)
    # print(msg_list)
    # print(new_array)


def main(key):
    #key="hello"
    language=check_language(key)
    conn = mysql.connect(
        host='127.0.0.1',
        port=3306,
        db='bettermatchdb',
        user='root',
        password='root',
        charset='utf8'
    )
    cursor = conn.cursor()
    sql = "select id,message from news_info_stream where type!=3"
    cursor.execute(sql)
    temp_list=cursor.fetchall()
    msg_list=np.array(temp_list)
    # print(msg_list[:,1].tolist())
    processor(msg_list,key)


    # for each in msg_list:
    #     msg=each[1]
    #
    # print(msg_list[:,1])
    # print(type(msg_list[:,1][4]))
    # print(check_language(msg_list[:,1][4]))


if __name__ == '__main__':
    key = sys.argv[1]
    main(key)









