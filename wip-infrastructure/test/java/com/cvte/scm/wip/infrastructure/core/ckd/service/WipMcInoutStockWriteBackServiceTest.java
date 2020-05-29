package com.cvte.scm.wip.infrastructure.core.ckd.service;

import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.enums.BooleanEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskDeliveringStockView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskInfoView;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcInoutStockEntity;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcInoutStockLineEntity;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskDeliveryStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskLineStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.enums.McTaskStatusEnum;
import com.cvte.scm.wip.domain.core.ckd.enums.TransactionTypeNameEnum;
import com.cvte.scm.wip.domain.core.ckd.hook.WriteBackHook;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcInoutStockLineService;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcInoutStockService;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcInoutStockWriteBackService;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.dto.EbsInoutStockVO;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.enums.EbsDeliveryStatusEnum;
import com.cvte.scm.wip.domain.core.thirdpart.ebs.service.EbsInvokeService;
import com.cvte.scm.wip.infrastructure.core.ckd.BaseMcTaskTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

public class WipMcInoutStockWriteBackServiceTest extends BaseMcTaskTest {


    @Mock
    private EbsInvokeService ebsInvokeService;

    @InjectMocks
    @Autowired
    private WipMcInoutStockWriteBackService writeBackService;

    @Autowired
    private WipMcInoutStockService wipMcInoutStockService;

    @Autowired
    private WipMcInoutStockLineService wipMcInoutStockLineService;

    private String deliveryOutHeaderId = "deliveryOutHeaderId";

    private String deliveryOutHeaderNo = "deliveryOutHeaderNo";

    private String inoutStockId = "inoutStockId";


    @Before
    public void setUp() {
        doReturn(mockEbsInoutStockVOS(new ArrayList<>(), new ArrayList<>())).when(ebsInvokeService).listEbsInoutStockView(any());

        ReflectionTestUtils.setField(writeBackService, "ebsInvokeService", ebsInvokeService);
        ReflectionTestUtils.setField(wipMcTaskService, "ebsInvokeService", ebsInvokeService);

    }
    @Override
    protected String getTaskStatus() {
        return McTaskStatusEnum.CONFIRM.getCode();
    }



    /** 所有调拨行都已过账时，头状态应为完成 **/
    @Test
    public void testAllPosted() {
        InoutStockTaskContext.clear();

        List<String> inoutLineIds = Arrays.asList("inoutLine1", "inoutLine2");
        doReturn(mockEbsInoutStockVOS(inoutLineIds, Arrays.asList(EbsDeliveryStatusEnum.POSTED.getCode(), EbsDeliveryStatusEnum.POSTED.getCode()))).when(ebsInvokeService).listEbsInoutStockView(any());
        ReflectionTestUtils.setField(writeBackService, "ebsInvokeService", ebsInvokeService);


        initInoutStock(inoutLineIds);

        writeBackService.writeBackInoutStock(new InoutStockTestHook(2));

        assertMcStatusEquals(mcTaskId, McTaskStatusEnum.FINISH);

    }


    /** 所有调拨行都为取消时，头状态应为关闭 **/
    @Test
    public void testAllCancel() {
        InoutStockTaskContext.clear();

        List<String> inoutLineIds = Arrays.asList("inoutLine1", "inoutLine2");
        doReturn(mockEbsInoutStockVOS(inoutLineIds, Arrays.asList(EbsDeliveryStatusEnum.CANCELLED.getCode(), EbsDeliveryStatusEnum.CANCELLED.getCode()))).when(ebsInvokeService).listEbsInoutStockView(any());
        ReflectionTestUtils.setField(writeBackService, "ebsInvokeService", ebsInvokeService);


        initInoutStock(inoutLineIds);

        writeBackService.writeBackInoutStock(new InoutStockTestHook(2));

        assertMcStatusEquals(mcTaskId, McTaskStatusEnum.CLOSE);

    }


