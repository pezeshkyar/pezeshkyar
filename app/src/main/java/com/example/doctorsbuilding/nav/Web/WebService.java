package com.example.doctorsbuilding.nav.Web;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.doctorsbuilding.nav.BuildConfig;
import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.Expert;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.MessageInfo;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.PatientFile;
import com.example.doctorsbuilding.nav.PatientInfo;
import com.example.doctorsbuilding.nav.PaymentInfo;
import com.example.doctorsbuilding.nav.PhotoDesc;
import com.example.doctorsbuilding.nav.Question.Question;
import com.example.doctorsbuilding.nav.Question.Reply;
import com.example.doctorsbuilding.nav.R;
import com.example.doctorsbuilding.nav.Reservation;
import com.example.doctorsbuilding.nav.ReservationByUser;
import com.example.doctorsbuilding.nav.Rturn;
import com.example.doctorsbuilding.nav.StringArraySerializer;
import com.example.doctorsbuilding.nav.SubExpert;
import com.example.doctorsbuilding.nav.Task;
import com.example.doctorsbuilding.nav.TaskGroup;
import com.example.doctorsbuilding.nav.Turn;
import com.example.doctorsbuilding.nav.User.City;
import com.example.doctorsbuilding.nav.User.State;
import com.example.doctorsbuilding.nav.User.User;
import com.example.doctorsbuilding.nav.UserType;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.support.Message;
import com.example.doctorsbuilding.nav.support.Subject;
import com.example.doctorsbuilding.nav.support.Ticket;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class WebService {
    //Namespace of the Webservice - can be found in WSDL
    private static String NAMESPACE = Util.getStringWS(R.string.ws_package);
    //Webservice URL - WSDL File location
    private static String URL = Util.getStringWS(R.string.ws_url);
    //    private static String URL = "http://185.129.168.135:8080/pezeshkyarServerAllInOne/services/Webservices?wsdl";
    //SOAP Action URI again Namespace + Web method name
    private static String SOAP_ACTION = Util.getStringWS(R.string.ws_package);

    private static final String connectMessage = "برقراری ارتباط با سرور امکان پذیر نیست !";
    private static final String nothingFromServer = "هیچ جوابی از سرور دریافت نشده است !";
    private static final String otherMessage = "خطایی در ارتباط با سرور رخ داده است !";
    private static final String isOnlineMessage = "دسترسی به اینترنت امکان پذیر نمی باشد، لطفا تنظیمات اینترنت خود را چک نمایید .";


    public static ArrayList<State> invokeGetProvinceNameWS() throws PException {

        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }

        String webMethName = Util.getStringWS(R.string.ws_getProvince);
        ArrayList<State> result = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            androidHttpTransport.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            ArrayList<State> states = new ArrayList<State>();
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) response.getProperty(i);
                State state = new State();
                state.SetStateID(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                state.SetStateName(obj.getProperty(Util.getStringWS(R.string.ws_name)).toString());
                states.add(state);
            }
            result = states;
        } catch (ConnectException e) {
            throw new PException(connectMessage);
        } catch (Exception e) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static ArrayList<City> invokeGetCityNameWS(int stateID) throws PException {

        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }

        String webMethName = Util.getStringWS(R.string.ws_getCityOfProvince);
        ArrayList<City> result = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        PropertyInfo property = new PropertyInfo();
        property.setName(Util.getStringWS(R.string.ws_provinceID));
        property.setValue(stateID);
        property.setType(Integer.class);
        request.addProperty(property);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            ArrayList<City> cities = new ArrayList<City>();
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) response.getProperty(i);
                City city = new City();
                city.SetCityID(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                city.SetStateID(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_provinceId)).toString()));
                city.SetCityName(obj.getProperty(Util.getStringWS(R.string.ws_name)).toString());
                cities.add(city);
            }
            result = cities;
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }


    public static String invokeRegisterWS(User user) throws PException {

        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }

        String webMethName = Util.getStringWS(R.string.ws_register);
        String result = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_name), user.getFirstName());
        request.addProperty(Util.getStringWS(R.string.ws_lastname), user.getLastName());
        request.addProperty(Util.getStringWS(R.string.ws_mobileno), user.getPhone());
        request.addProperty(Util.getStringWS(R.string.ws_username), user.getUserName());
        request.addProperty(Util.getStringWS(R.string.ws_password), user.getPassword());
        request.addProperty(Util.getStringWS(R.string.ws_cityid), user.getCityID());
        byte[] picbytes = getBytes(user.getImgProfile());
        request.addProperty(Util.getStringWS(R.string.ws_pic), Base64.encodeToString(picbytes, Base64.DEFAULT));
        request.addProperty(Util.getStringWS(R.string.ws_email), user.getEmail());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        new MarshalBase64().register(envelope);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = response.toString();
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static String invokeUpdateUserWS(String username, String password, User user) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_updateUserInfo3);
        String result = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_name), user.getFirstName());
        request.addProperty(Util.getStringWS(R.string.ws_lastname), user.getLastName());
        request.addProperty(Util.getStringWS(R.string.ws_mobileno), user.getPhone());
        request.addProperty(Util.getStringWS(R.string.ws_cityid), user.getCityID());
        request.addProperty(Util.getStringWS(R.string.ws_newPassword), user.getPassword());
        request.addProperty(Util.getStringWS(R.string.ws_email), user.getEmail());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        new MarshalBase64().register(envelope);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = response.toString();
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();


    }

//    public static int invokeLoginWS(int officeId, User user) throws PException {
//
//        if (!G.isOnline()) {
//            throw new PException(isOnlineMessage);
//        }
//
//        String webMethName = "login2";
//        int result = -1;
//        SoapObject request = new SoapObject(NAMESPACE, webMethName);
//
//        request.addProperty("username", user.getUserName());
//        request.addProperty("password", user.getPassword());
//        request.addProperty("officeId", officeId);
//
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.setOutputSoapObject(request);
//        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
//        try {
//            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
//            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
//            result = Integer.parseInt(response.toString());
//        } catch (ConnectException ex) {
//            throw new PException(connectMessage);
//        } catch (Exception ex) {
//            throw new PException(otherMessage);
//        }
//        return result;
//    }

    public static int invokeLogin3WS(String username, String password) throws PException {

        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }

        String webMethName = Util.getStringWS(R.string.ws_getRoleInAll);
        int result = -1;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Integer.parseInt(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }


//    public static String invokeNewLoginWS(String username, String password) throws PException {
//
//        if (!G.isOnline()) {
//            throw new PException(isOnlineMessage);
//        }
//
//        String webMethName = "login";
//        String result = null;
//        SoapObject request = new SoapObject(NAMESPACE, webMethName);
//
//        request.addProperty("username", username);
//        request.addProperty("password", password);
//
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.setOutputSoapObject(request);
//        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
//        try {
//            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
//            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
//            result = response.toString();
//        } catch (ConnectException ex) {
//            throw new PException(connectMessage);
//        } catch (Exception ex) {
//            throw new PException(otherMessage);
//        }
//        return result;
//    }

//    public static Boolean invokeAddTurnWs(String name, String webMethName, String username
//            , String password, int officeId, String date, int startHour
//            , int startMin, int duration, int capacity) throws PException
//
//    {
//        if (!G.isOnline()) {
//            throw new PException(isOnlineMessage);
//        }
//        boolean result = false;
//        SoapObject request = new SoapObject(NAMESPACE, webMethName);
//
//        request.addProperty("username", username);
//        request.addProperty("password", password);
//        request.addProperty("officeId", officeId);
//        request.addProperty("date", date);
//        request.addProperty("startHour", startHour);
//        request.addProperty("startMin", startMin);
//        request.addProperty("duration", duration);
//        request.addProperty("capacity", capacity);
//
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.setOutputSoapObject(request);
//        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
//        try {
//            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
//            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
//            result = Boolean.valueOf(response.toString());
//        } catch (ConnectException ex) {
//            throw new PException(connectMessage);
//        } catch (Exception ex) {
//            throw new PException(otherMessage);
//        }
//
//        return result;
//    }

    public static User invokeGetUserInfoWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getUserInfoWithoutPic);
        User user = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            user = new User();
            user.setFirstName(response.getProperty(Util.getStringWS(R.string.ws_name)).toString());
            user.setLastName(response.getProperty(Util.getStringWS(R.string.ws_lastname)).toString());
            user.setPhone(response.getProperty(Util.getStringWS(R.string.ws_mobileno)).toString());
            user.setUserName(response.getProperty(Util.getStringWS(R.string.ws_username)).toString());
            user.setRole(Integer.parseInt(response.getProperty(Util.getStringWS(R.string.ws_role)).toString()));
            user.setCityID(Integer.parseInt(response.getProperty(Util.getStringWS(R.string.ws_cityid)).toString()));
            user.setCityName(response.getProperty(Util.getStringWS(R.string.ws_city)).toString());
            user.setStateID(Integer.parseInt(response.getProperty(Util.getStringWS(R.string.ws_provinceid)).toString()));
            user.setStateName(response.getProperty(Util.getStringWS(R.string.ws_province)).toString());

