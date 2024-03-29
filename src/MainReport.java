import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MainReport {


    public static void main(String[] args) throws ParseException {

        if (args.length != 2) {
            System.out.println("You need to specify the paths!");
            return;
        }
        
        System.out.println("Our paths are: " + " \n "+ args[0] + " \n " + args[1]);

        try {
        	CreateCssFile(args[1]);
            File f = new File(args[1] + "/MainReport.htm");
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write("<html>");
            bw.write("<head>");
            bw.write("<title> Exercise-1 </title>");
            bw.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"mystyle.css\">");
            bw.write("</head> <body class = \"body\">");
            bw.write("<br><div class = \"title\"> Report for  <a class = \"link\" href=\" https://github.com/taggelos/Software-Engineering\"> "+args[0].substring(args[0].lastIndexOf("\\")+1)+"</a> repository </div>");
            bw.write("<br><br><table class= \"table\">");
            
            MainReport obj = new MainReport();

            String command;
            String output;
            
            
            int committers_count=0;
            int commits=0;
            
            command = "cmd /C git ls-files | wc -l";
            
            output = obj.executeCommand(command, args[0]);
            
            System.out.println("Number of files is: \n" +  output);
            bw.write("<tr><th class=\"th\">Number of files</th><td class = \"td\">"+ output+ "</td></tr>");
            
            
            command = "cmd /C git ls-files | xargs wc -l | tail -1";
            output = obj.executeCommand(command, args[0]);
            System.out.println("Number of total lines is: \n" +  totalLines(output)+ "\n") ;
            bw.write("<tr><th class=\"th\">Number of total lines</th><td class = \"td\">" + totalLines(output)+ "</td></tr>");
            
            /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
            command = "cmd /C git branch | wc -l";
            output = obj.executeCommand(command, args[0]);
            int numBranches = Integer.parseInt(output.replace("\n", ""));
            System.out.println("Number of total branches is: \n" +  numBranches);
            bw.write("<tr><th class=\"th\">Number of total branches</th><td class = \"td\">" + numBranches+ "</td></tr>");
            
            /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
            command = "cmd /C git tag | wc -l";
            output = obj.executeCommand(command, args[0]);
            System.out.println("Number of total tags is: \n" +  output);
            bw.write("<tr><th class=\"th\">Number of total tags</th><td class = \"td\">" + output+ "</td></tr>");
            
            /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
            command = "cmd /C git shortlog -sn --all | wc -l";
            output = obj.executeCommand(command, args[0]);
            System.out.println("Number of total committers is: \n" +  output);
            bw.write("<tr><th class=\"th\">Number of total committers</th><td class = \"td\">" + output+ "</td></tr>");
            committers_count = Integer.valueOf(output.substring(0, output.length()-1));
          
            
            /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
            command = "cmd /C  git log | findstr Author: | sort | wc -l";
            output = obj.executeCommand(command, args[0]);
            System.out.println("Number of total commits is: \n" +  output);
            bw.write("<tr><th class=\"th\">Number of total commits</th><td class = \"td\">" + output+ "</td></tr>");
            commits = Integer.valueOf(output.substring(0, output.length()-1));
            
            /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
                        
            List<String> paths = new ArrayList<String>();
            paths.add(args[0]);
            paths.add(args[1]);
            
            bw.write("</table><br>");
            List<String> brnames = createBranchTable(numBranches,bw, paths,commits, obj);
            
            createcommittersTable(bw, paths,brnames, committers_count, commits, obj);
            
            System.out.println("\n----------- Process Ended Successfully -----------");
            
            bw.write("<div class = \"text\"> <p>&copy; 2017 <p> </div>");
            bw.write("</body>");
            bw.write("</html>");

            bw.close();
        } catch (IOException ignored) {

        }

    }

    
    
    
    private static void CreateCssFile(String path) throws IOException {
    	File f = new File(path + "/mystyle.css");
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write(".body {font-weight: 300;margin: 0;background-color: #fcf5ef;color: #fff;text-align: center;}");
        bw.write(".table {text-align: left;font-size: 20px;color: white;max-width: 340px;margin: 0 auto;top: 80px;border-radius: 5px;box-shadow: 0 5px 50px rgba(0,0,0,2);}");
        bw.write(".th{background-color: #555555;color: white;margin-top: 0;border-radius: 5px 5px 0 0;white-space: nowrap;}");
        bw.write(".title{font-family:courier;font-weight: bold;text-align: right;font-size: 28px;text-align: center;color: #555555;}");
        bw.write(".td{background-color: white;color: black;text-align: center;margin-top: 0;border-radius: 5px 5px 0 0;	white-space: nowrap;}");
        bw.write(".td:hover{background-color: yellow;margin-top: 0;}");
        bw.write(".a{font-style: italic;}");
        bw.write(".a:hover{background-color: orange;}");
        bw.write(".link:visited {text-decoration: none;}");
        bw.write(".link:link {text-decoration: none;}");
        bw.write(".link:hover {text-decoration: underline;}");
        bw.write(".link:active {text-decoration: underline;}");
        bw.write(".text{position: absolute; bottom: 0; right: 0; width: 100px; text-align:center;color: #555555 ; font-style: bold;}");
        bw.close();
	}


	public String executeCommand(String command, String path) {

        StringBuilder output = new StringBuilder();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command, null, new File(path));
            
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line+'\n');
                
            }
            p.waitFor();
            p.destroy();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }
    
    private static String totalLines(String out){
    	String[] parts = out.split("\n");
    	//remove the word total
    	out = parts[parts.length-1].replace("total" , ""); 
    	//remove whitespaces from beginning
    	parts = out.split("\\s+");   
		return parts[1];
    }
    
    private static List<String> createBranchTable(int brnum, BufferedWriter bw, List<String> paths ,int commits, MainReport obj) throws IOException{
	    bw.write("<br><br>");
	    bw.write("<div class = \"title\"> Branches </div>");
    	bw.write("<br><br><table class = \"table\"");
    	bw.write("<tr><th class=\"th\">Name</th><th class=\"th\">Date of Creation</th><th class=\"th\" >Last Date of Modification</th><th class=\"th\">Percentage of Commits</th></tr>");
    	
    	String command, output;
    	int brcommits;
    	
    	ArrayList<String> date = new ArrayList<>();
    	ArrayList<String> brnames = new ArrayList<>();
    	for (int i=0; i<brnum;i++){
    		command = "git for-each-ref --sort=-committerdate refs/heads/ --format=%(committerdate:short),%(refname:short)";
    		output = obj.executeCommand(command, paths.get(0));
    		output = output.split("\n")[i];
         	
         	date.add(output.split(",")[0]);
         	brnames.add(output.split(",")[1]);
    	}
         	
    	for (int i=0; i<brnum;i++){
         	command = "cmd /C  git log "+brnames.get(i)+" --oneline | wc -l";
            output = obj.executeCommand(command, paths.get(0));
            brcommits = Integer.valueOf(output.substring(0, output.length()-1));
            
         	BranchReport.create(paths, brnames.get(i), brcommits ,obj);
         	
    		bw.write("<tr>");
    		bw.write("<td class = \"a\"><a class = \"link\" href=\"branchReports/"+brnames.get(i)+".htm \">" + brnames.get(i)+ "</a></td>");
    	    	
    		String next="";
    		if(i+1 < brnum) next = brnames.get(i+1)+"...";
    		if(brnames.get(i).equals("master")) {
    			command = "cmd /C git log master --date=format:%Y-%m-%d | grep Date: | tail -1";
    		}
    		else {
    			command = "cmd /C git log "+next+brnames.get(i)+" --date=format:%Y-%m-%d | grep Date: | tail -1";
    		}
    		
    		output = obj.executeCommand(command, paths.get(0));
    		
    		output = output.replace("Date:", "");
    		output = output.replaceAll(" ", "");
    		
    		bw.write("<td class = \"td\">"+output+"</td>");  
    		
    		bw.write("<td class = \"td\">"+date.get(i)+"</td>");
    		
    		bw.write("<td class = \"td\">"+String.format("%.02f", Float.valueOf(brcommits)*100/commits)+"%</td>");

        	bw.write("</tr>");
    	}
    	bw.write("</table><br>");
    	return brnames;
    } 
    
    private static void createcommittersTable(BufferedWriter bw, List<String> paths,List<String> brnames, int committers_count, int commits, MainReport obj) throws IOException, ParseException {
    	String s,command,out;
       
        bw.write("<br><br>");
	    bw.write("<div class = \"title\"> Committers </div>");
    	bw.write("<br><br><table class = \"table\"");
    	bw.write("<tr><th class=\"th\">Name</th><th class=\"th\">Number of commits</th><th class=\"th\">Percentage of Commits</th></tr>");
    	
    	
        List<String> committers = new ArrayList<String>();
        for (int i = 0; i < committers_count; i++) {
         	command = "cmd /C git shortlog -sn --all";
         	out = obj.executeCommand(command, paths.get(0));
         	
            
         	s = out.split("\n")[i];
         	while(s.startsWith(" ")){
         		s=s.replaceFirst(" ", "");
         	}

         	committers.add(s.split("\t")[1]);
         	
         	String name =committers.get(i);
         	
         	CommitterReport.create(paths,brnames,name,obj );
         	
         	bw.write("<tr>");
         	
         	bw.write("<td class = \"a\"><a class = \"link\" href=\"userReports/"+name.replace(" ", "")+".htm\">" + name + "</a></td>");
         	bw.write("<td class = \"td\">"+ s.split("\t")[0] +"</td>");
         	bw.write("<td class = \"td\">" + String.format("%.02f", Float.valueOf(s.split("\t")[0])*100/commits) + "%</td>");
         	bw.write("<tr>");	
        }
        bw.write("</table>");
    }
    
    
}
