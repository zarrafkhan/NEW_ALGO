import java.net.*;
import java.util.ArrayList;
//import java.util.Collections;
import java.io.*;  

class MyClient{  
	public static void main(String args[])throws Exception{  

		String mess = ""; //last message
		String[] minfo;   //specfic information from last message

		String[] first;
		//String[] line;
		String jobID = ""; 
		ArrayList<String[]> data = new ArrayList<String[]>();
        String[] recs;
        String[] recsC;
		ArrayList<Integer> dlist = new ArrayList<Integer>();
		boolean a = true;

		try{
			//default server connection
			String name = System.getProperty("user.name");
			Socket s=new Socket("localhost",50000);  
			DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
			BufferedReader bin = new BufferedReader(new InputStreamReader(s.getInputStream()));  

			//basic server exchanges
			dout.write(("HELO\n").getBytes());
			bin.readLine();
			dout.write(("AUTH " + name + "\n").getBytes());
			bin.readLine();

			dout.write(("REDY\n").getBytes());      
			mess = bin.readLine(); //recieves jobn first           

		while(a){

             if(mess.startsWith("JOBN")){

				minfo = mess.split(" "); 
			    jobID = minfo[2];
				//Sends GETS Available Core/Memory/Disk
				
			   dout.write(("GETS Avail " + minfo[4] + " " + minfo[5] + " " + minfo[6] + " \n").getBytes()); 
			   recs = bin.readLine().split(" "); //DATA nRecs Size
			   int nRecs = Integer.parseInt(recs[1]); 
               dout.write(("OK\n").getBytes()); 
               
               //No available servers, go to first capable
               if (Integer.parseInt(recs[1]) == 0){
                   bin.readLine();
                   dout.write(("OK\n").getBytes());
                   bin.readLine();
                   bin.readLine();

                   dout.write(("GETS Capable " + minfo[4] + " " + minfo[5] + " " + minfo[6] + " \n").getBytes());
                   recsC = bin.readLine().split(" "); //DATA nRecs Size
                   nRecs = Integer.parseInt(recsC[1]); 
                   dout.write(("OK\n").getBytes()); 
               }

                 //stores all data into data
                 first = bin.readLine().split(" ");
				 for (int i = 0;i<nRecs -1;i++){
					//  line = bin.readLine().split(" ");
					//  data.add(line);

					//  int dcore = Integer.parseInt(line[4]); //server state core
					//  int jcore = Integer.parseInt(minfo[4]); //required core for job
                    //  dlist.add(dcore-jcore);
                    bin.readLine();
				 }
				//  Integer max_diff = Collections.max(dlist);
				//  int inx = dlist.indexOf(max_diff);
				//  first = data.get(inx);

               dout.write(("OK\n").getBytes());  
               bin.readLine(); 

			   data.clear();
			   dlist.clear();
               dout.write(("SCHD " + jobID + " " + first[0] + " " + first[1] + "\n").getBytes());  
               bin.readLine();
              
			   dout.write(("REDY\n").getBytes());   
               mess = bin.readLine();   //next message
			}  
			
			 if(mess.startsWith("JCPL")){
				dout.write(("REDY\n").getBytes()); 
				mess = bin.readLine(); //recieves jobn first           
			}

			if(mess.startsWith("NONE")){
				a = false;
			}
		}

			 dout.write(("QUIT\n").getBytes());
			 dout.flush();
			 bin.readLine();
			 dout.close();  
			 s.close(); 

		}catch(Exception e){System.out.println(e);}
	} 
}
