package com.ruoyi.quartz.service.impl;


import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.reflect.ReflectUtils;
import com.ruoyi.quartz.domain.HkSourceFieldConfig;
import com.ruoyi.quartz.domain.Source;
import com.ruoyi.quartz.domain.SysJob;
//import com.ruoyi.quartz.mapper.HkEventInfoMapper;
import com.ruoyi.quartz.mapper.HkSourceFieldConfigMapper;
import com.ruoyi.quartz.mapper.HkSourceFieldFilterMapper;
import com.ruoyi.quartz.mapper.SourceMapper;
import com.ruoyi.quartz.mapper.SysJobMapper;
import com.ruoyi.quartz.service.SourceService;
import com.ruoyi.quartz.util.CronUtils;
import com.ruoyi.quartz.util.DBHelper;
import com.ruoyi.system.domain.HkEarlyWarning.HkEventInfo;
import com.ruoyi.system.mapper.HkEventMapper;


/**
 * @program: data-ruoyi
 * @description:
 * @author: Yang-Teng-Fei
 * @create: 2020-11-02 15:44
 **/
@Service
public class SourceServiceImpl implements SourceService {

    @Autowired
    private SourceMapper SourceMapper;
    @Autowired
    private HkEventMapper eventInfoMapper;
    @Autowired
    private SysJobMapper jobMapper;
//    @Autowired
//    private HkEventInfoMapper hkEventInfoMapper;
    @Autowired
    private HkSourceFieldConfigMapper fieldConfigMapper;
    @Autowired
    private HkSourceFieldFilterMapper filterMapper;

    private static Logger logger = LogManager.getLogger(SourceServiceImpl.class);