//            try {
//                String pic = response.getProperty("pic").toString();
//                byte[] imgbytes = Base64.decode(pic, Base64.DEFAULT);
//                user.setImgProfile(BitmapFactory.decodeByteArray(imgbytes, 0, imgbytes.length));
//            } catch (Exception e) {
//                user.setImgProfile(null);
//            }
            user.setPassword(password);
            try {
                if (response.getProperty(Util.getStringWS(R.string.ws_email)).toString().equals("anyType{}"))
                    user.setEmail("");
                else
                    user.setEmail(response.getProperty(Util.getStringWS(R.string.ws_email)).toString());

            } catch (Exception ex) {
                user.setEmail("");
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }

        return user;
    }


    public static Office invokeGetOfficeInfoWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getOfficeInfo);
        Office office = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            if (response != null) {
                office = new Office();
                office.setId(Integer.parseInt(response.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                office.setDrUsername(response.getProperty(Util.getStringWS(R.string.ws_doctorUsername)).toString());
                office.setFirstname(response.getProperty(Util.getStringWS(R.string.ws_doctorName)).toString());
                office.setLastname(response.getProperty(Util.getStringWS(R.string.ws_doctorLastName)).toString());
                office.setCityId(Integer.parseInt(response.getProperty(Util.getStringWS(R.string.ws_cityId)).toString()));
                office.setCityName(response.getProperty(Util.getStringWS(R.string.ws_city)).toString());
                office.setStateId(Integer.parseInt(response.getProperty(Util.getStringWS(R.string.ws_provinceId)).toString()));
                office.setStateName(response.getProperty(Util.getStringWS(R.string.ws_province)).toString());
                office.setExpertId(Integer.parseInt(response.getProperty(Util.getStringWS(R.string.ws_specId)).toString()));
                office.setExpertName(response.getProperty(Util.getStringWS(R.string.ws_spec)).toString());
                office.setSubExpertId(Integer.parseInt(response.getProperty(Util.getStringWS(R.string.ws_subspecId)).toString()));
                office.setSubExpertName(response.getProperty(Util.getStringWS(R.string.ws_subSpec)).toString());
                office.setAddress(response.getProperty(Util.getStringWS(R.string.ws_address)).toString());
                office.setPhone(response.getProperty(Util.getStringWS(R.string.ws_tellNo)).toString());
                office.setLatitude(Double.parseDouble(response.getProperty(Util.getStringWS(R.string.ws_latitude)).toString()));
                office.setLongitude(Double.parseDouble(response.getProperty(Util.getStringWS(R.string.ws_longitude)).toString()));
                try {
                    office.setBiography(response.getProperty(Util.getStringWS(R.string.ws_biograophy)).toString());
                } catch (Exception e) {
                    office.setBiography("");
                }
                office.setTimeQuantum(Integer.parseInt(response.getProperty(Util.getStringWS(R.string.ws_timeQuantum)).toString()));
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return office;
    }

    public static ArrayList<Office> invokeGetOfficeForUserWS(String username, String password) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getOfficeForUser);
        Office office = null;
        ArrayList<Office> offices = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response_test = (SoapObject) envelope.getResponse();
            SoapObject response = (SoapObject) envelope.bodyIn;
            if (response_test != null) {
                offices = new ArrayList<Office>();
                for (int i = 0; i < response.getPropertyCount(); i++) {
                    SoapObject object = (SoapObject) response.getProperty(i);
                    if (object != null) {
                        office = new Office();
                        office.setId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                        office.setDrUsername(object.getProperty(Util.getStringWS(R.string.ws_doctorUsername)).toString());
                        office.setFirstname(object.getProperty(Util.getStringWS(R.string.ws_doctorName)).toString());
                        office.setLastname(object.getProperty(Util.getStringWS(R.string.ws_doctorLastName)).toString());
                        office.setCityId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_cityId)).toString()));
                        office.setCityName(object.getProperty(Util.getStringWS(R.string.ws_city)).toString());
                        office.setStateId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_provinceId)).toString()));
                        office.setStateName(object.getProperty(Util.getStringWS(R.string.ws_province)).toString());
                        office.setExpertId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_specId)).toString()));
                        office.setExpertName(object.getProperty(Util.getStringWS(R.string.ws_spec)).toString());
                        office.setSubExpertId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_subspecId)).toString()));
                        office.setSubExpertName(object.getProperty(Util.getStringWS(R.string.ws_subSpec)).toString());
                        office.setAddress(object.getProperty(Util.getStringWS(R.string.ws_address)).toString());
                        office.setPhone(object.getProperty(Util.getStringWS(R.string.ws_tellNo)).toString());
                        office.setLatitude(Double.parseDouble(object.getProperty(Util.getStringWS(R.string.ws_latitude)).toString()));
                        office.setLongitude(Double.parseDouble(object.getProperty(Util.getStringWS(R.string.ws_longitude)).toString()));
                        try {
                            office.setBiography(object.getProperty(Util.getStringWS(R.string.ws_biograophy)).toString());
                        } catch (Exception e) {
                            office.setBiography("");
                        }
                        office.setTimeQuantum(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_timeQuantum)).toString()));
                        offices.add(office);
                    }
                }
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return offices;
    }

    public static ArrayList<Office> invokeGetAllOfficesForCityWS(String username, String password, int cityId, int count, int index) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getAllOfficeForCity);
        Office office = null;
        ArrayList<Office> offices = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_cityId), cityId);
        request.addProperty(Util.getStringWS(R.string.ws_count), count);
        request.addProperty(Util.getStringWS(R.string.ws_index), index);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response_test = (SoapObject) envelope.getResponse();
            SoapObject response = (SoapObject) envelope.bodyIn;
            if (response_test != null) {
                offices = new ArrayList<Office>();
                for (int i = 0; i < response.getPropertyCount(); i++) {
                    SoapObject object = (SoapObject) response.getProperty(i);
                    if (object != null) {
                        office = new Office();
                        office.setId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                        office.setDrUsername(object.getProperty(Util.getStringWS(R.string.ws_doctorUsername)).toString());
                        office.setFirstname(object.getProperty(Util.getStringWS(R.string.ws_doctorName)).toString());
                        office.setLastname(object.getProperty(Util.getStringWS(R.string.ws_doctorLastName)).toString());
                        office.setCityId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_cityId)).toString()));
                        office.setCityName(object.getProperty(Util.getStringWS(R.string.ws_city)).toString());
                        office.setStateId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_provinceId)).toString()));
                        office.setStateName(object.getProperty(Util.getStringWS(R.string.ws_province)).toString());
                        office.setExpertId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_specId)).toString()));
                        office.setExpertName(object.getProperty(Util.getStringWS(R.string.ws_spec)).toString());
                        office.setSubExpertId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_subspecId)).toString()));
                        office.setSubExpertName(object.getProperty(Util.getStringWS(R.string.ws_subSpec)).toString());
                        office.setAddress(object.getProperty(Util.getStringWS(R.string.ws_address)).toString());
                        office.setPhone(object.getProperty(Util.getStringWS(R.string.ws_tellNo)).toString());
                        office.setLatitude(Double.parseDouble(object.getProperty(Util.getStringWS(R.string.ws_latitude)).toString()));
                        office.setLongitude(Double.parseDouble(object.getProperty(Util.getStringWS(R.string.ws_longitude)).toString()));
                        try {
                            office.setBiography(object.getProperty(Util.getStringWS(R.string.ws_biograophy)).toString());
                        } catch (Exception e) {
                            office.setBiography("");
                        }
                        office.setTimeQuantum(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_timeQuantum)).toString()));
                        offices.add(office);
                    }
                }
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return offices;
    }


    public static ArrayList<Office> invokeGetOfficeForDoctorOrSecretaryWS(String username, String password) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getOfficeForDoctorOrSecretary);
        Office office = null;
        ArrayList<Office> offices = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response_test = (SoapObject) envelope.getResponse();
            SoapObject response = (SoapObject) envelope.bodyIn;
            if (response_test != null) {
                offices = new ArrayList<Office>();
                for (int i = 0; i < response.getPropertyCount(); i++) {
                    SoapObject object = (SoapObject) response.getProperty(i);
                    office = new Office();
                    office.setId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                    office.setDrUsername(object.getProperty(Util.getStringWS(R.string.ws_doctorUsername)).toString());
                    office.setFirstname(object.getProperty(Util.getStringWS(R.string.ws_doctorName)).toString());
                    office.setLastname(object.getProperty(Util.getStringWS(R.string.ws_doctorLastName)).toString());
                    office.setCityId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_cityId)).toString()));
                    office.setCityName(object.getProperty(Util.getStringWS(R.string.ws_city)).toString());
                    office.setStateId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_provinceId)).toString()));
                    office.setStateName(object.getProperty(Util.getStringWS(R.string.ws_province)).toString());
                    office.setExpertId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_specId)).toString()));
                    office.setExpertName(object.getProperty(Util.getStringWS(R.string.ws_spec)).toString());
                    office.setSubExpertId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_subspecId)).toString()));
                    office.setSubExpertName(object.getProperty(Util.getStringWS(R.string.ws_subSpec)).toString());
                    office.setAddress(object.getProperty(Util.getStringWS(R.string.ws_address)).toString());
                    office.setPhone(object.getProperty(Util.getStringWS(R.string.ws_tellNo)).toString());
                    office.setLatitude(Double.parseDouble(object.getProperty(Util.getStringWS(R.string.ws_latitude)).toString()));
                    office.setLongitude(Double.parseDouble(object.getProperty(Util.getStringWS(R.string.ws_longitude)).toString()));
                    try {
                        office.setBiography(object.getProperty(Util.getStringWS(R.string.ws_biograophy)).toString());
                    } catch (Exception e) {
                        office.setBiography("");
                    }
                    office.setTimeQuantum(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_timeQuantum)).toString()));
                    office.setMyOffice(Boolean.valueOf(object.getProperty(Util.getStringWS(R.string.ws_isMyOffice)).toString()));
                    offices.add(office);

                }
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return offices;
    }

    public static ArrayList<Office> invokeGetOfficeByFilterWS(String username, String password
            , int provinceId, int cityId, int specId, int subspecId, String firstname, String lastname, int count, int index) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getOfficeByFilter);
        Office office = null;
        ArrayList<Office> offices = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_provinceId), provinceId);
        request.addProperty(Util.getStringWS(R.string.ws_cityId), cityId);
        request.addProperty(Util.getStringWS(R.string.ws_specId), specId);
        request.addProperty(Util.getStringWS(R.string.ws_subspecId), subspecId);
        request.addProperty(Util.getStringWS(R.string.ws_firstName), firstname);
        request.addProperty(Util.getStringWS(R.string.ws_lastName), lastname);
        request.addProperty(Util.getStringWS(R.string.ws_count), count);
        request.addProperty(Util.getStringWS(R.string.ws_index), index);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response_test = (SoapObject) envelope.getResponse();
            SoapObject response = (SoapObject) envelope.bodyIn;
            if (response_test != null) {
                offices = new ArrayList<Office>();
                for (int i = 0; i < response.getPropertyCount(); i++) {
                    SoapObject object = (SoapObject) response.getProperty(i);
                    if (object != null) {
                        office = new Office();
                        office.setId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                        office.setDrUsername(object.getProperty(Util.getStringWS(R.string.ws_doctorUsername)).toString());
                        office.setFirstname(object.getProperty(Util.getStringWS(R.string.ws_doctorName)).toString());
                        office.setLastname(object.getProperty(Util.getStringWS(R.string.ws_doctorLastName)).toString());
                        office.setCityId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_cityId)).toString()));
                        office.setCityName(object.getProperty(Util.getStringWS(R.string.ws_city)).toString());
                        office.setStateId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_provinceId)).toString()));
                        office.setStateName(object.getProperty(Util.getStringWS(R.string.ws_province)).toString());
                        office.setExpertId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_specId)).toString()));
                        office.setExpertName(object.getProperty(Util.getStringWS(R.string.ws_spec)).toString());
                        office.setSubExpertId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_subspecId)).toString()));
                        office.setSubExpertName(object.getProperty(Util.getStringWS(R.string.ws_subSpec)).toString());
                        office.setAddress(object.getProperty(Util.getStringWS(R.string.ws_address)).toString());
                        office.setPhone(object.getProperty(Util.getStringWS(R.string.ws_tellNo)).toString());
                        office.setLatitude(Double.parseDouble(object.getProperty(Util.getStringWS(R.string.ws_latitude)).toString()));
                        office.setLongitude(Double.parseDouble(object.getProperty(Util.getStringWS(R.string.ws_longitude)).toString()));
                        try {
                            office.setBiography(object.getProperty(Util.getStringWS(R.string.ws_biograophy)).toString());
                        } catch (Exception e) {
                            office.setBiography("");
                        }
                        office.setTimeQuantum(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_timeQuantum)).toString()));
