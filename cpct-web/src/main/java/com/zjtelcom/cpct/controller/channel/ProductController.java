package com.zjtelcom.cpct.controller.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.ppm.dubbo.service.StrategySalesSearchService;
import com.ctzj.ppm.dubbo.service.StrategySalesViewService;
import com.ctzj.ppm.dubbo.vo.OfferVo;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.PpmProductMapper;
import com.zjtelcom.cpct.domain.channel.PpmProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@RestController
@RequestMapping("/ppm/product")
public class ProductController extends BaseController {

	@Autowired(required = false)
	private StrategySalesViewService strategySalesViewService;

	@Autowired(required = false)
	private StrategySalesSearchService strategySalesSearchService;
	@Autowired
	private PpmProductMapper ppmProductMapper;


	@GetMapping("/getPPMProduct")
	public Map<String, String> getPPMProduct(@RequestBody JSONObject request) {
		Map<String, String> param = new HashMap<String, String>();
		String offerName = request.getString("offerName");
		param.put("offerName", offerName);
		String offerNbr = request.getString("offerNbr");
		param.put("offerNbr", offerNbr);
		String page = request.getString("page");
		param.put("page", page);
		logger.info("get all PPM product start. req: " + JSON.toJSONString(param));
		Map<String, String> result = new HashMap<>(2);

		try {
			List<OfferVo> rs = strategySalesSearchService.salesSearch(param);
			logger.info("get PPM product salesSearch <> page=" + page + ", result.size=" + rs.size());
			result.put("resultCode",CODE_SUCCESS);
			result.put("resultMessage",CODE_SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("get PPM product error: ", e);
			result.put("resultCode",CODE_FAIL);
			result.put("resultMessage", CODE_FAIL);
		}
		return result;
	}

	@GetMapping("/updateAllPPMProduct")
	public JSONObject updateAllPPMProduct() {
		logger.info("updateAllPPMProduct controller start...");
		JSONObject result = new JSONObject();

		Map<String, String> param = new HashMap<String, String>();
		param.put("offerName", "");
		param.put("offerNbr", "");

		Long startTime = System.currentTimeMillis();
		logger.info("update All PPMProduct start. startTime=" + startTime);
		try {
			int totalNum = 0;
			int whileFlag = 0;
			int statusNotOne = 0;
			int notExist = 0;
			int alreadyExist = 0;
			int page = 1;
			String lastProductCode = "";
			do {
				param.put("page", page + "");
				List<OfferVo> rs = strategySalesSearchService.salesSearch(param);
				whileFlag = rs.size();
				totalNum += whileFlag;
				for (int i = 0; i < whileFlag; i++) {
					OfferVo tempOfferVo = rs.get(i);
					String code = tempOfferVo.getOfferNbr();
					//重复校验，如果刚刚更新过了这个销售品就直接忽略
					if (code == null || lastProductCode.equals(code)) {
						totalNum--;
						continue;
					}
					lastProductCode = code;
					if (!"1".equals(tempOfferVo.getStatusCd())) {
						statusNotOne++;
						continue;
					}
					List<PpmProduct> isExist = ppmProductMapper.selectPpmProductByCode(code);
					logger.info("updateAllPPMProduct-code=" + code);
					if (isExist == null || isExist.size() == 0) {
						notExist++;
						ppmProductMapper.insertSelective(parseOfferVo(tempOfferVo));
					} else {
						alreadyExist++;
						ppmProductMapper.updateByCodeSelective(parseOfferVo(tempOfferVo));
					}
				}
				page++;
			} while (whileFlag > 0);
			Long endTime = System.currentTimeMillis();
			logger.info("update All PPMProduct. 耗时=" + (endTime - startTime) + "ms, 销售品总量=" + totalNum + ", 状态不是1的="
					+ statusNotOne + ", 更新前库中不存在的销售品数=" + notExist + ", 更新前库中已存在的销售品数=" + alreadyExist);
			result.put("resultCode", CODE_SUCCESS);
			result.put("resultMessage", CODE_SUCCESS);
		} catch (Exception e) {
			logger.info("get PPM product error: ", e);
			result.put("resultCode",CODE_FAIL);
			result.put("resultMessage",CODE_FAIL);
		}
		return result;
	}

	@GetMapping("/updateProductInUse")
	public JSONObject updateProductInUse(@RequestBody JSONObject requestObj, HttpServletRequest request) {
		JSONObject result = new JSONObject();
		String requestIp = request.getRemoteAddr();
		if (checkIpIllegal(requestIp)) {
			return null;
		}
		try {
			List<PpmProduct> productInUse = ppmProductMapper.getAllProductInUse();
			logger.info("productInUse.size = " + productInUse.size());
			List<String> param = new ArrayList<String>();
			for (int i = 0; i < productInUse.size(); i++) {
				param.add(productInUse.get(i).getProductCode());
			}
			List<OfferVo> searchRs = new ArrayList<>();//TODO strategySalesViewService.salesView(param);
			logger.info("salesView result size = " + searchRs.size());
			for (int j = 0; j < searchRs.size(); j++) {
				logger.info("update ppm product. productCode=" + searchRs.get(j).getOfferNbr());
				ppmProductMapper.updateByCodeSelective(parseOfferVo(searchRs.get(j)));
			}
			result.put("resultCode", CODE_SUCCESS);
			result.put("resultMessage", CODE_SUCCESS);
		} catch (Exception e) {
			logger.info("update PPM product error: ", e);
			result.put("resultCode",CODE_FAIL);
			result.put("resultMessage",CODE_FAIL);
		}
		return result;
	}

	/**
	 * 允许进行请求的ip列表
	 */
	private final static String[]	legalIpList	= { "134.96.216.100", "134.96.216.165", "134.96.188.178",
			"134.96.188.179"					};

	/**
	 * 检查请求的ip是否合法。如果在允许请求的ip列表中，则返回false，否则返回true（即true代表不合法，需要拒绝请求）
	 */
	private boolean checkIpIllegal(String ip) {
		if (ip == null || "".equals(ip.trim())) {
			return true;
		} else {
			for (int i = 0; i < legalIpList.length; i++) {
				if (legalIpList[i].equals(ip)) {
					return false;
				}
			}
		}
		return true;
	}

	private PpmProduct parseOfferVo(OfferVo offerVo) {
		if (offerVo == null) {
			return null;
		}
		PpmProduct ppmProduct = new PpmProduct();
		ppmProduct.setProductCode(offerVo.getOfferNbr());
		ppmProduct.setProductName(offerVo.getOfferName());
		ppmProduct.setProductDesc(offerVo.getOfferDesc());
		ppmProduct.setStartTime(offerVo.getEffDate());
		ppmProduct.setEndTime(offerVo.getExpDate());
		ppmProduct.setProductType(offerVo.getOfferType());
		logger.debug("code=" + offerVo.getOfferNbr() + ", offerVo.getOfferId=" + offerVo.getOfferId());
		ppmProduct.setPpmProductId(Integer.parseInt(offerVo.getOfferId()));
		ppmProduct.setStatus(offerVo.getStatusCd());
		return ppmProduct;
	}
}
