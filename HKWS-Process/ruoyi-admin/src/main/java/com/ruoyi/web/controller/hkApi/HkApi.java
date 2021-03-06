package com.ruoyi.web.controller.hkApi;


import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import com.ruoyi.common.config.HkwsConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.map.GPSUtils;
import com.ruoyi.quartz.domain.SysJobLog;
import com.ruoyi.quartz.service.ISysJobLogService;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.HkEarlyWarning.HkEventInfo;
import com.ruoyi.system.domain.HkEntity.*;
import com.ruoyi.system.domain.platform.camera.cameraParm;
import com.ruoyi.system.domain.platform.zheLiFang.ZheLiFangDto;
import com.ruoyi.system.service.HkEarlyWarning.HkEventService;
import com.ruoyi.system.service.IHkMapService;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.service.IXlEquipmentInfoService;
import com.ruoyi.system.service.app.AppEventDetailService;
import com.ruoyi.system.utils.MD5Util;
import com.ruoyi.system.domain.platform.managementCM.management;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/hkapi")
public class HkApi {
    @Autowired
    private ISysDeptService iSysDeptService;
    @Autowired
    private HkEventService hkEventService;
    @Autowired
    private IXlEquipmentInfoService iXlEquipmentInfoService;
    @Autowired
    private  IHkMapService hkMapService;


    @Value("${hkapi.ak}")
    public String ak;

    @Value("${hkapi.sk}")
    public String sk;


    /**
     * ????????????
     * */
    @GetMapping("/subscribe")
    @ResponseBody
    public AjaxResult appSubscribe(){
        ces();
        return AjaxResult.success("??????");

    }


    /**
     * ????????????????????????
     * */
    @GetMapping("/qyinfo")
    @ResponseBody
    public AjaxResult hkqyinfo(){
        //??????qyinfo() ??????deptAll()????????????????????????
        qyinfo();
        return AjaxResult.success("??????");

    }


    /**
     * ????????????????????????
     * */
    @PostMapping("/beedback")
    @ResponseBody
    public AjaxResult qyinfoList(){
//        eventFeedback();
        return AjaxResult.success("??????");

    }