//                        office.setMyOffice(Boolean.valueOf(object.getProperty("isMyOffice").toString()));
                        offices.add(office);
                    }

                }
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return offices;
    }

    public static Bitmap invokeGetDoctorPicWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getDoctorPic);
        Bitmap pic = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            if (response != null) {
                String img = response.toString();
                byte[] imgbytes = Base64.decode(img, Base64.DEFAULT);
                pic = BitmapFactory.decodeByteArray(imgbytes, 0, imgbytes.length);
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return pic;
    }

    public static Bitmap invokeGetUserPicWS(String username, String password) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getUserPic);
        Bitmap pic = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
//        request.addProperty("officeId", G.officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            if (response != null) {
                String img = response.toString();
                byte[] imgbytes = Base64.decode(img, Base64.DEFAULT);
                pic = BitmapFactory.decodeByteArray(imgbytes, 0, imgbytes.length);
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return pic;
    }

    public static boolean invokeUpdateUserPicWS(String username, String password, Bitmap pic) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_updateUserPic2);
        boolean result = false;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
//        request.addProperty("officeId", G.officeId);
        byte[] img = getBytes(pic);
        request.addProperty(Util.getStringWS(R.string.ws_pic), Base64.encodeToString(img, Base64.DEFAULT));


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        new MarshalBase64().register(envelope);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Boolean.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static boolean invokeUpdateOfficeLatLngWS(String username, String password, int officeId, double latitude, double longitude) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_updateOfficeLocation);
        boolean result = false;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_latitude), String.valueOf(latitude));
        request.addProperty(Util.getStringWS(R.string.ws_longitude), String.valueOf(longitude));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Boolean.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static ArrayList<Expert> invokeGetSpecWS() throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getSpec);
        Expert expert;
        ArrayList<Expert> experts = new ArrayList<Expert>();
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {
                expert = new Expert();
                SoapObject obj = (SoapObject) response.getProperty(i);
                expert.setId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                expert.setName(obj.getProperty(Util.getStringWS(R.string.ws_name)).toString());
                experts.add(expert);
            }

        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return experts;
    }

    public static ArrayList<SubExpert> invokeGetSubSpecWS(int specId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getSubSpec);
        SubExpert subExpert;
        ArrayList<SubExpert> subExperts = new ArrayList<SubExpert>();
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_specId), specId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {
                subExpert = new SubExpert();
                SoapObject obj = (SoapObject) response.getProperty(i);
                subExpert.setId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                subExpert.setExpertId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_specId)).toString()));
                subExpert.setName(obj.getProperty(Util.getStringWS(R.string.ws_name)).toString());
                subExperts.add(subExpert);
            }

        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return subExperts;
    }

    public static boolean invokeUpdateOfficeWS(String username, String password, Office office) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_updateOfficeInfo);
        boolean result = false;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), office.getId());
        request.addProperty(Util.getStringWS(R.string.ws_cityId), office.getCityId());
        request.addProperty(Util.getStringWS(R.string.ws_spec), office.getExpertId());
        request.addProperty(Util.getStringWS(R.string.ws_subSpec), office.getSubExpertId());
        request.addProperty(Util.getStringWS(R.string.ws_address), office.getAddress());
        request.addProperty(Util.getStringWS(R.string.ws_tellNo), office.getPhone());
        request.addProperty(Util.getStringWS(R.string.ws_biography), office.getBiography());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Boolean.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static ArrayList<Turn> invokeGetAllTurnFromToday(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        Turn turn;
        ArrayList<Turn> result = new ArrayList<Turn>();
        String webMethName = Util.getStringWS(R.string.ws_getAllTurnFromToday);

        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;

            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject object = (SoapObject) response.getProperty(i);
                turn = new Turn();
                turn.setId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                turn.setDate(object.getProperty(Util.getStringWS(R.string.ws_date)).toString());
                turn.setLongDate(object.getProperty(Util.getStringWS(R.string.ws_longDate)).toString());
                turn.setHour(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_hour)).toString()));
                turn.setMin(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_min)).toString()));
                turn.setCapacity(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_capacity)).toString()));
                turn.setDuration(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_duration)).toString()));
                turn.setOfficeId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_officeId)).toString()));
                turn.setReserved(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_reserved)).toString()));
                turn.setIsReserved(Boolean.valueOf(object.getProperty(Util.getStringWS(R.string.ws_isReserved)).toString()));
                result.add(turn);
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static int invokeResevereForUser(String username, String password, Reservation reservation) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_reserveForUser);
        int result = 0;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_turnId), reservation.getTurnId());
        request.addProperty(Util.getStringWS(R.string.ws_firstReservationId), reservation.getFirstReservationId());
        request.addProperty(Util.getStringWS(R.string.ws_taskId), reservation.getTaskId());
        request.addProperty(Util.getStringWS(R.string.ws_numberOfTurns), reservation.getNumberOfTurns());
        request.addProperty(Util.getStringWS(R.string.ws_patientUserName), reservation.getPatientUserName());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Integer.parseInt(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static String invokeCancleReservation(String username, String password, int reservationId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_cancelReservation);
        String result = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
//        request.addProperty("officeId", G.officeId);
        request.addProperty(Util.getStringWS(R.string.ws_reservationId), reservationId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = response.toString();
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static ArrayList<User> invokeSearchUserWS(String username, String name, String lastName, String mobileNo) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        User user = null;
        ArrayList<User> users = new ArrayList<User>();
        String webMethName = Util.getStringWS(R.string.ws_searchUser);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_name), name);
        request.addProperty(Util.getStringWS(R.string.ws_lastName), lastName);
        request.addProperty(Util.getStringWS(R.string.ws_mobileNo), mobileNo);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), G.officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {

                SoapObject obj = (SoapObject) response.getProperty(i);
                user = new User();
                user.setUserName(obj.getProperty(Util.getStringWS(R.string.ws_username)).toString());
                user.setFirstName(obj.getProperty(Util.getStringWS(R.string.ws_name)).toString());
                user.setLastName(obj.getProperty(Util.getStringWS(R.string.ws_lastname)).toString());
                user.setPhone(obj.getProperty(Util.getStringWS(R.string.ws_mobileno)).toString());
                user.setCityName(obj.getProperty(Util.getStringWS(R.string.ws_city)).toString());
                users.add(user);
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return users;
    }

    public static ArrayList<Reservation> invokeGetReservationByTurnIdWS(String username, String password, int officeId, int turnId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        Reservation reservation = null;
        ArrayList<Reservation> reservations = new ArrayList<Reservation>();
        String webMethName = Util.getStringWS(R.string.ws_getReservationByTurnId);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_turnId), turnId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            if (response != null) {
                for (int i = 0; i < response.getPropertyCount(); i++) {

                    SoapObject obj = (SoapObject) response.getProperty(i);
                    reservation = new Reservation();
                    reservation.setId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                    reservation.setUsername(obj.getProperty(Util.getStringWS(R.string.ws_username)).toString());
                    reservation.setTurnId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_turnId)).toString()));
                    reservation.setTaskId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_taskId)).toString()));
                    reservation.setPatientFirstName(obj.getProperty(Util.getStringWS(R.string.ws_patientFirstName)).toString());
                    reservation.setPatientLastName(obj.getProperty(Util.getStringWS(R.string.ws_patientLastName)).toString());
                    reservation.setFirstReservationId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_firstReservationId)).toString()));
                    reservation.setPayment(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_payment)).toString()));
                    reservation.setNumberOfTurns(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_numberOfTurns)).toString()));
                    reservations.add(reservation);
                }
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return reservations;
    }

