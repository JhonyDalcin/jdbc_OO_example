package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import app.entities.Order;
import app.entities.OrderStatus;
import app.entities.Product;
import db.DB;

public class Program {

	public static void main(String[] args) throws SQLException {

		Connection conn = DB.getConnection();

		Statement st = conn.createStatement();

		ResultSet rs = st.executeQuery("SELECT * FROM tb_order "
										+ "INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id "
										+ "INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id");

		Map<Long, Order> mapOrder = new HashMap<>();
		Map<Long, Product> mapProd = new HashMap<>();
		
		while (rs.next()) {			
			Long orderId = rs.getLong("order_id");
			if(mapOrder.get(orderId) == null) {
				Order order = instatiateOrder(rs);
				mapOrder.put(orderId, order);
			}
			Long prodId = rs.getLong("product_id");
			if(mapProd.get(prodId) == null) {
				Product p = instatiateProduct(rs);
				mapProd.put(prodId, p);
			}
			mapOrder.get(orderId).getProducts().add(mapProd.get(prodId));
		}
		
		for(Long orderId : mapOrder.keySet()) {
			System.out.println(mapOrder.get(orderId));
			for(Long prodId : mapProd.keySet()) {
				System.out.println("    " + mapProd.get(prodId));
			}
			System.out.println("---------------------------------------------------------------------------------------------");
		}

	}

	private static Order instatiateOrder(ResultSet rs) throws SQLException {

		Order order = new Order();

		order.setId(rs.getLong("order_id"));
		order.setLatitude(rs.getDouble("latitude"));
		order.setLongitude(rs.getDouble("longitude"));
		order.setMoment(rs.getTimestamp("moment").toInstant());
		order.setStatus(OrderStatus.values()[rs.getInt("status")]);
		return order;
	}

	private static Product instatiateProduct(ResultSet rs) throws SQLException {
		Product p = new Product();
		p.setId(rs.getLong("product_id"));
		p.setName(rs.getString("name"));
		p.setPrice(rs.getDouble("price"));
		p.setDescription(rs.getString("description"));
		p.setImageUri(rs.getString("image_uri"));
		return p;
	}

}
