/*
   Instructions
   Anything that says TODO and is in all caps is stuff you must update.
   Put all images into folders Flag_0 (non-flags) and Flag_1 (ISIS flags).
   Run this, then run network.   
*/

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.lang.Math;

public class ImageProcessor
{
   public static int width = 300;
   public static double threshold = 10.0;   
   public static int[] cx = {0,0,0};
   public static int[] cy = {0,0,0};
   public static int[] R = {0,0,0};
   public static int circleCount = 0;
   
   public static void main(String[] args) throws FileNotFoundException, IOException {            
      String location = new java.io.File("").getAbsolutePath().replace("\\","/")+"/";
      forward(location);
      //train(location);     
      //prepare(location); 
   }
   
   //converts CropScaled to text files aka USELESS
   public static void prepare(String location) throws FileNotFoundException, IOException
   {      
      File[] files = new File(location+"CropScaled").listFiles();
      String[] pics = new String[files.length]; //filesN.length-1
      for(int i=0; i<files.length; i++)
         pics[i] = files[i].getName();
      int ans = 1;
      for(int z=0; z<pics.length; z++) {
         if(z%50==0)
            System.out.println(z);
         BufferedImage img = null;
         img = ImageIO.read(new File(location+"CropScaled"+"/"+pics[z]));
         int w = img.getWidth();
         int h = img.getHeight();
         int[] image = new int[w*h];
         image = img.getRGB(0,0,w,h,image,0,w);
         int[] imageBW = toBW(image);
         if(z==1417) //TODO: UPDATE
            ans = 0;
         PrintWriter out = new PrintWriter(location+"CropProcess/"+z+"_"+ans+"_input.txt", "UTF-8");
         for(int i=0; i<h*w; i++) {
            if(imageBW[i]>250)
               out.print("1 ");
            else
               out.print("0 ");
         }
         out.close();
      }
   
   }
   public static void forward(String location) throws FileNotFoundException, IOException
   {
      String outFile = "OutFile/";      
      //delete(new File(location+"OutFile"));
      //delete(new File(location+"Canny"));      
      new File(location+"OutFile");
      new File(location+"Canny");
   
      String subfile = ".thumb.stogram";
   
      
      int ttlsize = 0;
      ArrayList<Integer> fileSizes = new ArrayList<Integer>();
      Scanner sc = new Scanner(new File("users.txt"));
      ArrayList<String> users = new ArrayList<String>();
      while(sc.hasNextLine())
         users.add(sc.nextLine()+"/"+subfile);
      
      PrintWriter out = new PrintWriter(location+"picUsers.txt", "UTF-8");
            
      for(int z=0; z<users.size(); z++) { //TODO: make 0
         System.out.println("User: "+z+" Username:"+(users.get(z).split("/"))[0]);
         File[] files = new File(location+users.get(z)).listFiles();
         String[] pics = new String[files.length]; //TODO: Remove the -1
         int ctr = 0;
         for(int i=0; i<files.length; i++) { //TODO: Remove the -1
            if(files[ctr].getName().contains(".ini"))
               ctr++;   
            pics[i] = files[ctr].getName();
            ctr++;
         }
         fileSizes.add(pics.length);
         imgProc(pics, location, users.get(z), outFile, 2, ttlsize);
         ttlsize+=pics.length;
         
         out.println( (users.get(z).split("/"))[0] +" "+pics.length );
      }
      out.close();
      
      // System.out.println(ttlsize);
   //       System.out.println(fileSizes.size());
   //       for(int i=0; i<fileSizes.size(); i++)
   //          System.out.println(fileSizes.get(i));     
      
      
//       Runtime rt = Runtime.getRuntime();
//       String[] cmd = new String[4+fileSizes.size()];
//       cmd[0] = "python";
//       cmd[1] = "network.py";
//       cmd[2] = ttlsize+"";
//       cmd[3] = fileSizes.size()+"";
//       for(int i=0; i<fileSizes.size(); i++)
//          cmd[i+4] = fileSizes.get(i)+"";
//       //String[] cmd = {"python", "network.py", (files.length-1)+"","2","5","22"}; //update with dataset info      
//       Process proc = rt.exec(cmd);
   }
   public static void train(String location) throws FileNotFoundException, IOException
   {
      String flag1 = "Flag_1";
      String flag0 = "Flag_0";
      String outFile1 = "Input_1/";
      String outFile0 = "Input_0/";
      
      delete(new File(location+"Input_1"));
      delete(new File(location+"Input_0"));
      delete(new File(location+"Canny"));
      delete(new File(location+"Circle"));
      delete(new File(location+"Crop"));
      
      //output directories
      new File(location+"Input_1").mkdir();
      new File(location+"Input_0").mkdir();
      new File(location+"Canny").mkdir();
      new File(location+"Circle").mkdir();
      new File(location+"Crop").mkdir();
      
      File[] filesY = new File(location+flag1).listFiles();     
      File[] filesN = new File(location+flag0).listFiles();
      
      //preprocessing used to remove the desktop.ini file (normally hidden) due to google desktop
      //todo: Removing .getName() gives full location, can potentially remove need for string location, flag1, flag0
      //DEBUG: Flag_0 does not have desktop.ini so should it be added, second loop should be updated
      String[] picsY = new String[filesY.length-1]; //TODO: Remove the -1
      int ctr = 0;
      for(int i=0; i<filesY.length-1; i++) { //TODO: Remove the -1
         if(filesY[ctr].getName().contains(".ini"))
            ctr++;   
         picsY[i] = filesY[ctr].getName();
         ctr++;
      }
      
      String[] picsN = new String[filesN.length]; //filesN.length-1
      ctr = 0;
      for(int i=0; i<filesN.length; i++) { //filesN.length-1
         if(filesN[ctr].getName().contains(".ini"))
            ctr++;    
         picsN[i] = filesN[ctr].getName();
         ctr++;
      }   
   
      imgProc(picsY, location, flag1, outFile1, 1, 0); 
      imgProc(picsN, location, flag0, outFile0, 0, 0);
   }
      
