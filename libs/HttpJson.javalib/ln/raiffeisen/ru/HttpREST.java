package ln.raiffeisen.ru;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lotus.domino.AgentBase;
import lotus.domino.AgentContext;
import lotus.domino.Session;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class HttpREST extends AgentBase {
	// �������� ������ 
	public String URL = "https://httpbin.org/post"	;					//URL �������
	public String Query; 				//������ Query, ���� ������� POST 
	public String POST_Result;			//��������� (�����) � ���� JSON
	public String Token_Result;			
	public String Teams_Name = "";
	public String Teams_ID = "";

	//------------��������� HTTP------------
	public List<String> HeaderFieldName;
	public List<String> HeaderFieldValue;
	//--------------------------------------


	public void setHeadersFieldName(String[] inHeadersFN) {
    	//����� ��������� ��������� (Header). ��� ���������
    	System.out.println("setHeadersFieldName");
    	HeaderFieldName = new ArrayList<String>();
  		for (int i = 0 ; i < inHeadersFN.length ; i++) {
		this.HeaderFieldName.add(inHeadersFN[i]);
		}
    }
    public void setHeadersFieldValue(String[] inHeadersFV) {
    	System.out.println("setHeadersFieldValue");
    	//����� ��������� ��������� (Header). �������� ���������
    	HeaderFieldValue = new ArrayList<String>();
  		for (int i = 0 ; i < inHeadersFV.length ; i++) {
		this.HeaderFieldValue.add(inHeadersFV[i]);
		}
    }
    public String justReturn(){
    	return "return!";
    }  
    //-----------------------------------------------------------------------------------
	public String InvokeHTTP_POST() throws Exception {
		/*******************************************************
		 * ��������� ������ � ��������� �������� � POST_Result *
		 * *****************************************************
		 */
		System.out.println("Invoke!");
	    POST_Result = "";
		URL url = new URL(URL);
		try {
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			
			con.setRequestMethod("POST");
			for (int i = 0 ; i < HeaderFieldName.size() ; i++) {
				con.setRequestProperty (HeaderFieldName.get(i), HeaderFieldValue.get(i));
			}
			con.setDoOutput(true);
		    OutputStream os = con.getOutputStream();
		    Query = "accept: application/json";
		    os.write(Query.getBytes());
		    os.flush();
		    os.close();
			
			if ( con.getResponseCode() == 200 )
			{
			    BufferedReader rd; // ������ ������
			    String line;
		        rd = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		        while ((line = rd.readLine()) != null) {
		        	POST_Result += line;
		        }
		         rd.close();
			     return POST_Result + "@#$";
			}
	         	return con.getResponseCode() + "----" + con.getResponseMessage() + " ~~ " + Query.toString();
			
		} catch (Exception e) {
			POST_Result = e.getMessage();
			return POST_Result;
		}
	}
	//------------------------------------------------------------------------------------
	
	public String GetToken(String resultQuery){
        /** �� ����������� ������, � ���� JSON ���������, �������� �������� �� ����� ���� (�����)
        resultQuery - JSON ���������
        */
		System.out.println("GetToken");
       JsonParser jsonpars = new JsonParser();
       JsonObject tokenObject = jsonpars.parse(resultQuery).getAsJsonObject();
       JsonObject getResultObject = tokenObject.get("result").getAsJsonObject();	//�������� ������ result
       Token_Result = getResultObject.get("token").getAsString();					//�������� ������ Array ��� ���� Teams
       return Token_Result;
   }
	
	public void GetTeamsNameAndID(String resultQuery) {
        /** �� ����������� ������, � ���� JSON ���������, �������� �������� �� ����� ���� (�����)
        resultQuery - JSON ���������
        */
       JsonParser JSparser = new JsonParser();
       JsonObject mainObject = JSparser.parse(resultQuery).getAsJsonObject();
       JsonObject getTeamsObject = mainObject.get("data").getAsJsonObject();		//�������� ������ data
       JsonArray TeamsArray = getTeamsObject.getAsJsonArray("getTeams");			//�������� ������ Array ��� Teams
       for (JsonElement team : TeamsArray) {
           JsonObject teamObject = team.getAsJsonObject();
           if (Teams_Name == "") {
               Teams_Name = teamObject.get("name").getAsString();
               Teams_ID = teamObject.get("id").getAsString();
           } else {
               //��������� � ���������� "Teams_Name", "Teams_ID" ����� ����������� "����� ������"
               Teams_Name = Teams_Name + "\n" + teamObject.get("name").getAsString();
               Teams_ID = Teams_ID + "\n" + teamObject.get("id").getAsString();
           }
       }
   }
}