package com.ruoyi.web.controller.thirdParty.unifiedAddress;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.platform.unifiedAdress.GatewayAddress;
import com.ruoyi.system.domain.platform.unifiedAdress.GatewayAddressSearchReq;
import com.ruoyi.system.domain.platform.unifiedAdress.MetaDataUnifyAddress;
import com.ruoyi.system.domain.platform.unifiedAdress.tongAdress.ZheAddrList;
import com.ruoyi.system.domain.platform.unifiedAdress.tongAdress.ZheParams;
import com.ruoyi.system.domain.platform.zheLiFang.GatewayUnifyAddress;
import com.ruoyi.system.domain.unifyAdress.XlUnifyAddressInfo;
import com.ruoyi.system.service.unifyAdress.IXlUnifyAddressInfoService;
import com.ruoyi.web.controller.common.utils.RedisTemplateKey;
import com.ruoyi.web.controller.thirdParty.unifiedAddress.entity.GatewayAddressTokenReq;
import com.ruoyi.web.controller.thirdParty.unifiedAddress.util.BaseDataPushOut;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author FanKaibiao
 * @date 2021-05-26-11:10
 */
@RequestMapping("/hkapi")
@Controller
public class UnifiedController {

    @Value("${address.user}")
    private  String user;
    @Value("${address.pwd}")
    private  String pwd;

    @Value("${gateway.user}")
    private  String gatewayUser;
    @Value("${gateway.pwd}")
    private  String gatewayPwd;

    @Value("${gateway.ak}")
    private String ak;

    @Value("${gateway.sk}")
    private String sk;

    @Value("${gateway.tokenUrl}")
    private String gatewayTokenUrl;

    @Value("${gateway.addressUrl}")
    private String gatewayAddressUrl;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IXlUnifyAddressInfoService addressInfoService;


    @RequestMapping("opsted/list")
    @ResponseBody
    public void getToken(){
        String tokenKey= RedisTemplateKey.TOKEN_KEY;
            GatewayAddressTokenReq addressTokenReq = new GatewayAddressTokenReq();
            addressTokenReq.setPwd("123");
            addressTokenReq.setUser("xljdjsc");
            Map<String, Object> paramBody = addressTokenReq.getUrlBody();
//        String jsonStr = JSONUtil.toJsonStr(paramBody);
//        Map<String, Object> paramMap2 = new HashMap<>();
            String response = RequestGateway.request(gatewayTokenUrl, ak, sk, paramBody);
            System.out.println(response);
            JSONObject jsonObject = JSON.parseObject(response);

            String data = jsonObject.getString("data");
            if (StringUtils.isBlank(data)) {
                throw new RuntimeException("token??????????????????");
            }
            JSONObject jsonData = JSON.parseObject(data);
            System.out.println(JSON.toJSONString(jsonObject));
            String token = jsonData.getJSONObject("data").getString("token");
            System.out.println(token);
            ValueOperations<String,String> operations = redisTemplate.opsForValue();
            operations.set(tokenKey,token);
            redisTemplate.expire(tokenKey,2, TimeUnit.HOURS);
    }

