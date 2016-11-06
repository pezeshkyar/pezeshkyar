package com.example.doctorsbuilding.nav.Web;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.doctorsbuilding.nav.Dr.Clinic.Office;
import com.example.doctorsbuilding.nav.Expert;
import com.example.doctorsbuilding.nav.G;
import com.example.doctorsbuilding.nav.MessageInfo;
import com.example.doctorsbuilding.nav.PException;
import com.example.doctorsbuilding.nav.PatientFile;
import com.example.doctorsbuilding.nav.PatientInfo;
import com.example.doctorsbuilding.nav.PhotoDesc;
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
import java.util.Objects;
import java.util.TreeMap;

public class WebService {
    //Namespace of the Webservice - can be found in WSDL
    private static String NAMESPACE = "http://docTurn/";
    //Webservice URL - WSDL File location
//    private static String URL = "http://185.129.168.135:8080/pezeshkyar/services/Webservices?wsdl";
            private static String URL = "http://185.129.168.135:8080/pezeshkyarServerAllInOne/services/Webservices?wsdl";
    //SOAP Action URI again Namespace + Web method name
    private static String SOAP_ACTION = "http://docTurn/";

    private static final String connectMessage = "برقراری ارتباط با سرور امکان پذیر نیست !";
    private static final String otherMessage = "خطایی در ارتباط با سرور رخ داده است !";
    private static final String isOnlineMessage = "دسترسی به اینترنت امکان پذیر نمی باشد، لطفا تنظیمات اینترنت خود را چک نمایید .";


    public static ArrayList<State> invokeGetProvinceNameWS() throws PException {

        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }

        String webMethName = "getProvince";
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
                state.SetStateID(Integer.parseInt(obj.getProperty("id").toString()));
                state.SetStateName(obj.getProperty("name").toString());
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

        String webMethName = "getCityOfProvince";
        ArrayList<City> result = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        PropertyInfo property = new PropertyInfo();
        property.setName("provinceID");
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
                city.SetCityID(Integer.parseInt(obj.getProperty("id").toString()));
                city.SetStateID(Integer.parseInt(obj.getProperty("provinceId").toString()));
                city.SetCityName(obj.getProperty("name").toString());
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

        String webMethName = "register";
        String result = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("name", user.getFirstName());
        request.addProperty("lastname", user.getLastName());
        request.addProperty("mobileno", user.getPhone());
        request.addProperty("username", user.getUserName());
        request.addProperty("password", user.getPassword());
        request.addProperty("cityid", user.getCityID());
        byte[] picbytes = getBytes(user.getImgProfile());
        request.addProperty("pic", Base64.encodeToString(picbytes, Base64.DEFAULT));
        request.addProperty("email", user.getEmail());
        request.addProperty("officeId", G.officeId);

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
        String webMethName = "updateUserInfo3";
        String result = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", G.officeId);
        request.addProperty("name", user.getFirstName());
        request.addProperty("lastname", user.getLastName());
        request.addProperty("mobileno", user.getPhone());
        request.addProperty("cityid", user.getCityID());
        request.addProperty("newPassword", user.getPassword());
        request.addProperty("email", user.getEmail());

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

    public static int invokeLoginWS(int officeId, User user) throws PException {

        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }

