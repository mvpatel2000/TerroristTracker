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
public class FormatData {
        
    public static ArrayList<String[]> cities = new ArrayList<String[]>();
    public static ArrayList<String> keywords = new ArrayList<String>();
    public static ArrayList<int[]> weights = new ArrayList<int[]>();
    
    public static void main(String[] args) throws Exception {
        

        
        try {
            Scanner scCities = new Scanner(new File("Data/cities.txt"));
            Scanner scKeywords = new Scanner(new File("Data/keywords.txt"));
            scKeywords.nextLine();
            
            while(scCities.hasNextLine()) {
                String[] splited = scCities.nextLine().toLowerCase().split("\\s+");
                cities.add(splited);
            }
            while(scKeywords.hasNextLine()) {
                keywords.add(scKeywords.nextLine().toLowerCase());
                String[] splited = scKeywords.nextLine().split("\\s+");
                int[] numbers = new int[splited.length];
                for (int i=0; i<splited.length; i++) {
                    numbers[i] = Integer.parseInt(splited[i]);
                }
                weights.add(numbers);
            }
            
            scCities.close();
            scKeywords.close();
            
        } catch (Exception e) {
            System.out.println("Cannot find files Data/cities.txt and/or Data/keywords.txt");
        }
        
        for (String[] arr : cities) {
            System.out.print(Arrays.toString(arr));
        }
        System.out.println();
        System.out.println(keywords);        
        for (int[] arr : weights) {
            System.out.print(Arrays.toString(arr));
        }   
        //dankMemes();
        fromExcel();
        //ArrayList<String> format1List = formatData("Data/4k Stogram/data.txt");
        //formatData2(format1List);
        //testMyData();
        
            //Build reader instance

   
    }
    