    /** 调拨行都为已过账/取消时，头状态应为工厂已确认 **/
    @Test
    public void testAllPostedOrCancel() {
        InoutStockTaskContext.clear();

        List<String> inoutLineIds = Arrays.asList("inoutLine1", "inoutLine2");
        doReturn(mockEbsInoutStockVOS(inoutLineIds, Arrays.asList(EbsDeliveryStatusEnum.POSTED.getCode(), EbsDeliveryStatusEnum.CANCELLED.getCode()))).when(ebsInvokeService).listEbsInoutStockView(any());
        ReflectionTestUtils.setField(writeBackService, "ebsInvokeService", ebsInvokeService);


        initInoutStock(inoutLineIds);


        writeBackService.writeBackInoutStock(new InoutStockTestHook(2));

        assertMcStatusEquals(mcTaskId, McTaskStatusEnum.CONFIRM);
    }



    /** 调拨行都为已过账/取消时，头状态应为工厂确认（部分配料任务行未未创建调拨单) **/
    @Test
    public void testPartPostedOrCancel() {

        InoutStockTaskContext.clear();

        List<String> inoutLineIds = Arrays.asList("inoutLine1", "inoutLine2");
        doReturn(mockEbsInoutStockVOS(inoutLineIds, Arrays.asList(EbsDeliveryStatusEnum.POSTED.getCode(), EbsDeliveryStatusEnum.CANCELLED.getCode()))).when(ebsInvokeService).listEbsInoutStockView(any());
        ReflectionTestUtils.setField(writeBackService, "ebsInvokeService", ebsInvokeService);

        initInoutStock(inoutLineIds);


        wipMcTaskLineService.insertList(buildInitLineList(mcTaskId, Arrays.asList(UUIDUtils.get32UUID(), UUIDUtils.get32UUID())));

        writeBackService.writeBackInoutStock(new InoutStockTestHook(4));

        assertMcStatusEquals(mcTaskId, McTaskStatusEnum.CONFIRM);
    }


    /** 调拨行都为已取消时，头状态应为工厂确认（部分配料任务行未未创建调拨单) **/
    @Test
    public void testPartCancel() {
        InoutStockTaskContext.clear();

        List<String> inoutLineIds = Arrays.asList("inoutLine1", "inoutLine2");
        doReturn(mockEbsInoutStockVOS(inoutLineIds, Arrays.asList(EbsDeliveryStatusEnum.CANCELLED.getCode(), EbsDeliveryStatusEnum.CANCELLED.getCode()))).when(ebsInvokeService).listEbsInoutStockView(any());
        ReflectionTestUtils.setField(writeBackService, "ebsInvokeService", ebsInvokeService);

        initInoutStock(inoutLineIds);

        wipMcTaskLineService.insertList(buildInitLineList(mcTaskId, Arrays.asList(UUIDUtils.get32UUID(), UUIDUtils.get32UUID())));
        writeBackService.writeBackInoutStock(new InoutStockTestHook(4));

        assertMcStatusEquals(mcTaskId, McTaskStatusEnum.CONFIRM);
    }


    private void assertMcStatusEquals(String mcTaskId, McTaskStatusEnum mcTaskStatusEnum) {
        McTaskInfoView mcTaskInfoView = wipMcTaskService.getMcTaskInfoView(mcTaskId);
        Assert.assertEquals(mcTaskStatusEnum.getCode(), mcTaskInfoView.getStatus());
    }


    private List<EbsInoutStockVO> mockEbsInoutStockVOS(List<String> inoutLineIds, List<String> status) {
        List<EbsInoutStockVO> ebsInoutStockVOS = new ArrayList<>();
        for (int i = 0; i < inoutLineIds.size(); i ++) {

            EbsInoutStockVO ebsInoutStockVO = new EbsInoutStockVO();
            ebsInoutStockVO.setTicketNo(deliveryOutHeaderNo)
                    .setHeaderId(deliveryOutHeaderId)
                    .setActualQty(10)
                    .setCancelledFlag(BooleanEnum.NO.getCode())
                    .setPostedFlag(BooleanEnum.YES.getCode())
                    .setHeaderId(mcTaskId)
                    .setOrigSysSourceId(inoutLineIds.get(i))
                    .setPlanQty(11)
                    .setStatus(status.get(i));
            ebsInoutStockVOS.add(ebsInoutStockVO);
            InoutStockTaskContext.inoutStockLineIds.add(inoutLineIds.get(i));
        }

        InoutStockTaskContext.init();
        return ebsInoutStockVOS;
    }

