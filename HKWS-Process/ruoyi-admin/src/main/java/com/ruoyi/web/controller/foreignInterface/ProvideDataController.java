package com.ruoyi.web.controller.foreignInterface;


import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicResponseParameters;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.EventTypeStatus;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.map.GPSUtils;
import com.ruoyi.quartz.domain.SysJobLog;
import com.ruoyi.quartz.service.ISysJobLogService;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Action.HkActionProcess;
import com.ruoyi.system.domain.Daping.PercentResult;
import com.ruoyi.system.domain.Daping.PopulaStatiInfo;
import com.ruoyi.system.domain.Daping.ShowEventVo;
import com.ruoyi.system.domain.HkEarlyWarning.HkEventInfo;
import com.ruoyi.system.domain.HkEarlyWarning.SysRanksVo;
import com.ruoyi.system.domain.HkEarlyWarning.SysUserVo;
import com.ruoyi.system.domain.HkEntity.EventBackVo;
import com.ruoyi.system.domain.HkEntity.EventReportVo;
import com.ruoyi.system.domain.HkEntity.PointDTO;
import com.ruoyi.system.domain.app.AppActionInfo;
import com.ruoyi.system.domain.app.appField;
import com.ruoyi.system.domain.platform.gpsWeigh.XlGpsWeigh;
import com.ruoyi.system.domain.property.XlPropertyAndVillageVo;
import com.ruoyi.system.domain.vo.CameraIndexCodeVo;
import com.ruoyi.system.domain.vo.EventListCountVo;
import com.ruoyi.system.service.*;
import com.ruoyi.system.service.HkEarlyWarning.HkEventRecordService;
import com.ruoyi.system.service.HkEarlyWarning.HkEventService;
import com.ruoyi.system.service.app.AppEventDetailService;
import com.ruoyi.system.service.app.AppMyWorkService;
import com.ruoyi.system.service.garbageCar.LocationReqService;
import com.ruoyi.system.service.property.XlPropertyAndVillageVoService;
import com.ruoyi.system.service.statistics.HkEventStatisticsService;
import com.ruoyi.web.controller.common.utils.TimeField;
import com.ruoyi.web.controller.common.utils.TimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/hkapi")
@Api(tags = "????????????")
public class ProvideDataController extends BaseController {

    @Autowired
    private ISysRanksService ranksService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private HkEventService eventService;

    @Autowired
    private HkEventStatisticsService statisticsService;

    @Autowired
    private ISysJobLogService jobLogService;

    @Autowired
    private IHkMapService hkMapService;

    @Autowired
    private MirgantPopulationService populationService;

    @Autowired
    private IHkRentHouseInfoService houseInfoService;

    @Autowired
    private ISysConfigService configService;
    //??????
    @Autowired
    private ISysRegionService iSysRegionService;
    //??????
    @Autowired
    private IXlPersonnelService iXlPersonnelService;
    //??????
    @Autowired
    private IXlRoomService iXlRoomService;
    //??????
    @Autowired
    private IXlEnterpriseInfoService iXlEnterpriseInfoService;
    @Autowired
    private IXlEquipmentInfoService iXlEquipmentInfoService;
    //??????
    @Autowired
    private IXlEstateManagementService estateManagementService;
    //??????
    @Autowired
    private IXlVillageService villageService;
    //????????????
    @Autowired
    private XlPropertyAndVillageVoService propertyEventService;
    //?????????
    @Autowired
    private IXlLeaseService xlLeaseService;
    //?????????
    @Autowired
    private LocationReqService locationReqService;
    @Autowired
    private HkEventRecordService hkEventRecordService;
    @Autowired
    private AppMyWorkService appMyWorkService;
    @Autowired
    private AppEventDetailService appEventDetailService;

    @Value("${token.plat.zhzl}")
    private String zhzlToken;

    @Value("${token.plat.cgsj}")
    private String cgsjToken;

    @Value("${token.plat.yjxf}")
    private String yjxfToken;

    @Value("${token.plat.qsy}")
    private String qsyToken;

    @Value("${img.file-url}")
    private String fileUrl;

    @Value("${img.event-img-path}")
    private String imgPath;

    @Value("${replace.target-url.part4}")
    private String targetUrl;

    /**???????????????**/
    @Value("${platform.cg.eventType.motorVehicle}")
    private String motorVehicleCode;

    /**
     * ????????????
     * @param vo
     * @return
     */
    @GetMapping("/hkdata/queryRanks")
    @ResponseBody
    public AjaxResult queryRanks(SysRanksVo vo){
        /*String[] arr = new String[]{zhzlToken,cgsjToken,yjxfToken,qsyToken};
        Map<String, Object> map = new CheckApiUtil().checkApi(arr);
        if(!(Boolean)map.get("success")){
            return AjaxResult.error((String)map.get("msg"));
        }*/
        if(vo.getPageNum() == null || vo.getPageNum() == 0){
            vo.setPageNum(1);
        }
        if(vo.getPageSize() == null || vo.getPageSize() > 10){
            vo.setPageSize(10);
        }
        return AjaxResult.success(ranksService.queryRanks(vo));
    }

    /**
     * ????????????
     * @param vo
     * @return
     */
    @GetMapping("/hkdata/queryUser")
    @ResponseBody
    public AjaxResult queryUser(SysUserVo vo){
        /*String[] arr = new String[]{zhzlToken,cgsjToken,yjxfToken,qsyToken};
        Map<String, Object> map = new CheckApiUtil().checkApi(arr);
        if(!(Boolean)map.get("success")){
            return AjaxResult.error((String)map.get("msg"));
        }*/
        if(vo.getPageNum() == null || vo.getPageNum() == 0){
            vo.setPageNum(1);
        }
        if(vo.getPageSize() == null){
            vo.setPageSize(10);
        }
        return AjaxResult.success(userService.queryUser(vo));
    }

    /**
     * ????????????
     * @param vo
     * @return
     */
    @PostMapping("/appEventInfo/addReportEvent")
    @ResponseBody
    public AjaxResult reportEvent(@RequestBody EventReportVo vo){
        String[] arr = new String[]{zhzlToken,cgsjToken,yjxfToken,qsyToken};
        Map<String, Object> map = new CheckApiUtil().checkApi(arr);
        if(!(Boolean)map.get("success")){
            return AjaxResult.error((String)map.get("msg"));
        }
        HkEventInfo event = vo.getEvent();
        PointDTO point = vo.getPoint();
        logger.info("--------------------????????????????????????-----------------------");
        logger.info(JSONObject.toJSONString(vo));
        SysJobLog joblog = new SysJobLog();
        joblog.setInvokeTarget("????????????");
        joblog.setJobMessage("????????????:"+ JSONObject.toJSONString(vo));
        joblog.setJobName("??????"+map.get("platName")+"??????");
        joblog.setJobGroup("????????????");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(event == null){
            joblog.setStatus("1");
            joblog.setExceptionInfo("?????????????????????????????????");
            jobLogService.addJobLog(joblog);
            return AjaxResult.error("????????????????????????");
        }
        event.setEventId(UUID.randomUUID().toString().replaceAll("-",""));
        if(StringUtils.isEmpty(event.getEventTitle())){
            joblog.setStatus("1");
            joblog.setExceptionInfo("?????????????????????????????????");
            jobLogService.addJobLog(joblog);
            return AjaxResult.error("????????????????????????");
        }
        if(StringUtils.isEmpty(event.getEventAddress())){
            joblog.setStatus("1");
            joblog.setExceptionInfo("?????????????????????????????????");
            jobLogService.addJobLog(joblog);
            return AjaxResult.error("????????????????????????");
        }
        if(StringUtils.isEmpty(event.getReportTime())){
            joblog.setStatus("1");
            joblog.setExceptionInfo("???????????????????????????????????????");
            jobLogService.addJobLog(joblog);
            return AjaxResult.error("??????????????????????????????");
        }
        Date eventCreateTime = DateUtils.parseDate(event.getReportTime());
        event.setEventCreateTime(dateFormat.format(eventCreateTime));
        String componentId = map.get("platCode")+"";
        event.setComponentId(componentId);
        //1001???????????? 1002???????????? 1003???????????? 1004?????????
        /*****************????????????????????????ip?????????????????????????????? ??????????????????*********************/
        if("1003".equals(componentId) && !StringUtils.isEmpty(event.getEventImage())){
            event.setEventImage(UrlReplaceUtil.replaceFileUrl(event.getEventImage()));
            if(event.getExtendInt3() == null){
                event.setExtendInt3(3);//???????????????????????????
            }
        }else if("1001".equals(componentId) && !StringUtils.isEmpty(event.getEventImage())){
            /*String filePath = new UploadUrlUtil().getFilePath(event.getExtendInt1()+"",event.getEventImage(),imgPath,targetUrl);
            event.setEventImage(filePath);*/
            event.setEventImage(UrlReplaceUtil.replaceFileUrl(event.getEventImage()));
            if(event.getExtendInt3() == null){
                event.setExtendInt3(1);//???????????????????????????
            }
        }else if("1002".equals(componentId) && !StringUtils.isEmpty(event.getEventImage())) {
            event.setEventImage(UrlReplaceUtil.replaceFileUrl(event.getEventImage()));
            if(event.getExtendInt3() == null){
                event.setExtendInt3(2);//???????????????????????????
            }
        }else if("1004".equals(componentId)){
            if(event.getExtendInt3() == null){
                event.setExtendInt3(4);//???????????????????????????
            }
            //????????????????????????????????????????????????
            if(point != null && !StringUtils.isEmpty(point.getLongitude()) && !StringUtils.isEmpty(point.getLatitude())){
                double[] doubles = GPSUtils.gcj02_To_Bd09(Double.valueOf(point.getLongitude()), Double.valueOf(point.getLatitude()));
                point.setLongitude(doubles[0]+"");
                point.setLatitude(doubles[1]+"");
            }
        }

        if(event.getReportType() == null){
            return AjaxResult.error("??????????????????????????????");
        }
        if(StringUtils.isEmpty(event.getReportTypeName())){
            return AjaxResult.error("????????????????????????????????????");
        }
        if("????????????".equals(event.getReportTypeName())){
            event.setReportType(1);
            if(StringUtils.isEmpty(event.getCameraIndexCode())){
                return AjaxResult.error("??????ID????????????");
            }
            if(StringUtils.isEmpty(event.getCameraName())){
                return AjaxResult.error("????????????????????????");
            }
        }else{
            event.setReportType(2);
        }
        if(StringUtils.isNull(event.getRiskLevel())){
            return AjaxResult.error("??????????????????????????????");
        }

        if(event.getEventStatus()==null || event.getEventStatus() == 1){
            event.setEventStatus(5);
            event.setEventAlertStatus(2);
            event.setEventAlertStatusName("?????????");
        }
        else if(event.getEventStatus() == 3){
            event.setEventAlertStatus(6);
            event.setEventAlertStatusName("??????");
            //?????????????????????update_time
        }
        if(StringUtils.isEmpty(event.getEventIndexCode())){
            return AjaxResult.error("??????????????????????????????");
        }
        if("1001".equals(componentId) && motorVehicleCode.equals(event.getEventType())){
            if(StringUtils.isEmpty(event.getPlateNo())){
                joblog.setStatus("1");
                joblog.setExceptionInfo("????????????????????????????????????");
                jobLogService.addJobLog(joblog);
                return AjaxResult.error("???????????????????????????");
            }
        }
        String regionCode = "100";
        String regionName = "???????????????";
        if(point != null){
            if(!StringUtils.isEmpty(point.getLongitude())){
                event.setLongitude(point.getLongitude());
            }
            if(!StringUtils.isEmpty(point.getLatitude())){
                event.setLatitude(point.getLatitude());
            }
            if(!StringUtils.isEmpty(point.getLongitude()) && !StringUtils.isEmpty(point.getLatitude())){
                Double[] points = {Double.valueOf(point.getLongitude()),Double.valueOf(point.getLatitude())};
                Map<String, Object> map1 = hkMapService.calculateRegionByLongitudeAndLatitude(points);
                regionCode = map1.get("regionId")+"";
                regionName = map1.get("regionName")+"";
            }
        }
        event.setRegionIndexCode(regionCode);
        event.setRegionName(regionName);
        event.setCreateTime(new Date());
        try{
            int count = eventService.reportEvent(event);
            if(count != 1){
                joblog.setInvokeTarget("??????????????????");
                joblog.setStatus("1");
                joblog.setExceptionInfo("?????????????????????????????????");
                jobLogService.addJobLog(joblog);
                logger.info("--------------------??????????????????-----------------------");
                return AjaxResult.error("????????????");
            }
        }catch(Exception ex){
            joblog.setStatus("1");
            joblog.setExceptionInfo("???????????????"+ex.getMessage());
            jobLogService.addJobLog(joblog);
            ex.printStackTrace();
            return AjaxResult.error("????????????");
        }
        return AjaxResult.success("????????????");
    }

    /**
     * ????????????
     * @param vo
     * @return
     */
    @GetMapping("/appEventInfo/eventResult")
    @ResponseBody
    public AjaxResult eventResult(EventBackVo vo) {
        /**
         * {
         *      detail:{
         *          eventIndexCode:"",
         *          record:[
         *              processUserId:"1",
         *              processTime:"2021-05-14 00:00:01",
         *              processStatus:"?????????1???????????? 2 ?????????3?????????4????????? 5??????,6???????????????7?????????8???????????????"
         *              processResult:"?????????"???
         *              processOpinion:""
         *          ]
         *      },
         * }
         */
        String[] arr = new String[]{zhzlToken, cgsjToken, yjxfToken, qsyToken};
        Map<String, Object> map = new CheckApiUtil().checkApi(arr);
        if (!(Boolean) map.get("success")) {
            return AjaxResult.error((String) map.get("msg"));
        }
        if(vo.getPageNum() == null || vo.getPageNum() == 0){
            vo.setPageNum(1);
        }
        if(vo.getPageSize() == null){
            vo.setPageSize(10);
        }
        if(vo.getPageSize() > 50){
            vo.setPageSize(50);
        }
        vo.setComponentId((String)map.get("platCode"));
        PageInfo<EventBackVo> pageInfo = eventService.queryEventResult(vo);
        return AjaxResult.success(pageInfo);
    }




    private Double divideValue(BigDecimal value,BigDecimal toValue){
        Double divideValue = 0.0;
        if(toValue.compareTo(BigDecimal.ZERO) < 1){
            return  100.00;
        }
        divideValue = value.divide(toValue,2,BigDecimal.ROUND_UP).doubleValue();//?????????
        return divideValue;
    }

    /**
     * ?????????-???????????????/??????/??????
     * @return
     */
    @GetMapping("index/eventCountAndRate")
    @ResponseBody
    public AjaxResult eventTypeRate(){
        String componentId = "";//????????????

        /**
         * 1001-???????????? 1002-?????????????????? 1003-???????????? 1004-???????????????
         */
        Map<String,Object> params1 = new HashMap<>();
        params1.put("componentId","1001");
        PercentResult plat_1001 = statisticsService.eventCountAndRate(params1);//?????????-?????????/??????/?????????
        Map<String,Object> params2 = new HashMap<>();
        params2.put("componentId","1002");
        PercentResult plat_1002 = statisticsService.eventCountAndRate(params2);//?????????-?????????/??????/?????????
        Map<String,Object> params3 = new HashMap<>();
        params3.put("componentId","1003");
        PercentResult plat_1003 = statisticsService.eventCountAndRate(params3);//?????????-?????????/??????/?????????
        Map<String,Object> params4 = new HashMap<>();
        params4.put("componentId","1004");
        PercentResult plat_1004 = statisticsService.eventCountAndRate(params4);//?????????-?????????/??????/?????????

        List<PercentResult> result= new ArrayList<>();
        result.add(plat_1001);
        result.add(plat_1002);
        result.add(plat_1003);
        result.add(plat_1004);
        return  AjaxResult.success(result);
    }

    /***
     *  ??????????????????
     * ?????????????????? top5
     */
    @GetMapping("index/getNewEvent")
    @ResponseBody
    public AjaxResult getNewEvent(@RequestParam(value = "Component_Code",required = false) String componentId) {
        Map<String,Object> result = new HashMap<>();
        //??????????????????????????????
        Map<String,Object> eventParams = DateUtils.eventDate("day");
        eventParams.put("componentId",componentId);//?????????????????????
        eventParams.put("statusArr",new String[]{"2"});//????????????
        List<ShowEventVo> list1 = statisticsService.getNewEvent(eventParams);//???????????????
        result.put("noProcess",list1);
        eventParams.put("statusArr",new String[]{"6","7","9"});//????????????
        List<ShowEventVo> list2 = statisticsService.getNewEvent(eventParams);//???????????????
        result.put("process",list2);
        return AjaxResult.success(result);
    }