//    public static ArrayList<Reservation> invokeGetReservationByDateWS(String username, String password, int officeId
//            , String fromDate, String toDate) throws PException {
//        if (!G.isOnline()) {
//            throw new PException(isOnlineMessage);
//        }
//        Reservation reservationInfo = null;
//        ArrayList<Reservation> reservations = new ArrayList<Reservation>();
//        String webMethName = "getReservationByDate";
//        SoapObject request = new SoapObject(NAMESPACE, webMethName);
//
//        request.addProperty("username", username);
//        request.addProperty("password", password);
//        request.addProperty("officeId", officeId);
//        request.addProperty("fromDate", fromDate);
//        request.addProperty("toDate", toDate);
//
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.setOutputSoapObject(request);
//        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
//
//        try {
//            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
//            SoapObject response = (SoapObject) envelope.bodyIn;
//            for (int i = 0; i < response.getPropertyCount(); i++) {
//
//                SoapObject obj = (SoapObject) response.getProperty(i);
//                reservationInfo = new Reservation();
//                reservationInfo.setId(Integer.parseInt(obj.getProperty("id").toString()));
//                reservationInfo.setUsername(obj.getProperty("username").toString());
//                reservationInfo.setTurnId(Integer.parseInt(obj.getProperty("turnId").toString()));
//                reservationInfo.setTaskId(Integer.parseInt(obj.getProperty("taskId").toString()));
//                reservationInfo.setPatientFirstName(obj.getProperty("patientFirstName").toString());
//                reservationInfo.setPatientLastName(obj.getProperty("patientLastName").toString());
//                reservationInfo.setFirstReservationId(Integer.parseInt(obj.getProperty("firstResevationId").toString()));
//                reservationInfo.setPayment(Integer.parseInt(obj.getProperty("PaymentInfo").toString()));
//                reservationInfo.setNumberOfTurns(Integer.parseInt(obj.getProperty("numberOfTurns").toString()));
//                reservations.add(reservationInfo);
//            }
//        } catch (ConnectException ex) {
//            throw new PException(connectMessage);
//        } catch (Exception ex) {
//            throw new PException(otherMessage);
//        }
//        return reservations;
//    }

    public static ArrayList<Task> invokeGetTaskWS(String username, String password, int officeId, int taskGroupId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        Task task = null;
        ArrayList<Task> taskes = new ArrayList<Task>();
        String webMethName = Util.getStringWS(R.string.ws_getTasks);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_taskGroupId), taskGroupId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {

                SoapObject obj = (SoapObject) response.getProperty(i);
                task = new Task();
                task.setId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                task.setOfficeId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_officeId)).toString()));
                task.setName(obj.getProperty(Util.getStringWS(R.string.ws_name)).toString());
                task.setPrice(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_price)).toString()));
                task.setGroupId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_taskGroupId)).toString()));
                taskes.add(task);
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return taskes;
    }

    public static ArrayList<TaskGroup> invokeGetTaskGroupsWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        TaskGroup task = null;
        ArrayList<TaskGroup> taskGroups = new ArrayList<TaskGroup>();
        String webMethName = Util.getStringWS(R.string.ws_getTaskGroups);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {

                SoapObject obj = (SoapObject) response.getProperty(i);
                if (obj != null) {
                    task = new TaskGroup();
                    task.setId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                    task.setOfficeId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_officeId)).toString()));
                    task.setName(obj.getProperty(Util.getStringWS(R.string.ws_name)).toString());
                    taskGroups.add(task);
                }
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return taskGroups;
    }

    public static int invokeReserveForGuestWS(String username, String password, Reservation reservation, int cityId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        int result = 0;
        String webMethName = Util.getStringWS(R.string.ws_reserveForGuest);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_turnId), reservation.getTurnId());
        request.addProperty(Util.getStringWS(R.string.ws_firstReservationId), reservation.getFirstReservationId());
        request.addProperty(Util.getStringWS(R.string.ws_taskId), reservation.getTaskId());
        request.addProperty(Util.getStringWS(R.string.ws_numberOfTurns), reservation.getNumberOfTurns());
        request.addProperty(Util.getStringWS(R.string.ws_patientFirstName), reservation.getPatientFirstName());
        request.addProperty(Util.getStringWS(R.string.ws_patientLastName), reservation.getPatientLastName());
        request.addProperty(Util.getStringWS(R.string.ws_patientPhoneNo), reservation.getPatientPhoneNo());
        request.addProperty(Util.getStringWS(R.string.ws_patientCityId), cityId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Integer.parseInt(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static int invokeGetWalletWS(String username, String password) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        int result = 0;
        String webMethName = Util.getStringWS(R.string.ws_getWallet);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Integer.parseInt(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static int invokeReserveForGuestFromUserWS(String username, String password, Reservation reservation, int resNum) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        int result = 0;
        String webMethName = Util.getStringWS(R.string.ws_reserveForGuestFromUser);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_turnId), reservation.getTurnId());
        request.addProperty(Util.getStringWS(R.string.ws_firstReservationId), reservation.getFirstReservationId());
        request.addProperty(Util.getStringWS(R.string.ws_taskId), reservation.getTaskId());
        request.addProperty(Util.getStringWS(R.string.ws_numberOfTurns), reservation.getNumberOfTurns());
        request.addProperty(Util.getStringWS(R.string.ws_patientFirstName), reservation.getPatientFirstName());
        request.addProperty(Util.getStringWS(R.string.ws_patientLastName), reservation.getPatientLastName());
        request.addProperty(Util.getStringWS(R.string.ws_patientPhoneNo), reservation.getPatientPhoneNo());
        request.addProperty(Util.getStringWS(R.string.ws_patientCityId), reservation.getCityId());
        request.addProperty(Util.getStringWS(R.string.ws_resNum), resNum);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Integer.parseInt(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static int invokeReserveForMeWS(String username, String password, Reservation reservation, int resNum) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        int result = 0;
        String webMethName = Util.getStringWS(R.string.ws_reserveForMe);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_turnId), reservation.getTurnId());
        request.addProperty(Util.getStringWS(R.string.ws_firstReservationId), reservation.getFirstReservationId());
        request.addProperty(Util.getStringWS(R.string.ws_taskId), reservation.getTaskId());
        request.addProperty(Util.getStringWS(R.string.ws_numberOfTurns), reservation.getNumberOfTurns());
        request.addProperty(Util.getStringWS(R.string.ws_resNum), resNum);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Integer.parseInt(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static boolean invokeAddTurnByDateWS(String username, String password, int officeId
            , String fromDate, String toDate, int hour, int min, int duration, int capacity, String dayOfWeek) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        boolean result = false;
        String webMethName = Util.getStringWS(R.string.ws_addTurnByDate);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_fromDate), fromDate);
        request.addProperty(Util.getStringWS(R.string.ws_toDate), toDate);
        request.addProperty(Util.getStringWS(R.string.ws_hour), hour);
        request.addProperty(Util.getStringWS(R.string.ws_min), min);
        request.addProperty(Util.getStringWS(R.string.ws_duration), duration);
        request.addProperty(Util.getStringWS(R.string.ws_capacity), capacity);
        request.addProperty(Util.getStringWS(R.string.ws_dayOfWeek), dayOfWeek);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Boolean.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static ArrayList<Turn> invokeGetAllTurnWS(String username, String password, int officeId, String fromDate, String toDate) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        Turn turn;
        ArrayList<Turn> turns = new ArrayList<Turn>();
        String webMethName = Util.getStringWS(R.string.ws_getAllTurn);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_fromDate), fromDate);
        request.addProperty(Util.getStringWS(R.string.ws_toDate), toDate);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;

            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject object = (SoapObject) response.getProperty(i);
                turn = new Turn();
                turn.setId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                turn.setDate(object.getProperty(Util.getStringWS(R.string.ws_date)).toString());
                turn.setLongDate(object.getProperty(Util.getStringWS(R.string.ws_longDate)).toString());
                turn.setHour(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_hour)).toString()));
                turn.setMin(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_min)).toString()));
                turn.setCapacity(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_capacity)).toString()));
                turn.setDuration(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_duration)).toString()));
                turn.setOfficeId(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_officeId)).toString()));
                turn.setReserved(Integer.parseInt(object.getProperty(Util.getStringWS(R.string.ws_reserved)).toString()));
                turn.setIsReserved(Boolean.valueOf(object.getProperty(Util.getStringWS(R.string.ws_isReserved)).toString()));
                turns.add(turn);
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return turns;
    }

    public static boolean invokeRemoveTurnWS(String username, String password, int officeId, int turnId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        boolean result = false;
        String webMethName = Util.getStringWS(R.string.ws_removeTurn);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_turnId), turnId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Boolean.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static ArrayList<Rturn> invokeGetPatientTurnInfoByDate(String username, String password, int officeId, String fromDate, String toDate) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        Rturn rturn = null;
        ArrayList<Rturn> result = new ArrayList<Rturn>();
        String webMethName = Util.getStringWS(R.string.ws_getPatientTurnInfoByDate);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_fromDate), fromDate);
        request.addProperty(Util.getStringWS(R.string.ws_toDate), toDate);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {

                rturn = new Rturn();
                SoapObject obj = (SoapObject) response.getProperty(i);
                try {
                    if (obj.getProperty(Util.getStringWS(R.string.ws_username)).toString().equals("anyType")
                            || obj.getProperty(Util.getStringWS(R.string.ws_username)).toString().equals(""))

                        rturn.setUsername("");
                    else
                        rturn.setUsername(obj.getProperty(Util.getStringWS(R.string.ws_username)).toString());
                } catch (Exception ex) {

                    rturn.setUsername("");
                }

                try {
                    if (obj.getProperty(Util.getStringWS(R.string.ws_patientUsername)).toString().equals("anyType")
                            || obj.getProperty(Util.getStringWS(R.string.ws_patientUsername)).toString().equals(""))

                        rturn.setPatientUsername("");
                    else
                        rturn.setPatientUsername(obj.getProperty(Util.getStringWS(R.string.ws_patientUsername)).toString());
                } catch (Exception ex) {

                    rturn.setPatientUsername("");
                }

                rturn.setPatientFirstName(obj.getProperty(Util.getStringWS(R.string.ws_patientFirstName)).toString());
                rturn.setPatientLastName(obj.getProperty(Util.getStringWS(R.string.ws_patientLastName)).toString());
                rturn.setPatientPhoneNo(obj.getProperty(Util.getStringWS(R.string.ws_patientPhoneNo)).toString());
                rturn.setNumberOfTurns(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_numberOfTurns)).toString()));
                rturn.setShortDate(obj.getProperty(Util.getStringWS(R.string.ws_shortDate)).toString());
                rturn.setLongDate(obj.getProperty(Util.getStringWS(R.string.ws_longDate)).toString());
                rturn.setHour(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_hour)).toString()));
                rturn.setMin(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_min)).toString()));
                rturn.setDuration(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_duration)).toString()));
                rturn.setCapacity(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_capacity)).toString()));
                rturn.setReserved(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_reserved)).toString()));
                rturn.setReservationId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_reservationId)).toString()));
                rturn.setTurnId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_turnId)).toString()));
                rturn.setOfficeId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_officeId)).toString()));
                rturn.setTaskId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_taskId)).toString()));
                rturn.setTaskName(obj.getProperty(Util.getStringWS(R.string.ws_taskName)).toString());
                rturn.setPrice(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_price)).toString()));
                result.add(rturn);
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;

    }

    public static boolean invokeSendMessageBatchWS(String username, String password
            , int officeId, ArrayList<String> receivers, ArrayList<String> phoneNos, String subject, String message) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }

        boolean result = false;
        String webMethName = Util.getStringWS(R.string.ws_sendMessageBatch);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo receiverProperty = null;
        StringArraySerializer stringArrayUsername = new StringArraySerializer();
        for (int i = 0; i < receivers.size(); i++) {
            stringArrayUsername.add(receivers.get(i));
            receiverProperty = new PropertyInfo();
            receiverProperty.setName(Util.getStringWS(R.string.ws_receivers));
            receiverProperty.setValue(stringArrayUsername);
            receiverProperty.setType(stringArrayUsername.getClass());
            receiverProperty.setNamespace(NAMESPACE);
        }

        PropertyInfo phoneNoProperty = null;
        StringArraySerializer stringArrayPhoneNo = new StringArraySerializer();
        for (int i = 0; i < phoneNos.size(); i++) {
            stringArrayPhoneNo.add(phoneNos.get(i));
            phoneNoProperty = new PropertyInfo();
            phoneNoProperty.setName(Util.getStringWS(R.string.ws_phoneNo));
            phoneNoProperty.setValue(stringArrayPhoneNo);
            phoneNoProperty.setType(stringArrayPhoneNo.getClass());
            phoneNoProperty.setNamespace(NAMESPACE);
        }
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(receiverProperty);
        request.addProperty(phoneNoProperty);
        request.addProperty(Util.getStringWS(R.string.ws_subject), subject);
        request.addProperty(Util.getStringWS(R.string.ws_message), message);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Boolean.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }

        return result;
    }

    public static ArrayList<MessageInfo> invokeGetUnreadMessagesWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        MessageInfo messageInfo = null;
        ArrayList<MessageInfo> result = null;
        String webMethName = Util.getStringWS(R.string.ws_getUnreadMessages);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            result = new ArrayList<MessageInfo>();
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) response.getProperty(i);
                messageInfo = new MessageInfo();
                messageInfo.setId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                messageInfo.setSenderUsername(obj.getProperty(Util.getStringWS(R.string.ws_senderUsername)).toString());
                messageInfo.setSenderFirstName(obj.getProperty(Util.getStringWS(R.string.ws_senderFirstName)).toString());
                messageInfo.setSenderLastName(obj.getProperty(Util.getStringWS(R.string.ws_senderLastName)).toString());
                messageInfo.setSubject(obj.getProperty(Util.getStringWS(R.string.ws_subject)).toString());
                messageInfo.setMessage(obj.getProperty(Util.getStringWS(R.string.ws_message)).toString());
                messageInfo.setDate(obj.getProperty(Util.getStringWS(R.string.ws_date)).toString());
                messageInfo.setTime(obj.getProperty(Util.getStringWS(R.string.ws_time)).toString());
                result.add(messageInfo);

            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }

        return result;
    }

    public static ArrayList<MessageInfo> invokeGetAllUnreadMessagesWS(String username, String password) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        MessageInfo messageInfo = null;
        ArrayList<MessageInfo> result = null;
        String webMethName = Util.getStringWS(R.string.ws_getAllUnreadMessages);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            result = new ArrayList<MessageInfo>();
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) response.getProperty(i);
                messageInfo = new MessageInfo();
                messageInfo.setId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                messageInfo.setSenderUsername(obj.getProperty(Util.getStringWS(R.string.ws_senderUsername)).toString());
                messageInfo.setSenderFirstName(obj.getProperty(Util.getStringWS(R.string.ws_senderFirstName)).toString());
                messageInfo.setSenderLastName(obj.getProperty(Util.getStringWS(R.string.ws_senderLastName)).toString());
                messageInfo.setSubject(obj.getProperty(Util.getStringWS(R.string.ws_subject)).toString());
                messageInfo.setMessage(obj.getProperty(Util.getStringWS(R.string.ws_message)).toString());
                messageInfo.setDate(obj.getProperty(Util.getStringWS(R.string.ws_date)).toString());
                messageInfo.setTime(obj.getProperty(Util.getStringWS(R.string.ws_time)).toString());
                result.add(messageInfo);

            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }

        return result;
    }