   //find image info
   public static void imgProc(String[] pics, String location, String flag, String outFile, int ans, int shift) throws FileNotFoundException, IOException
   {      
      for(int z=0; z<pics.length; z++) { //starts at 0 pics.length
         if(z%50==0)
            System.out.println("Dataset: "+ans+" Number: "+z);
            //System.out.println("Begin_Set");
         BufferedImage white = ImageIO.read(new File(location+"white.jpg"));
         BufferedImage img = null;  //loads in image
      
         //try {
         img = ImageIO.read(new File(location+flag+"/"+pics[z]));
         int w = img.getWidth();
         int h = img.getHeight();
         if(w==300 && h==300) {
            width = w;
            int[] image = new int[w*h];
            image = img.getRGB(0,0,w,h,image,0,w);
            int[] IMAGE = image;
              
            int[] imageBW = toBW(image);  //converts to BW
            int[] imageBlur = toBlur(imageBW,h,w); //converts to Blur
            double[][] Gxy = getGxy(imageBlur, h, w);
            double[][] GCanny = getCanny(Gxy, h, w); 
                     
            image = toARGB(imageBlur,GCanny);      
            img.setRGB(0,0,w,h,image,0,w); //prints out image
            File outputfile = new File(location+"Canny/"+ans+"_"+(z+shift)+"_canny.png");
            ImageIO.write(img, "png", outputfile);
               
            double[][] GCircle = getCircle(clone(GCanny), imageBlur, h, w);       
               
            image = toARGBCircle(imageBlur,GCircle);      
            img.setRGB(0,0,w,h,image,0,w); //prints out image
            outputfile = new File(location+"Circle/"+ans+"_"+(z+shift)+"_circle.png");
            ImageIO.write(img, "png", outputfile);
            
            for(int i=0; i<3; i++) {               
               image = toARGBCrop(IMAGE, GCanny, w, h, i);
               if(image.length != 1) {
                  try {
                     img = new BufferedImage(R[i]*2,R[i]*2,BufferedImage.TYPE_INT_ARGB);
                     img.setRGB(0,0,R[i]*2,R[i]*2,image,0,R[i]*2);
                     outputfile = new File(location+"Crop/"+ans+"_"+circleCount+"_crop.png");
                     ImageIO.write(img, "png", outputfile);
                     circleCount++;
                  }
                  catch(Exception e) {
                     outputfile = new File(location+"Crop/"+ans+"_"+circleCount+"_crop.png");
                     ImageIO.write(white, "png", outputfile);
                     circleCount++;
                  }
               }
               else {
                  outputfile = new File(location+"Crop/"+ans+"_"+circleCount+"_crop.png");
                  ImageIO.write(white, "png", outputfile);
                  circleCount++;
               }
            }
            //getColor(imageBW, GCanny, h, w);
            
            PrintWriter out = new PrintWriter(location+outFile+(z+shift)+"_"+ans+"_input.txt", "UTF-8"); //pics[z].substring(0, pics[z].length()-4) is file name
            for(int i=0; i<h*w; i++) {
               if(Gxy[i][0]>threshold)
                  out.print("1 ");
               else
                  out.print("0 ");
            }
            out.close();   
         }
         else
            delete(new File( location+flag+"/"+pics[z] ));
         //} 
         //catch(Exception e) {System.out.println(z); }
      }
   }
   