    public static void main(String[] args) throws ParseException {
        HkApi api = new HkApi();
//        api.hkinfo();
        api.sheAll();
//        api.ces();
//        storeFetch();
//        api.qyinfo();
//        api.eventFeedback();
        /*qyinfouser();
        pointproposal();
        pointproposal();*/
//        api.eventFeedback();
    }
    private  void ces() {
        ArtemisConfig.host=HkwsConfig.host;
        ArtemisConfig.appKey = HkwsConfig.appKey;
        ArtemisConfig.appSecret = HkwsConfig.appSecret;
        final String getSecurityApi = HkwsConfig.subscribe; // ????????????
        Map<String, String> path = new HashMap<String,String>(2) {
            {
                put("https://", getSecurityApi);
            }
        };
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isSubscribe",true);
        jsonObject.put("subscribeId","????????????");
        jsonObject.put("callbackAddress","http://172.45.4.111:80/hkapi/callbackUrl");
        String body = jsonObject.toJSONString();
        String res = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json");
        System.out.println("==============="+res);
    }
    //???????????????????????????
    private  void previewUrl() {
        ArtemisConfig.host=HkwsConfig.host;
        ArtemisConfig.appKey = HkwsConfig.appKey;
        ArtemisConfig.appSecret = HkwsConfig.appSecret;
        final String getSecurityApi = HkwsConfig.previewUrl; // ????????????
        Map<String, String> path = new HashMap<String,String>(2) {
            {
                put("https://", getSecurityApi);
            }
        };
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cameraIndexCode", "33011068001321112507");
        jsonObject.put("protocol", "hls");
        jsonObject.put("expand","transcode=1&videotype=h264");
        String body = jsonObject.toJSONString();
        String res = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json");
        System.out.println("==============="+res);
    }
    //????????????
//    @Scheduled(cron="0 */10 * * * ?")
    public void cg() throws ParseException {
        ArtemisConfig.host=HkwsConfig.host;
        ArtemisConfig.appKey = HkwsConfig.appKey;
        ArtemisConfig.appSecret = HkwsConfig.appSecret;
        final String getSecurityApi = HkwsConfig.eventcg; // ????????????
        Map<String, String> path = new HashMap<String,String>(2) {
            {
                put("https://", getSecurityApi);
            }
        };
        JSONObject jsonbody = new JSONObject();
        DateFormat datef =  new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH,0);
        String StartTime=datef.format(cal.getTime())+"T00:00:00.000+08:00";
        String EndTime=datef.format(cal.getTime())+"T023:59:59.999+08:00";
        jsonbody.put("beginTime",StartTime);
        jsonbody.put("endTime",EndTime);
        String body = jsonbody.toJSONString();
        String res = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json");
        JSONObject jsonObject = JSONObject.parseObject(res);
//        JSONArray data = jsonObject.getJSONArray("data");
        management Passinfo= JSONObject.parseObject(jsonObject.toString(), management.class);
        geteventcg(Passinfo);
    }
    @RequestMapping("cameraAll")
    @ResponseBody
    public void sheAll() throws ParseException {
        ArtemisConfig.host=HkwsConfig.host;
        ArtemisConfig.appKey = HkwsConfig.appKey;
        ArtemisConfig.appSecret = HkwsConfig.appSecret;
        final String getSecurityApi = HkwsConfig.sheXiangAll; // ????????????
        Map<String, String> path = new HashMap<String,String>(2) {
            {
                put("https://", getSecurityApi);
            }
        };
        JSONObject jsonbody = new JSONObject();
        jsonbody.put("pageNo",3);
        jsonbody.put("pageSize",100);
        String body = jsonbody.toJSONString();
        String res = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json");
        JSONObject jsonObject = JSONObject.parseObject(res);
        System.out.println(res);
//        JSONArray data = jsonObject.getJSONArray("data");
        cameraParm camera= JSONObject.parseObject(jsonObject.toString(), cameraParm.class);
        cameraList(camera);
    }

    //????????????
    private AjaxResult cameraList(cameraParm camera) {
        XlEquipmentInfo xlEquipmentInfo = new XlEquipmentInfo();
        for(int i=0;i<camera.getData().getList().size();i++){
            String cameraIndexCode = camera.getData().getList().get(i).getCameraIndexCode();
            int count=iXlEquipmentInfoService.queryEquipmentCamindexCode(cameraIndexCode);
            if(count==0){
                //???????????????
                xlEquipmentInfo.setEquipmentName(camera.getData().getList().get(i).getName());
                //????????????
                xlEquipmentInfo.setEquipmentType(String.valueOf(camera.getData().getList().get(i).getCameraType()));
                //???????????????
                xlEquipmentInfo.setEquipmentSerialNumber(camera.getData().getList().get(i).getCameraIndexCode());
                //????????????
                xlEquipmentInfo.setEquipmentModel(camera.getData().getList().get(i).getDeviceResourceType());
                //????????????
                xlEquipmentInfo.setEquipmentAdress(camera.getData().getList().get(i).getName());
                //??????
                xlEquipmentInfo.setLongitude(camera.getData().getList().get(i).getLongitude());
                //??????
                xlEquipmentInfo.setLatitude(camera.getData().getList().get(i).getLatitude());
                //??????
                xlEquipmentInfo.setEquipmentStatus(camera.getData().getList().get(i).getStatus());
                //????????????
                xlEquipmentInfo.setEquipmentOrganization(camera.getData().getList().get(i).getUnitIndexCode());
                String recordLocation = camera.getData().getList().get(i).getRecordLocation();
                if(recordLocation.equals("")){

                }else{
                    //????????????
                    xlEquipmentInfo.setEquipmentStorage(Integer.parseInt(recordLocation));
                }
                //????????????
                xlEquipmentInfo.setCreateTime(new Date());
                //?????????
                xlEquipmentInfo.setCreateBy("admin");
                iXlEquipmentInfoService.insertXlEquipmentInfo(xlEquipmentInfo);
            }
        }
        return AjaxResult.success();
    }


    public  void qyinfo(){
        ArtemisConfig.host=HkwsConfig.host;
        ArtemisConfig.appKey = HkwsConfig.appKey;
        ArtemisConfig.appSecret = HkwsConfig.appSecret;
        final String getSecurityApi = HkwsConfig.qyinfo; // ??????????????????
        Map<String, String> path = new HashMap<String,String>(2) {
            {
                put("https://", getSecurityApi);
            }
        };

        JSONObject jsonBody = new JSONObject();
        String body = jsonBody.toJSONString();
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null,
                null,"application/json");
        //????????????????????????????????????
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONArray data = jsonObject.getJSONArray("data");
        //RegionInfoDto ?????????????????????  ??????????????????????????????,????????????????????????????????????????????????
        List<RegionInfoDto> regionInfoDtos = JSON.parseArray(data.toString(),RegionInfoDto.class);

        for(int i=0;i<regionInfoDtos.size();i++){
            List users = regionInfoDtos.get(i).getUsers();
            List<UserDto> userDtos = JSON.parseArray(users.toString(), UserDto.class);
            regionInfoDtos.get(i).setUserDto(userDtos);
        }
        //????????????????????????
