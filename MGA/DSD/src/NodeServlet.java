import java.io.*;
import java.util.*;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.*;

/**
 * Servlet implementation class NodeServlet
 */
@WebServlet("/NodeServlet")
public class NodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NodeServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String action 	 = "";
    	String sensorid  = "";
    	String start 	 = "";
    	String end 		 = "";
    	String time		 = "";
    	String decision	 = "";
    	String param 	 = "";
    	String urlString = "";
    	String lat1 	 = "";
    	String lat2 	 = "";
    	String lon1 	 = "";
    	String lon2 	 = "";
    	
    	// Take the parameters out of the request
    	java.util.Enumeration paramNames = request.getParameterNames();
    	while ( paramNames.hasMoreElements() ) {
    		param = (String) paramNames.nextElement();
    		String paramValue = request.getParameter(param);
//    		String[] paramValues = request.getParameterValues( param );  // Alternative if a param has many values
    		
    		if (param.equals("action")) {
    			action = paramValue;
    		} else if (param.equals("sensorid")) {
    			sensorid = paramValue;
    		} else if (param.equals("start")) {
    			start = paramValue;
    		} else if (param.equals("end")) {
    			end = paramValue;
    		} else if (param.equals("time")) {
    			time = paramValue;
    		} else if (param.equals("decision")) {
    			decision = paramValue;
    		} else if (param.equals("lat1")) {
    			lat1 = paramValue;
    		} else if (param.equals("lat2")) {
    			lat2 = paramValue;
    		} else if (param.equals("lon1")) {
    			lon1 = paramValue;
    		} else if (param.equals("lon2")) {
    			lon2 = paramValue;
    		}
    	}
    	    	
    	// Set the URL for any action
    	if (action.equals("solarenergy")) {
//    		System.out.println("solarenergy via servlet. start "+start+" . end "+end);
    		urlString = "http://greencom.fit.fraunhofer.de:3000/solarenergy/"+start+"/"+end;
    	} else if (action.equals("recentweather")) {
//    		System.out.println("recentweather via servlet");
    		urlString = "http://greencom.fit.fraunhofer.de:3000/recentweather";
    	} else if (action.equals("historicalweather")) {
//    		System.out.println("historicalweather via servlet. start "+start+" . end "+end);
    		urlString = "http://greencom.fit.fraunhofer.de:3000/historicalweather/"+start+"/"+end;
    	} else if (action.equals("energyprices")) {
//    		System.out.println("energyprices via servlet. start "+start+" . end "+end);
    		urlString = "http://greencom.fit.fraunhofer.de:3000/energyprices/"+start+"/"+end;
    	} else if (action.equals("recentload")) {			
//    		System.out.println("recentload via servlet");
    		urlString = "http://greencom.fit.fraunhofer.de:3000/recentload";
    	} else if (action.equals("recentloadactua")) {			
//    		System.out.println("recentload via servlet");
    		urlString = "http://greencom.fit.fraunhofer.de:3000/recentloadactua";
    	} else if (action.equals("sensorvalues")) {
//    		System.out.println("sensorvalues via servlet. sensorid "+sensorid+" . start "+start+" . end "+end);
    		urlString = "http://greencom.fit.fraunhofer.de:3000/sensorvalues/"+sensorid+"/"+start+"/"+end;
//    		urlString = "http://localhost:3000/sensorvalues/"+sensorid+"/"+start+"/"+end;
    	} else if (action.equals("sensorvaluesactua")) {
//    		System.out.println("sensorvalues via servlet. sensorid "+sensorid+" . start "+start+" . end "+end);
    		urlString = "http://greencom.fit.fraunhofer.de:3000/sensorvaluesactua/"+sensorid+"/"+start+"/"+end;
    	} else if (action.equals("instantvalues")) {
//    		System.out.println("sensorvalues via servlet. sensorid "+sensorid+" . start "+start+" . end "+end);
    		urlString = "http://greencom.fit.fraunhofer.de:3000/instantvalues";
    	} else if (action.equals("cumulativevalues")) {
//    		System.out.println("sensorvalues via servlet. sensorid "+sensorid+" . start "+start+" . end "+end);
    		urlString = "http://greencom.fit.fraunhofer.de:3000/cumulativevalues";
    	} else if (action.equals("getopenaggrequests")) {
//    		System.out.println("sensorvalues via servlet. sensorid "+sensorid+" . start "+start+" . end "+end);
    		urlString = "http://greencom.fit.fraunhofer.de:3000/getopenaggrequests";
//    		urlString = "http://localhost:3000/getopenaggrequests";
    	} else if (action.equals("getoldaggrequests")) {
//    		System.out.println("sensorvalues via servlet. sensorid "+sensorid+" . start "+start+" . end "+end);
    		urlString = "http://greencom.fit.fraunhofer.de:3000/getoldaggrequests";
//    		urlString = "http://localhost:3000/getoldaggrequests";
    	} else if (action.equals("confirmdsorequest")) {
//    		System.out.println("confirmdsorequest");
    		urlString = "http://greencom.fit.fraunhofer.de:3000/confirmdsorequest/"+time+"/"+decision;
//    		urlString = "http://localhost:3000/confirmdsorequest/"+time+"/"+decision;
    	} else if (action.equals("bbox")) {
//    		System.out.println("confirmdsorequest");
    		urlString = "http://greencom.fit.fraunhofer.de:3000/bbox/"+lon1+"/"+lat1+"/"+lon2+"/"+lat2;
//    		urlString = "http://localhost:3000/bbox/"+time+"/"+decision;
    	} else if (action.equals("getloadpredictionactua")) {
    		urlString = "http://greencom.fit.fraunhofer.de:3000/getloadpredictionactua/"+sensorid+"/"+time;
//    		urlString = "http://localhost:3000/getloadpredictionactua/"+sensorid+"/"+time;
    	} else if (action.equals("mgavaluesdwh")) {
//    		System.out.println("mga sensor values via servlet. sensorid "+sensorid+" . start "+start+" . end "+end);
    		urlString = "http://greencom.fit.fraunhofer.de:3000/mgavaluesdwh/"+sensorid+"/"+start+"/"+end;
//    		urlString = "http://localhost:3000/mgavaluesdwh/"+sensorid+"/"+start+"/"+end;
    	}
    	
