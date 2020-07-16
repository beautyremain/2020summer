import numpy as np
from Algorithm01 import step1
from sklearn.metrics.pairwise import cosine_similarity

def step3(result,history):
    history=np.array(history)

    data=np.vstack((history,result[:,2:7]))
    ans=cosine_similarity(data)
    point_list=ans[0:len(history),len(history):len(ans[0])]
    #print(point_list)
    point_array=np.zeros((len(point_list),len(point_list[0]))).astype('float64')
    for j in range(len(point_list)):
         for i in range(len(point_list[0])):
             if point_list[j][i]>0.9:
                 point_array[j][i] += point_list[j][i]*point_list[j][i]*1.5
                 continue
             if point_list[j][i]>0.8:
                 point_array[j][i] += point_list[j][i] * point_list[j][i] * 0.8
    final_p=np.zeros((1,len(point_list[0])))
    for each in point_array:
        final_p += each
    final_p /= len(point_array)
    final_p *= 10
   #print("final_p: ",final_p)
    return final_p.astype('float64')

