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
	// Свойства класса 
	public String URL = "https://httpbin.org/post"	;					//URL Запроса
	public String Query; 				//Запрос Query, тело запроса POST 
	public String POST_Result;			//Результат (ответ) в виде JSON
	public String Token_Result;			
	public String Teams_Name = "";
	public String Teams_ID = "";

	//------------Заголовок HTTP------------
	public List<String> HeaderFieldName;
	public List<String> HeaderFieldValue;
	//--------------------------------------


	public void setHeadersFieldName(String[] inHeadersFN) {
    	//Задаём параметры заголовка (Header). Имя заголовка
    	System.out.println("setHeadersFieldName");
    	HeaderFieldName = new ArrayList<String>();
  		for (int i = 0 ; i < inHeadersFN.length ; i++) {
		this.HeaderFieldName.add(inHeadersFN[i]);
		}
    }
    public void setHeadersFieldValue(String[] inHeadersFV) {
    	System.out.println("setHeadersFieldValue");
    	//Задаём параметры заголовка (Header). Значение заголовка
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
		 * Выполняем запрос и результат помещаем в POST_Result *
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
			    BufferedReader rd; // Читаем ответа
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
        /** Из полученного ответа, в виде JSON структуры, получаем значение по имени поля (ключа)
        resultQuery - JSON структура
        */
		System.out.println("GetToken");
       JsonParser jsonpars = new JsonParser();
       JsonObject tokenObject = jsonpars.parse(resultQuery).getAsJsonObject();
       JsonObject getResultObject = tokenObject.get("result").getAsJsonObject();	//Получили объект result
       Token_Result = getResultObject.get("token").getAsString();					//Получили объект Array для всех Teams
       return Token_Result;
   }
	
	public void GetTeamsNameAndID(String resultQuery) {
        /** Из полученного ответа, в виде JSON структуры, получаем значение по имени поля (ключа)
        resultQuery - JSON структура
        */
       JsonParser JSparser = new JsonParser();
       JsonObject mainObject = JSparser.parse(resultQuery).getAsJsonObject();
       JsonObject getTeamsObject = mainObject.get("data").getAsJsonObject();		//Получили объект data
       JsonArray TeamsArray = getTeamsObject.getAsJsonArray("getTeams");			//Получили объект Array для Teams
       for (JsonElement team : TeamsArray) {
           JsonObject teamObject = team.getAsJsonObject();
           if (Teams_Name == "") {
               Teams_Name = teamObject.get("name").getAsString();
               Teams_ID = teamObject.get("id").getAsString();
           } else {
               //добавляем в переменные "Teams_Name", "Teams_ID" через разделитель "новая строка"
               Teams_Name = Teams_Name + "\n" + teamObject.get("name").getAsString();
               Teams_ID = Teams_ID + "\n" + teamObject.get("id").getAsString();
           }
       }
   }
}