    @RequestMapping("opsted/listAll")
    @ResponseBody
    public List<MetaDataUnifyAddress> searchAddr(GatewayAddressSearchReq param) {
        String tokenKey = RedisTemplateKey.TOKEN_KEY;
        String token=getEventMaps(tokenKey);
        if(token == null){
            getToken();
            String tokenKey1 = RedisTemplateKey.TOKEN_KEY;
            String tokens=getEventMaps(tokenKey1);
            param.setToken(tokens);
        }else if(token!=null){
            param.setToken(token);
        }
        param.setFuzzy(false);
        //????????????????????????
        if(StringUtils.isBlank(param.getAddr())){
            param.setAddr("????????????");
        }
        param.setPage(2);
        param.setLimit(10000);
        Map<String, Object> urlBody = param.getUrlBody();
        String result = RequestGateway.request(gatewayAddressUrl, ak, sk, urlBody);

        GatewayAddress addr = JSONObject.parseObject(result, GatewayAddress.class);
        System.out.println(addr);
        // ????????????
        if (!"200".equals(addr.getCode())) {
            throw new RuntimeException("???????????????????????????");
        }
        String data = addr.getData();
//      log.info("?????????????????????????????????{}",data);
        ZheParams address = JSONObject.parseObject(data, ZheParams.class);
        System.out.println(address);
        // ??????????????????????????????
        int total = address.getData().getCount();
        // ????????????????????????
        int num = 1000;
        int pageStart = 0;
        int one = 1;

        if (total % num == pageStart) {
            pageStart = total / num;
        } else {
            pageStart = one + total / num;
        }
        GatewayAddressSearchReq req = new GatewayAddressSearchReq();
        req.setToken(token);
        req.setFuzzy(false);
        if(StringUtils.isBlank(param.getAddr())){
            req.setAddr("????????????");
        }else {
            req.setAddr(param.getAddr());
        }
        req.setLimit(num);
        List<GatewayUnifyAddress.DataVo.AddrList> metaDataList = new ArrayList<>();
        for(int i = 0; i < pageStart; i++){
            Map<String, Object> urlBody2 = req.getUrlBody();
            String result2 = RequestGateway.request(gatewayAddressUrl, ak, sk, urlBody2);
            GatewayAddress addr2 = JSONObject.parseObject(result2, GatewayAddress.class);
            if (!"200".equals(addr2.getCode())) {
                throw new RuntimeException("???????????????????????????");
            }
            String data2 = addr2.getData();
            GatewayUnifyAddress address2 = JSONObject.parseObject(data, GatewayUnifyAddress.class);
            address2.getData().getAddrList().stream().forEach(item -> {
                if(!CollectionUtils.isEmpty(item.getLoc().getCoordinates()) && item.getLoc().getCoordinates().size() == 2){
                    item.setLng(item.getLoc().getCoordinates().get(0));
                    item.setLat(item.getLoc().getCoordinates().get(1));
                }
                metaDataList.add(item);
            });
        }
//        if(total >=10000){
            /*for(int i=0;i<999;i++){
                String tokenKey2 = RedisTemplateKey.TOKEN_KEY;
                String token2=getEventMaps(tokenKey2);
                if(token2 == null){
                    getToken();
                    String tokenKey1 = RedisTemplateKey.TOKEN_KEY;
                    String tokens=getEventMaps(tokenKey1);
                    param.setToken(tokens);
                }else if(token2!=null){
                    param.setToken(token2);
                }
                param.setFuzzy(false);
                //????????????????????????
                if(StringUtils.isBlank(param.getAddr())){
                    param.setAddr("????????????");
                }
                param.setPage(i+1);
                param.setLimit(1000);
                Map<String, Object> urlBody1 = param.getUrlBody();
                String result1 = RequestGateway.request(gatewayAddressUrl, ak, sk, urlBody1);

                GatewayAddress addr1 = JSONObject.parseObject(result1, GatewayAddress.class);
                // ????????????
                if (!"200".equals(addr1.getCode())) {
                    throw new RuntimeException("???????????????????????????");
                }
                String data1 = addr1.getData();
//              log.info("?????????????????????????????????{}",data);
                ZheParams tongAdress = JSONObject.parseObject(data1, ZheParams.class);
                getUnifyAdress(tongAdress);
            }*/
//        }
        // ????????????????????????
        /*int num = 1000;
        int pageStart = 0;
        int one = 1;

        if (total % num == pageStart) {
            pageStart = total / num;
        } else {
            pageStart = one + total / num;
        }
//      log.info("?????????{}???????????????,??????{}???", total, pageStart);
        BaseDataPushOut<List<MetaDataUnifyAddress>> res = new BaseDataPushOut<List<MetaDataUnifyAddress>>();
        *//*res.setPushType(PushTypeEnum.????????????.getValue());
        res.setPushTypeCode(MetaDataTypeEnum.???????????????.getValue());*//*
        res.setRemark("?????????????????????");
        res.setHappenTime(new Date());
        res.setNum(total);

        GatewayAddressSearchReq req = new GatewayAddressSearchReq();
        req.setToken(token);
        req.setFuzzy(false);
        if(StringUtils.isBlank(param.getAddr())){
            req.setAddr("???");
        }else {
            req.setAddr(param.getAddr());
        }
        req.setLimit(num);
        List<ZheAddrList> metaDataList = new ArrayList<>();
        for (int i = 0; i < pageStart; i++) {
            req.setPage(i);
            Map<String, Object> urlBody2 = req.getUrlBody();
            String result2 = RequestGateway.request(gatewayAddressUrl, ak, sk, urlBody2);
            GatewayAddress addr2 = JSONObject.parseObject(result2, GatewayAddress.class);
            // ????????????
            if (!"200".equals(addr2.getCode())) {
                throw new RuntimeException("???????????????????????????");
            }
            String data2 = addr2.getData();
//            log.info("?????????????????????????????????{}",data);
            ZheParams address2 = JSONObject.parseObject(data2, ZheParams.class);

            if (0 != address2.getCode()) {
//                log.info("???????????????????????????");
                continue;
            }

            address2.getData().getAddrList().stream().forEach(item -> {
                if(!CollectionUtils.isEmpty(item.getLoc().getCoordinates()) && item.getLoc().getCoordinates().size() == 2){
                    item.setLon(item.getLoc().getCoordinates().get(0));
                    item.setLat(item.getLoc().getCoordinates().get(1));
                }
                metaDataList.add(item);
            });
        }

        List<MetaDataUnifyAddress> dataUnifyAddressList= BeanCopyUtil.mapList(metaDataList,MetaDataUnifyAddress.class);
        res.setData(dataUnifyAddressList);

        ReqComponentUtil.deal(componentConfig.getConf(),res);*/

        return null;
    }

