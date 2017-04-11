/*
 * Copyright (C) 2017 methu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

/**
 * java implementation of SMSGateway platform client
 * all methods are the same as in PHP implementation 
 * just that java is strict in data type and instead 
 * of associative arrays, Map is used since java does 
 * not support String indexes
 * 
 * @author methu
 */
public class SmsGateway {
    
    private final int GET= 1;
    private final int POST= 2;
    private final String email;
    private final String password;
    private final String baseUrl = "https://smsgateway.me/api/v3";
    
    /**
     * @param email String your account email
     * @param password String your account password
     */
    public SmsGateway(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    /**
     * Add contact to your account
     * @param name String contact name
     * @param number String phone number
     * @return String JSON create status
     */
    public String createContact (String name, String number) {
        HashMap<String, String> fields= new HashMap();
        fields.put("name", name);
        fields.put("number", number);
        return this.makeRequest("/contacts/create", this.POST, fields);
    }
    
    /**
     * get list of contacts 500 items per page
     * @return String JSON array 
     */
    public String getContacts(){
        return this.getContacts(1);
    }
    
    /**
     * get list of contacts 500 items per page
     * @param page Integer page number 
     * @return String JSON array 
     */
    public String getContacts (int page) {
        HashMap<String, String> fields= new HashMap();
        fields.put("page", Integer.toString(page));
        return this.makeRequest("/contacts", this.GET, fields);
    }
    
    /**
     * get single contact
     * @param id long contact id
     * @return String JSON response
     */
    public String getContact (long id) {
        return this.makeRequest("/contacts/view/"+id,this.GET);
    }
    
    /**
     * get list of devices 500 items per page
     * @return String JSON array 
     */
    public String getDevices(){
        return this.getDevices(1);
    }
    
    /**
     * get list of devices 500 items per page
     * @param page Integer page number 
     * @return String JSON array 
     */
    public String getDevices(int page){
        HashMap<String, String> fields= new HashMap();
        fields.put("page", Integer.toString(page));
        return this.makeRequest("/devices", this.GET, fields);
    }
    
    /**
     * get single device
     * @param id long device id
     * @return String JSON response
     */
    public String getDevice(long id){
        return this.makeRequest("/devices/view/"+id,this.GET);
    }
    
    /**
     * get list of messages 500 items per page
     * @return String JSON array 
     */
    public String getMessages(){
        return this.getMessages(1);
    }
    
    /**
     * get list of messages 500 items per page
     * @param page Integer page number 
     * @return String JSON array 
     */
    public String getMessages(int page){
        HashMap<String, String> fields= new HashMap();
        fields.put("page", Integer.toString(page));
        return this.makeRequest("/messages", this.GET, fields);
    }
    
    /**
     * get single message
     * @param id long message id
     * @return String JSON response
     */
    public String getMessage(long id){
        return this.makeRequest("/messages/view/"+id,this.GET);
    }
    
    /**
     * send message to number
     * @param to String number to send to
     * @param message String message content
     * @param device long device to use id
     * @return String JSON response
     */
    public String sendMessageToNumber(String to, String message, long device){
        HashMap<String, String> fields= new HashMap();
        return this.sendMessageToNumber(to, message, device, fields);
    }
    
    /**
     * send message to number
     * @param to String number to send to
     * @param message String message content
     * @param device long device to use id
     * @param fields HashMap options list
     * @return String JSON response
     */
    public String sendMessageToNumber(String to, String message, long device, Map fields){
        fields.put("number", to);
        fields.put("message", message);
        fields.put("device", Long.toString(device));
        return this.makeRequest("/messages/send", this.POST, fields);
    }
    
    /**
     * send single message to many numbers
     * @param to String List of numbers to send to
     * @param message String message content
     * @param device long device to use id
     * @return String JSON response
     */
    public String sendMessageToManyNumbers(String[] to, String message, long device){
        HashMap<String, String> fields= new HashMap();
        return this.sendMessageToManyNumbers(to, message, device, fields);
    }
    
    /**
     * send single message to many numbers
     * @param to String List of numbers to send to
     * @param message String message content
     * @param device long device to use id
     * @param fields HashMap options list
     * @return String JSON response
     */
    public String sendMessageToManyNumbers(String[] to, String message, long device, Map fields){
        fields.put("number", to);
        fields.put("message", message);
        fields.put("device", Long.toString(device));
        return this.makeRequest("/messages/send",this.POST, fields);
    }
    
    /**
     * send message to saved contact
     * @param to long contact id to send to
     * @param message String message content
     * @param device long device to use id
     * @return String JSON response
     */
    public String sendMessageToContact(long to, String message, long device){
        HashMap<String, String> fields= new HashMap();
        return this.sendMessageToContact(to, message, device, fields);
    }
    
    /**
     * send message to saved contact
     * @param to long contact id to send to
     * @param message String message content
     * @param device long device to use id
     * @param fields HashMap options list
     * @return String JSON response
     */
    public String sendMessageToContact(long to, String message, long device, Map fields){
        fields.put("contact", to);
        fields.put("message", message);
        fields.put("device", Long.toString(device));
        return this.makeRequest("/messages/send",this.POST, fields);
    }
    
    /**
     * send single message to many saved contacts
     * @param to String List of contact id
     * @param message String message content
     * @param device long device to use id
     * @return String JSON response
     */
    public String sendMessageToManyContacts (String[] to, String message, long device){
        HashMap<String, String> fields= new HashMap();
        return this.sendMessageToManyContacts(to, message, device, fields);
    }
    
    /**
     * send single message to many saved contacts
     * @param to String List of contact id
     * @param message String message content
     * @param device long device to use id
     * @param fields HashMap options list
     * @return String JSON response
     */
    public String sendMessageToManyContacts(String[] to, String message, long device, Map fields){
        fields.put("contact", to);
        fields.put("message", message);
        fields.put("device", Long.toString(device));
        return this.makeRequest("/messages/send",this.POST, fields);
    }
    
    /**
     * send many messages to many numbers
     * @param data List of Map of each message data
     * @return String JSON response
     */
    public String sendManyMessages(List<Map> data){
        HashMap<String, Object> fields= new HashMap();
        fields.put("data", data);
        return this.makeRequest("/messages/send",this.POST, fields);
    }
    
    /**
     * send request to server
     * @param url String request parameters
     * @param method Integer request method
     * @return String JSON response
     */
    private String makeRequest(String url, int method){
        return this.makeRequest(url, method, new HashMap());
    }
    
    /**
     * send request to server
     * @param url String request parameters
     * @param method Integer request method
     * @param fields HashMap options list
     * @return String JSON response
     */
    private String makeRequest(String url, int method, Map fields) {
        CloseableHttpClient client= HttpClients.createDefault();
        fields.put("email", this.email);
        fields.put("password", this.password);
        url= this.baseUrl+url;
        CloseableHttpResponse response= null;
        try {
            switch(method){
                case GET:
                    response= this.get(client, fields, url);
                    break;
                case POST:
                    response= this.post(client, fields, url);
                    break;
                default:
                    response= null;
                    break;
            }
        } catch (IOException ex) { 
            Logger.getLogger(SmsGateway.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.readResponse(response);
    }
    
    /**
     * extract JSON response from response
     * @param response CloseableHttpResponse response
     * @return String JSON result NULL otherwise
     */
    private String readResponse(CloseableHttpResponse response){
        StringBuilder builder= new StringBuilder();
        if (response!=null && response.getStatusLine().getStatusCode()==200){
            try {
                BufferedReader reader= new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                String line;
                while((line= reader.readLine())!= null){
                    builder.append(line);
                }
                response.close();
            } catch (IOException | UnsupportedOperationException ex) {
                Logger.getLogger(SmsGateway.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return builder.toString();
    }
    
    /**
     * execute a get request
     * @param client CloseableHttpClient client to handle execution
     * @param fields Map request data
     * @param url String address
     * @return CloseableHttpResponse response result
     * @throws IOException exception encountered in response
     */
    private CloseableHttpResponse get(CloseableHttpClient client, Map fields, String url) throws IOException{
        Scanner reader= new Scanner(this.prepareFormValues(fields).getContent());
        HttpGet get= new HttpGet(url+"?"+reader.nextLine());
        return client.execute(get);
    }
    
    /**
     * execute a post request
     * @param client CloseableHttpClient client to handle execution
     * @param fields Map request data
     * @param url String address
     * @return CloseableHttpResponse response result
     * @throws IOException exception encountered in response
     */
    private CloseableHttpResponse post(CloseableHttpClient client, Map fields, String url) throws IOException{
        HttpPost post= new HttpPost(url);
        post.setEntity(this.prepareFormValues(fields));
        return client.execute(post);
    }
    
    /**
     * convert Map to HTTP form values
     * @param map Map containing form indexes and values
     * @return UrlEncodedFormEntity HTTP form values
     */
    private UrlEncodedFormEntity prepareFormValues(Map<String, Object> map){
        List<NameValuePair> formparams = this.mapToStringQuery(map, new ArrayList(), null);
        return new UrlEncodedFormEntity(formparams, Consts.UTF_8);
    }
    
    /**
     * add Map values to List of NamePairs to allow form encoding
     * @param map Map containing form indexes and values
     * @param formparams List of NameValuePair to add values to
     * @param name String field name prefix, null if none
     * @return List of NameValuePair with Map values added
     */
    private List<NameValuePair> mapToStringQuery(Map<String, Object> map, List<NameValuePair> formparams, String prefix){
        for (Map.Entry<String, Object> entry: map.entrySet()){
            String name;
            if (prefix!=null && prefix.length()>0){
                name= prefix+"["+entry.getKey()+"]";
            } else {
                name= entry.getKey();
            }
            if (entry.getValue() instanceof String){
                formparams.add(new BasicNameValuePair(name, (String)entry.getValue()));
            } else if (entry.getValue() instanceof String[]){
                int index= 0;
                for (String value: (String[])entry.getValue()){
                    formparams.add(new BasicNameValuePair(name+"["+index+"]", value));
                    index++;
                }
            } else if (entry.getValue() instanceof List){
                int index= 0;
                for (Map value: (List<Map>)entry.getValue()){
                    this.mapToStringQuery(value, formparams, name+"["+index+"]");
                    index++;
                }
            }
        }
        return formparams;
    }
}