    private void initInoutStock(List<String> inoutStockLineIds) {


        WipMcInoutStockEntity wipMcInoutStock = new WipMcInoutStockEntity();
        wipMcInoutStock.setInoutStockId(inoutStockId)
                .setMcTaskId(mcTaskId)
                .setHeaderId(deliveryOutHeaderId)
                .setHeaderNo(deliveryOutHeaderNo)
                .setStatus(EbsDeliveryStatusEnum.ENTER.getCode())
                .setType(TransactionTypeNameEnum.OUT.name());
        EntityUtils.writeCurUserStdCrtInfoToEntity(wipMcInoutStock);
        wipMcInoutStockService.insert(wipMcInoutStock);

        List<WipMcInoutStockLineEntity> wipMcInoutStockLineEntities = new ArrayList<>();
        for (String inoutStockLineId : inoutStockLineIds) {
            WipMcInoutStockLineEntity wipMcInoutStockLine = new WipMcInoutStockLineEntity();
            wipMcInoutStockLine.setInoutStockLineId(inoutStockLineId)
                    .setInoutStockId(wipMcInoutStock.getInoutStockId())
                    .setLineId(InoutStockTaskContext.inoutStockLineIdMap.get(inoutStockLineId))
                    .setLineNo(UUIDUtils.get32UUID())
                    .setMcTaskLineId(InoutStockTaskContext.inoutStockLineAndLineIdMap.get(inoutStockLineId))
                    .setStatus(McTaskDeliveryStatusEnum.UN_POST.getCode())
                    .setQty(10)
                    .setPostQty(10);
            EntityUtils.writeCurUserStdCrtInfoToEntity(wipMcInoutStockLine);
            wipMcInoutStockLineEntities.add(wipMcInoutStockLine);
        }
        wipMcInoutStockLineService.insertList(wipMcInoutStockLineEntities);
    }

    private static class InoutStockTaskContext {

        /* 调拨行表ids */
        static List<String> inoutStockLineIds = new ArrayList<>();

        /* inoutStockLineId and DeliveryLineId map */
        static Map<String, String> inoutStockLineIdMap = new HashMap<>();

        /* inoutStockLineId and LineId */
        static Map<String, String> inoutStockLineAndLineIdMap = new HashMap<>();

        static void clear() {
            inoutStockLineIds = new ArrayList<>();
            inoutStockLineIdMap = new HashMap<>();
        }

        static void init() {
            int i = 0;
            for (String inoutStockLineId : inoutStockLineIds) {
                InoutStockTaskContext.inoutStockLineIdMap.put(inoutStockLineId, UUIDUtils.get32UUID());

                String mcTaskLineId = i < 2 ? lineIds.get(i) : UUIDUtils.get32UUID();
                InoutStockTaskContext.inoutStockLineAndLineIdMap.put(inoutStockLineId, mcTaskLineId);

                i ++;
            }

        }
    }


    private class InoutStockTestHook implements WriteBackHook {

        /* 配料任务行数, 用于模拟部分配料任务行没有调拨单的场景 */
        private int lineNum;

        InoutStockTestHook(int lineNum) {
            this.lineNum = lineNum;
        }

        @Override
        public List<McTaskDeliveringStockView> listMcTaskDeliveringStockView() {
            List<McTaskDeliveringStockView> views = new ArrayList<>();
            for (int i = 0; i < lineNum; i++) {
                String inoutStockLineId = null;
                String mcTaskLineId = UUIDUtils.get32UUID();
                if (i < InoutStockTaskContext.inoutStockLineIds.size()) {
                    inoutStockLineId = InoutStockTaskContext.inoutStockLineIds.get(i);
                    mcTaskLineId = InoutStockTaskContext.inoutStockLineAndLineIdMap.get(inoutStockLineId);
                }

                McTaskDeliveringStockView view = new McTaskDeliveringStockView();
                view.setMcTaskId(mcTaskId)
                        .setMcTaskLineId(mcTaskLineId)
                        .setLineStatus(McTaskLineStatusEnum.NORMAL.getCode())
                        .setDeliveryId(UUIDUtils.get32UUID())
                        .setDeliveryLineId(InoutStockTaskContext.inoutStockLineIdMap.get(inoutStockLineId))
                        .setDeliveryLineNo(UUIDUtils.get32UUID())
                        .setDeliveryNo(UUIDUtils.get32UUID())
                        .setInoutStockId(inoutStockId)
                        .setInoutStockLineId(inoutStockLineId);
                views.add(view);
            }
            return views;
        }

    }

}