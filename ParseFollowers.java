import java.io.*;
import java.util.*;

public class ParseFollowers {
   
   public static void main (String[] args) throws Exception {
        
        File folder = new File("Data/findusers/unformatted");
        File[] listOfFiles = folder.listFiles();
        Set<String> myUsers = new HashSet<String>();
        for (int i = 0; i < listOfFiles.length; i++) {
          File file = listOfFiles[i];
          if (file.isFile() && file.getName().endsWith(".txt")) {
            Scanner sc = new Scanner(file);
            PrintWriter w = new PrintWriter(new File("Data/findusers/formatted/" + file.getName()));
            sc.nextLine();
            String user = sc.nextLine();
            w.println(user);
            myUsers.add(user);
            while(sc.hasNextLine()) {
                 String thisLine = sc.nextLine();
                 if(thisLine.equals("Followers") || thisLine.equals("Follow")) {
                    String dank = sc.nextLine();
                    if(!(dank.equals("Close") || dank.equals("Options"))) {
                       String followers = sc.nextLine();
                       w.println(followers); // follower names
                       myUsers.add(followers);
                    }   
                 }   
              }
            w.close();
          } 
        }
        PrintWriter dank = new PrintWriter("Data/listofUsers.txt");
        for(String s: myUsers) {
            dank.println(s);
        }
        dank.close();    
   }   
   
}   