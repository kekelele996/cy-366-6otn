package com.generated.ldesportsbar.mapper;

import java.util.List;
import com.generated.ldesportsbar.model.Seat;
import com.generated.ldesportsbar.model.SessionBrief;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SeatMapper {

  @Select("SELECT * FROM seats ORDER BY zone, seat_no")
  List<Seat> findAll();

  @Select("SELECT DISTINCT zone FROM seats ORDER BY zone")
  List<String> findZones();

  @Select("SELECT * FROM seats WHERE id = #{id}")
  Seat findById(@Param("id") Long id);

  @Select("SELECT * FROM seats WHERE id = #{id} FOR UPDATE")
  Seat lockById(@Param("id") Long id);

  /** 条件置为使用中：仅空闲机位会成功，防止并发重复占用。 */
  @Update("UPDATE seats SET status = #{toStatus} WHERE id = #{id} AND status = #{expectedStatus}")
  int compareAndSetStatus(@Param("id") Long id,
                          @Param("expectedStatus") String expectedStatus,
                          @Param("toStatus") String toStatus);

  @Update("UPDATE seats SET status = #{status} WHERE id = #{id}")
  int updateStatus(@Param("id") Long id, @Param("status") String status);

  @Select("SELECT s.id AS session_id, m.id AS member_id, m.phone AS phone, m.name AS member_name, "
    + "s.planned_minutes AS planned_minutes, s.start_time AS start_time "
    + "FROM sessions s JOIN members m ON m.id = s.member_id "
    + "WHERE s.seat_id = #{seatId} AND s.status = 'ACTIVE'")
  @Results({
    @Result(column = "session_id", property = "sessionId"),
    @Result(column = "member_id", property = "memberId"),
    @Result(column = "phone", property = "phone"),
    @Result(column = "member_name", property = "memberName"),
    @Result(column = "planned_minutes", property = "plannedMinutes"),
    @Result(column = "start_time", property = "startTime")
  })
  List<SessionBrief> findActiveBriefs(@Param("seatId") Long seatId);
}
