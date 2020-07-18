import numpy as np
import math
import sys
def main(str1,str2):
    str_arr1=str1.split(',')
    str_arr2=str2.split(',')
    num_arr1=np.array(list(map(int,str_arr1)))
    num_arr2=np.array(list(map(int,str_arr2)))
    for i in range(len(num_arr2)):
        if num_arr2[i]==0:
            num_arr2[i]=num_arr1[i]
    print(math.floor(np.linalg.norm(-num_arr1+num_arr2)*10));

if __name__ == '__main__':
    # type = sys.argv[1]  # 0是用户找小组，1是小组找用户
    # userid = sys.argv[2]  # 推荐发起者id
    self_str=sys.argv[1]#5,3,4,6,3"
    other_str=sys.argv[2]#"6,4,8,7,4"
    main(self_str,other_str)