    private static void formatData2(ArrayList<String> format1List) {
        int startTime = 0;
        int endTime = 0;
        int numImages = 0;
        String picture_url = "";
        String caption = "";
        String[][] data2 = new String[format1List.size()][4];
        List<PrintWriter> writers = new ArrayList<PrintWriter>();
        ArrayList<String[]> userTimes = new ArrayList<String[]>();
        
        for(int i=2; i<format1List.size(); i++) {
            String thisData = format1List.get(i).substring(1, format1List.get(i).length()-1);
            System.out.println(thisData);
            data2[i] = thisData.split(",");
        }    
        
        
        try {
            Scanner sc = new Scanner(new File("Data/4k Stogram/users.txt"));     
            while ( sc.hasNextLine()) { // creates a printwriter and a user for each line
                String user = sc.nextLine();
                String[] userNameAndTimes = {user, 0+"", 0+"", 0+""};
                userTimes.add(userNameAndTimes);
                PrintWriter w = new PrintWriter("Data/userdata/" + user + ".txt", "UTF-8");
                writers.add(w);
            }
        } catch (Exception e) {
            System.out.println("users.txt does not exist.");
        }
        
        for(int i=2; i<data2.length; i++) {                 // the entire data
            int r=0;
            for (int j=0; j<userTimes.size(); j++) {        // check for the "j" number of users
                 if(data2[i][0].equals(userTimes.get(j)[0])) {  // the user matches with this data point
                    if(Integer.parseInt(data2[i][2]) > Integer.parseInt(userTimes.get(j)[1])) {
                        userTimes.get(j)[1] = data2[i][2];  // sets largest timestamp
                    } 
                    if ((Integer.parseInt(data2[i][2]) < Integer.parseInt(userTimes.get(j)[2])) || userTimes.get(j)[2].equals("0")) {
                        userTimes.get(j)[2] = data2[i][2];  // sets smallest timestamp
                    }
                    int q = Integer.parseInt(userTimes.get(j)[3]);
                    q+=1;
                    userTimes.get(j)[3] = Integer.toString(q);
                    r=j;
                    break;
                 }
            }
            String separator = System.getProperty("line.separator");
            writers.get(r).println("image"+separator+data2[i][3]); //writes image file
            writers.get(r).println(formatLines(excessiveHashtags(doesItEnglish(data2[i][1])))); //writes caption
        }
        for(int k=0; k<userTimes.size(); k++) { // for each of the "j" users
            int userTotalTime = Math.abs(Integer.parseInt(userTimes.get(k)[1]) - Integer.parseInt(userTimes.get(k)[2]));
            int userTotalPosts =Integer.parseInt(userTimes.get(k)[3]);
            writers.get(k).println(userTotalTime); // write the user's age
            writers.get(k).println(userTotalPosts);
        } 
        
        for (PrintWriter w : writers) { // closes all writers
            w.close();
        }   
                    
    }
    private static void testMyData() throws Exception {
        Scanner sc = new Scanner(new File("C:/Users/niksar/Desktop/Instagram/Data/4k Stogram/users.txt"));
        while(sc.hasNextLine()) {
            String user = sc.nextLine();
            PrintWriter w = new PrintWriter("C:/Users/niksar/Desktop/Instagram/Data/finaldata/" + user + ".txt", "UTF-8");
            Scanner newSc = new Scanner(new File("C:/Users/niksar/Desktop/Instagram/Data/userdata/" + user + ".txt"));
            int numFlagged = 0;
            String totalTime = "";
            String totalPosts = "";
            while(newSc.hasNextLine()) {
                String myLine = newSc.nextLine();
                if(myLine.equals("image")) {
                    w.println(newSc.nextLine());
                } else if (!myLine.equals("")) {
                    totalTime = myLine;
                    totalPosts = newSc.nextLine();
                }
                String captionTag = "";
                if(newSc.hasNextLine()) {
                    captionTag = newSc.nextLine();
                }
                double totalFlag = 0.0;
                boolean flagPost = false;
                while(captionTag.equals("caption")) { // There is another sentence in the caption
                        String mySentence = newSc.nextLine();
                        double[] magPolarity = {Math.random(), 
                                                (Math.random()*2)-1 };
                        String[] values = newSc.nextLine().split("\\s+");
                        double[] doubleValues = { Double.parseDouble(values[0]), Double.parseDouble(values[1]) };
                        double flag = 0;
                        if(magPolarity[1]<0.0) {
                            flag = doubleValues[1]*Math.abs(magPolarity[1])*magPolarity[0];
                        } else {
                            flag = doubleValues[0]*Math.abs(magPolarity[1])*magPolarity[0];
                        }
                        totalFlag+=flag;     
                        captionTag = newSc.nextLine(); // Should read "caption" or a number or 0
                }
                if(!captionTag.equals("")) { //if captionTag = number
                    double sentencesWithout = Double.parseDouble(captionTag)*6.0;
                    totalFlag = totalFlag-sentencesWithout;
                    if(totalFlag >10.0) {
                        flagPost = true;
                        numFlagged+=1;
                        w.println("flagged");
                    } 
                    w.println("not flagged");
                }
                //nextLine either==image or doesn't exist
            }
            w.println(totalTime);
            w.println(totalPosts);
            if(numFlagged>3) {
                w.println("USER iS flagged");
            }
            w.close();                  
        }
    }    
    private static String doesItEnglish(String caption) {
        String english = caption.replaceAll("\\W", "").trim();
        english = english.replaceAll("_", "").trim();
        english = english.replaceAll("#", "").trim();
        english = english.replaceAll("[^A-Za-z]+", "").trim();
        if(english.length()==0) {
            return "";
        }   
        else {
           if(english.length()<(caption.length()/10)) {
                return "";
           } else {    
               System.out.println(english + " darn " + caption);
               return caption;
            }   
        }
    }    
    
    private static String excessiveHashtags(String caption) {
        String[] parts = caption.split("(?=\\b[# ])");
        String total = "";
        boolean doubleHash = false;
        for(int i=0; i<parts.length-1; i++) {
            parts[i] = parts[i].trim();
            parts[i+1] = parts[i+1].trim();

            if(parts[i].substring(0,1).equals("#") && parts[i+1].substring(0,1).equals("#")) {
                parts[i] = "";
                doubleHash = true;
            } else if(parts[i].charAt(0)=='#' && doubleHash==true) {
                parts[i] = "";
            } else {
                doubleHash = false;
                if(parts[i].substring(0,1).equals("#")) {   
                    total+= parts[i].substring(1) + " ";
                } else {    
                    total+=parts[i] + " ";
                }    
            }          
        }
        return total;
    }
    
        //see if caption contains keywords or cities
        //if it does, split string by periods
        //capitalize first word after period if not already
        //run through google syntax analysis
        //if it contains multiple keywords and they are nouns in the same sentence,
        // split + periods after last object of the verb in the middle if verb in the middle
        // capitalize first word after period 
        // run through connotation analysis
        // use formula for polarity / magnitude of sentences with keywords
        // return the old caption \n new caption \n score
        
