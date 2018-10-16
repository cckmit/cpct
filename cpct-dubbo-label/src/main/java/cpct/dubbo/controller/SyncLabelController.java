package cpct.dubbo.controller;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import cpct.dubbo.model.LabModel;
import cpct.dubbo.model.RecordModel;
import cpct.dubbo.service.SyncLabelService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/label")
public class SyncLabelController {
    @Autowired
    private SyncLabelService syncLabelService;
    @Autowired
    private InjectionLabelMapper labelMapper;

    @RequestMapping(value = "syncLabel", method = RequestMethod.POST)
    @CrossOrigin
    public String syncLabel() {
        Map result = new HashMap();
        LabModel labModel = new LabModel();
        RecordModel record = new RecordModel();
        try{
//            InputStream inputStream = multipartFile.getInputStream();
//            XSSFWorkbook wb = new XSSFWorkbook(inputStream);
//            Sheet sheet = wb.getSheetAt(0);
//            Integer rowNums = sheet.getLastRowNum() + 1;
//            for (int i = 1; i < rowNums - 1; i++) {
//                Row rowFirst = sheet.getRow(0);
//                Row row = sheet.getRow(i);
//                Cell cell = row.getCell(0);
//            }
            labModel.setLabRowId(Long.valueOf(1));//标签id
            labModel.setLabName("资产唯一编号");//标签名称
            labModel.setLabEngName("ASSET_ROW_ID");//英文名称
            labModel.setLabCode("83310A02001001001998");//标签编码
            labModel.setLabManageType("B");//标签管理类型(A集团/B省份/C地市（可扩展）)
            labModel.setLabType("2");//标签类型(文本、数值、枚举)
            labModel.setLabRelevantFlag("20");//标签置信度
            labModel.setLabMissRate("100");//标签缺失率
            labModel.setLabObjectCode("10");//标签对象编码
            labModel.setLabObject("用户级");//标签对象（用户级，客户级，销售品级，区域级，填写标签对应归属对象）
            labModel.setLabLevel1("A");//一级分类编码
            labModel.setLabLevel1Name("基础属性");//一级分类编码名称
            labModel.setLabLevel2("02");//二级分类编码
            labModel.setLabLevel2Name("电信传统业务");//二级分类编码名称
            labModel.setLabLevel3("001");//三级分类编码
            labModel.setLabLevel3Name("用户信息");//三级分类编码名称
            labModel.setLabLevel4("001");//四级分类编码
            labModel.setLabLevel4Name("移动业务");//四级分类编码名称
            labModel.setLabLevel5("001");//五级分类编码
            labModel.setLabLevel5Name("用户ID");//五级分类编码名称
            labModel.setLabLevel6("998");//六级分类编码
            labModel.setLabLevel6Name("资产唯一编码");//六级分类编码名称
            labModel.setLabUpdateFeq("2");//更新频率（1:实时;2:日;3:周4:月）
            labModel.setLabBusiDesc("资产唯一编号");//业务口径
            labModel.setLabTechDesc("");//技术口径
            labModel.setLabState("3");//上线：3；下线：5
            record.setLabel(labModel);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return JSON.toJSON(result).toString();
    }




}
