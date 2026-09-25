package com.generated.ldesportsbar.mapper;

import java.time.LocalDateTime;
import java.util.List;
import com.generated.ldesportsbar.model.Session;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SessionMapper {

  @Select("SELECT * FROM sessions WHERE id = #{id}")
  Session findById(@Param("id") Long id);

  @Select("SELECT * FROM sessions WHERE id = #{id} FOR UPDATE")
  Session lockById(@Param("id") Long id);

  /** 同一开机请求（幂等键）直接返回原单，不重复扣费。 */
  @Select("SELECT * FROM sessions WHERE idempotency_key = #{key}")
  Session findByIdempotencyKey(@Param("key") String key);

  @Select("SELECT COUNT(*) FROM sessions WHERE member_id = #{memberId} AND status = 'ACTIVE'")
  int countActiveByMember(@Param("memberId") Long memberId);

  @Select("SELECT * FROM sessions WHERE member_id = #{memberId} ORDER BY start_time DESC, id DESC")
  List<Session> findByMemberId(@Param("memberId") Long memberId);

  @Select("SELECT * FROM sessions WHERE status = 'ACTIVE' ORDER BY start_time DESC, id DESC")
  List<Session> findActive();

  @Insert("INSERT INTO sessions (member_id, seat_id, idempotency_key, planned_minutes, status) "
    + "VALUES (#{memberId}, #{seatId}, #{idempotencyKey}, #{plannedMinutes}, #{status})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Session session);

  @Update("UPDATE sessions SET status = #{status}, end_time = #{endTime}, actual_minutes = #{actualMinutes} "
    + "WHERE id = #{id}")
  int finish(@Param("id") Long id,
             @Param("status") String status,
             @Param("endTime") LocalDateTime endTime,
             @Param("actualMinutes") Integer actualMinutes);
}