//    public static ArrayList<MessageInfo> invokeGetAllMessagesWS(String username, String password, int officeId) throws PException {
//        if (!G.isOnline()) {
//            throw new PException(isOnlineMessage);
//        }
//        MessageInfo messageInfo = null;
//        ArrayList<MessageInfo> result = null;
//        String webMethName = "getAllMessages";
//        SoapObject request = new SoapObject(NAMESPACE, webMethName);
//
//        request.addProperty("username", username);
//        request.addProperty("password", password);
//        request.addProperty("officeId", officeId);
//
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.setOutputSoapObject(request);
//        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
//
//        try {
//            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
//            SoapObject response = (SoapObject) envelope.bodyIn;
//            result = new ArrayList<MessageInfo>();
//            for (int i = 0; i < response.getPropertyCount(); i++) {
//                SoapObject obj = (SoapObject) response.getProperty(i);
//                messageInfo = new MessageInfo();
//                messageInfo.setId(Integer.parseInt(obj.getProperty("id").toString()));
//                messageInfo.setSenderUsername(obj.getProperty("senderUsername").toString());
//                messageInfo.setSenderFirstName(obj.getProperty("senderFirstName").toString());
//                messageInfo.setSenderLastName(obj.getProperty("senderLastName").toString());
//                messageInfo.setSubject(obj.getProperty("subject").toString());
//                messageInfo.setMessage(obj.getProperty("message").toString());
//                messageInfo.setDate(obj.getProperty("date").toString());
//                messageInfo.setTime(obj.getProperty("time").toString());
//                result.add(messageInfo);
//
//            }
//        } catch (ConnectException ex) {
//            throw new PException(connectMessage);
//        } catch (Exception ex) {
//            throw new PException(otherMessage);
//        }
//
//        return result;
//    }

    public static ArrayList<MessageInfo> invokeGetAllMessagesWS(String username, String password) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        MessageInfo messageInfo = null;
        ArrayList<MessageInfo> result = null;
        String webMethName = Util.getStringWS(R.string.ws_getAllMessages1);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            result = new ArrayList<MessageInfo>();
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) response.getProperty(i);
                messageInfo = new MessageInfo();
                messageInfo.setId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                messageInfo.setSenderUsername(obj.getProperty(Util.getStringWS(R.string.ws_senderUsername)).toString());
                messageInfo.setSenderFirstName(obj.getProperty(Util.getStringWS(R.string.ws_senderFirstName)).toString());
                messageInfo.setSenderLastName(obj.getProperty(Util.getStringWS(R.string.ws_senderLastName)).toString());
                messageInfo.setSubject(obj.getProperty(Util.getStringWS(R.string.ws_subject)).toString());
                messageInfo.setMessage(obj.getProperty(Util.getStringWS(R.string.ws_message)).toString());
                messageInfo.setDate(obj.getProperty(Util.getStringWS(R.string.ws_date)).toString());
                messageInfo.setTime(obj.getProperty(Util.getStringWS(R.string.ws_time)).toString());
                result.add(messageInfo);

            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }

        return result;
    }

