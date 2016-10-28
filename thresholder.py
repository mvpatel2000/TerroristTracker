def score(flag, control, limit, numFlag, numControl):
   pts = 0
   inc = 0
   fps = 0
   for i in range(numFlag):
      if int(flag[i])<40 or int(flag[i+numFlag])<2000:
         pts+=5
   for i in range(numControl):
      if int(control[i])>40 or int(control[i+numControl])>2000:
         pts+=5
   return pts

def final(flag, control, limit, numFlag, numControl):
   pts = 0
   inc = 0
   fps = 0
   for i in range(numFlag):
      if int(flag[i])<40 or int(flag[i+numFlag])<2000:
         pts+=2
         inc+=1
   for i in range(numControl):
      if int(control[i])>40 or int(control[i+numControl])>2000:
         pts+=2
         fps+=1
   return pts,inc,fps

numFlag = 523
numControl = 1085

vals = open('threshold.txt').read().split()
flag = []
control = []
set = False
for i in range(len(vals)):
   if i<numFlag:
      flag.append(vals[i])
   elif i<numFlag*2:
      flag.append(vals[i])
   elif i<numFlag*2+numControl:
      control.append(vals[i])
   else:
      control.append(vals[i])      
      
penalty = score(flag,control,40,numFlag,numControl)
ctr = 40
for i in range(120):
   penal = score(flag,control,i+40,numFlag,numControl)
   print(penal)
   if penal<penalty:
      penalty = penal
      ctr = i+40
      
print("Limit: "+str(ctr))
print("Penalty: "+str(penalty))
penalty,inc,fps = final(flag,control,ctr,numFlag,numControl)
print("Percent Incorrect: "+str(inc/523*100))
print("Percent False Positive: "+str(fps/1085*100))
print("Total Percent Error: "+str((inc+fps)/len(vals)*100))
#print(str(inc)+" "+str(fps))
print(final(flag,control,ctr,numFlag,numControl))