    /**
     * ??????api??????
     * @param params
     * @return
     */
    @Override
    public String findHkAddress(String params) {
      Source source=SourceMapper.findHkAddress(params);
        boolean flag=false;
      // ??????????????????
        String sourceAccess=source.getSourceAccess();
        if(sourceAccess.equals("API")){
        	//TODO ??????API??????
        	//TODO ??????????????????
        	//TODO ?????????????????????????????????.??????.????????????????????????????????????????????????.??????.???????????????????????????
            return "";
        }else {
        	Connection conn = null;
        	Statement stmt = null;
        	try{
        		conn = DBHelper.getConnection(source);
        		stmt = conn.createStatement();
        		HkSourceFieldConfig hkSourceFieldConfig = new HkSourceFieldConfig();
        		hkSourceFieldConfig.setSourceId(source.getId());
        		List<HkSourceFieldConfig> fieldConfig = fieldConfigMapper.selectHkSourceFieldConfigList(hkSourceFieldConfig);
        		if(null != fieldConfig && !fieldConfig.isEmpty()){
        			String sql = "select top(10) from "+source.getSourceTableName();
        			if(source.getDataType().equals(Constants.DATA_SOURCE_MYSQL)){
        				//TODO ???????????????????????????????????????
        				sql = "select top(10) from "+source.getSourceTableName();
        			}else if(source.getDataType().equals(Constants.DATA_SOURCE_ORACLE)){
        				//TODO ??????????????????????????????
        				sql = "select * from "+source.getSourceTableName() + " where PUBLISH_DATE BETWEEN to_date('2021-01-18 18:54:50','yyyy-mm-dd hh24:mi:ss') and to_date('2021-01-20 18:54:50','yyyy-mm-dd hh24:mi:ss') ";
        			}

            		ResultSet rs = stmt.executeQuery(sql);
        			while(rs.next()){
        				HkEventInfo hkEventInfo = new HkEventInfo();
	        			for(HkSourceFieldConfig configInfo:fieldConfig){
	        				String columnName = configInfo.getSourceColumnName();
	        				if(StringUtils.isNull(columnName) || columnName.equals("null")){
	        					continue;
	        				}
	        				Object val = rs.getObject(configInfo.getSourceColumnName());
	        				if(null == val || val.equals("")){
	        					//?????????????????????????????????
	        					val = configInfo.getDefaultValue();
	        				}
	        				if(val instanceof oracle.sql.CLOB){
	        					String content ="";
	        					oracle.sql.CLOB clob = (oracle.sql.CLOB)val;
	        					BufferedReader a = new BufferedReader(clob.getCharacterStream()); 
	        					String str = "";
	        					while ((str = a.readLine()) != null) {
	        					content = content.concat(str); //?????????String???????????????
	        					}
	        					val = content;
	        				}
	        				String propertyName = StringUtils.toCamelCase(configInfo.getColumnName());
	        				ReflectUtils.invokeSetter(hkEventInfo, propertyName, val);
	        			}
	        			eventInfoMapper.addHKEventInfo(hkEventInfo);
        			}
        		}
        	}catch(Exception e){
        		logger.error(e.getMessage());
        	}finally {
				if(null != conn){
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if(null != stmt){
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
        	
            return sourceAccess;
        }
    }
    /**
     * ?????????????????????
     * @param source
     * @return
     */
    @Override
    public List<Source> querySourceInfo(Source source) {
        return SourceMapper.querySourceInfo(source);
    }
    /**
     * ?????????????????????
     * @param source
     * @return
     */
    @Override
    public int addHkSource(Source source) {
    	String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        source.setUuid(uuid);
    	updateJob(source,null);
        return SourceMapper.addHkSource(source);
    }
    
    private void updateJob(Source source,Long jobId){
   	 // ????????????????????????????????????
       SysJob sysJob=new SysJob();
       String sourcAccess="ryTask.sqlParams('"+source.getSourceName()+"')";
       sysJob.setInvokeTarget(sourcAccess);
       sysJob.setJobName(source.getSourceName());
       sysJob.setSourceId(source.getUuid());
   	
       sysJob.setInvokeTarget(sourcAccess);
       // ??????cron?????????
       // ????????????
       String sourceSynchronize=source.getSourceSynchronize();
       // ????????????
       Integer time = source.getSourceSynRateVal();
       int rate = 4;
       if(sourceSynchronize.equals("??????")){
           rate=0;
       }else  if(sourceSynchronize.equals("?????????")){
           rate=1;
       }else if(sourceSynchronize.equals("?????????")){
           rate=2;
       }else if(sourceSynchronize.equals("?????????")){
           rate=3;
       }
       String synchronize=CronUtils.createLoopCronExpression(rate,time);
       sysJob.setCronExpression(synchronize);
       sysJob.setMisfirePolicy("1");
       sysJob.setConcurrent("1");
       if(jobId == null){
    	   jobMapper.insertJob(sysJob);
       }
       else{
    	   sysJob.setJobId(jobId);
    	   jobMapper.updateJob(sysJob);
       }
   }
    
    /**
     * ??????id?????????????????????
     * @param id
     * @return
     */
    @Override
    public Source findHkSource(Integer id) {
        return SourceMapper.findHkSource(id);
    }

    /**
     * ?????????????????????
     * @param source
     * @return
     */
    @Override
    public int updateHkSource(Source source) {
    	updateJob(source,source.getSysid());
        return SourceMapper.updateHkSource(source);
    }

    /**
     * ?????????????????????
     * @param ids
     * @return
     */
    @Override
    public int deleteHkSource(String ids) {
        int count=SourceMapper.deleteHkSource(Convert.toStrArray(ids));

        return count;
    }
    /**
     * ?????????????????????????????????
     * @param source
     * @return
     */
    @Override
    public int findHkSourceName(Source source) {
      return   SourceMapper.findHkSourceName(source);
    }
    /**
     * ??????Mysql????????????????????????
     * @param jdbcurl
     * @param sourceUsername
     * @param sourcePassword
     */
    public void conn(String name,String jdbcurl, String sourceUsername, String sourcePassword) {

        // 1.??????????????????
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 2.?????????????????????
            Connection conn = DriverManager.getConnection(jdbcurl, sourceUsername, sourcePassword);
            // 3.?????????????????????????????????????????????????????????????????????Statement??????
            //?????????
            String sourcName="eda89d0796c0114edfeb1fb3dbd4c4d2";
            String sql="select * from hk_event_info where event_index_code=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, sourcName);
            ResultSet rs = statement.executeQuery();
//			String sql="select * from userinfo where UserName='"+name+"'";
//			Statement statement = conn.createStatement();
//			ResultSet rs = statement.executeQuery(sql);
            // 4.??????????????????????????????(??????ResultSet???)
            while (rs.next()) {
                System.out.println(rs.getString("event_title") + " " + rs.getString("event_type"));
                // ??????????????????
                HkEventInfo hkEventInfo=new HkEventInfo();
                // ????????????
                String eventTitle=rs.getString("event_title");
                if(eventTitle == null && eventTitle == ""){hkEventInfo.setEventTitle(""); }else { hkEventInfo.setEventTitle(eventTitle); }
                // ????????????
                String eventType=rs.getString("event_type");
                if(eventType == null && eventType == ""){ hkEventInfo.setEventType("10"); }else { hkEventInfo.setEventType(eventType); }
                // ??????????????????
                String eventAddress=rs.getString("event_address");
                if(eventAddress == null && eventAddress == ""){ hkEventInfo.setEventAddress(""); }else { hkEventInfo.setEventAddress(eventAddress); }
                //  ??????????????????
                String reportTime=rs.getString("report_time");
                if(reportTime == null && reportTime == ""){ Date date = new Date();SimpleDateFormat formatter = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss");hkEventInfo.setReportTime(formatter.format(date)); }else { hkEventInfo.setReportTime(reportTime); }
                // ????????????
                String placeType=rs.getString("place_type");
                if(placeType == null && placeType == ""){ hkEventInfo.setPlaceType(""); }else { hkEventInfo.setPlaceType(placeType); }
                // ????????????(????????????
                String placeName=rs.getString("place_name");
                if(placeName == null && placeName == ""){ hkEventInfo.setPlaceName(""); }else { hkEventInfo.setPlaceName(placeName); }
                // ???????????????????????????(????????????)
                String regionName=rs.getString("region_name");
                if(regionName == null && regionName == ""){ hkEventInfo.setRegionName(""); }else { hkEventInfo.setRegionName(regionName); }
                // ?????????????????????(????????????)
                String cameraName=rs.getString("camera_name");
                if(cameraName == null && cameraName == ""){ hkEventInfo.setCameraName(""); }else { hkEventInfo.setCameraName(cameraName); }
                // ???????????????
                String reportPersonName=rs.getString("report_person_name");
                if(reportPersonName == null && reportPersonName == ""){ hkEventInfo.setReportPersonName(""); }else { hkEventInfo.setReportPersonName(reportPersonName); }
                //?????????????????????????????????????????????????????????
                String reportTypeName=rs.getString("report_type_name");
                if(reportTypeName == null && reportTypeName == ""){ hkEventInfo.setReportTypeName(""); }else { hkEventInfo.setReportTypeName(reportTypeName); }
                // ???????????????id
                String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");hkEventInfo.setEventId(uuid);


                // ??????????????????
                String eventTypeName= rs.getString("event_type_name");if(eventTypeName == null && eventTypeName == ""){ hkEventInfo.setEventTypeName(""); }else { hkEventInfo.setEventTypeName(eventTypeName); }
                // ??????????????????  ?????????(1,2,3)
                String eventCategory=rs.getString("event_category");if(eventCategory == null && eventCategory == ""){ hkEventInfo.setEventCategory(1); }else { Integer eventcatefor=Integer.parseInt(eventCategory);hkEventInfo.setEventCategory(eventcatefor); }
                // ????????????????????????
                String eventCategoryName=rs.getString("event_category_name");if(eventCategoryName == null && eventCategoryName == ""){ hkEventInfo.setEventCategoryName(""); }else { hkEventInfo.setEventCategoryName(eventCategoryName); }
                // ??????????????????  ?????????(1,2,3)
                String eventSource=rs.getString("event_source");if(eventSource == null && eventSource == ""){ hkEventInfo.setEventSource(1); }else { Integer sout=Integer.parseInt(eventSource);hkEventInfo.setEventSource(sout); }
                //  ????????????????????????
                String eventSoureName=rs.getString("event_source_name");if(eventSoureName == null && eventSoureName == ""){ hkEventInfo.setEventSourceName(""); }else { hkEventInfo.setEventSourceName(eventSoureName); }
                // ??????????????????  ?????????(1,2)
                String reportType =rs.getString("report_type");if(reportType == null && reportType == ""){ hkEventInfo.setReportType(1); }else { Integer type=Integer.parseInt(reportType);hkEventInfo.setReportType(type); }
                // ????????????
                String responsibilitySubject=rs.getString("responsibility_subject");if(responsibilitySubject == null && responsibilitySubject == ""){ hkEventInfo.setResponsibilitySubject(""); }else { hkEventInfo.setResponsibilitySubject(responsibilitySubject); }
                //  ????????????
                String companyLegalPerson=rs.getString("company_legal_person");if(companyLegalPerson == null && companyLegalPerson == ""){ hkEventInfo.setCompanyContactInformation(""); }else { hkEventInfo.setCompanyContactInformation(companyLegalPerson); }
                //  ?????????????????????
                String creditUnionCode=rs.getString("credit_union_code");if(creditUnionCode == null && creditUnionCode == ""){ hkEventInfo.setCreditUnionCode(""); }else { hkEventInfo.setCreditUnionCode(creditUnionCode); }
                // ????????????
                String companyContactInformation=rs.getString("company_contact_information");if(companyContactInformation == null && companyContactInformation == ""){ hkEventInfo.setCompanyContactInformation(""); }else { hkEventInfo.setCompanyContactInformation(companyContactInformation); }
                //  ????????????
                String gridCommunity=rs.getString("grid_community");if(gridCommunity == null && gridCommunity == ""){ hkEventInfo.setGridCommunity(""); }else { hkEventInfo.setGridCommunity(gridCommunity); }
                //  ????????????
                String gridName=rs.getString("grid_name");if(gridName == null && gridName == ""){ hkEventInfo.setGridName(""); }else { hkEventInfo.setGridName(gridName); }
                // ?????????
                String gridMan=rs.getString("grid_man");if(gridMan == null && gridMan == ""){ hkEventInfo.setGridMan(""); }else { hkEventInfo.setGridMan(gridMan); }
                // ????????????
                String patrolTeam=rs.getString("patrol_team");if(patrolTeam == null && patrolTeam == ""){ hkEventInfo.setPatrolTeam(""); }else { hkEventInfo.setPatrolTeam(patrolTeam); }
                // ???????????? ????????????
                String patrolTeamPhone=rs.getString("patrol_team_phone");if(patrolTeamPhone == null && patrolTeamPhone == ""){ hkEventInfo.setPatrolTeamPhone(""); }else { hkEventInfo.setPatrolTeamPhone(patrolTeamPhone); }
                // ??????????????????
                String gridContactInformation=rs.getString("grid_contact_information");if(gridContactInformation == null && gridContactInformation == ""){ hkEventInfo.setGridContactInformation(""); }else { hkEventInfo.setGridContactInformation(gridContactInformation); }
                // ???????????????

                eventInfoMapper.addHKEventInfo(hkEventInfo);

            }

            // ????????????
            conn.close();
            rs.close();
            statement.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * postgetsSQL????????????????????????
     * @param jdbcurl
     * @param sourceUsername
     * @param sourcePassword
     */
    public static void postConn(String jdbcurl, String sourceUsername, String sourcePassword){
        try {
            Class.forName("org.postgresql.Driver");
        }catch (ClassNotFoundException e){
            System.out.println("could not find postgreSQL JDBC Driver");
            e.printStackTrace();
            return;
        }
        Connection connection = null;
        try {
//             connection = DriverManager.getConnection(
//                     "jdbc:postgresql://localhost:5432/postgres",
//                     "postgres",
//                     "");
            connection = DriverManager.getConnection(jdbcurl,sourceUsername,sourcePassword);
        }catch (SQLException e){
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }
        if (connection != null){
            System.out.println("connect db successful!");
        }else {
            System.out.println("Failed to make conn!");
        }

        try {
            String sourcName="eda89d0796c0114edfeb1fb3dbd4c4d2";
            Statement  stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from hk_event_info where event_index_code=?");
            if(rs!=null){
                rs.next();  // ????????????
                System.out.println(rs.getString(1)+"          "+rs.getString(2)+"             "+rs.getString(3));

            }else {
                System.out.println("null");
            }
            rs.close();
            stmt.close();
            connection.close();
        }catch (Exception exp){
            exp.printStackTrace();
        }
    }


    /**
     * ?????????????????????
     * @param sourceAccess
     * @param sourceHost
     * @param sourcePort
     * @param sourceUsername
     * @param sourcePassword
     * @return
     */
    @Override
    public Map<String, Object> connection(String sourceAccess, String sourceHost, String sourcePort, String sourceUsername, String sourcePassword) {
        Connection conn = null;
        Map<String, Object> map = new HashMap<>();
        try {
            String jdbcurl = "";
            if ("MYSQL".equals(sourceAccess.toUpperCase())) {
                jdbcurl = "jdbc:mysql://" + sourceHost + ":"
                        + sourcePort
                        + "/maao-db?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true";
                conn = DBHelper.initMysql(jdbcurl, sourceUsername,sourcePassword, false);
                map.put("conn",conn);

            }else if("POSTGETSQL".equals(sourceAccess.toUpperCase())){
                jdbcurl = "jdbc:postgresql://" + sourceHost + ":"
                        + sourcePort;
                conn = DBHelper.initPostgetSQL(jdbcurl, sourceUsername,sourcePassword, false);
                map.put("conn",conn);

            }
        }catch  (Exception e){
            logger.error("?????????????????????",e);
            return null;
        }
        return map;
    }
    static HostnameVerifier hv = new HostnameVerifier() {
        public boolean verify(String urlHostName, SSLSession session) {
            return true;
        }
    };
    /**
     * ??????api??????
     * @param sourceAddress
     * @return
     */
    @Override
    public Map<String, Object> apiTest(String sourceAddress) {
        boolean flag=false;
        Map<String, Object> map = new HashMap<>();
        // ???????????????http??????
        boolean isurl = false;
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//?????????????????????

        Pattern pat = Pattern.compile(regex.trim());//??????
        Matcher mat = pat.matcher(sourceAddress.trim());
        isurl = mat.matches();//??????????????????
        if (!isurl) {
            isurl = false;
            map.put("flag",flag);
        }else {
            String tempUrl = sourceAddress.substring(0, 5);//???????????????5???
            if (tempUrl.contains("http")) {//????????????????????????????????????http
                if (tempUrl.equals("https")) {//????????????????????????https??????
                    try {
                        trustAllHttpsCertificates();//????????????https???
                    } catch (Exception e) {
                        e.printStackTrace();
                        map.put("flag", flag);
                    }
                    HttpsURLConnection.setDefaultHostnameVerifier(hv);//????????????https???
                }
                flag = isConnServerByHttp(sourceAddress);
            } else {//???????????????IP??????
                flag = isReachable(sourceAddress);
            }
            if (flag) {
                System.out.println("??????");
                map.put("flag", flag);
            } else {
                System.out.println("??????");
                map.put("flag", flag);
            }
            return map;
        }
        return map;
    }

    /*?????????Https??????*/
    private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                .getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
                .getSocketFactory());
    }
    /**
     * ?????????????????????IP???????????????????????????
     *
     * @param remoteInetAddr
     * @return
     */
    public static boolean isReachable(String remoteInetAddr) {// IP??????????????????????????????Ping??????
        boolean reachable = false;
        try {
            InetAddress address = InetAddress.getByName(remoteInetAddr);
            reachable = address.isReachable(1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reachable;
    }
    public static boolean isConnServerByHttp(String serverUrl) {// ?????????????????????
        boolean connFlag = false;
        URL url;
        HttpURLConnection conn = null;
        try {
            url = new URL(serverUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3 * 1000);
            if (conn.getResponseCode() == 200) {// ??????????????????????????????true
                connFlag = true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return connFlag;
    }

    static class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }

}
