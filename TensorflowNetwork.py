#imports tensorflow
import tensorflow as tf
import numpy as np
import math
sess = tf.InteractiveSession()

#weight generation with small amount of noise in normal dist & slight + bias bc ReLU nuerons
def weight_variable(shape):
  initial = tf.truncated_normal(shape, stddev=0.1)
  return tf.Variable(initial)

#bias generation with small amount of noise in normal dist & slight + bias bc ReLU nuerons
def bias_variable(shape):
  initial = tf.constant(0.1, shape=shape)
  return tf.Variable(initial)

#creates conv layer with stride 1 and 0 padding
def conv2d(x, W):
  return tf.nn.conv2d(x, W, strides=[1, 1, 1, 1], padding='SAME')

#creates max_pool layer thats 2x2
def max_pool_2x2(x):
  return tf.nn.max_pool(x, ksize=[1, 2, 2, 1], strides=[1, 2, 2, 1], padding='SAME')

#attaches a lot of summaries
def variable_summaries(var, name):
    with tf.name_scope('summaries'):
      mean = tf.reduce_mean(var)
      tf.scalar_summary('mean/' + name, mean)
      with tf.name_scope('stddev'):
        stddev = tf.sqrt(tf.reduce_sum(tf.square(var - mean)))
      tf.scalar_summary('sttdev/' + name, stddev)
      tf.scalar_summary('max/' + name, tf.reduce_max(var))
      tf.scalar_summary('min/' + name, tf.reduce_min(var))
      tf.histogram_summary(name, var)

#reads image file names and respective labels
def read_labeled_image_list(image_list_file):
    f = open(image_list_file, 'r')
    filenames = []
    labels = []
    for line in f:
        filename, label = line.split(' ')
        filenames.append(filename)
        labels.append(int(label))
    return filenames, labels

#image name to image
def read_images_from_disk(input_queue):
    label = input_queue[1]
    file_contents = tf.read_file(str(input_queue[0]))
    example = tf.image.decode_jpeg(file_contents, channels=1)
    return example, label

# Reads paths of images together with their labels
image_list, label_list = read_labeled_image_list("images.txt")
test_image_list, test_label_list = read_labeled_image_list("valid.txt")

images = tf.convert_to_tensor(image_list)
labels = tf.convert_to_tensor(label_list)
test_images = tf.convert_to_tensor(test_image_list)
test_labels = tf.convert_to_tensor(test_label_list)

# Makes an input queue
train_input_queue = tf.train.slice_input_producer([images, labels], shuffle=True)
test_input_queue = tf.train.slice_input_producer([test_images, test_labels], shuffle=True)

file_content = tf.read_file(train_input_queue[0])
train_image = tf.image.decode_jpeg(file_content, channels = 1)
train_label = train_input_queue[1]

test_file_content = tf.read_file(test_input_queue[0])
test_image = tf.image.decode_jpeg(test_file_content, channels = 1)
test_label = test_input_queue[1]

train_image.set_shape([28,28,1])
test_image.set_shape([28,28,1])

#Image and Label Batching
image_batch, label_batch = tf.train.batch([train_image, train_label],batch_size=200)
test_image_batch, test_label_batch = tf.train.batch([test_image, test_label],batch_size=200)

#placeholder define vars?
#x = tf.placeholder(tf.float32, shape=[None, 784])
#y_ = tf.placeholder(tf.float32, shape=[None, 2])

#x = np.float32(image_batch)
image_batch = tf.cast(image_batch, tf.float32)
label_batch = tf.cast(label_batch, tf.float32)
test_image_batch = tf.cast(test_image_batch, tf.float32)
test_label_batch = tf.cast(test_label_batch, tf.float32)
x = image_batch
y_ = label_batch

#4D tensor, 2 and 3 is w and h, 4th is color channels
x_image = tf.reshape(x, [-1,28,28,1])

#conv layer 1, 5x5 patch with 32 features   
W_conv1 = weight_variable([5, 5, 1, 32])
b_conv1 = bias_variable([32])
 
#conv + pool
h_conv1 = tf.nn.relu(conv2d(x_image, W_conv1) + b_conv1)
h_pool1 = max_pool_2x2(h_conv1)

