package com.ruoyi.web.controller.system.HkEarlyWarning;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.Arith;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Action.HkActionChain;
import com.ruoyi.system.domain.Action.HkActionDefinition;
import com.ruoyi.system.domain.Action.HkActionProcess;
import com.ruoyi.system.domain.HkEarlyWarning.*;
import com.ruoyi.system.domain.HkEntity.EventSearchVO;
import com.ruoyi.system.domain.ding.DingEvent;
import com.ruoyi.system.domain.vo.CenterConfirmUserVo;
import com.ruoyi.system.domain.vo.EventDeailVo;
import com.ruoyi.system.domain.vo.EventRecommendedVo;
import com.ruoyi.system.mapper.appEventInfoMapper;
import com.ruoyi.system.service.*;
import com.ruoyi.system.service.HkEarlyWarning.HkEventService;
import com.ruoyi.system.service.ding.DingEventService;
import com.ruoyi.system.service.impl.HkActionChainServiceImpl;
import com.ruoyi.system.service.statistics.HkEventStatisticsService;
import io.swagger.models.auth.In;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author FanKaibiao
 * @date 2020-11-03-16:48
 */
@Controller
@RequestMapping("system/event")
public class HkEventController extends BaseController {

    private String prefix = "system/event";

    @Autowired
    private HkEventService hkEventService;

    @Autowired
    private HkActionDefinitionService hkActionDefinitionService;

    @Autowired
    private ISysUserPostRegionService iSysUserPostRegionService;

    @Autowired
    private HkActionChainService hkActionChainService;

    @Autowired
    private HkEventStatisticsService statisticsService;

    @Autowired
    private ISysDeptService iSysDeptService;

    @Autowired
    private ISysUserService iSysUserService;

    @Autowired
    private ISysRegionService iSysRegionService;

    @Autowired
    private IHkLmanagementInfoService LmanagementInfoService;

    @Autowired
    private ISysPostService iSysPostService;

    @GetMapping()
    public String eventList(){
        return prefix + "/event";
    }

