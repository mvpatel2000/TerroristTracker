import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.text.BreakIterator;
import java.util.Locale;
import java.lang.Math;
import java.util.regex.Pattern;
import com.opencsv.CSVReader;
import java.text.DecimalFormat;
public class UserCaptionData {

    public static void main(String[] args) throws Exception {
         
         fromExcel();
          
    }
         
  private static void fromExcel() throws Exception {
    
    Scanner users = new Scanner(new File("Data/5k Stogram/users.txt"));

    ArrayList<String> userPosts = new ArrayList<String>();
    ArrayList<String> dates = new ArrayList<String>();
    ArrayList<String> captions = new ArrayList<String>();
    Set<String> image_id = new HashSet<String>();
      
    ArrayList<String[]> userData = new ArrayList<String[]>();

    while(users.hasNextLine()) {
        String[] suchUser = {users.nextLine(), 0+"", 0+"", 0+"", 0+"", 0+"", 0+"", 0+"", 0+""}; 
        // 0 userName,1 captionPoints,2 startTime,3 endTime,4 numPosts,5 numNotEnglish. 6 numISIS, 7 numCaptions, 8 numISISPICS
        //System.out.println(suchUser[0]);
        userData.add(suchUser);
    }
    users.close();      
      
      
      CSVReader reader = new CSVReader(new FileReader("Script/data.csv"), ',', '"', 1);
       
      //Read all rows at once
      List<String[]> allRows = reader.readAll();
       
      //Read CSV line by line and use the string array as you want
     for(String[] row : allRows){
            if(image_id.add(row[4])) {
                userPosts.add(row[1]); // usernames
                captions.add(row[9]);
                dates.add(row[11]);
            }
     }
    

    userData = generateData(userData, userPosts, dates, captions);

    ArrayList<String> fUsers = createUserList("Script/flagged.txt");
    ArrayList<String> nfUsers = createUserList("Script/notflagged.txt");
    
    ArrayList<int[]> dipshit = calculateScore(userData, fUsers, nfUsers);

    //***********************************************************************************
    //FOR TODAY ONLY, REWRITING DIPSHIT TO CONTAIN DATA FROM DATA/TESTDATA/MEMES (bots)
    // AND DATA/TESTDATA/DANK (not bots)
    //************************************************************************************
    /**
    dipshit = new ArrayList<int[]>();
    Scanner bots = new Scanner(new File("Data/testdata/memes.txt"));             //****
    Scanner notBots = new Scanner(new File("Data/testdata/dank.txt"));         //****
    ArrayList<String[]> myBots = new ArrayList<String[]>();                     //****
    ArrayList<String[]> myNotBots = new ArrayList<String[]>();                  //****
    while(bots.hasNextLine()) {                                                 //****
        String[] myData = bots.nextLine().split("\t");                          //****
        myBots.add(myData);                                                     //****
    }                                                                           //****    
    bots.close();                                                               //****
    while(notBots.hasNextLine()) {                                              //****
        String[] myData = notBots.nextLine().split("\t");                       //****
        myNotBots.add(myData);                                                  //****
    }                                                                           //****
    notBots.close();                                                            //****
    int combined = myBots.size() + myNotBots.size();                            //****    
    String[] myUsers = new String[combined];    
    double[] myFollowers = new double[combined];
    double[] myFollowing = new double[combined];
    double[] myPosts = new double[combined];
    double[] myDays = new double[combined];
    double[] myPercentEnglish = new double[combined];
    double[] myPercentISIS = new double[combined];
    double[] myNumCaptions = new double[combined];

    for(int i=0; i<myBots.size(); i++) {
        myUsers[i] = myBots.get(i)[0];
        myDays[i] = Double.parseDouble(myBots.get(i)[1]);
        myFollowers[i] = Double.parseDouble(myBots.get(i)[2]);
        myFollowing[i] = Double.parseDouble(myBots.get(i)[3]);
        myPosts[i] = Double.parseDouble(myBots.get(i)[4]);
        myNumCaptions[i] = Double.parseDouble(myBots.get(i)[5]);
        myPercentEnglish[i] = Double.parseDouble(myBots.get(i)[6]);
        myPercentISIS[i] = Double.parseDouble(myBots.get(i)[7]);
    }
    for(int j=0; j<myNotBots.size(); j++) {
        myUsers[myBots.size() +j] = myNotBots.get(j)[0];
        myDays[myBots.size() + j] = Double.parseDouble(myNotBots.get(j)[1]);
        myFollowers[myBots.size() + j] = Double.parseDouble(myNotBots.get(j)[2]);
        myFollowing[myBots.size() + j] = Double.parseDouble(myNotBots.get(j)[3]);
        myPosts[myBots.size() + j] = Double.parseDouble(myNotBots.get(j)[4]);
        myPercentEnglish[myBots.size() + j] = Double.parseDouble(myNotBots.get(j)[5]);
        myPercentISIS[myBots.size() + j] = Double.parseDouble(myNotBots.get(j)[6]);    
    }
    
    for(int q=0; q<myUsers.length; q++) {
        double value = 0;
        boolean one = false;
        boolean two = false;
        boolean three = false;
        if(myDays[q]<15) {
            value+=20;
            three=true;
        }
        if((myPercentEnglish[q]/(myNumCaptions[q]+0.001))<0.01) {
            value+=15;
        }
        if(myFollowing[q]<260) {
            value+=10;
        } if((myPosts[q]/myDays[q])>5) {
            value+=30;
            one=true;
        } if((myPercentISIS[q]/(myNumCaptions[q]+0.001))>0.05) {
            value+=30;
            two=true;
        }
        if(myFollowers[q]<90) {
            value+=10;
        }
        if((myFollowers[q]/myFollowing[q])>1.5) {
            value-=20;
        }                    
        //double swag1 = 20-myDays[q];
        //double swag2 = (300-myFollowing[q])/15;
        //double swag3 = ((myPosts[q]/myDays[q])-3)/2;
        //double swag4 = (200-myFollowers[q])/10;
        //double swag5 = 20 - (myPercentEnglish[q]/(myNumCaptions[q]+0.001));
        //double swag6 = (myPercentISIS[q]/(myNumCaptions[q]+0.001))/3;
        //double swag7 = swag1+swag2+swag3+swag4+swag5+swag6;
        int[] isItCorrect = new int[1];
        for(int threshold=72; threshold<73; threshold++) {
            if(value>threshold) {
                if(q<myBots.size()) { // a bot
                    isItCorrect[threshold-72]=0;    //correctly flagged
                } else {
                    isItCorrect[threshold-72]=1;    //incorrectly flagged
                }    
            } else {
                if(q>myBots.size()) {
                    isItCorrect[threshold-72]=2;  //correctly not flagged  
                } else {
                    isItCorrect[threshold-72]=3;    //incorrectly not flagged
                }
            }                                
        }
        dipshit.add(isItCorrect); 

    }*/
    
    //**************************END WRITING TO FILES********************************
    
    
      System.out.println("Thrshld Correct Missed Correct-NF Fpos %Correct");
      //System.out.println("\tDays\t%Eng\tFlwng\tPst/Day\t%ISIS\tFlwrs\t#Flwrs\t#Flwng\tRatio\tTotal\tUsername");
      int min = 30;
      int max = 120;
      for(int u=min; u<max; u++) {
           int numCorrectFlagged = 0;
           int numIncorrectFlagged = 0;
           int numCorrectNotFlagged = 0;
           int numIncorrectNotFlagged = 0;
          for (int e=0; e<userData.size(); e++) { //change to userData.size when done with dipshit re-data
                if(dipshit.get(e)[u-min]==0) {
                    numCorrectFlagged++;
                } else if (dipshit.get(e)[u-min]==1) {
                    numIncorrectFlagged++;
                    //int myVal = 0;
                    //System.out.print("FalPos \t");
                            /**if(myDays[e]<15) {
                                System.out.print(20 + "\t"); 
                                myVal+=20;  
                            } else {
                                System.out.print(0 + "\t");
                            }    
                            if((myPercentEnglish[e]/(myNumCaptions[e]+0.001))<0.01) {
                                System.out.print(15 + "\t");
                                myVal +=15;
                            } else {
                                System.out.print(0 + "\t");
                            }   
                            if(myFollowing[e]<260) {
                                System.out.print(10 + "\t");
                                myVal+=10;
                            } else {
                                System.out.print(0 + "\t");
                            }
                            if((myPosts[e]/myDays[e])>5) {
                                System.out.print(myPosts[e] + "\t");
                                myVal+=30;
                            } else {
                                System.out.print(myPosts[e] + "\t");
                            }
                            if((myPercentISIS[e]/(myNumCaptions[e]+0.001))>0.05) {
                                System.out.print(30 + "\t");
                                myVal+=30;
                            } else {
                                System.out.print(0 + "\t");
                            }
                            if(myFollowers[e]<90) {
                                System.out.print(10 + "\t");
                                myVal+=10;
                            } else {
                                System.out.print(0 + "\t");
                            }
                            if((myFollowers[e]/myFollowing[e])>1.5) {
                                System.out.print(myFollowers[e] + "\t");
                                System.out.print(myFollowing[e] + "\t");
                                System.out.print(-20 + "\t");
                                myVal-=20;
                            }  else {
                                System.out.print(myFollowers[e] + "\t");
                                System.out.print(myFollowing[e] + "\t");
                                System.out.print(0 + "\t");
                            }
                            System.out.print(myVal + "\t"); 
                            System.out.println(myUsers[e]);*/
                } else if(dipshit.get(e)[u-min]==2) {
                    numCorrectNotFlagged++;
                } else if(dipshit.get(e)[u-min]==3) {
                    numIncorrectNotFlagged++;
                    //int myVal = 0;
                    //System.out.print("Missed \t");
                            /**if(myDays[e]<15) {
                                System.out.print(20 + "\t"); 
                                myVal+=20;  
                            } else {
                                System.out.print(0 + "\t");
                            }    
                            if((myPercentEnglish[e]/(myNumCaptions[e]+0.001))<0.01) {
                                System.out.print(15 + "\t");
                                myVal +=15;
                            } else {
                                System.out.print(0 + "\t");
                            }   
                            if(myFollowing[e]<260) {
                                System.out.print(10 + "\t");
                                myVal+=10;
                            } else {
                                System.out.print(0 + "\t");
                            }
                            if((myPosts[e]/myDays[e])>5) {
                                System.out.print(myPosts[e] + "\t");
                                myVal+=30;
                            } else {
                                System.out.print(myPosts[e] + "\t");
                            }
                            if((myPercentISIS[e]/(myNumCaptions[e]+0.001))>0.05) {
                                System.out.print(30 + "\t");
                                myVal+=30;
                            } else {
                                System.out.print(0 + "\t");
                            }
                            if(myFollowers[e]<90) {
                                System.out.print(10 + "\t");
                                myVal+=10;
                            } else {
                                System.out.print(0 + "\t");
                            }
                            if((myFollowers[e]/myFollowing[e])>1.5) {
                                System.out.print(myFollowers[e] + "\t");
                                System.out.print(myFollowing[e] + "\t");
                                System.out.print(-20 + "\t");
                                myVal-=20;
                            }  else {
                                System.out.print(myFollowers[e] + "\t");
                                System.out.print(myFollowing[e] + "\t");
                                System.out.print(0 + "\t");
                            }
                            System.out.print(myVal + "\t"); 
                            System.out.println(myUsers[e]); */
                }
           }
            double percentCorrect = (100 * (numCorrectFlagged + numCorrectNotFlagged))/(userData.size());
            System.out.println( new DecimalFormat("#.##").format(u) + "\t" + numCorrectFlagged + "\t" + numIncorrectNotFlagged + "\t" +numCorrectNotFlagged + "\t" + numIncorrectFlagged + "\t" +  percentCorrect);
      } 
                 
  }
  
   
   private static ArrayList<String[]> generateData(ArrayList<String[]> userData, ArrayList<String> userPosts, ArrayList<String> dates, ArrayList<String> captions) {
       
        for(int j=1; j<userPosts.size(); j++) {
           
            String thisCaption = captions.get(j).trim();
            //System.out.println(captions.get(j));
            String thisUser = userPosts.get(j);
            int thisDate = Integer.parseInt(dates.get(j));
            
            int value  = 0;     //captionValue
            int userNumber = 0; //index in unique users array
    
            for(int i=0; i<userData.size(); i++) { // finds current user in the ArrayList of users
                if(thisUser.equals(userData.get(i)[0])) {
                    userNumber = i;
                    break;
                } else if(i==userData.size()-1) {
                    System.out.println("NOPE NOPE NOPE WRONG DATA");
                    System.out.println(thisUser);
                    System.out.println(j);
                    System.exit(0);
                } 
            }    
            
            String[] thisUserData = userData.get(userNumber);
            
            //Date Checking
            if(thisUserData[2].equals("0")) {
                thisUserData[2] = Integer.toString(thisDate);
                //System.out.println(thisUserData[2]);
            }    
            if(thisDate>Integer.parseInt(thisUserData[3])) {
                thisUserData[3] = Integer.toString(thisDate);   // if date > latest date
            }
            if(thisDate<Integer.parseInt(thisUserData[2])){
                thisUserData[2] = Integer.toString(thisDate); // if date < first date or first date == 0
            }        
            thisUserData[5] = userData.get(userNumber)[5];
            thisUserData[6] = userData.get(userNumber)[6];
            //End Date Checking
    
            
            //English and ISIS Checking
            boolean containsISIS = thisCaption.toLowerCase().contains("isis");
            
            if(containsISIS) {
                thisUserData[6] = Integer.toString(Integer.parseInt(thisUserData[6]) + 1); // adds to numISIS
            }
            
            if(thisCaption.length()!=0) {    
                thisUserData[7]= Integer.toString((Integer.parseInt(thisUserData[7])+1));  
                String english = thisCaption.replaceAll("\\W", "").trim();
                if(english.length()<thisCaption.length()/2) { // 
                    value += 3;  //contains english under 1/2
                    if(containsISIS) {
                        value+=15; // contains isis
                    }
                } else {
                    thisUserData[5]= Integer.toString((Integer.parseInt(thisUserData[5])+1));  
                    value-=6; // contains english over 1/2
                }       
            } else {
                value +=1; // no caption
            }
            //End English and ISIS Checking
            
            
            //Updating the number of posts and the caption points
            thisUserData[4]=Integer.toString((Integer.parseInt(thisUserData[4])+1)); //update number of posts
            thisUserData[1]=Integer.toString(value); // update value
            
            userData.set(userNumber, thisUserData);
        }
        return userData;
   }
   
