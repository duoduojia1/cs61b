# Gitlet Design Document


1. remove暂存区有点问题。我是如果当前暂存区有内容，则直接删除当前暂存区的Blob，不往remove暂存区  
添加内容。如果当前暂存区没有，才加入remove暂存区，等到commit的时候，删掉它。这里会过不了第15  
个测试用例，即又删又添加。
2. 我的merge或者其他地方，都是满足条件就直接对工作目录写，但是出现异常对工作目录的写就无法回滚了  
（merge的时候应该跟普通的add或commit同样的设计），所以上面两个就可能要重写很多地方了，没时间写了，写了依托答辩。  
![img.png](img.png)

