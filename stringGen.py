f = open('images.txt','w')

for z in range(6):
	for i in range(1317): #1417
		if not i==378:
			#f.write("~/Downloads/Instagram/VideoAlgo/CropScaled/1_"+str(i)+"_crop.jpg 1\n")
			f.write("Instagram/VideoAlgo/CropScaled/1_"+str(i)+"_crop.jpg 1\n")

	for i in range(1417,1417+2283): #3910
		if not i==3816:
			#f.write("~/Downloads/Instagram/VideoAlgo/CropScaled/0_"+str(i)+"_crop.jpg 0\n")
			f.write("Instagram/VideoAlgo/CropScaled/0_"+str(i)+"_crop.jpg 0\n")
#total 3900

v = open('valid.txt','w')

for i in range(1317,100+1317): #1417
	if not i==378:
		v.write("Instagram/VideoAlgo/CropScaled/1_"+str(i)+"_crop.jpg 1\n")

for i in range(1417+2383,1417+2383+101): #3910
	if not i==3816:
		v.write("Instagram/VideoAlgo/CropScaled/0_"+str(i)+"_crop.jpg 0\n")

e1 = open('eval1.txt','w')
e2 = open('eval2.txt','w')
e3 = open('eval3.txt','w')
e4 = open('eval4.txt','w')
size = 1

if size == 1:
	for i in range(0,17178): #2: 13026 3:21309 4:21000 5:4494
		e1.write("VideoAlgo/4KStogram/CropScaled/3_"+str(i)+"_crop.jpg 0\n")		
	for i in range(17178,17178*2): #2: 13026 3:21309 4:21000 5:4494
		e2.write("VideoAlgo/4KStogram/CropScaled/3_"+str(i)+"_crop.jpg 0\n")		
	for i in range(17178*2,17178*3): #2: 13026 3:21309 4:21000 5:4494
		e3.write("VideoAlgo/4KStogram/CropScaled/3_"+str(i)+"_crop.jpg 0\n")		
	for i in range(17178*3,17178*4): #2: 13026 3:21309 4:21000 5:4494
		e4.write("VideoAlgo/4KStogram/CropScaled/3_"+str(i)+"_crop.jpg 0\n")		
		
else: #60329
	for i in range(0,13026): #2: 13026 3:21309 4:21000 5:4494
		e1.write("VideoAlgo/4KStogram_Small/CropScaled_Small/2_"+str(i)+"_crop.jpg 0\n")
	for i in range(0,21309): #2: 13026 3:21309 4:21000 5:4494
		e2.write("VideoAlgo/4KStogram_Small/CropScaled_Small/3_"+str(i)+"_crop.jpg 0\n")
	for i in range(0,21000): #2: 13026 3:21309 4:21000 5:4494
		e3.write("VideoAlgo/4KStogram_Small/CropScaled_Small/4_"+str(i)+"_crop.jpg 0\n")
	for i in range(0,4494): #2: 13026 3:21309 4:21000 5:4494
		e4.write("VideoAlgo/4KStogram_Small/CropScaled_Small/5_"+str(i)+"_crop.jpg 0\n")