    private AjaxResult getUnifyAdress(ZheParams tongAdress) {
        XlUnifyAddressInfo xlUnifyAddressInfo = new XlUnifyAddressInfo();
        for(int i=0;i<tongAdress.getData().getAddrList().size();i++){
            xlUnifyAddressInfo.setUnifyCode(tongAdress.getData().getAddrList().get(i).getCode());//??????????????????
            xlUnifyAddressInfo.setUnifyAddr(tongAdress.getData().getAddrList().get(i).getAddr());//????????????
            xlUnifyAddressInfo.setUnifyLv(tongAdress.getData().getAddrList().get(i).getLv());//????????????
            xlUnifyAddressInfo.setUnifyScore(tongAdress.getData().getAddrList().get(i).getScore());//??????????????????
            xlUnifyAddressInfo.setUnifyLon(tongAdress.getData().getAddrList().get(i).getLon());//??????
            xlUnifyAddressInfo.setUnifyLat(tongAdress.getData().getAddrList().get(i).getLat());//??????
//            xlUnifyAddressInfo.setUnifyLoc(tongAdress.getData().getAddrList().get(i).getLoc());
            xlUnifyAddressInfo.setUnifyCity(tongAdress.getData().getAddrList().get(i).getCity());//???
            xlUnifyAddressInfo.setUnifyCounty(tongAdress.getData().getAddrList().get(i).getCounty());//???
            xlUnifyAddressInfo.setUnifyTown(tongAdress.getData().getAddrList().get(i).getTown());//??????
            xlUnifyAddressInfo.setUnifyCommunity(tongAdress.getData().getAddrList().get(i).getCommunity());//??????
            xlUnifyAddressInfo.setUnifySquad(tongAdress.getData().getAddrList().get(i).getSquad());//?????????
            xlUnifyAddressInfo.setUnifyVillage(tongAdress.getData().getAddrList().get(i).getVillage());//?????????
            xlUnifyAddressInfo.setUnifySzone(tongAdress.getData().getAddrList().get(i).getSzone());//?????????
            xlUnifyAddressInfo.setUnifyStreet(tongAdress.getData().getAddrList().get(i).getStreet());//?????????
            xlUnifyAddressInfo.setUnifyDoor(tongAdress.getData().getAddrList().get(i).getDoor());//??????
            xlUnifyAddressInfo.setUnifyResregion(tongAdress.getData().getAddrList().get(i).getResregion());//??????
            xlUnifyAddressInfo.setUnifyBuilding(tongAdress.getData().getAddrList().get(i).getBuilding());//?????????
            xlUnifyAddressInfo.setUnifyBuildingNum(tongAdress.getData().getAddrList().get(i).getBuilding_num());//??????
            xlUnifyAddressInfo.setUnifyUnit(tongAdress.getData().getAddrList().get(i).getUnit());//??????
            xlUnifyAddressInfo.setUnifyFloor(tongAdress.getData().getAddrList().get(i).getFloor());//??????
            xlUnifyAddressInfo.setUnifyRoom(tongAdress.getData().getAddrList().get(i).getRoom());//??????
            xlUnifyAddressInfo.setRoomFloor(tongAdress.getData().getAddrList().get(i).getRoom_floor());//??????????????????
            xlUnifyAddressInfo.setCreateTime(new Date());
            addressInfoService.insertXlUnifyAddressInfo(xlUnifyAddressInfo);
        }


        return AjaxResult.success();
    }

    private String getEventMaps(String thingKey){
        ValueOperations<String,String> operations = redisTemplate.opsForValue();
        return operations.get(thingKey);
    }
}
