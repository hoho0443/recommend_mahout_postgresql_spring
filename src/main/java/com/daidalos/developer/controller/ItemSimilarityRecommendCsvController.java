package com.daidalos.developer.controller;

import java.io.File;
import java.net.URL;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.PostgreSQLJDBCDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.daidalos.developer.util.Util;
import com.google.gson.Gson;

@Controller
public class ItemSimilarityRecommendCsvController {
	private static Logger log = LoggerFactory.getLogger(ItemSimilarityRecommendCsvController.class);

	@ResponseBody
	@RequestMapping(value = {"/item/nograde"}, method = RequestMethod.GET)
	public String recommend(HttpServletRequest request) {
		
		try {
			URL dataFilePath = getClass().getResource("/dataset_nograde.csv"); // 데이터 파일 경로
			DataModel model = new FileDataModel(new File( dataFilePath.getPath() )); // 데이터 파일 읽기
			
			ItemSimilarity sim = new LogLikelihoodSimilarity(model);
			GenericItemBasedRecommender recommender = new GenericBooleanPrefItemBasedRecommender(model, sim);
			
			long itemId = 100170;
			List<RecommendedItem> recommendations = recommender.mostSimilarItems(itemId, 10);

			for(RecommendedItem recommendation : recommendations) {
				log.debug("############## " + "id: " +  recommendation.getItemID() + "   value: " +  recommendation.getValue()    );
			}
			
			Gson gson = new Gson();
			return gson.toJson(recommendations);
			
			
		} catch (TasteException e) {
			log.error(e.toString());
		} catch (Exception e){
			log.error(e.toString());
		}
		return "";	
	}
}