    private static ArrayList<String> keyWords(String caption) {
        
        ArrayList<String> captionWords = new ArrayList<String>();
        
        caption = caption.trim();
        if(caption.equals("")) {
            return null;
        }    
        captionWords.add(caption);
        
        for(int i=0; i<keywords.size(); i++) {
            if(caption.toLowerCase().contains(keywords.get(i))) {
                captionWords.add(keywords.get(i));
                //caption = caption + " keyword "+ keywords.get(i);
            }
        }
        for(int i=0; i<cities.size(); i++) {
            if(caption.toLowerCase().contains(cities.get(i)[cities.get(i).length-1])) {
                captionWords.add(cities.get(i)[cities.get(i).length-1]);
                //caption = caption + " city "+ cities.get(i)[cities.get(i).length-1];
            }
        } 
        caption.substring(0, 1).toUpperCase();  
        if((caption.charAt(caption.length()-1)!='.') || (caption.charAt(caption.length()-1)!='!') || (caption.charAt(caption.length()-1)!='?')) {
            caption = caption + ".";
        }    
        if(captionWords.size()!=0) {
            
        } else {
            return null;
        }                 
        return captionWords;

    }        
    private static String formatLines (String caption) {
        // Separates captions into sentences (BreakIterator)
        // Finds which sentences have keywords
        // Stores number of non-keyword sentences
        // Deltes non-keyword sentences
        // For each keyword-having sentence:
        // Finds all keywords
        // Average keywords positive values, store
        // Average keywords negative values, store
        // return stuff
        caption = caption.trim();
        if(!caption.equals("")){
            if((caption.charAt(caption.length()-1)!='.') || (caption.charAt(caption.length()-1)!='!') || (caption.charAt(caption.length()-1)!='?')) {
                caption = caption + ".";
            }  
        }
        ArrayList<String> sentences = new ArrayList<String>();
        int numNotKeyword = 0;
        boolean notCity = false;
        String returnThis = "";
       
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        String source = caption;
        iterator.setText(source);
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            //System.out.println(source.substring(start,end));
            sentences.add(source.substring(start, end));
        }
        for(int i=0; i<sentences.size(); i++) {
            double totalPositive = 0.0;
            double totalNegative = 0.0;
            ArrayList<String> myCaption = keyWords(sentences.get(i));
            System.out.println(myCaption + "myCaption");
            if(myCaption!=null) { // contains keywords
                for(int j=1; j<myCaption.size(); j++) { // for each keyword
                    for(int k=0; k<keywords.size(); k++) { // find keyword position
                        if(myCaption.get(j).equalsIgnoreCase(keywords.get(k))) {
                            System.out.println(keywords.get(k) + " "  + weights.get(k)[0] + " " +weights.get(k)[1]);
                            totalPositive += weights.get(k)[0]; // positive keyword weight
                            totalNegative += weights.get(k)[1]; // negative keyword weight
                            notCity = true;
                        }
                    }
                    if(notCity==false) { 
                        System.out.println("city +2 -1");
                        totalPositive += 2; // positive keyword weight
                        totalNegative += -1; // negative keyword weight
                    }
                    notCity = false;
                }
                totalPositive = totalPositive / (myCaption.size()-1); // avgPostive
                totalNegative = totalNegative / (myCaption.size()-1); // avgNegative
                totalPositive = Math.round(totalPositive*100);
                totalPositive = totalPositive/100;
                totalNegative = Math.round(totalNegative*100);
                totalNegative = totalNegative/100;
                String separator = System.getProperty("line.separator");
                returnThis += "caption" + separator + sentences.get(i) + separator + totalPositive + " " + totalNegative + separator;
            } else {
                numNotKeyword += 1;
            }                    
        }
        returnThis+=numNotKeyword;
        System.out.println(returnThis);        
        return returnThis;
    }            
     private static ArrayList<String> formatData(String posLoc) {
      ArrayList<String> dataPoints = new ArrayList<String>();
      Set<String> result = new TreeSet<String>();
      BufferedReader br = null;
      try {
         br = new BufferedReader(new FileReader(new File(posLoc)));
         String available;
         String dataPoint = "";
         String all = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890 #_+,.-_=/?\\'';:|\"\"a";
         boolean quotes = false;
         while((available = br.readLine()) != null) {    
            try {
                for(int i=0; i<available.length()-1; i++) {
                    boolean contains = false;
                    for(int j=0; j<all.length()-1; j++) {
                        if(available.substring(i, i+1).equals(all.substring(j, j+1))) {
                            contains = true;
                        }
                    }
                    if(contains == false) {
                        //System.out.println("FALSE FALSE");
                        //System.out.println(available.substring(i, i+1));
                        available = available.substring(0, i) + available.substring(i+1);
                        i-=1;
                    }
                }                
                if(available.substring(0,1).equals("\"")) {
                    dataPoints.add(dataPoint);
                    dataPoint = available;
                    
                 } else {    
                    dataPoint += available;
                    
                 }  
             } catch (Exception e) {
                System.out.println(available);
            }    
               
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         if (br != null) {
            try {
               br.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
      
      return dataPoints;
  }
  private static void dankMemes() throws Exception {
    Scanner flaggedUsers = new Scanner(new File("Script2/flagged.txt"));
    Scanner notFlaggedUsers = new Scanner(new File("Script2/notflagged.txt"));
    PrintWriter w = new PrintWriter(new File("flagData.txt"));
    PrintWriter n = new PrintWriter(new File("notFlagData.txt"));
    ArrayList<String> fUsers = new ArrayList<String>();
    ArrayList<String> nfUsers = new ArrayList<String>();
    
    while(flaggedUsers.hasNextLine()) {
        String thisUser = flaggedUsers.nextLine().replaceAll("\\\\n","");
        fUsers.add(thisUser);
    }
    while(notFlaggedUsers.hasNextLine()) {
        String thisUser = notFlaggedUsers.nextLine().replaceAll("\\\\n","");
        nfUsers.add(thisUser);
    }
    for(int i=0; i<fUsers.size(); i++) {
        System.out.println(fUsers.get(i));
        Scanner k = new Scanner(new File("Script2/Data/" + fUsers.get(i) + ".txt"));
        int posts = Integer.parseInt(k.nextLine().replaceAll("[^\\d.]", ""));
        int followers = Integer.parseInt(k.nextLine().replaceAll("[^\\d.]", ""));
        int following = Integer.parseInt(k.nextLine().replaceAll("[^\\d.]", ""));
        w.println(posts + " " + followers + " " + following);   
    }
    for(int j=0; j<nfUsers.size(); j++) {
        System.out.println(nfUsers.get(j));
        Scanner m = new Scanner(new File("Script2/Data/" + nfUsers.get(j) + ".txt"));
        int posts = Integer.parseInt(m.nextLine().replaceAll("[^\\d.]", ""));
        int followers = Integer.parseInt(m.nextLine().replaceAll("[^\\d.]", ""));
        int following = Integer.parseInt(m.nextLine().replaceAll("[^\\d.]", ""));
        n.println(posts + " " + followers + " " + following);
    }
    
    w.close();
    n.close();
  }            
  private static void fromExcel() throws Exception {
    
    Scanner users = new Scanner(new File("Data/4k Stogram/users.txt"));

    ArrayList<String> userPosts = new ArrayList<String>();
    ArrayList<String> dates = new ArrayList<String>();
    ArrayList<String> captions = new ArrayList<String>();
    
      CSVReader reader = new CSVReader(new FileReader("Data/excel/data.csv"), ',', '"', 1);
       
      //Read all rows at once
      List<String[]> allRows = reader.readAll();
       
      //Read CSV line by line and use the string array as you want
     for(String[] row : allRows){
        userPosts.add(row[1]); // usernames
        captions.add(row[9]);
        dates.add(row[11]);
     }
    
    ArrayList<String[]> userData = new ArrayList<String[]>();

    while(users.hasNextLine()) {
        String[] suchUser = {users.nextLine(), 0+"", 0+"", 0+"", 0+"", 0+"", 0+"", 0+""}; 
        // 0 userName,1 captionPoints,2 startTime,3 endTime,4 numPosts,5 numNotEnglish. 6 numISIS, numCaptions
        
        userData.add(suchUser);
    }
    users.close();

    userData = generateData(userData, userPosts, dates, captions);

    ArrayList<String> fUsers = createUserList("Script2/flagged.txt");
    ArrayList<String> nfUsers = createUserList("Script2/notflagged.txt");
    
    ArrayList<int[]> tempvar = calculateScore(userData);

    
    //**************************END WRITING TO FILES********************************
      System.out.println("Thrshld Correct Fpos Correct-NF Missed %Correct");
      for(double u=0; u<2; u+=0.01) {
           int numCorrectFlagged = 0;
           int numIncorrectFlagged = 0;
           int numCorrectNotFlagged = 0;
           int numIncorrectNotFlagged = 0;
          for (int e=0; e<userData.size(); e++) {
                if(tempvar.get(e)[(int)(u*100)]==0) {
                    numCorrectFlagged++;
                } else if (tempvar.get(e)[(int)(u*100)]==1) {
                    numIncorrectFlagged++;
                } else if(tempvar.get(e)[(int)(u*100)]==2) {
                    numCorrectNotFlagged++;
                } else if(tempvar.get(e)[(int)(u*100)]==3) {
                    numIncorrectNotFlagged++;
                }
           }
            double percentCorrect = (100 * (numCorrectFlagged + numCorrectNotFlagged))/(userData.size());
            System.out.println( new DecimalFormat("#.##").format(u) + "\t" + numCorrectFlagged + "\t" + numIncorrectFlagged + "\t" + 
                            numCorrectNotFlagged + "\t" + numIncorrectNotFlagged + "\t" + percentCorrect);
      }            
  }
  
   
   private static ArrayList<String> generateData(ArrayList<String> userData, ArrayList<String> userPosts, ArrayList<String> dates, ArrayList<String> captions) {
       
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
                }    
            }    
            
            String[] thisUserData = userData.get(userNumber);
            
            //Date Checking
            if(thisUserData[2].equals("0")) {
                thisUserData[2] = Integer.toString(thisDate);
                System.out.println(thisUserData[2]);
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
   
   private static ArrayList<String> createUserList(String filename) {
        ArrayList<String> myList = new ArrayList<String>();
        Scanner myUsers = new Scanner(new File(filename));
       
        while(myUsers.hasNextLine()) {
            String thisUser = myUsers.nextLine().replaceAll("\\\\n","");
            myList.add(thisUser);
        }
        
        return myList;
   }
   
   private ArrayList<Integer> calculateScore(ArrayList<String> userData) {
   
        ArrayList<int> tempvar = new ArrayList<int>();
        
       for(int i=0; i<userData.size(); i++) {
            Scanner memes = new Scanner(new File("Script2/Data/"+userData.get(i)[0]+".txt"));
            
            memes.nextLine();
            System.out.println(userData.get(i)[0]);
            double followers = Double.parseDouble(memes.nextLine().replaceAll("[^\\d.]", ""));
            double following = Double.parseDouble(memes.nextLine().replaceAll("[^\\d.]", ""));
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
            PrintWriter rqr;
            if(fUsers.contains(userData.get(i)[0])) {
                rqr = new PrintWriter(new File("Data/userdata/" + i + "_1.txt"));
            } else {
                rqr = new PrintWriter(new File("Data/userdata/" + i + "_0.txt"));
            }    
                days = days-17;
                followers = followers-180;
                following = following-260;
                posts = posts;
                numCaptions = numCaptions;
                if((percentEnglish/(numCaptions+0.001))<0.1) {
                    percentEnglish = 0;
                } else {
                    percentEnglish = 1;
                }
                if((percentISIS/(numCaptions+0.001))<0.05) {
                    percentISIS = 0;
                } else {
                    percentISIS = 1;
                }        
                rqr.println(days + " " + followers + " " + following + " "  + posts + " "  + numCaptions
                        + " " + percentEnglish + " " + percentISIS);
                rqr.close();        
            //CALCULATING THE SCORE
            /**
            double swag1 = 20-days;
            double swag2 = (300-following)/15;
            double swag3 = ((posts/days)-3)/2;
            double swag4 = (200-followers)/10;
            double swag5 = 20 - (percentEnglish/(numCaptions+0.001));
            double swag6 = (percentISIS/(numCaptions+0.001))/3;
            **/
            double swag1 = (percentISIS/(numCaptions+0.001));
            double swag2 = (1-(percentEnglish/(numCaptions+0.001)));
            double swag3 = 10*(posts/days);
            double swag4 = (1-(1/days))*10;
            double swag5 = (numCaptions/posts);
            double swag6 = (100/(followers+following));
            double swag7 = (swag1 * swag2 * swag3 * swag4 * swag5) + swag6;
            int[] isItCorrect = new int[200];
            for(double threshold=0; threshold<2; threshold+=0.01) {
                if(swag7>threshold) {
                    if(fUsers.contains(userData.get(i)[0])) {
                        isItCorrect[(int)(threshold*100)]=0;    //correctly flagged
                    } else {
                        isItCorrect[(int)(threshold*100)]=1;    //incorrectly flagged
                    }    
                } else {
                    if(nfUsers.contains(userData.get(i)[0])) {
                        isItCorrect[(int)(threshold*100)]=2;  //correctly not flagged  
                    } else {
                        isItCorrect[(int)(threshold*100)]=3;    //incorrectly not flagged
                    }
                }                                
            }
            tempvar.add(isItCorrect);
       
        } 
        return tempvar;
   }   
   
   private static double trimmedMean(double[] data) {
       Arrays.sort(data); 
       
       int lowerBound = 2*(data.length/10);
       int upperBound = 8*(data.length/10);
       double sum = 0;
       
       for(int i=lowerBound; i<upperBound; i++) {   
            sum+=data[i];
       }
       
       return sum/(upperBound-lowerBound);
   }                                       
        
}