#second conv layer, 64 feature extraction
W_conv2 = weight_variable([5, 5, 32, 64])
b_conv2 = bias_variable([64])
h_conv2 = tf.nn.relu(conv2d(h_pool1, W_conv2) + b_conv2)
h_pool2 = max_pool_2x2(h_conv2)

#converts from feature to 1024
W_fc1 = weight_variable([7 * 7 * 64, 1024])
b_fc1 = bias_variable([1024])
h_pool2_flat = tf.reshape(h_pool2, [-1, 7*7*64])
h_fc1 = tf.nn.relu(tf.matmul(h_pool2_flat, W_fc1) + b_fc1)

#softmax layer with 10 outputs
W_fc2 = weight_variable([1024, 1])
b_fc2 = bias_variable([1])
y_conv=tf.matmul(h_fc1, W_fc2) + b_fc2


print("Net built")

#training: ADAM optimizer with overfitting help and logging every 100th iteration
with tf.name_scope('cross_entropy'):
  #cross_entropy = tf.reduce_mean(-tf.reduce_sum(y_ * tf.log(y_conv), reduction_indices=[1]))
  #cross_entropy = tf.reduce_mean(-tf.reduce_sum(y_ * tf.log(y_conv)))
  #cross_entropy = tf.sqrt(tf.reduce_mean(tf.square(tf.sub(y_, y_conv))))

  cross_entropy = tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(tf.squeeze(y_conv),y_))
  #cross_entropy = tf.nn.sigmoid_cross_entropy_with_logits(y_conv,y_)
  tf.scalar_summary('cross entropy', cross_entropy)
with tf.name_scope('train'):
  train_step = tf.train.AdamOptimizer(1e-4).minimize(cross_entropy)
with tf.name_scope('train_accuracy'):
  expect = tf.to_int32(y_)
  output = tf.squeeze(tf.to_int32(tf.scalar_mul(2,tf.clip_by_value(y_conv,.01,.99))))
  correct_prediction = tf.equal(tf.squeeze(tf.to_int32(tf.scalar_mul(2,tf.clip_by_value(y_conv,.01,.99)))), tf.to_int32(y_))
  accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
  tf.scalar_summary('accuracy', accuracy)

merged = tf.merge_all_summaries()
train_writer = tf.train.SummaryWriter("/tmp/data/train", sess.graph)  
test_writer = tf.train.SummaryWriter("/tmp/data/test", sess.graph)  
saver = tf.train.Saver()

sess.run(tf.initialize_all_variables())

coord = tf.train.Coordinator()
threads = tf.train.start_queue_runners(coord=coord)

print("Beginning Training...")

for i in range(20000):
  imgs, lbls = sess.run([image_batch, label_batch])
  test_imgs, test_lbls = sess.run([test_image_batch,test_label_batch])
  
  if i%10 == 0: #TODO should be 0
    print("Epoch: "+str(i))
    #print(expect.eval(feed_dict={x:imgs, y_:lbls}))
    #print(output.eval(feed_dict={x:imgs,y_:lbls}))
    train_accuracy = accuracy.eval(feed_dict={ x:imgs, y_: lbls})
    print("step %d, training accuracy %g"%(i, train_accuracy))    
  if i%10 == 0: #TODO should be 0
    #print(expect.eval(feed_dict={x:test_imgs, y_:test_lbls}))
    #print(output.eval(feed_dict={x:test_imgs,y_:test_lbls}))    
    #print("Epoch: "+str(i))
    test_accuracy = accuracy.eval(feed_dict={ x:test_imgs, y_: test_lbls})
    print("step %d, test accuracy %g"%(i, test_accuracy))

  if i>1500 and i%100 == 0:
    save_path = saver.save(sess, "/tmp/model.ckpt")
    print("Network saved in file: %s" %save_path)

  summary, acc = sess.run([merged,accuracy], feed_dict = { x:test_imgs, y_:test_lbls})
  test_writer.add_summary(summary,i)
  summary, acc = sess.run([merged,train_step], feed_dict = { x:imgs, y_:lbls})
  train_writer.add_summary(summary, i)

#prints final accuracy, to be updated
#print("test accuracy %g"%accuracy.eval(feed_dict={
#    x: mnist.test.images, y_: mnist.test.labels, keep_prob: 1.0}))
train_writer.close()
test_writer.close()
coord.request_stop()
coord.join(threads)

print("Move model to downloads!")
