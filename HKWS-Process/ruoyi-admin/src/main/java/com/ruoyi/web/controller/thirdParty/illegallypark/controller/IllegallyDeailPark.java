package com.ruoyi.web.controller.thirdParty.illegallypark.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.map.GPSUtils;
import com.ruoyi.system.domain.HkEarlyWarning.HkEventInfo;
import com.ruoyi.system.domain.platform.illagallyPark.illagallyDail;
import com.ruoyi.system.service.HkEarlyWarning.HkEventService;
import com.ruoyi.system.domain.platform.illagallyPark.Park;
import com.ruoyi.system.domain.platform.illagallyPark.illParkList;
import com.ruoyi.system.service.IHkMapService;
import com.ruoyi.web.controller.thirdParty.illegallypark.util.RequestGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author FanKaibiao
 * @date 2021-05-19-16:36
 */
@Component
@Controller
@RequestMapping("/hkapi")
public class IllegallyDeailPark extends BaseController {

    @Autowired
    private HkEventService hkEventService;

    @Value("${illegallypark.appKey}")
    private String ak;

    @Value("${illegallypark.appSecret}")
    private String sk;

    @Value("${illegallypark.listurl}")
    private String listUrl;

    @Value("${illegallypark.deailurl}")
    private String detailUrl;

    @Value("${illegallypark.imageUrl}")
    private String imageUrl;
    @Autowired
    private IHkMapService hkMapService;