        String webMethName = "login2";
        int result = -1;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", user.getUserName());
        request.addProperty("password", user.getPassword());
        request.addProperty("officeId", officeId);

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
        String webMethName = "getUserInfoWithoutPic";
        User user = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            user = new User();
            user.setFirstName(response.getProperty("name").toString());
            user.setLastName(response.getProperty("lastname").toString());
            user.setPhone(response.getProperty("mobileno").toString());
            user.setUserName(response.getProperty("username").toString());
            user.setRole(Integer.parseInt(response.getProperty("role").toString()));
            user.setCityID(Integer.parseInt(response.getProperty("cityid").toString()));
            user.setCityName(response.getProperty("city").toString());
            user.setStateID(Integer.parseInt(response.getProperty("provinceid").toString()));
            user.setStateName(response.getProperty("province").toString());

//            try {
//                String pic = response.getProperty("pic").toString();
//                byte[] imgbytes = Base64.decode(pic, Base64.DEFAULT);
//                user.setImgProfile(BitmapFactory.decodeByteArray(imgbytes, 0, imgbytes.length));
//            } catch (Exception e) {
//                user.setImgProfile(null);
//            }
            user.setPassword(password);
            try {
                if (response.getProperty("email").toString().equals("anyType{}"))
                    user.setEmail("");
                else
                    user.setEmail(response.getProperty("email").toString());

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
        String webMethName = "getOfficeInfo2";
        Office office = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
//        request.addProperty("username", username);
//        request.addProperty("password", password);
        request.addProperty("officeId", officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            office = new Office();
            office.setId(Integer.parseInt(response.getProperty("id").toString()));
            office.setDrUsername(response.getProperty("doctorUsername").toString());
            office.setFirstname(response.getProperty("doctorName").toString());
            office.setLastname(response.getProperty("doctorLastName").toString());
            office.setCityId(Integer.parseInt(response.getProperty("cityId").toString()));
            office.setCityName(response.getProperty("city").toString());
            office.setStateId(Integer.parseInt(response.getProperty("provinceId").toString()));
            office.setStateName(response.getProperty("province").toString());
            office.setExpertId(Integer.parseInt(response.getProperty("specId").toString()));
            office.setExpertName(response.getProperty("spec").toString());
            office.setSubExpertId(Integer.parseInt(response.getProperty("subspecId").toString()));
            office.setSubExpertName(response.getProperty("subSpec").toString());
            office.setAddress(response.getProperty("address").toString());
            office.setPhone(response.getProperty("tellNo").toString());
            office.setLatitude(Double.parseDouble(response.getProperty("latitude").toString()));
            office.setLongitude(Double.parseDouble(response.getProperty("longitude").toString()));
            try {
                office.setBiography(response.getProperty("biograophy").toString());
            } catch (Exception e) {
                office.setBiography("");
            }
            office.setTimeQuantum(Integer.parseInt(response.getProperty("timeQuantum").toString()));
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return office;
    }

    public static Bitmap invokeGetDoctorPicWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = "getDoctorPic";
        Bitmap pic = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);

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
        String webMethName = "getUserPic";
        Bitmap pic = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", G.officeId);

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