    /**
     * ??????id????????????
     * @param id
     * @return
     */
    @RequestMapping(value = "details",method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult hkEventDetails(Integer id){
        HkEventInfo eventDetail = hkEventService.queryEventDetail(id);
        if(eventDetail == null){
            return AjaxResult.error("?????????????????????");
        }
        //???????????? ???????????????????????????
        List<Map<String,Object>> list = new ArrayList<>();
        eventDetail.setExtendStr2("????????????");
        String regionIndexCode = eventDetail.getRegionIndexCode();
        if(!StringUtils.isEmpty(regionIndexCode)){
            String regionDetail = iSysRegionService.queryRegionAncestorsByRegion(Integer.parseInt(regionIndexCode));
            eventDetail.setRegionDetail(regionDetail);
            //????????????code????????????,?????????????????????code?????????????????????????????????
            if(!StringUtils.isEmpty(regionIndexCode) && !"100".equals(regionIndexCode)){
                List<SysUser> allUser = iSysUserService.findAllUserName(regionIndexCode);
                for(SysUser user : allUser){
                    Map<String,Object> map = new HashMap<>();
                    map.put("userName",user.getUserName());
                    map.put("phonenumber",user.getPhonenumber());
                    map.put("userPostName",user.getUserPostName());
                    list.add(map);
                }
                eventDetail.setListMap(list);
            }
        }
        //??????id ???????????????????????????
        hkEventService.updateEventReadType(id);
        Date createTime = eventDetail.getCreateTime();
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date1 = yyyyMMdd.format(createTime);
        eventDetail.setDateNow(date1);
        return AjaxResult.success(eventDetail);
    }


    /**
     * ???????????????????????? ????????????????????????????????????
     * @param hkEventInfo
     * @return ????????????
     */
    @RequestMapping(value = "getEventWithList",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult getEventWithList(HkEventInfo hkEventInfo){
        //??????????????? ?????? ????????????
        HkActionChain hkActionChain;
        hkActionChain = hkActionChainService.setActionIdByComponentAndLongitude(hkEventInfo);
        if(hkActionChain==null) return AjaxResult.warn("???????????????????????????,????????????????????????");
        //??????????????????????????????
        HkActionDefinition hkActionDefinition = new HkActionDefinition();
        hkActionDefinition.setActionChainId(hkActionChain.getActionChainId());
        hkActionDefinition.setHandlerOrder("1");
        List<HkActionDefinition> definition = hkActionDefinitionService.findDefinitionByHandlerOrder(hkActionDefinition);
        //???????????? ??? ?????? ?????? ?????????????????????????????????
        Integer handlerPost = definition.get(0).getHandlerPostId();             //??????id
        String actionRegionCode = hkActionChain.getActionRegionCode();          //??????code
        SysUserPostRegion sysUserPostRegion = new SysUserPostRegion(handlerPost,actionRegionCode);
        ArrayList<CenterConfirmUserVo> eventWithlist = new ArrayList<>();
        //????????????id ???????????????id
        List<SysUserPostRegion> sysUserPostRegions = iSysUserPostRegionService.selectSysUserPostRegionList(sysUserPostRegion);
        sysUserPostRegions.forEach(item ->{
            SysUser sysUser = new SysUser();
            sysUser.setUserId(new Long(item.getUserId()));
            sysUser.setDelFlag("0");
            SysUser sysUsers = iSysUserService.selectUserByidAndDelFalg(sysUser);

            CenterConfirmUserVo centerConfirmUserVo = new CenterConfirmUserVo();
            centerConfirmUserVo.setUserid(item.getUserId().intValue());
            centerConfirmUserVo.setPostName(iSysPostService.selectPostById(new Long(handlerPost)).getPostName());
            centerConfirmUserVo.setPostId(handlerPost);
            centerConfirmUserVo.setUsername(sysUsers.getUserName());
            centerConfirmUserVo.setPhonenumber(sysUsers.getPhonenumber());
            centerConfirmUserVo.setRegionId(Integer.valueOf(item.getRegionId()));
            centerConfirmUserVo.setRegionName(iSysRegionService.selectSysRegionById(new Long(item.getRegionId())).getRegionName());
            eventWithlist.add(centerConfirmUserVo);
        });
        return AjaxResult.success(eventWithlist);
    }

    /**
     * -?????????????????????
     * @param eventRecommendedVo hkEventInfo ??? centerConfirmUserVo ????????????
     * @return
     */
    @RequestMapping(value = "confirmDetail",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult confirmDetail(@RequestBody EventRecommendedVo eventRecommendedVo){
        //????????????
        HkEventInfo hkEventInfo = eventRecommendedVo.getHkEventInfo();
        CenterConfirmUserVo centerConfirmUserVo = eventRecommendedVo.getCenterConfirmUserVo();
        /**
         * 1.??????????????????
         */
        Integer id = hkEventInfo.getId();
        //???????????????????????????????????????????????????
        String eventType = hkEventInfo.getComponentId();
        String regionIndexCode = centerConfirmUserVo.getRegionId().toString();//??????????????????code
        HkActionChain hkActionChain = new HkActionChain();
        hkActionChain.setActionStandard(eventType);
        hkActionChain.setActionRegionCode(regionIndexCode);
        List<HkActionChain> hkActionChains=hkActionChainService.findActionChain(hkActionChain);
        if(hkActionChains.size() == 0) return AjaxResult.warn("???????????????????????????,????????????????????????");
        //???????????????
        HkActionChain actionChain = hkActionChains.get(0);
        //set???????????????????????????
        HkEventInfo eventInfo = new HkEventInfo();
        eventInfo.setId(id);
        eventInfo.setUpdateTime(new Date());
        eventInfo.setEventAlertStatus(3);
        eventInfo.setEventAlertStatusName("?????????");
        String expectedEvents = actionChain.getExpectedEvents();
        String[] split = expectedEvents.split("/");
        String s=split[0];

        Date parse = null;
        if(s.equals("?????????")){
            String date=split[1];
            parse = DateUtil.offset(new Date(), DateField.MINUTE, Integer.parseInt(date));//????????????
            eventInfo.setTimeOut(parse);
        }else if(s.equals("?????????")){
            String date=split[1];
            parse = DateUtil.offset(new Date(), DateField.HOUR_OF_DAY, Integer.parseInt(date));
            eventInfo.setTimeOut(parse);
        }
        SysUser user = ShiroUtils.getSysUser();
        if(user==null){ return AjaxResult.warn("????????????????????????????????????"); }
        eventInfo.setProcdefType(actionChain.getActionChainId());
        eventInfo.setDispatchEventUser(Integer.parseInt(user.getUserId()+""));
        eventInfo.setDispatchEventTime(new Date());
        eventInfo.setRegionName(centerConfirmUserVo.getRegionName());
        eventInfo.setRegionIndexCode(centerConfirmUserVo.getRegionId().toString());
        /**
         * 2.?????????????????????
         */
        List<HkActionProcess> processList = new ArrayList<>();
        //???????????????id????????????????????????
        HkActionDefinition hkActionDefinition = new HkActionDefinition();
        hkActionDefinition.setActionChainId(actionChain.getActionChainId());
        List<HkActionDefinition> hkActionDefinitions=hkActionDefinitionService.findDefinitionByChainId(hkActionDefinition);
        //??????????????????
        Random rd = new Random();
        hkActionDefinitions.forEach(item->{
            HkActionProcess hkActionProcess = new HkActionProcess();
            hkActionProcess.setActionChainId(actionChain.getActionChainId());
            hkActionProcess.setEventId(hkEventInfo.getEventId());
            hkActionProcess.setActionStandardId(item.getActionStandardId());//????????????id
            hkActionProcess.setHandlerOrder(item.getHandlerOrder());
            hkActionProcess.setCreateTime(new Date());
            hkActionProcess.setCreateUser(user.getUserName());
            hkActionProcess.setPostId(item.getHandlerPostId());
            //?????????????????????????????? ?????????????????????????????????????????? ?????????????????????
            if(item.getHandlerOrder().equals("1")){
                hkActionProcess.setHandlerUserId(centerConfirmUserVo.getUserid().toString());
            }else{
                String postId = item.getHandlerPostId().toString();
                Long regionid = Long.valueOf(actionChain.getActionRegionCode());
                boolean b = hkEventInfo.getComponentId().equals("1001");
                Long parentId = b ? iSysRegionService.queryParentRegionByRegionId(regionid) : regionid;
                List<SysUserPostRegion> userPostRegionList = iSysUserService.selectUserPostRegionByRidPid(parentId.toString(),postId);
                if(userPostRegionList.size()>0) {
                    int number = rd.nextInt(userPostRegionList.size());//??????[0???userPostRegionList.size())?????????
                    SysUserPostRegion userPostRegion = userPostRegionList.get(number);
                    hkActionProcess.setHandlerUserId(userPostRegion.getUserId().toString());
                }
            }
            processList.add(hkActionProcess);
        });
        /**
         * 3.?????????????????????
         */
        HKrecord hKrecord = new HKrecord();
        // ??????????????????
        hKrecord.setRecordId(UUID.randomUUID().toString().trim().replaceAll("-", ""));
        hKrecord.setHandlerIndexCode(centerConfirmUserVo.getUserid().toString());
        hKrecord.setHandlerName("");
        hKrecord.setHandlerContent("");
        hKrecord.setEventId(hkEventInfo.getEventId());
        hKrecord.setProcessStatus("2");//??????
        hKrecord.setExtendStr3(user.getUserName());
        hKrecord.setExtendInt3(1);
        try {
            logger.info("<<<<<<<<<<<???????????????????????????param:{}", JSON.toJSONString(processList));
            logger.info("<<<<<<<<<<<?????????????????????????????????param:{}", JSON.toJSONString(eventInfo));
            hkEventService.updateEventAndProcess(eventInfo, processList, hKrecord, user);
            return AjaxResult.success();
        }catch (Exception e){
            logger.error("??????????????????:",e);
            return AjaxResult.error("?????????????????????????????????????????????");
        }
    }


    /**
     * ????????????
     * @param hkEventInfo
     * @return
     */
    @RequestMapping(value = "confirmDetail1",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult confirmDetail(HkEventInfo hkEventInfo){
        //??????id
        Integer id = hkEventInfo.getId();
        Date parse = null;
        //???????????????????????????????????????????????????
        String eventType = hkEventInfo.getComponentId();
        String regionIndexCode = hkEventInfo.getRegionIndexCode();//??????????????????code
        HkActionChain hkActionChain = new HkActionChain();
        hkActionChain.setActionStandard(eventType);
        hkActionChain.setActionRegionCode(regionIndexCode);
        List<HkActionChain> hkActionChains=hkActionChainService.findActionChain(hkActionChain);
        if(hkActionChains.size() == 0) {
            return AjaxResult.warn("???????????????????????????,????????????????????????");
        }

        HkActionChain actionChain = hkActionChains.get(0);
        //set???????????????????????????
        HkEventInfo eventInfo = new HkEventInfo();
        eventInfo.setId(id);
        eventInfo.setUpdateTime(new Date());
        eventInfo.setEventAlertStatus(3);
        eventInfo.setEventAlertStatusName("?????????");
        String expectedEvents = actionChain.getExpectedEvents();
        String[] split = expectedEvents.split("/");
        String s=split[0];
        if(s.equals("?????????")){
            String date=split[1];
            parse = DateUtil.offset(new Date(), DateField.MINUTE, Integer.parseInt(date));//????????????
            eventInfo.setTimeOut(parse);
        }else if(s.equals("?????????")){
            String date=split[1];
            parse = DateUtil.offset(new Date(), DateField.HOUR_OF_DAY, Integer.parseInt(date));
            eventInfo.setTimeOut(parse);
        }
        eventInfo.setProcdefType(actionChain.getActionChainId());

        List<HkActionProcess> processList = new ArrayList<>();
        //???????????????id????????????????????????
        HkActionDefinition hkActionDefinition = new HkActionDefinition();
        hkActionDefinition.setActionChainId(actionChain.getActionChainId());
        List<HkActionDefinition> hkActionDefinitions=hkActionDefinitionService.findDefinitionByChainId(hkActionDefinition);

        String regionId = actionChain.getActionRegionCode();
        Random rd = new Random();
        String userName = ShiroUtils.getSysUser().getUserName();
        String handlerUserId = "";//?????????????????????
        for (HkActionDefinition definition : hkActionDefinitions){
            String postId = definition.getHandlerPostId().toString();
            List<SysUserPostRegion> userPostRegionList = iSysUserService.selectUserPostRegionByRidPid(regionId,postId);
            if (userPostRegionList.size() == 0){
                logger.info("??????????????????[{}]?????????[{}]??????????????????",regionId,postId);
                return AjaxResult.error("????????????????????????????????????????????????????????????????????????");
            }
            int number = rd.nextInt(userPostRegionList.size());//??????[0???userPostRegionList.size())?????????
            SysUserPostRegion userPostRegion = userPostRegionList.get(number);

            HkActionProcess hkActionProcess = new HkActionProcess();
            //??????????????????????????????
            hkActionProcess.setActionChainId(actionChain.getActionChainId());
            hkActionProcess.setEventId(hkEventInfo.getEventId());
            hkActionProcess.setHandlerUserId(userPostRegion.getUserId().toString());
            hkActionProcess.setActionStandardId(definition.getActionStandardId());//????????????id
            hkActionProcess.setHandlerOrder(definition.getHandlerOrder());
            hkActionProcess.setCreateTime(new Date());
            hkActionProcess.setCreateUser(userName);
            if ("1".equals(definition.getHandlerOrder())){ //???????????? ???????????????????????????????????????????????????
                handlerUserId = userPostRegion.getUserId().toString();
            }
            processList.add(hkActionProcess);
        }
        HKrecord hKrecord = new HKrecord();
        // ??????????????????
        hKrecord.setRecordId(UUID.randomUUID().toString().trim().replaceAll("-", ""));
        hKrecord.setHandlerIndexCode(handlerUserId);
        hKrecord.setHandlerName("");
        hKrecord.setHandlerContent("");
        hKrecord.setEventId(hkEventInfo.getEventId());
        hKrecord.setProcessStatus("2");//??????
        hKrecord.setExtendStr3(userName);
        hKrecord.setExtendInt3(1);
        try {
            logger.info("<<<<<<<<<<<???????????????????????????param:{}", JSON.toJSONString(processList));
            logger.info("<<<<<<<<<<<?????????????????????????????????param:{}", JSON.toJSONString(eventInfo));
            SysUser user = ShiroUtils.getSysUser();
            eventInfo.setDispatchEventUser(Integer.parseInt(user.getUserId()+""));
            eventInfo.setDispatchEventTime(new Date());
            hkEventService.updateEventAndProcess(eventInfo, processList,hKrecord,user);
            return AjaxResult.success();
        }catch (Exception e){
            logger.error("??????????????????:",e);
            return AjaxResult.error("?????????????????????????????????????????????");
        }
    }

    /**
     * ??????????????????
     */
    @RequestMapping(value = "confirmYingDetail",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult confirmYingDetail(Integer id){
        hkEventService.updateByIdRiskLevel(id);
        List<HkEventInfo> hkEventInfos =hkEventService.findEventByIdAll(id);
        EventDeailVo eventDeailVo = new EventDeailVo();
        eventDeailVo.setId(hkEventInfos.get(0).getId());
        eventDeailVo.setComponentId(hkEventInfos.get(0).getComponentId());
        eventDeailVo.setCreateTime(hkEventInfos.get(0).getCreateTime());
        eventDeailVo.setEventAdress(hkEventInfos.get(0).getEventAddress());
        eventDeailVo.setEventId(hkEventInfos.get(0).getEventId());
        eventDeailVo.setEventLon(hkEventInfos.get(0).getLongitude());
        eventDeailVo.setEventLat(hkEventInfos.get(0).getLatitude());
        eventDeailVo.setRiskLevelName("??????");
        eventDeailVo.setImage(hkEventInfos.get(0).getEventImage());
        eventDeailVo.setType(hkEventInfos.get(0).getEventTypeName());
        eventDeailVo.setEventTitle(hkEventInfos.get(0).getEventTitle());
        String event = JSON.toJSONString(eventDeailVo);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://331w72372t.zicp.vip/xianlin/eventCenter/eventPush");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.addHeader("Authorization", "Basic YWRtaW46");
        try {
            StringEntity ss = new StringEntity(event, "utf-8");
            ss.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                    "application/json"));
            httpPost.setEntity(ss);
            // ????????????
            HttpResponse httpResponse = client.execute(httpPost);
            // ?????????????????????
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
            StringBuilder strber = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                strber.append(line + "\n");
            inStream.close();
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                System.out.println("???????????????????????????????????????");
            } else {
                System.out.println("?????????????????????");
            }
        } catch (Exception e) {
//                                    logger.error("???????????????"+e.getMessage());
            throw new RuntimeException(e);
        }
        return AjaxResult.success("??????");
    }

    /**
     * ????????????
     * @param id
     * @param IgnoreReason
     * @return
     */
    @RequestMapping(value = "ignoreDetail",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult ignoreDetail(Integer id,String IgnoreReason){
        HkEventInfo hkEventInfo = new HkEventInfo();
        hkEventInfo.setId(id);
        hkEventInfo.setEventRemarks(IgnoreReason);
        hkEventInfo.setUpdateTime(new Date());
        SysUser user = ShiroUtils.getSysUser();
        return toAjax(hkEventService.updateEventStatus(hkEventInfo,user));
    }

    /**
     * ????????????
     * @param id
     * @param closeReason
     * @return
     */
    @RequestMapping(value = "closeDetail",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult closeDetail(Integer id,String closeReason){
        HkEventInfo hkEventInfo = new HkEventInfo();
        hkEventInfo.setId(id);
        hkEventInfo.setEventRemarks(closeReason);
        hkEventInfo.setUpdateTime(new Date());
        SysUser user = ShiroUtils.getSysUser();
        return toAjax(hkEventService.closeEventStatus(hkEventInfo,user));
    }

    /**
     * ????????????
     * @param searchInfo
     * @param
     * @return
     */
    @GetMapping("searchVague")
    @ResponseBody
    public Map searchVague(EventSearchVO searchInfo){
        //??????????????????startPage ??????????????????
        /**????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????**/
        Long deptId = ShiroUtils.getSysUser().getDeptId();
        SysDept sysDept = iSysDeptService.selectDeptById(deptId);
        if(sysDept.getLevel() > 1){
            searchInfo.setAssignDeptId(deptId);
        }
        Map<String,Object> data = new HashMap<>();
        /**?????????**/
        int  pendingCount = statisticsService.findPendingVehicleCountByParams(searchInfo);
        data.put("pendingCount",pendingCount);
        /**????????????**/
        searchInfo.setStatusArr(new String[]{"3","4","5","6"});
        int achieveCount = statisticsService.findCountByParam(searchInfo);
        data.put("achieveCount",achieveCount);
        /**????????????**/
        searchInfo.setStatusArr(new String[]{"7"});
        int ignoreCount = statisticsService.findCountByParam(searchInfo);
        data.put("ignoreCount",ignoreCount);
        /**????????????**/
        searchInfo.setStatusArr(new String[]{"8"});
        int abnormalCount = statisticsService.findCountByParam(searchInfo);
        data.put("abnormalCount",abnormalCount);

        startPage();

        List<HkEventInfo> eventList = hkEventService.searchEventInfo(searchInfo);
        for (HkEventInfo event : eventList){
            if(!StringUtils.isEmpty(event.getExtendStr3())){
                int count = hkEventService.queryEventCountRepeat(event.getExtendStr3());
                event.setCount(count);
            }else{
                event.setCount(0);
            }
            Date createTime = event.getCreateTime();
            event.setDate(DateUtils.subStrDateStringRetainOne(DateUtil.formatBetween(new Date(),createTime))+"???");
        }
        TableDataInfo tableDataInfo = getDataTable(eventList);//data
        data.put("code",0);//code
        data.put("rows",tableDataInfo.getRows());
        data.put("total",tableDataInfo.getTotal());
        return data;
    };

    /**
     * ????????????
     * @param vo
     * @return
     */
    @PostMapping("addEventLm")
    @ResponseBody
    public AjaxResult addEventLm(@RequestBody HkEventLmanageVo vo){
        if(StringUtils.isNull(vo.getEventId())){
            return AjaxResult.error("??????ID????????????");
        }
        if(StringUtils.isNull(vo.getLmList())){
            return AjaxResult.error("????????????????????????");
        }
        HkEventInfo eventDetail = hkEventService.queryEventDetail(Integer.parseInt(vo.getEventId()+""));
        if(eventDetail.getEventAlertStatus() == 6 && !eventDetail.getRiskLevel().equals(vo.getRiskLevel())){
            return AjaxResult.error("?????????????????????????????????????????????");
        }
        vo.setCreateUser(ShiroUtils.getUserId()+"");
        return hkEventService.addLmanagementByEvent(vo);
    }

    /**
     * ??????????????????
     * @param id
     * @return
     */
    @GetMapping("queryEventLm")
    @ResponseBody
    public AjaxResult queryEventLm(Long id){
        Map<String,Object> result = new HashMap<>();
        List<HkLmanagementInfo> seeLm = LmanagementInfoService.queryLmByEvent(id);
        List<HkLmanagementInfo> allLm = LmanagementInfoService.selectHkLmanagementInfoList(new HkLmanagementInfo());
        HkEventInfo eventDetail = hkEventService.queryEventDetail(Integer.valueOf(id+""));
        result.put("lmList",seeLm);
        result.put("allLm",allLm);
        result.put("riskLevel",eventDetail.getRiskLevel());
        return AjaxResult.success(result);
    }

    /**
     * ????????????
     * @param regionName
     * @param longitude
     * @param latitude
     * @param eventType
     * @return
     */
    @GetMapping("eventTypeHistory")
    @ResponseBody
    public AjaxResult hkEventDetails(String regionName,String longitude,String latitude,String eventType) {
        if(StringUtils.isEmpty(eventType)){
            return AjaxResult.error("????????????????????????");
        }
        HkEventInfo eventInfo = new HkEventInfo();
        eventInfo.setRegionName(regionName);
        eventInfo.setLongitude(longitude);
        eventInfo.setLatitude(latitude);
        eventInfo.setEventType(eventType);
        List<HkEventInfo> list = hkEventService.findEventHistory(eventInfo);
        for(HkEventInfo dto : list){
            Date createTime = dto.getCreateTime();
            SimpleDateFormat yyyyMMdd = new SimpleDateFormat("MM???dd??? HH:mm:ss");
            String date = yyyyMMdd.format(createTime);
            dto.setDate(date);
        }
        return AjaxResult.success(list);
    }

    /**
     * ????????????
     */
    @PostMapping("eventAssign")
    @ResponseBody
    public AjaxResult eventAssign(EventAssignVO vo){
        if(StringUtils.isEmpty(vo.getEventId())){
            return AjaxResult.error("??????ID????????????");
        }
        if(StringUtils.isEmpty(vo.getDeptIds())){
            return AjaxResult.error("????????????????????????");
        }
        if(StringUtils.isEmpty(vo.getAssignReason())){
            return AjaxResult.error("????????????????????????");
        }
        Long userId = ShiroUtils.getUserId();
        vo.setAssignOperator(userId);
        HkEventInfo hkEvent = hkEventService.findEventByEventId(vo.getEventId());
        if(hkEvent == null){
            return AjaxResult.error("???????????????");
        }
        int result = hkEventService.updateAssignInfo(vo);
        if(result == 1){
            return AjaxResult.success("????????????");
        }
        return AjaxResult.error("????????????");
    }

    @GetMapping("getVideoUrl")
    @ResponseBody
    public AjaxResult getVideoUrl(String eventId){
        if(!StringUtils.isEmpty(eventId)){

        }

        return null;
    }

    @GetMapping("getHKEventAccounted")
    @ResponseBody
    public AjaxResult getHKeventCount(String startTime, String endTime,@RequestParam(value = "createIndexCode",required=false)
            List<String> createIndexCode,@RequestParam(value = "eventType",required=false) List<String> eventType){
        /*Map<String, Object> hkEventCount = hkEventService.findHKEventCount();
        hkEventCount.put("disposal",hkEventCount.get("disposal").toString());
        hkEventCount.put("complete",hkEventCount.get("complete").toString());*/
        Map<String,Object> thuths = new HashMap<>();
        Map<String,Object> params = new HashMap<>();
        params.put("startTime",startTime);
        params.put("endTime",endTime);
        /*String [] arr=new String[createIndexCode.size()];
        String [] eventTypeAll=new String[eventType.size()];*/
        String cameraIndexCode = null;
        String eventTypeAll=null;
        if(cameraIndexCode!=null){
            for(int i=0;i<createIndexCode.size();i++){
                String createIndexCode1 = createIndexCode.get(i);
                if(i==0){
                    cameraIndexCode=createIndexCode1;
                }else{
                    cameraIndexCode=cameraIndexCode+","+createIndexCode1;
                }

            }
        }
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
       /* String [] arr=new String[]{cameraIndexCode};
        String [] eventTypeInfo=new String[]{eventTypeAll};*/
        if(cameraIndexCode!=null){
            String[] split = cameraIndexCode.split(",");
            params.put("cameraIndexCode",split);

        }
        if(eventTypeAll!=null){
            String[] split1 = eventTypeAll.split(",");
            params.put("eventTypeAll",split1);

        }
        Integer count= hkEventService.queryEventCountByList(params);
        params.put("statusArr",new String[]{"2"});
        Integer weiCount= hkEventService.queryEventCountByList(params);
        params.put("statusArr",new String[]{"3","5"});
        Integer chuCount= hkEventService.queryEventCountByList(params);
        params.put("statusArr",new String[]{"6","7","9"});
        Integer endCount= hkEventService.queryEventCountByList(params);
        thuths.put("countAll",count);
        thuths.put("weiCount",weiCount);
        thuths.put("chuCount",chuCount);
        thuths.put("endCount",endCount);
        return AjaxResult.success(thuths);
    }

    /**
     * ???????????????
     * @return
     */
    @GetMapping("getIntervalCount")
    @ResponseBody
    public AjaxResult getIntervalCount(){
        return AjaxResult.success(hkEventService.getIntervalCount());
    }

    /**
     * ???????????????????????????
     * @return
     */
    @GetMapping("getTodayEventCount")
    @ResponseBody
    public AjaxResult getTodayEventCount(){return AjaxResult.success(hkEventService.getTodayEventCount());}

    /**
     * ????????????
     */
    @GetMapping("getKeyViolations")
    @ResponseBody
    public AjaxResult getKeyViolations(){
        return AjaxResult.success(hkEventService.getKeyViolations());
    }

    /**
     * ????????????
     * @return
     */
    @GetMapping("getTodayHandleProp")
    @ResponseBody
    public AjaxResult getTodayHandleProp(){
        return AjaxResult.success(hkEventService.getTodayHandleProp());
    }
}
