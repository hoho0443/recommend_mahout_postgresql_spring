package com.daidalos.developer.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.impl.model.jdbc.PostgreSQLJDBCDataModel;
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

@Controller
public class ItemSimilarityRecommendController {
	private static Logger log = LoggerFactory.getLogger(ItemSimilarityRecommendController.class);

	@ResponseBody
	@RequestMapping(value = {"/"}, method = RequestMethod.GET)
	public String recommend(HttpServletRequest request) {
		
		try {
			PGPoolingDataSource dataSource = new PGPoolingDataSource();
			dataSource.setServerName(  Util.prop("db.host") );
			dataSource.setDatabaseName( Util.prop("db.dbname") );
			dataSource.setUser( Util.prop("db.id") );
			dataSource.setPassword( Util.prop("db.pwd") );
			dataSource.setMaxConnections(20);
			
			JDBCDataModel dm = new PostgreSQLJDBCDataModel( dataSource  ,"recommend" , "userId",  "view", "rank", "time");
						
			ItemSimilarity sim = new LogLikelihoodSimilarity(dm);
			GenericItemBasedRecommender recommender = new GenericBooleanPrefItemBasedRecommender(dm, sim);
			
			long itemId = 11;
			List<RecommendedItem> recommendations = recommender.mostSimilarItems(itemId, 5);

			for(RecommendedItem recommendation : recommendations) {
				log.debug("id" + recommendation.getItemID() );
				log.debug("value" + recommendation.getValue() );
			}
		} catch (TasteException e) {
			log.error(e.toString());
		} catch (Exception e){
			log.error(e.toString());
		}
		return "";	
	}
}