   //finds % bw
   public static void getColor(int[] bw, double[][] GCanny, int h, int w)
   {
      int total=0;
      int black=0;
      int white=0;
      int mark=0;
      for(int x=0; x<w; x++) {
         for(int y=0; y<h; y++) {
            if(Math.pow(x-cx[0],2)+Math.pow(y-cy[0],2) <= Math.pow(R[0],2)) {
               total++;
               if(bw[x+y*w]>220)
                  black++;
               else if(bw[x+y*w]<80)
                  white++;
               else
                  mark++;
            }
         }
      }
      System.out.println( black +" "+white+" "+(black - white) );// / (total*1.0) * 100 );
   }
   
   //find circle
   public static double[][] getCircle(double[][] Gxy, int[] imageBlur, int h, int w)
   {
      int[][] count = new int[w][h];
      //int[][][] radius = new int[w][h][w*10];
      int[][] radius = new int[w][h];
      int cEdge = 0;
      //int[][] mkr = new int[w][h];
      for(int x=0; x<w; x+=1) { //+= 10 should be ++
         for(int y=0; y<h; y+=1) {
            int i = x+y*w;
            if( (!( x<1 || x>w-2 || y<1 || y>h-2)) && Gxy[i][0] > threshold ) {
               cEdge += 1;
               double theta = Math.atan2(Gxy[i][2],Gxy[i][1]);
               for(int q=0; q<(int)(Math.sqrt(w*w+h*h)); q++) {
                  double xsh = q*Math.cos(theta);
                  double ysh = q*Math.sin(theta);
                  int xc = (int)(xsh) + x;
                  int yc = (int)(ysh) + y;
                  if(xc<0 || xc>w-1 || yc<0 || yc>h-1)
                     break;
                  int ct = xc + yc*w;
                  count[xc][yc] += 1;
                  //radius[xc][yc][mkr[xc][yc]] = q;
                  radius[xc][yc] += q;
                  //mkr[xc][yc]++;
               }               
               theta += Math.PI;
               for(int q=0; q<(int)(Math.sqrt(w*w+h*h)); q++) {
                  double xsh = q*Math.cos(theta);
                  double ysh = q*Math.sin(theta);
                  int xc = (int)(xsh) + x;
                  int yc = (int)(ysh) + y;
                  if(xc<0 || xc>w-1 || yc<0 || yc>h-1)
                     break;
                  int ct = xc + yc*w;
                  count[xc][yc] += 1;
                  //radius[xc][yc][mkr[xc][yc]] = q;
                  radius[xc][yc] += q;
                  //mkr[xc][yc]++;
               }               
            }
         }
      }      
      //to adjust for circle radius size thing
      for(int x=0; x<w; x++) {
         for(int y=0; y<h; y++) {
            if(count[x][y]==0) {
               //radius[x][y][0] = 0;
               radius[x][y] = 0;
            }
            else {
                //int[] rDist = radius[x][y];
                //radius[x][y][0] = rDist[count[x][y]/2];
               radius[x][y] = radius[x][y] / count[x][y];
            }
         }
      }        
      for(int x=0; x<w; x++) {
         for(int y=0; y<h; y++) {
            //if(radius[x][y][0] == 0 )
            if(radius[x][y] == 0)
               count[x][y] = 0;
            else {
               //count[x][y] = (int)(count[x][y] / Math.pow(radius[x][y][0], 1/4));
               count[x][y] = (int)(count[x][y] / Math.pow(radius[x][y], 1/4));
            }
         }
      }      
      int[] lng = {0,0,0};
      for(int x=0; x<w; x++) {
         for(int y=0; y<h; y++) {
            if(count[x][y] > lng[0]) {
               lng[2] = lng[1]; cx[2] = cx[1]; cy[2] = cy[1];
               lng[1] = lng[0]; cx[1] = cx[0]; cy[1] = cy[0];
               lng[0] = count[x][y]; cx[0] = x; cy[0] = y;
            }
            else if(count[x][y] > lng[1]) {
               lng[2] = lng[1]; cx[1] = cx[0]; cy[1] = cy[0];
               lng[1] = count[x][y]; cx[0] = x; cy[0] = y;
            }
            else if(count[x][y] > lng[0]) {
               lng[2] = count[x][y]; cx[0] = x; cy[0] = y;
            }
         }
      }            
      //R = radius[cx][cy][0];
      R[0] = radius[cx[0]][cy[0]];
      R[1] = radius[cx[1]][cy[1]];
      R[2] = radius[cx[2]][cy[2]];
   
      double tht = 0.0;
      while(tht<Math.PI*2) {
         try {
            Gxy[cx[0]+cy[0]*w + (int)(R[0]*Math.cos(tht))+(int)(R[0]*Math.sin(tht))*w][0] = 999;
         }
         catch(Exception e) {}
         tht+=Math.PI/2880.0;
      }
      
      tht = 0.0;
      while(tht<Math.PI*2) {
         try {
            Gxy[cx[1]+cy[1]*w + (int)(R[1]*Math.cos(tht))+(int)(R[1]*Math.sin(tht))*w][0] = 999;
         }
         catch(Exception e) {}
         tht+=Math.PI/2880.0;
      }
      
      tht = 0.0;
      while(tht<Math.PI*2) {
         try {
            Gxy[cx[2]+cy[2]*w + (int)(R[2]*Math.cos(tht))+(int)(R[2]*Math.sin(tht))*w][0] = 999;
         }
         catch(Exception e) {}
         tht+=Math.PI/2880.0;
      }            
      return Gxy;
   }
   