    /***
     * ????????????
     * ??????--????????????
     *
     */
    @GetMapping("index/energizeAll")
    @ResponseBody
    public AjaxResult energizeAll(@RequestParam(value = "today",required = false) String today) {
        //???????????????????????????
        Map<String,Object> result = new HashMap<>();
        List<PercentResult> part_2 = new ArrayList<>();
        //????????????????????????
        Map<String,Object> params1 = new HashMap<>();
        params1.put("componentId","1001");
        if("1".equals(today)){
            params1.put("currentDate",DateUtils.getDate());
        }
        PercentResult plat_1001 = statisticsService.eventCountAndRate(params1);//?????????-?????????/??????/?????????
        Map<String,Object> params2 = new HashMap<>();
        params2.put("componentId","1002");
        if("1".equals(today)){
            params2.put("currentDate",DateUtils.getDate());
        }
        PercentResult plat_1002 = statisticsService.eventCountAndRate(params2);//?????????-?????????/??????/?????????
        Map<String,Object> params3 = new HashMap<>();
        params3.put("componentId","1003");
        if("1".equals(today)){
            params3.put("currentDate",DateUtils.getDate());
        }
        PercentResult plat_1003 = statisticsService.eventCountAndRate(params3);//?????????-?????????/??????/?????????
        Map<String,Object> params4 = new HashMap<>();
        params4.put("componentId","1004");
        if("1".equals(today)){
            params4.put("currentDate",DateUtils.getDate());
        }
        PercentResult plat_1004 = statisticsService.eventCountAndRate(params4);//?????????-?????????/??????/?????????
        List<PercentResult> part_1= new ArrayList<>();
        part_1.add(plat_1001);
        part_1.add(plat_1002);
        part_1.add(plat_1003);
        part_1.add(plat_1004);
        List<PercentResult> list = statisticsService.queryCountByEventType(DateUtils.eventDate("day"));
        Long other = 0L;
        Long another = 0L;
        list.sort(Comparator.comparing(PercentResult::getValue).reversed());
        for(int i=0;i<list.size();i++){
            if(list.size() > 4 && i > 4){
                //????????????????????????????????????
                Long value = list.get(i).getValue();
                other += value;
            }else{
                Long value = list.get(i).getValue();
                another += value;
                part_2.add(list.get(i));
            }
        }
        PercentResult param = new PercentResult();
        param.setName("other");
        param.setValue(other);
        part_2.add(param);
        for(PercentResult dto : part_2){
            Long count = other+another;
            Double percent = rate(new BigDecimal(dto.getValue()), new BigDecimal(count));
            dto.setPercent(percent);
        }
        result.put("part_1",part_1);
        result.put("part_2",part_2);
        return AjaxResult.success(result);
    }


    /***
     * ????????????
     * ??????--????????????
     *
     */
    @GetMapping("index/transferAll")
    @ResponseBody
    public AjaxResult transferAll(@RequestParam(value = "Component_Code",required = false) String componentId) {
        Map<String,Object> eventParams = DateUtils.eventDate("day");
        eventParams.put("componentId",componentId);
        return AjaxResult.success(statisticsService.queryCountByAlertStatus(eventParams));
    }

    /***
     * ????????????
     * ??????--????????????
     *
     */
    @GetMapping("index/governAll")
    @ResponseBody
    public AjaxResult governAll() {
        Map<String,Object> result = new HashMap<>();
        String twentyFour= DateFormatUtils.format(DateUtils.getTwentyFour(),"yyyy-MM-dd HH:mm:ss");//??????24??????????????????
        String createTime = DateFormatUtils.format(new Date(),"yyyy-MM-dd");//????????????
        try{
            Map<String,Object> params = new HashMap<>();
            params.put("statusArr",new String[]{"6","7","8","9"});//???????????????
            List<Integer> intList = new ArrayList<>();
            for(long i = 1L;i <= 24L; i++) {
                params.put("hourBegin",twentyFour);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//????????????
                long time = sdf.parse(twentyFour).getTime()+3600000L;
                String nextTime= DateFormatUtils.format(new Date(time),"yyyy-MM-dd HH:mm:ss");//???????????????????????????
                params.put("hourEnd",nextTime);
                Integer doneCount = statisticsService.countAllByParams(params);
                twentyFour = nextTime;
                intList.add(doneCount);
            }

            Map<String,Object> curretParams = new HashMap<>();
            curretParams.put("currentDate",createTime);//??????
            curretParams.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            int currentCount = statisticsService.countAllByParams(curretParams);//??????????????????
            Map<String,Object> doneParams = new HashMap<>();
            doneParams.put("currentDate",createTime);//??????
            doneParams.put("statusArr",new String[]{"6","7","8","9"});//???????????????
            Integer doneCount = statisticsService.countAllByParams(doneParams);//????????????????????????
            Double doneRate = rate(new BigDecimal(doneCount),new BigDecimal(currentCount));//?????????????????????
            Map<String,Object> param1 = new HashMap<>();
            param1.put("currentDate",createTime);//??????
            param1.put("statusArr",new String[]{"6","7","9"});
            param1.put("notEventStatus",3);
            int machineDis = statisticsService.countAllByParams(param1);
            result.put(EventTypeStatus.EVENT_DONE_WAY01.desc(),rate(new BigDecimal(machineDis+""),new BigDecimal(doneCount+"")));//????????????
            Map<String,Object> param2 = new HashMap<>();
            param2.put("currentDate",createTime);//??????
            param2.put("eventAlterStatus",8);
            int errorDis = statisticsService.countAllByParams(param2);
            result.put(EventTypeStatus.EVENT_DONE_WAY02.desc(),rate(new BigDecimal(errorDis+""),new BigDecimal(doneCount+"")));//????????????
            Map<String,Object> param3 = new HashMap<>();
            param3.put("currentDate",createTime);//??????
            param3.put("eventStatus",3);
            int centerDis = statisticsService.countAllByParams(param3);//????????????
            result.put(EventTypeStatus.EVENT_DONE_WAY03.desc(),rate(new BigDecimal(centerDis+""),new BigDecimal(doneCount+"")));//????????????
            /*Map<String,Object> param4 = new HashMap<>();
            param4.put("eventAlterStatus",6);
            param4.put("notEventStatus",3);
            int lawDis = statisticsService.countAllByParams(param4);//????????????*/
            int lawDis = 0;
            result.put(EventTypeStatus.EVENT_DONE_WAY04.desc(),rate(new BigDecimal(lawDis+""),new BigDecimal(doneCount+"")));//????????????
            result.put("doneRate",doneRate);
            result.put("activeCount",intList);
        }catch(Exception ex){
            logger.info(ex.getMessage());
            ex.printStackTrace();
        }
        return AjaxResult.success(result);
    }


    /***
     *  ??????????????????
     * ????????????-?????????-???24???????????????????????????
     */
    @GetMapping("index/cityEye")
    @ResponseBody
    public AjaxResult getCityEye(@RequestParam(value = "Component_Code",required = false) String componentId) {
        String twentyFour= DateFormatUtils.format(DateUtils.getTwentyFour(),"yyyy-MM-dd HH:mm:ss");//??????24??????????????????
        String createTime = DateFormatUtils.format(new Date(),"yyyy-MM-dd");//????????????
        Map<String,Object> response = new HashMap<>();
        try{
            Map<String,Object> twentyFourParams = new HashMap<>();
            twentyFourParams.put("lastDay",twentyFour);//??????24????????????
            twentyFourParams.put("componentId",componentId);//??????????????????
            twentyFourParams.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            List<HkEventInfo> list = statisticsService.getEventByParams(twentyFourParams);//??????24??????????????????
            List<TimeField> timeUtils = new TimeUtil().getTime("24hours");
            List<Integer> twentyFourCounts = DataFormat(list,null, timeUtils);//?????????

            Map<String,Object> countParams = new HashMap<>();
            countParams.put("componentId",componentId);
            countParams.put("currentDate",createTime);//?????????
            countParams.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            Integer count = statisticsService.countAllByParams(countParams);//???????????????

            Map<String,Object> exceptionParams = new HashMap<>();
            exceptionParams.put("eventAlterStatus","8");//????????????
            exceptionParams.put("currentDate",createTime);//?????????
            Integer exceptionCount = statisticsService.countAllByParams(exceptionParams);

            Map<String,Object> transferParams = new HashMap<>();
            transferParams.put("statusArr",new String[]{"3","4","5","6","7","8","9"});//?????????
            transferParams.put("currentDate",createTime);//?????????
            Integer changeCount = statisticsService.countAllByParams(transferParams);

            Double changeRate = rate(new BigDecimal(changeCount),new BigDecimal(count));//???????????????

            Map<String,Object> signParams = new HashMap<>();
            signParams.put("currentDate",createTime);//?????????
            signParams.put("eventAlterStatus","5");//????????????
            Integer signCount = statisticsService.countAllByParams(signParams);//???????????????
            //Double signRate = rate(new BigDecimal(signCount),new BigDecimal(count));//???????????????

            Map<String,Object> closeParams = new HashMap<>();
            closeParams.put("statusArr",new String[]{"6","7","9"});//????????????
            closeParams.put("currentDate",createTime);//?????????
            Integer closeCount = statisticsService.countAllByParams(closeParams);
            response.put("twentyFourCounts",twentyFourCounts);       //???24???????????????????????????
            response.put("changeRate",changeRate);  //???????????????
            response.put("signCount",signCount);    //???????????????
            response.put("closeCount",closeCount);  //???????????????
            response.put("exceptionCount",exceptionCount);//????????????????????????
        }catch(Exception ex){
            logger.info(ex.getMessage());
            ex.printStackTrace();
        }
        return AjaxResult.success(response);
    }


    /***
     *  ??????????????????
     * ????????????-????????????????????????????????????
     *
     */
    @GetMapping("index/DistributionOfMember")
    @ResponseBody
    public AjaxResult getDistributionOfMember() {
        //??????????????????????????????????????????

        Map<String,Object> param = new HashMap<>();
        statisticsService.getDistributionOfMember(param);
        List<PercentResult> distributionOfMemberVOS = deptService.getUserByDeptLevel();
        return AjaxResult.success(distributionOfMemberVOS);
    }


    /**
     * ????????????
     *
     */
    @GetMapping("index/voxPopuliNonstop")
    @ResponseBody
    public AjaxResult voxPopuliNonstop(@RequestParam("Component_Code") String componentId) {
        Map<String, Object> param = DateUtils.eventDate("day");//????????????
        param.put("componentId",componentId);
        param.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
        return AjaxResult.success(statisticsService.getMapByEventType(param));
    }



    /**
     * ????????????
     *
     */
    @GetMapping("index/thingPopuliNonstop")
    @ResponseBody
    public AjaxResult thingPopuliNonstop(@RequestParam("Component_Code") String componentId) {
        Map<String, Object> param = new HashMap<>();
        param.put("componentId",componentId);
        param.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
       return AjaxResult.success(statisticsService.getMouthEventCount(param));
    }


    /**
     * ????????????
     */
    @GetMapping("index/popularWillNonstop")
    @ResponseBody
    public AjaxResult popularWillNonstop(@RequestParam("Component_Code") String componentId) {
        Map<String, Object> param = DateUtils.eventDate("day");//????????????
        param.put("componentId",componentId);
       return AjaxResult.success(statisticsService.getEventDoneInfo(param));
    }



    /**
     * ?????????????????????????????????
     * @param componentId
     *
     *
     * ?????????????????????????????????
     * @return
     */
    @GetMapping("index/eventTypeCountByCG")
    @ResponseBody
    public AjaxResult getEventTypeCountByCG(@RequestParam("Component_Code") String componentId) {
        String createTime = DateFormatUtils.format(new Date(),"yyyy-MM-dd");//????????????
        String[] strArr = EventTypeStatus.EVENT_PLAT_1001_CODE.desc().split(",");
        Map<String,Object> result = new HashMap<>();
        List<PercentResult> list = new ArrayList<>();
        for(String str : strArr){
            PercentResult dto = new PercentResult();
            String[] statusArr = {"1","2","3","4","5","6","7","8","9"};
            int count = statisticsService.todayEventTypeCount(componentId,str,null,createTime,statusArr);
            dto.setValue((long)count);
            dto.setName(str);
            list.add(dto);
        }
        list.sort(Comparator.comparing(PercentResult::getValue).reversed());
        Long other = 0L;
        for(int i=0;i<list.size();i++) {
            if (i > 6) {
                //????????????????????????????????????
                Long value = list.get(i).getValue();
                other += value;
            } else {
                Long value = list.get(i).getValue();
                String name = list.get(i).getName();
                if(name.equals(EventTypeStatus.EVENT_PLAT_1001_CODE001.code())){
                    result.put(EventTypeStatus.EVENT_PLAT_1001_CODE001.desc(),value);
                }else if(name.equals(EventTypeStatus.EVENT_PLAT_1001_CODE002.code())){
                    result.put(EventTypeStatus.EVENT_PLAT_1001_CODE002.desc(),value);
                }else if(name.equals(EventTypeStatus.EVENT_PLAT_1001_CODE003.code())){
                    result.put(EventTypeStatus.EVENT_PLAT_1001_CODE003.desc(),value);
                }else if(name.equals(EventTypeStatus.EVENT_PLAT_1001_CODE004.code())){
                    result.put(EventTypeStatus.EVENT_PLAT_1001_CODE004.desc(),value);
                }else if(name.equals(EventTypeStatus.EVENT_PLAT_1001_CODE005.code())){
                    result.put(EventTypeStatus.EVENT_PLAT_1001_CODE005.desc(),value);
                }else if(name.equals(EventTypeStatus.EVENT_PLAT_1001_CODE006.code())){
                    result.put(EventTypeStatus.EVENT_PLAT_1001_CODE006.desc(),value);
                }else if(name.equals(EventTypeStatus.EVENT_PLAT_1001_CODE007.code())){
                    result.put(EventTypeStatus.EVENT_PLAT_1001_CODE007.desc(),value);
                }else if(name.equals(EventTypeStatus.EVENT_PLAT_1001_CODE008.code())){
                    result.put(EventTypeStatus.EVENT_PLAT_1001_CODE008.desc(),value);
                }else if(name.equals(EventTypeStatus.EVENT_PLAT_1001_CODE009.code())){
                    result.put(EventTypeStatus.EVENT_PLAT_1001_CODE009.desc(),value);
                }else if(name.equals(EventTypeStatus.EVENT_PLAT_1001_CODE010.code())){
                    result.put(EventTypeStatus.EVENT_PLAT_1001_CODE010.desc(),value);
                }else if(name.equals(EventTypeStatus.EVENT_PLAT_1001_CODE011.code())){
                    result.put(EventTypeStatus.EVENT_PLAT_1001_CODE011.desc(),value);
                }
            }
        }
        PercentResult percentResult = new PercentResult();
        percentResult.setName("other");
        percentResult.setValue(other);
        result.put("other",other);
        return AjaxResult.success(result);
    }


    /**
     * ?????????????????????????????????????????????
     * @param componentId
     * @return
     */
    @GetMapping("index/eventTypeRateByCG")
    @ResponseBody
    public AjaxResult eventTypeRateByCG(@RequestParam("Component_Code") String componentId) {
        String createTime = DateFormatUtils.format(new Date(),"yyyy-MM-dd");//????????????
        String[]arr = {"6","7","8","9"};//5:?????? 6:?????? 7:????????? 8????????? 9:??????????????????
        String[]allArr = {"1","2","3","4","5","6","7","8","9"};
        String[] strArr = EventTypeStatus.EVENT_PLAT_1001_CODE.desc().split(",");
        List<PercentResult> result = new ArrayList<>();
        List<PercentResult> list = new ArrayList<>();
        for(String str : strArr){
            PercentResult dto = new PercentResult();
            int count = statisticsService.todayEventTypeCount(componentId,str,null,createTime,allArr);
            int doneCount = statisticsService.todayEventTypeCount(componentId,str,null,createTime,arr);
            Double doneRate = rate(new BigDecimal(doneCount), new BigDecimal(count));
            dto.setValue((long)count);
            dto.setCode(str);
            if(str.equals(EventTypeStatus.EVENT_PLAT_1001_CODE001.code())){
                dto.setName(EventTypeStatus.EVENT_PLAT_1001_CODE001.desc());
            }else if(str.equals(EventTypeStatus.EVENT_PLAT_1001_CODE002.code())){
                dto.setName(EventTypeStatus.EVENT_PLAT_1001_CODE002.desc());
            }else if(str.equals(EventTypeStatus.EVENT_PLAT_1001_CODE003.code())){
                dto.setName(EventTypeStatus.EVENT_PLAT_1001_CODE003.desc());
            }else if(str.equals(EventTypeStatus.EVENT_PLAT_1001_CODE004.code())){
                dto.setName(EventTypeStatus.EVENT_PLAT_1001_CODE004.desc());
            }else if(str.equals(EventTypeStatus.EVENT_PLAT_1001_CODE005.code())){
                dto.setName(EventTypeStatus.EVENT_PLAT_1001_CODE005.desc());
            }else if(str.equals(EventTypeStatus.EVENT_PLAT_1001_CODE006.code())){
                dto.setName(EventTypeStatus.EVENT_PLAT_1001_CODE006.desc());
            }else if(str.equals(EventTypeStatus.EVENT_PLAT_1001_CODE007.code())){
                dto.setName(EventTypeStatus.EVENT_PLAT_1001_CODE007.desc());
            }else if(str.equals(EventTypeStatus.EVENT_PLAT_1001_CODE008.code())){
                dto.setName(EventTypeStatus.EVENT_PLAT_1001_CODE008.desc());
            }else if(str.equals(EventTypeStatus.EVENT_PLAT_1001_CODE009.code())){
                dto.setName(EventTypeStatus.EVENT_PLAT_1001_CODE009.desc());
            }else if(str.equals(EventTypeStatus.EVENT_PLAT_1001_CODE010.code())){
                dto.setName(EventTypeStatus.EVENT_PLAT_1001_CODE010.desc());
            }else if(str.equals(EventTypeStatus.EVENT_PLAT_1001_CODE011.code())){
                dto.setName(EventTypeStatus.EVENT_PLAT_1001_CODE011.desc());
            }
            dto.setPercent(doneRate);
            list.add(dto);
        }
        list.sort(Comparator.comparing(PercentResult::getValue).reversed());
        for(int i=0;i<list.size();i++) {
            if(i < 7){
                result.add(list.get(i));
            }
        }
        return AjaxResult.success(result);
    }


