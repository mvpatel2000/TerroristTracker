size = 1

opt = []
for z in range(4):
	file = open("frop"+str(z+1)+".txt", 'r').read()
	file = file.split("\n")
	topt = []
	for x in range(len(file)-1):
		i = file[x]
		topt.append(int( float( i.split('e')[0] ) ))
	for i in topt:
		opt.append(i)
#print(opt)
print(len(opt))
usrname = []
pics = [] #num picsof each user
for z in range(1): #4
	if size==1:
		file = open("VideoAlgo/4KStogram/expected.txt").read() #TODO fix
	else:
		file = open("VideoAlgo/4KStogram_Small/expected.txt").read()
	file = file.split("\n")
	tusr = []
	tpics = []
	for x in range(len(file)-1):
		i = file[x].split(" ")
		#print(i[0]+" "+i[1])
		tpics.append(i[1])
		tusr.append(i[0])
	for i in range(len(tpics)):
		pics.append(int(tpics[i]))
		usrname.append(tusr[i])
sum=0
for i in pics:
	sum+=i
print(sum*3)

flgs = [] #num flags
curr = 0

try:
	for i in range(len(pics)):
		max = pics[i]
		flag = 0
		for x in range(0,max*3,3):
			if opt[curr+x] + opt[curr+x+1] + opt[curr+x+2] >1:
				flag+=1
		flgs.append(flag)
		curr += max*3
except:
	print("dank")
print(flgs)
f = open('output.txt','w')
for i in range(len(usrname)):
	try:
		f.write(usrname[i]+" "+str(flgs[i])+"\n")
	except:
		f.write(usrname[i]+" 0\n")
