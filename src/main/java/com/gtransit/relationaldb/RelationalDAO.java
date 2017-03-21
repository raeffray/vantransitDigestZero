package com.gtransit.relationaldb;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.gtransit.csv.CSVReader;
import com.gtransit.raw.data.RawData;
import com.gtransit.reflection.ReflectionData;

/**
 * handle the relational DAO operations
 * 
 * @author Renato Barbosa
 * 
 * **/
public class RelationalDAO {

	CSVReader reader = new CSVReader();

	static Logger logger = Logger.getLogger(RelationalDAO.class);

	private static RelationalDAO instance;

	private RelationalDAO() throws Exception {

	}

	public static RelationalDAO getInstance() throws Exception {
		if (instance == null) {
			try {
				instance = new RelationalDAO();
			} catch (SQLException e) {
				logger.error(e);
			}
			return instance;
		}
		return instance;
	}

	public void serverShutdown() {
		HibernateWrapper.shutdown();
	}

	public void insertData(Class<? extends RawData> clazz) throws Exception {
		Session session = HibernateWrapper.getSessionFactory().openSession();
		session.beginTransaction();
		Collection<RawData> list = ReflectionData.getInstance().buildList(clazz,
				reader.readCSVForData(clazz));

		for (RawData rawData : list) {
			session.save(rawData);
		}
		session.getTransaction().commit();
	}

	public List<RawData> findStopTimesByTripId(String tripId) {
		Session session = HibernateWrapper.getSessionFactory().openSession();
		String hql = "FROM StopTimes st where st.trip_id = :tripId";
		Query query = session.createQuery(hql).setParameter("tripId", tripId);
		return query.list();
	}

	public List<RawData> findRoutesByAgencyId(String agencyId) {
		Session session = HibernateWrapper.getSessionFactory().openSession();
		String hql = "FROM Routes r where r.agency_id = :agency_id";
		Query query = session.createQuery(hql).setParameter("agency_id",
				agencyId);
		return query.list();
	}

	public List<RawData> findTripsByRouteId(String routeId) {
		Session session = HibernateWrapper.getSessionFactory().openSession();
		String hql = "FROM Trips t where t.route_id = :routeId";
		Query query = session.createQuery(hql).setParameter("routeId", routeId);
		return query.list();
	}
}