//    	System.out.println("url "+urlString);
    
    	// Get the data and send back the result
       	URL urlObject = new URL(urlString);
		URLConnection urlConnection = urlObject.openConnection();
		BufferedReader buffReader = new BufferedReader( new InputStreamReader( urlConnection.getInputStream() ) );

		String inputLine;
		String inputCollection = "";

		while ((inputLine = buffReader.readLine()) != null) {
			inputCollection += inputLine;
		}
		buffReader.close();

		// Prepare the response and send it
		// response.setContentType("application/json");
		PrintWriter out = response.getWriter(); 
		out.print(inputCollection);
		out.close();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// This method gets json data via POST, stringifies it and sends it to the node-server via POST. 
		
		String action = request.getParameter("action");
		
		String timestamp ="";
		String stringifiedJson = "";
		String query = "";
		String urlString = "";
		
		if (action.equals("addaggrequest")) {
			timestamp 		= request.getParameter("time");
			stringifiedJson = request.getParameter("reductionData");
			query 			= "action=addaggrequest&time="+timestamp+"&reductionData="+stringifiedJson;
			urlString 		= "http://greencom.fit.fraunhofer.de:3000/addaggrequest";
		}
//		System.out.println(urlString);
		
		// Do POST request --- START
		// Send out the POST request to the node script
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true); // Triggers POST.
		connection.setRequestProperty("Accept-Charset", "UTF-8");
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

		try (OutputStream output = connection.getOutputStream()) {
		    output.write(query.getBytes("UTF-8"));
		}
		
		// Get the response from the server and send it back
		BufferedReader buffReader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );

		String inputLine;
		String inputCollection = "";

		while ((inputLine = buffReader.readLine()) != null) {
			inputCollection += inputLine;
		}
		buffReader.close();

		// Prepare the response and send it
		PrintWriter out = response.getWriter(); 
		out.print(inputCollection);
		out.close();
		// Do POST request --- END
	}
}