package com.service;

import com.forms.PtReportForm;
import com.util.HcmsConnectionFactory;
import com.util.HcmsException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.opensymphony.xwork2.ActionSupport;

import org.apache.struts2.interceptor.SessionAware;

import java.io.BufferedReader;            
import java.io.DataInputStream;           
import java.io.FileInputStream;           
import java.io.FileNotFoundException;     
import java.io.IOException;               
import java.io.InputStreamReader;                                           
import java.sql.Connection;                                                 
import java.sql.PreparedStatement;                                          
import java.sql.ResultSet;                                                  
import java.sql.SQLException;                                               
import java.sql.Statement;                                                  
import java.sql.Timestamp;                                                  
import java.text.ParseException;                                            
import java.text.SimpleDateFormat;                                          
import java.util.ArrayList;                                                 
import java.util.Calendar;                                                  
import java.util.Date;                                                      
import java.util.HashMap;                                                   
import java.util.Iterator;                                                  
import java.util.Map;                                                       
import java.util.MissingResourceException;

public class PtReportService extends ActionSupport implements SessionAware {

	public String getAppointmentDate() {
		return AppointmentDate;
	}
	public void setAppointmentDate(String appointmentDate) {
		AppointmentDate = appointmentDate;
	}
	private String AppointmentDate;
	private String PatientSSN;
	
	  public String getPatientSSN() {
		return PatientSSN;
	}
	public void setPatientSSN(String patientSSN) {
		PatientSSN = patientSSN;
	}
	ArrayList<PtReportForm> list=new ArrayList<PtReportForm>();  
	   
	   public ArrayList<PtReportForm> getList() {  
	       return list;  
	   }  
	   public void setList(ArrayList<PtReportForm> list) {  
	       this.list = list;  
	   }  
	

	   public String execute() 
	   {
	    boolean validation = false;
	    int err=0;
	    String dobFormatted;
	    
	    if ( this.AppointmentDate == null) {
	 	   setAppointmentDate("");
	    }
	    if ( this.PatientSSN == null) {
	 	   setPatientSSN("");
	    }
	    if ( this.AppointmentDate == null || this.AppointmentDate.replaceAll(" ", "").equalsIgnoreCase("")
	    		|| this.AppointmentDate.replaceAll(",","").replaceAll(" ", "").length() == 0) {
	 	   setAppointmentDate("");
	    	dobFormatted = "";
	    } else {
	    	dobFormatted = this.AppointmentDate.replaceAll(",","").replaceAll(" ", "").substring(0,10);
	    }
	    
	    
	    System.out.println(" IN PtReportService Before JDBC connection 111");

	     Connection connection = null;
	    PreparedStatement statement = null;
	    ResultSet resultSet = null;
	     
	    System.out.println(" PtReportService connection obtained" + AppointmentDate+ "-" + PatientSSN + "-");
	     try
	    {
	 	   if (this.AppointmentDate.equalsIgnoreCase("") || this.AppointmentDate == null
	 			  || this.AppointmentDate.replaceAll(",","").replaceAll(" ", "").length() == 0 ) {
	  		  System.out.println("Appt Date is blank  ");
	  		  addActionError("Appt Date is blank");
	  		  err =1;
	   	  }
	  	  if (this.PatientSSN.equalsIgnoreCase("") || this.PatientSSN == null
	  			|| this.PatientSSN.replaceAll(",","").replaceAll(" ", "").length() == 0) {
	  		  System.out.println("Patient SSN is blank  ");
	  		  addActionError("Patient SSN is blank");
	  		  err=1;
	  	  }
	        if (err > 0 ) {
	      	  return "error";
	        }
	   	 System.out.println(" IN PtReportService Before JDBC connection");
	      connection = HcmsConnectionFactory.getConnection();
	      if (connection == null) {
	     	 addActionError("Error in getting DB Connection PtReportService");
	   	      System.out.println(" Error in getting DB Connection PtReportService");
	   	    return "error";
	        //throw new HcmsException("Error in getting DB connection .");
	  
	      }
	      System.out.println(" PtReportService connection obtained 222" + AppointmentDate + AppointmentDate.replaceAll(",","").replaceAll(" ", "")
	     		 + " date " + dobFormatted);
	      
	      
	      statement = connection.prepareStatement("  select pn.name, r.report_content, r.date, r.bloodpressure, r.heightincm, r.weightinlb, r.comments"
	      		+ "                                   from report r , appointment a , patient p , person pn "
	      		+ "                                  where r.appointment = a.appointment_id "
	      		+ "                                    and a.patient = p.patient_id and p.ssn = pn.ssn "
	      		+ "                                    and  r.date =  ?  and p.ssn =  ?   ");
//	      System.out.println( " SELECT  usertype from HCMS_USERS WHERE USR_ID = ? AND USR_PWD = ?" + loginId + " " + password);
	       statement.setString(1, dobFormatted);
	        statement.setInt(2, Integer.parseInt(PatientSSN.replaceAll(",","").replaceAll(" ", "")));
	    
	      System.out.println(" PtReportService connection obtained "  );
	      resultSet = statement.executeQuery();
	      while(resultSet.next()){  
	     	
	    	  PtReportForm a = new PtReportForm();  
	     	    a.setName(resultSet.getString(1));
	     	    a.setReport(resultSet.getString(2));
	     	    a.setDate(resultSet.getString(3));
	     	    a.setBloodpressure(resultSet.getString(4));
	     	    a.setHeightincm(resultSet.getString(5));
	     	    a.setWeightinlb(resultSet.getString(6));
	     	    a.setComments(resultSet.getString(7));
	     	  
	                 list.add(a); 
	        }
	      statement.close();
	      resultSet.close();
	   
	         
	     }
	    catch (SQLException sqe)
	    {
	        System.out.println( " Error in Data Base Connection line PtReportService" + sqe);
	        validation = false;
	     // throw new HcmsException("Error in Data Base Connection DrApptDetails.", sqe);
	    }
	    catch (Exception exception)
	    {
	 	   System.out.println( " Error in Data Base Connection Exception PtReportService" );
	 	   validation = false;
	     // throw exception;
	    }
	 //   finally
	 //   {
//	 	   System.out.println( " Data Base Connection FINALLY DrApptDetails" );
//	      if (resultSet != null) {
//	        resultSet.close();
//	      }
//	      if (statement != null) {
//	        statement.close();
//	      }
	      validation = true;
	 //   }
	  //  return validation;
	    return "success";
	  }	
	   
	@Override
	public void setSession(Map<String, Object> arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
