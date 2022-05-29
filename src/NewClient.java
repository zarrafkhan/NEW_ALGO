import java.net.*;
import java.io.*;  

class NewClient{  
	public static void main(String args[])throws Exception{  

		String mess = ""; //last message
        String[] minfo;   //specfic information from last message
        
		String[] first;   
		String jobID = ""; 
        String[] recs; //nRecs for gets available
        String[] recsC;//nRecs for gets Capable
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

        //exits infinite loop if client recieves NONE 
		while(a){

             if(mess.startsWith("JOBN")){

                //Refresh values for new job
				minfo = mess.split(" "); 
                jobID = minfo[2];
                
			   //Sends GETS Avail Core Memory Disk
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

                 //only store the first record and skip the rest
                 first = bin.readLine().split(" ");
				 for (int i = 0;i<nRecs -1;i++){
                    bin.readLine();
				 }

               dout.write(("OK\n").getBytes());  
               bin.readLine(); 
               //SCHD jobID server_name server_id
               dout.write(("SCHD " + jobID + " " + first[0] + " " + first[1] + "\n").getBytes());  
               bin.readLine();
              
			   dout.write(("REDY\n").getBytes());   
               mess = bin.readLine();   //next message
			}  
			
			 if(mess.startsWith("JCPL")){ //skip job completion msg
				dout.write(("REDY\n").getBytes()); 
				mess = bin.readLine();           
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