//        deptAll(regionInfoDtos);
    }

    //???????????????????????????(??????????????????)
    public  void qyinfouser(){
        ArtemisConfig.host=HkwsConfig.host;
        ArtemisConfig.appKey = HkwsConfig.appKey;
        ArtemisConfig.appSecret = HkwsConfig.appSecret;
        final String getSecurityApi = HkwsConfig.qyinfouser; // ???????????????????????????????????????
        Map<String, String> path = new HashMap<String,String>(2) {
            {
                put("https://", getSecurityApi);
            }
        };

        JSONObject jsonBody = new JSONObject();

        String body = jsonBody.toJSONString();
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null,
                null,"application/json");
        System.out.println(result);
    }
    //????????????
    public  void pointproposal(){
        ArtemisConfig.host=HkwsConfig.host;
        ArtemisConfig.appKey = HkwsConfig.appKey;
        ArtemisConfig.appSecret = HkwsConfig.appSecret;
        final String getSecurityApi = HkwsConfig.pointproposal; // ????????????
        Map<String, String> path = new HashMap<String,String>(2) {
            {
                put("https://", getSecurityApi);
            }
        };

        JSONObject jsonBody = new JSONObject();

        String body = jsonBody.toJSONString();
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null,
                null,"application/json");
        System.out.println(result);
    }
    //????????????(??????????????????)
        public  void pointproposalback(){
        ArtemisConfig.host=HkwsConfig.host;
        ArtemisConfig.appKey = HkwsConfig.appKey;
        ArtemisConfig.appSecret = HkwsConfig.appSecret;
        final String getSecurityApi = HkwsConfig.pointproposalback; // ????????????
        Map<String, String> path = new HashMap<String,String>(2) {
            {
                put("https://", getSecurityApi);
            }
        };

        JSONObject jsonBody = new JSONObject();
        String body = jsonBody.toJSONString();
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null,
                null,"application/json");
        System.out.println(result);
    }


    //????????????(??????????????????)
    @PostMapping("/eventFeedback")
    @ResponseBody
    public  String eventFeedback(String evenid){
        ArtemisConfig.host=HkwsConfig.host;
        ArtemisConfig.appKey = HkwsConfig.appKey;
        ArtemisConfig.appSecret = HkwsConfig.appSecret;
        final String getSecurityApi = HkwsConfig.eventFeedback; // ????????????
        Map<String, String> path = new HashMap<String,String>(2) {
            {
                put("https://", getSecurityApi);
            }
        };
//        configService.selectConfigByKey("sys.index.skinName")

        EventAnalysisDTO eventAnalysisDTO = new EventAnalysisDTO();
        feedbck(eventAnalysisDTO,evenid);
        String body = JSONObject.toJSON(eventAnalysisDTO).toString();
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null,
                null,"application/json");
        return result;
    }

    private  void storeFetch(){
        ArtemisConfig.host=HkwsConfig.host;
        ArtemisConfig.appKey = HkwsConfig.appKey;
        ArtemisConfig.appSecret = HkwsConfig.appSecret;
        final String getSecurityApi = HkwsConfig.storefetch; // ????????????
        Map<String, String> path = new HashMap<String,String>(2) {
            {
                put("https://", getSecurityApi);
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("pageNo",0);
        jsonBody.put("pageSize",20);
        jsonBody.put("placeTypeNum","ocos");
        String body = jsonBody.toJSONString();
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null,
                null,"application/json");
        System.out.println(result);

    }

    private static final Logger log = LoggerFactory.getLogger(HkApi.class);


    @Autowired
    private AppEventDetailService appEventDetailService;

    @Autowired
    private ISysUserService iSysUserService;

    @Autowired
    private ISysJobLogService iSysJobLogService;
    /**
     * ??????????????????
     * */
    @PostMapping("/callbackUrl")
    @ResponseBody
    public AjaxResult callbackUrl(@RequestBody EventAnalysisDTO obj){

        log.info("????????????:==="+JSON.toJSONString(obj));
//        System.out.println("????????????"+obj.getComponentId()+"========="+obj.getEventInfo());
        SysJobLog joblog = new SysJobLog();
        joblog.setJobName("??????????????????");
        joblog.setJobGroup(obj.getComponentId());
        try{
            if(obj.getEventInfo()== null){
                return AjaxResult.error("????????????");
            }
            HkEventInfo hkEventInfo = infoHkwsinfo(obj.getEventInfo());
            hkEventInfo.setComponentId(obj.getComponentId());
            String regionIndexCode = obj.getEventInfo().getRegionIndexCode();
            int sysDeptCount = iSysDeptService.findSysDeptCount(regionIndexCode);
            if(sysDeptCount !=0){
                hkEventService.addHKEventInfo(hkEventInfo);
                joblog.setInvokeTarget("??????????????????");
                joblog.setStatus("0");
                joblog.setJobMessage("????????????:"+JSON.toJSONString(obj));
                iSysJobLogService.addJobLog(joblog);
            }else{
                joblog.setInvokeTarget("??????????????????");
                joblog.setStatus("1");
                joblog.setExceptionInfo("????????????:"+JSON.toJSONString(obj));
                iSysJobLogService.addJobLog(joblog);
            }
        }catch (Exception e){
            e.printStackTrace();
            joblog.setStatus("1");
            joblog.setInvokeTarget("????????????");
            joblog.setExceptionInfo("???????????????"+e.getMessage());
            iSysJobLogService.addJobLog(joblog);
        }

        return AjaxResult.success();
    }

   /* private void  hkinfo(){
        ArtemisConfig.host=HkwsConfig.host;
        ArtemisConfig.appKey = HkwsConfig.appKey;
        ArtemisConfig.appSecret = HkwsConfig.appSecret;
        String getSecurityApi = HkwsConfig.fetchAll; // ????????????
        Map<String, String> path = new HashMap<String,String>(2) {
            {
                put("https://", getSecurityApi);
            }
        };

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("pageNo",0);
        jsonBody.put("pageSize",20);
        jsonBody.put("eventType","fireExitOccupy");
        String body = jsonBody.toJSONString();
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null,
                null,"application/json");
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject jsonlist = JSONObject.parseObject(jsonObject.get("data").toString());
        List<EventInfoDTOOne> list = JSONArray.parseArray(jsonlist.get("list").toString(),EventInfoDTOOne.class);
        HkEventInfo hkEventInfo = infoHkwsinfo(list.get(0));
        //hkEventService.addHKEventInfo(hkEventInfo);
        System.out.println(result);
    }*/

    private HkEventInfo infoHkwsinfo(EventInfoDTO eventInfo) throws ParseException {
        EventDTO eventinfo = eventInfo.getEvent();
        HkEventInfo hkEventInfo = new HkEventInfo();
        if(eventInfo.getReportProvider()!=null && eventInfo.getReportProvider().getReportDevice()!=null){
            hkEventInfo.setCameraIndexCode(eventInfo.getReportProvider().getReportDevice().getCameraIndexCode());
            hkEventInfo.setCameraName(eventInfo.getReportProvider().getReportDevice().getCameraName());
        }

        hkEventInfo.setRegionIndexCode(eventInfo.getRegionIndexCode());
        hkEventInfo.setRegionName(eventInfo.getRegionName());
        hkEventInfo.setPlaceName(eventInfo.getPlaceName());
        hkEventInfo.setEventId(UUID.randomUUID().toString().trim().replaceAll("-", ""));
        hkEventInfo.setEventIndexCode(eventinfo.getEventIndexCode());//????????????????????????
        hkEventInfo.setEventTitle(eventinfo.getEventTitle());
        hkEventInfo.setEventAddress(eventinfo.getEventAddress());
        hkEventInfo.setReportTime(eventinfo.getReportTime());
        hkEventInfo.setEventType(eventinfo.getEventType());
        hkEventInfo.setEventTypeName(eventinfo.getEventTypeName());
        hkEventInfo.setEventImage(eventinfo.getEventImage());
        hkEventInfo.setEventThumbnailImage(eventinfo.getEventImage());
        hkEventInfo.setStorageId(eventinfo.getStorageId());
        if(eventinfo.getEventStatus() == 3){
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String format = dateFormat.format(date);
            hkEventInfo.setEventCreateTime(format);
            hkEventInfo.setEventAlertStatus(9);
            hkEventInfo.setEventAlertStatusName("??????????????????");
        }else{
            //??????????????????
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String eventUpdateTime = eventinfo.getEventUpdateTime();
            if( eventUpdateTime == null  || eventUpdateTime.equals("") ){
                Date date1 = new Date();
                String format1 = dateFormat.format(date1);
                hkEventInfo.setEventCreateTime(format1);
                String eventType = eventinfo.getEventType();
                String eventTitle = eventinfo.getEventTitle();
                String regionIndexCode = eventInfo.getRegionIndexCode();
                HkEventInfo hkEventInfo1 = new HkEventInfo();
                hkEventInfo1.setEventCreateTime(format1);
                hkEventInfo1.setEventType(eventType);
                hkEventInfo1.setRegionIndexCode(regionIndexCode);
                hkEventInfo1.setEventTitle(eventTitle);
                /*???????????????????????????????????????????????????????????????code??????????????????2 ???????????????
                ???????????????????????????0????????????????????????????????????????????????0 ?????????????????????????????????10(????????????)*/
                int eventcount=hkEventService.selectEventRepeat(hkEventInfo1);
                if(eventcount !=0){
                    hkEventInfo.setEventAlertStatus(10);
                    hkEventInfo.setEventAlertStatusName("????????????");
                }else{
                    hkEventInfo.setEventAlertStatus(eventinfo.getEventStatus());
                    hkEventInfo.setEventAlertStatusName("?????????");
                }
            }else{
                Date parse = dateFormat1.parse(eventUpdateTime);
                String format1 = dateFormat.format(parse);
                hkEventInfo.setEventCreateTime(format1);
                String eventType = eventinfo.getEventType();
                String eventTitle = eventinfo.getEventTitle();
                String regionIndexCode = eventInfo.getRegionIndexCode();
                HkEventInfo hkEventInfo1 = new HkEventInfo();
                hkEventInfo1.setEventCreateTime(format1);
                hkEventInfo1.setEventType(eventType);
                hkEventInfo1.setRegionIndexCode(regionIndexCode);
                hkEventInfo1.setEventTitle(eventTitle);
            /*???????????????????????????????????????????????????????????????code??????????????????2 ???????????????
            ???????????????????????????0????????????????????????????????????????????????0 ?????????????????????????????????10(????????????)*/
                int eventcount=hkEventService.selectEventRepeat(hkEventInfo1);
                if(eventcount !=0){
                    hkEventInfo.setEventAlertStatus(10);
                    hkEventInfo.setEventAlertStatusName("????????????");
                }else{
                    hkEventInfo.setEventAlertStatus(eventinfo.getEventStatus());
                    hkEventInfo.setEventAlertStatusName("?????????");
                }
            }
        }
        hkEventInfo.setReportType(eventinfo.getReportType());
        hkEventInfo.setReportTypeName(eventinfo.getReportTypeName());
        hkEventInfo.setLawEnforcementType(eventinfo.getLawEnforcementType());
        hkEventInfo.setLawEnforcementTypeName(eventinfo.getLawEnforcementTypeName());
        hkEventInfo.setRiskLevel(eventinfo.getRiskLevel());
        hkEventInfo.setCurrentProcessorId(eventinfo.getCurrentProcessorId());
        hkEventInfo.setCurrentProcessorName(eventinfo.getCurrentProcessorName());
        hkEventInfo.setEventUpdateTime(eventinfo.getEventUpdateTime());
        hkEventInfo.setCreateTime(new Date());
        if(eventInfo.getPoint()!=null){
            hkEventInfo.setLongitude(eventInfo.getPoint().getLongitude());
            hkEventInfo.setLatitude(eventInfo.getPoint().getLatitude());
        }
//        hkEventInfo.setGeometry(eventinfo.getGeometry());
        return hkEventInfo;
    }

    /**
     * ?????????????????????????????????,?????????????????? ????????????
     *  regionInfoDtos ?????????????????? ??????????????????
     * @param regionInfoDtos
     * @return
     */
    public AjaxResult deptAll(List<RegionInfoDto> regionInfoDtos){
        SysDept sysDept = new SysDept();
        for(int i=0;i<regionInfoDtos.size();i++){
            String parentIndexCode = regionInfoDtos.get(i).getParentIndexCode();
            if(parentIndexCode.equals("-1")){
                String indexCode = regionInfoDtos.get(i).getIndexCode();
                String name = regionInfoDtos.get(i).getName();
                sysDept.setDeptUuid(indexCode);
                sysDept.setParentId("0");
                sysDept.setDeptName(name);
                int depeId=iSysDeptService.insertSysDept(sysDept);
                Long id = (Long) sysDept.getDeptId();
            }

        }
        for(int j=0;j<regionInfoDtos.size();j++){
            String parentIndexCode = regionInfoDtos.get(j).getParentIndexCode();
            if(!parentIndexCode.equals("-1")){
               List<SysDept> sysDepts= iSysDeptService.findsysDeptUuid(parentIndexCode);
               if(sysDepts.size()!=0){
                   Long deptId = sysDepts.get(0).getDeptId();
                   sysDept.setParentId(deptId.toString());
                   sysDept.setDeptUuid(regionInfoDtos.get(j).getIndexCode());
                   sysDept.setDeptName(regionInfoDtos.get(j).getName());
                   sysDept.setCreateTime(new Date());
                   int depeId=iSysDeptService.insertSysDept(sysDept);
                   Long id1 = (Long) sysDept.getDeptId();
                   List<UserDto> userDto = regionInfoDtos.get(j).getUserDto();
                   SysUser sysUser = new SysUser();
                   for(int m=0;m<userDto.size();m++){
                       String userId = userDto.get(m).getUserId();
                       String userName = userDto.get(m).getUserName();
                       String realName = userDto.get(m).getRealName();
                       String phoneNum = userDto.get(m).getPhoneNum();
                           String md5 = MD5Util.getMD5(userName + "123123");
                           sysUser.setDeptId(id1);
                           sysUser.setUserName(realName);
                           if(phoneNum == null){
                               sysUser.setPhonenumber("");
                           }else{
                               sysUser.setPhonenumber(phoneNum);
                           }
                           sysUser.setLoginName(userName);
                           sysUser.setPassword(md5);
                           sysUser.setCreateTime(new Date());
                           int userCount =iSysUserService.findUserCount(sysUser);
                           if(userCount == 0){
                               int user=iSysUserService.insertSysuser(sysUser);
                               Long userId1 = sysUser.getUserId();
                               SysUserRole sysUserRole = new SysUserRole();
                               sysUserRole.setUserId(userId1);
                               String roleId="2";
                               sysUserRole.setRoleId(Long.valueOf(roleId).longValue());
                               iSysUserService.insertUserRole(sysUserRole);
                           }
                   }

               }
            }
        }

        for(int y=0;y<regionInfoDtos.size();y++){
            String parentIndexCode = regionInfoDtos.get(y).getParentIndexCode();
            if(!parentIndexCode.equals("-1")){
                String indexCode = regionInfoDtos.get(y).getIndexCode();
                int count=iSysDeptService.findSysDeptCount(indexCode);
                if(count ==0){
                    List<SysDept> sysDepts= iSysDeptService.findsysDeptUuid(parentIndexCode);
                    if(sysDepts.size()!=0){
                        Long deptId = sysDepts.get(0).getDeptId();
                        sysDept.setParentId(deptId.toString());
                        sysDept.setDeptUuid(regionInfoDtos.get(y).getIndexCode());
                        sysDept.setDeptName(regionInfoDtos.get(y).getName());
                        sysDept.setCreateTime(new Date());
                        int depeId=iSysDeptService.insertSysDept(sysDept);
                        Long id2 = (Long) sysDept.getDeptId();
                        List<UserDto> userDto = regionInfoDtos.get(y).getUserDto();
                        SysUser sysUser = new SysUser();
                        for(int m=0;m<userDto.size();m++){
                            String userId = userDto.get(m).getUserId();
                            String userName = userDto.get(m).getUserName();
                            String realName = userDto.get(m).getRealName();
                            String phoneNum = userDto.get(m).getPhoneNum();
                            String md5 = MD5Util.getMD5(userName + "123123");
                            sysUser.setDeptId(id2);
                            sysUser.setUserName(realName);
                            if(phoneNum == null){
                                sysUser.setPhonenumber("");
                            }else{
                                sysUser.setPhonenumber(phoneNum);
                            }
                            sysUser.setLoginName(userName);
                            sysUser.setPassword(md5);
                            sysUser.setCreateTime(new Date());
                            int userCount =iSysUserService.findUserCount(sysUser);
                            if(userCount == 0){
                                int user=iSysUserService.insertSysuser(sysUser);
                                Long userId1 = sysUser.getUserId();
                                SysUserRole sysUserRole = new SysUserRole();
                                sysUserRole.setUserId(userId1);
                                String roleId="2";
                                sysUserRole.setRoleId(Long.valueOf(roleId).longValue());
                                iSysUserService.insertUserRole(sysUserRole);
                            }
                        }

                    }
                }
            }
        }
        for(int x=0;x<regionInfoDtos.size();x++){
            String parentIndexCode = regionInfoDtos.get(x).getParentIndexCode();
            if(!parentIndexCode.equals("-1")){
                String indexCode = regionInfoDtos.get(x).getIndexCode();
                int count=iSysDeptService.findSysDeptCount(indexCode);
                if(count ==0){
                    List<SysDept> sysDepts= iSysDeptService.findsysDeptUuid(parentIndexCode);
                    if(sysDepts.size()!=0){
                        Long deptId = sysDepts.get(0).getDeptId();
                        sysDept.setParentId(deptId.toString());
                        sysDept.setDeptUuid(regionInfoDtos.get(x).getIndexCode());
                        sysDept.setDeptName(regionInfoDtos.get(x).getName());
                        sysDept.setCreateTime(new Date());
                        int depeId=iSysDeptService.insertSysDept(sysDept);
                        Long id3 = (Long) sysDept.getDeptId();
                        List<UserDto> userDto = regionInfoDtos.get(x).getUserDto();
                        SysUser sysUser = new SysUser();
                        for(int m=0;m<userDto.size();m++){
                            String userId = userDto.get(m).getUserId();
                            String userName = userDto.get(m).getUserName();
                            String realName = userDto.get(m).getRealName();
                            String phoneNum = userDto.get(m).getPhoneNum();
                            String md5 = MD5Util.getMD5(userName + "123123");
                            sysUser.setDeptId(id3);
                            sysUser.setUserName(realName);
                            if(phoneNum == null){
                                sysUser.setPhonenumber("");
                            }else{
                                sysUser.setPhonenumber(phoneNum);
                            }
                            sysUser.setLoginName(userName);
                            sysUser.setPassword(md5);
                            sysUser.setCreateTime(new Date());
                            int userCount =iSysUserService.findUserCount(sysUser);
                            if(userCount == 0){
                                int user=iSysUserService.insertSysuser(sysUser);
                                Long userId1 = sysUser.getUserId();
                                SysUserRole sysUserRole = new SysUserRole();
                                sysUserRole.setUserId(userId1);
                                String roleId="2";
                                sysUserRole.setRoleId(Long.valueOf(roleId).longValue());
                                iSysUserService.insertUserRole(sysUserRole);
                            }
                        }

                    }
                }
            }
        }
        return AjaxResult.success();
    }

    public AjaxResult feedbck(EventAnalysisDTO eventAnalysisDTO,String evenid) {
        //???????????? ?????????
        EventInfoDTO eventInfoDTO = new EventInfoDTO();
        EventDTO eventDTO = new EventDTO();
        EventReportProviderDTO eventReportProviderDTO = new EventReportProviderDTO();
        HkEventInfo hkEventInfo = new HkEventInfo();
         hkEventInfo.setEventId(evenid);
        List<HkEventInfo> event=hkEventService.selectEventInfoAll(hkEventInfo);
        eventAnalysisDTO.setComponentId(event.get(0).getComponentId());
        eventDTO.setEventIndexCode(event.get(0).getEventIndexCode());
        eventDTO.setEventTitle(event.get(0).getEventTitle());
        eventDTO.setEventAddress(event.get(0).getEventAddress());
        eventDTO.setReportTime(event.get(0).getReportTime());
        eventDTO.setEventType(event.get(0).getEventType());
        eventDTO.setEventTypeName(event.get(0).getEventTypeName());
        eventDTO.setEventStatus(3);
        eventDTO.setEventStatusName("?????????");
        eventDTO.setEventSubStatus(event.get(0).getEventSubStatus());
        eventDTO.setEventSubStatusName(event.get(0).getEventSubStatusName());
        eventDTO.setReportType(event.get(0).getReportType());
        eventDTO.setReportTypeName(event.get(0).getReportTypeName());
        eventDTO.setLawEnforcementType(event.get(0).getLawEnforcementType());
        eventDTO.setLawEnforcementTypeName(event.get(0).getLawEnforcementTypeName());
        eventDTO.setCurrentProcessorId(event.get(0).getCurrentProcessorId());
        eventDTO.setCurrentProcessorName(event.get(0).getCurrentProcessorName());
        eventDTO.setEventImage(event.get(0).getEventImage());
        eventInfoDTO.setEvent(eventDTO);
        eventInfoDTO.setReportProvider(eventReportProviderDTO);
        eventInfoDTO.setRegionIndexCode(event.get(0).getRegionIndexCode());
        eventInfoDTO.setRegionName(event.get(0).getRegionName());
        eventInfoDTO.setPlaceType(event.get(0).getPlaceType());
        eventInfoDTO.setPlaceTypeName(event.get(0).getPlaceTypeName());
        eventInfoDTO.setPlaceIndexCode(event.get(0).getPlaceIndexCode());
        eventInfoDTO.setPlaceName(event.get(0).getPlaceName());
        eventInfoDTO.setExtendStr1(event.get(0).getExtendStr1());
        eventAnalysisDTO.setEventInfo(eventInfoDTO);
        EventProcessRecordDTO eventProcessRecordDTO=new EventProcessRecordDTO();
        HKrecord hk=new HKrecord();
        hk.setEventId(evenid);
        List<HKrecord> hKrecord=appEventDetailService.selectPro(hk);

        List<EventProcessRecordDTO> lists=new ArrayList<>();

        eventProcessRecordDTO.setExtendInt1(hKrecord.get(0).getExtendInt1());
        eventProcessRecordDTO.setExtendInt2(hKrecord.get(0).getExtendInt2());
        eventProcessRecordDTO.setExtendInt3(hKrecord.get(0).getExtendInt3());
        eventProcessRecordDTO.setExtendJson(hKrecord.get(0).getExtendjson());
//        eventProcessRecordDTO.setExtendStr1(hKrecord.get(i).getExtendStr1().toString());
        eventProcessRecordDTO.setExtendStr2(hKrecord.get(0).getExtendStr2());
        eventProcessRecordDTO.setExtendStr3(hKrecord.get(0).getExtendStr3());
        eventProcessRecordDTO.setHandlerName(hKrecord.get(0).getHandlerName());
        eventProcessRecordDTO.setProcessResult(appEventDetailService.result(hKrecord.get(0).getRecordId()));
//        eventProcessRecordDTO.setProcessStatus(Integer.parseInt(hKrecord.get(i).getProcessStatus()));
        eventProcessRecordDTO.setProcessStatusName(hKrecord.get(0).getProcessStatusName());
        eventProcessRecordDTO.setRegionName(hKrecord.get(0).getRegionName());
        eventProcessRecordDTO.setHandlerPhone(hKrecord.get(0).getHandlerPhone());
//        eventProcessRecordDTO.setHandlerIndexCode(hKrecord.get(0).getHandlerIndexCode());
//        eventProcessRecordDTO.setProcessTime(hKrecord.get(i).getProcessTime().toString());
        eventProcessRecordDTO.setHandlerRecordIndexCode(UUID.randomUUID().toString().trim().replaceAll("-", ""));
         lists.add(eventProcessRecordDTO);
        eventAnalysisDTO.setProcessRecord(lists);
        return AjaxResult.success();
    }


    //?????????????????????????????????
    @PostMapping("/applyParkingArea")
    @ResponseBody
public String applyParkingArea(){

        ArtemisConfig.host=HkwsConfig.host;
        ArtemisConfig.appKey = HkwsConfig.appKey;
        ArtemisConfig.appSecret = HkwsConfig.appSecret;
        final String getSecurityApi = HkwsConfig.applyparkingarea; // ????????????
        Map<String, String> path = new HashMap<String,String>(2) {
            {
                put("https://", getSecurityApi);
            }
        };
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("userId","");
        jsonBody.put("imageUrls","http://10.13.172.10:6120/pic?aiuhrdhejasfdgghggh23dsasda");
        jsonBody.put("longitude", 120.1643513);
        jsonBody.put("latitude",30.1685413);
        jsonBody.put("applyAddressName","test");
        String body = jsonBody.toJSONString();
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null,
                null,"application/json");
        return result;
    }



    public void geteventcg(management passinfo) {
        for(int i=0;i<=passinfo.getData().getList().size()-1;i++){
            String id = passinfo.getData().getList().get(i).getId();
            int count=hkEventService.queryEventEventIndexCode(id);
            if(count==0){
                HkEventInfo hkEventInfo = new HkEventInfo();
                //????????????????????????
                hkEventInfo.setEventId(UUID.randomUUID().toString().trim().replaceAll("-", "").trim().replaceAll("-", "").trim().replaceAll("-", ""));
                //?????????????????????
                hkEventInfo.setEventIndexCode(passinfo.getData().getList().get(i).getId());
                //????????????
                long collectTime = passinfo.getData().getList().get(i).getCollectTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String format = dateFormat.format(collectTime);
                hkEventInfo.setReportTime(format);
                Date date = new Date(collectTime);
                hkEventInfo.setCreateTime(date);
                System.out.println(date);
                //????????????
                hkEventInfo.setEventType(passinfo.getData().getList().get(i).getIntelligentType());
                //??????????????????
                hkEventInfo.setEventTypeName(passinfo.getData().getList().get(i).getIntelligentTypeName());
                //???????????????
                hkEventInfo.setCameraIndexCode(passinfo.getData().getList().get(i).getIndexCode());
                //???????????????
                hkEventInfo.setCameraName(passinfo.getData().getList().get(i).getCameraName());
                hkEventInfo.setEventAddress(passinfo.getData().getList().get(i).getCameraName());
                //??????
                hkEventInfo.setEventTitle(passinfo.getData().getList().get(i).getIntelligentTypeName());
                //??????
                hkEventInfo.setEventImage(passinfo.getData().getList().get(i).getPicUrl());
                //?????????
                hkEventInfo.setEventThumbnailImage(passinfo.getData().getList().get(i).getThumbnailUrl());
                //????????????
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                String format1 = dateFormat1.format(new Date());
                hkEventInfo.setEventCreateTime(format1);
                //??????
                hkEventInfo.setEventAlertStatus(2);
                hkEventInfo.setEventAlertStatusName("?????????");
                //???????????????. ??????-??????
                double[] convertPoint = GPSUtils.gcj02_To_Bd09(Double.parseDouble(passinfo.getData().getList().get(i).getLatitude()), Double.parseDouble(passinfo.getData().getList().get(i).getLongitude()));
//                System.out.println(convertPoint[1]+"----------"+convertPoint[0]);
                Double[] points = {convertPoint[1], convertPoint[0]};
                Map<String, Object> map1 = hkMapService.calculateRegionByLongitudeAndLatitude(points);
                String regionCode = map1.get("regionId") + "";
                String regionName = map1.get("regionName") + "";
                System.out.println(regionCode+"------------"+regionName);
                //??????
                hkEventInfo.setLongitude(convertPoint[1]+"");
                //??????
                hkEventInfo.setLatitude(convertPoint[0]+"");
                //??????code
                hkEventInfo.setRegionIndexCode(regionCode);
                //????????????
                hkEventInfo.setRegionName(regionName);
                //?????????
                String plateNo = passinfo.getData().getList().get(i).getPlateNo();
                if(plateNo!=null){
                    hkEventInfo.setPlateNo(plateNo);
                }
                //1001???????????? 100  1002????????????
                hkEventInfo.setComponentId("1001");
                //????????????
                hkEventInfo.setReportType(1);
                hkEventInfo.setReportTypeName("????????????");
                hkEventInfo.setEventSourceName("CM??????");
                //??????????????????
                hkEventInfo.setEventCategory(Integer.parseInt(passinfo.getData().getList().get(i).getOrgCode()));
                //??????????????????
                hkEventInfo.setEventCategoryName(passinfo.getData().getList().get(i).getOrgName());
                hkEventService.addHKEventInfo(hkEventInfo);
            }
        }
    }

   /* @RequestMapping("zuobiao")
    @ResponseBody
    public AjaxResult zuobiao(){
        String Latitude="30.222858";
        String Longitude="119.98463";
        //???????????????. ??????-??????
        double[] convertPoint = GPSUtils.gcj02_To_Bd09(Double.parseDouble(Latitude), Double.parseDouble(Longitude));
        System.out.println(convertPoint[1]+"----------"+convertPoint[0]);
        Double[] points = {convertPoint[1], convertPoint[0]};
        Map<String, Object> map1 = hkMapService.calculateRegionByLongitudeAndLatitude(points);
        String regionCode = map1.get("regionId") + "";
        String regionName = map1.get("regionName") + "";
        System.out.println(regionCode+"------------"+regionName);
        return AjaxResult.success();
    }*/

   @RequestMapping(value = "ZheLiFang",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult ceshi1(@RequestBody ZheLiFangDto zheLiFangDto){
       String storeid = zheLiFangDto.getStoreid();
       int count = hkEventService.queryEventEventIndexCode(storeid);
       if(count == 0){
           HkEventInfo hkEventInfo = new HkEventInfo();
           //????????????????????????
           hkEventInfo.setEventId(UUID.randomUUID().toString().trim().replaceAll("-", "").trim().replaceAll("-", "").trim().replaceAll("-", ""));
           //????????????????????????
           hkEventInfo.setEventIndexCode(zheLiFangDto.getStoreid());
           //????????????
           hkEventInfo.setComponentId("1002");
           //??????????????????
           hkEventInfo.setEventType("1102");
           //??????????????????
           hkEventInfo.setEventTypeName("???????????????");
           //????????????
           hkEventInfo.setCompanyContactInformation(zheLiFangDto.getSjhm());
           //?????????
           hkEventInfo.setCompanyLegalPerson(zheLiFangDto.getXm());
           //????????????
           hkEventInfo.setReportType(2);
           //??????????????????
           hkEventInfo.setReportTypeName("????????????");
           //????????????
           hkEventInfo.setEventAlertStatus(2);
           //??????????????????
           hkEventInfo.setEventAlertStatusName("?????????");
           //????????????
           hkEventInfo.setCameraName(zheLiFangDto.getQx());
           hkEventInfo.setEventAddress(zheLiFangDto.getQx());
           //??????
           hkEventInfo.setCreateTime(new Date());
           //????????????
           hkEventInfo.setReportTime(zheLiFangDto.getXfrq());
           hkEventInfo.setEventCreateTime(zheLiFangDto.getXfrq());
           //????????????
           hkEventInfo.setEventTitle(zheLiFangDto.getGk());
           hkEventService.addHKEventInfo(hkEventInfo);
       }
       return AjaxResult.success();
   }
}