   //CannyEdge
   public static double[][] getCanny(double[][] Gxy, int h, int w)
   {
      for(int i=w; i<h*w-w; i++) {
         if(i%w==0 || i%w==w-1 || Gxy[i][0]==0) {}
         else {
            double theta = Math.atan2(Gxy[i][2],Gxy[i][1]);
            if(theta>Math.PI*3/8.0 && theta<-1*Math.PI*3/8.0) { //vertical
               if(Gxy[i][0]>Gxy[i+w][0])
                  Gxy[i+w][0]=0;
               else if(Gxy[i][0]>Gxy[i-w][0])
                  Gxy[i-w][0]=0;
            }
            else if(theta>Math.PI*1/8.0) { //TR
               if(Gxy[i][0]>Gxy[i+w-1][0])
                  Gxy[i+w-1][0]=0;
               else if(Gxy[i][0]>Gxy[i-w+1][0])
                  Gxy[i-w+1][0]=0;
            }
            else if(theta>-1*Math.PI*1/8.0) { //horizontal
               if(Gxy[i][0]>Gxy[i+1][0])
                  Gxy[i+1][0]=0;
               else if(Gxy[i][0]>Gxy[i-1][0])
                  Gxy[i-1][0]=0;
            }
            else { //Br
               if(Gxy[i][0]>Gxy[i+w+1][0])
                  Gxy[i+w+1][0]=0;
               else if(Gxy[i][0]>Gxy[i-w-1][0])
                  Gxy[i-w-1][0]=0;
            }
         }
      }
      return Gxy;
   }
   
   //G matrix
   public static double[][] getGxy(int[] img, int h, int w)
   {
      double[][] Gxy = new double[h*w][3];
      for(int i=0; i<h*w; i++) {
         if(i%w==0 || i%w==w-1 || i<w || i>h*w-w-1) {
            Gxy[i][0] = 0;
            Gxy[i][1] = 0;
            Gxy[i][2] = 0;
         }
         else {
            double Gx = (img[i+1]*2 + img[i+1+w] + img[i+1-w] - img[i-1]*2 - img[i-1+w] - img[i-1-w])/16.0;
            double Gy = (img[i+w]*2 + img[i+1+w] + img[i-1+w] - img[i-w]*2 - img[i+1-w] - img[i-1-w])/16.0;
            Gxy[i][0] = Math.abs(Gx) + Math.abs(Gy);
            Gxy[i][1] = Gx;
            Gxy[i][2] = Gy;
            if(Gxy[i][0]<threshold)
               Gxy[i][0]=0;
         }
      }
      return Gxy;
   }
   
