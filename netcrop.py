#todo: Tune hyperparameters
from pybrain.tools.shortcuts import buildNetwork
from pybrain.structure import SigmoidLayer
from pybrain.datasets import SupervisedDataSet
from pybrain.datasets import ClassificationDataSet
from pybrain.supervised.trainers import BackpropTrainer
from pybrain.utilities import percentError

from pybrain.tools.customxml.networkwriter import NetworkWriter
from pybrain.tools.customxml.networkreader import NetworkReader
import os
from sys import argv

#input data
def load(ds):
   location = os.getcwd().replace("\\","/")+"/";
   for i in range(1417): #1417
      inputText = open(location+"CropProcess/"+str(i)+'_1_input.txt').read().split() #todo: _tan after input for tanh
      input = [int(ipt) for ipt in inputText]
      ds.addSample(input, 1)
   for i in range(1417, 1417+2493): #2493
      inputText = open(location+"CropProcess/"+str(i)+'_0_input.txt').read().split() #todo: _tan after input for tanh
      input = [int(ipt) for ipt in inputText]
      ds.addSample(input, 0)      #todo: -1 for tanh
   return ds
   
#gets percent error   
def getError(output, target):
   total = output.size
   correct = 0
   miss = 0
   fpos = 0
   for i in range(total):
      if output[i] > .9 and target[i] == 1 or output[i] < .1 and target[i] == 0: #TODO: For actual testing, cutoff as 0 shud be .9 as well | todo: expect -1 for tanh
         correct+=1
      elif target[i] == 1:
         miss+=1
      else:
         fpos+=1
   print("Missed: "+str(miss)+" False Positive: "+str(fpos))   
   return 100-correct/total*100

def run(ds):
   net = buildNetwork(784, 100, 1, bias=True, hiddenclass = SigmoidLayer, outclass = SigmoidLayer) #todo: TanhLayer for tanh
   trainer = BackpropTrainer(net, ds, learningrate=0.01, momentum=0.99)
   ctr=0
   pe1 = 80
   err = .998
   er2 = .999
   threshold = 9.0
   epoch = 0
   print("Beginning training...")
   
   while pe1>threshold: 
      err = trainer.train()
      print(err)
      pe1 = getError( net.activateOnDataset(ds), ds['target'] )
      print( "Percent Error: "+str( pe1 )+"%" )   #getError(net, tstdata))
      print("Epoch: "+str(epoch))
      if abs(err - er2) < .000001:
         ctr+=1   
      else:
         ctr=0
         er2 = err
      if ctr==3:         
         print("....Restarting....")
         run(ds)
      if pe1 < threshold:
         print("Network trained!")
         print( "Final Percent Error: "+str( getError( net.activateOnDataset(ds), ds['target'] ) ) )
         NetworkWriter.writeToFile(net, 'netsave.xml')
         threshold = pe1 - .1
      epoch+=1
   
ds = ClassificationDataSet(784, 1)
ds = load(ds)
run(ds)