   @RequestMapping("/IllegallyList")
   @ResponseBody
//   @Scheduled(cron="0 */20 * * * ?")
   public AjaxResult getParamBody() {
       Map<String, Object> paramMap = new HashMap<>();
       paramMap.put("OrgCode", "001029");
       SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
       Date date = new Date();
       String format1 = dateFormat1.format(date);
       HkEventInfo hkEventInfo1 = new HkEventInfo();
       hkEventInfo1.setEventCreateTime(format1);
       //??????????????????????????????????????????????????????????????????????????????,?????????????????????????????????
       List<HkEventInfo> hkEventInfos= hkEventService.queryIllegaDeail(hkEventInfo1);
        String jsonStr="";
        if(hkEventInfos.size()!=0){
           Date createTime = hkEventInfos.get(0).getCreateTime();
            DateFormat datef =  new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String format = dateFormat.format(createTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(createTime);
            cal.add(Calendar.DAY_OF_MONTH,0);
            String StartTime=datef.format(cal.getTime())+" "+format;
            String EndTime=datef.format(cal.getTime())+" 23:59:59";
            paramMap.put("StartTime",StartTime);
            paramMap.put("EndTime",EndTime);
            jsonStr = JSONUtil.toJsonStr(paramMap);
       }else{
            DateFormat datef =  new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH,0);
            String StartTime=datef.format(cal.getTime())+" 00:00:00";
            String EndTime=datef.format(cal.getTime())+" 23:59:59";
            paramMap.put("StartTime",StartTime);
            paramMap.put("EndTime",EndTime);
            jsonStr = JSONUtil.toJsonStr(paramMap);
        }

       Map<String, Object> paramMap2 = new HashMap<>();
       paramMap2.put("entityData", jsonStr);
       String request = RequestGateway.request(listUrl, ak, sk, paramMap2);
       if(request.equals(null)){
           System.out.println("????????????");
       }else{
           Park parseObject = JSONObject.parseObject(request,Park.class);
           String data = parseObject.getData();
           illParkList res = JSONObject.parseObject(data, illParkList.class);
           for(int i=0;i<=res.getData().size()-1;i++){
               String recordid = res.getData().get(i).getRecordid();
               int count = hkEventService.queryEventEventIndexCode(recordid);
//               String plateno = res.getData().get(0).getPlateno();
               if (count==0){
                   Map<String, Object> map=new HashMap<>();
                   map.put("recordid", recordid);
                   //????????????
                   String request1 = RequestGateway.request(detailUrl, ak, sk, map);
                   Park park = JSONObject.parseObject(request1,Park.class);
                   String deailPark = park.getData();
                   illagallyDail parkDeailList = JSONObject.parseObject(deailPark, illagallyDail.class);
                   HkEventInfo hkEventInfo = new HkEventInfo();
                   //uuid
                   hkEventInfo.setEventId(UUID.randomUUID().toString().trim().replaceAll("-", "").trim().replaceAll("-", "").trim().replaceAll("-", ""));
                   //????????????
                   hkEventInfo.setEventIndexCode(parkDeailList.getData().getParkingRecord().getRecordId());
                   //????????????
                   SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                   Date date1 = new Date();
                   String format2 = dateFormat2.format(date1);
                   hkEventInfo.setEventCreateTime(format2);
                   //1011 ????????????
                   hkEventInfo.setComponentId("1001");
                   //????????????
                   hkEventInfo.setEventType("1101");
                   hkEventInfo.setEventTypeName("??????");

                   //???????????????. ??????-??????
                   double[] convertPoint = GPSUtils.gcj02_To_Bd09(Double.parseDouble(parkDeailList.getData().getParkingRecord().getLat()), Double.parseDouble(parkDeailList.getData().getParkingRecord().getLng()));
//                System.out.println(convertPoint[1]+"----------"+convertPoint[0]);
                   Double[] points = {convertPoint[1], convertPoint[0]};
                   Map<String, Object> map1 = hkMapService.calculateRegionByLongitudeAndLatitude(points);
                   String regionCode = map1.get("regionId") + "";
                   String regionName = map1.get("regionName") + "";
                   System.out.println(regionCode+"------------"+regionName);
                   hkEventInfo.setRegionName(regionName);
                   //????????????
                   hkEventInfo.setLongitude(convertPoint[1]+"");
                   //????????????
                   hkEventInfo.setLatitude( convertPoint[0]+"");
                   //??????code
                   hkEventInfo.setRegionIndexCode(regionCode);
                   //?????????
                   hkEventInfo.setPlateNo(parkDeailList.getData().getParkingRecord().getPlateNo());
                   //????????????
                   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                   Date parkingTime = parkDeailList.getData().getParkingRecord().getParkingTime();
//                   Date recordTime = parkDeailList.getData().getParkingRecord().getRecordTime();
                   String format = dateFormat.format(parkingTime);
//                   String format1 = dateFormat.format(recordTime);
                   hkEventInfo.setReportTime(format);
                   hkEventInfo.setCreateTime(parkingTime);
                   //????????????
                   hkEventInfo.setCameraName(parkDeailList.getData().getParkingRecord().getParkingAddress());
                   hkEventInfo.setEventAddress(parkDeailList.getData().getParkingRecord().getParkingAddress());
                   //????????????
                   String vehicleKind = parkDeailList.getData().getParkingRecord().getVehicleKind();
                   if(vehicleKind.equals("01")){
                       hkEventInfo.setEventTitle("??????????????????");
                   }else if(vehicleKind.equals("02")){
                       hkEventInfo.setEventTitle("??????????????????");
                   }else if(vehicleKind.equals("03")){
                       hkEventInfo.setEventTitle("??????????????????");
                   }else if(vehicleKind.equals("04")){
                       hkEventInfo.setEventTitle("??????????????????");
                   }else if(vehicleKind.equals("05")){
                       hkEventInfo.setEventTitle("????????????");
                   }else if(vehicleKind.equals("06")){
                       hkEventInfo.setEventTitle("?????????????????????");
                   }else if (vehicleKind.equals("07")){
                       hkEventInfo.setEventTitle("?????????????????????");
                   }
                   //????????????
                   String plateNoColor = parkDeailList.getData().getParkingRecord().getPlateNoColor();
                   //????????????
                   String status = parkDeailList.getData().getParkingRecord().getStatus();
                   if(status.equals("00")){
                       hkEventInfo.setEventAlertStatus(2);
                       hkEventInfo.setEventAlertStatusName("?????????");
                   }else if(status.equals("01")){
                       hkEventInfo.setEventAlertStatus(9);
                       hkEventInfo.setEventAlertStatusName("??????????????????");
                       hkEventInfo.setUpdateTime(new Date());
                   }else if(status.equals("02")){
                       hkEventInfo.setEventAlertStatus(9);
                       hkEventInfo.setEventAlertStatusName("??????????????????");
                       hkEventInfo.setUpdateTime(new Date());
                   }else if(status.equals("03")){
                       hkEventInfo.setEventAlertStatus(9);
                       hkEventInfo.setEventAlertStatusName("??????????????????");
                       hkEventInfo.setUpdateTime(new Date());
                   }else if(status.equals("04")){
                       hkEventInfo.setEventAlertStatus(9);
                       hkEventInfo.setEventAlertStatusName("??????????????????");
                       hkEventInfo.setUpdateTime(new Date());
                   }else if(status.equals("05")){
                       hkEventInfo.setEventAlertStatus(9);
                       hkEventInfo.setEventAlertStatusName("??????????????????");
                       hkEventInfo.setUpdateTime(new Date());
                   }else if(status.equals("06")){
                       hkEventInfo.setEventAlertStatus(9);
                       hkEventInfo.setEventAlertStatusName("??????????????????");
                       hkEventInfo.setUpdateTime(new Date());
                   }else if(status.equals("07")){
                       hkEventInfo.setEventAlertStatus(9);
                       hkEventInfo.setEventAlertStatusName("??????????????????");
                       hkEventInfo.setUpdateTime(new Date());
                   }else if(status.equals("08")){
                       hkEventInfo.setEventAlertStatus(9);
                       hkEventInfo.setEventAlertStatusName("?????????");
                       hkEventInfo.setUpdateTime(new Date());
                   }
                   hkEventInfo.setReportType(1);
                   hkEventInfo.setReportTypeName("????????????");
                   //??????
                   for(int j=0;j<parkDeailList.getData().getImageList().size()-1;j++){
                       hkEventInfo.setEventImage(imageUrl+parkDeailList.getData().getImageList().get(0).getImageUrl());
                       hkEventInfo.setEventThumbnailImage(imageUrl+parkDeailList.getData().getImageList().get(0).getImageUrl());
                   }
                   hkEventService.addHKEventInfo(hkEventInfo);
               }
           }
       }
       return AjaxResult.success("??????");
   }

