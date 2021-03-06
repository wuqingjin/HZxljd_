package com.ruoyi.web.controller.system.HkEarlyWarning;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Daping.PercentResult;
import com.ruoyi.system.domain.HkEarlyWarning.*;
import com.ruoyi.system.service.*;
import com.ruoyi.system.service.HkEarlyWarning.HkEventService;
import com.ruoyi.system.service.statistics.HkEventStatisticsService;
import com.ruoyi.web.config.JWTConfigProperties;
import com.ruoyi.web.controller.common.utils.RedisTemplateKey;
import com.ruoyi.web.controller.common.utils.TimeField;
import com.ruoyi.web.controller.common.utils.TimeUtil;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author FanKaibiao
 * @date 2020-12-30-21:27
 */
@Controller
@RequestMapping("system/archives")
public class ArchivesController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(ArchivesController.class);

    @Autowired
    private JWTConfigProperties jwtConfigProperties;

    @Autowired
    private RedisTemplate redisTemplate;

    private String prefix = "system/Archives";

    @Autowired
    private HkEventService hkEventService;

    @Autowired
    private HkEventStatisticsService statisticsService;

    @Autowired
    private ISysDeptService sysDeptService;

    @Autowired
    private ISysRegionService sysRegionService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysRanksService sysRanksService;

    @Autowired
    private HkEClassificationService hkEClassificationService;

    @Autowired
    private ISysUserService userService;

    /**
     * ??????????????????
     * @param vo
     * @return
     */
    @GetMapping("queryThingList")
    @ResponseBody
    public TableDataInfo queryThingList(HkEventFourVo vo){
        if(StringUtils.isNull(vo.getPageNum())){
            throw new RuntimeException("????????????????????????");
        }
        if(StringUtils.isNull(vo.getPageSize())){
            throw new RuntimeException("????????????????????????");
        }
        startPage();
        List<HkEventThingVo> list = statisticsService.queryThingList(vo);
        return getDataTable(list);
    }

    /**
     * ??????????????????????????????
     * @param vo
     * @return
     */
    @PostMapping(value = "thingTableExport")
    @ResponseBody
    public AjaxResult thingTableExport(HkEventFourVo vo){
        vo.setPageNum(null);
        vo.setPageSize(null);
        List<HkEventThingVo> list = statisticsService.queryThingList(vo);
        ExcelUtil<HkEventThingVo> util = new ExcelUtil<HkEventThingVo>(HkEventThingVo.class);
        return util.exportExcel(list, "thingEventData");
    }


    /**
     * ??????????????????
     * @param vo
     * @return
     */
    @GetMapping("queryFieldList")
    @ResponseBody
    public TableDataInfo queryFieldList(HkEventFourVo vo){
        startPage();
        List<HkEventFieldVo> list = statisticsService.queryFieldList(vo);
        return getDataTable(list);
    }

    /**
     *??????????????????????????????
     * @param vo
     * @return
     */
    @PostMapping(value = "fieldTableExport")
    @ResponseBody
    public AjaxResult fieldTableExport(HkEventFourVo vo){
        vo.setPageNum(null);
        vo.setPageSize(null);
        List<HkEventFieldVo> list = statisticsService.queryFieldList(vo);
        ExcelUtil<HkEventFieldVo> util = new ExcelUtil<HkEventFieldVo>(HkEventFieldVo.class);
        return util.exportExcel(list, "fieldEventData");
    }

    /**
     * ??????????????????
     * @param vo
     * @return
     */
    @GetMapping("queryPersonList")
    @ResponseBody
    public TableDataInfo queryPersonList(HkEventFourVo vo){
        startPage();
        List<HkEventPersonVo> list = statisticsService.queryPersonList(vo);
        return getDataTable(list);
    }

    /**
     * ??????????????????????????????
     * @param vo
     * @return
     */
    @PostMapping(value = "personTableExport")
    @ResponseBody
    public AjaxResult personTableExport(HkEventFourVo vo){
        vo.setPageNum(null);
        vo.setPageSize(null);
        List<HkEventPersonVo> list = statisticsService.queryPersonList(vo);
        ExcelUtil<HkEventPersonVo> util = new ExcelUtil<HkEventPersonVo>(HkEventPersonVo.class);
        return util.exportExcel(list, "personEventData");
    }

    /**
     * ??????????????????
     * @param vo
     * @return
     */
    @GetMapping("queryStageList")
    @ResponseBody
    public TableDataInfo queryDateList(HkEventFourVo vo){
        /*startPage();
        List<HkEventStageVo> list = hkEventService.queryStageList(vo);
        return getDataTable(list);*/
        if(StringUtils.isNull(vo.getPageNum())){
            throw new RuntimeException("????????????????????????");
        }
        if(StringUtils.isNull(vo.getPageSize())){
            throw new RuntimeException("????????????????????????");
        }
        startPage();
        List<HkEventThingVo> list = statisticsService.queryThingList(vo);
        return getDataTable(list);
    }

    /**
     * ??????????????????????????????
     * @param vo
     * @return
     */
    @PostMapping(value = "stageTableExport")
    @ResponseBody
    public AjaxResult stageTableExport(HkEventFourVo vo){
        vo.setPageNum(null);
        vo.setPageSize(null);
        List<HkEventThingVo> list = statisticsService.queryThingList(vo);
        ExcelUtil<HkEventThingVo> util = new ExcelUtil<HkEventThingVo>(HkEventThingVo.class);
        return util.exportExcel(list, "stageEventData");
    }


    //????????????
    @RequestMapping(value = "Thinglist",method = RequestMethod.GET)
    public String ThingList(ModelMap mmap){
        //?????????
        return prefix + "/Thing";
    }

    //???????????? ????????????
    @RequestMapping(value = "ThingEventList",method = RequestMethod.GET)
    public String ThingEventList(ModelMap mmap){
        //?????????
        List<HkEClassification> hkEClassifications = hkEClassificationService.queryEventEClassifications();
        mmap.put("eventClass",hkEClassifications);
        return prefix + "/EventThing";
    }

    /**
     * ???????????? ????????????/????????????/?????????/?????????
     * @param type
     * @return
     */
    @RequestMapping(value = "eventCount",method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult eventCount(String type){
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.THING_COUNT_KEY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.THING_COUNT_KEY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.THING_COUNT_KEY_MONTH;//??????????????????
        }
        Map<String,String> map = getEventMaps(eventType);*/

        Map<String,String> map = null;
        if(map == null){
            Date date = new Date();
            map = getThing("1",type,date);//????????????????????????
            Map<String,String> lastThing = getThing("2",type,date);//????????????????????????
            String lastWeekThingRate = compareTo(new BigDecimal(map.get("thingCount")),new BigDecimal(lastThing.get("thingCount")));
            String lastWeekCompleteRate = compareTo(new BigDecimal(map.get("completeCount")),new BigDecimal(lastThing.get("completeCount")));
            String lastWeekThingTurnoverRate = compareToRate(new BigDecimal(map.get("turnoverRate")),new BigDecimal(lastThing.get("turnoverRate")));
            String lastWeekThingTreatmentRate = compareToRate(new BigDecimal(map.get("handleRate")),new BigDecimal(lastThing.get("handleRate")));
            map.put("lastWeekThingRate",lastWeekThingRate);//????????????
            map.put("lastWeekCompleteRate",lastWeekCompleteRate);//??????????????????
            map.put("lastWeekThingTurnoverRate",lastWeekThingTurnoverRate);//???????????????
            map.put("lastWeekThingTreatmentRate",lastWeekThingTreatmentRate);//???????????????
        }
        AjaxResult success = AjaxResult.success(map);
        return success;
    }

    public Map<String,String> getThing(String flag,String type,Date date){
        /**1:?????? 2:????????? 3:?????????,4:????????? 5:?????? 6:?????? 7:????????? 8:???????????? 9:??????????????????,10:????????????**/
        /**??????????????? 1:?????? 2:????????? 3:?????????,4:????????? 5:?????? 6:?????? 7:?????????**/
        /**???????????? 1:?????? 2:????????? 3:?????????,4:????????? 5:?????? 6:?????? 7:????????? 9:??????????????????**/
        /**????????????????????? 6:?????? 7:????????? 9:??????????????????**/
        Map<String,String> thingResult = new HashMap<>();
        Map<String,Object> params1 = packageEventDate(flag,type,date);
        /**????????????**/
        params1.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
        Integer thingCount = statisticsService.countAllByParams(params1);//????????????
        Map<String,Object> params2 = packageEventDate(flag,type,date);
        /**????????????**/
        params2.put("statusArr",new String[]{"6","7","8","9"});
        Integer thingDoneCount = statisticsService.countAllByParams(params2);
        Map<String,Object> params3 = packageEventDate(flag,type,date);
        /**?????????**/
        params3.put("statusArr",new String[]{"3","4","5","6","7","8","9"});
        Integer turnoverRateCount = statisticsService.countAllByParams(params3);//?????????
        String turnoverRate = divideTo(new BigDecimal(thingCount),new BigDecimal(turnoverRateCount));//?????????
        /**?????????**/
        String handleRate = divideTo(new BigDecimal(thingCount),new BigDecimal(thingDoneCount));//?????????

        thingResult.put("thingCount",String.valueOf(thingCount));
        thingResult.put("completeCount",String.valueOf(thingDoneCount));
        thingResult.put("turnoverRate",turnoverRate);
        thingResult.put("handleRate",handleRate);
        return thingResult;
    }

    /**
     * ??????/??????????????????
     * @param rate
     * @return
     */
    private String RateCompareTo(BigDecimal rate){
        String rateSub = "";
        if(rate.compareTo(BigDecimal.ZERO) == -1){
            rateSub = "<i class='Down'>????????????"+ -rate.doubleValue()+"%</i>";
        }else{
            rateSub = "<i class='Up'>????????????"+rate+"%</i>";
        }
        return rateSub;
    }

    /**
     * ????????????
     * ??????????????????
     * @param type ??????: day ?????? week ?????? month
     * @return
     */
    @RequestMapping(value = "eventTrend",method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult eventTrend(@RequestParam String type) throws ParseException {
        //redis-key
        /*String eventType = "";
        if("day".equals(type)){
            eventType = RedisTemplateKey.THING_EVENT_TREND_BY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.THING_EVENT_TREND_BY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.THING_EVENT_TREND_BY_MONTH;//??????????????????
        }
        //??????????????????????????? redis
        Map data = getEventMaps(eventType);*/
        Map<String,Object> data = null;
        if(data == null){
            Date date = new Date();
            //??????????????????Map
            Map<String,Object> params = EventDate(type,date);
            //?????????????????? //?????????????????????
            List<Integer> eventTrendCount = new ArrayList<>();

            String nearlyADayBegin      =  (String)params.get("nearlyADayBegin");
            String nearlyADayEnd        =  (String)params.get("nearlyADayEnd");
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
                if(!StringUtils.isEmpty(nearlyADayBegin) && StringUtils.isEmpty(nearlyADayEnd)){
                    SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
                    params.clear();
                    //????????????24?????????
                    long time = sdf.parse(nearlyADayBegin).getTime();
                    String beginDateTime = nearlyADayBegin;
                    for(int i = 1; i <= 24; i++){
                        time += 3600000;
                        String endDateTime = sdf.format(new Date(time));
                        String endTime = sdf1.format(new Date(time))+"???";
                        params.put("endDateTime",endDateTime);
                        params.put("beginDateTime",beginDateTime);
                        params.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
                        Integer thingCount = statisticsService.countAllByParams(params);
                        beginDateTime = endDateTime;
                        countList.add(thingCount);
                        timeList.add(endTime);
                    }
                }
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
            data.put("eventTrend",eventTrendCount);
            data.put("eventPlaceTypeTrends",countList);
            data.put("eventTrendDate",timeList);
            /*putEvents(eventType, data,type);*/
        }
        AjaxResult success = AjaxResult.success(data);
        return success;
    }


    /**
     * ????????????-????????????-????????????
     * @param startDate ????????????
     * @param endDate ????????????
     * @return
     */
    /*@RequestMapping(value = "eventTrendByDate",method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult eventTrendByDay(@RequestParam String startDate,@RequestParam String endDate) throws ParseException {
        //???????????????
        Map<String,Object> map = new TimeUtil().getTimeDifference(startDate,endDate);
        Boolean isBetweenZero = (Boolean) map.get("isBetweenZero");//????????????????????????
        //???????????????
        List<Integer> eventPlaceTypeTrendCount = new ArrayList<>();
        //???????????????
        List<Integer> eventTrendCount = new ArrayList<>();
        //?????????
        List<String> eventTrendDate = new ArrayList<>();
        List<String> times = null;

        Map<String,Object> initTimeParams = new HashMap<>();
        //????????????12????????? ????????????
        if(isBetweenZero){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //??????????????????
            initTimeParams.put("currentDate",sdf.format(sdf.parse(startDate)));
            List<EventTrendVO> eventPlaceTypeTrends = statisticsService.queryEventTrends(initTimeParams);
            //??????type?????????????????????????????????????????????day???00-02 02-04???????????????????????????week????????????????????????month??????7????????????
            List<TimeField> timeFields = new TimeUtil().getTime("day");
            times =  timeFields.stream().map(TimeField::getDate).collect(Collectors.toList());
            eventPlaceTypeTrendCount = DataFormat(eventPlaceTypeTrends,"day",timeFields);
        }else{
            //?????????????????????????????????????????????????????????????????????
            times = (List<String>) map.get("timeField");
            String initTimeField =times.get(0);
            initTimeParams.put("currentDate",initTimeField);
            //??????????????????
            int count = statisticsService.queryEventTrendByDay(initTimeParams);
            eventPlaceTypeTrendCount.add(count);
            eventTrendDate.add(initTimeField);
            if(times.size() > 1){
                Map<String,Object> timeParams = new HashMap<>();
                for(int i =0;i < times.size() -1;i++){
                    String startDateParam =  times.get(i);
                    String endDateParam = times.get(i+1);
                    timeParams.put("startDate",startDateParam);
                    timeParams.put("endDate",endDateParam);
                    count = statisticsService.queryEventTrendByDay(timeParams);
                    eventPlaceTypeTrendCount.add(count);
                    eventTrendDate.add(endDateParam);
                }
            }
        }

        //response
        Map data = new HashMap();
        data.put("eventTrend",eventTrendCount);
        data.put("eventPlaceTypeTrends",eventPlaceTypeTrendCount);
        data.put("eventTrendDate",times);
        AjaxResult success = AjaxResult.success(data);
        return success;
    }*/

    /**
     * ????????????
     * ??????????????????
     * @param
     * @return
     */
    @GetMapping(value = "eventRegion")
    @ResponseBody
    public AjaxResult queryEventRegion(HkEventThingParamVO param){
        //redis-key
        String type = param.getType();
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.THING_REGION_BY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.THING_REGION_BY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.THING_REGION_BY_MONTH;//??????????????????
        }
        //??????????????????????????? redis
        Map data = getEventMaps(eventType);*/
        Map<String,Object> data = null;
        if(data == null){
            data = new HashMap();
            Date date = new Date();
            //??????????????????Map
            Map<String,Object> params = EventDate(type,date);
            params.put("regionCode",param.getRegionCode());
            if(!StringUtils.isEmpty(param.getSort()) && !StringUtils.isEmpty(param.getSortItem())){
                params.put("sort",param.getSort());
                params.put("sortItem",param.getSortItem());
            }
            List<EventInspectVO>  eventInspectVOS = statisticsService.queryEventRegion(params);
            List<Integer> eventCount = eventInspectVOS.stream().map(EventInspectVO::getEventCount).collect(Collectors.toList());//????????????
            List<String> regionName = eventInspectVOS.stream().map(EventInspectVO::getRegionName).collect(Collectors.toList());//????????????
            List<String> governanceRate = eventInspectVOS.stream().map(EventInspectVO::getGovernanceRate).collect(Collectors.toList());//?????????
            List<String> transferRate = eventInspectVOS.stream().map(EventInspectVO::getTransferRate).collect(Collectors.toList());//?????????
            data.put("eventCount",eventCount);
            data.put("regionName",regionName);
            data.put("transferRate",transferRate);
            data.put("governanceRate",governanceRate);
            //?????????redis
            /*putEventMaps(eventType, data,type);*/
        }
        AjaxResult success = AjaxResult.success(data);
        return success;
    }


    /**
     * ????????????-
     * ??????????????????
     * @param type ??????: day ?????? week ?????? month
     * @param regionCode ??????Code
     * @return
     */
    @GetMapping(value = "eventType")
    @ResponseBody
    public AjaxResult queryEventType(@RequestParam String type,@RequestParam(required = false) String regionCode){
        //redis-key
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.THING_EVENT_TYPE_COUNT_BY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.THING_EVENT_TYPE_COUNT_BY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.THING_EVENT_TYPE_COUNT_BY_MONTH;//??????????????????
        }
        List<EventTypeVO> typeVOS = getEvents(eventType);//??????????????????????????????,??????????????????????????????*/
        List<EventTypeVO> typeVOS = new ArrayList<>();
        if(typeVOS ==null || typeVOS.size() < 1){
            Date date = new Date();
            //??????????????????Map
            Map<String,Object> map = EventDate(type,date);
            map.put("regionId",regionCode);
            List<EventTypeVO> eventTypeVOS = statisticsService.queryEventType(map);
            /**???????????????????????????????????????????????????????????????**/
            /*List<EventTypeVO> eventTypeVOS = statisticsService.queryEventTypePartTwo(map);*/
            typeVOS = new ArrayList<>();
            packageEventType(eventTypeVOS,typeVOS);
            //?????????redis
            /*putEvents(eventType,typeVOS,type);*/
        }
        AjaxResult success = AjaxResult.success(typeVOS);
        return success;
    }

    public void packageEventType(List<EventTypeVO> eventTypeVOS,List<EventTypeVO> typeVOS){
        Integer otherValue = 0;
        for (int i =0;i<eventTypeVOS.size();i++) {
            if(i<=4){
                typeVOS.add(eventTypeVOS.get(i));
                continue;
            }
            otherValue = otherValue + eventTypeVOS.get(i).getValue();
        }
        //??????????????????????????????????????????
        EventTypeVO otherTypeVOS = new EventTypeVO();
        otherTypeVOS.setName("??????");
        otherTypeVOS.setValue(otherValue);
        typeVOS.add(otherTypeVOS);
    }

    /**
     * ????????????
     * ??????????????????
     * @return
     */
    @GetMapping(value = "regionEventTypeByRegion")
    @ResponseBody
    public AjaxResult regionEventTypeByRegion(@RequestParam String type,@RequestParam(required = false) String regionId){
        Date date = new Date();
        Map<String,Object> params = EventDate(type,date);
        params.put("regionId",regionId);
        List<EventTypeVO> regionEventTypeVOS = statisticsService.queryEventTypeByRegion(params);
        List<EventTypeVO> typeVOS = new ArrayList<>();
        typeVOS = new ArrayList<>();
        packageEventType(regionEventTypeVOS,typeVOS);
        AjaxResult success = AjaxResult.success(regionEventTypeVOS);
        return success;
    }

    /**
     * ???????????? ????????????
     * @return
     */
    @GetMapping(value = "regionNames")
    @ResponseBody
    public AjaxResult queryRegionNames(){
        //????????????
        List<SysRegion> sysRegions =  sysRegionService.queryRegionNames();
        return AjaxResult.success(sysRegions);
    }


    /**
     * ????????????-????????????
     * ??????????????????
     *
     * @param type ??????: day ?????? week ?????? month
     * @return
     */
    @GetMapping(value = "eventAnalysis")
    @ResponseBody
    public AjaxResult queryEventAnalysis(@RequestParam String type){
        //redis-key
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.THING_EVENT_ANALYSIS_BY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.THING_EVENT_ANALYSIS_BY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.THING_EVENT_ANALYSIS_BY_MONTH;//??????????????????
        }
        Map<String,String> map = getEventMaps(eventType);*/
        Map<String,String> map = null;
        if(map == null){
            map = new HashMap();
            Date date = new Date();
            //??????????????????Map
            Map<String,Object> params = EventDate(type,date);
            params.put("statusArr",new String[]{"6","7","8","9"});
            List<EventAnalysisVO> eventAnalysisVOS = statisticsService.queryEventAnalysisPartTwo(params);
            for(EventAnalysisVO eventAnalysisVO:eventAnalysisVOS){
                params.put("componentId",eventAnalysisVO.getEventAnalysisTypeCode());
                Long aLong = statisticsService.queryEventSumTime(params);
                aLong = aLong == null?0:aLong;
                map.put(eventAnalysisVO.getEventAnalysisTypeName(),DateUtils.getHourByTime(aLong).toString());
            }
            //.........................?????????????????????????????????????????? ??????...........................................
            //List<EventAnalysisVO> eventAnalysisVOS = statisticsService.queryEventAnalysisPart(params);//??????????????????????????????
            /*List<EventAnalysisVO> eventTimeInfo = statisticsService.eventActiveTimeByEventType(params);//?????????????????????????????????
            for(EventAnalysisVO vo : eventTimeInfo){
                map.put(vo.getEventAnalysisTypeName(),DateUtils.getHourByTime((long)vo.getEventAnalysisAvgTime()).toString());
            }*/
            //?????????redis
            /*putEventMaps(eventType,map,type);*/
        }
        Map<String,Object> response = new HashMap<>();
        response.put("eventAnalysisTypeNames",map.keySet());
        response.put("eventAnalysisTypeCounts",map.values());
        AjaxResult success = AjaxResult.success(response);
        return success;
    }

    /**
     * ????????????-????????????
     * ??????????????????
     * @param param
     * {
     *       type: ???day ???week ???month ???year
     *       sortItem:????????????
     *       sort: ??????
     * }
     * @return
     */
    @GetMapping(value = "eventProcessAnalysis")
    @ResponseBody
    public AjaxResult eventProcessAnalysis(HkEventThingParamVO param){
        //redis-key
        String type = param.getType();
        String sort = param.getSort();
        String sortItem = param.getSortItem();
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.THING_EVENT_ANALYSIS_BY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.THING_EVENT_ANALYSIS_BY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.THING_EVENT_ANALYSIS_BY_MONTH;//??????????????????
        }
        Map<String,String> map = getEventMaps(eventType);*/
        Map<String,String> map = null;
        Map<String,Object> response = new HashMap<>();
        if(map == null){
            map = new HashMap();
            Date date = new Date();
            //??????????????????Map
            Map<String,Object> params = EventDate(type,date);
            params.put("sort",sort);
            params.put("sortItem",sortItem);
            List<EventAnalysisVO> eventAnalysisVOS = statisticsService.eventProcessAnalysis(params);
            //.........................?????????????????????????????????????????? ??????...........................................
            //List<EventAnalysisVO> eventAnalysisVOS = statisticsService.queryEventAnalysisPart(params);//??????????????????????????????
            /*List<EventAnalysisVO> eventTimeInfo = statisticsService.eventActiveTimeByEventType(params);//?????????????????????????????????
            for(EventAnalysisVO vo : eventTimeInfo){
                map.put(vo.getEventAnalysisTypeName(),DateUtils.getHourByTime((long)vo.getEventAnalysisAvgTime()).toString());
            }*/

            List<String> governanceRateList = eventAnalysisVOS.stream().map(EventAnalysisVO::getGovernanceRate).collect(Collectors.toList());//?????????
            List<String> transforRateList = eventAnalysisVOS.stream().map(EventAnalysisVO::getTransforRate).collect(Collectors.toList());//?????????
            List<String> eventAnalysisTypeNames = eventAnalysisVOS.stream().map(EventAnalysisVO::getEventAnalysisTypeName).collect(Collectors.toList());//??????????????????
            response.put("transforRateList",transforRateList);
            response.put("governanceRateList",governanceRateList);
            response.put("eventAnalysisTypeNames",eventAnalysisTypeNames);
            //?????????redis
            /*putEventMaps(eventType,map,type);*/
        }
        return AjaxResult.success(response);
    }

    /**
     * ????????????-????????????
     * @param type ??????: day ?????? week ?????? month
     * @return
     */
    @GetMapping(value = "eventChange")
    @ResponseBody
    public AjaxResult queryEventChange(@RequestParam String type){
        //redis-key
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.THING_EVENT_CHANGE_BY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.THING_EVENT_CHANGE_BY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.THING_EVENT_CHANGE_BY_MONTH;//??????????????????
        }
        Map map = getEventMaps(eventType);*/
        Map<String,Object> map = null;
        if(map ==null ){
            Date date = new Date();
            /**1:?????? 2:????????? 3:?????????,4:????????? 5:?????? 6:?????? 7:????????? 8:???????????? 9:??????????????????,10:????????????**/
            Map<String,Object> thingParams = EventDate(type,date);
            thingParams.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            Integer thingCount = statisticsService.countAllByParams(thingParams);//????????????
            /*inAdvanceParams.put("inAdvanceStatus","1,10");*/
            thingParams.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});//????????????
            int inAdvanceCount =  statisticsService.countAllByParams(thingParams);
            /*judgeParams.put("judgeStatus","1,2,10");*/
            thingParams.put("statusArr",new String[]{"2","3","4","5","6","7","8","9"});//??????
            int judgeCount = statisticsService.countAllByParams(thingParams);
            /*handleParams.put("handleStatus","1,2,3,4,10");*/
            thingParams.put("statusArr",new String[]{"5","6","7","8","9"});//????????????
            int handleCount = statisticsService.countAllByParams(thingParams);
            /*achieveParams.put("achieveStatus","6,7,9");*/
            thingParams.put("statusArr",new String[]{"6","7","8","9"});//??????
            int achieveCount =  statisticsService.countAllByParams(thingParams);

            map = new HashMap();
            List<EventChangeVO> eventChangeVOS = new ArrayList<>();
            EventChangeVO inAdvanceEventChangeVO = new EventChangeVO();
            inAdvanceEventChangeVO.setName("????????????");
            inAdvanceEventChangeVO.setValue(Double.valueOf(inAdvanceCount));
            EventChangeVO judgeEventChangeVO = new EventChangeVO();
            judgeEventChangeVO.setName("????????????");
            judgeEventChangeVO.setValue(Double.valueOf(judgeCount));
            EventChangeVO handleEventChangeVO = new EventChangeVO();
            handleEventChangeVO.setName("????????????");
            handleEventChangeVO.setValue(Double.valueOf(handleCount));
            EventChangeVO achieveEventChangeVO = new EventChangeVO();
            achieveEventChangeVO.setName("????????????");
            achieveEventChangeVO.setValue(Double.valueOf(achieveCount));
            eventChangeVOS.add(inAdvanceEventChangeVO);
            eventChangeVOS.add(judgeEventChangeVO);
            eventChangeVOS.add(handleEventChangeVO);
            eventChangeVOS.add(achieveEventChangeVO);
            map.put("eventChangeVOS",eventChangeVOS);
            //?????????redis
            /*putEventMaps(eventType,map,"");*/
        }
        AjaxResult success = AjaxResult.success(map);
        return success;
    }


    /**
     * ????????????
     * @param mmap
     * @return
     */
    @GetMapping(value = "Fieldlist")
    public String FieldList(ModelMap mmap){
        //?????????
        return prefix + "/Field";
    }

    //???????????? ????????????
    @GetMapping(value = "FieldEventList")
    public String FieldEventList(ModelMap mmap){
        //?????????
        /*mmap.put("user", ShiroUtils.getLoginName());*/
        List<HkEClassification> hkEClassifications = hkEClassificationService.queryEventEClassifications();
        mmap.put("eventClass",hkEClassifications);
        return prefix + "/EventField";
    }

    /**
     * ???????????? ????????????
     * @return
     */
    @GetMapping(value = "regionCount")
    @ResponseBody
    public AjaxResult queryRegionCount(String type){

        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.REGION_COUNT_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.REGION_COUNT_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.REGION_COUNT_MONTH;//??????????????????
        }
        Map<String,String> map = getEventMaps(eventType);*/
        Map<String,String> map = null;
        if(map == null){
            Date date = new Date();
            map = getRegion("1",type,date);
            Map<String,String> lastRegion = getRegion("2",type,date);
            String lastWeekRegionCount = compareTo(new BigDecimal(map.get("regionCount")),new BigDecimal(lastRegion.get("regionCount")));
            String lastWeekRegionEventAvgCount = compareTo(new BigDecimal(map.get("regionEventAvgCount")),new BigDecimal(lastRegion.get("regionEventAvgCount")));
            String lastWeekRegionPeopleCount = compareTo(new BigDecimal(map.get("regionPeopleCount")),new BigDecimal(lastRegion.get("regionPeopleCount")));
            String lastWeekRegionLoad = compareToRate(new BigDecimal(map.get("regionLoad")),new BigDecimal(lastRegion.get("regionLoad")));

            map.put("regionCountRate",lastWeekRegionCount);//???????????????
            map.put("regionEventAvgCountRate",lastWeekRegionEventAvgCount);//???????????????
            map.put("regionPeopleCountRate",lastWeekRegionPeopleCount);//???????????????
            map.put("regionLoadRate",lastWeekRegionLoad);//???????????????
        }
        AjaxResult success = AjaxResult.success(map);
        return success;
    }

    public Map<String,String> getRegion(String flag,String type,Date date){
        Map<String,String> thingResult = new HashMap<>();

        //???????????? ???????????????????????????
        Map<String,Object> param1 = new HashMap<>();
        param1.put("level",2);
        Integer regionCount = sysRegionService.queryRegionCountByParam(param1);

        Map<String,Object> thingParams = packageEventDate(flag,type,date);
        thingParams.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
        /*RegionLoadVO regionLoadVO = statisticsService.queryEventHappenRegionInfo(thingParams);
        Integer regionCount = regionLoadVO.getRegionCount();//???????????????????????????
        Integer regionPeopleCount = regionLoadVO.getUserCount();//???????????????????????????????????????*/

        Integer eventAllCount = statisticsService.countAllByParams(thingParams);//????????????
        //?????????????????? = ?????????/????????????
        String regionEventAvgCount = divideToAvg(new BigDecimal(regionCount),new BigDecimal(eventAllCount));//??????????????????

        //???????????? ??????????????????
        Map<String,Object> param3 = new HashMap<>();
        Integer regionPeopleCount = sysRanksService.querySysRanksCount(param3);
        Integer days = 0;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            switch (type){
                case "week":
                    days = 7;
                    break;
                case "month":
                    days = Integer.parseInt(DateUtil.betweenDay(DateUtil.offsetMonth(date,-1),date,false)+"");
                    break;
                case "year":
                    Date parse = sdf.parse(DateUtils.getNearlyYear(date, -1));
                    days = Integer.parseInt(DateUtil.betweenDay(parse,date,false)+"");
                    break;
                default:
                    days = 1;
                    break;
            }
        }catch(Exception ex){
            logger.info("==================????????????????????????==================");
            ex.printStackTrace();
        }
        //????????????
        String regionLoad = new BigDecimal(eventAllCount).divide(new BigDecimal(regionPeopleCount),2,BigDecimal.ROUND_HALF_UP)
                .divide(new BigDecimal(days),2,BigDecimal.ROUND_HALF_UP).doubleValue()+"";
        thingResult.put("regionCount",String.valueOf(regionCount));
        thingResult.put("regionEventAvgCount",regionEventAvgCount);
        thingResult.put("regionPeopleCount",String.valueOf(regionPeopleCount));
        thingResult.put("regionLoad",regionLoad);
        return thingResult;
    }


    private String compareTo(BigDecimal count,BigDecimal lastCount){
        BigDecimal rate =new BigDecimal(0);
        if(count.compareTo(BigDecimal.ZERO) >0){
            BigDecimal regionCountSubtract = count.subtract(lastCount).setScale(2, BigDecimal.ROUND_HALF_UP);
            rate = regionCountSubtract.divide(count,2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
        }
        return RateCompareTo(rate);
    }

    private String compareToRate(BigDecimal count,BigDecimal lastCount){
        BigDecimal rate =new BigDecimal(0);
        if(count.compareTo(BigDecimal.ZERO) >0){
            rate = count.subtract(lastCount);
        }
        return RateCompareTo(rate);
    }

    private String divideTo(BigDecimal num,BigDecimal num1){
        BigDecimal rate =new BigDecimal(0);
        if(num.compareTo(BigDecimal.ZERO) >0){
            rate = num1.divide(num,2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
        }
        return rate.toString();
    }

    private String divideToAvg(BigDecimal num,BigDecimal num1){
        BigDecimal rate =new BigDecimal(0);
        if(num.compareTo(BigDecimal.ZERO) >0){
            rate = num1.divide(num,2, BigDecimal.ROUND_HALF_UP);
        }
        return rate.toString();
    }


    /**
     * ???????????? ????????????
     * @return
     */

    @GetMapping(value = "regionChange")
    @ResponseBody
    public AjaxResult queryRegionChange(@RequestParam String type) {
       /* String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.REGION_CHANGE_RATE_BY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.REGION_CHANGE_RATE_BY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.REGION_CHANGE_RATE_BY_MONTH;//??????????????????
        }
        Map map = getEventMaps(eventType);*/
        Map<String,Object> map = null;
        if(map == null){
            Date date = new Date();
            Map<String,Object> params = EventDate(type,date);
            //????????? dept_id ?????????redis????????????????????????????????????parent_id ?????????????????????????????????dept_name ??????????????????
            params.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            List<RegionChangeVO> eventChanges = sysRegionService.queryRegionChange(params);
            List<Integer> eventChangeCounts = eventChanges.stream().map(RegionChangeVO::getEventChangeCount).collect(Collectors.toList());//???????????????
            List<String> eventChangeNames = eventChanges.stream().map(RegionChangeVO::getEventChangeName).collect(Collectors.toList());//??????????????????
            map = new HashMap();
            map.put("eventChangeCounts",eventChangeCounts);
            map.put("eventChangeNames",eventChangeNames);
            //?????????redis
            /*putEvents(eventType,map,"");*/
        }
        AjaxResult success = AjaxResult.success(map);
        return success;
    }


    /**
     * ???????????? ????????????-??????????????????
     * @return
     */
    @GetMapping(value = "regionChangeByDate")
    @ResponseBody
    public AjaxResult queryRegionChangeByDate(@RequestParam(required = true) String startDate,@RequestParam(required = true) String endDate) throws ParseException {
        Map params = new HashMap();
        params.put("startDate",DateFormatUtils.format(DateUtils.parseDate(startDate),"yyyy-MM-dd"));
        params.put("endDate",DateFormatUtils.format(DateUtils.parseDate(endDate),"yyyy-MM-dd"));
        //????????? dept_id ?????????redis????????????????????????????????????parent_id ?????????????????????????????????dept_name ??????????????????
        params.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
        List<RegionChangeVO> eventChanges = sysRegionService.queryRegionChange(params);
        List<Integer> eventChangeCounts = eventChanges.stream().map(RegionChangeVO::getEventChangeCount).collect(Collectors.toList());//???????????????
        List<String> eventChangeNames = eventChanges.stream().map(RegionChangeVO::getEventChangeName).collect(Collectors.toList());//??????????????????
        Map map = new HashMap();
        map.put("eventChangeCounts",eventChangeCounts);
        map.put("eventChangeNames",eventChangeNames);
        AjaxResult success = AjaxResult.success(map);
        return success;
    }

    /**
     * ???????????? ????????????
     * @return
     */
    @GetMapping(value = "regionLoad")
    @ResponseBody
    public AjaxResult queryRegionLoad(@RequestParam String type){
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.REGION_LOAD_BY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.REGION_LOAD_BY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.REGION_LOAD_BY_MONTH;//??????????????????
        }
        Map map = getEventMaps(eventType);*/
        Map<String,Object> map = null;
        if(map == null){
            Date date = new Date();
            Map<String,Object> params = EventDate(type,date);
            params.put("desc","desc");
            params.put("limit",8);
            params.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            List<RegionLoadVO> regionLoadVOS = sysRegionService.queryRegionLoad(params);
            for (RegionLoadVO regionLoadVO : regionLoadVOS) {
                String eventCount = regionLoadVO.getEventCount() == null ? "0" : ( regionLoadVO.getEventCount() + "");
                String userCount = regionLoadVO.getUserCount()== null ? "0" : (regionLoadVO.getUserCount()+"");
                //??????????????? ????????????/????????????
                BigDecimal _userCount = new BigDecimal(userCount);
                BigDecimal _eventCount = new BigDecimal(eventCount);
                regionLoadVO.setRegionLoadCount(Double.valueOf(divideTo(_userCount,_eventCount)));
            }

            List<Double> regionEfficiencyCount = regionLoadVOS.stream().map(RegionLoadVO::getRegionLoadCount).collect(Collectors.toList());//?????????
            List<String> regionEfficiencyName = regionLoadVOS.stream().map(RegionLoadVO::getRegionName).collect(Collectors.toList());//????????????
            map = new HashMap();
            map.put("regionEfficiencyCount",regionEfficiencyCount);
            map.put("regionEfficiencyName",regionEfficiencyName);
            //?????????redis
            /*putEventMaps(eventType,map,"");*/
        }
        AjaxResult success = AjaxResult.success(map);
        return success;
    }

    /**
     * ???????????? ????????????
     * @return
     */
    @GetMapping(value = "regionEventType")
    @ResponseBody
    public AjaxResult queryRegionEventType(@RequestParam String type){
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.REGION_EVENT_TYPE_COUNT_BY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.REGION_EVENT_TYPE_COUNT_BY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.REGION_EVENT_TYPE_COUNT_BY_MONTH;//??????????????????
        }
        List<RegionEventTypeVO> eventTypeVOS = getEvents(eventType);*/
        List<RegionEventTypeVO> eventTypeVOS = new ArrayList<>();
        if(eventTypeVOS == null || eventTypeVOS.size() <1){
            Date date = new Date();
            Map<String,Object> params = EventDate(type,date);
            params.put("desc","desc");
            params.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            List<RegionEventTypeVO> regionEventTypeVOS = statisticsService.queryRegionEventType(params);
            eventTypeVOS = new ArrayList<>();
            packageRegion(regionEventTypeVOS,eventTypeVOS);
            //?????????redis
            /*putEvents(eventType,eventTypeVOS,"");*/
        }
        AjaxResult success = AjaxResult.success(eventTypeVOS);
        return success;
    }

    public void packageRegion(List<RegionEventTypeVO> regionEventTypeVOS,List<RegionEventTypeVO> eventTypeVOS){
        Integer otherValue = 0;
        for(int i =0;i<regionEventTypeVOS.size();i++){
            if(i<=4){
                eventTypeVOS.add(regionEventTypeVOS.get(i));
                continue;
            }
            otherValue = otherValue + regionEventTypeVOS.get(i).getValue();
        }
        //??????????????????????????????????????????
        RegionEventTypeVO regionEventTypeVO = new RegionEventTypeVO();
        regionEventTypeVO.setName("??????");
        regionEventTypeVO.setValue(otherValue);
        eventTypeVOS.add(regionEventTypeVO);
    }


    /**
     * ???????????? ??????????????????
     * @return
     */
    @GetMapping(value = "eventEClassifications")
    @ResponseBody
    public AjaxResult queryEventTypes(){
        List<HkEClassification> hkEClassifications = hkEClassificationService.queryEventEClassifications();
        AjaxResult success = AjaxResult.success(hkEClassifications);
        return success;
    }

    /**
     * ???????????? ????????????-????????????
     * @return
     */
    @GetMapping(value = "regionEventTypeByName")
    @ResponseBody
    public AjaxResult queryRegionEventTypeByName(@RequestParam String eventType){
        Map<String,Object> params = new HashMap<>();
        params.put("eventType",eventType);
        params.put("desc","desc");
        params.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
        List<RegionEventTypeVO> regionEventTypeVOS = statisticsService.queryRegionEventType(params);
        List<RegionEventTypeVO> eventTypeVOS = new ArrayList<>();
        packageRegion(regionEventTypeVOS,eventTypeVOS);
        AjaxResult success = AjaxResult.success(eventTypeVOS);
        return success;
    }


    /**
     * ???????????? ??????????????????????????????
     * @return
     */
    @GetMapping(value = "regionEfficiencyByRegionName")
    @ResponseBody
    public AjaxResult regionEfficiencyByRegionName(@RequestParam String type,String regionId){

        //redis-key
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.REGION_EFFICIENCY_COUNT_BY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.REGION_EFFICIENCY_COUNT_BY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.REGION_EFFICIENCY_COUNT_BY_MONTH;//??????????????????
        }
        Map map = getEventMaps(eventType);*/
        Map<String,Object> map = null;
        if(map ==null ){
            Date date = new Date();
            Map<String,Object> thingParams = EventDate(type,date);
            thingParams.put("regionId",regionId);
            thingParams.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            Integer thingCount = statisticsService.queryEventTransferByRegion(thingParams);//????????????
            thingParams.put("statusArr",new String[]{"1"});//??????
            int inAdvanceCount =  thingCount - statisticsService.queryEventTransferByRegion(thingParams);
            thingParams.put("statusArr",new String[]{"2"});//??????
            int judgeCount = thingCount - statisticsService.queryEventTransferByRegion(thingParams);
            thingParams.put("statusArr",new String[]{"3","4","5"});//????????????
            int handleCount = thingCount - statisticsService.queryEventTransferByRegion(thingParams);
            thingParams.put("statusArr",new String[]{"6","7","8","9"});//??????
            int achieveCount =  statisticsService.queryEventTransferByRegion(thingParams);

            map = new HashMap();
            List<EventChangeVO> eventChangeVOS = new ArrayList<>();
            EventChangeVO inAdvanceEventChangeVO = new EventChangeVO();
            inAdvanceEventChangeVO.setName("?????????");
            inAdvanceEventChangeVO.setValue(Double.valueOf(inAdvanceCount));
            EventChangeVO judgeEventChangeVO = new EventChangeVO();
            judgeEventChangeVO.setName("?????????");
            judgeEventChangeVO.setValue(Double.valueOf(judgeCount));
            EventChangeVO handleEventChangeVO = new EventChangeVO();
            handleEventChangeVO.setName("?????????");
            handleEventChangeVO.setValue(Double.valueOf(handleCount));
            EventChangeVO achieveEventChangeVO = new EventChangeVO();
            achieveEventChangeVO.setName("?????????");
            achieveEventChangeVO.setValue(Double.valueOf(achieveCount));
            eventChangeVOS.add(inAdvanceEventChangeVO);
            eventChangeVOS.add(judgeEventChangeVO);
            eventChangeVOS.add(handleEventChangeVO);
            eventChangeVOS.add(achieveEventChangeVO);
            map.put("eventChangeVOS",eventChangeVOS);
            //?????????redis
            /*putEventMaps(eventType,map,"");*/
        }
        AjaxResult success = AjaxResult.success(map);
        return success;

    }

    /**
     * ???????????? ???????????????????????? ????????????
     * @return
     */
    @GetMapping(value = "regionEventAnalysis")
    @ResponseBody
    public AjaxResult queryRegionEventAnalysis(String type){
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.REGION_EVENT_ANALYSIS_CHANGE_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.REGION_EVENT_ANALYSIS_CHANGE_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.REGION_EVENT_ANALYSIS_CHANGE_MONTH;//??????????????????
        }
        Map map = getEventMaps(eventType);*/
        Map<String,Object> map = null;
        if(map ==null ){
            map = new HashMap();
            Date date = new Date();
            //?????????????????????
            Map<String,Object> thingParams = EventDate(type,date);
            thingParams.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            List<RegionEventAnalysisVO> regionEventAnalysisVOS = statisticsService.queryRegionAnalysis(thingParams);
            Map<String,Integer> regionEvent = new HashMap<>();
            for(RegionEventAnalysisVO regionEventAnalysisVO:regionEventAnalysisVOS){
                regionEvent.put(regionEventAnalysisVO.getRegionName(),regionEventAnalysisVO.getEventCount());
            }

            //?????????
            thingParams.put("statusArr",new String[]{"3","4","5","6","7","8","9"});
            List<RegionEventAnalysisVO> changeRegionEventAnalysisVOS = statisticsService.queryRegionAnalysis(thingParams);
            packEventAnalysis(regionEvent,changeRegionEventAnalysisVOS,true);

            //?????????
            thingParams.put("statusArr",new String[]{"6","7","8","9"});
            List<RegionEventAnalysisVO> handleRegionEventAnalysisVOS = statisticsService.queryRegionAnalysis(thingParams);
            packEventAnalysis(regionEvent,handleRegionEventAnalysisVOS,false);

            List<Double> regionEfficiencyCount = changeRegionEventAnalysisVOS.stream().map(RegionEventAnalysisVO::getEventRate).collect(Collectors.toList());//?????????
            List<String> regionEfficiencyName = changeRegionEventAnalysisVOS.stream().map(RegionEventAnalysisVO::getRegionName).collect(Collectors.toList());//????????????
            List<Double> regionCommonEfficiencyCount = handleRegionEventAnalysisVOS.stream().map(RegionEventAnalysisVO::getEventRate).collect(Collectors.toList());//?????????
            List<String> regionCommonEfficiencyName = handleRegionEventAnalysisVOS.stream().map(RegionEventAnalysisVO::getRegionName).collect(Collectors.toList());
            map.put("regionEfficiencyCount",regionEfficiencyCount);
            map.put("regionEfficiencyName",regionEfficiencyName);
            map.put("regionCommonEfficiencyCount",regionCommonEfficiencyCount);
            map.put("regionCommonEfficiencyName",regionCommonEfficiencyName);
            //?????????redis
            /*putEventMaps(eventType,map,"");*/
        }
        AjaxResult success = AjaxResult.success(map);
        return success;
    }


    public void packEventAnalysis(Map<String,Integer> map,List<RegionEventAnalysisVO> regionEventAnalysisVOS,boolean flag){
        for(RegionEventAnalysisVO regionEventAnalysisVO:regionEventAnalysisVOS){
            Integer deptEventCountSum = map.get(regionEventAnalysisVO.getRegionName());
            Integer deptEventCount = regionEventAnalysisVO.getEventCount();
            if(flag){
                deptEventCount = deptEventCountSum - regionEventAnalysisVO.getEventCount();
            }
            regionEventAnalysisVO.setEventCount(deptEventCount);
            if(deptEventCountSum == null || deptEventCountSum == 0){
                regionEventAnalysisVO.setEventRate(0.00);
                continue;
            }
            regionEventAnalysisVO.setEventRate(new BigDecimal(deptEventCount).divide(new BigDecimal(deptEventCountSum),2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).doubleValue());
        }
    }

    /**
     * ????????????
     * @param mmap
     * @return
     */
    @GetMapping(value = "Personlist")
    public String PersonList(ModelMap mmap){
        //?????????
        /*mmap.put("user", ShiroUtils.getLoginName());*/
        return prefix + "/Person";
    }

    //???????????? ????????????
    @GetMapping(value = "PersonEventList")
    public String PersonEventList(ModelMap mmap){
        //?????????
        List<HkEClassification> hkEClassifications = hkEClassificationService.queryEventEClassifications();
        mmap.put("eventClass",hkEClassifications);
        return prefix + "/EventPerson";
    }

    /**
     * ???????????? ???????????????????????????????????????????????????
     * @return
     */
    @GetMapping(value = "peopleInfo")
    @ResponseBody
    public AjaxResult queryPeopleInfo(@RequestParam String type){
        /*String eventType ="";
        type = "week";
        if("day".equals(type)){
            eventType = RedisTemplateKey.PEOPLE_INFO_KEY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.PEOPLE_INFO_KEY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.PEOPLE_INFO_KEY_MONTH;//??????????????????
        }
        Map<String,String> map = getEventMaps(eventType);*/
        Map<String,String> map = null;
        if(map == null){
            Date date = new Date();
            map = packagePeople("1",type,date);
            Map<String,String> lastPeopleMap = packagePeople("2",type,date);
            String lastRegionPeopleCount = compareTo(new BigDecimal(map.get("regionPeopleCount")),new BigDecimal(lastPeopleMap.get("regionPeopleCount")));
            String lastWeekSysUsers = compareToRate(new BigDecimal(map.get("accountStatusCount")),new BigDecimal(lastPeopleMap.get("accountStatusCount")));
            String lastWeekEventAvgCountRate = compareToRate(new BigDecimal(map.get("eventAvgCount")),new BigDecimal(lastPeopleMap.get("eventAvgCount")));
            String lastWeekAvgEventTimeRate = compareToRate(new BigDecimal(map.get("avgEventTime")),new BigDecimal(lastPeopleMap.get("avgEventTime")));

            map.put("avgEventTime", DateUtils.subStrDateStringRetainTwo(DateUtil.formatBetween(Long.parseLong(map.get("avgEventTime"))*1000)));
            map.put("regionPeopleCountRate",lastRegionPeopleCount);
            map.put("accountStatusCountRate",lastWeekSysUsers);
            map.put("eventAvgCountRate",lastWeekEventAvgCountRate);
            map.put("avgEventTimeRate",lastWeekAvgEventTimeRate);
            //?????????redis
           /*putEventMaps(eventType,map,"");*/
        }
        AjaxResult success = AjaxResult.success(map);
        return success;
    }

    public Map<String,String> packagePeople(String flag,String type,Date date){
        Map<String,String> peopleParams = new HashMap<>();

        //????????????????????? ???????????????????????????
        /*Map<String,Object> param = new HashMap<>();*/
        /*param.put("postArr",new String[]{"1","2","3"});//???????????????????????????????????????????????????
        Integer regionPeopleCount = userService.findUserCountByParam(param);*///?????????
        /*Integer regionPeopleCount = sysRanksService.querySysRanksCount(param);*/
        //??????????????????
        Integer onLineUserCount = queryOnLineUserCount();

        /*Map<String,Object> thingParams = packageEventDate(flag,type);
        thingParams.put("statusArr",new String[]{"6","7","9"});//?????????
        Integer thingCount = statisticsService.countAllByParams(thingParams);*///????????????
        Map<String,Object> thingParams = packageEventDate(flag,type,date);
        Map<String,Object> params1 = new HashMap<>();
        params1.putAll(thingParams);
        /**?????????????????????  ???????????????????????????**/
        //params1.put("statusArr",new String[]{"6","7","9"});//?????????
        params1.put("processStatusArr",new String[]{"4","6"});//????????????????????????
        /**????????????????????? ?????????????????????????????????????????????????????????????????????????????????????????????????????????**/
        List<PeopleAnalysisVO> peopleAnalysisVOS = statisticsService.queryEventCountByAllRanks(params1);
        Integer eventCount = 0;
        for(PeopleAnalysisVO vo : peopleAnalysisVOS){
            eventCount += vo.getEventCount();
        }
        /**?????????**/
        Integer regionPeopleCount = peopleAnalysisVOS.size();
        String eventAvgCount = divideToAvg(new BigDecimal(regionPeopleCount),new BigDecimal(eventCount));//????????????
        Map<String,Object> params2 = new HashMap<>();
        params2.putAll(thingParams);
        params2.put("statusArr",new String[]{"6","7","8","9"});//?????????
        //params2.put("processStatusArr",new String[]{"4","6"});//????????????????????????
        /**?????????????????????????????????????????????????????????????????????????????????????????????????????? ????????????**/
        Long aLong = statisticsService.queryProcessTimeByParam(params2);//???????????????????????????
        aLong = aLong == null?0:aLong;
        /*Map<String,Object> avgTimeParams = packageEventDate(flag,type);
        //??????????????????
        List<HkEventInfo> hkEventInfos = statisticsService.queryEventByAlterStatus(avgTimeParams);
        BigDecimal timeCount = new BigDecimal(0.00);//?????????
        //???????????? ???????????? - ???????????? / ????????????
        for (HkEventInfo hkEventInfo:hkEventInfos) {
            //?????????
            if(hkEventInfo.getUpdateTime() == null){
                continue;
            }
            BigDecimal hour = new BigDecimal(DateUtils.getHour(hkEventInfo.getUpdateTime(),hkEventInfo.getCreateTime())).setScale(2, BigDecimal.ROUND_HALF_UP);
            timeCount = timeCount.add(hour);
        }*/
        Long avgEventTime = 0L;
        if(aLong > 0){
            avgEventTime = new BigDecimal(aLong).divide(new BigDecimal(regionPeopleCount),2, BigDecimal.ROUND_HALF_UP).longValue();//????????????
        }
        peopleParams.put("regionPeopleCount",String.valueOf(regionPeopleCount));
        peopleParams.put("accountStatusCount",String.valueOf(onLineUserCount));
        peopleParams.put("eventAvgCount",eventAvgCount);
        peopleParams.put("avgEventTime",String.valueOf(avgEventTime));
        return peopleParams;
    }




    /**
     * ???????????? ???????????? ??????????????? ??????????????????
     * @return
     */
    @GetMapping(value = "peopleDisposalEfficiency")
    @ResponseBody
    public AjaxResult queryPeopleDisposalEfficiency(@RequestParam String type) throws ParseException {
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.PEOPLE_DISPOSAL_EFFICIENCY_BY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.PEOPLE_DISPOSAL_EFFICIENCY_BY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.PEOPLE_DISPOSAL_EFFICIENCY_BY_MONTH;//??????????????????
        }
        Map map = getEventMaps(eventType);*/
        Map<String,Object> map = null;
        if(map == null){
            Date date = new Date();
            //??????????????????Map
            Map<String,Object> params = EventDate(type,date);
            params.put("statusArr",new String[]{"6","7","8","9"});//?????????
            //???????????????
            List<TimeField> times = new TimeUtil().getTime(type);
            List<String> eventTrendDate =  times.stream().map(TimeField::getDate).collect(Collectors.toList());//????????????

            //?????????????????? //?????????????????????
            List<Integer> eventTrendCount = new ArrayList<>();

            //??????????????????
            List<EventTrendVO> eventPlaceTypeTrends = statisticsService.queryEventTrends(params);
            //??????type?????????????????????????????????????????????day???00-02 02-04???????????????????????????week????????????????????????month??????7????????????
            List<Integer> eventPlaceTypeTrendCount = DataFormat(eventPlaceTypeTrends,type,times);
            //response
            map = new HashMap();
            map.put("eventTrend",eventTrendCount);
            map.put("eventPlaceTypeTrends",eventPlaceTypeTrendCount);
            map.put("eventTrendDate",eventTrendDate);
            //?????????redis
            /*putEvents(eventType, map,type);*/
        }
        AjaxResult success = AjaxResult.success(map);
        return success;
    }

    /**
     * ???????????? ????????????
     * @return
     */
    @GetMapping(value = "peopleDisposalAnalysis")
    @ResponseBody
    public AjaxResult queryPeopleDisposalAnalysis(@RequestParam String type){
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.PEOPLE_DISPOSAL_ANALYSIS_BY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.PEOPLE_DISPOSAL_ANALYSIS_BY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.PEOPLE_DISPOSAL_ANALYSIS_BY_MONTH;//??????????????????
        }
        Map map = getEventMaps(eventType);*/
        Map<String,Object> map = null;
        if(map == null){
            Date date = new Date();
            Map<String,Object> params = EventDate(type,date);
            /**?????????????????????  ???????????????????????????**/
            //params.put("statusArr",new String[]{"6","7","9"});//?????????
            params.put("processStatusArr",new String[]{"4","6"});//????????????????????????
            params.put("limit","10");
            List<PeopleAnalysisVO> peopleAnalysisVOS = statisticsService.queryEventCountByAllRanks(params);
            //????????????
            /*List<PeopleDisposalAnalysisVO>  peopleDisposalAnalysisVOS = sysRanksService.queryPeopleDisposalAnalysis(params);*/
            List<Integer> eventCounts = peopleAnalysisVOS.stream().map(PeopleAnalysisVO::getEventCount).collect(Collectors.toList());//?????????
            List<String>  userNames = peopleAnalysisVOS.stream().map(PeopleAnalysisVO::getUserName).collect(Collectors.toList());//????????????
            map = new HashMap();
            map.put("eventCounts",eventCounts);
            map.put("userNames",userNames);
            //?????????redis
            /*putEvents(eventType,map,"");*/
        }
        return AjaxResult.success(map);
    }

    /**
     * ???????????? ????????????
     * ???????????????
     * @return
     */
    @GetMapping(value = "peopleEfficiency")
    @ResponseBody
    public AjaxResult queryPeopleEfficiency(@RequestParam(required = true) String type){
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.PEOPLE_EFFICIENCY_COUNT_BY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.PEOPLE_EFFICIENCY_COUNT_BY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.PEOPLE_EFFICIENCY_COUNT_BY_MONTH;//??????????????????
        }
        List<PeopleDisposalAnalysisVO> peopleEfficiencyVOS = getEvents(eventType);*/
        List<PeopleDisposalAnalysisVO> peopleEfficiencyVOS = new ArrayList<>();
        if(peopleEfficiencyVOS ==null || peopleEfficiencyVOS.size() <1){
            Date date = new Date();
            Map<String,Object> params = EventDate(type,date);
            //??????????????????????????????
            /*peopleEfficiencyVOS = sysRanksService.queryPeopleDisposalAnalysis(params);*/
            /*peopleEfficiencyVOS = sysUserService.queryPeopleDisposalAnalysis(params);*/
            peopleEfficiencyVOS = statisticsService.queryPeopleDisposalAnalysis(params);
            //?????????redis
            /*putEvents(eventType,peopleEfficiencyVOS,"");*/
        }
        return AjaxResult.success(peopleEfficiencyVOS);
    }

    /**
     * ???????????? ????????????
     * @param type
     * @return
     */
    @GetMapping(value = "peopleType")
    @ResponseBody
    public AjaxResult queryPeopleType(@RequestParam String type){
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.PEOPLE_TYPE_COUNT_BY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.PEOPLE_TYPE_COUNT_BY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.PEOPLE_TYPE_COUNT_BY_MONTH;//??????????????????
        }
        List<PeopleTypeVO> list = getEvents(eventType);*/
        List<PeopleTypeVO> list = new ArrayList<>();
        if(list == null || list.size() < 1){
            list = new ArrayList<>();
            Map<String,Object> params = new HashMap<>();
            List<SysPost> postList = postService.selectPostList(new SysPost());
            for (SysPost post:postList) {
                params.put("postArr",new Long[]{post.getPostId()});
                Integer count = userService.findUserCountByParam(params);
                PeopleTypeVO peopleTypeVO = new PeopleTypeVO();
                Integer peopleOnLines = queryOnLineUserCount();//????????????
                peopleTypeVO.setName(post.getPostName());
                peopleTypeVO.setPeopleOnLineCount(peopleOnLines);
                peopleTypeVO.setPeopleOutLineCount(count-peopleOnLines);
                peopleTypeVO.setValue(count);
                list.add(peopleTypeVO);
            }
            //?????????redis
            /*putEvents(eventType,list,"");*/
        }
        AjaxResult success = AjaxResult.success(list);
        return success;
    }

    /**
     * ???????????? ????????????
     *  @param station ???????????????????????? ?????????111 ????????????222?????????333???????????????
     * @return
     */
    @GetMapping(value = "peopleTime")
    @ResponseBody
    public AjaxResult queryPeopleTime(@RequestParam String station){
        /*String eventType = "";
        if("1".equals(station)){
            eventType = RedisTemplateKey.PEOPLE_TIME_COUNT_BY_MAN;
        }else if("3".equals(station)){
            eventType = RedisTemplateKey.PEOPLE_TIME_COUNT_COMMON;
        }else{
            eventType = RedisTemplateKey.PEOPLE_TIME_COUNT_BY_MANGE;
        }
        Map map = getEventMaps(eventType);*/
        Map<String,Object> map = null;
        if(map == null){
            //?????????????????????
            Map<String,Object> params = new HashMap<>();
            /*params.put("station",station);//?????? 1 ????????? 2 ??????????????? 3???????????????
            params.put("accountStatus","0");//???????????? 0????????? 1?????????
            params.put("trendWeekMonday", DateFormatUtils.format(DateUtils.getThisWeekMonday(),DateUtils.YYYY_MM_DD));//????????????
            *//*List<PeopleTimeVO> peopleTimeVOS = sysRanksService.queryPeopleTime(params);*//*
            List<PeopleTimeVO> peopleTimeVOS = sysUserService.queryPeopleTime(params);*/


            /**?????????????????????  ???????????????????????????**/
            //params.put("statusArr",new String[]{"6","7","9"});//?????????
            params.put("processStatusArr",new String[]{"4","6"});//????????????????????????
            if("111".equals(station)){//?????????
                params.put("postArr",new String[]{"1","2","3"});//???????????????????????????????????????
            }else if("222".equals(station)){//??????
                params.put("postArr",new String[]{"4"});//??????
            }else if("333".equals(station)){//????????????
                params.put("postArr",new String[]{"5"});//????????????
            }
//            params.put("limit","10");
            List<PeopleAnalysisVO> peopleAnalysisVOS = statisticsService.queryEventCountByParam(params);
            List<Double> responseTimes = new ArrayList<>();
            List<String> names = new ArrayList<>();
            for(PeopleAnalysisVO vo : peopleAnalysisVOS){
                names.add(vo.getUserName());
                if(vo.getEventCount() > 0){
                    params.put("userId",vo.getUserId());
                    Long aLong = statisticsService.queryProcessTimeByParam(params);//??????????????????????????????????????????????????????????????????????????????
                    aLong = aLong == null?0:aLong;
                    responseTimes.add(DateUtils.getHourByTime1(aLong/vo.getEventCount()));
                }else{
                    responseTimes.add(0.00);
                }
            }
            map = new HashMap();
            map.put("times",responseTimes);
            map.put("names",names);
            //?????????redis
            /*putEventMaps(eventType,map,"");*/
        }
        //???????????? / ?????????  ????????????/?????????
        AjaxResult success = AjaxResult.success(map);
        return success;
    }

    /**
     * ????????????
     * @param mmap
     * @return
     */
    @GetMapping(value = "Stagelist")
    public String StageList(ModelMap mmap){
        //?????????
        return prefix + "/Stage";
    }


    //???????????? ????????????
    @GetMapping(value = "StageEventList")
    public String StageEventList(ModelMap mmap){
        //?????????
        List<HkEClassification> hkEClassifications = hkEClassificationService.queryEventEClassifications();
        mmap.put("eventClass",hkEClassifications);
        return prefix + "/EventStage";
    }

    /**
     * ???????????? ????????????
     * @return
     */
    @GetMapping(value = "stageCount")
    @ResponseBody
    public AjaxResult queryStageCount(@RequestParam String type){
        String eventType = "";
        Integer stageCount = 0;//?????????
        Date date = new Date();
        if("day".equals(type)){
            stageCount = 1;
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.STAGE_COUNT_WEEK;//??????????????????
            stageCount = 7;
        }else if("month".equals(type)){
            eventType = RedisTemplateKey.STAGE_COUNT_MONTH;//??????????????????
            //calendar.setTime(DateUtils.getThisMonthEndDay());
            stageCount = Integer.parseInt(DateUtil.betweenDay(DateUtil.offsetMonth(date,-1),date,false)+"");
        }else{
            stageCount = 365;
            try{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parse = sdf.parse(DateUtils.getNearlyYear(date, -1));
                stageCount = Integer.parseInt(DateUtil.betweenDay(parse,date,false)+"");
            }catch(Exception ex){
                logger.info("==================????????????????????????==================");
                ex.printStackTrace();
            }
        }
        //??????????????????????????? redis
        /*Map data = getEventMaps(eventType);*/
        Map<String,Object> data = null;
        if(data == null){

            //????????????
            Map<String,Object> param = new HashMap<>();
            Integer peopleCount = sysRanksService.querySysRanksCount(param);
            //?????????
            param.put("level",2);
            int stageJurisdictionCount = sysRegionService.queryRegionCountByParam(param);
            Map<String,Object> thingParams = EventDate(type,date);
            thingParams.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            Integer thingCount = statisticsService.countAllByParams(thingParams);//????????????
            //?????????
            Map<String,Object> params = EventDate(type,date);
            params.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            int stageEventCount = thingCount - statisticsService.countAllByParams(params);

            //?????????
            int LastStageJurisdictionCount = 0;

            /**??????????????????**/
            Map<String,Object> lastThingParams = lastEventDate(type,date);
            lastThingParams.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            Integer LastThingCount = statisticsService.countAllByParams(lastThingParams);//????????????

            //?????????
            int LastStageEventCount = LastThingCount - thingCount;


            data = new HashMap();
            data.put("stageCount",stageCount);
            data.put("peopleCount",peopleCount);
            data.put("stageJurisdictionCount",stageJurisdictionCount);
            data.put("stageEventCount",stageEventCount);
            data.put("peopleCountRate",compareTo( new BigDecimal(peopleCount),new BigDecimal(peopleCount)));
            data.put("stageJurisdictionCountRate",compareTo( new BigDecimal(stageJurisdictionCount),new BigDecimal(LastStageJurisdictionCount)));
            data.put("stageEventCountRate",compareTo( new BigDecimal(stageEventCount),new BigDecimal(LastStageEventCount)));
            //?????????redis
            /*putEvents(eventType, data,type);*/
        }
        AjaxResult success = AjaxResult.success(data);
        return success;
    }

    /**
     * ???????????? ????????????
     * @param vo ??????: day ?????? week ?????? month
     * @return
     */
    @RequestMapping(value = "eventRegionStage",method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult eventRegionTest(HkEventThingParamVO vo){
        //redis-key
        String type = vo.getType();
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.THING_REGION_BY_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.THING_REGION_BY_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.THING_REGION_BY_MONTH;//??????????????????
        }
        //??????????????????????????? redis
        Map data = getEventMaps(eventType);*/
        Map<String,Object> data = null;
        if(data == null){
            data = new HashMap();
            Date date = new Date();
            //??????????????????Map
            Map<String,Object> params = EventDate(type,date);
            params.put("desc","desc");
            //??????????????????
            List<EventInspectVO>  eventInspectVOS = statisticsService.queryEventRegion1(params);
            List<Integer> eventRegionCount = eventInspectVOS.stream().map(EventInspectVO::getEventCount).collect(Collectors.toList());//??????????????????
            List<Integer> eventPlaceTypeRegionCount = new ArrayList<>();
            List<String> regionName = eventInspectVOS.stream().map(EventInspectVO::getRegionName).collect(Collectors.toList());//????????????
            data.put("eventRegionCount",eventRegionCount);
            data.put("eventPlaceTypeRegionCount",eventPlaceTypeRegionCount);
            data.put("regionName",regionName);
            //?????????redis
            /*putEventMaps(type, data,type);*/
        }
        AjaxResult success = AjaxResult.success(data);
        return success;
    }

    /**
     * ???????????? ????????????
     * @param type
     * @return
     */
    @GetMapping(value = "stageRegionEfficiency")
    @ResponseBody
    public AjaxResult queryStageRegionEfficiency(String type){
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.STAGE_REGION_EFFICIENCY_COUNT_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.STAGE_REGION_EFFICIENCY_COUNT_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.STAGE_REGION_EFFICIENCY_COUNT_MONTH;//??????????????????
        }
        Map map = getEventMaps(eventType);*/
        Map<String,Object> map = null;
        if(map ==null ){
            map = new HashMap();
            Date date = new Date();
            //?????????????????????
            Map<String,Object> thingParams = EventDate(type,date);
            thingParams.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            List<RegionEventAnalysisVO> regionEventAnalysisVOS = statisticsService.queryRegionAnalysis(thingParams);
            System.out.println(JSONObject.toJSONString(regionEventAnalysisVOS));
            Map<String,Integer> regionEvent = new HashMap<>();
            for(RegionEventAnalysisVO regionEventAnalysisVO:regionEventAnalysisVOS){
                regionEvent.put(regionEventAnalysisVO.getRegionName(),regionEventAnalysisVO.getEventCount());
            }

            //???????????????
            Map<String,Object> handleStatusParams = EventDate(type,date);
            handleStatusParams.put("statusArr",new String[]{"6","7","8","9"});
            List<RegionEventAnalysisVO> handleRegionEventAnalysisVOS = statisticsService.queryRegionAnalysis(handleStatusParams);
            packStageRegionEfficiency(regionEvent,handleRegionEventAnalysisVOS,false);

            List<Integer> regionEfficiencyCount = regionEventAnalysisVOS.stream().map(RegionEventAnalysisVO::getEventCount).collect(Collectors.toList());//?????????
            List<String> regionEfficiencyName = regionEventAnalysisVOS.stream().map(RegionEventAnalysisVO::getRegionName).collect(Collectors.toList());//????????????
            List<Integer> regionCommonEfficiencyCount = handleRegionEventAnalysisVOS.stream().map(RegionEventAnalysisVO::getEventCount).collect(Collectors.toList());//?????????
            List<String> regionCommonEfficiencyName = handleRegionEventAnalysisVOS.stream().map(RegionEventAnalysisVO::getRegionName).collect(Collectors.toList());
            map.put("regionEfficiencyCount",regionEfficiencyCount);
            map.put("regionEfficiencyName",regionEfficiencyName);
            map.put("regionCommonEfficiencyCount",regionCommonEfficiencyCount);
            map.put("regionCommonEfficiencyName",regionCommonEfficiencyName);
            //?????????redis
            /*putEventMaps(eventType,map,"");*/
        }
        AjaxResult success = AjaxResult.success(map);
        return success;
    }

    public void packStageRegionEfficiency(Map<String,Integer> map,List<RegionEventAnalysisVO> regionEventAnalysisVOS,boolean flag){
        for(RegionEventAnalysisVO regionEventAnalysisVO:regionEventAnalysisVOS){
            Integer deptEventCountSum = map.get(regionEventAnalysisVO.getRegionName());
            Integer deptEventCount = regionEventAnalysisVO.getEventCount();
            if(flag){
                deptEventCount = deptEventCountSum - regionEventAnalysisVO.getEventCount();
            }
            regionEventAnalysisVO.setEventCount(deptEventCount);
        }
    }

    /**
     * ???????????? ????????????
     * @return
     */
    @GetMapping(value = "stageRegionLoad")
    @ResponseBody
    public AjaxResult stageRegionLoad(String type){
        /*String eventType ="";
        if("day".equals(type)){
            eventType = RedisTemplateKey.STAGE_ANALYSIS_COUNT_DAY;//??????????????????
        }else if("week".equals(type)){
            eventType = RedisTemplateKey.STAGE_ANALYSIS_COUNT_WEEK;//??????????????????
        }else{
            eventType = RedisTemplateKey.STAGE_ANALYSIS_COUNT_MONTH;//??????????????????
        }
        Map map = getEventMaps(eventType);*/
        Map<String,Object> map = null;
        if(map == null){
            map = new HashMap();
            Date date = new Date();
            //????????????????????????
            Map<String,Object> thingParams = EventDate(type,date);
            thingParams.put("statusArr",new String[]{"1","2","3","4","5","6","7","8","9"});
            List<RegionEventAnalysisVO> regionEventAnalysisVOS = statisticsService.queryRegionAnalysis(thingParams);
            Map<String,Integer> regionEvent = new HashMap<>();
            for(RegionEventAnalysisVO regionEventAnalysisVO:regionEventAnalysisVOS){
                regionEvent.put(regionEventAnalysisVO.getRegionName(),regionEventAnalysisVO.getEventCount());
            }
        }
        return AjaxResult.success(map);
    }


    private Map<String,Object> packageEventDate(String flag,String type,Date date){
        if("1".equals(flag)){
            return EventDate(type,date);
        }else{
            return lastEventDate(type,date);
        }
    }

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
            //map.put("trendWeekMonday",DateFormatUtils.format(DateUtils.getThisWeekMonday(),"yyyy-MM-dd"));
            //?????????
            map.put("nearlyAWeekBegin",DateUtil.offsetWeek(date, -1).toString());
        }else if("month".equals(type)){
            //????????????
            //map.put("trendMonthMonday",DateFormatUtils.format(DateUtils.getThisMonthMonday(),"yyyy-MM-dd"));
            //?????????
            map.put("nearlyAMonthBegin",DateUtil.offsetMonth(date, -1).toString());
        }else if("year".equals(type)){
            //?????????
            map.put("nearlyAYearBegin",DateUtils.getNearlyYear(date,-1));
        }
        return map;
    }

    /**
     * ??????????????????
     * @param type
     * @return
     */
    public Map<String,Object> lastEventDate(String type,Date date){
        Map<String,Object> map = new HashMap<>();
        if("day".equals(type)){
            //??????
            //map.put("currentDate",DateFormatUtils.format(DateUtils.getTwentyFour(),"yyyy-MM-dd"));
            map.put("nearlyADayBegin",DateUtil.offsetDay(date, -2).toString());
            map.put("nearlyADayEnd",DateUtil.offsetDay(date, -1).toString());
        }else if("week".equals(type)){
            //map.put("thisWeekMonday",DateFormatUtils.format(DateUtils.getThisWeekMonday(),DateUtils.YYYY_MM_DD));//?????????
            //map.put("lastWeekDay",DateFormatUtils.format(DateUtils.getLastWeekMonday(),DateUtils.YYYY_MM_DD));//?????????
            map.put("nearlyAWeekBegin",DateUtil.offsetWeek(date, -2).toString());
            map.put("nearlyAWeekEnd",DateUtil.offsetWeek(date, -1).toString());
        }else if("month".equals(type)){
            //map.put("thisMonthFirstDay",DateFormatUtils.format(DateUtils.getThisMonthMonday(),"yyyy-MM-dd"));//????????????
            //map.put("lastMonthFirstDay",DateFormatUtils.format(DateUtils.getLastMonthFirstDay(),"yyyy-MM-dd"));//???????????????
            map.put("nearlyAMonthBegin",DateUtil.offsetMonth(date, -2).toString());
            map.put("nearlyAMonthEnd",DateUtil.offsetMonth(date, -1).toString());
        }else{
            //?????????
            map.put("nearlyAYearBegin",DateUtils.getNearlyYear(date,-1));
            map.put("nearlyAYearEnd",DateUtils.getNearlyYear(date,-1));
        }
        return map;
    }



    /**
     * ????????????KEY ??????????????????
     * @param thingKey ??????key
     * @return
     */
    private String getEventCounts(String thingKey){
        ValueOperations<String,String> operations = redisTemplate.opsForValue();
        return operations.get(thingKey);
    }

    /**
     * ????????????key ??????????????????
     * @param thingKey ??????key
     * @param thingValue ??????value
     * @param hour
     */
    private void putEventCount(String thingKey,String thingValue,int hour){
        ValueOperations<String,String> operations = redisTemplate.opsForValue();
        operations.set(thingKey,thingValue);
        redisTemplate.expire(thingKey,hour, TimeUnit.HOURS);
    }

    /**
     * ????????????KEY ??????maps????????????
     * @param thingKey ??????key ??????value
     */
    private Map<String,String> getEventMaps(String thingKey){
        ValueOperations<String,Map<String,String>> operations = redisTemplate.opsForValue();
        return operations.get(thingKey);
    }

    /**
     * ????????????KEY ??????maps????????????
     * @param thingKey ??????key ??????value
     * @param thingValue ??????value
     * @param type ??????type?????????????????????????????????
     */
    private void putEventMaps(String thingKey,Map<String,String> thingValue,String type){
        ValueOperations<String,Map<String,String>> operations = redisTemplate.opsForValue();
        operations.set(thingKey,thingValue);
        redisTemplate.expire(thingKey,2, TimeUnit.HOURS);
    }

    /**
     * ????????????KEY ??????????????????
     * @param thingKey ??????key
     * @return
     */
    private  <T> List<T>  getEvents(String thingKey){
        ValueOperations<String,List<T>> operations = redisTemplate.opsForValue();
        return operations.get(thingKey);
    }

    /**
     * ????????????KEY ??????????????????
     * @param thingKey ??????key
     * @param clazz ????????????List
     * @param type  ??????type?????????????????????????????????
     * @param <T>
     */
    private  <T> void  putEvents(String thingKey,T clazz,String type){
        ValueOperations<String,T> operations = redisTemplate.opsForValue();
        operations.set(thingKey,clazz);
        //??????redis???????????????2??????
        if("day".equals(type)){
            redisTemplate.expire(thingKey,2, TimeUnit.HOURS);
        }
    }

    public int queryOnLineUserCount(){
        Set keys = redisTemplate.keys(jwtConfigProperties.getCachePrefix() + "*");
        return keys.size();
    }

    /**
     * ?????????????????????????????? ??????day???00-02 02-04???????????????????????????week????????????????????????month??????7????????????
     * @param eventTrendVOS ?????????
     * @param type ????????????????????????
     * @return
     */
    private  List<Integer>  DataFormat(List<EventTrendVO> eventTrendVOS, String type,List<TimeField> timeFields) throws ParseException {
        for(EventTrendVO eventTrendVO : eventTrendVOS){
            for(TimeField timeField:timeFields){
                Integer count = timeField.getCount();
                if(count == null){
                    count = 0;
                    timeField.setCount(count);
                }
                String format = timeField.getFormat();
                Date startDate =  DateUtils.parseDate(timeField.getStartTime(),format);
                Date endDate = DateUtils.parseDate(timeField.getEndTime(),format);
                String DateStr = DateUtils.parseDateToStr(format,eventTrendVO.getCreateTime());
                if(startDate.getTime()<= DateUtils.parseDate(DateStr,format).getTime() && endDate.getTime()>=DateUtils.parseDate(DateStr,format).getTime()){
                    timeField.setCount(++count);
                }
            }
        }
        return timeFields.stream().map(TimeField::getCount).collect(Collectors.toList());//??????????????????
    }

}