//    public static void invokeSetMessageReadWS(String username, String password, int officeId, int messageId) throws PException {
//        if (!G.isOnline()) {
//            throw new PException(isOnlineMessage);
//        }
//        String webMethName = "setMessageRead";
//        SoapObject request = new SoapObject(NAMESPACE, webMethName);
//
//        request.addProperty("username", username);
//        request.addProperty("password", password);
//        request.addProperty("officeId", officeId);
//        request.addProperty("messageId", messageId);
//
//        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//        envelope.setOutputSoapObject(request);
//        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
//
//        try {
//            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
//        } catch (ConnectException ex) {
//            throw new PException(connectMessage);
//        } catch (Exception ex) {
//            throw new PException(otherMessage);
//        }
//    }

    public static void invokeSetMessageRead2WS(String username, String password, int messageId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_setMessageRead2);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_messageId), messageId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
    }

    public static ArrayList<ReservationByUser> invokeGetReservayionByUserWS(String username, String password, int officeId, int count, int index) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        ReservationByUser reserve = null;
        ArrayList<ReservationByUser> result = null;
        String webMethName = Util.getStringWS(R.string.ws_getReservationByUser);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_count), count);
        request.addProperty(Util.getStringWS(R.string.ws_index), index);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            result = new ArrayList<ReservationByUser>();
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) response.getProperty(i);
                if (obj != null) {
                    reserve = new ReservationByUser();
                    reserve.setReservationId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_reservationId)).toString()));
                    reserve.setTurnId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_turnId)).toString()));
                    reserve.setTaskId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_taskId)).toString()));
                    reserve.setUsername(obj.getProperty(Util.getStringWS(R.string.ws_username)).toString());
                    reserve.setTaskName(obj.getProperty(Util.getStringWS(R.string.ws_taskName)).toString());
                    reserve.setPatientFirstName(obj.getProperty(Util.getStringWS(R.string.ws_patientFirstName)).toString());
                    reserve.setPatientLastName(obj.getProperty(Util.getStringWS(R.string.ws_patientLastName)).toString());
                    reserve.setPatientPhoneNo(obj.getProperty(Util.getStringWS(R.string.ws_patientPhoneNo)).toString());
                    reserve.setFirstReservationId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_firstReservationId)).toString()));
                    reserve.setPayment(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_payment)).toString()));
                    reserve.setNumberOfTurns(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_numberOfTurns)).toString()));
                    reserve.setDate(obj.getProperty(Util.getStringWS(R.string.ws_date)).toString());
                    reserve.setLongDate(obj.getProperty(Util.getStringWS(R.string.ws_longDate)).toString());
                    reserve.setTime(obj.getProperty(Util.getStringWS(R.string.ws_time)).toString());
                    reserve.setDoctorUsername(obj.getProperty(Util.getStringWS(R.string.ws_doctorUsername)).toString());
                    reserve.setDoctorFirstName(obj.getProperty(Util.getStringWS(R.string.ws_doctorFirstName)).toString());
                    reserve.setDoctorLastName(obj.getProperty(Util.getStringWS(R.string.ws_doctorLastName)).toString());
                    reserve.setDoctorSpec(obj.getProperty(Util.getStringWS(R.string.ws_doctorSpec)).toString());
                    reserve.setDoctorSubSpec(obj.getProperty(Util.getStringWS(R.string.ws_doctorSubSpec)).toString());
                    result.add(reserve);
                }

            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }

        return result;
    }

    public static boolean invokeRemoveMessageWS(String username, String password, int officeId, int messageId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        Boolean result = false;
        String webMethName = Util.getStringWS(R.string.ws_removeMessage2);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_messageId), messageId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Boolean.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }

        return result;
    }

    public static boolean invokeReceptionWS(String username, String password, int officeId,
                                            int reservationId, int payment, String description) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        boolean result = false;
        String webMethName = Util.getStringWS(R.string.ws_reception);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_reservationId), reservationId);
        request.addProperty(Util.getStringWS(R.string.ws_payment), payment);
        request.addProperty(Util.getStringWS(R.string.ws_description), description);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Boolean.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }

        return result;
    }

    public static ArrayList<PatientInfo> invokeGetTodayPatientWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        PatientInfo patientInfo = null;
        ArrayList<PatientInfo> patientInfos = new ArrayList<PatientInfo>();
        String webMethName = Util.getStringWS(R.string.ws_getTodayPatient);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            if (response != null) {
                for (int i = 0; i < response.getPropertyCount(); i++) {
                    SoapObject obj = (SoapObject) response.getProperty(i);
                    if (obj != null) {
                        patientInfo = new PatientInfo();
                        patientInfo.setFirstName(obj.getProperty(Util.getStringWS(R.string.ws_firstName)).toString());
                        patientInfo.setLastName(obj.getProperty(Util.getStringWS(R.string.ws_lastName)).toString());
                        patientInfo.setMobileNo(obj.getProperty(Util.getStringWS(R.string.ws_mobileNo)).toString());
                        try {
                            patientInfo.setUsername(obj.getProperty(Util.getStringWS(R.string.ws_username)).toString());
                        } catch (Exception ex) {
                            patientInfo.setUsername("");
                        }
                        patientInfo.setReservationId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_reservationId)).toString()));
                        patientInfo.setFirstReservationId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_firstReservationId)).toString()));
                        patientInfo.setTaskId(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_taskId)).toString()));
                        patientInfo.setTaskName(obj.getProperty(Util.getStringWS(R.string.ws_taskName)).toString());
                        patientInfo.setPayment(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_payment)).toString()));
                        try {
                            patientInfo.setDescription(obj.getProperty(Util.getStringWS(R.string.ws_description)).toString());
                        } catch (Exception ex) {
                            patientInfo.setDescription("");
                        }
                        patientInfo.setTaskGroupId(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_taskGroupId)).toString()));
                        patientInfo.setTaskGroupName(obj.getProperty(Util.getStringWS(R.string.ws_taskGroupName)).toString());
                        patientInfos.add(patientInfo);
                    }
                }
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }

        return patientInfos;
    }

    public static Map<Integer, ArrayList<PatientFile>> invokeGetPatientFileWS(String username, String password, int officeId, String patientUsername) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        PatientFile patientFile = null;
        Map<Integer, ArrayList<PatientFile>> map = null;
        ArrayList<PatientFile> patientFiles = new ArrayList<PatientFile>();
        String webMethName = Util.getStringWS(R.string.ws_getPatientFile);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_patientUsername), patientUsername);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            if (response != null) {
                for (int i = 0; i < response.getPropertyCount(); i++) {
                    SoapObject obj = (SoapObject) response.getProperty(i);
                    if (obj != null) {
                        patientFile = new PatientFile();
                        patientFile.setReservationId(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_reservationId)).toString()));
                        patientFile.setFirstReservationId(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_firstReservationId)).toString()));
                        patientFile.setDate(obj.getProperty(Util.getStringWS(R.string.ws_date)).toString());
                        patientFile.setLongDate(obj.getProperty(Util.getStringWS(R.string.ws_longDate)).toString());
                        patientFile.setTime(obj.getProperty(Util.getStringWS(R.string.ws_time)).toString());
                        patientFile.setTaskId(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_taskId)).toString()));
                        patientFile.setTaskName(obj.getProperty(Util.getStringWS(R.string.ws_taskName)).toString());
                        try {
                            patientFile.setDescription(obj.getProperty(Util.getStringWS(R.string.ws_description)).toString());
                        } catch (Exception ex) {
                            patientFile.setDescription("");
                        }
                        patientFile.setPrice(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_price)).toString()));
                        patientFile.setPayment(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_payment)).toString()));
                        patientFile.setTotalPayment(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_totalPayment)).toString()));
                        patientFile.setRemain(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_remain)).toString()));
                        patientFiles.add(patientFile);
                    }
                }
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        map = new TreeMap<Integer, ArrayList<PatientFile>>();
        for (int i = 0; i < patientFiles.size(); i++) {
            int firstReservationId = patientFiles.get(i).getFirstReservationId();
            if (firstReservationId == 0) {
                ArrayList<PatientFile> temp = new ArrayList<PatientFile>();
                PatientFile tempFile = patientFiles.get(i);
                tempFile.setTotalPayment(tempFile.getPayment());
                tempFile.setRemain(tempFile.getPrice() - tempFile.getTotalPayment());
                temp.add(patientFiles.get(i));
                map.put(tempFile.getReservationId(), temp);
            } else {
                ArrayList<PatientFile> temp = map.get(firstReservationId);
                PatientFile tempFile = patientFiles.get(i);
                int totalPayment = tempFile.getPayment();
                if (temp != null) {
                    for (int j = 0; j < temp.size(); j++) {
                        totalPayment += temp.get(j).getPayment();
                    }
                    tempFile.setTotalPayment(totalPayment);
                    tempFile.setRemain(tempFile.getPrice() - totalPayment);
                    temp.add(tempFile);
                }
            }
        }
        return map;
    }

    public static ArrayList<Integer> invokegetAllGalleryPicIdWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        ArrayList<Integer> imageIds = null;
        String webMethName = Util.getStringWS(R.string.ws_getAllGalleryPicId);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            if (!isNullBodyIn(response)) {
                imageIds = new ArrayList<Integer>();
                for (int i = 0; i < response.getPropertyCount(); i++) {
                    SoapPrimitive obj = (SoapPrimitive) response.getProperty(i);
                    imageIds.add(Integer.parseInt(obj.toString()));
                }
            }

        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }

        return imageIds;
    }

    public static PhotoDesc invokeGetGalleryPicWS(String username, String password, int officeId, int picId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getGalleryPic);
        PhotoDesc photoDesc = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_picId), picId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            photoDesc = new PhotoDesc();
            photoDesc.setId(Integer.parseInt(response.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
            String pic = response.getProperty(Util.getStringWS(R.string.ws_photo)).toString();
            byte[] imgbytes = Base64.decode(pic, Base64.DEFAULT);
            photoDesc.setPhoto(BitmapFactory.decodeByteArray(imgbytes, 0, imgbytes.length));
            if (response.getProperty(Util.getStringWS(R.string.ws_description)).toString().equals("anyType{}"))
                photoDesc.setDescription("");
            else
                photoDesc.setDescription(response.getProperty(Util.getStringWS(R.string.ws_description)).toString());

            photoDesc.setDate(response.getProperty(Util.getStringWS(R.string.ws_date)).toString());

        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }

        return photoDesc;
    }

    public static int invokeSetGalleryPicWS(String username, String password, int officeId, Bitmap pic, String description) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_setGalleryPic);
        int id = -1;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        byte[] img = getBytes(pic);
        request.addProperty(Util.getStringWS(R.string.ws_pic), Base64.encodeToString(img, Base64.DEFAULT));
        request.addProperty(Util.getStringWS(R.string.ws_description), description);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        new MarshalBase64().register(envelope);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            id = Integer.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return id;
    }

    public static void invokeDeleteFromGalleryWS(String username, String password, int officeId, int picId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_deleteFromGallery);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_picId), picId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
    }

    public static void invokeChangeGalleryPicDescriptionWS(String username, String password, int officeId, int picId, String description) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_changeGalleryPicDescription);
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_picId), picId);
        request.addProperty(Util.getStringWS(R.string.ws_description), description);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
    }

    public static int invokeAddTaskGroupWS(String username, String password, int officeId, String taskNameGroup) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_addTaskGroup);
        int result = 0;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_taskGroupName), taskNameGroup);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Integer.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static String invokeUpdateTaskGroupWS(String username, String password, int officeId, int taskGroupId, String taskNameGroup) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_updateTaskGroup);
        String result = "";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_taskGroupId), taskGroupId);
        request.addProperty(Util.getStringWS(R.string.ws_taskGroupName), taskNameGroup);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = response.toString();
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static String invokeDeleteTaskGroupWS(String username, String password, int officeId, int taskGroupId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_deleteTaskGroup);
        String result = "";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_taskGroupId), taskGroupId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = response.toString();
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static int invokeAddTaskWS(String username, String password, int officeId, String name, int taskGroupId, int price) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_addTask);
        int result = 0;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_name), name);
        request.addProperty(Util.getStringWS(R.string.ws_taskGroupId), taskGroupId);
        request.addProperty(Util.getStringWS(R.string.ws_price), price);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Integer.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static String invokeUpdateTaskNameWS(String username, String password, int officeId, int taskId, String taskName) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_updateTaskName);
        String result = "";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_taskId), taskId);
        request.addProperty(Util.getStringWS(R.string.ws_taskName), taskName);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = response.toString();
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static String invokeUpdateTaskPriceWS(String username, String password, int officeId, int taskId, int price) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_updateTaskPrice);
        String result = "";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_taskId), taskId);
        request.addProperty(Util.getStringWS(R.string.ws_price), price);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = response.toString();
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }


    public static String invokeDeleteTaskWS(String username, String password, int officeId, int taskId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_deleteTask);
        String result = "";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_taskId), taskId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = response.toString();
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }


    public static User invokeAddSecretaryWS(String username, String password, int officeId, String secretary_username) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_addSecretaryToOffice2);
        User user = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_secretary), secretary_username);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            if (Integer.valueOf(response.getProperty(Util.getStringWS(R.string.ws_role)).toString()) != 0) {
                user = new User();
                user.setFirstName(response.getProperty(Util.getStringWS(R.string.ws_name)).toString());
                user.setLastName(response.getProperty(Util.getStringWS(R.string.ws_lastname)).toString());
                user.setUserName(response.getProperty(Util.getStringWS(R.string.ws_username)).toString());
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return user;
    }

    public static boolean invokeRemoveSecretaryWS(String username, String password, int officeId, String secretary_username) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_removeSecretaryFromOffice);
        boolean result = false;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_secretary), secretary_username);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Boolean.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static ArrayList<User> invokeGetSecretaryInfoWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getSecretaryInfo);
        User secretary = null;
        ArrayList<User> secretary_list = new ArrayList<User>();
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) response.getProperty(i);
                if (obj != null) {
                    secretary = new User();
                    secretary.setFirstName(obj.getProperty(Util.getStringWS(R.string.ws_name)).toString());
                    secretary.setLastName(obj.getProperty(Util.getStringWS(R.string.ws_lastname)).toString());
                    secretary.setUserName(obj.getProperty(Util.getStringWS(R.string.ws_username)).toString());

                    secretary_list.add(secretary);
                }
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return secretary_list;
    }

    public static ArrayList<PhotoDesc> invokeGetPhotoDescsWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getAllGalleryPicId2);
        PhotoDesc photo = null;
        ArrayList<PhotoDesc> photos = new ArrayList<PhotoDesc>();
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) response.getProperty(i);
                if (obj != null) {
                    photo = new PhotoDesc();
                    photo.setId(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                    photo.setPhoto(null);
                    photo.setDescription(obj.getProperty(Util.getStringWS(R.string.ws_description)).toString());
                    photo.setDate(obj.getProperty(Util.getStringWS(R.string.ws_date)).toString());
                    photos.add(photo);
                }
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return photos;
    }

    public static ArrayList<Subject> invokeGetTicketSubjectWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getUserTicketSubject);
        Subject subject = null;
        ArrayList<Subject> subjects = new ArrayList<Subject>();
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
//        request.addProperty("officeId", officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) response.getProperty(i);
                if (obj != null) {
                    subject = new Subject();
                    subject.setId(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                    subject.setSubject(obj.getProperty(Util.getStringWS(R.string.ws_subject)).toString());
                    subjects.add(subject);
                }
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return subjects;
    }

    public static ArrayList<Ticket> invokeGetTicketWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getUserTicket);
        Ticket ticket = null;
        ArrayList<Ticket> tickets = new ArrayList<Ticket>();
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
//        request.addProperty("officeId", officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response_test = (SoapObject) envelope.getResponse();
            SoapObject response = (SoapObject) envelope.bodyIn;
            if (response_test != null) {
                for (int i = 0; i < response.getPropertyCount(); i++) {
                    SoapObject obj = (SoapObject) response.getProperty(i);
                    if (obj != null) {
                        ticket = new Ticket();
                        ticket.setId(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                        ticket.setUser_id(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_userId)).toString()));
                        ticket.setSubject_id(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_subjectId)).toString()));
                        ticket.setSubject(String.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_subject)).toString()));
                        ticket.setTopic(String.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_topic))));
                        ticket.setPriority(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_priority)).toString()));
                        ticket.setStart_date(String.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_startDate))));
                        ticket.setEnd_date(String.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_endDate))));
                        tickets.add(ticket);
                    }
                }
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(ex.getMessage());
        }
        return tickets;
    }

    public static boolean isNullBodyIn(SoapObject response) {
        boolean res = false;
        try {
            SoapPrimitive obj = (SoapPrimitive) response.getProperty(0);
            res = (obj == null);
        } catch (Exception ex) {
            res = true;
        }
        return res;
    }

    public static int invokeRegisterTicketWS(String username, String password, Integer officeId, Ticket ticket) throws PException {

        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }

        String webMethName = Util.getStringWS(R.string.ws_setUserTicket);
        int result = 0;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
