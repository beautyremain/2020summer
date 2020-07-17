import numpy as np
from Algorithm01 import step1
from sklearn.metrics.pairwise import cosine_similarity
from Algorithm03 import step3
import pymysql as mysql
import sys

import importlib as imp
imp.reload(sys)
#type=0 #0是用户找小组，1是小组找用户
#userid=1 # 推荐发起者id
#sys.setdefaultencoding("utf-8")
def main(type,userid):
    history=[]
    conn = mysql.connect(
        host='127.0.0.1',
        port=3306,
        db='bettermatchdb',
        user='root',
        password='root',
        charset='utf8'
    )
    cursor = conn.cursor()
    # sql = "select * from label_info"
    # res=cursor.execute(sql)
    # ret3 = cursor.fetchall()
    sql_getTarget=""
    if type==0:
        sql_getTarget="select chara_point from userinfo where id=%s"
    else:
        sql_getTarget="select chara_point from groupinfo where id=%s"

    cursor.execute(sql_getTarget,userid)
    target=list(map(int,cursor.fetchone()[0].split(",")))
    #print("target:",target)

    if type == 0:
        sql_getGroup = "select group_id from userinfo where id=%s"
        cursor.execute(sql_getGroup, userid)
        group_list = cursor.fetchone()[0]
        group_list = group_list.split(",")
        condition = ""
        for each in group_list:
            condition += str(each) + ","
        sql_getHistory = "select ori_point from groupinfo where id in(" + condition[:-1] + ") "
        print(sql_getHistory)
        cursor.execute(sql_getHistory)
        res = cursor.fetchall()
        reslist = []
        for i in range(len(res)):
            temp = res[i][0].split(",")
            reslist.append(list(map(int,temp)))
        history=np.array(reslist)
    #print("history:",history)
    sql_getAllGroupChara=""
    if type == 0:
        sql_getAllGroupChara="select chara_point from groupinfo where sign_state=0"
    else:
        sql_getAllGroupChara="select chara_point from userinfo"
    cursor.execute(sql_getAllGroupChara)
    res=cursor.fetchall()
    reslist = []
    for i in range(2):
        temp = res[i][0].split(",")
        reslist.append(list(map(int, temp)))
    candidate_list = np.array(reslist)
    #print("candidate_list:",candidate_list)
    sql_getId=""
    if type == 0:
        sql_getId = "select id from groupinfo where sign_state=0"
    else:
        sql_getId = "select id from userinfo"
    cursor.execute(sql_getId)
    anslist=[]
    res=cursor.fetchall()
    #print(res[1][0])
    for i in range(len(res)):
        anslist.append(res[i][0])
        #list(map(int,cursor.fetchall()))
    id_list = np.array(anslist)
    #print(id_list)
    candidate_list=np.insert(candidate_list,0,values=id_list,axis=1)

    '''
    以下是测试用的数据
    '''
    # target=(8,5,5,7,1)#推荐发起者的特征组(c0,c1,c2,c3,c4)
    #
    # history=[[7,3,6,2,1],[1,5,4,8,4]]#推荐发起者的历史加组记录
    #
    # candidate_list=[[3,5,5,3,5],[7,4,6,2,8],[8,1,1,4,1],[5,5,5,5,2]]
    # candidate_list=np.array(candidate_list)
    # a=np.random.randint(1,10,(10,len(target)))
    # candidate_list=np.vstack((a,candidate_list))
    # id=np.arange(1,15,1)
    # candidate_list=np.insert(candidate_list,0,values=id,axis=1)#所有的小组 ( id,c0,c1,c2,c3,c4 )



    result1=step1(candidate_list,target)#初始积分
    # print("result1:",result1)
    if type == 0 and len(history)>0:
        result2=step3(result1,history)#新积分
        #print("result2:",result2)
        result1[:,0] += result2[0]

    result1=result1[result1[:,0].argsort()]#排序
    order_id=result1[:,1].astype('int32').tolist()
    order_id.reverse()
    print(order_id)#降序排列的推荐id

if __name__ == '__main__':
    type = sys.argv[1]  # 0是用户找小组，1是小组找用户
    userid = sys.argv[2]  # 推荐发起者id
    main(type,userid)
