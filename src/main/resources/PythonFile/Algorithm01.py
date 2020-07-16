import numpy as np

target=(8,5,5,7,1)
candidate_list=[[3,5,5,3,5],[7,4,6,2,8],[8,1,1,4,1],[5,5,5,5,2]]
candidate_list=np.array(candidate_list)
a=np.random.randint(1,10,(10,len(target))).astype('float64')
candidate_list=np.vstack((a,candidate_list))
id=np.arange(1,15,1)
candidate_list=np.insert(candidate_list,0,values=id,axis=1)
#print(candidate_list)
def step1(candidate_list,target):
    point_array=np.zeros((len(candidate_list),len(target)))
    final_point_array=np.zeros((1,len(candidate_list)))
    for i in range(len(candidate_list)):
        for j in range(len(target)):
            if target[j]>candidate_list[i][j+1]+3 or target[j]<candidate_list[i][j+1]-3:
                continue
            elif target[j]-candidate_list[i][j+1] == 0:
                point_array[i][j] += 3
            elif target[j]-candidate_list[i][j+1] == 1:
                point_array[i][j] += 3 - 0.33
            elif target[j]-candidate_list[i][j+1] == 2:
                point_array[i][j] += 3 - 0.33 - 0.5
            elif target[j]-candidate_list[i][j+1] == 3:
                point_array[i][j] += 3 - 0.33 - 0.5 -1
            elif candidate_list[i][j+1] - target[j] == 1:
                point_array[i][j] += 3 - 0.5
            elif candidate_list[i][j+1] - target[j] == 2:
                point_array[i][j] += 3 - 0.5 -0.75
            elif candidate_list[i][j+1] - target[j] == 3:
                point_array[i][j] += 3 - 0.5 -0.75 -1.5
    for i in range(len(candidate_list)):
        for j in range(len(target)):
            if point_array[i][j] == min(point_array[i]):
                continue
            final_point_array[0][i] += point_array[i][j]*target[j]*target[j]/10
            # if i==12:
                #print("加法数据",point_array[i][j],target[j],point_array[i][j]*target[j]*target[j]/10)
    candidate_list=np.insert(candidate_list,0,values=final_point_array[0],axis=1)
    # print(candidate_list)

    #print(final_point_array)
    result=final_point_array[0]
    candidate_list=candidate_list[candidate_list[:,0].argsort()]
    testoutput=candidate_list[:, 1].tolist()
    testoutput.reverse()
    #print(testoutput)
    # print(candidate_list)
    return candidate_list[0:100].astype('float64')
#answer=step1(candidate_list,target)
#print(answer)