//        request.addProperty("officeId", officeId);

        request.addProperty(Util.getStringWS(R.string.ws_subject), ticket.getSubject_id());
        request.addProperty(Util.getStringWS(R.string.ws_topic), ticket.getTopic());
        request.addProperty(Util.getStringWS(R.string.ws_priority), ticket.getPriority());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Integer.parseInt(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static String invokeSetUserTicketMessageWS(String username, String password,
                                                      Integer officeId, int ticketId, String message) throws PException {

        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }

        String webMethName = Util.getStringWS(R.string.ws_setUserTicketMessage);
        String result = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty(Util.getStringWS(R.string.ws_ticketId), ticketId);
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
//        request.addProperty("officeId", officeId);
        request.addProperty(Util.getStringWS(R.string.ws_sendMessage), message);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = response.toString();
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static ArrayList<Message> invokeGetUserTicketMessageWS(String username, String password,
                                                                  Integer officeId, int ticketId) throws PException {

        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }

        String webMethName = Util.getStringWS(R.string.ws_getUserTicketMessage);
        String result = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        Message message;
        ArrayList<Message> messages = new ArrayList<Message>();

        request.addProperty(Util.getStringWS(R.string.ws_ticketId), ticketId);
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
//        request.addProperty("officeId", officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) response.getProperty(i);
                if (obj != null) {
                    message = new Message();
                    message.setId(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                    message.setUserId(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_userId)).toString()));
                    message.setMessage(obj.getProperty(Util.getStringWS(R.string.ws_message)).toString());
                    message.setDate(obj.getProperty(Util.getStringWS(R.string.ws_dateMessage)).toString());
                    message.setTicketId(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_ticketId)).toString()));
                    message.setUsername(obj.getProperty(Util.getStringWS(R.string.ws_username)).toString());
                    message.setFirstName(String.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_firstName))));
                    message.setLastName(String.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_lastName))));
                    messages.add(message);
                }
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return messages;
    }

    public static int invokeSetQuestionWS(String username, String password, int officeId, String label, int replyType) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_setQuestion);
        int result = 0;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_label), label);
        request.addProperty(Util.getStringWS(R.string.ws_replyType), replyType);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Integer.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static boolean invokeDeleteQuestionWS(String username, String password, int officeId, int questionId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_deleteQuestion);
        boolean result = false;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_questionId), questionId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Boolean.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }


    public static ArrayList<Question> invokeGetQuestionsWS(String username, String password, Integer officeId) throws PException {

        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }

        String webMethName = Util.getStringWS(R.string.ws_getQuestion);
        String result = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        Question question;
        ArrayList<Question> questions = new ArrayList<Question>();

        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) response.getProperty(i);
                if (obj != null) {
                    question = new Question();
                    question.setId(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                    question.setLabel(obj.getProperty(Util.getStringWS(R.string.ws_label)).toString());
                    question.setReplyType(Integer.parseInt(obj.getProperty(Util.getStringWS(R.string.ws_replyType)).toString()));
                    questions.add(question);
                }
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return questions;
    }

    public static boolean invokeSetReplyBatchWS(String username, String password, int officeId, int[] questionId, String[] reply) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_setReplyBatch);
        boolean result = false;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);

        SoapObject questionIds = new SoapObject(NAMESPACE, Util.getStringWS(R.string.ws_questionId));
        for (int i = 0; i < questionId.length; i++)
            questionIds.addProperty(Util.getStringWS(R.string.ws_int), questionId[i]);
        request.addProperty(Util.getStringWS(R.string.ws_questionId), questionIds);

        SoapObject replies = new SoapObject(NAMESPACE, Util.getStringWS(R.string.ws_reply));
        for (int i = 0; i < reply.length; i++)
            replies.addProperty(Util.getStringWS(R.string.ws_string), reply[i]);
        request.addProperty(Util.getStringWS(R.string.ws_reply), replies);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Boolean.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static boolean invokeSetReplyBatchForUserWS(String username, String password, int officeId, String patientUsername, int[] questionId, String[] reply) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_setReplyBatchForUser);
        boolean result = false;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_patientUserName), patientUsername);

        SoapObject questionIds = new SoapObject(NAMESPACE, Util.getStringWS(R.string.ws_questionId));
        for (int i = 0; i < questionId.length; i++)
            questionIds.addProperty(Util.getStringWS(R.string.ws_int), questionId[i]);
        request.addProperty(Util.getStringWS(R.string.ws_questionId), questionIds);

        SoapObject replies = new SoapObject(NAMESPACE, Util.getStringWS(R.string.ws_reply));
        for (int i = 0; i < reply.length; i++)
            replies.addProperty(Util.getStringWS(R.string.ws_string), reply[i]);
        request.addProperty(Util.getStringWS(R.string.ws_reply), replies);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Boolean.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static ArrayList<Reply> invokeGetReplyWS(String username, String password, int officeId, String patientUsername) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getReply);
        Reply reply = null;
        ArrayList<Reply> replies = new ArrayList<Reply>();
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);
        request.addProperty(Util.getStringWS(R.string.ws_patientUserName), patientUsername);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject obj = (SoapObject) response.getProperty(i);
                if (obj != null) {
                    reply = new Reply();
                    reply.setId(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_id)).toString()));
                    reply.setUserId(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_userId)).toString()));
                    reply.setQuestionId(Integer.valueOf(obj.getProperty(Util.getStringWS(R.string.ws_questionId)).toString()));
                    reply.setReply(obj.getProperty(Util.getStringWS(R.string.ws_reply)).toString());
                    replies.add(reply);
                }
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return replies;
    }

    public static String invokeAddOfficeForUserWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_addOfficeForUser);
        String result = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = String.valueOf(response.toString());

        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            if (result == null) {
                throw new PException(nothingFromServer);
            } else {
                throw new PException(otherMessage);
            }
        }
        return result;
    }

    public static String invokeDeleteOfficeForUserWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_deleteOfficeForUser);
        String result = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = String.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static int invokeGetRoleInOfficeWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_getRoleInOffice);
        int result = 0;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_officeId), officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            result = Integer.valueOf(response.toString());
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return result;
    }

    public static PaymentInfo getRequestNumber(String username, String password, int amount) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = Util.getStringWS(R.string.ws_setResNum);
        int result = 0;
        PaymentInfo paymentInfo = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty(Util.getStringWS(R.string.ws_username), username);
        request.addProperty(Util.getStringWS(R.string.ws_password), password);
        request.addProperty(Util.getStringWS(R.string.ws_amount1), amount);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            paymentInfo = new PaymentInfo();
            paymentInfo.setUrl(response.getProperty(Util.getStringWS(R.string.ws_payUrl)).toString());
            paymentInfo.setRedirecturl(response.getProperty(Util.getStringWS(R.string.ws_redirectUrl)).toString());
            paymentInfo.setMid(Integer.valueOf(response.getProperty(Util.getStringWS(R.string.ws_mid)).toString()));
            paymentInfo.setResNum(Integer.parseInt(response.getProperty(Util.getStringWS(R.string.ws_resNum)).toString()));
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }

        return paymentInfo;
    }
}