    /**
     * ?????????-???????????????-???????????????
     *
     *
     * ?????????????????????7???
     * @param componentId
     * @return
     */
    @GetMapping("index/eventTypeStatistics")
    @ResponseBody
    public AjaxResult getEventTypeStatistics(@RequestParam("Component_Code") String componentId) {
        String createTime = DateFormatUtils.format(new Date(),"yyyy-MM-dd");//????????????
        Map<String,Object> result = new HashMap<>();
        try{
            Map<String,Object> curretParams = new HashMap<>();
            curretParams.put("currentDate",createTime);//??????
            curretParams.put("componentId",componentId);//??????????????????
            curretParams.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});//???????????????
            int currentCount = statisticsService.countAllByParams(curretParams);//??????????????????
            Map<String,Object> doneParams = new HashMap<>();
            doneParams.put("currentDate",createTime);//??????
            doneParams.put("componentId",componentId);//??????????????????
            doneParams.put("statusArr",new String[]{"6","7","8","9"});//???????????????
            Integer doneCount = statisticsService.countAllByParams(doneParams);//????????????????????????
            Double doneRate = rate(new BigDecimal(doneCount),new BigDecimal(currentCount));//?????????????????????
            Map<String,Object> param1 = new HashMap<>();
            param1.put("currentDate",createTime);//??????
            param1.put("componentId",componentId);//??????????????????
            param1.put("statusArr",new String[]{"6","7","9"});
            param1.put("notEventStatus",3);
            int machineDis = statisticsService.countAllByParams(param1);
            result.put(EventTypeStatus.EVENT_DONE_WAY01.desc(),rate(new BigDecimal(machineDis+""),new BigDecimal(doneCount+"")));//????????????
            Map<String,Object> param2 = new HashMap<>();
            param2.put("currentDate",createTime);//??????
            param2.put("componentId",componentId);//??????????????????
            param2.put("eventAlterStatus",8);
            int errorDis = statisticsService.countAllByParams(param2);
            result.put(EventTypeStatus.EVENT_DONE_WAY02.desc(),rate(new BigDecimal(errorDis+""),new BigDecimal(doneCount+"")));//????????????
            Map<String,Object> param3 = new HashMap<>();
            param3.put("currentDate",createTime);//??????
            param3.put("componentId",componentId);//??????????????????
            param3.put("eventStatus",3);
            int centerDis = statisticsService.countAllByParams(param3);//????????????
            result.put(EventTypeStatus.EVENT_DONE_WAY03.desc(),rate(new BigDecimal(centerDis+""),new BigDecimal(doneCount+"")));//????????????
            /*Map<String,Object> param4 = new HashMap<>();
            param4.put("currentDate",createTime);//??????
            param4.put("componentId",componentId);//??????????????????
            param4.put("eventAlterStatus",6);
            param4.put("notEventStatus",3);
            int lawDis = statisticsService.countAllByParams(param4);//????????????*/
            int lawDis = 0;
            result.put(EventTypeStatus.EVENT_DONE_WAY04.desc(),rate(new BigDecimal(lawDis+""),new BigDecimal(doneCount+"")));//????????????
            result.put("doneRate",doneRate);
        }catch(Exception ex){
            logger.info(ex.getMessage());
            ex.printStackTrace();
        }
        return AjaxResult.success(result);
    }


    /***
     *  ??????????????????
     * ???????????? - ???????????????/?????????????????? ??????????????? ?????????
     */
    @GetMapping("index/eventTarget")
    @ResponseBody
    public AjaxResult getEventTarget(@RequestParam("Component_Code") String componentId) {
        Map<String,Object> response = new HashMap<>();
        String eventCreatTime = DateFormatUtils.format(new Date(),"yyyy-MM-dd");//???????????? yyyy-MM-dd
        String timeOut = DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss");//????????????

        Map<String,Object> eventParams = new HashMap<>();
        eventParams.put("eventCreatTime",eventCreatTime);
        eventParams.put("componentId",componentId);
        eventParams.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
        int eventCounts = statisticsService.queryTodayEventCounts(eventParams);//????????????

        int unprocessedEventCounts = statisticsService.queryTodayUnprocessedEventCounts(eventParams);//??????????????????

        Map<String,Object> timeOutParams = new HashMap<>();
        timeOutParams.put("timeOut",timeOut);
        timeOutParams.put("componentId",componentId);
        int eventTimeOutCounts = statisticsService.queryEventTimeOutCounts(timeOutParams);//???????????????

        response.put("eventCounts",eventCounts);//????????????
        response.put("unprocessedEventCounts",unprocessedEventCounts);//??????????????????
        response.put("eventTimeOutCounts",eventTimeOutCounts);//???????????????
        double achieveRate;
        if (eventCounts == 0){
            achieveRate = 0.0;
        }else {
            achieveRate = (100d*(eventCounts - unprocessedEventCounts)/eventCounts);
            achieveRate = Double.parseDouble(String.format("%.2f", achieveRate));
        }
        response.put("eventTimeOutCounts",eventTimeOutCounts);//???????????????
        response.put("achieveRate",achieveRate);
        return AjaxResult.success(response);
    }

    /**
     * ????????????
     *
     * ??????????????????????????????????????????====================================================================
     *
     *
     */
    @GetMapping("index/eventTypeCountByZZ")
    @ResponseBody
    public AjaxResult eventTypeCountByZZ(@RequestParam("Component_Code") String componentId){
        Map<String,Object> response = new HashMap<>();
        String createTime = DateFormatUtils.format(new Date(),"yyyy-MM-dd");//????????????
        String[] statusArr = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        //?????? ????????????
        int typeCount01 = statisticsService.todayEventTypeCount(componentId,null,"02001",createTime,statusArr);
        //?????? ????????????
        int typeCount02 = statisticsService.todayEventTypeCount(componentId,null,"03003",createTime,statusArr);
        //?????? ????????????
        int typeCount03 = statisticsService.todayEventTypeCount(componentId,null,"02012",createTime,statusArr);
        //?????? ????????????
        int typeCount04 = statisticsService.todayEventTypeCount(componentId,null,"04010",createTime,statusArr);
        //?????? ????????????
        int typeCount05 = statisticsService.todayEventTypeCount(componentId,null,"04012",createTime,statusArr);
        //?????? ????????????
        int typeCount06 = statisticsService.todayEventTypeCount(componentId,null,"01002",createTime,statusArr);
        //?????? ????????????
        int typeCount07 = statisticsService.todayEventTypeCount(componentId,null,"01007",createTime,statusArr);
        //?????? ????????????
        int typeCount08 = statisticsService.todayEventTypeCount(componentId,null,"02006",createTime,statusArr);
        //?????? ????????????
        int typeCount09 = statisticsService.todayEventTypeCount(componentId,null,"04015",createTime,statusArr);
        //?????? ????????????
        int typeCount10 = statisticsService.todayEventTypeCount(componentId,null,"02004",createTime,statusArr);
        response.put("typeCount01",typeCount01);
        response.put("typeCount02",typeCount02);
        response.put("typeCount03",typeCount03);
        response.put("typeCount04",typeCount04);
        response.put("typeCount05",typeCount05);
        response.put("typeCount06",typeCount06);
        response.put("typeCount07",typeCount07);
        response.put("typeCount08",typeCount08);
        response.put("typeCount09",typeCount09);
        response.put("typeCount10",typeCount10);
        return AjaxResult.success(response);
    }

    /**
     *  (?????????????????????????????????)
     *
     * ????????????
     *
     * ????????????
     */
    @GetMapping("index/energizeAllByZZ")
    @ResponseBody
    public AjaxResult energizeAllByZZ(@RequestParam("Component_Code") String componentId){
        String createTime = DateFormatUtils.format(new Date(),"yyyy-MM-dd");//????????????
        Map<String,Object> params = new HashMap<>();
        params.put("componentId",componentId);
        params.put("currentDate",createTime);
        params.put("statusArr",new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"});
        return AjaxResult.success(statisticsService.getMapByCameraType(params));
    }



    /**
     * ????????????
     *
     * ????????????
     */
    @GetMapping("index/governAllByZZ")
    @ResponseBody
    public AjaxResult governAll(@RequestParam("Component_Code") String componentId) {
        Map<String,Object> result = new HashMap<>();
        try{
            String createTime = DateFormatUtils.format(new Date(),"yyyy-MM-dd");//????????????
            String weekTime = DateFormatUtils.format(DateUtils.getThisWeekMonday(), DateUtils.YYYY_MM_DD);
            Map<String,Object> params = new HashMap<>();
            params.put("currentDate",createTime);
            params.put("componentId",componentId);
            params.put("statusArr",new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"});
            result = statisticsService.getMapByEventName(params);
            Map<String,Object> weekParams = new HashMap<>();
            weekParams.put("componentId",componentId);
            weekParams.put("trendWeekMonday",weekTime);
            weekParams.put("statusArr",new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"});
            List<HkEventInfo> weeList = statisticsService.getEventByParams(weekParams);//??????????????????
            List<TimeField> timeUtils = new TimeUtil().getTime("week");
            List<Integer> weekCounts = DataFormat(weeList,"week",timeUtils);//?????????
            result.put("weekCount",weekCounts);
        }catch(Exception ex){
            logger.info(ex.getMessage());
            ex.printStackTrace();
        }
        return AjaxResult.success(result);
    }


    /**
     * ????????????
     *
     *  ???????????????/?????????????????????/???????????????
     * @return
     */
    @GetMapping("index/hiddenTrouble")
    @ResponseBody
    public AjaxResult getHiddenTrouble(@RequestParam("Component_Code") String componentId){
        Map<String,Object> troubleParams = new HashMap<>();
        troubleParams.put("componentId",componentId);
        troubleParams.put("eventType","01");
        troubleParams.put("statusArr",new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"});
        int troubleCount = statisticsService.countAllByParams(troubleParams);//????????????
        troubleParams.put("eventType","01");
        troubleParams.put("statusArr",new String[]{"6","7","8","9"});
        int processCount = statisticsService.countAllByParams(troubleParams);//?????????????????????
        Map<String,Object> troubleParams1 = new HashMap<>();
        troubleParams1.put("componentId",componentId);
        troubleParams1.put("eventType","01");
        troubleParams1.put("statusArr",new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"});
        Map<String,Object> result = statisticsService.getMapBySubStatus(troubleParams1);
        result.put("processCount",processCount);
        result.put("troubleCount",troubleCount);
        return AjaxResult.success(result);
    }

    /**
     *  ??????   ?????????
     *  ???????????????/???????????????/???????????????
     * @return
     */
    @GetMapping("index/processRate")
    @ResponseBody
    public AjaxResult processRate(@RequestParam("Component_Code") String componentId){
        Map<String,Object> result = new HashMap<>();
        Map<String,Object> params = new HashMap<>();
        params.put("componentId",componentId);
        params.put("eventType","00");
        params.put("statusArr",new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"});
        int alarmCount = statisticsService.countAllByParams(params);//????????????
        params.put("statusArr",new String[]{"6","7","8","9"});
        int alarmDoneCount = statisticsService.countAllByParams(params);//??????????????????
        params.put("eventType","01");
        params.put("statusArr",new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"});
        int troubleCount = statisticsService.countAllByParams(params);//????????????
        params.put("statusArr",new String[]{"6","7","8","9"});
        int troubleDoneCount = statisticsService.countAllByParams(params);//??????????????????
        params.put("eventType","03");
        params.put("statusArr",new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"});
        int patrolCount = statisticsService.countAllByParams(params);//????????????
        params.put("statusArr",new String[]{"6","7","8","9"});
        int patrolDoneCount = statisticsService.countAllByParams(params);//??????????????????
        result.put("alarmRate",rate(new BigDecimal(alarmDoneCount),new BigDecimal(alarmCount)));//???????????????
        result.put("troubleRate",rate(new BigDecimal(troubleDoneCount),new BigDecimal(troubleCount)));//???????????????
        result.put("patrolRate",rate(new BigDecimal(patrolDoneCount),new BigDecimal(patrolCount)));//???????????????
        return AjaxResult.success(result);
    }

    /**
     *  ??????   ??????????????????
     *
     * @return
     */
    @GetMapping("index/eventInfoByYJ")
    @ResponseBody
    public AjaxResult eventInfoByYJ(@RequestParam(value = "Component_Code",required = false) String componentId,Long requestTime){
        Map<String,Object> params = new HashMap<>();
        params.put("componentId",componentId);
        if(requestTime != null){
            params.put("lastRequestTime",DateUtils.getLastRequestTime(requestTime));
        }
        String[] extendArr = null;
        if(StringUtils.isEmpty(componentId)){
            /**?????????????????????**/
            String accordingItem = configService.selectConfigByKey("dp.dialog.accordingItem");
            if(!StringUtils.isEmpty(accordingItem)){
                extendArr = accordingItem.split(",");
            }
        }
        int[] array = Arrays.asList(extendArr).stream().mapToInt(Integer::parseInt).toArray();
        params.put("extendInt3Arr",array);
        return AjaxResult.success(statisticsService.getNewEventByYJ(params));
    }

    /**
     *  ??????????????????
     *  ????????????
     *
     * @return
     */
    @GetMapping("index/eventServerInfo")
    @ResponseBody
    public AjaxResult eventServerInfo(@RequestParam("Component_Code") String componentId){
        Map<String,Object> result = new HashMap<>();
        Map<String,Object> params1 = new HashMap<>();
        params1.put("componentId",componentId);
        params1.put("statusArr",new String[]{"6","7","8","9"});
        PercentResult percentResult1 = statisticsService.eventCountAndRate(params1);
        Map<String,Object> params2 = new HashMap<>();
        params2.put("componentId",componentId);
        params2.put("statusArr",new String[]{"6","7","8","9"});
        PercentResult percentResult2 = statisticsService.eventActiveTime(params2);
        percentResult2.setName(componentId);
        result.put("serverCount",percentResult1);
        result.put("serverHours",percentResult2);
        return AjaxResult.success(result);
    }

    /**
     *  ???????????????/??????????????????
     *
     * @return
     */
    @GetMapping("index/populaHouseCount")
    @ResponseBody
    public AjaxResult populaHouseCount(){
        Map<String,Object> result = new HashMap<>();
        Map<String,Object> param = new HashMap<>();
        result.put("populaCount",populationService.allCount(param));
        result.put("houseCount",houseInfoService.allCount(param));
        return AjaxResult.success(result);
    }

    /**
     *  ?????????
     * @return
     */
    @GetMapping("index/populaQhj")
    @ResponseBody
    public AjaxResult populaQhj(){
        //?????????
        Map<String,Object> result = new HashMap<>();
        result.put("part_1",populationService.queryOutStatistics());
        //??????????????????
        Map<String,Object> param = new HashMap<>();
        List<PopulaStatiInfo> list = populationService.queryPopulaInfoByRegion(param);
        result.put("part_2",list);
        return AjaxResult.success(result);
    }


    List<Integer>  DataFormat(List<HkEventInfo> eventInfos, String type,List<TimeField> timeFields) throws ParseException {
        for(HkEventInfo eventInfo : eventInfos){
            for(TimeField timeField:timeFields){
                Integer count = timeField.getCount();
                if(count == null){
                    count = 0;
                    timeField.setCount(count);
                }
                String format = timeField.getFormat();
                Date startDate =  DateUtils.parseDate(timeField.getStartTime(),format);
                Date endDate = DateUtils.parseDate(timeField.getEndTime(),format);
                String DateStr = DateUtils.parseDateToStr(format,eventInfo.getCreateTime());
                if(startDate.getTime()<= DateUtils.parseDate(DateStr,format).getTime() && endDate.getTime()>=DateUtils.parseDate(DateStr,format).getTime()){
                    timeField.setCount(++count);
                }
            }
        }
        return timeFields.stream().map(TimeField::getCount).collect(Collectors.toList());//?????????
    }

    public Double rate(BigDecimal num,BigDecimal mum1){
        if(mum1.compareTo(BigDecimal.ZERO) < 1){
            return 0.00;
        }
        return num.divide(mum1,4,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /*??????-??????????????????
    * ?????????--????????????????????????????????????*/
    @RequestMapping("index/overview")
    @ResponseBody
    public AjaxResult overview(){
        Map<String,Object> result = new HashMap<>();
        XlPersonnel xlPersonnel = new XlPersonnel();
        //??????????????????
        int count = iSysRegionService.queryVillageCommunity();
        //???????????????
        int gridCount=iSysRegionService.queryGridALl();
        //??????????????????
        int personCount=iXlPersonnelService.queryPersonCount(xlPersonnel);
        result.put("count",count);
        result.put("gridCount",gridCount);
        result.put("personCount",personCount);
        return AjaxResult.success(result);
    }
    /**
     *??????-??????????????????
     * ????????????--????????????????????????????????????
     */
    @ApiOperation(value = "????????????",notes = "????????????")
//    @ApiImplicitParam(name = "userId", value = "??????ID", required = true, dataType = "int", paramType = "path")
    @DynamicResponseParameters(name = "personnel",properties = {
           @DynamicParameter(name = "HouseholdRegister",value = "???????????????",required = true,dataTypeClass = Integer.class),
           @DynamicParameter(name = "personCount",value = "????????????",required = true,dataTypeClass = Integer.class),
           @DynamicParameter(name = "flowcount",value = "????????????",required = true,dataTypeClass = Integer.class),
    })
    @RequestMapping(value = "index/personnel",method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult personnel(String nativePlace,String gender,String age,String polity){
        Map<String,Object> result = new HashMap<>();
        XlPersonnel xlPersonnel = new XlPersonnel();
        xlPersonnel.setNativePlace(nativePlace);
        if(gender==null){

        }else{
            xlPersonnel.setGender(gender);
        }
        xlPersonnel.setAge(age);
        xlPersonnel.setPolity(polity);
        //??????????????????
        int personCount=iXlPersonnelService.queryPersonCount(xlPersonnel);
        //????????????
        int flowcount= iXlPersonnelService.queryLiuPopulation(xlPersonnel);
        //????????????
        int HouseholdRegister=iXlPersonnelService.queryHuPopulation(xlPersonnel);
        result.put("personCount",personCount);
        result.put("flowcount",flowcount);
        result.put("HouseholdRegister",HouseholdRegister);
        return AjaxResult.success(result);
    }

    /**
     *??????-??????????????????
     * ????????????--????????????
     */
    @RequestMapping(value = "index/personnelList",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult personnelList(String personName,String phone,String idcard,String liveType,String nativePlace,String polity,String villageName,String ethnicGroup, Integer pageNum,Integer pageSize ){
        if(pageNum==null){
            pageNum=1;
        }else if(pageSize==null){
            pageSize=20;
        }
        startPage();
        XlPersonnel xlPersonnel = new XlPersonnel();
        xlPersonnel.setNativePlace(nativePlace);
        xlPersonnel.setName(personName);
        xlPersonnel.setPhone(phone);
        xlPersonnel.setIdcard(idcard);
        xlPersonnel.setLiveType(liveType);
        xlPersonnel.setVillageName(villageName);
        xlPersonnel.setNation(ethnicGroup);
        xlPersonnel.setPolity(polity);
        List<XlPersonnel> xlPersonnelList=iXlPersonnelService.selectXlPersonnelList(xlPersonnel);
        return AjaxResult.success(xlPersonnelList);
    }


    /**
     * ??????-??????????????????
     * ????????????--????????????????????????????????????
     */
    @RequestMapping("index/house")
    @ResponseBody
    public AjaxResult house(String communityName,String roomType,String roomLiveType){
        Map<String,Object> result = new HashMap<>();
        XlRoom xlRoom = new XlRoom();
        xlRoom.setCommunityName(communityName);
        xlRoom.setRoomType(roomType);
        if(roomLiveType!=null){
            xlRoom.setRoomLiveType(Integer.parseInt(roomLiveType));
        }
        //??????????????????
        int roomCount=iXlRoomService.queryRoomAll(xlRoom);
        //?????????????????????
        int commercialHouse=iXlRoomService.queryCommercialCount(xlRoom);
        //?????????????????????
        int selfBuiltHouse=iXlRoomService.querySelfBuiltCount(xlRoom);
        result.put("roomCount",roomCount);
        result.put("commercialHouse",commercialHouse);
        result.put("selfBuiltHouse",selfBuiltHouse);
        return AjaxResult.success(result);
    }

    /**
     * ??????-??????????????????
     * ????????????--??????
     */
    @RequestMapping(value = "index/houseList",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult houseList(String communityName,String roomType,String roomLiveType,String villageName,String blurred,Integer pageNum,Integer pageSize){
        if(pageNum==null){
            pageNum=1;
        }else if(pageSize==null){
            pageSize=20;
        }
        startPage();
        Map<String,Object> result = new HashMap<>();
        XlRoom xlRoom = new XlRoom();
        xlRoom.setCommunityName(communityName);
        xlRoom.setRoomType(roomType);
        if(roomLiveType!=null){
            xlRoom.setRoomLiveType(Integer.parseInt(roomLiveType));
        }
        xlRoom.setVillageName(villageName);
        xlRoom.setAddress(blurred);
        List<XlRoom> xlRooms = iXlRoomService.selectXlRoomList(xlRoom);
        return AjaxResult.success(xlRooms);
    }

    /**
     * ??????-??????????????????
     * ????????????--??????
     */
    @RequestMapping(value = "index/houseDetail",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult houseDetail(String id){
        Map<String,Object> result = new HashMap<>();
        List<XlEnterpriseInfo> xlEnterpriseInfos= iXlRoomService.queryRoomDetail(Integer.parseInt(id));
        return AjaxResult.success(xlEnterpriseInfos);
    }


    /**
     * ??????-??????????????????
     * ????????????--??????????????????????????????????????????
     */
    @RequestMapping("index/enterprise")
    @ResponseBody
    public AjaxResult enterprise(Integer enterpriseType,String natureBusiness,String enterpriseName){
        Map<String,Object> result = new HashMap<>();
        XlEnterpriseInfo xlEnterpriseInfo = new XlEnterpriseInfo();
//        xlEnterpriseInfo.setEnterpriseType(enterpriseType);
        xlEnterpriseInfo.setEnterpriseTypeNum(enterpriseType);
        xlEnterpriseInfo.setNatureBusiness(natureBusiness);
        xlEnterpriseInfo.setEnterpriseName(enterpriseName);
        //????????????
        int enterpriseCount =iXlEnterpriseInfoService.queryEnterpriseAll(xlEnterpriseInfo);
        //??????????????????
        int ListedEnterpriseCount=iXlEnterpriseInfoService.queryListedEnterprise(xlEnterpriseInfo);
        //??????????????????
        int geTiEnterpriseCount=iXlEnterpriseInfoService.queryListGeTiEnterprise(xlEnterpriseInfo);
        result.put("enterpriseCount",enterpriseCount);
        result.put("ListedEnterpriseCount",ListedEnterpriseCount);
        result.put("geTiEnterpriseCount",geTiEnterpriseCount);
        return AjaxResult.success(result);
    }
    /**
     * ??????-??????????????????
     * ????????????--????????????
     */
    @RequestMapping("index/enterpriseList")
    @ResponseBody
    public AjaxResult enterpriseList(Integer enterpriseType,String enterpriseName,Integer isOnStock,Integer enterpriseStatus,String unifiedCreditCode,String establishPerson,String startTime,String endTime,Integer pageNum,Integer pageSize){
        if(pageNum==null){
            pageNum=1;
        }else if(pageSize==null){
            pageSize=20;
        }
        startPage();
        Map<String,Object> result = new HashMap<>();
        result.put("enterpriseType",enterpriseType);
        result.put("enterpriseName",enterpriseName);
        result.put("isOnStock",isOnStock);
        result.put("enterpriseStatus",enterpriseStatus);
        result.put("unifiedCreditCode",unifiedCreditCode);
        result.put("establishPerson",establishPerson);
        result.put("startTime",startTime);
        result.put("endTime",endTime);
        List<XlEnterpriseInfo> xlEnterpriseInfos = iXlEnterpriseInfoService.selectXlEnterpriseInfo(result);
        return AjaxResult.success(xlEnterpriseInfos);
    }

    /**
     * ??????????????????--????????????????????????????????????????????????????????????
     */
    @RequestMapping("index/event")
    @ResponseBody
    public AjaxResult event(){
        Map<String,Object> result = new HashMap<>();
        Map<String,Object> thingResult = new HashMap<>();
        Map<String,Object> params = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.MONTH,0);
        Date time = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //???????????????
        String format = dateFormat.format(time);
        thingResult.put("startTime",format);
        thingResult.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
        //??????????????????
        int eventCount=eventService.queryEventAllCount(thingResult);
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(new Date());
        calendar1.set(Calendar.DAY_OF_MONTH,1);
        calendar1.add(Calendar.MONTH,-1);
        Date time1 = calendar1.getTime();
        calendar2.setTime(new Date());
        calendar2.set(Calendar.DAY_OF_MONTH,0);
        calendar2.add(Calendar.MONTH,0);
        Date time2 = calendar2.getTime();
        //??????????????????
        String format1 = dateFormat.format(time1);
        //?????????????????????
        String format2 = dateFormat.format(time2);
        thingResult.put("lastStartTime",format1);
        thingResult.put("lastEndTime",format2);
        //?????????????????????
        int lastEventCount=eventService.queryLastEventAllCount(thingResult);
        //????????????????????????
        params.put("startTime",format);
        params.put("statusArr",new String[]{"6","7","8","9"});
        int endCount=eventService.queryEventAllCount(params);
        result.put("eventCount",eventCount);
        result.put("lastEventCount",lastEventCount);
        result.put("endCount",endCount);
        return AjaxResult.success(result);
    }

    /**
     *??????????????????--?????????????????????????????????????????????????????????
     */
    @RequestMapping("index/urbanEvent")
    @ResponseBody
    public AjaxResult urbanEvent(){
        Map<String,Object> result = new HashMap<>();
        Map<String,Object> thingResult = new HashMap<>();
        Map<String,Object> params = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.MONTH,0);
        Date time = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //???????????????
        String format = dateFormat.format(time);
        thingResult.put("startTime",format);
        thingResult.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
        thingResult.put("componentId","1001");
        //??????????????????
        int eventCount=eventService.queryEventAllCount(thingResult);
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(new Date());
        calendar1.set(Calendar.DAY_OF_MONTH,1);
        calendar1.add(Calendar.MONTH,-1);
        Date time1 = calendar1.getTime();
        calendar2.setTime(new Date());
        calendar2.set(Calendar.DAY_OF_MONTH,0);
        calendar2.add(Calendar.MONTH,0);
        Date time2 = calendar2.getTime();
        //??????????????????
        String format1 = dateFormat.format(time1);
        //?????????????????????
        String format2 = dateFormat.format(time2);
        thingResult.put("lastStartTime",format1);
        thingResult.put("lastEndTime",format2);
        thingResult.put("componentId","1001");
        //?????????????????????
        int lastEventCount=eventService.queryLastEventAllCount(thingResult);
        //????????????????????????
        params.put("startTime",format);
        params.put("statusArr",new String[]{"6","7","8","9"});
        int endCount=eventService.queryEventAllCount(params);
        result.put("eventCount",eventCount);
        result.put("lastEventCount",lastEventCount);
        result.put("endCount",endCount);

        return AjaxResult.success(result);
    }

    /**
     * ????????????
     * ??????????????????--??????????????????????????????????????????
     */
    @RequestMapping("index/mapElement")
    @ResponseBody
    public AjaxResult mapElement(){
        Map<String,Object> thingResult = new HashMap<>();
        XlPersonnel xlPersonnel = new XlPersonnel();
        XlEnterpriseInfo xlEnterpriseInfo = new XlEnterpriseInfo();
        XlRoom xlRoom = new XlRoom();
        int personCount=iXlPersonnelService.queryPersonCount(xlPersonnel);
        int roomCount=iXlRoomService.queryRoomAll(xlRoom);
        int enterpriseCount =iXlEnterpriseInfoService.queryEnterpriseAll(xlEnterpriseInfo);
        int equipmentCount=iXlEquipmentInfoService.queryEquipmentCount();
        thingResult.put("personCount",personCount);
        thingResult.put("roomCount",roomCount);
        thingResult.put("enterpriseCount",enterpriseCount);
        thingResult.put("equipmentCount",equipmentCount);
        return AjaxResult.success(thingResult);
    }

    /**
     * ????????????
     * ????????????--????????????
     */
    @RequestMapping("index/eventSource")
    @ResponseBody
    public AjaxResult eventSource(String time,String deptName,String communityName,String gridName){
        Map<String,Object> thingResult = new HashMap<>();
        Map<String,Object> params = new HashMap<>();
        Date date = new Date();
        if(time==null){
           /* int deptId=deptService.queryDeptName(deptName);
            int deptCount=eventService.queryEventDept(String.valueOf(deptId));*/
            thingResult.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            thingResult.put("nearlyAWeekBegin",DateUtil.offsetWeek(date, -1).toString());
            int eventAllCount=eventService.queryLastEventAllCount(thingResult);//??????????????????
            thingResult.put("statusArr",new String[]{"2"});
            int studyEventCount=eventService.queryLastEventAllCount(thingResult);//????????????????????????
            thingResult.put("statusArr",new String[]{"3","4","5"});
            int dealEventCount=eventService.queryLastEventAllCount(thingResult);//???????????????????????????
            thingResult.put("statusArr",new String[]{"6","7","8","9"});
            int endEventCount=eventService.queryLastEventAllCount(thingResult);//????????????????????????
            thingResult.put("statusArr",new String[]{"7"});
            int neglectEventCount=eventService.queryLastEventAllCount(thingResult);//????????????????????????
            //?????????????????????
            int evaluateCount=eventService.queryEvaluate(thingResult);
//            String oneStarProportion=(float)evaluateCount/(float)eventAllCount*100.00+"%";//?????????????????????
            String handleRate = divideTo(new BigDecimal(eventAllCount),new BigDecimal(evaluateCount))+"%";
            params.put("neglectEventCount",neglectEventCount);
            params.put("eventAllCount",eventAllCount);
            params.put("studyEventCount",studyEventCount);
            params.put("dealEventCount",dealEventCount);
            params.put("endEventCount",endEventCount);
            params.put("handleRate",handleRate);
            thingResult.put("componentId",new String[]{"1002","1004"});
            int platformCount=eventService.queryPlatformEvent(thingResult);//????????????????????????????????????????????????
            thingResult.put("componentId",new String[]{"1005"});
            int streetCount=eventService.queryPlatformEvent(thingResult);//??????????????????
            params.put("platformCount",platformCount);
            params.put("streetCount",streetCount);
        }else if (time.equals("???")){
            thingResult.put("nearlyAMonthBegin",DateUtil.offsetMonth(date, -1).toString());
            int eventAllCount=eventService.queryLastEventAllCount(thingResult);
            thingResult.put("statusArr",new String[]{"2"});
            int studyEventCount=eventService.queryLastEventAllCount(thingResult);//????????????????????????
            thingResult.put("statusArr",new String[]{"3","4","5"});
            int dealEventCount=eventService.queryLastEventAllCount(thingResult);//???????????????????????????
            thingResult.put("statusArr",new String[]{"6","7","8","9"});
            int endEventCount=eventService.queryLastEventAllCount(thingResult);//????????????????????????
            thingResult.put("statusArr",new String[]{"7"});
            int neglectEventCount=eventService.queryLastEventAllCount(thingResult);//????????????????????????
            //?????????????????????
            int evaluateCount=eventService.queryEvaluate(thingResult);
//            String oneStarProportion=(float)evaluateCount/(float)eventAllCount*100+"%";//?????????????????????
            String handleRate = divideTo(new BigDecimal(eventAllCount),new BigDecimal(evaluateCount))+"%";
            params.put("neglectEventCount",neglectEventCount);
            params.put("eventAllCount",eventAllCount);
            params.put("studyEventCount",studyEventCount);
            params.put("dealEventCount",dealEventCount);
            params.put("endEventCount",endEventCount);
            params.put("handleRate",handleRate);
            thingResult.put("componentId",new String[]{"1002","1004"});
            int platformCount=eventService.queryPlatformEvent(thingResult);//????????????????????????????????????????????????
            thingResult.put("componentId",new String[]{"1005"});
            int streetCount=eventService.queryPlatformEvent(thingResult);//??????????????????
            params.put("platformCount",platformCount);
            params.put("streetCount",streetCount);
        }else if(time.equals("???")){
            thingResult.put("nearlyAYearBegin",DateUtils.getNearlyYear(date,-1));
            int eventAllCount=eventService.queryLastEventAllCount(thingResult);
            thingResult.put("statusArr",new String[]{"2"});
            int studyEventCount=eventService.queryLastEventAllCount(thingResult);//????????????????????????
            thingResult.put("statusArr",new String[]{"3","4","5"});
            int dealEventCount=eventService.queryLastEventAllCount(thingResult);//???????????????????????????
            thingResult.put("statusArr",new String[]{"6","7","8","9"});
            int endEventCount=eventService.queryLastEventAllCount(thingResult);//????????????????????????
            thingResult.put("statusArr",new String[]{"7"});
            int neglectEventCount=eventService.queryLastEventAllCount(thingResult);//????????????????????????
            //?????????????????????
            int evaluateCount=eventService.queryEvaluate(thingResult);
//            String oneStarProportion=(float)evaluateCount/(float)eventAllCount*100+"%";//?????????????????????
            String handleRate = divideTo(new BigDecimal(eventAllCount),new BigDecimal(evaluateCount))+"%";

            params.put("neglectEventCount",neglectEventCount);
            params.put("eventAllCount",eventAllCount);
            params.put("studyEventCount",studyEventCount);
            params.put("dealEventCount",dealEventCount);
            params.put("endEventCount",endEventCount);
            params.put("handleRate",handleRate);
            thingResult.put("componentId",new String[]{"1002","1004"});
            int platformCount=eventService.queryPlatformEvent(thingResult);//????????????????????????????????????????????????
            thingResult.put("componentId",new String[]{"1005"});
            int streetCount=eventService.queryPlatformEvent(thingResult);//??????????????????
            params.put("platformCount",platformCount);
            params.put("streetCount",streetCount);
        }else if(time.equals("???")){
            thingResult.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            thingResult.put("nearlyAWeekBegin",DateUtil.offsetWeek(date, -1).toString());
            int eventAllCount=eventService.queryLastEventAllCount(thingResult);//??????????????????
            thingResult.put("statusArr",new String[]{"2"});
            int studyEventCount=eventService.queryLastEventAllCount(thingResult);//????????????????????????
            thingResult.put("statusArr",new String[]{"3","4","5"});
            int dealEventCount=eventService.queryLastEventAllCount(thingResult);//???????????????????????????
            thingResult.put("statusArr",new String[]{"6","7","8","9"});
            int endEventCount=eventService.queryLastEventAllCount(thingResult);//????????????????????????
            thingResult.put("statusArr",new String[]{"7"});
            int neglectEventCount=eventService.queryLastEventAllCount(thingResult);//????????????????????????
            //?????????????????????
            int evaluateCount=eventService.queryEvaluate(thingResult);
//            String oneStarProportion=(float)evaluateCount/(float)eventAllCount*100+"%";//?????????????????????
            String handleRate = divideTo(new BigDecimal(eventAllCount),new BigDecimal(evaluateCount))+"%";
            params.put("neglectEventCount",neglectEventCount);
            params.put("eventAllCount",eventAllCount);
            params.put("studyEventCount",studyEventCount);
            params.put("dealEventCount",dealEventCount);
            params.put("endEventCount",endEventCount);
            params.put("handleRate",handleRate);
            thingResult.put("componentId",new String[]{"1002","1004"});
            int platformCount=eventService.queryPlatformEvent(thingResult);//????????????????????????????????????????????????
            thingResult.put("componentId",new String[]{"1005"});
            int streetCount=eventService.queryPlatformEvent(thingResult);//??????????????????
            params.put("platformCount",platformCount);
            params.put("streetCount",streetCount);
        }

        return AjaxResult.success(params);
    }
    //????????????
    @RequestMapping(value = "index/eventList",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult eventList(String eventType,String startTime,String endTime,String status,String grade,Integer pageNum,Integer pageSize){
        if(pageNum==null){
            pageNum=1;
        }else if(pageSize==null){
            pageSize=20;
        }
        startPage();
        Map<String,Object> thingResult = new HashMap<>();
        thingResult.put("eventType",eventType);
        thingResult.put("startTime",startTime);
        thingResult.put("endTime",endTime);
        thingResult.put("status",status);
        String gradeType=null;
        if(grade==null){

        }else if(grade.equals("??????")){
            gradeType="99";
        }else if(grade.equals("??????")){
            gradeType="999";
        }else{
            gradeType="1";
        }
        thingResult.put("grade",gradeType);
        List<HkEventInfo> list= eventService.queryEventInfoAllList(thingResult);
        return AjaxResult.success(list);
    }
    //????????????
    @RequestMapping("index/eventdeail")
    @ResponseBody
    public AjaxResult eventdeail(String eventId){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String,Object> result = new HashMap<>();
        HkEventInfo eventInfo = hkEventRecordService.findEventId(eventId);
        if(eventInfo == null){
            AjaxResult.error("???????????????");
        }
        Date createTime = eventInfo.getCreateTime();
        Date updateTime = eventInfo.getUpdateTime();
        result.put("eventTitle",eventInfo.getEventTitle());
        result.put("timeOut",eventInfo.getTimeOut());
        result.put("eventTypeName",eventInfo.getEventTypeName());
        result.put("reportTypeName",eventInfo.getReportTypeName());
        result.put("createTime",eventInfo.getCreateTime());//?????????????????????????????????????????????????????????
        result.put("updateTime",eventInfo.getUpdateTime());//??????????????????
        result.put("regionName",eventInfo.getRegionName());
        result.put("eventAddress",eventInfo.getEventAddress());
        result.put("eventImage",eventInfo.getEventImage());
        result.put("evaluate",eventInfo.getEvaluate());
        if(updateTime!=null){
            result.put("allTime",DateUtils.subStrDateStringRetainTwo(DateUtil.formatBetween(createTime,updateTime)));

        }
        List<Map<String,Object>> list = new ArrayList<>();
        List<HkActionProcess> processes = appMyWorkService.findActionChainEventId(eventId);
        if(processes == null || processes.size() == 0){
            result.put("list",list);
            return AjaxResult.success(result);
        }
        /**????????????????????????**/
        processes.sort(Comparator.comparing(HkActionProcess::getCreateTime));
        HkActionProcess process = processes.get(0);

        String handlerUserId = process.getHandlerUserId();
        SysUser user = userService.selectUserById(Long.parseLong(handlerUserId));
        String userName = user.getUserName();
        String phone = user.getPhonenumber();
        result.put("phone",phone);
        result.put("userName",userName);
        result.put("processTime",createTime);
        Map<String,Object> param = new HashMap<>();
        param.put("eventId",process.getEventId());
        param.put("statusArr",new String[]{"1","2","3","5","4","6"});
        List<HKrecord> hKrecords = appEventDetailService.selectProcessByParams(param);
        for(HKrecord record : hKrecords){
            Map<String,Object> map = new HashMap<>();
            SysUser user1 = userService.selectUserById(Long.parseLong(record.getHandlerIndexCode()));
            map.put("todoUserId",user1.getUserId());
            map.put("todoUserName",user1.getUserName());
            map.put("processStatus",record.getProcessStatus());
            map.put("processTime",dateFormat.format(record.getCreateTime()));
            /*if ("6".equals(record.getProcessStatus())){ //????????????
                map.put("todoUserName","????????????");
                map.put("phone","-");
                map.put("processTime",dateFormat.format(eventInfo.getUpdateTime()));
                map.put("zpUserName",eventInfo.getUpdateUser());//?????????
            }else*/
            if("5".equals(record.getProcessStatus())){//???????????????
                String json = record.getHandlerContent();
                JSONObject voiceJson = JSON.parseObject(json);
                String handlerContent = voiceJson.getString("handlerContent");
                map.put("backReason",handlerContent);
                String nowTime = voiceJson.getString("nowTime");
                map.put("processTime",nowTime);
            } else {
                map.put("phone",user1.getPhonenumber());
                map.put("processTime",dateFormat.format(record.getCreateTime()));
                map.put("zpUserName",process.getCreateUser());//?????????
                if ("4".equals(record.getProcessStatus())){ //??????
                    for (HkActionProcess dto : processes){
                        if (dto.getHandlerUserId().equals(record.getHandlerIndexCode())){ //????????????????????????????????????
                            AppActionInfo as  =  JSONObject.parseObject(record.getHandlerContent(), AppActionInfo.class);
                            if(!as.getActionName().equals("QS0001")){
                                List<appField> listInfo = JSONObject.parseArray(as.getAppField(), appField.class);
                                map.put("processResult",listInfo);
                                break;
                            }
                        }
                    }
                }
            }
            list.add(map);
        }
        result.put("list",list);

        return AjaxResult.success(result);
    }

    /**
     * ????????????--????????????
     * ????????????--???????????????????????????????????????????????????????????????
     */
    @RequestMapping("index/propertysurvey")
    @ResponseBody
    public AjaxResult propertysurvey(){
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> thingResult = new HashMap<>();
        int estateManagements=estateManagementService.queryPropert(thingResult);//????????????
        XlPersonnel xlPersonnel = new XlPersonnel();
        XlRoom xlRoom = new XlRoom();
        //????????????
        int villageCount=villageService.queryVillageCount();
        //?????????????????????
        int commercialHouse=iXlRoomService.queryCommercialCount(xlRoom);
        //?????????????????????
        int selfBuiltHouse=iXlRoomService.querySelfBuiltCount(xlRoom);
        //??????
        int tenantCount= iXlPersonnelService.queryLiuPopulation(xlPersonnel);
        //??????
        int ownerCount=iXlPersonnelService.queryHuPopulation(xlPersonnel);
        params.put("estateManagements",estateManagements);
        params.put("villageCount",villageCount);
        params.put("commercialHouse",commercialHouse);
        params.put("selfBuiltHouse",selfBuiltHouse);
        params.put("tenantCount",tenantCount);
        params.put("ownerCount",ownerCount);
        return AjaxResult.success(params);
    }

    /**
     * ??????--????????????
     * ??????????????????
     */
    @RequestMapping("index/equipmentPoints")
    @ResponseBody
    public AjaxResult equipmentPoints(@RequestParam(value = "eventType",required=false) List<String> eventType,String deviceType,Integer pageNum,Integer pageSize){
        if(pageNum==null){
            pageNum=1;
        }else if(pageSize==null){
            pageSize=20;
        }
        startPage();
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> thingResult = new HashMap<>();
        String eventTypeAll=null;
        String cameraIndexCode=null;
        if(eventType!=null){
            for(int j=0;j<eventType.size();j++){
                String eventType1 = eventType.get(j);
                if(j==0){
                    eventTypeAll=eventType1;
                }else{
                    eventTypeAll=eventTypeAll+","+eventType1;
                }
            }
        }
        if(eventTypeAll!=null){
            String[] split1 = eventTypeAll.split(",");
            params.put("eventTypeAll",split1);
        }
        List<CameraIndexCodeVo> indexCodeDtos= eventService.querycameraIndexCode(params);
        for(int k=0;k<indexCodeDtos.size();k++){
            String cameraIndexCode1 = indexCodeDtos.get(k).getCameraIndexCode();
            if(k==0){
                cameraIndexCode=cameraIndexCode1;
            }else{
                cameraIndexCode=cameraIndexCode+","+cameraIndexCode1;
            }
        }
        if(cameraIndexCode!=null){
            String[] split = cameraIndexCode.split(",");
            thingResult.put("cameraIndexCode",split);
        }
        thingResult.put("deviceType",deviceType);
       List<XlEquipmentInfo> xlEquipmentInfos= iXlEquipmentInfoService.queryList(thingResult);
//        List<XlEquipmentInfo> list=iXlEquipmentInfoService.queryEquipmentList();
        return AjaxResult.success(xlEquipmentInfos);
    }

    /**
     *????????????--???????????????(??????????????????????????????)
     */
    @RequestMapping("index/propertyrElevant")
    @ResponseBody
    public AjaxResult propertyrElevant(String neighborhood,Integer pageNum,Integer pageSize){
        if(pageNum==null){
            pageNum=1;
        }else if(pageSize==null){
            pageSize=20;
        }
        startPage();
        List<Map> list1 = new ArrayList<>();
        if(neighborhood==null){
            List<XlVillageModel> list=villageService.queryVillageList();
            for(int i=0;i<list.size();i++){
                Map map = new HashMap<>();
                String name = list.get(i).getName();
                String neighborhoodPerson = list.get(i).getNeighborhoodPerson();
                String neighborhoodPhone = list.get(i).getNeighborhoodPhone();
                map.put("quartersName",name);
                map.put("propertyName","?????????");
                map.put("neighborhoodPerson",neighborhoodPerson);
                map.put("neighborhoodPhone",neighborhoodPhone);
                list1.add(map);
            }
            return AjaxResult.success(list1);
        }else if(neighborhood.equals("???????????????")){
            List<XlVillageModel> list=villageService.queryVillageList();
            for(int i=0;i<list.size();i++){
                Map map = new HashMap<>();
                String name = list.get(i).getName();
                String neighborhoodPerson = list.get(i).getNeighborhoodPerson();
                String neighborhoodPhone = list.get(i).getNeighborhoodPhone();
                map.put("quartersName",name);
                map.put("propertyName","?????????");
                map.put("neighborhoodPerson",neighborhoodPerson);
                map.put("neighborhoodPhone",neighborhoodPhone);
                list1.add(map);
            }
            return AjaxResult.success(list1);
        }else if(neighborhood.equals("???????????????")){
            List<XlVillageModel> list=villageService.queryVillageComList();
            for(int i=0;i<list.size();i++){
                Map map = new HashMap<>();
                String name = list.get(i).getName();
                String quartersCommitteePerson = list.get(i).getQuartersCommitteePerson();
                String quartersCommitteePhone = list.get(i).getQuartersCommitteePhone();
                map.put("quartersName",name);
                map.put("propertyName",name+"?????????");
                map.put("neighborhoodPerson",quartersCommitteePerson);
                map.put("neighborhoodPhone",quartersCommitteePhone);
                list1.add(map);
            }
            return AjaxResult.success(list1);
        }else if(neighborhood.equals("????????????")){
            List<XlVillageModel> list=villageService.queryPropertyList();
            for(int i=0;i<list.size();i++){
                Map map = new HashMap<>();
                String name = list.get(i).getName();
                String propertyName = list.get(i).getPropertyName();
                String propertyPerson = list.get(i).getPropertyPerson();
                String propertyPhone = list.get(i).getPropertyPhone();
                map.put("quartersName",name);
                map.put("propertyName",propertyName);
                map.put("neighborhoodPerson",propertyPerson);
                map.put("neighborhoodPhone",propertyPhone);
                list1.add(map);
            }
            return AjaxResult.success(list1);
        }
        return AjaxResult.success();
    }
    /**
     * ???????????????
     */
    @RequestMapping("index/dropDown")
    @ResponseBody
    public AjaxResult dropDown(String quartersName){
        XlVillageModel xlVillageModel = new XlVillageModel();
        xlVillageModel.setName(quartersName);
        List<XlVillageModel> xlVillageModels=villageService.queryvillageName(xlVillageModel);
        List<Map> list1 = new ArrayList<>();
        for(int i=0;i<xlVillageModels.size();i++){
            Map map = new HashMap<>();
            map.put("quartersName",xlVillageModels.get(i).getName());
            map.put("quartersId",xlVillageModels.get(i).getId());
            list1.add(map);
        }
        return AjaxResult.success(list1);
    }
    /**
     *??????????????????
     */
    @RequestMapping("index/propertyEventList")
    @ResponseBody
    public AjaxResult propertyEventList(String eventSource,String eventType,Integer villageId,Integer eventStatus){
        startPage();
        Map<String,Object> thingResult = new HashMap<>();
        thingResult.put("eventSource",eventSource);
        thingResult.put("eventType",eventType);
        thingResult.put("residentialQuarters",villageId);
        thingResult.put("eventStatus",eventStatus);
       /* if(residentialQuarters==null){

        }else{
            XlVillageModel xlVillageModel = new XlVillageModel();
            xlVillageModel.setName(residentialQuarters);
            int id=villageService.queryVillageId(xlVillageModel);

        }*/
        List<XlPropertyAndVillageVo> xlPropertyEvents=propertyEventService.queryPropertyEventList(thingResult);
        List<Map> list1 = new ArrayList<>();
        for(int i=0;i<xlPropertyEvents.size();i++){
            Map map = new HashMap<>();
            map.put("eventId",xlPropertyEvents.get(i).getEventId());
            map.put("quartersName",xlPropertyEvents.get(i).getName());
            map.put("eventSource",xlPropertyEvents.get(i).getEventSource());
            map.put("eventType",xlPropertyEvents.get(i).getEventType());
            map.put("eventStatus",xlPropertyEvents.get(i).getEventStatus());
//            map.put("eventAddress",xlPropertyEvents.get(i).getEventAddress());
//            map.put("reporter",xlPropertyEvents.get(i).getReporter());
//            map.put("reporterPhone",xlPropertyEvents.get(i).getReporterPhone());
            map.put("reportTime",xlPropertyEvents.get(i).getReportTime());
//            map.put("reportContent",xlPropertyEvents.get(i).getReportContent());
//            map.put("reportImage",xlPropertyEvents.get(i).getReportImage());
//            map.put("propertyName",xlPropertyEvents.get(i).getPropertyName());
            list1.add(map);
        }

        return AjaxResult.success(list1);
    }
    /**
     * ??????--????????????
     * ????????????-?????????????????????
     */
    @RequestMapping("index/trendChart")
    @ResponseBody
    public AjaxResult trendChart(String source,String type,String allStreet,String communityName,String dept){
        if(source==null || source.equals("all")){
        Map<String,Object> data = null;
        if(data == null){
            Date date = new Date();
            //??????????????????Map
            Map<String,Object> params = EventDate(type,date);
            //?????????????????? //?????????????????????
//            List<Integer> eventTrendCount = new ArrayList<>();

           /* String nearlyADayBegin      =  (String)params.get("nearlyADayBegin");
            String nearlyADayEnd        =  (String)params.get("nearlyADayEnd");*/
            String nearlyAWeekBegin     =  (String)params.get("nearlyAWeekBegin");
            String nearlyAWeekEnd       =  (String)params.get("nearlyAWeekEnd");
            String nearlyAMonthBegin    =  (String)params.get("nearlyAMonthBegin");
            String nearlyAMonthEnd      =  (String)params.get("nearlyAMonthEnd");
            String nearlyAYearBegin     =  (String)params.get("nearlyAYearBegin");
            String nearlyAYearEnd       =  (String)params.get("nearlyAYearEnd");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<Integer> countList = new ArrayList<>();
            List<String> timeList = new ArrayList<>();
            try{
                if(!StringUtils.isEmpty(nearlyAWeekBegin) && StringUtils.isEmpty(nearlyAWeekEnd)){
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    params.clear();
                    //??????????????????
                    long time = sdf.parse(nearlyAWeekBegin).getTime();
                    String beginDateTime = nearlyAWeekBegin;
                    for(int i = 1; i <= 7; i++){
                        time += 3600000*24;
                        String endDateTime = sdf.format(new Date(time));
                        String endTime = sdf1.format(new Date(time));
                        params.put("endDateTime",endDateTime);
                        params.put("beginDateTime",beginDateTime);
                        params.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
                        Integer thingCount = statisticsService.countAllByParams(params);
                        beginDateTime = endDateTime;
                        countList.add(thingCount);
                        timeList.add(endTime);
                    }
                }
                if(!StringUtils.isEmpty(nearlyAMonthBegin) && StringUtils.isEmpty(nearlyAMonthEnd)){
                    long days = DateUtil.betweenDay(DateUtil.offsetMonth(date, -1), date, false);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    params.clear();
                    //??????????????????
                    long time = sdf.parse(nearlyAMonthBegin).getTime();
                    String beginDateTime = nearlyAMonthBegin;
                    for(int i = 1; i <= days; i++){
                        time += 3600000*24;
                        String endDateTime = sdf.format(new Date(time));
                        String endTime = sdf1.format(new Date(time));
                        params.put("endDateTime",endDateTime);
                        params.put("beginDateTime",beginDateTime);
                        params.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
                        Integer thingCount = statisticsService.countAllByParams(params);
                        beginDateTime = endDateTime;
                        countList.add(thingCount);
                        timeList.add(endTime);
                    }
                }
                if(!StringUtils.isEmpty(nearlyAYearBegin) && StringUtils.isEmpty(nearlyAYearEnd)){
                    params.clear();
                    //??????????????????
                    String beginDateTime = nearlyAYearBegin;
                    for(int i = 1; i <= 12; i++){
                        String endTime = DateUtil.offsetMonth(sdf.parse(beginDateTime), i).toString().substring(0,10);
                        params.put("endDateTime",DateUtil.offsetMonth(sdf.parse(beginDateTime), i).toString());
                        params.put("beginDateTime",DateUtil.offsetMonth(sdf.parse(beginDateTime), i-1).toString());
                        params.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
                        Integer thingCount = statisticsService.countAllByParams(params);
                        countList.add(thingCount);
                        timeList.add(endTime);
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
            data = new HashMap();
//            data.put("eventTrend",eventTrendCount);
            data.put("eventPlaceTypeTrends",countList);
            data.put("eventTrendDate",timeList);
            /*putEvents(eventType, data,type);*/
        }
        return AjaxResult.success(data);
        }else if(source.equals("visitSource")){
            Map<String,Object> data = new HashMap();
            return AjaxResult.success(data);
        }else if(source.equals("policeSource")){
            Map<String,Object> data = new HashMap();
            return AjaxResult.success(data);
        }else if(source.equals("sourceAction")){
            Map<String,Object> data = new HashMap();
            return AjaxResult.success(data);
        }else if(source.equals("other")){
            Map<String,Object> data = new HashMap();
            return AjaxResult.success(data);
        }
        return success();
    }
    /**
     * ??????--???????????????
     */
    @RequestMapping("index/procatTrend")
    @ResponseBody
    public AjaxResult procatTrend(String type){
        Map<String,Object> data = null;
        if(data == null){
            Date date = new Date();
            //??????????????????Map
            Map<String,Object> params = EventDateType(type,date);
            //?????????????????? //?????????????????????
//            List<Integer> eventTrendCount = new ArrayList<>();

            String nearlyAWeekBegin     =  (String)params.get("nearlyAWeekBegin");
            String nearlyAWeekEnd       =  (String)params.get("nearlyAWeekEnd");
            String nearlyAMonthBegin    =  (String)params.get("nearlyAMonthBegin");
            String nearlyAMonthEnd      =  (String)params.get("nearlyAMonthEnd");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<Integer> countList = new ArrayList<>();
            List<String> timeList = new ArrayList<>();
            List<Integer> juList = new ArrayList<>();
            List<Map> chuList = new ArrayList<>();
            List<Integer> xunList = new ArrayList<>();
//            List<String> fangList = new ArrayList<>();
            String zhiHandleRate=null;
            String juHandleRate =null;
            String xunHandleRate=null;
            String zongHandleRate=null;
            try{
                //??????????????????
                if(!StringUtils.isEmpty(nearlyAWeekBegin) && StringUtils.isEmpty(nearlyAWeekEnd)){
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    params.clear();
                    //??????????????????
                    long time = sdf.parse(nearlyAWeekBegin).getTime();
                    String beginDateTime = nearlyAWeekBegin;
                    for(int i = 1; i <= 7; i++){
                        time += 3600000*24;
                        String endDateTime = sdf.format(new Date(time));
                        String endTime = sdf1.format(new Date(time));
                        params.put("endDateTime",endDateTime);
                        params.put("beginDateTime",beginDateTime);
                        params.put("statusArr",new String[]{"1"});
                        Integer thingCount = propertyEventService.countAllByParams(params);
                        beginDateTime = endDateTime;
                        countList.add(thingCount);
                        timeList.add(endTime);
                    }
                    //?????? ?????????
                    Map<String,Object> eventPro = new HashMap<>();
                    Map<String,Object> thingResult = new HashMap<>();
                    thingResult.put("startTime",DateFormatUtils.format(DateUtils.getThisWeekMonday(),"yyyy-MM-dd HH:mm:ss"));
                    thingResult.put("statusArr","");
                    int propertyCount=propertyEventService.queryPropertyEventCount(thingResult);//??????????????????
                    thingResult.put("eventSource",new String[]{"1"});
                    thingResult.put("eventStatus",new String[]{"3"});
                    int zhiEndCount=propertyEventService.queryPropertyEventCount(thingResult);//??????????????????
                    thingResult.put("eventSource",new String[]{"2","3"});
                    thingResult.put("eventStatus",new String[]{"3"});
                    int juEndCount=propertyEventService.queryPropertyEventCount(thingResult);//??????????????????
                    thingResult.put("eventSource",new String[]{"5"});
                    thingResult.put("eventStatus",new String[]{"3"});
                    int xunEndCount=propertyEventService.queryPropertyEventCount(thingResult);//??????????????????
                    //????????????????????????
                    int peopertyEndCount=zhiEndCount+juEndCount+xunEndCount;
                    if(propertyCount!=0){
                        zongHandleRate = divideTo(new BigDecimal(propertyCount),new BigDecimal(peopertyEndCount))+"%";//??????????????????????????????
                        zhiHandleRate = divideTo(new BigDecimal(propertyCount),new BigDecimal(zhiEndCount))+"%";//??????????????????????????????
                        juHandleRate = divideTo(new BigDecimal(propertyCount),new BigDecimal(juEndCount))+"%";//??????????????????????????????
                        xunHandleRate = divideTo(new BigDecimal(propertyCount),new BigDecimal(xunEndCount))+"%";//??????????????????????????????
                    }else{
                        zongHandleRate="0%";//??????????????????
                        zhiHandleRate = "0%";//??????????????????????????????
                        juHandleRate = "0%";//??????????????????????????????
                        xunHandleRate = "0%";//??????????????????????????????
                    }
                    eventPro.put("zhiHandleRate",zhiHandleRate);
                    eventPro.put("juHandleRate",juHandleRate);
                    eventPro.put("xunHandleRate",xunHandleRate);
                    chuList.add(eventPro);
                }
                //??????????????????
                if(!StringUtils.isEmpty(nearlyAMonthBegin) && StringUtils.isEmpty(nearlyAMonthEnd)){
                    long days = DateUtil.betweenDay(DateUtil.offsetMonth(date, -1), date, false);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    params.clear();
                    //??????????????????
                    long time = sdf.parse(nearlyAMonthBegin).getTime();
                    String beginDateTime = nearlyAMonthBegin;
                    for(int i = 1; i <= days; i++){
                        time += 3600000*24;
                        String endDateTime = sdf.format(new Date(time));
                        String endTime = sdf1.format(new Date(time));
                        params.put("endDateTime",endDateTime);
                        params.put("beginDateTime",beginDateTime);
                        params.put("statusArr",new String[]{"1"});
                        Integer thingCount = propertyEventService.countAllByParams(params);
                        beginDateTime = endDateTime;
                        countList.add(thingCount);
                        timeList.add(endTime);
                    }
                    //?????? ?????????
                    Map<String,Object> eventPro = new HashMap<>();
                    Map<String,Object> thingResult = new HashMap<>();
                    thingResult.put("startTime",DateFormatUtils.format(DateUtils.getThisMonthMonday(),"yyyy-MM-dd HH:mm:ss"));
                    thingResult.put("statusArr","");
                    int propertyCount=propertyEventService.queryPropertyEventCount(thingResult);//??????????????????
                    thingResult.put("eventSource",new String[]{"1"});
                    thingResult.put("eventStatus",new String[]{"3"});
                    int zhiEndCount=propertyEventService.queryPropertyEventCount(thingResult);//??????????????????
                    thingResult.put("eventSource",new String[]{"2","3"});
                    thingResult.put("eventStatus",new String[]{"3"});
                    int juEndCount=propertyEventService.queryPropertyEventCount(thingResult);//??????????????????
                    thingResult.put("eventSource",new String[]{"5"});
                    thingResult.put("eventStatus",new String[]{"3"});
                    int xunEndCount=propertyEventService.queryPropertyEventCount(thingResult);//??????????????????
                    //????????????????????????
                    int peopertyEndCount=zhiEndCount+juEndCount+xunEndCount;
                    if(propertyCount!=0){
                        zongHandleRate = divideTo(new BigDecimal(propertyCount),new BigDecimal(peopertyEndCount))+"%";//??????????????????????????????
                        zhiHandleRate = divideTo(new BigDecimal(propertyCount),new BigDecimal(zhiEndCount))+"%";//??????????????????????????????
                        juHandleRate = divideTo(new BigDecimal(propertyCount),new BigDecimal(juEndCount))+"%";//??????????????????????????????
                        xunHandleRate = divideTo(new BigDecimal(propertyCount),new BigDecimal(xunEndCount))+"%";//??????????????????????????????
                    }else{
                        zongHandleRate="0%";//??????????????????
                        zhiHandleRate = "0%";//??????????????????????????????
                        juHandleRate = "0%";//??????????????????????????????
                        xunHandleRate = "0%";//??????????????????????????????
                    }
                    eventPro.put("zongHandleRate",zongHandleRate);
                    eventPro.put("zhiHandleRate",zhiHandleRate);
                    eventPro.put("juHandleRate",juHandleRate);
                    eventPro.put("xunHandleRate",xunHandleRate);
                    chuList.add(eventPro);
                }
                //??????????????????
                if(!StringUtils.isEmpty(nearlyAWeekBegin) && StringUtils.isEmpty(nearlyAWeekEnd)){
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    params.clear();
                    //??????????????????
                    long time = sdf.parse(nearlyAWeekBegin).getTime();
                    String beginDateTime = nearlyAWeekBegin;
                    for(int i = 1; i <= 7; i++){
                        time += 3600000*24;
                        String endDateTime = sdf.format(new Date(time));
                        String endTime = sdf1.format(new Date(time));
                        params.put("endDateTime",endDateTime);
                        params.put("beginDateTime",beginDateTime);
                        params.put("statusArr",new String[]{"2","3"});
                        Integer thingCount = propertyEventService.countAllByParams(params);
                        beginDateTime = endDateTime;
                        juList.add(thingCount);
                    }
                }
                //??????????????????
                if(!StringUtils.isEmpty(nearlyAMonthBegin) && StringUtils.isEmpty(nearlyAMonthEnd)){
                    long days = DateUtil.betweenDay(DateUtil.offsetMonth(date, -1), date, false);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    params.clear();
                    //??????????????????
                    long time = sdf.parse(nearlyAMonthBegin).getTime();
                    String beginDateTime = nearlyAMonthBegin;
                    for(int i = 1; i <= days; i++){
                        time += 3600000*24;
                        String endDateTime = sdf.format(new Date(time));
                        String endTime = sdf1.format(new Date(time));
                        params.put("endDateTime",endDateTime);
                        params.put("beginDateTime",beginDateTime);
                        params.put("statusArr",new String[]{"1"});
                        Integer thingCount = propertyEventService.countAllByParams(params);
                        beginDateTime = endDateTime;
                        juList.add(thingCount);
                    }
                }
                //??????????????????
                if(!StringUtils.isEmpty(nearlyAWeekBegin) && StringUtils.isEmpty(nearlyAWeekEnd)){
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    params.clear();
                    //??????????????????
                    long time = sdf.parse(nearlyAWeekBegin).getTime();
                    String beginDateTime = nearlyAWeekBegin;
                    for(int i = 1; i <= 7; i++){
                        time += 3600000*24;
                        String endDateTime = sdf.format(new Date(time));
                        String endTime = sdf1.format(new Date(time));
                        params.put("endDateTime",endDateTime);
                        params.put("beginDateTime",beginDateTime);
                        params.put("statusArr",new String[]{"2","3"});
                        Integer thingCount = propertyEventService.countAllByParams(params);
                        beginDateTime = endDateTime;
                        xunList.add(thingCount);
                    }
                }
                //??????????????????
                if(!StringUtils.isEmpty(nearlyAMonthBegin) && StringUtils.isEmpty(nearlyAMonthEnd)){
                    long days = DateUtil.betweenDay(DateUtil.offsetMonth(date, -1), date, false);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    params.clear();
                    //??????????????????
                    long time = sdf.parse(nearlyAMonthBegin).getTime();
                    String beginDateTime = nearlyAMonthBegin;
                    for(int i = 1; i <= days; i++){
                        time += 3600000*24;
                        String endDateTime = sdf.format(new Date(time));
                        String endTime = sdf1.format(new Date(time));
                        params.put("endDateTime",endDateTime);
                        params.put("beginDateTime",beginDateTime);
                        params.put("statusArr",new String[]{"1"});
                        Integer thingCount = propertyEventService.countAllByParams(params);
                        beginDateTime = endDateTime;
                        xunList.add(thingCount);
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
            data = new HashMap();
//            data.put("eventTrend",eventTrendCount);
            data.put("zhiTypeTrends",countList);
            data.put("zhiTrendDate",timeList);
            data.put("juTypeTrends",juList);
            data.put("xunTypeTrends",xunList);
            data.put("chuEventTrends",chuList);
            /*putEvents(eventType, data,type);*/
        }
        return AjaxResult.success(data);
    }


    /**
     * ????????????
     */
    @RequestMapping("index/general")
    @ResponseBody
    public AjaxResult general(String housingEstate){
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> thingResult = new HashMap<>();
        thingResult.put("housingEstate",housingEstate);
        int roomCount=iXlRoomService.queryRoomCount(thingResult);
        int chuRoomCount=xlLeaseService.queryRoomListCount(thingResult);
        int housingEstateCount=iXlPersonnelService.queryPersonCountAll(thingResult);
        thingResult.put("liveType","1");
        int liuCount=iXlPersonnelService.queryPersonCountAll(thingResult);
        thingResult.put("liveType","2");
        int huCount=iXlPersonnelService.queryPersonCountAll(thingResult);
        params.put("roomCount",roomCount);
        params.put("chuRoomCount",chuRoomCount);
        params.put("housingEstateCount",housingEstateCount);
        params.put("zuCount",liuCount);
        params.put("yeCount",huCount);
        return AjaxResult.success(params);
    }

    /**
     * ????????????--??????????????????
     */
    @RequestMapping("index/urbanEventSource")
    @ResponseBody
    public AjaxResult eventSource(){
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> thingResult = new HashMap<>();
        thingResult.put("startTime",DateFormatUtils.format(DateUtils.getThisMonthMonday(),"yyyy-MM-dd HH:mm:ss"));
        int count=eventService.eventSource(thingResult);
        thingResult.put("reportType",1);
        int zhiCount=eventService.eventSource(thingResult);
        String zhiRate = divideTo(new BigDecimal(count),new BigDecimal(zhiCount))+"%";//??????????????????????????????
        thingResult.put("reportType",2);
        int renCount=eventService.eventSource(thingResult);
        String renRate = divideTo(new BigDecimal(count),new BigDecimal(renCount))+"%";//??????????????????????????????
        params.put("count",count);
        params.put("zhiCount",zhiCount);
        params.put("renCount",renCount);
        params.put("zhiRate",zhiRate);
        params.put("renRate",renRate);
        return AjaxResult.success(params);
    }
    /**
     *????????????--????????????
     */
    @RequestMapping("index/eventNotice")
    @ResponseBody
    public AjaxResult eventNotice(){
        List list = new ArrayList<>();
        List<HkEventInfo> hkEventInfos=eventService.queryEventOne();
        for(int i=0;i<hkEventInfos.size();i++){
            Map<String,Object> thingResult = new HashMap<>();
            thingResult.put("eventId",hkEventInfos.get(i).getEventId());
            thingResult.put("riskLevel",hkEventInfos.get(i).getRiskLevel());
            thingResult.put("longitude",hkEventInfos.get(i).getLongitude());
            thingResult.put("latitude",hkEventInfos.get(i).getLatitude());
            list.add(thingResult);
        }
        return AjaxResult.success(list);
    }

    /**
     * ????????????--??????????????????
     */
    @RequestMapping("index/propertyEfficiency")
    @ResponseBody
    public AjaxResult propertyEfficiency(){
        Map<String,Object> thingResult = new HashMap<>();
        Map<String,Object> params = new HashMap<>();
        List<XlEstateManagement> estateManagements=estateManagementService.queryPropertEfficiency();//????????????
        int estate=estateManagementService.queryPropert(thingResult);//????????????
        Integer ping =0;
        for(int i=0;i<estateManagements.size();i++){
            if(estateManagements.get(i).getEstateStar()==null){

            }else if(estateManagements.get(i).getEstateStar().equals("??????")){
                ping = ping+1;
            }else if(estateManagements.get(i).getEstateStar().equals("??????")){
                ping=ping+2;
            }else if(estateManagements.get(i).getEstateStar().equals("??????")){
                ping=ping+3;
            }else if(estateManagements.get(i).getEstateStar().equals("??????")){
                ping=ping+4;
            }else if(estateManagements.get(i).getEstateStar().equals("??????")){
                ping=ping+5;
            }
        }
        int average=ping/estate;//??????????????????
        //????????????????????????
        thingResult.put("eventSource",new String[]{"4"});
        thingResult.put("startTime",DateFormatUtils.format(DateUtils.getThisMonthMonday(),"yyyy-MM-dd HH:mm:ss"));
        int xunEndCount=propertyEventService.queryPropertyEventCount(thingResult);//??????????????????
        int endCount=propertyEventService.queryPropertyEndCount();
        params.put("average",average);
        params.put("zhengCount",xunEndCount);
        params.put("endCount",endCount);
        return AjaxResult.success(params);
    }

    /**
     *????????????--??????????????????
     */
    @RequestMapping("index/managementList")
    @ResponseBody
    public AjaxResult managementList(String reportType,String startTime,String endTime,Integer eventAlertStatus,String riskLevel){
        startPage();
       List list= new ArrayList<>();
        Map<String,Object> thingResult = new HashMap<>();
        thingResult.put("reportType",reportType);
        thingResult.put("startTime",startTime);
        thingResult.put("endTime",endTime);
        thingResult.put("eventAlertStatus",eventAlertStatus);
        if(riskLevel!=null &&reportType.equals("??????")){
            thingResult.put("riskLevel","99");
        }else if(riskLevel!=null &&reportType.equals("??????")){
            thingResult.put("riskLevel","999");
        }else{
            thingResult.put("riskLevel","1");
        }
        thingResult.put("riskLevel",riskLevel);
        List<HkEventInfo> hkEventInfos= eventService.queryeventChengInfo(thingResult);
        for(int i=0;i<hkEventInfos.size();i++){
            Map<String,Object> params = new HashMap<>();
            params.put("eventType",hkEventInfos.get(i).getEventType());
            params.put("cameraName",hkEventInfos.get(i).getCameraName());
            params.put("createTime",hkEventInfos.get(i).getCreateTime());
            params.put("longitude",hkEventInfos.get(i).getLongitude());
            params.put("latitude",hkEventInfos.get(i).getLatitude());
            params.put("eventStatus",hkEventInfos.get(i).getEventAlertStatus());
            params.put("cameraIndexCode",hkEventInfos.get(i).getCameraIndexCode());
            params.put("image",hkEventInfos.get(i).getEventImage());
            list.add(params);
        }
        return AjaxResult.success(list);
    }

    /**
     * ???????????????
     * @return
     */
    @RequestMapping("index/communityGrid")
    @ResponseBody
    public AjaxResult communityGrid(String communityName){
        List list = new ArrayList<>();
        //????????????
//        Integer s=iSysRegionService.queryCommunit();
        SysRegion sysRegion = new SysRegion();
        sysRegion.setRegionName(communityName);
        List<SysRegion> regions=iSysRegionService.queryCommunitList(sysRegion);
        for(int i=0;i<regions.size();i++){
            Map<String,Object> thingResult = new HashMap<>();
            thingResult.put("deptName",regions.get(i).getRegionName());
            thingResult.put("deptId",regions.get(i).getRegionId());
            list.add(thingResult);
        }
        return AjaxResult.success(list);
    }

    /**
     * ????????????????????????
     */
    @RequestMapping("index/GridList")
    @ResponseBody
    public AjaxResult GridList(String grilName,Integer deptId){
        if(deptId == null){
            List list = new ArrayList<>();
            SysRegion sysRegion = new SysRegion();
            sysRegion.setRegionName(grilName);
            List<SysRegion> sysRegions = iSysRegionService.queryCommunitWangListAll(sysRegion);
            for(int i=0;i<sysRegions.size();i++){
                Map<String,Object> thingResult = new HashMap<>();
                thingResult.put("deptName",sysRegions.get(i).getRegionName());
                thingResult.put("deptId",sysRegions.get(i).getRegionId());
                list.add(thingResult);
            }
            return AjaxResult.success(list);
        }else{
            List list = new ArrayList<>();
            SysRegion sysRegion = new SysRegion();
            sysRegion.setParentId(String.valueOf(deptId));
            sysRegion.setRegionName(grilName);
            List<SysRegion> regions=iSysRegionService.queryCommunitWangList(sysRegion);
            for(int i=0;i<regions.size();i++){
                Map<String,Object> thingResult = new HashMap<>();
                thingResult.put("deptName",regions.get(i).getRegionName());
                thingResult.put("deptId",regions.get(i).getRegionId());
                list.add(thingResult);
            }
            return AjaxResult.success(list);
        }
    }

    /**
     * ????????????--????????????
     */
    @RequestMapping("index/managementEquipment")
    @ResponseBody
    public AjaxResult managementEquipment(){
        List list = new ArrayList<>();
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> policeParams = new HashMap<>();
        List<XlEquipmentInfo> equipmentInfos=iXlEquipmentInfoService.queryEquipmentPoliceList();//??????????????????
        for(int i=0;i<equipmentInfos.size();i++){
            Map<String,Object> thingResult = new HashMap<>();
            thingResult.put("equipmentName",equipmentInfos.get(i).getEquipmentName());
            thingResult.put("equipmentType",equipmentInfos.get(i).getEquipmentType());
            thingResult.put("deviceType",equipmentInfos.get(i).getDeviceType());
            thingResult.put("equipmentSerialNumber",equipmentInfos.get(i).getEquipmentSerialNumber());
            thingResult.put("equipmentModel",equipmentInfos.get(i).getEquipmentModel());
            thingResult.put("equipmentAdress",equipmentInfos.get(i).getEquipmentAdress());
            thingResult.put("longitude",equipmentInfos.get(i).getLongitude());
            thingResult.put("latitude",equipmentInfos.get(i).getLatitude());
            list.add(thingResult);
        }
        //??????????????????
        params.put("equipmentOrganization","33011068");
        int policeCount=iXlEquipmentInfoService.queryEquipmentPoliceCount(params);
        //?????????
        params.put("equipmentStatus",1);
        int onLineCount=iXlEquipmentInfoService.queryEquipmentPoliceCount(params);
        //?????????
        params.put("equipmentStatus",0);
        int offLineCount=iXlEquipmentInfoService.queryEquipmentPoliceCount(params);
        policeParams.put("policeCount",policeCount);
        policeParams.put("onLineCount",onLineCount);
        policeParams.put("offLineCount",offLineCount);
        list.add(policeParams);
        return AjaxResult.success(list);
    }

    /**
     * ?????????????????????
     */
    @RequestMapping("index/garbageTruck")
    @ResponseBody
    public AjaxResult garbageTruck(String startTime,String endTime){
            Map<String,Object> policeParams = new HashMap<>();
            policeParams.put("startTime",startTime);
            policeParams.put("endTime",endTime);
            List list=new ArrayList<>();
            List<XlGpsWeigh>  xlGpsWeighs=locationReqService.queryGarbageList(policeParams);
            for(int i=0;i<xlGpsWeighs.size();i++){
                Map<String,Object> params = new HashMap<>();
                params.put("licensePlate",xlGpsWeighs.get(i).getLicensePlate());
                params.put("locationSim",xlGpsWeighs.get(i).getLocationSim());
                params.put("tripartLng",xlGpsWeighs.get(i).getTripartLng());
                params.put("tripartLat",xlGpsWeighs.get(i).getTripartLat());
                params.put("tripartHeight",xlGpsWeighs.get(i).getTripartHeight());
                params.put("tripartSpeed",xlGpsWeighs.get(i).getTripartSpeed());
                params.put("tripartDirection",xlGpsWeighs.get(i).getTripartDirection());
                params.put("recortTime",xlGpsWeighs.get(i).getRecortTime());
                list.add(params);
            }
            return AjaxResult.success(list);
    }

    /**
     * 2D--??????
     */
    @RequestMapping("index/vehicleInfo")
    @ResponseBody
    public AjaxResult vehicleInfo(String startTime,String endTime,String tripartName){
        if(tripartName==null){
            Map<String,Object> policeParams = new HashMap<>();
            policeParams.put("startTime",startTime);
            policeParams.put("endTime",endTime);
            List list=new ArrayList<>();
            List<XlGpsWeigh>  xlGpsWeighs=locationReqService.queryGarbageListAll(policeParams);
            for(int i=0;i<xlGpsWeighs.size();i++){
                Map<String,Object> params = new HashMap<>();
                params.put("licensePlate",xlGpsWeighs.get(i).getLicensePlate());
                params.put("locationSim",xlGpsWeighs.get(i).getLocationSim());
                params.put("tripartLng",xlGpsWeighs.get(i).getTripartLng());
                params.put("tripartLat",xlGpsWeighs.get(i).getTripartLat());
                //???????????????. ??????-??????
                double[] convertPoint = GPSUtils.gcj02_To_Bd09(Double.parseDouble(xlGpsWeighs.get(i).getTripartLat()), Double.parseDouble(xlGpsWeighs.get(i).getTripartLng()));
//                System.out.println(convertPoint[1]+"----------"+convertPoint[0]);
                Double[] points = {convertPoint[1], convertPoint[0]};
                Map<String, Object> map1 = hkMapService.calculateRegionByLongitudeAndLatitude(points);
                String regionCode = map1.get("regionId") + "";
                String regionName = map1.get("regionName") + "";
                params.put("regionName",regionName);
                params.put("tripartHeight",xlGpsWeighs.get(i).getTripartHeight());
                params.put("tripartSpeed",xlGpsWeighs.get(i).getTripartSpeed());
                params.put("tripartDirection",xlGpsWeighs.get(i).getTripartDirection());
                params.put("recortTime",xlGpsWeighs.get(i).getRecortTime());
                params.put("wasteCar",xlGpsWeighs.get(i).getTripartName());
                list.add(params);
            }
            return AjaxResult.success(list);
        }else if(tripartName.equals("waste")){
            Map<String,Object> policeParams = new HashMap<>();
            policeParams.put("startTime",startTime);
            policeParams.put("endTime",endTime);
            List list=new ArrayList<>();
            List<XlGpsWeigh>  xlGpsWeighs=locationReqService.queryGarbageList(policeParams);
            for(int i=0;i<xlGpsWeighs.size();i++){
                Map<String,Object> params = new HashMap<>();
                params.put("licensePlate",xlGpsWeighs.get(i).getLicensePlate());
                params.put("locationSim",xlGpsWeighs.get(i).getLocationSim());
                params.put("tripartLng",xlGpsWeighs.get(i).getTripartLng());
                params.put("tripartLat",xlGpsWeighs.get(i).getTripartLat());
                //???????????????. ??????-??????
                double[] convertPoint = GPSUtils.gcj02_To_Bd09(Double.parseDouble(xlGpsWeighs.get(i).getTripartLat()), Double.parseDouble(xlGpsWeighs.get(i).getTripartLng()));
//                System.out.println(convertPoint[1]+"----------"+convertPoint[0]);
                Double[] points = {convertPoint[1], convertPoint[0]};
                Map<String, Object> map1 = hkMapService.calculateRegionByLongitudeAndLatitude(points);
                String regionCode = map1.get("regionId") + "";
                String regionName = map1.get("regionName") + "";
                params.put("regionName",regionName);
                params.put("tripartHeight",xlGpsWeighs.get(i).getTripartHeight());
                params.put("tripartSpeed",xlGpsWeighs.get(i).getTripartSpeed());
                params.put("tripartDirection",xlGpsWeighs.get(i).getTripartDirection());
                params.put("recortTime",xlGpsWeighs.get(i).getRecortTime());
                params.put("wasteCar",xlGpsWeighs.get(i).getTripartName());
                list.add(params);
            }
            return AjaxResult.success(list);
        }else if(tripartName.equals("sprinkle")){
            List list=new ArrayList<>();
            return AjaxResult.success(list);
        }
        return AjaxResult.success();
    }

    /**
     *???????????????
     */
    @RequestMapping("index/householder")
    @ResponseBody
    public AjaxResult householder(String name){
//        startPage();
        List list=new ArrayList<>();
        Map<String,Object> params = new HashMap<>();
        params.put("name",name);
        List<XlPersonnel> xlPersonnels=iXlPersonnelService.queryPersonHu(params);
        for(int i=0;i<xlPersonnels.size();i++){
            Map<String,Object> thingResult = new HashMap<>();
            thingResult.put("name",xlPersonnels.get(i).getName());
            thingResult.put("nameId",xlPersonnels.get(i).getId());
            list.add(thingResult);
        }
        return AjaxResult.success(list);
    }

    /**
     * ??????????????????
     */
    @RequestMapping("index/rommAdress")
    @ResponseBody
    public AjaxResult rommAdress(Integer nameId){
        if(nameId ==null){
            return AjaxResult.success("???????????????id");
        }else{
            Map<String,Object> params = new HashMap<>();
            String villageCode=iXlPersonnelService.queryPersonVillageCode(nameId);
            if(villageCode == null){
                return AjaxResult.success(params);
            }else{
                Integer roomId=Integer.parseInt(villageCode);
                String adress=iXlRoomService.queryRoomAdress(roomId);
                params.put("adress",adress);
                return AjaxResult.success(params);
            }

        }
    }

    /**
     * ?????????????????????
     */
    @RequestMapping("index/propertyDown")
    @ResponseBody
    public AjaxResult propertyDown(String propertyName){
        XlEstateManagement xlEstateManagement = new XlEstateManagement();
        xlEstateManagement.setEstateName(propertyName);
        List<XlEstateManagement> estateManagements=estateManagementService.queryPropertyDownName(xlEstateManagement);
        List list = new ArrayList<>();
        for(int i=0;i<estateManagements.size();i++){
            Map<String,Object> params = new HashMap<>();
            params.put("propertyName",estateManagements.get(i).getEstateName());
            params.put("propertyId",estateManagements.get(i).getId());
            list.add(params);
        }
        return AjaxResult.success(list);
    }
    /**
     * ????????????
     */
    @RequestMapping("index/starStatistics")
    @ResponseBody
    public AjaxResult starStatistics(){
        List list = new ArrayList<>();
        Map<String,Object> paramsAll = new HashMap<>();
        Map<String,Object> paramsOne = new HashMap<>();
        Map<String,Object> paramsTwo = new HashMap<>();
        Map<String,Object> paramsThree = new HashMap<>();
        Map<String,Object> paramsFour = new HashMap<>();
        Map<String,Object> paramsFive = new HashMap<>();
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> thingResult = new HashMap<>();
        int estateManagements=estateManagementService.queryPropert(thingResult);//????????????
        thingResult.put("statusArr","??????");//??????????????????
        int oneStar=estateManagementService.queryPropert(thingResult);
        thingResult.put("statusArr","??????");//??????????????????
        int twoStar=estateManagementService.queryPropert(thingResult);
        thingResult.put("statusArr","??????");//??????????????????
        int threeStar=estateManagementService.queryPropert(thingResult);
        thingResult.put("statusArr","??????");//??????????????????
        int fourStar=estateManagementService.queryPropert(thingResult);
        thingResult.put("statusArr","??????");//??????????????????
        int fiveStar=estateManagementService.queryPropert(thingResult);
        String oneStarProportion = divideTo(new BigDecimal(estateManagements),new BigDecimal(oneStar))+"%";
//        String twoStarProportion=(float)twoStar/(float)estateManagements*100+"%";//????????????
        String twoStarProportion = divideTo(new BigDecimal(estateManagements),new BigDecimal(twoStar))+"%";
//        String threeStarProportion=(float)threeStar/(float)estateManagements*100+"%";//????????????
        String threeStarProportion = divideTo(new BigDecimal(estateManagements),new BigDecimal(threeStar))+"%";
//        String fourStarProportion=(float)fourStar/(float)estateManagements*100+"%";//????????????
        String fourStarProportion = divideTo(new BigDecimal(estateManagements),new BigDecimal(fourStar))+"%";
//        String fiveStarProportion=(float)fiveStar/(float)estateManagements*100+"%";//????????????
        String fiveStarProportion = divideTo(new BigDecimal(estateManagements),new BigDecimal(fiveStar))+"%";
        paramsOne.put("star","??????");
        paramsOne.put("count",oneStar);
        paramsOne.put("starProportion",oneStarProportion);
        paramsTwo.put("star","??????");
        paramsTwo.put("count",twoStar);
        paramsTwo.put("starProportion",twoStarProportion);
        paramsThree.put("star","??????");
        paramsThree.put("count",threeStar);
        paramsThree.put("starProportion",threeStarProportion);
        paramsFour.put("star","??????");
        paramsFour.put("count",fourStar);
        paramsFour.put("starProportion",fourStarProportion);
        paramsFive.put("star","??????");
        paramsFive.put("count",fiveStar);
        paramsFive.put("starProportion",fiveStarProportion);
        list.add(paramsOne);
        list.add(paramsTwo);
        list.add(paramsThree);
        list.add(paramsFour);
        list.add(paramsFive);
        return AjaxResult.success(list);
    }

    /**
     * ???????????????
     */
    @RequestMapping("index/deptDown")
    @ResponseBody
    public AjaxResult deptDown(String deptName){
        SysDept sysDept = new SysDept();
        sysDept.setDeptName(deptName);
        List<SysDept> depts=deptService.queryDeptNameAll(sysDept);
        List list = new ArrayList<>();
        for(int i=0;i<depts.size();i++){
            Map<String,Object> params = new HashMap<>();
            params.put("deptALlName",depts.get(i).getDeptName());
            params.put("deptAllId",depts.get(i).getDeptId());
            list.add(params);
        }
        return AjaxResult.success(list);
    }

    /**
     * ?????????-??????
     */
    @RequestMapping("index/wanggeEventCount")
    @ResponseBody
    public AjaxResult wanggeEventCount(String eventType,String startTime,String endTime,String status,String grade){
        SysRegion sysRegion = new SysRegion();
        List list = new ArrayList<>();
        List<SysRegion> sysRegions=iSysRegionService.queryCommunitList(sysRegion);
        for(int i=0;i<sysRegions.size();i++){
            Long regionId = sysRegions.get(i).getRegionId();
            sysRegion.setParentId(String.valueOf(regionId));
            List<SysRegion> grilList= iSysRegionService.queryGrilName(sysRegion);
            for(int j=0;j<grilList.size();j++){
                String gradeType=null;
                Map<String,Object> paramsAll = new HashMap<>();
                Map<String,Object> params = new HashMap<>();
                Long regionCode = grilList.get(j).getRegionId();
                paramsAll.put("eventType",eventType);
                paramsAll.put("startTime",startTime);
                paramsAll.put("endTime",endTime);
                paramsAll.put("status",status);
                if(grade==null){

                }else if(grade.equals("??????")){
                    gradeType="99";
                }else if(grade.equals("??????")){
                    gradeType="999";
                }else{
                    gradeType="1";
                }
                paramsAll.put("grade",gradeType);
                paramsAll.put("regionIndexCode",regionCode);
                int count=eventService.queryeventCountAll(paramsAll);
                paramsAll.put("statusArr",new String[]{"2"});
                int weiCount=eventService.queryeventCountAll(paramsAll);
                paramsAll.put("statusArr",new String[]{"3","5"});
                int chuCount=eventService.queryeventCountAll(paramsAll);
                paramsAll.put("statusArr",new String[]{"6","7","9"});
                int endCount=eventService.queryeventCountAll(paramsAll);
                params.put("regionId",grilList.get(j).getRegionId());
                params.put("grilName",grilList.get(j).getRegionName());
                params.put("eventCount",count);
                params.put("weiCount",weiCount);
                params.put("chuCount",chuCount);
                params.put("endCount",endCount);
                list.add(params);
            }
        }
        return AjaxResult.success(list);
    }

    /**
     * ?????????--??????

     * @return
     */
    @RequestMapping("index/fiveColorDiagramPerson")
    @ResponseBody
    public AjaxResult fiveColorDiagramPerson(String personName,String phone,String idcard,String liveType,String nativePlace,String polity,String villageName,String ethnicGroup){
        SysRegion sysRegion = new SysRegion();
        List list = new ArrayList<>();
        List<SysRegion> sysRegions=iSysRegionService.queryCommunitList(sysRegion);
        for(int i=0;i<sysRegions.size();i++){
            Long regionId = sysRegions.get(i).getRegionId();
            sysRegion.setParentId(String.valueOf(regionId));
            List<SysRegion> grilList= iSysRegionService.queryGrilName(sysRegion);
            for(int j=0;j<grilList.size();j++){
                Long regionCode = grilList.get(j).getRegionId();
                Map<String,Object> paramsAll = new HashMap<>();
                Map<String,Object> params = new HashMap<>();
                paramsAll.put("personName",personName);
                paramsAll.put("phone",phone);
                paramsAll.put("idcard",idcard);
                paramsAll.put("liveType",liveType);
                paramsAll.put("nativePlace",nativePlace);
                paramsAll.put("polity",polity);
                paramsAll.put("villageName",villageName);
                paramsAll.put("ethnicGroup",ethnicGroup);
                paramsAll.put("regionIndexCode",regionCode);
                Integer personnels=iXlPersonnelService.queryPersonAllList(paramsAll);
                paramsAll.put("statusArr",new String[]{"1"});
                Integer liupersonnels=iXlPersonnelService.queryPersonAllList(paramsAll);
                paramsAll.put("statusArr",new String[]{"2"});
                Integer hupersonnels=iXlPersonnelService.queryPersonAllList(paramsAll);
                params.put("regionId",regionCode);
                params.put("grilName",grilList.get(j).getRegionName());
                params.put("personnelsCount",personnels);
                params.put("liupersonnels",liupersonnels);
                params.put("hupersonnels",hupersonnels);
                list.add(params);
            }
        }
        return AjaxResult.success(list);
    }

    /**
     * ?????????--??????
     */
    @RequestMapping("index/fiveColorDiagramRoom")
    @ResponseBody
    public AjaxResult fiveColorDiagramRoom(String communityName,String roomType,String roomLiveType,String villageName,String blurred){
        SysRegion sysRegion = new SysRegion();
        List list = new ArrayList<>();
        List<SysRegion> sysRegions=iSysRegionService.queryCommunitList(sysRegion);
        for(int i=0;i<sysRegions.size();i++){
            Long regionId = sysRegions.get(i).getRegionId();
            sysRegion.setParentId(String.valueOf(regionId));
            List<SysRegion> grilList= iSysRegionService.queryGrilName(sysRegion);
            for(int j=0;j<grilList.size();j++){
                Long regionCode = grilList.get(j).getRegionId();
                Map<String,Object> paramsAll = new HashMap<>();
                Map<String,Object> params = new HashMap<>();
                paramsAll.put("communityName",communityName);
                paramsAll.put("roomType",roomType);
                paramsAll.put("roomLiveType",roomLiveType);
                paramsAll.put("villageName",villageName);
                paramsAll.put("blurred",blurred);
                paramsAll.put("regionIndexCode",regionCode);
                Integer xlRoomsCount=iXlRoomService.queryRoomListCount(paramsAll);
                paramsAll.put("statusArr",new String[]{"1"});
                Integer sahngXlRooms=iXlRoomService.queryRoomListCount(paramsAll);
                paramsAll.put("statusArr",new String[]{"2"});
                Integer ziXlRooms=iXlRoomService.queryRoomListCount(paramsAll);
                params.put("regionId",regionCode);
                params.put("grilName",grilList.get(j).getRegionName());
                params.put("xlRoomsCount",xlRoomsCount);
                params.put("sahngXlRooms",sahngXlRooms);
                params.put("ziXlRooms",ziXlRooms);
                list.add(params);
            }
        }
        return AjaxResult.success(list);
    }

    /**
     * ?????????---??????
     */
    @RequestMapping("index/fiveColorDiagramEnterprise")
    @ResponseBody
    public AjaxResult fiveColorDiagramEnterprise(){

        return AjaxResult.success();
    }

    /**
     * ?????????---??????
     */
    @RequestMapping("index/fiveColorDiagramEquipment")
    @ResponseBody
    public AjaxResult fiveColorDiagramEquipment(){
        return AjaxResult.success();
    }

    /**
     * ?????????--??????
     */
    @RequestMapping("index/heatingPowerEvent")
    @ResponseBody
    public AjaxResult heatingPowerEvent(String status){
        Map<String,Object> thingResult = new HashMap<>();
        thingResult.put("eventType",status);
        List list = new ArrayList<>();
        List<EventListCountVo> eventListCountVos=eventService.queryLongitude(thingResult);
        for(int i=0;i<eventListCountVos.size();i++){
            Map<String,Object> params = new HashMap<>();
            params.put("count",eventListCountVos.get(i).getCount());
            params.put("longitude",eventListCountVos.get(i).getLongitude());
            params.put("latitude",eventListCountVos.get(i).getLatitude());
            list.add(params);
        }
        return AjaxResult.success(list);
    }

    /**
     * ?????????--??????
     */
    @RequestMapping("index/heatingPowerRoom")
    @ResponseBody
    public AjaxResult heatingPowerRoom(){
        return AjaxResult.success();
    }

    /**
     * ?????????--??????
     */
    @RequestMapping("index/heatingPowerPerson")
    @ResponseBody
    public AjaxResult heatingPowerPerson(){
        return AjaxResult.success();
    }

    /**
     * ?????????--??????
     */
    @RequestMapping("index/heatingPowerEnterprise")
    @ResponseBody
    public AjaxResult heatingPowerEnterprise(){
        return AjaxResult.success();
    }

    /**
     * ?????????--??????
     */
    @RequestMapping("index/heatingPowerEquipment")
    @ResponseBody
    public AjaxResult heatingPowerEquipment(String type){
        Map<String,Object> thingResult = new HashMap<>();
        thingResult.put("eventType",type);
        List list = new ArrayList<>();
        List<EventListCountVo> eventListCountVos=iXlEquipmentInfoService.queryEquipmentLongitude(thingResult);
        for(int i=0;i<eventListCountVos.size();i++){
            Map<String,Object> params = new HashMap<>();
            params.put("count",eventListCountVos.get(i).getCount());
            params.put("longitude",eventListCountVos.get(i).getLongitude());
            params.put("latitude",eventListCountVos.get(i).getLatitude());
            list.add(params);
        }
        return AjaxResult.success(list);
    }


    /**
     * ????????????-???????????????????????????????????????
     */
    @RequestMapping("index/eventCountSort")
    @ResponseBody
    public AjaxResult eventCountSort(String type,Integer showCount){
        if(type.equals("community")){
            SysRegion sysRegion = new SysRegion();
            List list = new ArrayList<>();
            List<SysRegion> sysRegions=iSysRegionService.queryCommunitList(sysRegion);
            Integer communityCount=0;
            for(int i=0;i<sysRegions.size();i++){
                Long regionId = sysRegions.get(i).getRegionId();
                sysRegion.setParentId(String.valueOf(regionId));
                List<SysRegion> grilList= iSysRegionService.queryGrilName(sysRegion);
                for(int j=0;j<grilList.size();j++){
                    Long regionCode = grilList.get(j).getRegionId();
                    Map<String,Object> paramsAll = new HashMap<>();
                    Map<String,Object> params = new HashMap<>();
                    paramsAll.put("regionIndexCode",regionCode);
                    int count=eventService.queryeventCountAll(paramsAll);
                    communityCount = communityCount+count;
                    if(j==grilList.size()-1){
                        params.put("communityName",sysRegions.get(i).getRegionName());
                        params.put("communityEventCount",communityCount);
                        list.add(params);
                        communityCount=0;
                        if(showCount !=null && showCount !=0){
                            showCount = showCount-1;
                            if(showCount == 0){
                                return AjaxResult.success(list);
                            }
                        }

                    }
                }
            }
            return AjaxResult.success(list);
        }else if(type.equals("department")){
            return AjaxResult.success();
        }
        return AjaxResult.success();
    }

    /**
     * ???????????????
     */
    @RequestMapping("index/disposalRate")
    @ResponseBody
    public AjaxResult disposalRate(String type,Integer showCount){
        if(type.equals("community")){
            SysRegion sysRegion = new SysRegion();
            List list = new ArrayList<>();
            List<SysRegion> sysRegions=iSysRegionService.queryCommunitList(sysRegion);
            Integer communityCount=0;
            Integer EndEventCount = 0;
            for(int i=0;i<sysRegions.size();i++){
                Long regionId = sysRegions.get(i).getRegionId();
                sysRegion.setParentId(String.valueOf(regionId));
                List<SysRegion> grilList= iSysRegionService.queryGrilName(sysRegion);
                for(int j=0;j<grilList.size();j++){
                    Long regionCode = grilList.get(j).getRegionId();
                    Map<String,Object> paramsAll = new HashMap<>();
                    Map<String,Object> params = new HashMap<>();
                    paramsAll.put("regionIndexCode",regionCode);
                    int count=eventService.queryeventCountAll(paramsAll);
                    communityCount = communityCount+count;
                    paramsAll.put("statusArr",new String[]{"6","7","9"});
                    int endcount=eventService.queryeventCountAll(paramsAll);
                    EndEventCount=EndEventCount+endcount;
                    if(j==grilList.size()-1){
                        String eventProportion="";
                        params.put("communityName",sysRegions.get(i).getRegionName());
                        if(communityCount == 0){
                           eventProportion="0%";
                            params.put("eventProportion",eventProportion);
                        }else{
                            eventProportion = divideTo(new BigDecimal(communityCount),new BigDecimal(EndEventCount))+"%";
                            params.put("eventProportion",eventProportion);
                        }
                        list.add(params);
                        communityCount=0;
                        if(showCount !=null && showCount !=0){
                            showCount = showCount-1;
                            if(showCount == 0){
                                return AjaxResult.success(list);
                            }
                        }

                    }
                }
            }
            return AjaxResult.success(list);
        }else if(type.equals("department")){
            List list = new ArrayList<>();
            return AjaxResult.success(list);
        }
        return AjaxResult.success();
    }

    /**
     * ????????????????????????
     */
    @RequestMapping("index/villageEventAll")
    @ResponseBody
    public AjaxResult villageEventAll(Integer villageId,String startTime,String endTime,Integer eventSource){
        startPage();
        Map<String,Object> paramsAll = new HashMap<>();
        List list = new ArrayList<>();
        paramsAll.put("villageId",villageId);
        paramsAll.put("startTime",startTime);
        paramsAll.put("endTime",endTime);
        paramsAll.put("eventSource",eventSource);
        List<XlPropertyAndVillageVo> xlPropertyAndVillageVos= propertyEventService.queryVillageEvent(paramsAll);
        for(int i=0;i<xlPropertyAndVillageVos.size();i++){
            Map<String,Object> params = new HashMap<>();
            params.put("propertyEventSource",xlPropertyAndVillageVos.get(i).getEventSource());
            params.put("propertyEventType",xlPropertyAndVillageVos.get(i).getEventType());
            params.put("propertyEventStatus",xlPropertyAndVillageVos.get(i).getEventStatus());
            params.put("createTime",xlPropertyAndVillageVos.get(i).getCreateTime());
            list.add(params);
        }
        return AjaxResult.success(list);
    }


/*    @RequestMapping("ceshi")
    @ResponseBody
    public AjaxResult ceshi (@RequestParam("list[]") String[] list){
        System.out.println(list);
        return AjaxResult.success();
    }*/
    /**
     * ????????????????????????
     * @param type
     * @return
     */
    public Map<String,Object> EventDate(String type,Date date){
        Map<String,Object> map = new HashMap<>();
        if("day".equals(type)){
            //????????????
            //map.put("currentDate",DateFormatUtils.format(new Date(),"yyyy-MM-dd"));
            //?????????
            map.put("nearlyADayBegin",DateUtil.offsetDay(date, -1).toString());
        }else if("week".equals(type)){
            //?????????
//            map.put("nearlyAWeekBegin",DateFormatUtils.format(DateUtils.getThisWeekMonday(),"yyyy-MM-dd HH:mm:ss"));
            //?????????
            map.put("nearlyAWeekBegin",DateUtil.offsetWeek(date, -1).toString());
        }else if("month".equals(type)){
            //????????????
//            map.put("nearlyAMonthBegin",DateFormatUtils.format(DateUtils.getThisMonthMonday(),"yyyy-MM-dd HH:mm:ss"));
            //?????????
            map.put("nearlyAMonthBegin",DateUtil.offsetMonth(date, -1).toString());
        }else if("year".equals(type)){
            //?????????
            map.put("nearlyAYearBegin",DateUtils.getNearlyYear(date,-1));
        }
        return map;
    }


    /**
     * ????????????????????????
     * @param type
     * @return
     */
    public Map<String,Object> EventDateType(String type,Date date){
        Map<String,Object> map = new HashMap<>();
        if("day".equals(type)){
            //????????????
            //map.put("currentDate",DateFormatUtils.format(new Date(),"yyyy-MM-dd"));
            //?????????
            map.put("nearlyADayBegin",DateUtil.offsetDay(date, -1).toString());
        }else if("week".equals(type)){
            //?????????
            map.put("nearlyAWeekBegin",DateFormatUtils.format(DateUtils.getThisWeekMonday(),"yyyy-MM-dd HH:mm:ss"));
            //?????????
//            map.put("nearlyAWeekBegin",DateUtil.offsetWeek(date, -1).toString());
        }else if("month".equals(type)){
            //????????????
            map.put("nearlyAMonthBegin",DateFormatUtils.format(DateUtils.getThisMonthMonday(),"yyyy-MM-dd HH:mm:ss"));
            //?????????
//            map.put("nearlyAMonthBegin",DateUtil.offsetMonth(date, -1).toString());
        }else if("year".equals(type)){
            //?????????
            map.put("nearlyAYearBegin",DateUtils.getNearlyYear(date,-1));
        }
        return map;
    }
    private String divideTo(BigDecimal num,BigDecimal num1){
        BigDecimal divide = num1.divide(num,4, RoundingMode.HALF_UP);
        BigDecimal bigDecimal = new BigDecimal(100);
        BigDecimal bignum3 = divide.multiply(bigDecimal);
        BigDecimal bigDecimal1 = bignum3.setScale(2, BigDecimal.ROUND_HALF_UP);
//        BigDecimal bigDecimal = divide.setScale(2, BigDecimal.ROUND_HALF_UP);
//        System.out.println(bigDecimal);
        return bigDecimal1.toString();
    }

    /*@RequestMapping("shuju")
    @ResponseBody
    public AjaxResult shuju(){
        List<XlRoom> xlRooms=iXlRoomService.selectXlPersonnelListAll();
        for(int i=0;i<xlRooms.size();i++){
            if(xlRooms.get(i).getVillageName() !=null){
                String villageName = xlRooms.get(i).getVillageName();
                List<XlVillageModel> xlVillageModels=villageService.selectVillageId(villageName);
                if(xlVillageModels.size() ==1){
                    Long id = xlVillageModels.get(0).getId();
                    Map<String,Object> thingResult = new HashMap<>();
                    thingResult.put("villageName",villageName);
                    thingResult.put("villageid",id);
                    iXlRoomService.updatePersonVillageId(thingResult);
                }
            }
        }
        return AjaxResult.success();
    }*/

    /*@RequestMapping("shuju1")
    @ResponseBody
    public AjaxResult shuju1(){
        XlPersonnel xlPersonnel = new XlPersonnel();
        List<XlPersonnel> xlPersonnelList=iXlPersonnelService.selectXlPersonnelListAll();
        for(int i=0;i<xlPersonnelList.size();i++){
            String address = xlPersonnelList.get(i).getAddress();
            List<XlRoom> xlRooms=iXlRoomService.queryRoomId(address);
            if(xlRooms.size()==1){
                Long id = xlRooms.get(0).getId();
                Map<String,Object> thingResult = new HashMap<>();
                thingResult.put("address",address);
                thingResult.put("villageCode",id);
                iXlPersonnelService.updatePersonVillageId(thingResult);
            }
        }
        return AjaxResult.success();
    }*/
    //
   /* public static void main(String[] args) {
        *//*String time ="???";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_YEAR,1);
        calendar.add(Calendar.YEAR,-1);
        Date time = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = dateFormat.format(time);
        System.out.println(format);
        if(time.equals("???")){
            System.out.println("???");
        }else if(time.equals("???")){
            System.out.println("???");
        }else{
            System.out.println("???");
        }*//*
    }*/
}