   private static ArrayList<String> createUserList(String filename) throws Exception {
        ArrayList<String> myList = new ArrayList<String>();
        Scanner myUsers = new Scanner(new File(filename));
       
        while(myUsers.hasNextLine()) {
            String thisUser = myUsers.nextLine().replaceAll("\\\\n","");
            myList.add(thisUser);
        }
        
        return myList;
   }
   
   private static ArrayList<int[]> calculateScore(ArrayList<String[]> userData, ArrayList<String> fUsers, ArrayList<String> nfUsers) throws Exception {
   
        ArrayList<int[]> dipshit = new ArrayList<int[]>();
       System.out.println("USERNAME\t\tDays\t%Eng\tFlwng\tPst/Day\t%ISIS\tFlwrs\tFlwr/Flwing\t Caption/Posts \t ISISPics \t Total");
        
        double[] myFollowers = new double[userData.size()];
        double[] myFollowing = new double[userData.size()];
        double[] myPosts = new double[userData.size()];
        double[] myVerification = new double[userData.size()];
        double[] myDays = new double[userData.size()];
        double[] myPercentEnglish = new double[userData.size()];
        double[] myPercentISIS = new double[userData.size()];
        double[] myPostsOverDays = new double[userData.size()];
        double[] myPostsOverDays2 = new double[userData.size()];
        double[] myCaptionsOverPosts = new double[userData.size()];
        double[] myISISPics = new double[userData.size()];
        String dank = "";
        String lel = ""; 
        String corrnf = "";
        String corrf = "";
        
        Scanner userpics = new Scanner(new File("Script/output_small.txt"));
        while (userpics.hasNextLine()) {
            String[] splited = userpics.nextLine().split(" ");
            for(int i=0; i<userData.size(); i++) {
                if(userData.get(i)[0].equals(splited[0])) {
                    userData.get(i)[8] = splited[1];
                }
            }
        }
        
        
       for(int i=0; i<userData.size(); i++) {
            Scanner memes = new Scanner(new File("Script/Data/"+userData.get(i)[0]+".txt"));

            String mePosts =  memes.nextLine();
            //System.out.println(userData.get(i)[0]);
            String meFoll = memes.nextLine();
            String meFlwng = memes.nextLine();
            double dankPosts = Double.parseDouble(mePosts.replaceAll("[^\\d.]", ""));
            double followers = Double.parseDouble(meFoll.replaceAll("[^\\d.]", ""));
            double following = Double.parseDouble(meFlwng.replaceAll("[^\\d.]", ""));
            if(mePosts.contains("k")) {
                dankPosts = dankPosts * 1000;
            }
            if(mePosts.contains("m")) {
                dankPosts = dankPosts * 1000000;
            }            
            if(meFoll.contains("k")) {
                followers = followers * 1000;
            }
            if(meFoll.contains("m")) {
                followers = followers * 1000000;
            }
            if(meFlwng.contains("k")) {
                following = following * 1000;
            }
            if(meFlwng.contains("m")) {
                following = following * 1000000;
            }    
            memes.close();
            double numCaptions = Double.parseDouble(userData.get(i)[7]);
            double posts = Double.parseDouble(userData.get(i)[4]);
            double days =  (((double) (System.currentTimeMillis() / 1000L))
                            - Double.parseDouble(userData.get(i)[2]))/86400;
            
            
            double currentScore = Double.parseDouble(userData.get(i)[1]);
            
            
            
            double percentEnglish = Double.parseDouble(userData.get(i)[5]);
            double percentISIS = Double.parseDouble(userData.get(i)[6]);
            
            //change the user age to last post - first post if last post within last two days
            if((((double) (System.currentTimeMillis() / 1000L))-(Double.parseDouble(userData.get(i)[3]))) < 172800) {
                       days =  ((Double.parseDouble(userData.get(i)[3]))
                            - Double.parseDouble(userData.get(i)[2]))/86400; 
            }                
            if(posts==1.0) {
                days+=0.1;
            }
            
            
            myFollowers[i] = followers;
            myFollowing[i] = following;
            myPosts[i] = posts;
            myVerification[i] = dankPosts;
            myDays[i] = days;
            myPercentEnglish[i] = percentEnglish/(numCaptions+0.001);
            myPercentISIS[i] = percentISIS/(numCaptions+0.001);
            myPostsOverDays[i] = posts/Math.max(1,(days+0.001));
            myPostsOverDays2[i] = myPostsOverDays[i]/Math.max(1,(days+0.001));
            myCaptionsOverPosts[i] = numCaptions/(posts+0.001);
            myISISPics[i] = (Integer.parseInt(userData.get(i)[8]))/myPosts[i];
            
            double value = 0;
            double subValue1 = 0;
            double subValue2 = 0;
            double subValue3 = 0;
            double subValue4 = 0;
            double subValue5 = 0;
            double subValue6 = 0;
            double subValue7 = 0;
            double subValue8 = 0;
            double subValue9 = 0;
            if(myDays[i]<15) {
                value+=20;
                subValue1 = 20;
            }
            if((myPercentEnglish[i])<0.01) {
                value+=15;
                subValue2 = 15;
            }
            if(myFollowing[i]<260) {
                value+=10;
                subValue3 = 10;
            } if((myPostsOverDays[i])>5) {
                value+=20;
                subValue4 = 20;
            } if((myPercentISIS[i])>0.05) {
                value+=30;
                subValue5 = 30;
            }
            if(myFollowers[i]<90) {
                value+=10;
                subValue6 = 10;
            }
            if(((myFollowers[i]+100)/(myFollowing[i]+100))>2) {
                value-=20;
                subValue7 = -20;
            } 
            if(myCaptionsOverPosts[i]>0.92) {
                value += 10;
                subValue8 = 10;
            } 


            int min = 30;
            int max = 120;
            int[] isItCorrect = new int[max-min];

            for(int threshold=min; threshold<max; threshold++) {
                if(value>threshold) {
                    if(fUsers.contains(userData.get(i)[0])) {
                       isItCorrect[threshold-min]=0;    //correctly flagged
                       if(threshold==44) {
                            String TMP = userData.get(i)[0];
                            if(TMP.length()>15)
                                TMP = TMP.substring(0,15);
                            corrf += (TMP + "\t\t" + subValue1 + "\t" + subValue2 + "\t" + subValue3 + "\t" + subValue4 + "\t" + subValue5 + "\t" + subValue6 + "\t" + subValue7 + "\t\t" + subValue8 + "\t\t" + subValue9 + "\t\t" + value + "\tCORRECT-F");
                            corrf += "\n";
                        } 
                    } else {
                        isItCorrect[threshold-min]=1;    //incorrectly flagged
                        if(threshold==44) {
                            String TMP = userData.get(i)[0];
                            if(TMP.length()>15)
                                TMP = TMP.substring(0,15);
                            else if(TMP.length()<8)
                                TMP += "   ";
                            dank += (TMP + "\t\t" + subValue1 + "\t" + subValue2 + "\t" + subValue3 + "\t" + subValue4 + "\t" + subValue5 + "\t" + subValue6 + "\t" + subValue7 + "\t\t" + subValue8 + "\t\t" + subValue9 + "\t\t" + value + "\tFPOS");
                            dank += "\n";
                        }  
                    }    
                } else {
                    if(nfUsers.contains(userData.get(i)[0])) {
                        isItCorrect[threshold-min]=2;  //correctly not flagged  
                          if(threshold==44) {
                            String TMP = userData.get(i)[0];
                            if(TMP.length()>15)
                                TMP = TMP.substring(0,15);
                            corrnf += (TMP + "\t\t" + subValue1 + "\t" + subValue2 + "\t" + subValue3 + "\t" + subValue4 + "\t" + subValue5 + "\t" + subValue6 + "\t" + subValue7 + "\t\t" + subValue8 + "\t\t" + subValue9 + "\t\t" + value + "\tCORRECT-NF");
                            corrnf += "\n";
                        }    
                    } else {
                        if(threshold==44) {
                            String TMP = userData.get(i)[0];
                            if(TMP.length()>15)
                                TMP = TMP.substring(0,15);
                            lel += (TMP + "\t\t" + subValue1 + "\t" + subValue2 + "\t" + subValue3 + "\t" + subValue4 + "\t" + subValue5 + "\t" + subValue6 + "\t" + subValue7 + "\t\t" + subValue8 + "\t\t" + subValue9 + "\t\t" + value + "\tMISSED");
                            lel += "\n";
                        }    
                        isItCorrect[threshold-min]=3;    //incorrectly not flagged
                    }
                }                                
            }
            dipshit.add(isItCorrect);
            
        }
        String a = "";
        String b = "";
        for (int q=0; q<userData.size(); q++) {
            if(nfUsers.contains(userData.get(q)[0])) {
               a += ("Should not be flagged "+ userData.get(q)[8] + " " + Double.parseDouble(userData.get(q)[8])/Double.parseDouble(userData.get(q)[4]) + "\n");
            } else if (fUsers.contains(userData.get(q)[0])) {
               b += ("Should be flagged "+ userData.get(q)[8] + " " + Double.parseDouble(userData.get(q)[8])/Double.parseDouble(userData.get(q)[4]) + "\n");
            }   
        } 
        System.out.println(a);
        System.out.println(b);       
        System.out.println(dank);
        System.out.println(lel);
        System.out.println(corrf);
        System.out.println(corrnf);
        //Write originial (raw) data to file
        PrintWriter excel = new PrintWriter(new File("originial.txt"));
        excel.println("Username\tFollowers\tFollowing\tPosts\tVerify\tDays\t%English\t%ISIS\tPosts/Days\tCaptions/Posts\tISISPics");
        for(int i=0; i<userData.size(); i++) {
            excel.println(userData.get(i)[0] + "\t\t\t" + myFollowers[i] + "\t" + myFollowing[i] + "\t" + myPosts[i] + "\t" + myVerification[i] + "\t" + myDays[i] + "\t" + 
                    myPercentEnglish[i] + "\t" + myPercentISIS[i] + "\t" + myPostsOverDays[i] + "\t" + myCaptionsOverPosts[i] + "\t" + myISISPics[i]);              
        }
        excel.close();    
        
        //Z-Scores for data to go into network
        myFollowers = calculateStats(myFollowers);
        myFollowing = calculateStats(myFollowing);
        myPosts = calculateStats(myPosts);
        myDays = calculateStats(myDays);
        myPercentEnglish = calculateStats(myPercentEnglish);
        myPercentISIS = calculateStats(myPercentISIS);
        myPostsOverDays = calculateStats(myPostsOverDays);
        myCaptionsOverPosts = calculateStats(myCaptionsOverPosts);
        myISISPics = calculateStats(myISISPics);
        //Write modified data to input files and verification file
        excel = new PrintWriter(new File("verifyer.txt"));
        int m=0;
        int n=fUsers.size();
        for(int i=0; i<userData.size(); i++) {
            PrintWriter rqr;
            
            if(fUsers.contains(userData.get(i)[0])) {
                rqr = new PrintWriter(new File("Data/picsdata/" + m + "_1_input.txt"));
                m++;
            } else {
                rqr = new PrintWriter(new File("Data/picsdata/" + n + "_0_input.txt"));
                n++;
            }
            excel.println(myFollowers[i] + " " + myFollowing[i] + " " + myPosts[i] + " " + myDays[i] + " " + 
                    myPercentEnglish[i] + " " + myPercentISIS[i] + " " + myPostsOverDays[i] + " " + myCaptionsOverPosts[i] + " " + myISISPics[i]); 
            rqr.println(myFollowers[i] + " " + myFollowing[i] + " " + myPosts[i] + " " + myDays[i] + " " + 
                    myPercentEnglish[i] + " " + myPercentISIS[i] + " " + myPostsOverDays[i] + " " + myCaptionsOverPosts[i] + " " + myISISPics[i]); 
           
           rqr.close();               
        }
        excel.close();  
        
        return dipshit;  
   }   
   
   
   private static double[] calculateStats (double[] myDouble) {
        double[] dank = myDouble.clone(); //create a copy because Arrays.sort() below messes up order
        double[] zScore = new double[myDouble.length];
        double trimmedMean = trimmedMean(myDouble);
        double variance = 0;
        double sum = 0;
        for(int i=0; i<dank.length; i++) {
            sum+=dank[i];
            variance += Math.pow(dank[i]-trimmedMean, 2);
        }
        variance = variance/dank.length;
        double standDev = Math.sqrt(variance);
        
        System.out.println(trimmedMean);
        System.out.println(standDev);
        System.out.println();
        for(int j=0; j<dank.length; j++) {
            double thisZScore = (dank[j]-trimmedMean)/standDev;
            zScore[j] = thisZScore;
        }
        
        return zScore;    
                
   }
   
   
   private static double trimmedMean(double[] data) {
       Arrays.sort(data); 
       
       int lowerBound = 1*(data.length/10);
       int upperBound = 9*(data.length/10);
       double sum = 0;
       
       for(int i=lowerBound; i<upperBound; i++) { 
            sum+=data[i];
       }
       
       return sum/(upperBound-lowerBound);
   }                                       
        
}