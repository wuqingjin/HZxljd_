package com.ruoyi.system.service.impl.HkEarlyWarning;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.ActProcess.ActHiTaskinst;
import com.ruoyi.system.domain.ActProcess.ActRuTask;
import com.ruoyi.system.domain.Action.HkActionProcess;
import com.ruoyi.system.domain.HKrecord;
import com.ruoyi.system.domain.HkEarlyWarning.HkEventInfo;
import com.ruoyi.system.domain.HkEarlyWarning.HkEventInfoRecord;
import com.ruoyi.system.domain.HkEntity.EventAfreshAssignVo;
import com.ruoyi.system.domain.HkEntity.EventSearchVO;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.mapper.ding.DingEventMapper;
import com.ruoyi.system.service.HkEarlyWarning.HkEventRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author FanKaibiao
 * @date 2020-11-05-15:46
 */
@Service
public class HkEventRecordServiceImpl implements HkEventRecordService {

    @Autowired
    private HkEventRecordMapper hkEventRecordMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private ActHiTaskinstMapper actHiTaskinstMapper;

    @Autowired
    private ActRutaskMapper actRutaskMapper;

    @Autowired
    private appMyWorkMapper appMyWorkMapper;

    @Autowired
    private appEventDetalisMapper appEventDetalisMapper;

    @Autowired
    private DingEventMapper dingEventMapper;

    @Override
    public void insertEventRecord(HkEventInfoRecord hkEventInfoRecord) {
        hkEventRecordMapper.insertEventRecord(hkEventInfoRecord);
    }

    @Override
    public List<HkEventInfo> eventRecordList(HkEventInfo hkEventInfo) {
        return hkEventRecordMapper.eventRecordList(hkEventInfo);
    }

    @Override
    public HkEventInfo recordDetailById(Integer id) {
        return hkEventRecordMapper.recordDetailById(id);
    }

    @Override
    public List<HkEventInfo> findRecordOne() {
        return hkEventRecordMapper.findRecordOne();
    }

    @Override
    public List<HkEventInfo> findSearchVague(EventSearchVO searchInfo) {
        return hkEventRecordMapper.findSearchVague(searchInfo);
    }

    @Override
    public Integer findCountByParam(EventSearchVO searchInfo) {
        return hkEventRecordMapper.findCountByParam(searchInfo);
    }

    @Override
    public void updateActHiTaskinst(ActHiTaskinst actHiTaskinst) {
        actHiTaskinstMapper.updateActHiTaskinst(actHiTaskinst);
    }

    @Override
    public void updateActRuTask(ActRuTask actRuTask) {
        actRutaskMapper.updateActRuTask(actRuTask);
    }

    @Override
    public List<HkEventInfo> findSignName(HkEventInfo hkEventInfo) {
        return hkEventRecordMapper.findSignName(hkEventInfo);
    }

    @Override
    public int updateEventRemarks(HkEventInfo hkEventInfo) {
        return hkEventRecordMapper.updateEventRemarks(hkEventInfo);
    }

    @Override
    public void updateEventAlertStatus(HkEventInfo hkEventInfo) {
        hkEventRecordMapper.updateEventAlertStatus(hkEventInfo);
    }

    /**
     * ???????????????
     * @param vo
     */
    @Override
    @Transactional
    public void afreshAssign(EventAfreshAssignVo vo) {
        HkActionProcess hkActionProcess = new HkActionProcess();
        hkActionProcess.setEventId(vo.getEventId());
        hkActionProcess.setIsComplete(0);
        hkActionProcess.setHandlerOrder(vo.getHandlerOrder()+"");
        hkActionProcess.setHandlerUserId(vo.getAfterHandlerUserId());
        appMyWorkMapper.updateHkActionProcess(hkActionProcess);
        HkEventInfo hkEventInfo = new HkEventInfo();
        hkEventInfo.setEventId(vo.getEventId());
        hkEventRecordMapper.updateEventAlertStatus(hkEventInfo);
        HKrecord hKrecord = new HKrecord();
        // ??????????????????
        hKrecord.setRecordId(UUID.randomUUID().toString().trim().replaceAll("-", ""));
        hKrecord.setHandlerIndexCode(vo.getAfterHandlerUserId());
        SysUser user1 = userMapper.selectUserById(Long.parseLong(vo.getAfterHandlerUserId()));
        hKrecord.setHandlerName(user1.getUserName());
        hKrecord.setEventId(vo.getEventId());
        hKrecord.setProcessStatus("1");
        hKrecord.setExtendStr3(vo.getOperator());
        hKrecord.setExtendInt3(vo.getHandlerOrder());
        appEventDetalisMapper.addHkrecond(hKrecord);
    }

    @Override
    public HkEventInfo findEventId(String eventId) {
        return hkEventRecordMapper.findEventId(eventId);
    }

    /**
     * ????????????
     * @param eventId
     * @param closeReason
     * @param sysUser
     * @param handlerOrder
     * @return
     */
    @Override
    @Transactional
    public AjaxResult machineHandling(String eventId, String closeReason, SysUser sysUser,Integer handlerOrder) {
        HKrecord hKrecord = new HKrecord();// ??????????????????
        hKrecord.setRecordId(UUID.randomUUID().toString().trim().replaceAll("-", ""));
        hKrecord.setHandlerIndexCode(sysUser.getUserId().toString());
        hKrecord.setHandlerName("");
        hKrecord.setHandlerContent(closeReason);
        hKrecord.setEventId(eventId);
        hKrecord.setProcessStatus("6");//????????????
        hKrecord.setExtendStr3(sysUser.getUserName());
        hKrecord.setExtendInt3(handlerOrder);
        appEventDetalisMapper.addHkrecond(hKrecord);
        HkEventInfo hkEventInfo = new HkEventInfo();
        hkEventInfo.setUpdateUser(sysUser.getUserId()+"");
        hkEventInfo.setEventId(eventId);
        hkEventInfo.setEventRemarks("");
        hkEventInfo.setCurrentProcessorId("");
        hkEventInfo.setUpdateTime(new Date());
        hkEventRecordMapper.updateEventRemarks(hkEventInfo);
        Map<String,Object> param = new HashMap<>();
        param.put("eventId",eventId);
        param.put("isComplete","1");
        param.put("otherUserId",sysUser.getUserId()+"");//???????????????
        param.put("returnFlagArr",new String[]{"0","1","2"});//??????
        param.put("afterReturnFlag",3);//??????????????????
        dingEventMapper.updateHkActionByParam(param);//??????isComplete??????1????????????????????????????????????
        return AjaxResult.success("??????????????????");
    }

    @Override
    public void updateEventInfo(HkEventInfo hkEventInfo) {
        hkEventRecordMapper.updateEventInfo(hkEventInfo);
    }


}