    public static boolean invokeUpdateDoctorPicWS(String username, String password, Bitmap pic) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = "updateUserPic2";
        boolean result = false;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", G.officeId);
        byte[] img = getBytes(pic);
        request.addProperty("pic", Base64.encodeToString(img, Base64.DEFAULT));


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
        String webMethName = "updateOfficeLocation";
        boolean result = false;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("latitude", String.valueOf(latitude));
        request.addProperty("longitude", String.valueOf(longitude));

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
        String webMethName = "getSpec";
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
                expert.setId(Integer.parseInt(obj.getProperty("id").toString()));
                expert.setName(obj.getProperty("name").toString());
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
        String webMethName = "getSubSpec";
        SubExpert subExpert;
        ArrayList<SubExpert> subExperts = new ArrayList<SubExpert>();
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("specId", specId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {
                subExpert = new SubExpert();
                SoapObject obj = (SoapObject) response.getProperty(i);
                subExpert.setId(Integer.parseInt(obj.getProperty("id").toString()));
                subExpert.setExpertId(Integer.parseInt(obj.getProperty("specId").toString()));
                subExpert.setName(obj.getProperty("name").toString());
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
        String webMethName = "updateOfficeInfo";
        boolean result = false;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", office.getId());
        request.addProperty("cityId", office.getCityId());
        request.addProperty("spec", office.getExpertId());
        request.addProperty("subSpec", office.getSubExpertId());
        request.addProperty("address", office.getAddress());
        request.addProperty("tellNo", office.getPhone());
        request.addProperty("biography", office.getBiography());

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
        String webMethName = "getAllTurnFromToday";

        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;

            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject object = (SoapObject) response.getProperty(i);
                turn = new Turn();
                turn.setId(Integer.parseInt(object.getProperty("id").toString()));
                turn.setDate(object.getProperty("date").toString());
                turn.setLongDate(object.getProperty("longDate").toString());
                turn.setHour(Integer.parseInt(object.getProperty("hour").toString()));
                turn.setMin(Integer.parseInt(object.getProperty("min").toString()));
                turn.setCapacity(Integer.parseInt(object.getProperty("capacity").toString()));
                turn.setDuration(Integer.parseInt(object.getProperty("duration").toString()));
                turn.setOfficeId(Integer.parseInt(object.getProperty("officeId").toString()));
                turn.setReserved(Integer.parseInt(object.getProperty("reserved").toString()));
                turn.setIsReserved(Boolean.valueOf(object.getProperty("isReserved").toString()));
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
        String webMethName = "reserveForUser";
        int result = 0;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("turnId", reservation.getTurnId());
        request.addProperty("firstReservationId", reservation.getFirstReservationId());
        request.addProperty("taskId", reservation.getTaskId());
        request.addProperty("numberOfTurns", reservation.getNumberOfTurns());
        request.addProperty("patientUserName", reservation.getPatientUserName());

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

    public static boolean invokeCancleReservation(String username, String password, int reservationId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = "cancelReservation";
        boolean result = false;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", G.officeId);
        request.addProperty("reservationId", reservationId);

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

    public static ArrayList<User> invokeSearchUserWS(String username, String name, String lastName, String mobileNo) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        User user = null;
        ArrayList<User> users = new ArrayList<User>();
        String webMethName = "searchUser";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("name", name);
        request.addProperty("lastName", lastName);
        request.addProperty("mobileNo", mobileNo);
        request.addProperty("officeId", G.officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {

                SoapObject obj = (SoapObject) response.getProperty(i);
                user = new User();
                user.setUserName(obj.getProperty("username").toString());
                user.setFirstName(obj.getProperty("name").toString());
                user.setLastName(obj.getProperty("lastname").toString());
                user.setPhone(obj.getProperty("mobileno").toString());
                user.setCityName(obj.getProperty("city").toString());
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
        String webMethName = "getReservationByTurnId";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("turnId", turnId);

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
                    reservation.setId(Integer.parseInt(obj.getProperty("id").toString()));
                    reservation.setUsername(obj.getProperty("username").toString());
                    reservation.setTurnId(Integer.parseInt(obj.getProperty("turnId").toString()));
                    reservation.setTaskId(Integer.parseInt(obj.getProperty("taskId").toString()));
                    reservation.setPatientFirstName(obj.getProperty("patientFirstName").toString());
                    reservation.setPatientLastName(obj.getProperty("patientLastName").toString());
                    reservation.setFirstReservationId(Integer.parseInt(obj.getProperty("firstReservationId").toString()));
                    reservation.setPayment(Integer.parseInt(obj.getProperty("payment").toString()));
                    reservation.setNumberOfTurns(Integer.parseInt(obj.getProperty("numberOfTurns").toString()));
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

    public static ArrayList<Reservation> invokeGetReservationByDateWS(String username, String password, int officeId
            , String fromDate, String toDate) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        Reservation reservation = null;
        ArrayList<Reservation> reservations = new ArrayList<Reservation>();
        String webMethName = "getReservationByDate";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("fromDate", fromDate);
        request.addProperty("toDate", toDate);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {

                SoapObject obj = (SoapObject) response.getProperty(i);
                reservation = new Reservation();
                reservation.setId(Integer.parseInt(obj.getProperty("id").toString()));
                reservation.setUsername(obj.getProperty("username").toString());
                reservation.setTurnId(Integer.parseInt(obj.getProperty("turnId").toString()));
                reservation.setTaskId(Integer.parseInt(obj.getProperty("taskId").toString()));
                reservation.setPatientFirstName(obj.getProperty("patientFirstName").toString());
                reservation.setPatientLastName(obj.getProperty("patientLastName").toString());
                reservation.setFirstReservationId(Integer.parseInt(obj.getProperty("firstResevationId").toString()));
                reservation.setPayment(Integer.parseInt(obj.getProperty("payment").toString()));
                reservation.setNumberOfTurns(Integer.parseInt(obj.getProperty("numberOfTurns").toString()));
                reservations.add(reservation);
            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }
        return reservations;
    }

    public static ArrayList<Task> invokeGetTaskWS(String username, String password, int officeId, int taskGroupId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        Task task = null;
        ArrayList<Task> taskes = new ArrayList<Task>();
        String webMethName = "getTasks";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("taskGroupId", taskGroupId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            for (int i = 0; i < response.getPropertyCount(); i++) {

                SoapObject obj = (SoapObject) response.getProperty(i);
                task = new Task();
                task.setId(Integer.parseInt(obj.getProperty("id").toString()));
                task.setOfficeId(Integer.parseInt(obj.getProperty("officeId").toString()));
                task.setName(obj.getProperty("name").toString());
                task.setPrice(Integer.parseInt(obj.getProperty("price").toString()));
                task.setGroupId(Integer.parseInt(obj.getProperty("taskGroupId").toString()));
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
        String webMethName = "getTaskGroups";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);

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
                    task.setId(Integer.parseInt(obj.getProperty("id").toString()));
                    task.setOfficeId(Integer.parseInt(obj.getProperty("officeId").toString()));
                    task.setName(obj.getProperty("name").toString());
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
        String webMethName = "reserveForGuest";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("turnId", reservation.getTurnId());
        request.addProperty("firstReservationId", reservation.getFirstReservationId());
        request.addProperty("taskId", reservation.getTaskId());
        request.addProperty("numberOfTurns", reservation.getNumberOfTurns());
        request.addProperty("patientFirstName", reservation.getPatientFirstName());
        request.addProperty("patientLastName", reservation.getPatientLastName());
        request.addProperty("patientPhoneNo", reservation.getPatientPhoneNo());
        request.addProperty("patientCityId", cityId);

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

    public static int invokeReserveForMeWS(String username, String password, Reservation reservation) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        int result = 0;
        String webMethName = "reserveForMe";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("turnId", reservation.getTurnId());
        request.addProperty("firstReservationId", reservation.getFirstReservationId());
        request.addProperty("taskId", reservation.getTaskId());
        request.addProperty("numberOfTurns", reservation.getNumberOfTurns());

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
        String webMethName = "addTurnByDate";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("fromDate", fromDate);
        request.addProperty("toDate", toDate);
        request.addProperty("hour", hour);
        request.addProperty("min", min);
        request.addProperty("duration", duration);
        request.addProperty("capacity", capacity);
        request.addProperty("dayOfWeek", dayOfWeek);

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
        String webMethName = "getAllTurn";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("fromDate", fromDate);
        request.addProperty("toDate", toDate);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;

            for (int i = 0; i < response.getPropertyCount(); i++) {
                SoapObject object = (SoapObject) response.getProperty(i);
                turn = new Turn();
                turn.setId(Integer.parseInt(object.getProperty("id").toString()));
                turn.setDate(object.getProperty("date").toString());
                turn.setLongDate(object.getProperty("longDate").toString());
                turn.setHour(Integer.parseInt(object.getProperty("hour").toString()));
                turn.setMin(Integer.parseInt(object.getProperty("min").toString()));
                turn.setCapacity(Integer.parseInt(object.getProperty("capacity").toString()));
                turn.setDuration(Integer.parseInt(object.getProperty("duration").toString()));
                turn.setOfficeId(Integer.parseInt(object.getProperty("officeId").toString()));
                turn.setReserved(Integer.parseInt(object.getProperty("reserved").toString()));
                turn.setIsReserved(Boolean.valueOf(object.getProperty("isReserved").toString()));
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
        String webMethName = "removeTurn";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("turnId", turnId);

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
        String webMethName = "getPatientTurnInfoByDate";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("fromDate", fromDate);
        request.addProperty("toDate", toDate);

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
                    if (obj.getProperty("username").toString().equals("anyType")
                            || obj.getProperty("username").toString().equals(""))

                        rturn.setUsername("");
                    else
                        rturn.setUsername(obj.getProperty("username").toString());
                } catch (Exception ex) {

                    rturn.setUsername("");
                }

                try {
                    if (obj.getProperty("patientUsername").toString().equals("anyType")
                            || obj.getProperty("patientUsername").toString().equals(""))

                        rturn.setPatientUsername("");
                    else
                        rturn.setPatientUsername(obj.getProperty("patientUsername").toString());
                } catch (Exception ex) {

                    rturn.setPatientUsername("");
                }

                rturn.setPatientFirstName(obj.getProperty("patientFirstName").toString());
                rturn.setPatientLastName(obj.getProperty("patientLastName").toString());
                rturn.setPatientPhoneNo(obj.getProperty("patientPhoneNo").toString());
                rturn.setNumberOfTurns(Integer.parseInt(obj.getProperty("numberOfTurns").toString()));
                rturn.setShortDate(obj.getProperty("shortDate").toString());
                rturn.setLongDate(obj.getProperty("longDate").toString());
                rturn.setHour(Integer.parseInt(obj.getProperty("hour").toString()));
                rturn.setMin(Integer.parseInt(obj.getProperty("min").toString()));
                rturn.setDuration(Integer.parseInt(obj.getProperty("duration").toString()));
                rturn.setCapacity(Integer.parseInt(obj.getProperty("capacity").toString()));
                rturn.setReserved(Integer.parseInt(obj.getProperty("reserved").toString()));
                rturn.setReservationId(Integer.parseInt(obj.getProperty("reservationId").toString()));
                rturn.setTurnId(Integer.parseInt(obj.getProperty("turnId").toString()));
                rturn.setOfficeId(Integer.parseInt(obj.getProperty("officeId").toString()));
                rturn.setTaskId(Integer.parseInt(obj.getProperty("taskId").toString()));
                rturn.setTaskName(obj.getProperty("taskName").toString());
                rturn.setPrice(Integer.parseInt(obj.getProperty("price").toString()));
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
        String webMethName = "sendMessageBatch";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo receiverProperty = null;
        StringArraySerializer stringArrayUsername = new StringArraySerializer();
        for (int i = 0; i < receivers.size(); i++) {
            stringArrayUsername.add(receivers.get(i));
            receiverProperty = new PropertyInfo();
            receiverProperty.setName("receivers");
            receiverProperty.setValue(stringArrayUsername);
            receiverProperty.setType(stringArrayUsername.getClass());
            receiverProperty.setNamespace(NAMESPACE);
        }

        PropertyInfo phoneNoProperty = null;
        StringArraySerializer stringArrayPhoneNo = new StringArraySerializer();
        for (int i = 0; i < phoneNos.size(); i++) {
            stringArrayPhoneNo.add(phoneNos.get(i));
            phoneNoProperty = new PropertyInfo();
            phoneNoProperty.setName("phoneNo");
            phoneNoProperty.setValue(stringArrayPhoneNo);
            phoneNoProperty.setType(stringArrayPhoneNo.getClass());
            phoneNoProperty.setNamespace(NAMESPACE);
        }
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty(receiverProperty);
        request.addProperty(phoneNoProperty);
        request.addProperty("subject", subject);
        request.addProperty("message", message);

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
        String webMethName = "getUnreadMessages";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);

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
                messageInfo.setId(Integer.parseInt(obj.getProperty("id").toString()));
                messageInfo.setSenderUsername(obj.getProperty("senderUsername").toString());
                messageInfo.setSenderFirstName(obj.getProperty("senderFirstName").toString());
                messageInfo.setSenderLastName(obj.getProperty("senderLastName").toString());
                messageInfo.setSubject(obj.getProperty("subject").toString());
                messageInfo.setMessage(obj.getProperty("message").toString());
                messageInfo.setDate(obj.getProperty("date").toString());
                messageInfo.setTime(obj.getProperty("time").toString());
                result.add(messageInfo);

            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }

        return result;
    }

    public static ArrayList<MessageInfo> invokeGetAllMessagesWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        MessageInfo messageInfo = null;
        ArrayList<MessageInfo> result = null;
        String webMethName = "getAllMessages";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);

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
                messageInfo.setId(Integer.parseInt(obj.getProperty("id").toString()));
                messageInfo.setSenderUsername(obj.getProperty("senderUsername").toString());
                messageInfo.setSenderFirstName(obj.getProperty("senderFirstName").toString());
                messageInfo.setSenderLastName(obj.getProperty("senderLastName").toString());
                messageInfo.setSubject(obj.getProperty("subject").toString());
                messageInfo.setMessage(obj.getProperty("message").toString());
                messageInfo.setDate(obj.getProperty("date").toString());
                messageInfo.setTime(obj.getProperty("time").toString());
                result.add(messageInfo);

            }
        } catch (ConnectException ex) {
            throw new PException(connectMessage);
        } catch (Exception ex) {
            throw new PException(otherMessage);
        }