   //gaussian blur
   public static int[] toBlur(int[] img, int h, int w) 
   {
      int[] image = new int[h*w];
      for(int i=0; i<h*w; i++) {
         if(i%w==0 || i%w==w-1 || i<w || i>h*w-w-1)
            image[i] = img[i];
         else
            image[i] = (int)((img[i]*4 + img[i+1]*2 + img[i-1]*2 + img[i+w]*2 + img[i-w]*2 + img[i+1+w] + img[i-1+w] + img[i+1-w] + img[i-1-w])/16.0);
      }
      return image;
   }
   
   //convert ARGB to BW
   public static int[] toBW(int[] img) 
   {
      int[] image = new int[img.length];
      for(int i=0; i<img.length; i++) {
         Color rgb = new Color(img[i]);
         int red = rgb.getRed();
         int green = rgb.getGreen();
         int blue = rgb.getBlue();
         image[i] = (int)(255.0*(0.21*red/255.0 + 0.72*green/255.0 + 0.07*blue/255.0));
      }
      return image;
   }
   
   //convert to ARGB
   public static int[] toARGB(int[] img) 
   {
      int[] image = new int[img.length];
      for(int i=0; i<img.length; i++) {
         Color rgb = new Color(img[i],img[i],img[i]);
         image[i] = rgb.getRGB();
      }
      return image;
   }
   
   //convert to ARGB
   public static int[] toARGB(int[] img, double[][] Gxy) 
   {
      int[] image = new int[img.length];
      for(int i=0; i<img.length; i++) {         
         Color rgb = new Color(img[i],img[i],img[i]);
         if(Gxy[i][0]>threshold)
            rgb = new Color(0,0,0);
         else
            rgb = new Color(255,255,255);           
         image[i] = rgb.getRGB();
      }
      return image;
   }
   
   //convert to ARGB
   public static int[] toARGBCircle(int[] img, double[][] Gxy) 
   {
      int[] image = new int[img.length];
      for(int i=0; i<img.length; i++) {         
         Color rgb = new Color(img[i],img[i],img[i]);
         if(Gxy[i][0] == 999)
            rgb = new Color(255,0,0);
         else if(Gxy[i][0]>threshold)
            rgb = new Color(0,0,0);
         else
            rgb = new Color(255,255,255);           
         image[i] = rgb.getRGB();
      }
      return image;
   }
   
   //convert to ARGB color
   public static int[] toARGBCropColor(int[] img, double[][] Gxy, int w, int h, int num) throws IOException
   {  
      try {
         int[] image = new int[R[num]*2*R[num]*2];
         for(int x=0; x<R[num]*2; x++) {
            for(int y=0; y<R[num]*2; y++) {
               int i = x+cx[num]-R[num] + (y+cy[num]-R[num])*w;
               int o = x+y*R[num]*2;
               Color rgb = new Color(img[i]);
               image[o] = rgb.getRGB();
            }
         }
         return image;
      } 
      catch(Exception e) { 
         int[] ret = new int[1];
         return ret;
      }
   }
   
   //to ARGB
   public static int[] toARGBCrop(int[] img, double[][] Gxy, int w, int h, int num) throws IOException
   {  
      try {
         int[] image = new int[R[num]*2*R[num]*2];
         for(int x=0; x<R[num]*2; x++) {
            for(int y=0; y<R[num]*2; y++) {
               int i = x+cx[num]-R[num] + (y+cy[num]-R[num])*w;
               int o = x+y*R[num]*2;
               //Color rgb = new Color(img[i],img[i],img[i]);
               Color rgb = new Color(img[i]);
               if(Gxy[i][0]>threshold)
                  rgb = new Color(0,0,0);
               else
                  rgb = new Color(255,255,255);           
               image[o] = rgb.getRGB();
            }
         }
         return image;
      } 
      catch(Exception e) { 
         int[] ret = new int[1];
         return ret;
      }
   }
   
   //clones an array fully
   public static double[][] clone(double[][] arr)
   {
      double[][] ret = new double[arr.length][arr[0].length];
      for(int i=0; i<arr.length; i++)
         for(int x=0; x<arr[0].length; x++)
            ret[i][x] = arr[i][x];
      return ret;
   }
   
   //Copied from StackOverflow  -  written by user Erickson
   public static void delete(File f) throws IOException 
   {
      if (f.isDirectory()) {
         for (File c : f.listFiles())
            delete(c);
      }
      //if (!f.delete())
      //   throw new FileNotFoundException("Failed to delete file: " + f);
   }
}