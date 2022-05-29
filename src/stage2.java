import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.io.*;  

class MyClient{  
	public static void main(String args[])throws Exception{  

		String mess = ""; //last message
		String[] minfo;   //specfic information from last message

		String[] first;
		String[] line;
		String jobID = ""; 
		ArrayList<String[]> data = new ArrayList<String[]>();
		ArrayList<String[]> boot = new ArrayList<String[]>();
		String[] recs; 
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
				//Sends GETS Capable Core/Memory/Disk
				
			   dout.write(("GETS Capable " + minfo[4] + " " + minfo[5] + " " + minfo[6] + " \n").getBytes()); 
			   recs = bin.readLine().split(" "); //DATA nRecs Size
			   int nRecs = Integer.parseInt(recs[1]); 
			   dout.write(("OK\n").getBytes()); 

				 //stores all data into data
				 for (int i = 0;i<nRecs;i++){
					 line = bin.readLine().split(" ");
					 data.add(line);

					 int dcore = Integer.parseInt(line[4]); //server state core
					 int jcore = Integer.parseInt(minfo[4]); //required core for job
					 dlist.add(dcore-jcore);
					 

					//  if ((dcore >= jcore)){
					// 	 data.add(line);
					//  }


					// if ((line[2].startsWith("booting"))){ //**** better fc
					// 	boot.add(line);
					// }
					// if ((line[2].startsWith("active ")))
					// data.add(line);
				 }
				 Integer max_diff = Collections.max(dlist);
				 int inx = dlist.indexOf(max_diff);
				 first = data.get(inx);


			   //first = data.get(0);
               dout.write(("OK\n").getBytes());  
               bin.readLine(); 

			   if (boot.size() >= 1){
				   for(String[] str: boot){
					   //LSTJ process
					   dout.write(("LSTJ " + str[0] + " " + str[1] + "\n").getBytes()); 
					   recs = bin.readLine().split(" "); //DATA nRecs Size
			           nRecs = Integer.parseInt(recs[1]); 
					   dout.write(("OK\n").getBytes());
					   for (int i = 0;i<nRecs;i++){
						   bin.readLine();
					   }
				   }
			   }

			   data.clear();
			   boot.clear();
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