        return result;
    }

    public static void invokeSetMessageReadWS(String username, String password, int officeId, int messageId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        String webMethName = "setMessageRead";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("messageId", messageId);

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
        String webMethName = "getReservationByUser";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("count", count);
        request.addProperty("index", index);

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
                    reserve.setReservationId(Integer.parseInt(obj.getProperty("reservationId").toString()));
                    reserve.setTurnId(Integer.parseInt(obj.getProperty("turnId").toString()));
                    reserve.setTaskId(Integer.parseInt(obj.getProperty("taskId").toString()));
                    reserve.setUsername(obj.getProperty("username").toString());
                    reserve.setTaskName(obj.getProperty("taskName").toString());
                    reserve.setPatientFirstName(obj.getProperty("patientFirstName").toString());
                    reserve.setPatientLastName(obj.getProperty("patientLastName").toString());
                    reserve.setPatientPhoneNo(obj.getProperty("patientPhoneNo").toString());
                    reserve.setFirstReservationId(Integer.parseInt(obj.getProperty("firstReservationId").toString()));
                    reserve.setPayment(Integer.parseInt(obj.getProperty("payment").toString()));
                    reserve.setNumberOfTurns(Integer.parseInt(obj.getProperty("numberOfTurns").toString()));
                    reserve.setDate(obj.getProperty("date").toString());
                    reserve.setLongDate(obj.getProperty("longDate").toString());
                    reserve.setTime(obj.getProperty("time").toString());
                    reserve.setDoctorUsername(obj.getProperty("doctorUsername").toString());
                    reserve.setDoctorFirstName(obj.getProperty("doctorFirstName").toString());
                    reserve.setDoctorLastName(obj.getProperty("doctorLastName").toString());
                    reserve.setDoctorSpec(obj.getProperty("doctorSpec").toString());
                    reserve.setDoctorSubSpec(obj.getProperty("doctorSubSpec").toString());
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
        String webMethName = "removeMessage";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("messageId", messageId);

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
        String webMethName = "reception";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("reservationId", reservationId);
        request.addProperty("payment", payment);
        request.addProperty("description", description);

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
        String webMethName = "getTodayPatient";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);

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
                        patientInfo.setFirstName(obj.getProperty("firstName").toString());
                        patientInfo.setLastName(obj.getProperty("lastName").toString());
                        patientInfo.setMobileNo(obj.getProperty("mobileNo").toString());
                        try {
                            patientInfo.setUsername(obj.getProperty("username").toString());
                        } catch (Exception ex) {
                            patientInfo.setUsername("");
                        }
                        patientInfo.setReservationId(Integer.parseInt(obj.getProperty("reservationId").toString()));
                        patientInfo.setFirstReservationId(Integer.parseInt(obj.getProperty("firstReservationId").toString()));
                        patientInfo.setTaskId(Integer.parseInt(obj.getProperty("taskId").toString()));
                        patientInfo.setTaskName(obj.getProperty("taskName").toString());
                        patientInfo.setPayment(Integer.valueOf(obj.getProperty("payment").toString()));
                        try {
                            patientInfo.setDescription(obj.getProperty("description").toString());
                        } catch (Exception ex) {
                            patientInfo.setDescription("");
                        }
                        patientInfo.setTaskGroupId(Integer.valueOf(obj.getProperty("taskGroupId").toString()));
                        patientInfo.setTaskGroupName(obj.getProperty("taskGroupName").toString());
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
        String webMethName = "getPatientFile";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("patientUsername", patientUsername);

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
                        patientFile.setReservationId(Integer.valueOf(obj.getProperty("reservationId").toString()));
                        patientFile.setFirstReservationId(Integer.valueOf(obj.getProperty("firstReservationId").toString()));
                        patientFile.setDate(obj.getProperty("date").toString());
                        patientFile.setLongDate(obj.getProperty("longDate").toString());
                        patientFile.setTime(obj.getProperty("time").toString());
                        patientFile.setTaskId(Integer.valueOf(obj.getProperty("taskId").toString()));
                        patientFile.setTaskName(obj.getProperty("taskName").toString());
                        try {
                            patientFile.setDescription(obj.getProperty("description").toString());
                        } catch (Exception ex) {
                            patientFile.setDescription("");
                        }
                        patientFile.setPrice(Integer.parseInt(obj.getProperty("price").toString()));
                        patientFile.setPayment(Integer.parseInt(obj.getProperty("payment").toString()));
                        patientFile.setTotalPayment(Integer.parseInt(obj.getProperty("totalPayment").toString()));
                        patientFile.setRemain(Integer.valueOf(obj.getProperty("remain").toString()));
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

    public static ArrayList<Integer> invokegetAllGalleyPicIdWS(String username, String password, int officeId) throws PException {
        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }
        ArrayList<Integer> imageIds = null;
        String webMethName = "getAllGalleyPicId";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
            if (response != null) {
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
        String webMethName = "getGalleryPic";
        PhotoDesc photoDesc = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("picId", picId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);
        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            photoDesc = new PhotoDesc();
            photoDesc.setId(Integer.parseInt(response.getProperty("id").toString()));
            String pic = response.getProperty("photo").toString();
            byte[] imgbytes = Base64.decode(pic, Base64.DEFAULT);
            photoDesc.setPhoto(BitmapFactory.decodeByteArray(imgbytes, 0, imgbytes.length));
            if (response.getProperty("description").toString().equals("anyType{}"))
                photoDesc.setDescription("");
            else
                photoDesc.setDescription(response.getProperty("description").toString());

            photoDesc.setDate(response.getProperty("date").toString());

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
        String webMethName = "setGalleryPic";
        int id = -1;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        byte[] img = getBytes(pic);
        request.addProperty("pic", Base64.encodeToString(img, Base64.DEFAULT));
        request.addProperty("description", description);

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
        String webMethName = "deleteFromGallery";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("picId", picId);

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
        String webMethName = "changeGalleryPicDescription";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("picId", picId);
        request.addProperty("description", description);

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
        String webMethName = "addTaskGroup";
        int result = 0;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("taskGroupName", taskNameGroup);

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
        String webMethName = "updateTaskGroup";
        String result = "";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("taskGroupId", taskGroupId);
        request.addProperty("taskGroupName", taskNameGroup);

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
        String webMethName = "deleteTaskGroup";
        String result = "";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("taskGroupId", taskGroupId);

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
        String webMethName = "addTask";
        int result = 0;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("name", name);
        request.addProperty("taskGroupId", taskGroupId);
        request.addProperty("price", price);

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
        String webMethName = "updateTaskName";
        String result = "";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("taskId", taskId);
        request.addProperty("taskName", taskName);

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
        String webMethName = "updateTaskPrice";
        String result = "";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("taskId", taskId);
        request.addProperty("price", price);

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
        String webMethName = "deleteTask";
        String result = "";
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("taskId", taskId);

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
        String webMethName = "addSecretaryToOffice2";
        User user = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("secretary", secretary_username);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            if (Integer.valueOf(response.getProperty("role").toString()) != 0) {
                user = new User();
                user.setFirstName(response.getProperty("name").toString());
                user.setLastName(response.getProperty("lastname").toString());
                user.setUserName(response.getProperty("username").toString());
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
        String webMethName = "removeSecretaryFromOffice";
        boolean result = false;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("secretary", secretary_username);

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
        String webMethName = "getSecretaryInfo";
        User secretary = null;
        ArrayList<User> secretary_list = new ArrayList<User>();
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);

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
                    secretary.setFirstName(obj.getProperty("name").toString());
                    secretary.setLastName(obj.getProperty("lastname").toString());
                    secretary.setUserName(obj.getProperty("username").toString());

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
        String webMethName = "getAllGalleyPicId2";
        PhotoDesc photo = null;
        ArrayList<PhotoDesc> photos = new ArrayList<PhotoDesc>();
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);

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
                    photo.setId(Integer.valueOf(obj.getProperty("id").toString()));
                    photo.setPhoto(null);
                    photo.setDescription(obj.getProperty("description").toString());
                    photo.setDate(obj.getProperty("date").toString());
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
        String webMethName = "getUserTicketSubject";
        Subject subject = null;
        ArrayList<Subject> subjects = new ArrayList<Subject>();
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);

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
                    subject.setId(Integer.valueOf(obj.getProperty("id").toString()));
                    subject.setSubject(obj.getProperty("subject").toString());
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
        String webMethName = "getUserTicket";
        Ticket ticket = null;
        ArrayList<Ticket> tickets = new ArrayList<Ticket>();
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        PropertyInfo property = new PropertyInfo();
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransportSE = new HttpTransportSE(URL);

        try {
            androidHttpTransportSE.call(SOAP_ACTION + webMethName, envelope);
            SoapObject response = (SoapObject) envelope.bodyIn;
           if(isNullBodyIn(response)) {
               for (int i = 0; i < response.getPropertyCount(); i++) {
                   SoapObject obj = (SoapObject) response.getProperty(i);
                   if (obj != null) {
                       ticket = new Ticket();
                       ticket.setId(Integer.valueOf(obj.getProperty("id").toString()));
                       ticket.setUser_id(Integer.valueOf(obj.getProperty("userId").toString()));
                       ticket.setSubject_id(Integer.valueOf(obj.getProperty("subjectId").toString()));
                       ticket.setSubject(String.valueOf(obj.getProperty("subject").toString()));
                       ticket.setTopic(String.valueOf(obj.getProperty("topic")));
                       ticket.setPriority(Integer.valueOf(obj.getProperty("priority").toString()));
                       ticket.setStart_date(String.valueOf(obj.getProperty("startDate")));
                       ticket.setEnd_date(String.valueOf(obj.getProperty("endDate")));
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

    public static boolean isNullBodyIn(SoapObject response){
        boolean res= true;
        int count = 0;
        try {
            count = response.getPropertyCount();
        }catch (Exception ex){
            res = false;
        }
        return res;
    }

    public static int invokeRegisterTicketWS(String username, String password, Integer officeId, Ticket ticket) throws PException {

        if (!G.isOnline()) {
            throw new PException(isOnlineMessage);
        }

        String webMethName = "setUserTicket";
        int result = 0;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);

        request.addProperty("subject", ticket.getSubject_id());
        request.addProperty("topic", ticket.getTopic());
        request.addProperty("priority", ticket.getPriority());

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

        String webMethName = "setUserTicketMessage";
        String result = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);

        request.addProperty("ticketId", ticketId);
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);
        request.addProperty("sendMessage", message);

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

        String webMethName = "getUserTicketMessage";
        String result = null;
        SoapObject request = new SoapObject(NAMESPACE, webMethName);
        Message message;
        ArrayList<Message> messages = new ArrayList<Message>();

        request.addProperty("ticketId", ticketId);
        request.addProperty("username", username);
        request.addProperty("password", password);
        request.addProperty("officeId", officeId);

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
                    message.setId(Integer.valueOf(obj.getProperty("id").toString()));
                    message.setUserId(Integer.valueOf(obj.getProperty("userId").toString()));
                    message.setMessage(obj.getProperty("message").toString());
                    message.setDate(obj.getProperty("dateMessage").toString());
                    message.setTicketId(Integer.valueOf(obj.getProperty("ticketId").toString()));
                    message.setUsername(obj.getProperty("username").toString());
                    message.setFirstName(String.valueOf(obj.getProperty("firstName")));
                    message.setLastName(String.valueOf(obj.getProperty("lastName")));
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
}