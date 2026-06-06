package cafe;

import DBConnect.DBconnector1;
import java.sql.*;
import java.util.*;

public class CafeDAO {

    // 전체 카페 조회
    public List<Cafe> findAll() {
        List<Cafe> list = new ArrayList<>();

        String sql = "SELECT c.cafe_id, c.user_id, c.cafe_name, c.region, c.address, "
                   + "GROUP_CONCAT(t.tag_name ORDER BY t.tag_name SEPARATOR ',') AS tags "
                   + "FROM study_cafe c "
                   + "LEFT JOIN cafe_tag ct ON c.cafe_id = ct.cafe_id "
                   + "LEFT JOIN tag t ON ct.tag_id = t.tag_id "
                   + "GROUP BY c.cafe_id, c.user_id, c.cafe_name, c.region, c.address";

        try (Connection conn = DBconnector1.getConnection();
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Cafe(
                    rs.getInt("cafe_id"),
                    rs.getInt("user_id"),
                    rs.getString("cafe_name"),
                    rs.getString("region"),
                    rs.getString("address"),
                    rs.getString("tags")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("전체 카페 조회 실패", e);
        }
        return list;
    }

    // 지역별 카페 조회
    public List<Cafe> findByRegion(String region) {
        List<Cafe> list = new ArrayList<>();

        String sql = "SELECT c.cafe_id, c.user_id, c.cafe_name, c.region, c.address, "
                   + "GROUP_CONCAT(t.tag_name ORDER BY t.tag_name SEPARATOR ',') AS tags "
                   + "FROM study_cafe c "
                   + "LEFT JOIN cafe_tag ct ON c.cafe_id = ct.cafe_id "
                   + "LEFT JOIN tag t ON ct.tag_id = t.tag_id "
                   + "WHERE c.region = ? "
                   + "GROUP BY c.cafe_id, c.user_id, c.cafe_name, c.region, c.address";

        try (Connection conn = DBconnector1.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, region);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Cafe(
                        rs.getInt("cafe_id"),
                        rs.getInt("user_id"),
                        rs.getString("cafe_name"),
                        rs.getString("region"),
                        rs.getString("address"),
                        rs.getString("tags")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("지역별 카페 조회 실패", e);
        }
        return list;
    }

    // 태그별 카페 조회
    public List<Cafe> findByTag(String tagName) {
        List<Cafe> list = new ArrayList<>();

        String sql = "SELECT c.cafe_id, c.user_id, c.cafe_name, c.region, c.address, "
                   + "GROUP_CONCAT(t2.tag_name ORDER BY t2.tag_name SEPARATOR ',') AS tags "
                   + "FROM study_cafe c "
                   + "JOIN cafe_tag ct   ON c.cafe_id  = ct.cafe_id "
                   + "JOIN tag t         ON ct.tag_id  = t.tag_id "
                   + "LEFT JOIN cafe_tag ct2 ON c.cafe_id  = ct2.cafe_id "
                   + "LEFT JOIN tag t2       ON ct2.tag_id = t2.tag_id "
                   + "WHERE t.tag_name = ? "
                   + "GROUP BY c.cafe_id, c.user_id, c.cafe_name, c.region, c.address";

        try (Connection conn = DBconnector1.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tagName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Cafe(
                        rs.getInt("cafe_id"),
                        rs.getInt("user_id"),
                        rs.getString("cafe_name"),
                        rs.getString("region"),
                        rs.getString("address"),
                        rs.getString("tags")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("태그별 카페 조회 실패", e);
        }
        return list;
    }

    // 카페 등록
    public Cafe save(Cafe cafe) {
        String sql = "INSERT INTO study_cafe (user_id, cafe_name, region, address) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBconnector1.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, cafe.getUserId());
            ps.setString(2, cafe.getCafeName());
            ps.setString(3, cafe.getRegion());
            ps.setString(4, cafe.getAddress());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    cafe.setCafeId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("카페 등록 실패", e);
        }
        return cafe;
    }

    // 태그 연결 저장
    public void saveTags(int cafeId, String tags) {
        if (tags == null || tags.trim().isEmpty()) return;

        String findTagSql = "SELECT tag_id FROM tag WHERE tag_name = ?";
        String insertSql  = "INSERT INTO cafe_tag (cafe_id, tag_id) VALUES (?, ?)";

        try (Connection conn = DBconnector1.getConnection()) {
            for (String tagName : tags.split(",")) {
                tagName = tagName.trim();
                if (tagName.isEmpty()) continue;

                try (PreparedStatement ps = conn.prepareStatement(findTagSql)) {
                    ps.setString(1, tagName);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int tagId = rs.getInt("tag_id");
                            try (PreparedStatement ps2 = conn.prepareStatement(insertSql)) {
                                ps2.setInt(1, cafeId);
                                ps2.setInt(2, tagId);
                                ps2.executeUpdate();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("태그 저장 실패", e);
        }
    }
    // 좌석 등록
    // 타입별 개수만 입력하면 좌석은 자동으로 생성됩니당 (A: 일반석, B: 노트북석, C: 회의실)
    public void saveSeats(int cafeId, String seatType, String prefix, int count) {
        if (count <= 0) return;

        String sql = "INSERT INTO seat (cafe_id, seat_name, seat_type, status) " + "VALUES (?, ?, ?, 'AVAILABLE')";

        try (Connection conn = DBconnector1.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
        	
        	
        	
            for (int i = 1; i <= count; i++) {
                String seatName = String.format("%s-%02d"  , prefix, i);
                ps.setInt(1, cafeId);
                
                ps.setString(2, seatName);
                
                ps.setString(3, seatType);
                ps.executeUpdate(); 
            }

        } 
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("좌석 등록 실패", e);
        }
    }
}