   /* public static void main(String[] args) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("OrgCode", "001029");
        DateFormat datef =  new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH,0);
        String StartTime=datef.format(cal.getTime())+" 00:00:00";
        String EndTime=datef.format(cal.getTime())+" 23:59:59";
        paramMap.put("StartTime",StartTime);
        paramMap.put("EndTime",EndTime);
        String jsonStr = JSONUtil.toJsonStr(paramMap);
        Map<String, Object> paramMap2 = new HashMap<>();
        paramMap2.put("entityData", jsonStr);
        String request = RequestGateway.request("http://10.32.188.81:81/gateway/prod/1071/1223", "b720c669f14e4ca6ab9a06423b64d952", "ec167a1253734ff9b83b1a9792136044", paramMap2);
        if(request.equals(null)){
            System.out.println("????????????");
        }else{
//            System.out.println(request);
            Park parseObject = JSONObject.parseObject(request,Park.class);
            String data = parseObject.getData();
//            System.out.println(data);
            illParkList res = JSONObject.parseObject(data, illParkList.class);
            String recordid1 = res.getData().get(0).getRecordid();
            Map<String, Object> map=new HashMap<>();
            map.put("recordid", recordid1);
            System.out.println(res.getData().get(0).getRecordid());
           *//* for(int i=0;i<=res.getData().size();i++){
                String recordid = res.getData().get(i).getRecordid();
                Map<String, Object> map=new HashMap<>();
                map.put("recordid", recordid);
            }*//*
           *//* String request1 = RequestGateway.request("http://10.32.188.81:81/gateway/prod/1072/1241", "b720c669f14e4ca6ab9a06423b64d952", "ec167a1253734ff9b83b1a9792136044", map);
            Park park = JSONObject.parseObject(request1,Park.class);
            String deailPark = park.getData();
            System.out.println(deailPark);*//*
        }
    }
*/
   /* public static void main(String[] args) {
        String Latitude="30.23217";
        String Longitude="119.992534";
        //???????????????. ??????-??????
        double[] convertPoint = GPSUtils.bd09_To_Gcj02(Double.parseDouble(Latitude), Double.parseDouble(Longitude));
        System.out.println(convertPoint[1]+","+convertPoint[0]);
        Double[] points = {convertPoint[1], convertPoint[0]};
//        Map<String, Object> map1 = hkMapService.calculateRegionByLongitudeAndLatitude(points);
//        String regionCode = map1.get("regionId") + "";
//        String regionName = map1.get("regionName") + "";
//        System.out.println(regionCode+"------------"+regionName);
    }*/
   public static void main(String[] args) {
       String s="";
       if(s!=""){
           System.out.println("111");
       }else{
           System.out.println("@22");
       }
   }
}
