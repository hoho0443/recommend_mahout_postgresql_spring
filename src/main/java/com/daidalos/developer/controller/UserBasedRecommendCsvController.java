package com.daidalos.developer.controller;

import java.io.File;
import java.net.URL;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.PostgreSQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
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
public class UserBasedRecommendCsvController {
	private static Logger log = LoggerFactory.getLogger(UserBasedRecommendCsvController.class);

	@ResponseBody
	@RequestMapping(value = {"/user"}, method = RequestMethod.GET)
	public String recommend(HttpServletRequest request) {
		
		try {
			URL dataFilePath = getClass().getResource("/dataset.csv"); // 데이터 파일 경로
			DataModel model = new FileDataModel(new File( dataFilePath.getPath() )); // 데이터 파일 읽기
			
			UserSimilarity similarity = new PearsonCorrelationSimilarity(model); // 읽은 데이터로 유저 유사도 계산
			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model); //유저 유사도 계산된걸 바탕으로 그룹화(?)
			UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
	
			List<RecommendedItem> recommendations = recommender.recommend(1000443348, 10); //추천 조회 user_id, 갯수
			
			for(RecommendedItem recommendation : recommendations) {
				log.debug("############## " + "id: " +  recommendation.getItemID() + "   value: " +  recommendation.getValue()    );
			}
			
			Gson gson = new Gson();
			return gson.toJson(recommendations);
			
		} catch (TasteException e) {
			log.error("##" + e.toString());
		} catch (Exception e){
			log.error("##" + e.toString());
		}
		
		return "";
	}
}